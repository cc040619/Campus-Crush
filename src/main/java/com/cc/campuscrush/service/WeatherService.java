package com.cc.campuscrush.service;

import java.util.Map;

/**
 * 【WeatherService】服务层接口
 * &lt;p&gt;核心功能：提供当日天气信息查询功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于情侣空间首页天气展示场景，被WeatherController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface WeatherService {

    /**
     * 获取指定情侣空间的当日天气信息
     * 业务逻辑：根据情侣档案中的城市信息 → 调用天气API查询当日天气 → 返回温度、天气状况、风向等信息
     * 异常场景：情侣空间不存在或未设置城市时返回默认天气数据；天气API不可用时返回缓存数据或默认数据
     *
     * @param coupleId 情侣空间ID（必填，用于获取关联的城市信息）
     * @return Map包含天气信息（如temperature温度、weather天气状况、wind风向等），无数据时返回默认值
     */
    Map<String, Object> getToday(Long coupleId);
}
