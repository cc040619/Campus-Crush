package com.cc.campuscrush.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * LoveNotification实体类
 * &lt;p&gt;核心功能：情侣模块通知消息存储，涵盖绑定请求、接受和拒绝等事件类型及已读状态&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣绑定过程中的各类通知在消息中心展示，被LoveNotificationService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class LoveNotification {
    private Long id;
    private Long userId;
    private String type; // couple_request, couple_accept, couple_reject
    private Long fromUserId;
    private String fromUserName;
    private String fromUserAvatar;
    private String content;
    private Long relatedId;
    private Integer isRead; // 0-未读, 1-已读
    private LocalDateTime createTime;
}
