package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.LoveCoupleProfile;
import com.cc.campuscrush.mapper.*;
import com.cc.campuscrush.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 【StatsServiceImpl】情侣统计服务层实现
 * &lt;p&gt;核心功能：情侣空间概览数据（相恋天数、打卡次数、连续打卡天数、愿望完成率）及近7天打卡趋势图&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间首页数据概览面板，被 StatsController 调用，连续打卡天数通过向前逐日回溯计算得出，趋势图按日统计打卡数量，愿望完成率基于已完成项占比计算&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private LoveCoupleProfileMapper coupleProfileMapper;

    @Autowired
    private LoveCheckinMapper checkinMapper;

    @Autowired
    private LoveWishlistItemMapper wishlistMapper;

    /**
     * 获取情侣空间概览数据（被StatsController调用）
     * 业务逻辑：按userId查couple_profile计算相恋天数 → 按coupleId统计累计打卡次数 → 向前逐日回溯计算连续打卡天数(calcStreak) → 统计愿望完成率（已完成/总数*100四舍五入） → 返回{daysTogether, totalCheckins, streak, wishRate}
     * 异常场景：profile不存在或startDate为null时daysTogether=0；无心愿时wishRate=0
     *
     * @param userId   用户ID（必填，用于查个人profile）
     * @param coupleId 情侣关系ID（必填，用于统计打卡和愿望）
     * @return 概览Map含相恋天数、累计打卡、连续打卡、愿望完成率
     */
    @Override
    public Map<String, Object> getOverview(Long userId, Long coupleId) {
        Map<String, Object> result = new HashMap<>();

        // 相恋天数（用user_id查profile，避免共享couple_id时selectOne返回多条）
        LoveCoupleProfile profile = coupleProfileMapper.findByUserId(userId);
        long daysTogether = 0;
        if (profile != null && profile.getStartDate() != null) {
            daysTogether = ChronoUnit.DAYS.between(profile.getStartDate(), LocalDate.now());
        }
        result.put("daysTogether", (int) daysTogether);

        // 累计打卡次数
        int totalCheckins = checkinMapper.countByCoupleId(coupleId);
        result.put("totalCheckins", totalCheckins);

        // 连续打卡天数
        int streak = calcStreak(coupleId);
        result.put("streak", streak);

        // 愿望完成率
        List<com.cc.campuscrush.entity.LoveWishlistItem> items = wishlistMapper.findByCoupleId(coupleId);
        long completed = items.stream().filter(i -> i.getCompleted() != null && i.getCompleted()).count();
        int wishRate = items.isEmpty() ? 0 : (int) Math.round((double) completed / items.size() * 100);
        result.put("wishRate", wishRate);

        return result;
    }

    /**
     * 获取近7天打卡趋势图表数据（被StatsController调用）
     * 业务逻辑：从今天往前推6天共7天 → 每天按coupleId和日期统计打卡数 → 返回{day: "MM/dd", count}列表
     * 异常场景：某天无打卡时count=0
     *
     * @param coupleId 情侣关系ID（必填）
     * @return 7天趋势数据列表
     */
    @Override
    public List<Map<String, Object>> getChart(Long coupleId) {
        List<Map<String, Object>> chart = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd");

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            int count = checkinMapper.countByCoupleIdAndDate(coupleId, date);
            Map<String, Object> bar = new HashMap<>();
            bar.put("day", date.format(fmt));
            bar.put("count", count);
            chart.add(bar);
        }
        return chart;
    }

    private int calcStreak(Long coupleId) {
        LocalDate today = LocalDate.now();
        int streak = 0;
        while (true) {
            int count = checkinMapper.countByCoupleIdAndDate(coupleId, today);
            if (count > 0) {
                streak++;
                today = today.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }
}
