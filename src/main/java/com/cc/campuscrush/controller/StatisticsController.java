package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.service.StatisticsService;
import com.cc.campuscrush.vo.StatisticsVO;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * StatisticsController控制器
 * &lt;p&gt;核心功能：用户个人数据统计和图表展示&lt;/p&gt;
 * &lt;p&gt;使用场景：个人数据中心模块，支持综合统计概览、按类型查看基础/社交/内容统计、图表数据查询和每日浏览量趋势，被前端数据统计页面调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/statistics")
@CrossOrigin
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取当前登录用户的综合数据统计概览
     * 业务逻辑：从request属性获取userId → 委托statisticsService查询用户的全维度统计数据 → 返回StatisticsVO
     * 异常场景：未登录（userId为null）返回"未登录"错误
     *
     * @param request HTTP请求对象（用于从request属性获取userId）
     * @return Result.data 为StatisticsVO对象，包含帖子数、关注数、粉丝数、浏览量等综合统计指标
     */
    @GetMapping
    public Result<StatisticsVO> getStatistics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }
        return Result.success(statisticsService.getStatistics(userId));
    }

    /**
     * 按类型获取当前用户的分类统计数据
     * 业务逻辑：从request属性获取userId → 根据type参数路由到不同的统计服务 → basic=基础统计、social=社交统计、product=内容统计 → 返回对应StatisticsVO
     * 异常场景：未登录返回"未登录"错误；无效的type值返回"无效的统计类型"错误
     *
     * @param request HTTP请求对象（用于从request属性获取userId）
     * @param type 统计类型（路径参数，必填，有效值为"basic"/"social"/"product"）
     * @return Result.data 为StatisticsVO对象，包含对应类型的统计指标
     */
    @GetMapping("/{type}")
    public Result<StatisticsVO> getStatisticsByType(
            HttpServletRequest request,
            @PathVariable String type) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        StatisticsVO result;
        switch (type) {
            case "basic":
                result = statisticsService.getBasicStats(userId);
                break;
            case "social":
                result = statisticsService.getSocialStats(userId);
                break;
            case "product":
                result = statisticsService.getProductStats(userId);
                break;
            default:
                return Result.error("无效的统计类型");
        }

        return Result.success(result);
    }

    /**
     * 获取指定类型的图表趋势数据
     * 业务逻辑：从request属性获取userId → 委托statisticsService按chartType查询图表数据 → 校验结果 → 返回ChartData
     * 异常场景：未登录返回"未登录"错误；无效的chartType返回"无效的图表类型"错误
     *
     * @param request HTTP请求对象（用于从request属性获取userId）
     * @param chartType 图表类型（路径参数，必填，如"daily"/"weekly"/"monthly"等）
     * @return Result.data 为ChartData对象，包含图表标签和数据系列
     */
    @GetMapping("/chart/{chartType}")
    public Result<StatisticsVO.ChartData> getChartData(
            HttpServletRequest request,
            @PathVariable String chartType) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        StatisticsVO.ChartData chartData = statisticsService.getChartData(userId, chartType);
        if (chartData == null) {
            return Result.error("无效的图表类型");
        }

        return Result.success(chartData);
    }

    /**
     * 获取当前用户最近N天的每日浏览量趋势数据
     * 业务逻辑：从request属性获取userId → 委托statisticsService查询最近days天的每日浏览量 → 返回趋势数据列表
     * 异常场景：未登录返回"未登录"错误；无浏览记录时返回空列表
     *
     * @param request HTTP请求对象（用于从request属性获取userId）
     * @param days 查询最近天数（可选，默认值为7）
     * @return Result.data 为List，每项是一个Map包含日期和浏览量；无记录时为空数组
     */
    @GetMapping("/daily-browse")
    public Result<List<Map<String, Object>>> getDailyBrowseStats(
            HttpServletRequest request,
            @RequestParam(defaultValue = "7") int days) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        List<Map<String, Object>> dailyStats = statisticsService.getDailyBrowseStats(userId, days);
        return Result.success(dailyStats);
    }
}
