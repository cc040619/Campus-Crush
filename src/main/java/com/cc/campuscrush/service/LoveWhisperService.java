package com.cc.campuscrush.service;

import com.cc.campuscrush.entity.LoveWhisper;
import java.util.List;

/**
 * 【LoveWhisperService】服务层接口
 * &lt;p&gt;核心功能：提供情侣间私密消息的发送、历史记录查询和已读标记功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于情侣空间私密聊天场景，被LoveWhisperController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface LoveWhisperService {

    /**
     * 发送一条情侣私密消息
     * 业务逻辑：创建LoveWhisper实体 → 设置发送方和接收方 → 持久化消息记录
     * 异常场景：发送方或接收方不存在时静默失败
     *
     * @param fromUserId 发送方用户ID（必填）
     * @param toUserId   接收方用户ID（必填）
     * @param content    消息内容（必填，可为空字符串）
     */
    void sendMessage(Long fromUserId, Long toUserId, String content);

    /**
     * 获取与伴侣的私密聊天历史记录
     * 业务逻辑：查询双方互为收发方的所有私密消息 → 按时间升序排列
     * 异常场景：无聊天记录时返回空列表
     *
     * @param userId    当前用户ID（必填）
     * @param partnerId 伴侣用户ID（必填）
     * @return 私密消息列表，无数据时返回空列表
     */
    List<LoveWhisper> getChatHistory(Long userId, Long partnerId);

    /**
     * 标记伴侣发送的所有未读消息为已读
     * 业务逻辑：查找伴侣发送给当前用户的所有未读消息 → 批量更新已读状态
     * 异常场景：无未读消息时不执行任何操作
     *
     * @param userId    当前用户ID（必填，作为接收方）
     * @param partnerId 伴侣用户ID（必填，作为发送方）
     */
    void markAsRead(Long userId, Long partnerId);
}
