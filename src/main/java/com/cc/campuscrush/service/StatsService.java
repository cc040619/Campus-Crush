package com.cc.campuscrush.service;

import java.util.List;
import java.util.Map;

/**
 * 【StatsService】服务层接口
 * &lt;p&gt;核心功能：提供情侣空间综合概览数据和图表统计数据&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于情侣空间数据看板展示场景，被StatsController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface StatsService {

    /**
     * 获取情侣空间综合概览数据
     * 业务逻辑：汇总恋爱天数、打卡天数、相册数、纪念日数、日记数、心愿数等概览指标 → 封装为Map返回
     * 异常场景：情侣空间不存在时返回各字段均为0的Map
     *
     * @param userId   当前用户ID（必填，用于权限校验）
     * @param coupleId 情侣空间ID（必填）
     * @return Map包含各项概览指标，无数据时各字段为0
     */
    Map<String, Object> getOverview(Long userId, Long coupleId);

    /**
     * 获取情侣空间的图表统计数据
     * 业务逻辑：查询该情侣空间的各项活动趋势数据（如每周打卡趋势、每月日记发布趋势等） → 按时间维度聚合为图表数据
     * 异常场景：情侣空间不存在或活动记录时返回空列表
     *
     * @param coupleId 情侣空间ID（必填）
     * @return 图表数据列表，每个元素包含日期和对应数值，无数据时返回空列表
     */
    List<Map<String, Object>> getChart(Long coupleId);
}
