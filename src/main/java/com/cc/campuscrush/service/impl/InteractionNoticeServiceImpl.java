package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.InteractionNotice;
import com.cc.campuscrush.mapper.InteractionNoticeMapper;
import com.cc.campuscrush.service.InteractionNoticeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 【InteractionNoticeServiceImpl】互动通知服务层实现
 * &lt;p&gt;核心功能：帖子被点赞/收藏/评论时的互动消息通知管理&lt;/p&gt;
 * &lt;p&gt;使用场景：用户帖子收到互动时产生通知，被 PostServiceImpl 和 CommentServiceImpl 调用，支持按通知类型（点赞/收藏/评论）分类查询、分类已读标记和分类批量删除，通知内容包含发起人头像和帖子标题&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class InteractionNoticeServiceImpl implements InteractionNoticeService {

    @Resource
    private InteractionNoticeMapper interactionNoticeMapper;

    /**
     * 创建互动通知（帖子被点赞/收藏/评论时，被PostServiceImpl和CommentServiceImpl调用）
     * 业务逻辑：构造InteractionNotice对象（含接收者、发起者信息、头像默认占位图、帖子标题、类型、创建时间、未读标记） → 插入MySQL
     * 异常场景：fromUserAvatar为空时使用默认头像URL
     *
     * @param userId           通知接收者用户ID（必填）
     * @param fromUserId       互动发起者用户ID（必填）
     * @param fromUserNickname 发起者昵称（必填）
     * @param fromUserAvatar   发起者头像URL（必填，为空时使用默认头像）
     * @param postId           相关帖子ID（必填）
     * @param postTitle        帖子标题（必填）
     * @param type             通知类型：1-点赞，2-收藏，3-评论（必填）
     */
    @Override
    public void createNotice(Long userId, Long fromUserId, String fromUserNickname, String fromUserAvatar,
                             Long postId, String postTitle, Integer type) {
        InteractionNotice notice = new InteractionNotice();
        notice.setUserId(userId);
        notice.setFromUserId(fromUserId);
        notice.setFromUserNickname(fromUserNickname);
        notice.setFromUserAvatar(fromUserAvatar != null && !fromUserAvatar.isEmpty()
                ? fromUserAvatar : "https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg");
        notice.setPostId(postId);
        notice.setPostTitle(postTitle);
        notice.setType(type);
        notice.setCreateTime(LocalDateTime.now());
        notice.setIsRead(0);
        interactionNoticeMapper.insert(notice);
    }

    /**
     * 获取用户所有互动通知（被InteractionNoticeController调用）
     * 业务逻辑：按userId查询所有通知记录（不分类别）
     * 异常场景：无通知时返回空列表
     *
     * @param userId 用户ID（必填）
     * @return 互动通知列表
     */
    @Override
    public List<InteractionNotice> getNoticesByUserId(Long userId) {
        return interactionNoticeMapper.selectByUserId(userId);
    }

    /**
     * 统计用户未读互动通知总数（被InteractionNoticeController调用）
     * 业务逻辑：按userId和isRead=0计数（不分类别）
     * 异常场景：无未读通知时返回0
     *
     * @param userId 用户ID（必填）
     * @return 未读通知数量
     */
    @Override
    public int countUnreadNotices(Long userId) {
        return interactionNoticeMapper.countUnreadByUserId(userId);
    }

    /**
     * 标记所有互动通知为已读（全类别，被InteractionNoticeController调用）
     * 业务逻辑：分别调用updateIsReadByType标记type=1（点赞）和type=2（收藏）为已读
     * 异常场景：无通知时update影响0行
     *
     * @param userId 用户ID（必填）
     */
    @Override
    public void markAllAsRead(Long userId) {
        interactionNoticeMapper.updateIsReadByType(userId, 1L);
        interactionNoticeMapper.updateIsReadByType(userId, 2L);
    }

    /**
     * 标记单条互动通知为已读（被InteractionNoticeController调用）
     * 业务逻辑：按noticeId和userId精确更新单条通知isRead=1
     * 异常场景：无匹配记录时更新0行
     *
     * @param noticeId 通知ID（必填）
     * @param userId   用户ID（必填，用于权限校验）
     */
    @Override
    public void markAsRead(Long noticeId, Long userId) {
        interactionNoticeMapper.updateNoticeIsRead(noticeId, userId);
    }

    /**
     * 按类型获取用户互动通知（被InteractionNoticeController调用）
     * 业务逻辑：按userId和type查通知列表
     * 异常场景：无通知时返回空列表
     *
     * @param userId 用户ID（必填）
     * @param type   通知类型：1-点赞，2-收藏，3-评论（必填）
     * @return 指定类型的互动通知列表
     */
    @Override
    public List<InteractionNotice> getNoticesByUserIdAndType(Long userId, Long type) {
        return interactionNoticeMapper.selectByUserIdAndType(userId, type);
    }

    /**
     * 按类型统计未读互动通知数（被InteractionNoticeController调用）
     * 业务逻辑：按userId、type和isRead=0计数
     * 异常场景：无未读通知时返回0
     *
     * @param userId 用户ID（必填）
     * @param type   通知类型：1-点赞，2-收藏，3-评论（必填）
     * @return 指定类型未读通知数量
     */
    @Override
    public int countUnreadNoticesByType(Long userId, Long type) {
        return interactionNoticeMapper.countUnreadByUserIdAndType(userId, type);
    }

    /**
     * 按类型标记所有通知为已读（被InteractionNoticeController调用）
     * 业务逻辑：按userId和type更新所有匹配通知isRead=1
     * 异常场景：无匹配通知时更新0行
     *
     * @param userId 用户ID（必填）
     * @param type   通知类型（必填）
     */
    @Override
    public void markAllAsReadByType(Long userId, Long type) {
        interactionNoticeMapper.updateIsReadByType(userId, type);
    }

    /**
     * 删除用户所有已读通知（全类别，被InteractionNoticeController调用）
     * 业务逻辑：按userId删除所有isRead=1的通知
     * 异常场景：无已读通知时删除0行
     *
     * @param userId 用户ID（必填）
     */
    @Override
    public void deleteAllReadNotices(Long userId) {
        interactionNoticeMapper.deleteAllReadByUserId(userId);
    }

    /**
     * 按类型删除用户已读通知（被InteractionNoticeController调用）
     * 业务逻辑：按userId和type删除isRead=1的通知
     * 异常场景：无匹配通知时删除0行
     *
     * @param userId 用户ID（必填）
     * @param type   通知类型（必填）
     */
    @Override
    public void deleteAllReadNoticesByType(Long userId, Long type) {
        interactionNoticeMapper.deleteAllReadByUserIdAndType(userId, type);
    }
 }