package com.cc.campuscrush.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * LoveCoupleRequest实体类
 * &lt;p&gt;核心功能：情侣绑定请求记录，包含发起方、接收方和状态（待处理/已接受/已拒绝）&lt;/p&gt;
 * &lt;p&gt;使用场景：用户发起情侣绑定邀请，对方接受或拒绝后更新状态，被LoveCoupleController、LoveCoupleService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class LoveCoupleRequest {
    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private LocalDate startDate;
    private Integer status; // 0-待处理, 1-已接受, 2-已拒绝
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 前端展示用
    private String fromUserName;
    private String fromUserAvatar;
}
