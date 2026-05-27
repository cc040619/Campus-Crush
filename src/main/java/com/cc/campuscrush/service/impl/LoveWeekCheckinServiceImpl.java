package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.LoveWeekCheckin;
import com.cc.campuscrush.mapper.LoveWeekCheckinMapper;
import com.cc.campuscrush.service.LoveWeekCheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * 【LoveWeekCheckinServiceImpl】周打卡服务层实现
 * &lt;p&gt;核心功能：情侣每周打卡日历的展示与逐日打卡状态更新&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间中的周打卡日历面板，被 LoveWeekCheckinController 调用，自动计算当前自然周起止日期和每天日期标签，返回七天打卡状态（是否已打卡、是否为今天），支持按天 upsert 打卡记录&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class LoveWeekCheckinServiceImpl implements LoveWeekCheckinService {

    private static final String[] DAY_LABELS = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    @Autowired
    private LoveWeekCheckinMapper weekCheckinMapper;

    /**
     * 获取当前自然周的打卡日历数据（被LoveWeekCheckinController调用）
     * 业务逻辑：计算本周一日期 → 查数据库本周打卡记录 → 构建7天数组（每天含label/num/checked/isToday） → 返回{days: [...]}
     * 异常场景：本周无打卡记录时所有天checked=false；isToday同时校验星期序号和日期相等
     *
     * @param coupleId 情侣关系ID（必填）
     * @return Map含days数组，每项含label（周一~周日）、num（几号）、checked、isToday
     */
    @Override
    public Map<String, Object> getCurrentWeek(Long coupleId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(WeekFields.ISO.getFirstDayOfWeek());
        int todayDayOfWeek = today.getDayOfWeek().getValue();

        List<LoveWeekCheckin> weekRecords = weekCheckinMapper.findByCoupleIdAndWeekStart(coupleId, weekStart);
        Map<Integer, Boolean> checkedMap = new HashMap<>();
        for (LoveWeekCheckin wc : weekRecords) {
            checkedMap.put(wc.getDayNum(), wc.getChecked());
        }

        List<Map<String, Object>> days = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            Map<String, Object> day = new HashMap<>();
            day.put("label", DAY_LABELS[i - 1]);
            LocalDate date = weekStart.plusDays(i - 1);
            day.put("num", String.valueOf(date.getDayOfMonth()));
            day.put("checked", checkedMap.getOrDefault(i, false));
            day.put("isToday", i == todayDayOfWeek && date.equals(today));
            days.add(day);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("days", days);
        return result;
    }

    /**
     * 更新本周某一天的打卡状态为已打卡（事务性，被LoveWeekCheckinController调用）
     * 业务逻辑：计算本周一起始日期 → upsert本周打卡表（coupleId+weekStart+dayNum为唯一键，checked=true，记录userId和日期）
     * 异常场景：事务内任何步骤失败均回滚
     *
     * @param coupleId 情侣关系ID（必填）
     * @param userId   操作者用户ID（必填）
     * @param dayNum   星期几（必填，1=周一~7=周日）
     */
    @Override
    @Transactional
    public void updateDay(Long coupleId, Long userId, Integer dayNum) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(WeekFields.ISO.getFirstDayOfWeek());

        weekCheckinMapper.upsert(coupleId, weekStart, dayNum, true, userId, today);
    }
}
