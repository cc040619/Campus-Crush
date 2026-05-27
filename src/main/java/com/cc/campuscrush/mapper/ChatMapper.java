package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.Chat;
import java.util.List;

/**
 * ChatMapper数据访问层
 * <p>核心功能：管理用户间聊天消息，支持私聊对话、消息已读标记、会话逻辑删除及投诉举报记录</p>
 * <p>使用场景：私聊功能、消息已读通知、会话管理，被ChatService调用</p>
 *
 * @author zcongcong
 * @date  2026-05-27
 */
public interface ChatMapper {

    /**
     * 新增一条聊天消息
     *
     * @param chat 聊天消息实体（必填）
     */
    void insert(Chat chat);

    /**
     * 查询两人之间的全部聊天记录（不含已逻辑删除的消息）
     *
     * @param userId   当前用户ID（必填）
     * @param friendId 好友ID（必填）
     * @return 聊天消息列表
     */
    List<Chat> selectByUserIdAndFriendId(Long userId, Long friendId);

    /**
     * 查询两人之间的全部聊天记录（含已逻辑删除的消息）
     *
     * @param userId   当前用户ID（必填）
     * @param friendId 好友ID（必填）
     * @return 聊天消息列表（含已删除消息）
     */
    List<Chat> selectByUserIdAndFriendIdWithDelete(Long userId, Long friendId);

    /**
     * 查询某个用户参与的所有会话消息
     *
     * @param userId 用户ID（必填）
     * @return 该用户相关的所有聊天消息列表
     */
    List<Chat> selectByUserId(Long userId);

    /**
     * 将来自某用户发给某用户的所有消息标记为已读
     *
     * @param fromId 发送者ID（必填）
     * @param toId   接收者ID（必填）
     * @param isRead 已读状态值（必填）
     */
    void updateIsReadByFromIdAndToId(Long fromId, Long toId, Integer isRead);

    /**
     * 逻辑删除两人之间的全部聊天记录
     *
     * @param userId   当前用户ID（必填）
     * @param friendId 好友ID（必填）
     */
    void deleteByUserIdAndFriendId(Long userId, Long friendId);

    /**
     * 将发送者发送的消息标记为发送方已删除
     *
     * @param fromId 发送者ID（必填）
     * @param toId   接收者ID（必填）
     */
    void updateDeleteByFrom(Long fromId, Long toId);

    /**
     * 将发送者发送的消息标记为接收方已删除
     *
     * @param fromId 发送者ID（必填）
     * @param toId   接收者ID（必填）
     */
    void updateDeleteByTo(Long fromId, Long toId);

    /**
     * 新增一条投诉举报记录
     *
     * @param complaintUserId 投诉人用户ID（必填）
     * @param targetUserId    被投诉用户ID（必填）
     * @param sessionId       会话ID（必填）
     * @param reason          投诉原因（必填）
     */
    void insertComplaint(Long complaintUserId, Long targetUserId, String sessionId, String reason);
}
