package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.entity.SysUser;
import com.cc.campuscrush.entity.Chat;
import com.cc.campuscrush.service.FriendService;
import com.cc.campuscrush.service.ChatService;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * FriendController控制器
 * &lt;p&gt;核心功能：好友管理、好友申请处理和好友聊天&lt;/p&gt;
 * &lt;p&gt;使用场景：社区社交模块的好友功能，支持添加/删除好友、搜索用户、发送/同意/拒绝好友申请、好友列表查询、好友昵称管理，以及好友聊天消息的发送、历史记录、已读标记、置顶、搜索和投诉举报，被前端好友列表、聊天窗口和好友申请页调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/community/friend")
@RequiredArgsConstructor
public class FriendController {

    @Autowired
    private FriendService friendService;
    @Autowired
    private ChatService chatService;

    /**
     * 添加好友（直接添加，无需对方同意）
     * 业务逻辑：接收当前用户ID和目标用户ID → 委托friendService添加好友关系 → 返回操作结果
     * 异常场景：目标用户不存在或已经是好友时服务层返回false
     *
     * @param friendId 要添加的目标用户ID（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为true表示添加成功，false表示失败
     */
    @PostMapping
    public Result<Boolean> addFriend(
            @RequestParam Long friendId,
            @RequestHeader("X-User-Id") Long userId) {
        var result = friendService.addFriend(userId, friendId);
        return Result.success(result);
    }

    /**
     * 删除好友关系
     * 业务逻辑：接收当前用户ID和目标用户ID → 委托friendService删除好友关系 → 返回操作结果
     * 异常场景：不是好友关系时服务层返回false
     *
     * @param friendId 要删除的好友用户ID（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为true表示删除成功，false表示失败
     */
    @DeleteMapping
    public Result<Boolean> deleteFriend(
            @RequestParam Long friendId,
            @RequestHeader("X-User-Id") Long userId) {
        var result = friendService.deleteFriend(userId, friendId);
        return Result.success(result);
    }

    /**
     * 分页查询指定用户的好友列表
     * 业务逻辑：接收目标用户ID和分页参数 → 委托friendService分页查询好友 → 返回分页结果
     * 异常场景：用户无好友时返回空分页数据
     *
     * @param userId 目标用户ID（路径参数，必填）
     * @param pageNum 页码（可选，默认值为1）
     * @param pageSize 每页条数（可选，默认值为20）
     * @return Result.data 为PageInfo分页对象，包含好友用户列表及分页信息
     */
    @GetMapping("/list/{userId}")
    public Result<PageInfo<?>> getFriendList(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        var friendList = friendService.getFriendList(userId, pageNum, pageSize);
        return Result.success(friendList);
    }

    /**
     * 查询当前用户与指定用户是否为好友
     * 业务逻辑：接收当前用户ID和目标用户ID → 委托friendService查询好友关系 → 返回布尔状态
     * 异常场景：不是好友时返回false
     *
     * @param friendId 目标用户ID（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为true表示是好友，false表示不是
     */
    @GetMapping("/status")
    public Result<Boolean> isFriend(
            @RequestParam Long friendId,
            @RequestHeader("X-User-Id") Long userId) {
        var result = friendService.isFriend(userId, friendId);
        return Result.success(result);
    }

    /**
     * 按关键词搜索用户（返回含好友/申请状态的搜索结果）
     * 业务逻辑：接收关键词和当前用户ID → 委托friendService搜索用户并注入与该用户的好友和申请状态 → 返回用户列表
     * 异常场景：无匹配用户时返回空列表
     *
     * @param keyword 搜索关键词（请求参数，必填，用于模糊匹配用户名/昵称）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为用户列表，每项包含用户信息和好友/申请状态字段
     */
    @GetMapping("/search")
    public Result<List<?>> searchUsers(
            @RequestParam String keyword,
            @RequestHeader("X-User-Id") Long userId) {
        var users = friendService.searchUsersWithStatus(userId, keyword);
        return Result.success(users);
    }

    /**
     * 向指定用户发送好友申请
     * 业务逻辑：接收当前用户ID和目标用户ID → 委托friendService创建好友申请 → 返回操作结果
     * 异常场景：目标用户不存在、已是好友或已有待处理申请时返回false
     *
     * @param friendId 目标用户ID（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为true表示发送成功，false表示失败
     */
    @PostMapping("/apply")
    public Result<Boolean> sendFriendRequest(
            @RequestParam Long friendId,
            @RequestHeader("X-User-Id") Long userId) {
        var result = friendService.sendFriendRequest(userId, friendId);
        return Result.success(result);
    }

