package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.LoveWishlistItem;
import com.cc.campuscrush.mapper.LoveWishlistItemMapper;
import com.cc.campuscrush.service.LoveWishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 【LoveWishlistServiceImpl】心愿清单服务层实现
 * &lt;p&gt;核心功能：情侣共享心愿清单的管理，包含心愿新增、完成状态切换、删除及完成进度统计&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间中的心愿清单模块，被 LoveWishlistController 调用，通过 action 参数统一路由（add/toggle/delete），自动计算已完成数、总数和完成百分比，支持事务性更新操作&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class LoveWishlistServiceImpl implements LoveWishlistService {

    @Autowired
    private LoveWishlistItemMapper wishlistMapper;

    /**
     * 获取心愿清单信息（含完成进度，被LoveWishlistController调用）
     * 业务逻辑：查coupleId下所有心愿项 → 统计已完成的项数 → 计算完成百分比（四舍五入） → 返回items列表+completed+total+percent
     * 异常场景：无心愿项时total=0, percent=0
     *
     * @param coupleId 情侣关系ID（必填）
     * @return Map含items（心愿列表）、completed（已完成数）、total（总数）、percent（完成百分比）
     */
    @Override
    public Map<String, Object> getInfo(Long coupleId) {
        List<LoveWishlistItem> items = wishlistMapper.findByCoupleId(coupleId);
        long completed = items.stream().filter(i -> i.getCompleted() != null && i.getCompleted()).count();
        int total = items.size();
        int percent = total > 0 ? (int) Math.round((double) completed / total * 100) : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("completed", (int) completed);
        result.put("total", total);
        result.put("percent", percent);
        return result;
    }

    /**
     * 统一处理心愿清单的增删改操作（事务性，被LoveWishlistController调用）
     * 业务逻辑：根据action路由 → "add"：构造心愿项（description默认"期待实现的那一天~"）并插入 → "toggle"：查已有项并翻转completed状态 → "delete"：按wishId删除 → 最后统一调用getInfo返回最新清单数据
     * 异常场景：事务内任何步骤失败均回滚；toggle时existing为null则静默跳过
     *
     * @param coupleId    情侣关系ID（必填）
     * @param userId      操作用户ID（必填）
     * @param action      操作类型：add/toggle/delete（必填）
     * @param wishId      心愿ID（toggle和delete时必填）
     * @param title       心愿标题（add时必填）
     * @param description 心愿描述（add时可选，为null时使用默认值）
     * @return 更新后的心愿清单数据Map
     */
    @Override
    @Transactional
    public Map<String, Object> update(Long coupleId, Long userId, String action, Long wishId,
                                       String title, String description) {
        switch (action) {
            case "add" -> {
                LoveWishlistItem item = new LoveWishlistItem();
                item.setCoupleId(coupleId);
                item.setUserId(userId);
                item.setTitle(title);
                item.setDescription(description != null ? description : "期待实现的那一天~");
                item.setCompleted(false);
                wishlistMapper.insert(item);
            }
            case "toggle" -> {
                LoveWishlistItem existing = wishlistMapper.findById(wishId);
                if (existing != null) {
                    boolean newStatus = existing.getCompleted() == null || !existing.getCompleted();
                    wishlistMapper.updateCompleted(wishId, newStatus);
                }
            }
            case "delete" -> wishlistMapper.deleteById(wishId);
        }
        return getInfo(coupleId);
    }
}
