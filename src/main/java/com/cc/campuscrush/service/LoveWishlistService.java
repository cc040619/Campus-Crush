package com.cc.campuscrush.service;

import com.cc.campuscrush.entity.LoveWishlistItem;

import java.util.Map;

/**
 * 【LoveWishlistService】服务层接口
 * &lt;p&gt;核心功能：提供情侣心愿清单的查询和更新（添加、完成、删除）功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于情侣空间心愿管理场景，被LoveWishlistController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface LoveWishlistService {

    /**
     * 获取情侣空间的心愿清单信息
     * 业务逻辑：查询该情侣空间下的所有心愿 → 分类展示（待完成和已完成） → 返回心愿列表和统计信息
     * 异常场景：情侣空间不存在时返回空Map；无心愿时待完成和已完成列表均为空
     *
     * @param coupleId 情侣空间ID（必填）
     * @return Map包含心愿列表（pending/completed）和统计数据，无数据时各列表为空
     */
    Map<String, Object> getInfo(Long coupleId);

    /**
     * 更新心愿清单（添加、完成或删除心愿）
     * 业务逻辑：根据action参数执行对应操作 → add添加新心愿 / complete标记完成 / delete删除心愿 → 返回更新后的心愿信息
     * 异常场景：情侣空间不存在时返回错误信息；心愿ID无效时操作失败
     *
     * @param coupleId    情侣空间ID（必填）
     * @param userId      操作用户ID（必填）
     * @param action      操作类型（必填，如"add"添加、"complete"完成、"delete"删除）
     * @param wishId      心愿ID（complete和delete操作时必填，add操作时可为null）
     * @param title       心愿标题（add操作时必填）
     * @param description 心愿描述（add操作时可选）
     * @return Map包含操作结果和更新后的心愿数据，操作失败时返回错误信息
     */
    Map<String, Object> update(Long coupleId, Long userId, String action, Long wishId,
                                String title, String description);
}
