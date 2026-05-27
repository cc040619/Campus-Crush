package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.UserCategory;
import com.cc.campuscrush.mapper.UserCategoryMapper;
import com.cc.campuscrush.service.UserCategoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 【UserCategoryServiceImpl】用户内容分类服务层实现
 * &lt;p&gt;核心功能：用户首页内容偏好分类的增删改管理，支持默认分类初始化和 JSON 序列化存储&lt;/p&gt;
 * &lt;p&gt;使用场景：用户个性化首页内容筛选，被 UserCategoryController 调用，预设26个默认分类（穿搭、美食、日常等），支持动态添加和删除自定义分类（\"推荐\"分类不可删除且始终置顶），分类数据以 JSON 数组存储于 MySQL 并通过 ObjectMapper 序列化/反序列化&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class UserCategoryServiceImpl implements UserCategoryService {

    @Autowired
    private UserCategoryMapper userCategoryMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private static final List<String> DEFAULT_CATEGORIES = Arrays.asList(
            "推荐", "穿搭", "美食", "日常", "旅行", "美妆", "健身", "读书",
            "直播", "短剧", "头像", "音乐", "游戏", "舞蹈", "绘画", "摄影",
            "情感", "搞笑", "动漫", "壁纸", "手工", "影视", "学习", "健身塑型",
            "家装", "科技数码"
    );

    /**
     * 获取用户的首页内容分类列表（被UserCategoryController调用）
     * 业务逻辑：查user_category表 → 无记录则初始化26个默认分类并返回 → 有记录则JSON反序列化categories字段返回列表
     * 异常场景：JSON反序列化失败时降级返回默认分类列表
     *
     * @param userId 用户ID（必填）
     * @return 分类名称列表，首条为"推荐"
     */
    @Override
    public List<String> getUserCategories(Long userId) {
        UserCategory userCategory = userCategoryMapper.selectByUserId(userId);

        if (userCategory == null) {
            initDefaultCategories(userId);
            return new ArrayList<>(DEFAULT_CATEGORIES);
        }

        try {
            return objectMapper.readValue(userCategory.getCategories(), new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new ArrayList<>(DEFAULT_CATEGORIES);
        }
    }

    /**
     * 添加自定义分类（被UserCategoryController调用）
     * 业务逻辑：获取当前分类列表 → 已存在则返回false → 追加到列表末尾 → 更新存储
     * 异常场景：分类名已存在时返回false（不重复添加）
     *
     * @param userId       用户ID（必填）
     * @param categoryName 分类名称（必填）
     * @return true-添加成功，false-分类名已存在
     */
    @Override
    public boolean addCategory(Long userId, String categoryName) {
        List<String> categories = getUserCategories(userId);

        if (categories.contains(categoryName)) {
            return false;
        }

        categories.add(categoryName);
        return updateCategories(userId, categories);
    }

    /**
     * 删除自定义分类（被UserCategoryController调用）
     * 业务逻辑："推荐"分类不可删除返回false → 获取当前列表 → 分类不存在返回false → 从列表中移除 → 更新存储
     * 异常场景："推荐"分类不允许删除；分类不存在返回false
     *
     * @param userId       用户ID（必填）
     * @param categoryName 分类名称（必填，不能为"推荐"）
     * @return true-删除成功，false-不可删除或分类不存在
     */
    @Override
    public boolean removeCategory(Long userId, String categoryName) {
        if ("推荐".equals(categoryName)) {
            return false;
        }

        List<String> categories = getUserCategories(userId);

        if (!categories.contains(categoryName)) {
            return false;
        }

        categories.remove(categoryName);
        return updateCategories(userId, categories);
    }

    /**
     * 更新用户分类列表（保证"推荐"始终在首位，被addCategory和removeCategory调用）
     * 业务逻辑：若列表不含"推荐"则插入到首位 → JSON序列化列表 → 查user_category表 → 已有记录则update → 无记录则insert新记录
     * 异常场景：JSON序列化失败返回false
     *
     * @param userId     用户ID（必填）
     * @param categories 分类名称列表（必填）
     * @return true-更新成功，false-序列化失败
     */
    @Override
    public boolean updateCategories(Long userId, List<String> categories) {
        if (!categories.contains("推荐")) {
            categories.add(0, "推荐");
        }

        try {
            String categoriesJson = objectMapper.writeValueAsString(categories);

            UserCategory existing = userCategoryMapper.selectByUserId(userId);
            if (existing != null) {
                return userCategoryMapper.updateCategories(userId, categoriesJson) > 0;
            } else {
                UserCategory userCategory = new UserCategory();
                userCategory.setUserId(userId);
                userCategory.setCategories(categoriesJson);
                return userCategoryMapper.insert(userCategory) > 0;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void initDefaultCategories(Long userId) {
        try {
            String categoriesJson = objectMapper.writeValueAsString(DEFAULT_CATEGORIES);
            UserCategory userCategory = new UserCategory();
            userCategory.setUserId(userId);
            userCategory.setCategories(categoriesJson);
            userCategoryMapper.insert(userCategory);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}