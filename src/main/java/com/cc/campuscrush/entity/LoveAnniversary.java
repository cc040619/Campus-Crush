package com.cc.campuscrush.entity;

import lombok.Data;
import java.time.LocalDate;

/**
 * LoveAnniversary实体类
 * &lt;p&gt;核心功能：恋爱纪念日记录，支持多种纪念日类型和提前提醒配置&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣用户录入和管理恋爱纪念日，配合ReminderScheduler实现邮件/站内提醒，被LoveAnniversaryController、LoveAnniversaryService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class LoveAnniversary {
    private Long id;
    private Long userId;
    private String name;
    private String type;
    private LocalDate date;
    private Integer remindDays;
    private Boolean remindEnabled;
}
