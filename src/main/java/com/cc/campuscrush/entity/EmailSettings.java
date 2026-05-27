package com.cc.campuscrush.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * EmailSettings实体类
 * &lt;p&gt;核心功能：用户邮件通知偏好配置，包括邮箱地址、订阅类型和开关状态&lt;/p&gt;
 * &lt;p&gt;使用场景：用户在个人设置中配置邮件通知偏好，被EmailSettingsService、ReminderScheduler调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class EmailSettings {
    private Long id;
    private Long userId;
    private String email;
    private String subscription;
    private Boolean enabled;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}