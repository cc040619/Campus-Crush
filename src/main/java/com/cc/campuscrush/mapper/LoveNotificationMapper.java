package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.LoveNotification;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * LoveNotificationMapper数据访问层
 * <p>核心功能：管理情侣相关通知，支持通知发送、按用户查询通知列表、未读计数及标记已读</p>
 * <p>使用场景：情侣空间通知推送、情侣互动提醒，被LoveNotificationService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface LoveNotificationMapper {

    /**
     * 新增一条情侣通知
     *
     * @param notification 通知实体（必填）
     * @return 受影响行数
     */
    int insert(LoveNotification notification);

    /**
     * 查询某用户的所有情侣通知
     *
     * @param userId 用户ID（必填）
     * @return 情侣通知列表
     */
    List<LoveNotification> findByUserId(Long userId);

    /**
     * 统计某用户的未读情侣通知数量
     *
     * @param userId 用户ID（必填）
     * @return 未读通知数量
     */
    int countUnread(Long userId);

    /**
     * 将单条情侣通知标记为已读
     *
     * @param id 通知ID（必填）
     * @return 受影响行数
     */
    int markAsRead(Long id);

    /**
     * 将某用户所有情侣通知标记为已读
     *
     * @param userId 用户ID（必填）
     * @return 受影响行数
     */
    int markAllAsRead(Long userId);
}
