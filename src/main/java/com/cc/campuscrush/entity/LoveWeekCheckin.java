package com.cc.campuscrush.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * LoveWeekCheckin实体类
 * &lt;p&gt;核心功能：情侣每周打卡记录，按周维度追踪每日打卡完成情况&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣用户在恋爱空间进行每周打卡互动，统计打卡进度，被LoveWeekCheckinController、LoveWeekCheckinService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class LoveWeekCheckin {
    private Long id;
    private Long coupleId;
    private LocalDate weekStart;
    private Integer dayNum;
    private Boolean checked;
    private Long userId;
    private LocalDate checkinDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