    /**
     * 查询当前用户收到的好友申请列表
     * 业务逻辑：从请求头获取userId → 委托friendService查询待处理的好友申请 → 返回申请列表
     * 异常场景：无待处理申请时返回空列表
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为好友申请列表，每项包含申请人信息和申请状态
     */
    @GetMapping("/apply/list")
    public Result<List<?>> getFriendRequests(
            @RequestHeader("X-User-Id") Long userId) {
        var requests = friendService.getFriendRequests(userId);
        return Result.success(requests);
    }

    /**
     * 同意好友申请
     * 业务逻辑：接收当前用户ID和申请人ID → 委托friendService处理同意逻辑 → 建立好友关系 → 返回操作结果
     * 异常场景：申请不存在或已处理时返回false
     *
     * @param friendId 申请人用户ID（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为true表示同意成功，false表示失败
     */
    @PostMapping("/agree")
    public Result<Boolean> agreeFriendRequest(
            @RequestParam Long friendId,
            @RequestHeader("X-User-Id") Long userId) {
        var result = friendService.agreeFriendRequest(userId, friendId);
        return Result.success(result);
    }

    /**
     * 拒绝好友申请
     * 业务逻辑：接收当前用户ID和申请人ID → 委托friendService处理拒绝逻辑 → 返回操作结果
     * 异常场景：申请不存在或已处理时返回false
     *
     * @param friendId 申请人用户ID（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为true表示拒绝成功，false表示失败
     */
    @PostMapping("/refuse")
    public Result<Boolean> refuseFriendRequest(
            @RequestParam Long friendId,
            @RequestHeader("X-User-Id") Long userId) {
        var result = friendService.refuseFriendRequest(userId, friendId);
        return Result.success(result);
    }

    /**
     * 查询当前用户的已通过好友列表（直接返回SysUser列表）
     * 业务逻辑：从请求头获取userId → 委托friendService查询已接受的好友 → 返回好友用户列表
     * 异常场景：无好友时返回空列表
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为SysUser列表，每项包含好友用户的ID、昵称、头像等基本信息
     */
    @GetMapping("/list/accepted")
    public Result<List<SysUser>> getFriends(
            @RequestHeader("X-User-Id") Long userId) {
        var friends = friendService.getFriends(userId);
        return Result.success(friends);
    }

    /**
     * 为好友设置自定义昵称
     * 业务逻辑：接收当前用户ID、好友ID和昵称 → 委托friendService更新好友备注昵称 → 返回操作结果
     * 异常场景：不是好友关系时返回false
     *
     * @param friendId 好友用户ID（请求参数，必填）
     * @param nickname 自定义昵称（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为true表示设置成功，false表示失败
     */
    @PostMapping("/nickname")
    public Result<Boolean> updateFriendNickname(
            @RequestParam Long friendId,
            @RequestParam String nickname,
            @RequestHeader("X-User-Id") Long userId) {
        var result = friendService.updateFriendNickname(userId, friendId, nickname);
        return Result.success(result);
    }

    /**
     * 查询为好友设置的自定义昵称
     * 业务逻辑：接收当前用户ID和好友ID → 委托friendService查询备注昵称 → 返回昵称字符串
     * 异常场景：未设置昵称时可能返回null或空字符串
     *
     * @param friendId 好友用户ID（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为好友的备注昵称字符串
     */
    @GetMapping("/nickname")
    public Result<String> getFriendNickname(
            @RequestParam Long friendId,
            @RequestHeader("X-User-Id") Long userId) {
        var nickname = friendService.getFriendNickname(userId, friendId);
        return Result.success(nickname);
    }

    /**
     * 获取聊天列表中的好友（含最新消息摘要和置顶状态）
     * 业务逻辑：从请求头获取userId → 委托chatService查询聊天好友列表（含最新消息和置顶状态）→ 返回好友用户列表
     * 异常场景：无好友或无聊天记录时返回空列表
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为SysUser列表，包含好友信息和聊天摘要
     */
    @GetMapping("/list/chat")
    public Result<List<SysUser>> getFriendListForChat(
            @RequestHeader("X-User-Id") Long userId) {
        var friends = chatService.getFriendListForChat(userId);
        return Result.success(friends);
    }

    /**
     * 发送一条好友聊天消息
     * 业务逻辑：接收发送者ID、接收者ID和消息内容 → 委托chatService保存并发送消息 → 返回成功
     * 异常场景：不是好友关系时服务层处理
     *
     * @param toId 消息接收者用户ID（请求参数，必填）
     * @param content 消息内容（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填，作为消息发送者）
     * @return Result.data 为true，表示发送成功
     */
    @PostMapping("/chat/send")
    public Result<Boolean> sendMessage(
            @RequestParam Long toId,
            @RequestParam String content,
            @RequestHeader("X-User-Id") Long userId) {
        chatService.sendMessage(userId, toId, content);
        return Result.success(true);
    }

