package com.cc.campuscrush.service;

import com.cc.campuscrush.entity.LoveCheckin;

import java.util.List;
import java.util.Map;

/**
 * 【LoveCheckinService】服务层接口
 * &lt;p&gt;核心功能：提供情侣打卡记录的查询、创建和点赞切换功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于情侣空间每日打卡互动场景，被LoveCheckinController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface LoveCheckinService {

    /**
     * 获取指定情侣空间的所有打卡记录
     * 业务逻辑：查询该coupleId下的所有打卡记录 → 按打卡时间倒序排列
     * 异常场景：无打卡记录时返回空列表
     *
     * @param coupleId 情侣空间ID（必填）
     * @return 打卡记录列表，无数据时返回空列表
     */
    List<LoveCheckin> getList(Long coupleId);

    /**
     * 创建一条新的打卡记录
     * 业务逻辑：构建LoveCheckin实体 → 设置打卡内容、图片和用户信息 → 保存打卡记录
     * 异常场景：情侣空间不存在时创建失败
     *
     * @param coupleId 情侣空间ID（必填）
     * @param userId   打卡用户ID（必填）
     * @param nickname 打卡用户昵称（必填）
     * @param avatar   打卡用户头像URL（可为空）
     * @param content  打卡内容（可为空）
     * @param images   打卡图片URL列表（可为空，多个URL以逗号分隔）
     * @return 创建成功的打卡记录实体
     */
    LoveCheckin create(Long coupleId, Long userId, String nickname, String avatar, String content, String images);

    /**
     * 切换打卡记录的点赞状态（点赞或取消点赞）
     * 业务逻辑：检查当前用户对该打卡记录的点赞状态 → 已点赞则取消，未点赞则添加 → 返回最新点赞状态和数量
     * 异常场景：打卡记录不存在时返回错误信息
     *
     * @param recordId 打卡记录ID（必填）
     * @param userId   操作用户ID（必填）
     * @return Map包含点赞状态（liked）和点赞数量（count），操作失败时返回空Map
     */
    Map<String, Object> toggleLike(Long recordId, Long userId);
}
