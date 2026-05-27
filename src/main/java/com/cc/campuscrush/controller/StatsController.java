package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.entity.LoveCoupleProfile;
import com.cc.campuscrush.mapper.LoveCoupleProfileMapper;
import com.cc.campuscrush.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * StatsController控制器
 * &lt;p&gt;核心功能：情侣关系数据总览和统计图表&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间数据统计模块，查询情侣双方的综合数据概览和图表趋势数据，需绑定情侣关系后使用，被前端情侣统计页面调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/stats")
@CrossOrigin
public class StatsController {

    @Autowired
    private StatsService statsService;

    @Autowired
    private LoveCoupleProfileMapper profileMapper;

    private Long getCoupleId(Long userId) {
        if (userId == null) return null;
        LoveCoupleProfile profile = profileMapper.findByUserId(userId);
        return profile != null ? profile.getCoupleId() : null;
    }

    /**
     * 获取当前用户情侣关系的数据总览（打卡天数、日记数、相册数等综合统计）
     * 业务逻辑：从请求头获取userId → 查询用户绑定的情侣ID → 委托statsService查询该情侣对的综合统计概览 → 返回统计Map
     * 异常场景：未登录返回"未登录"错误；未绑定情侣关系返回"请先在设置中绑定情侣关系"错误
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为Map，包含打卡天数、日记总数、相册总数、在一起天数等情侣统计指标
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) return Result.error("未登录");
        Long coupleId = getCoupleId(userId);
        if (coupleId == null) return Result.error("请先在设置中绑定情侣关系");
        return Result.success(statsService.getOverview(userId, coupleId));
    }

    /**
     * 获取当前用户情侣关系的图表趋势数据（如月度打卡趋势等）
     * 业务逻辑：从请求头获取userId → 查询用户绑定的情侣ID → 委托statsService查询该情侣对的图表数据 → 返回数据列表
     * 异常场景：未登录返回"未登录"错误；未绑定情侣关系返回"请先在设置中绑定情侣关系"错误
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为List，包含图表趋势数据（如每月打卡次数等）
     */
    @GetMapping("/chart")
    public Result<List<Map<String, Object>>> getChart(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) return Result.error("未登录");
        Long coupleId = getCoupleId(userId);
        if (coupleId == null) return Result.error("请先在设置中绑定情侣关系");
        return Result.success(statsService.getChart(coupleId));
    }
}
