package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.FollowNotice;
import com.cc.campuscrush.mapper.FollowNoticeMapper;
import com.cc.campuscrush.service.FollowNoticeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 【FollowNoticeServiceImpl】关注通知服务层实现
 * &lt;p&gt;核心功能：关注/取关事件的通知创建、查询、已读标记和批量删除管理&lt;/p&gt;
 * &lt;p&gt;使用场景：用户关注或被关注时产生通知记录，被 FollowController 调用，支持单条已读标记、全部已读、按用户批量删除已读通知等操作&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class FollowNoticeServiceImpl implements FollowNoticeService {

    @Autowired
    private FollowNoticeMapper followNoticeMapper;

    /**
     * 创建关注/取关通知（被FollowServiceImpl调用）
     * 业务逻辑：构造FollowNotice对象（含通知接收者、发起者信息、类型、创建时间、未读标记） → 插入MySQL
     * 异常场景：数据库插入失败由调用方处理
     *
     * @param userId           通知接收者用户ID（必填）
     * @param fromUserId       发起关注/取关的用户ID（必填）
     * @param fromUserNickname 发起者昵称（必填）
     * @param fromUserAvatar   发起者头像URL（必填）
     * @param type             通知类型：1-关注，2-取关（必填）
     */
    @Override
    public void createNotice(Long userId, Long fromUserId, String fromUserNickname, String fromUserAvatar, Integer type) {
        FollowNotice notice = new FollowNotice();
        notice.setUserId(userId);
        notice.setFromUserId(fromUserId);
        notice.setFromUserNickname(fromUserNickname);
        notice.setFromUserAvatar(fromUserAvatar);
        notice.setType(type);
        notice.setCreateTime(LocalDateTime.now());
        notice.setIsRead(0);
        followNoticeMapper.insert(notice);
    }

    /**
     * 获取用户的所有关注通知（被FollowController调用）
     * 业务逻辑：按userId查询所有通知记录
     * 异常场景：无通知时返回空列表
     *
     * @param userId 用户ID（必填）
     * @return 关注通知列表
     */
    @Override
    public List<FollowNotice> getNoticesByUserId(Long userId) {
        return followNoticeMapper.selectByUserId(userId);
    }

    /**
     * 统计用户未读关注通知数量（被FollowController调用）
     * 业务逻辑：按userId和isRead=0计数
     * 异常场景：无未读通知时返回0
     *
     * @param userId 用户ID（必填）
     * @return 未读通知数量
     */
    @Override
    public int countUnreadNotices(Long userId) {
        return followNoticeMapper.countUnreadByUserId(userId);
    }

    /**
     * 标记所有关注通知为已读（被FollowController调用）
     * 业务逻辑：按userId批量更新所有通知isRead=1
     * 异常场景：无通知时update影响0行
     *
     * @param userId 用户ID（必填）
     */
    @Override
    public void markAllAsRead(Long userId) {
        followNoticeMapper.updateIsRead(userId);
    }

    /**
     * 标记单条关注通知为已读（被FollowController调用）
     * 业务逻辑：按noticeId和userId精确更新单条通知isRead=1
     * 异常场景：无匹配记录时更新0行
     *
     * @param noticeId 通知ID（必填）
     * @param userId   用户ID（必填，用于权限校验）
     */
    @Override
    public void markAsRead(Long noticeId, Long userId) {
        followNoticeMapper.updateNoticeIsRead(noticeId, userId);
    }

    /**
     * 删除用户所有已读通知（被FollowController调用）
     * 业务逻辑：按userId批量删除isRead=1的通知
     * 异常场景：无已读通知时删除0行
     *
     * @param userId 用户ID（必填）
     */
    @Override
    public void deleteAllReadNotices(Long userId) {
        followNoticeMapper.deleteAllReadByUserId(userId);
    }
}