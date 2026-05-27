package com.cc.campuscrush.service;

import com.cc.campuscrush.entity.Chat;
import com.cc.campuscrush.entity.SysUser;
import java.util.List;

/**
 * 【ChatService】服务层接口
 * &lt;p&gt;核心功能：提供用户间聊天消息的发送、历史查询、已读标记、聊天置顶、记录清理及投诉功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于校园社交应用中的即时通讯场景，被ChatController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface ChatService {

    /**
     * 发送一条聊天消息
     * 业务逻辑：创建Chat实体 → 设置发送方和接收方 → 持久化消息记录
     * 异常场景：发送方或接收方不存在时静默失败
     *
     * @param fromId  发送方用户ID（必填）
     * @param toId    接收方用户ID（必填）
     * @param content 消息内容（必填，可为空字符串）
     */
    void sendMessage(Long fromId, Long toId, String content);

    /**
     * 获取与指定好友的聊天历史记录
     * 业务逻辑：查询双方互为收发方的所有消息 → 按时间升序排列
     * 异常场景：无聊天记录时返回空列表
     *
     * @param userId   当前用户ID（必填）
     * @param friendId 好友用户ID（必填）
     * @return 聊天消息列表，无数据时返回空列表
     */
    List<Chat> getChatHistory(Long userId, Long friendId);

    /**
     * 获取当前用户的聊天好友列表
     * 业务逻辑：查找所有与该用户有过聊天记录的好友 → 返回去重后的用户列表
     * 异常场景：无聊天好友时返回空列表
     *
     * @param userId 当前用户ID（必填）
     * @return 聊过天的好友用户列表，无数据时返回空列表
     */
    List<SysUser> getFriendListForChat(Long userId);

    /**
     * 标记与指定好友的所有未读消息为已读
     * 业务逻辑：查找双方之间所有未读消息 → 批量更新已读状态
     * 异常场景：无未读消息时不执行任何操作
     *
     * @param userId   当前用户ID（必填，作为接收方）
     * @param friendId 好友用户ID（必填，作为发送方）
     */
    void markMessagesAsRead(Long userId, Long friendId);

    /**
     * 按关键词搜索聊天历史记录（分页）
     * 业务逻辑：在双方聊天记录中模糊匹配关键词 → PageHelper分页返回结果
     * 异常场景：无匹配结果时返回空列表
     *
     * @param currentUserId 当前用户ID（必填）
     * @param targetUserId  对方用户ID（必填）
     * @param keyword       搜索关键词（必填）
     * @param page          页码（必填，从1开始）
     * @param size          每页条数（必填）
     * @return 匹配的聊天记录分页列表，无数据时返回空列表
     */
    List<Chat> searchChatHistory(Long currentUserId, Long targetUserId, String keyword, int page, int size);

    /**
     * 设置或取消与指定好友的聊天置顶
     * 业务逻辑：查找置顶记录 → 存在则更新置顶状态，不存在则新增置顶记录
     * 异常场景：好友关系不存在时操作无效
     *
     * @param currentUserId 当前用户ID（必填）
     * @param targetUserId  目标好友ID（必填）
     * @param isTop         true表示置顶，false表示取消置顶
     */
    void setChatTop(Long currentUserId, Long targetUserId, boolean isTop);

    /**
     * 判断与指定好友的聊天是否已置顶
     * 业务逻辑：查询置顶记录表 → 返回是否存在有效的置顶标记
     * 异常场景：无置顶记录时返回false
     *
     * @param currentUserId 当前用户ID（必填）
     * @param targetUserId  目标好友ID（必填）
     * @return true表示已置顶，false表示未置顶
     */
    boolean isChatTop(Long currentUserId, Long targetUserId);

    /**
     * 清空与指定好友的所有聊天记录
     * 业务逻辑：删除双方互为收发方的所有消息记录
     * 异常场景：无聊天记录时不执行任何操作
     *
     * @param currentUserId 当前用户ID（必填）
     * @param targetUserId  目标好友ID（必填）
     */
    void clearChatHistory(Long currentUserId, Long targetUserId);

    /**
     * 提交对某用户的投诉举报
     * 业务逻辑：创建投诉记录 → 关联会话ID和被投诉用户 → 保存投诉原因
     * 异常场景：投诉用户或被投诉用户不存在时静默失败
     *
     * @param complaintUserId 投诉人用户ID（必填）
     * @param targetUserId    被投诉用户ID（必填）
     * @param sessionId       会话ID（必填，用于定位具体的聊天上下文）
     * @param reason          投诉原因（必填）
     */
    void submitComplaint(Long complaintUserId, Long targetUserId, String sessionId, String reason);
}
