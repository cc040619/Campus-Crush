package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * WeatherController控制器
 * &lt;p&gt;核心功能：根据用户位置获取当日天气信息&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间首页的天气展示模块，根据用户绑定的城市查询实时天气数据，被前端天气卡片组件调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/weather")
@CrossOrigin
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    /**
     * 查询当前用户所在城市今日天气信息
     * 业务逻辑：从请求头获取userId → 委托weatherService根据用户绑定的城市查询今日天气 → 返回天气数据Map
     * 异常场景：userId为null（未登录）时服务层使用默认城市或返回空数据
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为Map，包含天气状况、温度、城市等天气信息字段
     */
    @GetMapping("/today")
    public Result<Map<String, Object>> getToday(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return Result.success(weatherService.getToday(userId));
    }
}
