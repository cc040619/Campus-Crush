package com.cc.campuscrush.service;

import java.util.Map;

/**
 * 【LoveWeekCheckinService】服务层接口
 * &lt;p&gt;核心功能：提供情侣周打卡状态的查询和每日打卡更新功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于情侣空间每周打卡互动场景，被LoveWeekCheckinController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface LoveWeekCheckinService {

    /**
     * 获取当前自然周的打卡状态
     * 业务逻辑：查询当前周一到周日的打卡记录 → 汇总每天的打卡状态和打卡人信息 → 返回完整周打卡视图
     * 异常场景：情侣空间不存在时返回空Map；本周无打卡记录时各天状态为未打卡
     *
     * @param coupleId 情侣空间ID（必填）
     * @return Map包含本周每天（1-7）的打卡状态、打卡人等信息，无数据时各天为默认未打卡状态
     */
    Map<String, Object> getCurrentWeek(Long coupleId);

    /**
     * 更新指定某天的打卡状态
     * 业务逻辑：检查当天是否已打卡 → 未打卡则标记为已打卡并记录打卡人 → 已打卡则提示重复
     * 异常场景：情侣空间不存在时操作无效；当天已打卡时静默处理
     *
     * @param coupleId 情侣空间ID（必填）
     * @param userId   打卡用户ID（必填）
     * @param dayNum   星期几（必填，1=周一至7=周日）
     */
    void updateDay(Long coupleId, Long userId, Integer dayNum);
}