    /**
     * 查询与指定好友的聊天历史记录
     * 业务逻辑：接收当前用户ID和好友ID → 委托chatService查询双方的聊天记录 → 返回消息列表
     * 异常场景：无聊天记录时返回空列表
     *
     * @param toId 好友用户ID（路径参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为Chat消息列表，按时间顺序排列
     */
    @GetMapping("/chat/history/{toId}")
    public Result<List<Chat>> getChatHistory(
            @PathVariable Long toId,
            @RequestHeader("X-User-Id") Long userId) {
        var messages = chatService.getChatHistory(userId, toId);
        return Result.success(messages);
    }

    /**
     * 标记与指定好友的聊天消息为已读
     * 业务逻辑：接收当前用户ID和好友ID → 委托chatService将好友发送给自己的消息标记为已读 → 返回成功
     * 异常场景：无未读消息时操作无效果但不报错
     *
     * @param friendId 好友用户ID（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为true，表示操作成功
     */
    @PostMapping("/chat/mark-read")
    public Result<Boolean> markRead(
            @RequestParam Long friendId,
            @RequestHeader("X-User-Id") Long userId) {
        chatService.markMessagesAsRead(userId, friendId);
        return Result.success(true);
    }

    /**
     * 按关键词搜索与指定好友的聊天历史
     * 业务逻辑：接收当前用户ID、好友ID、关键词和分页参数 → 委托chatService在聊天记录中搜索 → 返回匹配的消息列表
     * 异常场景：无匹配消息时返回空列表
     *
     * @param targetUserId 好友用户ID（请求参数，必填）
     * @param keyword 搜索关键词（请求参数，必填）
     * @param page 页码（可选，默认值为1）
     * @param size 每页条数（可选，默认值为20）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为匹配的Chat消息列表
     */
    @GetMapping("/chat/search")
    public Result<List<Chat>> searchChatHistory(
            @RequestParam Long targetUserId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader("X-User-Id") Long userId) {
        var messages = chatService.searchChatHistory(userId, targetUserId, keyword, page, size);
        return Result.success(messages);
    }

    /**
     * 设置或取消与指定好友的聊天置顶
     * 业务逻辑：接收当前用户ID、好友ID和置顶标识 → 委托chatService更新置顶状态 → 返回成功
     * 异常场景：不是好友关系时服务层处理
     *
     * @param targetUserId 好友用户ID（请求参数，必填）
     * @param isTop 是否置顶（请求参数，必填，true置顶/false取消置顶）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为true，表示操作成功
     */
    @PostMapping("/chat/top")
    public Result<Boolean> setChatTop(
            @RequestParam Long targetUserId,
            @RequestParam boolean isTop,
            @RequestHeader("X-User-Id") Long userId) {
        chatService.setChatTop(userId, targetUserId, isTop);
        return Result.success(true);
    }

    /**
     * 查询与指定好友的聊天是否已置顶
     * 业务逻辑：接收当前用户ID和好友ID → 委托chatService查询置顶状态 → 返回布尔值
     * 异常场景：未置顶时返回false
     *
     * @param targetUserId 好友用户ID（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为true表示已置顶，false表示未置顶
     */
    @GetMapping("/chat/top/status")
    public Result<Boolean> isChatTop(
            @RequestParam Long targetUserId,
            @RequestHeader("X-User-Id") Long userId) {
        var isTop = chatService.isChatTop(userId, targetUserId);
        return Result.success(isTop);
    }

    /**
     * 清空与指定好友的聊天历史记录
     * 业务逻辑：接收当前用户ID和好友ID → 委托chatService清空双方聊天记录 → 返回成功
     * 异常场景：无聊天记录时操作无效果但不报错
     *
     * @param targetUserId 好友用户ID（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为true，表示操作成功
     */
    @DeleteMapping("/chat/clear")
    public Result<Boolean> clearChatHistory(
            @RequestParam Long targetUserId,
            @RequestHeader("X-User-Id") Long userId) {
        chatService.clearChatHistory(userId, targetUserId);
        return Result.success(true);
    }

    /**
     * 提交对好友聊天消息的投诉举报
     * 业务逻辑：接收当前用户ID、被投诉用户ID、会话ID和投诉原因 → 委托chatService记录投诉 → 返回成功
     * 异常场景：不是好友关系时服务层处理
     *
     * @param targetUserId 被投诉用户ID（请求参数，必填）
     * @param sessionId 聊天会话ID（请求参数，可选）
     * @param reason 投诉原因（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填，作为投诉发起者）
     * @return Result.data 为true，表示提交成功
     */
    @PostMapping("/chat/complaint")
    public Result<Boolean> submitComplaint(
            @RequestParam Long targetUserId,
            @RequestParam(required = false) String sessionId,
            @RequestParam String reason,
            @RequestHeader("X-User-Id") Long userId) {
        chatService.submitComplaint(userId, targetUserId, sessionId, reason);
        return Result.success(true);
    }

}
