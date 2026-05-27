package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.Chat;
import com.cc.campuscrush.entity.SysUser;
import com.cc.campuscrush.entity.Friend;
import com.cc.campuscrush.mapper.ChatMapper;
import com.cc.campuscrush.mapper.SysUserMapper;
import com.cc.campuscrush.mapper.FriendMapper;
import com.cc.campuscrush.service.ChatService;
import com.cc.campuscrush.service.ImageCacheService;
import com.cc.campuscrush.utils.RedisContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 【ChatServiceImpl】聊天服务层实现
 * &lt;p&gt;核心功能：实时聊天消息的发送、接收、缓存管理及会话状态维护&lt;/p&gt;
 * &lt;p&gt;使用场景：好友间私聊功能，被 ChatController 调用，集成 Redis 缓存读写加速消息检索，支持异步消息处理、未读计数、置顶会话、聊天记录关键词搜索和投诉举报&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    private ChatMapper chatMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private FriendMapper friendMapper;

    @Resource
    private RedisContext redisContext;
    
    @Resource
    private ImageCacheService imageCacheService;

    private static final int MAX_CHAT_CACHE_SIZE = 50;
    private static final String CHAT_KEY_PREFIX = "Campus-Crush:chat:";
    private static final String CHAT_UNREAD_PREFIX = "Campus-Crush:chat:unread:";
    private static final String CHAT_TOP_KEY_PREFIX = "Campus-Crush:chat:top:";
    
    // ObjectMapper用于兼容旧的JSON字符串格式
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * 发送聊天消息
     * 业务逻辑：构造消息对象（文本消息、未读）并设置创建时间 → 插入MySQL持久化 → 异步处理Redis缓存（写入共享缓存List、控制长度50、接收方未读+1）
     * 异常场景：Redis操作失败仅打印异常，不影响主流程
     *
     * @param fromId  发送者用户ID（必填）
     * @param toId    接收者用户ID（必填）
     * @param content 消息文本内容（必填）
     */
    @Override
    public void sendMessage(Long fromId, Long toId, String content) {
        // 创建消息对象
        Chat chat = new Chat();
        chat.setFromId(fromId);
        chat.setToId(toId);
        chat.setContent(content);
        chat.setIsRead(0); // 未读
        chat.setMsgType(1); // 文本消息
        chat.setCreateTime(LocalDateTime.now()); // 设置创建时间
        
        // 保存到MySQL
        chatMapper.insert(chat);
        
        // 异步处理Redis操作
        asyncHandleRedis(chat);
    }

    /**
     * 异步处理聊天消息的Redis缓存写入（被sendMessage调用）
     * 业务逻辑：生成共享缓存key（小ID:大ID保证一致性）→ 消息写入Redis List左侧 → 裁剪List长度为50 → 接收方未读计数+1
     * 异常场景：任何步骤失败仅打印堆栈，不影响消息已落库的持久化结果
     *
     * @param chat 已持久化的聊天消息对象（必填）
     */
    @Async
    public void asyncHandleRedis(Chat chat) {
        try {
            // 生成聊天缓存key（共享缓存）
            String chatKey = generateChatKey(chat.getFromId(), chat.getToId());
            
            // 写入Redis List（左插）- RedisTemplate会自动序列化
            redisContext.leftPush(chatKey, chat);
            
            // 控制长度为50
            redisContext.trim(chatKey, 0, MAX_CHAT_CACHE_SIZE - 1);
            
            // 接收方未读计数 +1
            String unreadKey = CHAT_UNREAD_PREFIX + chat.getToId();
            redisContext.incrementHash(unreadKey, chat.getFromId().toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取两个用户之间的聊天历史记录（被ChatController调用）
     * 业务逻辑：生成共享缓存key → 查Redis List缓存 → 缓存命中则反序列化并按删除标记过滤可见消息 → 存在无效数据则回退查库 → 缓存未命中则查MySQL并回写Redis → 按时间升序返回
     * 异常场景：Redis异常时回退到数据库查询；删除标记（deleted_by_from/deleted_by_to）为1的消息对当前用户不可见
     *
     * @param userId   当前用户ID（必填）
     * @param friendId 好友用户ID（必填）
     * @return 聊天消息列表，无消息时返回空列表
     */
    @Override
    public List<Chat> getChatHistory(Long userId, Long friendId) {
        try {
            // 生成聊天缓存key（共享缓存）
            String chatKey = generateChatKey(userId, friendId);
            
            // 尝试从Redis获取缓存
            List<Object> cachedChats = redisContext.range(chatKey, 0, -1);
            if (cachedChats != null && !cachedChats.isEmpty()) {
                List<Chat> chats = new ArrayList<>();
                boolean hasInvalidData = false;
                
                for (Object obj : cachedChats) {
                    try {
                        Chat chat = null;
                        if (obj instanceof Chat) {
                            chat = (Chat) obj;
                        } else if (obj instanceof String) {
                            try {
                                chat = objectMapper.readValue((String) obj, Chat.class);
                            } catch (Exception e) {
                                hasInvalidData = true;
                                continue;
                            }
                        } else {
                            hasInvalidData = true;
                            continue;
                        }
                        
                        // 根据用户级删除标记过滤消息
                        if (isMessageVisible(userId, chat)) {
                            chats.add(chat);
                        }
                    } catch (Exception e) {
                        hasInvalidData = true;
                    }
                }
                
                if (hasInvalidData) {
                    return loadFromDatabaseAndCache(userId, friendId);
                }
                
                chats.sort(Comparator.comparing(Chat::getCreateTime, Comparator.nullsFirst(Comparator.naturalOrder())));
                return chats;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 缓存未命中，从数据库查询
        return loadFromDatabaseAndCache(userId, friendId);
    }
    
    /**
     * 判断消息对当前用户是否可见
     * @param userId 当前用户ID
     * @param chat 消息对象
     * @return true-可见，false-已被当前用户删除
     */
    private boolean isMessageVisible(Long userId, Chat chat) {
        if (chat == null) {
            return false;
        }
        
        // 当前用户是发送者（消息是我发出去的）
        if (userId.equals(chat.getFromId())) {
            // 检查接收者是否删除了这条消息（对发送者来说，接收者删除不影响发送者查看）
            // 发送者自己删除是通过deleted_by_from
            Integer deletedByFrom = chat.getDeletedByFrom();
            return deletedByFrom == null || deletedByFrom == 0;
        }
        
        // 当前用户是接收者（消息是我收到的）
        if (userId.equals(chat.getToId())) {
            // 检查发送者是否删除了这条消息（对接收者来说，发送者删除不影响接收者查看）
            // 接收者自己删除是通过deleted_by_to
            Integer deletedByTo = chat.getDeletedByTo();
            return deletedByTo == null || deletedByTo == 0;
        }
        
        return true;
    }
    
    /**
     * 从数据库加载聊天记录并更新缓存
     */
    private List<Chat> loadFromDatabaseAndCache(Long userId, Long friendId) {
        // 查询时不应用删除标记过滤，保留完整数据用于缓存
        List<Chat> chats = chatMapper.selectByUserIdAndFriendId(userId, friendId);
        
        // 将数据库查询结果写入Redis缓存（保留完整数据，不过滤删除标记）
        try {
            String chatKey = generateChatKey(userId, friendId);
            // 保留原有缓存，只在缓存为空时写入（避免覆盖其他用户的数据）
            List<Object> existing = redisContext.range(chatKey, 0, -1);
            if (existing == null || existing.isEmpty()) {
                for (Chat chat : chats) {
                    redisContext.leftPush(chatKey, chat);
                }
                redisContext.trim(chatKey, 0, MAX_CHAT_CACHE_SIZE - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 返回时根据当前用户过滤删除标记
        return chats.stream()
                .filter(chat -> isMessageVisible(userId, chat))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 获取当前用户的聊天好友列表（被ChatController调用）
     * 业务逻辑：双向查询好友关系（userId和friendId两个方向）→ 合并去重排除自己 → 查用户表获取好友信息 → 加载每个好友的备注、最新可见消息、Redis未读数和置顶状态 → 按置顶优先+最新消息时间倒序排序
     * 异常场景：Redis读取未读数或置顶状态失败时使用默认值（0/未置顶）
     *
     * @param userId 当前用户ID（必填）
     * @return 好友用户列表（含最新消息摘要、未读数、置顶状态），无好友时返回空列表
     */
    @Override
    public List<SysUser> getFriendListForChat(Long userId) {
        // 获取所有好友（status=1）
        List<Friend> friends1 = friendMapper.selectByUserIdAndStatus(userId, 1);
        List<Friend> friends2 = friendMapper.selectByFriendIdAndStatus(userId, 1);

        // 合并去重，获取好友ID
        Set<Long> friendIds = new HashSet<>();
        for (Friend friend : friends1) {
            friendIds.add(friend.getFriendId());
        }
        for (Friend friend : friends2) {
            friendIds.add(friend.getUserId());
        }

        // 移除自己
        friendIds.remove(userId);

        // 获取好友信息
        List<SysUser> friends = new ArrayList<>();
        for (Long friendId : friendIds) {
            SysUser user = sysUserMapper.selectById(friendId);
            if (user != null) {
                friends.add(user);
            }
        }

        // 获取每个好友的最新消息、未读数量、置顶状态和备注
        String topKey = CHAT_TOP_KEY_PREFIX + userId;
        for (SysUser friend : friends) {
            // 获取好友备注（双向查找）
            String nickname = friendMapper.selectFriendNickname(userId, friend.getId());
            if (nickname == null || nickname.isEmpty()) {
                nickname = friendMapper.selectFriendNickname(friend.getId(), userId);
            }
            if (nickname != null && !nickname.isEmpty()) {
                friend.setNickname(nickname);
            }
            
            List<Chat> chats = chatMapper.selectByUserIdAndFriendId(userId, friend.getId());
            if (!chats.isEmpty()) {
                // 找到对当前用户可见的最新消息（考虑删除标记）
                Chat lastVisibleChat = null;
                for (int i = chats.size() - 1; i >= 0; i--) {
                    Chat chat = chats.get(i);
                    if (isMessageVisible(userId, chat)) {
                        lastVisibleChat = chat;
                        break;
                    }
                }
                if (lastVisibleChat != null) {
                    friend.setLastMessage(lastVisibleChat.getContent());
                    friend.setLastMessageTime(lastVisibleChat.getCreateTime());
                }
            }
            
            // 获取未读数量
            try {
                String unreadKey = CHAT_UNREAD_PREFIX + userId;
                Object unreadCount = redisContext.getHash(unreadKey, friend.getId().toString());
                if (unreadCount != null) {
                    friend.setUnreadCount(Integer.parseInt(unreadCount.toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // 获取置顶状态
            try {
                boolean isTop = redisContext.isMemberOfSet(topKey, friend.getId().toString());
                friend.setTop(isTop);
            } catch (Exception e) {
                e.printStackTrace();
                friend.setTop(false);
            }
        }

        // 按置顶状态和最新消息时间排序（置顶优先，同状态按时间倒序）
        friends.sort((a, b) -> {
            // 置顶状态比较：置顶的排在前面
            if (Boolean.TRUE.equals(a.getTop()) && Boolean.FALSE.equals(b.getTop())) {
                return -1;
            }
            if (Boolean.FALSE.equals(a.getTop()) && Boolean.TRUE.equals(b.getTop())) {
                return 1;
            }
            
            // 同状态按最新消息时间倒序排序
            if (a.getLastMessageTime() == null && b.getLastMessageTime() == null) {
                return 0;
            }
            if (a.getLastMessageTime() == null) {
                return 1;
            }
            if (b.getLastMessageTime() == null) {
                return -1;
            }
            return b.getLastMessageTime().compareTo(a.getLastMessageTime());
        });

        return friends;
    }

    /**
     * 将好友发来的消息标记为已读（被ChatController调用）
     * 业务逻辑：更新MySQL中friendId发来的所有消息isRead=1 → 清除Redis中该好友的未读计数Hash字段
     * 异常场景：Redis操作失败仅打印堆栈，不影响MySQL已读状态更新
     *
     * @param userId   当前用户ID（必填，作为消息接收者）
     * @param friendId 好友用户ID（必填，作为消息发送者）
     */
    @Override
    public void markMessagesAsRead(Long userId, Long friendId) {
        // 更新数据库中的消息状态为已读
        chatMapper.updateIsReadByFromIdAndToId(friendId, userId, 1);
        // 清除Redis中的未读计数
        try {
            String unreadKey = CHAT_UNREAD_PREFIX + userId;
            redisContext.deleteHash(unreadKey, friendId.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 生成聊天缓存key，确保小ID在前，大ID在后，保证一致性（共享缓存）
    private String generateChatKey(Long userId1, Long userId2) {
        if (userId1 < userId2) {
            return CHAT_KEY_PREFIX + userId1 + ":" + userId2;
        } else {
            return CHAT_KEY_PREFIX + userId2 + ":" + userId1;
        }
    }

    /**
     * 按关键词搜索聊天记录（被ChatController调用）
     * 业务逻辑：获取全量聊天历史 → 按关键词内容过滤 → 为每条消息加载发送者头像（优先Redis缓存） → 手动分页返回
     * 异常场景：page超出范围时返回空列表
     *
     * @param currentUserId 当前用户ID（必填）
     * @param targetUserId  目标好友ID（必填）
     * @param keyword       搜索关键词（必填，区分大小写）
     * @param page          页码（必填，从1开始）
     * @param size          每页条数（必填）
     * @return 匹配的聊天消息列表，无匹配时返回空列表
     */
    @Override
    public List<Chat> searchChatHistory(Long currentUserId, Long targetUserId, String keyword, int page, int size) {
        List<Chat> allChats = getChatHistory(currentUserId, targetUserId);
        
        List<Chat> filteredChats = allChats.stream()
                .filter(chat -> chat.getContent() != null && chat.getContent().contains(keyword))
                .collect(java.util.stream.Collectors.toList());
        
        // 为每条消息设置发送者头像（优先从Redis缓存获取）
        for (Chat chat : filteredChats) {
            Long senderId = chat.getFromId();
            String avatarUrl = imageCacheService.getAvatar(senderId);
            chat.setAvatar(avatarUrl);
        }
        
        int total = filteredChats.size();
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        
        if (fromIndex >= total) {
            return new java.util.ArrayList<>();
        }
        
        return filteredChats.subList(fromIndex, toIndex);
    }

    /**
     * 设置或取消聊天置顶（被ChatController调用）
     * 业务逻辑：构造当前用户的置顶Redis Set key → isTop为true则添加好友ID到Set → isTop为false则从Set移除
     * 异常场景：Redis操作失败仅打印堆栈
     *
     * @param currentUserId 当前用户ID（必填）
     * @param targetUserId  目标好友ID（必填）
     * @param isTop         true-置顶，false-取消置顶
     */
    @Override
    public void setChatTop(Long currentUserId, Long targetUserId, boolean isTop) {
        String topKey = CHAT_TOP_KEY_PREFIX + currentUserId;
        String targetIdStr = String.valueOf(targetUserId);
        
        try {
            if (isTop) {
                redisContext.addSet(topKey, targetIdStr);
            } else {
                redisContext.removeSet(topKey, targetIdStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断某个聊天是否已被当前用户置顶（被ChatController调用）
     * 业务逻辑：查询Redis Set中是否存在目标好友ID
     * 异常场景：Redis异常时返回false（默认未置顶）
     *
     * @param currentUserId 当前用户ID（必填）
     * @param targetUserId  目标好友ID（必填）
     * @return true-已置顶，false-未置顶
     */
    @Override
    public boolean isChatTop(Long currentUserId, Long targetUserId) {
        String topKey = CHAT_TOP_KEY_PREFIX + currentUserId;
        String targetIdStr = String.valueOf(targetUserId);
        
        try {
            return redisContext.isMemberOfSet(topKey, targetIdStr);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 清除当前用户与目标好友的聊天记录（用户级软删除，被ChatController调用）
     * 业务逻辑：设置当前用户发送的消息deleted_by_from=1 → 设置当前用户接收的消息deleted_by_to=1 → 清空Redis中该好友的未读计数 → 保留Redis缓存自然过期
     * 异常场景：SQL执行失败仅打印堆栈；不影响对方查看消息
     *
     * @param currentUserId 当前用户ID（必填）
     * @param targetUserId  目标好友ID（必填）
     */
    @Override
    public void clearChatHistory(Long currentUserId, Long targetUserId) {
        try {
            // 用户级软删除：只标记当前用户删除，不影响对方
            // 更新发送给对方的消息（当前用户作为发送者）
            // from_id = currentUserId, to_id = targetUserId → 设置 deleted_by_from = 1
            chatMapper.updateDeleteByFrom(currentUserId, targetUserId);
            // 更新从对方收到的消息（当前用户作为接收者）
            // from_id = targetUserId, to_id = currentUserId → 设置 deleted_by_to = 1
            chatMapper.updateDeleteByTo(targetUserId, currentUserId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 不删除Redis缓存，让缓存自然过期或后续更新覆盖
        
        // 清空未读计数
        try {
            String unreadKey = CHAT_UNREAD_PREFIX + currentUserId;
            redisContext.deleteHash(unreadKey, String.valueOf(targetUserId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交聊天投诉举报（被ChatController调用）
     * 业务逻辑：将投诉信息（投诉人、被投诉人、会话ID、投诉原因）插入数据库complaint表
     * 异常场景：插入失败时抛出RuntimeException("投诉提交失败")
     *
     * @param complaintUserId 投诉人用户ID（必填）
     * @param targetUserId    被投诉人用户ID（必填）
     * @param sessionId       会话标识（必填，对应聊天缓存key）
     * @param reason          投诉原因（必填）
     * @throws RuntimeException 数据库插入失败时抛出
     */
    @Override
    public void submitComplaint(Long complaintUserId, Long targetUserId, String sessionId, String reason) {
        try {
            chatMapper.insertComplaint(complaintUserId, targetUserId, sessionId, reason);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("投诉提交失败");
        }
    }
}
