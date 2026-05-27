package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.LoveWhisper;
import com.cc.campuscrush.mapper.LoveWhisperMapper;
import com.cc.campuscrush.service.LoveWhisperService;
import com.cc.campuscrush.utils.RedisContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 【LoveWhisperServiceImpl】情侣私语服务层实现
 * &lt;p&gt;核心功能：情侣间私密消息的发送、历史查询和已读状态管理，基于 Redis 缓存加速消息读写&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间中的私语聊天功能，被 LoveWhisperController 调用，采用双向共享缓存键（小ID在前）保证一致性，支持 Redis List 缓存（最多100条）与 MySQL 持久化双写，未读消息通过 Hash 计数&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class LoveWhisperServiceImpl implements LoveWhisperService {

    private static final String WHISPER_KEY_PREFIX = "Campus-Crush:whisper:";
    private static final String WHISPER_UNREAD_PREFIX = "Campus-Crush:whisper:unread:";
    private static final int MAX_CACHE_SIZE = 100;

    @Resource
    private LoveWhisperMapper whisperMapper;

    @Resource
    private RedisContext redisContext;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private String buildKey(Long userId1, Long userId2) {
        return WHISPER_KEY_PREFIX + Math.min(userId1, userId2) + ":" + Math.max(userId1, userId2);
    }

    /**
     * 发送情侣私语消息（被LoveWhisperController调用）
     * 业务逻辑：构造LoveWhisper对象（文本消息、未读）并设置创建时间 → 插入MySQL持久化 → 写入Redis共享缓存List左侧并裁剪为100条 → 接收方未读计数+1
     * 异常场景：Redis操作异常不影响MySQL已持久化的消息
     *
     * @param fromUserId 发送者用户ID（必填）
     * @param toUserId   接收者用户ID（必填）
     * @param content    消息文本内容（必填）
     */
    @Override
    public void sendMessage(Long fromUserId, Long toUserId, String content) {
        LoveWhisper whisper = new LoveWhisper();
        whisper.setFromUserId(fromUserId);
        whisper.setToUserId(toUserId);
        whisper.setContent(content);
        whisper.setMsgType(1);
        whisper.setIsRead(0);
        whisper.setCreateTime(LocalDateTime.now());

        whisperMapper.insert(whisper);

        String key = buildKey(fromUserId, toUserId);
        redisContext.leftPush(key, whisper);
        redisContext.trim(key, 0, MAX_CACHE_SIZE - 1);

        redisContext.incrementHash(WHISPER_UNREAD_PREFIX + toUserId, fromUserId.toString(), 1);
    }

    /**
     * 获取情侣私语聊天历史（被LoveWhisperController调用）
     * 业务逻辑：构建共享缓存key（小ID:大ID） → 查Redis List缓存 → 命中则反序列化（兼容LoveWhisper/Map/String三种类型） → 无无效数据时按时间升序返回 → 缓存未命中或有无效数据则查MySQL → 清除旧缓存并回填 → 返回数据库结果
     * 异常场景：缓存类型无法识别时回退查库
     *
     * @param userId    当前用户ID（必填）
     * @param partnerId 伴侣用户ID（必填）
     * @return 私语消息列表，无消息时返回空列表
     */
    @Override
    public List<LoveWhisper> getChatHistory(Long userId, Long partnerId) {
        String key = buildKey(userId, partnerId);
        List<Object> cached = redisContext.range(key, 0, -1);

        if (cached != null && !cached.isEmpty()) {
            List<LoveWhisper> result = new ArrayList<>();
            boolean hasInvalid = false;
            for (Object obj : cached) {
                try {
                    LoveWhisper w = null;
                    if (obj instanceof LoveWhisper) {
                        w = (LoveWhisper) obj;
                    } else if (obj instanceof Map) {
                        // Jackson2JsonRedisSerializer 反序列化为 LinkedHashMap
                        w = objectMapper.convertValue(obj, LoveWhisper.class);
                    } else if (obj instanceof String) {
                        w = objectMapper.readValue((String) obj, LoveWhisper.class);
                    } else {
                        hasInvalid = true;
                        continue;
                    }
                    if (w != null) result.add(w);
                } catch (Exception e) {
                    hasInvalid = true;
                }
            }
            if (!hasInvalid && !result.isEmpty()) {
                result.sort(Comparator.comparing(LoveWhisper::getCreateTime, Comparator.nullsFirst(Comparator.naturalOrder())));
                return result;
            }
        }

        // 从数据库加载并回填缓存
        List<LoveWhisper> fromDb = whisperMapper.selectByUserIdAndPartnerId(userId, partnerId);
        redisContext.delete(key); // 清除旧缓存
        for (LoveWhisper w : fromDb) {
            redisContext.leftPush(key, w);
        }
        redisContext.trim(key, 0, MAX_CACHE_SIZE - 1);
        return fromDb;
    }

    /**
     * 将伴侣发来的私语消息标记为已读（被LoveWhisperController调用）
     * 业务逻辑：更新MySQL中partnerId发来的所有消息isRead=1 → 清除Redis中伴侣的未读计数Hash字段
     * 异常场景：Redis操作失败不影响MySQL已读状态更新
     *
     * @param userId    当前用户ID（必填，作为消息接收者）
     * @param partnerId 伴侣用户ID（必填，作为消息发送者）
     */
    @Override
    public void markAsRead(Long userId, Long partnerId) {
        whisperMapper.updateIsRead(partnerId, userId);
        redisContext.deleteHash(WHISPER_UNREAD_PREFIX + userId, partnerId.toString());
    }
}
