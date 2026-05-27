package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.InteractionNotice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * InteractionNoticeMapper数据访问层
 * <p>核心功能：管理互动通知（点赞、评论、收藏），支持按类型过滤通知、未读计数、批量标记已读及清理已读通知</p>
 * <p>使用场景：互动消息通知中心、按类型查看通知、未读红点提示，被InteractionNoticeService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface InteractionNoticeMapper {

    /**
     * 新增一条互动通知
     *
     * @param notice 互动通知实体（必填）
     */
    void insert(InteractionNotice notice);

    /**
     * 查询某用户的所有互动通知
     *
     * @param userId 用户ID（必填）
     * @return 互动通知列表
     */
    List<InteractionNotice> selectByUserId(@Param("userId") Long userId);

    /**
     * 统计某用户的未读互动通知数量
     *
     * @param userId 用户ID（必填）
     * @return 未读通知数量
     */
    int countUnreadByUserId(@Param("userId") Long userId);

    /**
     * 将某用户的所有互动通知标记为已读
     *
     * @param userId 用户ID（必填）
     */
    void updateIsRead(@Param("userId") Long userId);

    /**
     * 将单条互动通知标记为已读
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
     * 查询某用户指定类型的互动通知
     *
     * @param userId 用户ID（必填）
     * @param type   通知类型（必填）
     * @return 指定类型的互动通知列表
     */
    List<InteractionNotice> selectByUserIdAndType(@Param("userId") Long userId, @Param("type") Long type);

    /**
     * 统计某用户指定类型的未读互动通知数量
     *
     * @param userId 用户ID（必填）
     * @param type   通知类型（必填）
     * @return 指定类型的未读通知数量
     */
    int countUnreadByUserIdAndType(@Param("userId") Long userId, @Param("type") Long type);

    /**
     * 将某用户指定类型的所有互动通知标记为已读
     *
     * @param userId 用户ID（必填）
     * @param type   通知类型（必填）
     */
    void updateIsReadByType(@Param("userId") Long userId, @Param("type") Long type);

    /**
     * 删除某用户所有已读的互动通知
     *
     * @param userId 用户ID（必填）
     */
    void deleteAllReadByUserId(@Param("userId") Long userId);

    /**
     * 删除某用户指定类型所有已读的互动通知
     *
     * @param userId 用户ID（必填）
     * @param type   通知类型（必填）
     */
    void deleteAllReadByUserIdAndType(@Param("userId") Long userId, @Param("type") Long type);
}
