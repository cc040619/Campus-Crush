package com.cc.campuscrush.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ReminderItem实体类
 * &lt;p&gt;核心功能：邮件提醒数据DTO，封装纪念日名称、剩余天数和日期信息&lt;/p&gt;
 * &lt;p&gt;使用场景：ReminderScheduler构建纪念日提醒邮件内容时使用，作为邮件模板的数据载体&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderItem {
    private String name;
    private Integer daysLeft;
    private String date;
}