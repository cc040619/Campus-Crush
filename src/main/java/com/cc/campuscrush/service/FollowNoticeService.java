package com.cc.campuscrush.service;

import com.cc.campuscrush.entity.FollowNotice;

import java.util.List;

/**
 * 【FollowNoticeService】服务层接口
 * &lt;p&gt;核心功能：提供关注通知的创建、查询、未读统计、已读标记和清理功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于用户关注行为消息通知场景，被FollowNoticeController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface FollowNoticeService {

    /**
     * 创建一条新的关注通知
     * 业务逻辑：构建FollowNotice实体 → 设置通知接收方和发起方信息 → 保存通知记录
     * 异常场景：接收方用户不存在时静默失败
     *
     * @param userId           通知接收方用户ID（必填）
     * @param fromUserId       发起关注的用户ID（必填）
     * @param fromUserNickname 发起方用户昵称（必填）
     * @param fromUserAvatar   发起方用户头像URL（可为空）
     * @param type             通知类型（必填，如1=关注、2=取消关注等）
     */
    void createNotice(Long userId, Long fromUserId, String fromUserNickname, String fromUserAvatar, Integer type);

    /**
     * 获取指定用户的所有关注通知
     * 业务逻辑：查询该用户的所有关注通知 → 按创建时间倒序排列
     * 异常场景：无通知时返回空列表
     *
     * @param userId 用户ID（必填）
     * @return 关注通知列表，无数据时返回空列表
     */
    List<FollowNotice> getNoticesByUserId(Long userId);

    /**
     * 统计指定用户的未读关注通知数量
     * 业务逻辑：查询该用户所有未读状态的关注通知 → 返回计数
     * 异常场景：无未读通知时返回0
     *
     * @param userId 用户ID（必填）
     * @return 未读通知数量，无数据时返回0
     */
    int countUnreadNotices(Long userId);

    /**
     * 将指定用户的所有关注通知标记为已读
     * 业务逻辑：查询该用户所有未读通知 → 批量更新已读状态
     * 异常场景：无未读通知时不执行任何操作
     *
     * @param userId 用户ID（必填）
     */
    void markAllAsRead(Long userId);

    /**
     * 将单条关注通知标记为已读
     * 业务逻辑：查询指定通知 → 校验通知归属 → 更新已读状态
     * 异常场景：通知不存在或不属于该用户时静默处理
     *
     * @param noticeId 通知ID（必填）
     * @param userId   用户ID（必填，用于校验通知归属）
     */
    void markAsRead(Long noticeId, Long userId);

    /**
     * 删除指定用户所有已读的关注通知
     * 业务逻辑：查询该用户所有已读通知 → 批量删除
     * 异常场景：无已读通知时不执行任何操作
     *
     * @param userId 用户ID（必填）
     */
    void deleteAllReadNotices(Long userId);
}
