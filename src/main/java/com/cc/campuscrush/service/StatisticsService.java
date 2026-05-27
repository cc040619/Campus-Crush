package com.cc.campuscrush.service;

import com.cc.campuscrush.vo.StatisticsVO;

import java.util.List;
import java.util.Map;

/**
 * 【StatisticsService】服务层接口
 * &lt;p&gt;核心功能：提供用户个人数据统计，包括基础统计、社交统计、作品统计、图表数据和每日浏览趋势&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于用户个人中心数据看板展示场景，被StatisticsController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface StatisticsService {

    /**
     * 获取用户综合统计数据（汇总所有维度的统计信息）
     * 业务逻辑：聚合基础统计、社交统计和作品统计 → 合并为完整的StatisticsVO返回
     * 异常场景：用户不存在时返回各字段均为0或空的StatisticsVO
     *
     * @param userId 用户ID（必填）
     * @return 综合统计VO，无数据时返回空对象
     */
    StatisticsVO getStatistics(Long userId);

    /**
     * 获取用户基础统计信息
     * 业务逻辑：统计用户注册天数、活跃天数、最后登录时间等基础指标 → 封装为StatisticsVO
     * 异常场景：用户不存在时返回各字段均为0的StatisticsVO
     *
     * @param userId 用户ID（必填）
     * @return 基础统计VO，无数据时返回空对象
     */
    StatisticsVO getBasicStats(Long userId);

    /**
     * 获取用户社交统计信息
     * 业务逻辑：统计粉丝数、关注数、好友数、获赞数、被收藏数等社交指标 → 封装为StatisticsVO
     * 异常场景：用户不存在时返回各字段均为0的StatisticsVO
     *
     * @param userId 用户ID（必填）
     * @return 社交统计VO，无数据时返回空对象
     */
    StatisticsVO getSocialStats(Long userId);

    /**
     * 获取用户作品统计信息
     * 业务逻辑：统计帖子数、评论数、相册数、日记数等作品相关指标 → 封装为StatisticsVO
     * 异常场景：用户不存在时返回各字段均为0的StatisticsVO
     *
     * @param userId 用户ID（必填）
     * @return 作品统计VO，无数据时返回空对象
     */
    StatisticsVO getProductStats(Long userId);

    /**
     * 获取指定类型的图表数据
     * 业务逻辑：根据chartType参数 → 查询对应的统计维度数据（如每周发帖趋势、每月互动趋势） → 返回图表数据
     * 异常场景：用户不存在或图表类型无效时返回空ChartData
     *
     * @param userId    用户ID（必填）
     * @param chartType 图表类型（必填，如"weekly_posts"周帖子趋势、"monthly_likes"月点赞趋势等）
     * @return 图表数据对象，包含标签和数据系列，无数据时返回空ChartData
     */
    StatisticsVO.ChartData getChartData(Long userId, String chartType);

    /**
     * 获取指定天数内的每日浏览趋势
     * 业务逻辑：统计最近N天每天的帖子浏览量和访客数 → 按日期排列返回每天的统计数据
     * 异常场景：天数无效时使用默认值；无浏览数据时返回各天数据均为0的列表
     *
     * @param userId 用户ID（必填）
     * @param days   统计天数（必填，如7表示近7天）
     * @return 每日浏览统计数据列表，无数据时返回空列表
     */
    List<Map<String, Object>> getDailyBrowseStats(Long userId, int days);
}
