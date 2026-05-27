package com.cc.campuscrush.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * FollowNotice实体类
 * &lt;p&gt;核心功能：关注通知消息存储，记录谁关注了你及已读状态&lt;/p&gt;
 * &lt;p&gt;使用场景：当用户被他人关注时生成通知，在消息中心展示，被FollowNoticeService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class FollowNotice {

    private Long id;

    private Long userId;

    private Long fromUserId;

    private String fromUserNickname;

    private String fromUserAvatar;

    private Integer type;

    private LocalDateTime createTime;

    private Integer isRead;
}