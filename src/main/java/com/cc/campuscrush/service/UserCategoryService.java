package com.cc.campuscrush.service;

import java.util.List;

/**
 * 【UserCategoryService】服务层接口
 * &lt;p&gt;核心功能：提供用户兴趣分类的查询、添加、删除和批量更新功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于用户个性化标签管理场景，被UserCategoryController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface UserCategoryService {

    /**
     * 获取指定用户的所有兴趣分类标签
     * 业务逻辑：查询用户分类关联表 → 返回该用户已选择的所有分类名称
     * 异常场景：用户无任何分类时返回空列表
     *
     * @param userId 用户ID（必填）
     * @return 分类名称列表，无数据时返回空列表
     */
    List<String> getUserCategories(Long userId);

    /**
     * 为用户添加一个兴趣分类标签
     * 业务逻辑：检查该分类是否已存在 → 不存在则新增用户-分类关联记录
     * 异常场景：用户已拥有该分类时返回false；分类名称无效时返回false
     *
     * @param userId       用户ID（必填）
     * @param categoryName 分类名称（必填）
     * @return true表示添加成功，false表示添加失败或已存在
     */
    boolean addCategory(Long userId, String categoryName);

    /**
     * 移除用户的一个兴趣分类标签
     * 业务逻辑：查找用户-分类关联记录 → 删除该关联
     * 异常场景：用户无该分类时返回false
     *
     * @param userId       用户ID（必填）
     * @param categoryName 分类名称（必填）
     * @return true表示移除成功，false表示移除失败或不存在
     */
    boolean removeCategory(Long userId, String categoryName);

    /**
     * 批量更新用户的兴趣分类标签
     * 业务逻辑：删除该用户所有现有分类 → 批量插入新的分类列表 → 实现全量替换
     * 异常场景：用户ID无效时返回false
     *
     * @param userId     用户ID（必填）
     * @param categories 新的分类名称列表（可为空，为空表示清空所有分类）
     * @return true表示更新成功，false表示更新失败
     */
    boolean updateCategories(Long userId, List<String> categories);
}
