package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.FollowNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * FollowNoticeMapper数据访问层
 * <p>核心功能：管理关注通知，支持新增关注通知、按用户查询通知列表、未读计数、标记已读及批量删除已读通知</p>
 * <p>使用场景：关注通知推送、通知列表展示、未读红点提示，被FollowNoticeService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface FollowNoticeMapper {

    /**
     * 新增一条关注通知
     *
     * @param notice 关注通知实体（必填）
     */
    void insert(FollowNotice notice);

    /**
     * 查询某用户的所有关注通知
     *
     * @param userId 用户ID（必填）
     * @return 关注通知列表
     */
    List<FollowNotice> selectByUserId(@Param("userId") Long userId);

    /**
     * 统计某用户的未读关注通知数量
     *
     * @param userId 用户ID（必填）
     * @return 未读通知数量
     */
    int countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 将某用户的所有关注通知标记为已读
     *
     * @param userId 用户ID（必填）
     */
    void updateIsRead(@Param("userId") Long userId);

    /**
     * 将单条关注通知标记为已读
     *
     * @param noticeId 通知ID（必填）
     * @param userId   用户ID（必填）
     */
    void updateNoticeIsRead(@Param("noticeId") Long noticeId, @Param("userId") Long userId);

    /**
     * 更新通知发起者的昵称（用户修改昵称后同步更新通知中的昵称）
     *
     * @param fromUserId  发起者用户ID（必填）
     * @param newNickname 新昵称（必填）
     */
    void updateFromUserNickname(@Param("fromUserId") Long fromUserId, @Param("newNickname") String newNickname);

    /**
     * 删除某用户所有已读的关注通知
     *
     * @param userId 用户ID（必填）
     */
    void deleteAllReadByUserId(@Param("userId") Long userId);
}
