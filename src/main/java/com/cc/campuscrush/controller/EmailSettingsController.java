package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.entity.EmailSettings;
import com.cc.campuscrush.entity.LoveAnniversary;
import com.cc.campuscrush.service.EmailSettingsService;
import com.cc.campuscrush.service.LoveAnniversaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * EmailSettingsController控制器
 * &lt;p&gt;核心功能：邮件通知订阅设置和纪念日提醒查询&lt;/p&gt;
 * &lt;p&gt;使用场景：用户个人设置模块，配置邮箱地址和通知订阅偏好，查询即将到来的纪念日提醒列表，被前端邮箱设置页面调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/email")
@CrossOrigin
public class EmailSettingsController {

    @Autowired
    private EmailSettingsService emailSettingsService;

    @Autowired
    private LoveAnniversaryService anniversaryService;

    private Long getCurrentUserId(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new RuntimeException("未登录");
        }
        return userId;
    }

    /**
     * 查询当前用户的邮件通知设置
     * 业务逻辑：从请求头获取userId → 委托emailSettingsService查询用户设置 → 未找到时返回默认设置（空邮箱、订阅类型为"all"、未启用）
     * 异常场景：未登录时getCurrentUserId抛出RuntimeException
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为EmailSettings对象；首次查询且无设置记录时返回默认空设置，不为null
     */
    @GetMapping("/settings")
    public Result<EmailSettings> getSettings(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        EmailSettings settings = emailSettingsService.findByUserId(currentUserId);
        if (settings == null) {
            settings = new EmailSettings();
            settings.setEmail("");
            settings.setSubscription("all");
            settings.setEnabled(false);
        }
        return Result.success(settings);
    }

    /**
     * 保存或更新当前用户的邮件通知设置
     * 业务逻辑：从请求头获取userId并注入settings对象 → 委托emailSettingsService保存设置 → 返回成功提示
     * 异常场景：未登录时getCurrentUserId抛出RuntimeException
     *
     * @param settings 邮件设置请求体，包含email（邮箱地址，必填）、subscription（订阅类型，如"all"/"important"，可选）、enabled（是否启用，可选）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为字符串"保存成功"
     */
    @PostMapping("/settings")
    public Result<String> saveSettings(@RequestBody EmailSettings settings, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        settings.setUserId(currentUserId);
        emailSettingsService.save(settings);
        return Result.success("保存成功");
    }

    /**
     * 查询用户即将到来的纪念日提醒列表（按提醒天数阈值过滤并排序）
     * 业务逻辑：从请求头获取userId → 查询用户全部纪念日 → 过滤已开启提醒的 → 计算下一个纪念日距今剩余天数 → 筛选在天数阈值内的 → 按剩余天数升序排列 → 返回提醒列表
     * 异常场景：未登录时getCurrentUserId抛出RuntimeException；无纪念日或无即将到来的提醒时返回空列表，hasReminders为false
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 包含reminders（提醒列表，每项含name/daysLeft/date）和hasReminders（布尔值）
     */
    @GetMapping("/reminders")
    public Result<Map<String, Object>> getReminders(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        List<LoveAnniversary> anniversaries = anniversaryService.findAllByUserId(currentUserId);
        
        List<Map<String, Object>> reminderList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM月dd日");
        
        for (LoveAnniversary item : anniversaries) {
            if (item.getDate() == null || !Boolean.TRUE.equals(item.getRemindEnabled())) {
                continue;
            }
            
            LocalDate targetDate = LocalDate.of(today.getYear(), item.getDate().getMonth(), item.getDate().getDayOfMonth());
            if (targetDate.isBefore(today)) {
                targetDate = targetDate.plusYears(1);
            }
            
            long daysLeft = ChronoUnit.DAYS.between(today, targetDate);
            
            Integer remindDays = item.getRemindDays();
            if (remindDays == null || remindDays <= 0) {
                remindDays = 7;
            }
            
            if (daysLeft <= remindDays) {
                Map<String, Object> reminder = new java.util.HashMap<>();
                reminder.put("name", item.getName());
                reminder.put("daysLeft", (int) daysLeft);
                reminder.put("date", item.getDate().format(formatter));
                reminderList.add(reminder);
            }
        }
        
        reminderList.sort((a, b) -> {
            Integer daysA = (Integer) a.get("daysLeft");
            Integer daysB = (Integer) b.get("daysLeft");
            return daysA.compareTo(daysB);
        });
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("reminders", reminderList);
        result.put("hasReminders", !reminderList.isEmpty());
        
        return Result.success(result);
    }
}