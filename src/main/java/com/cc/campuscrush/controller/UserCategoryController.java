package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.service.UserCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * UserCategoryController控制器
 * &lt;p&gt;核心功能：用户兴趣分类的个性化管理&lt;/p&gt;
 * &lt;p&gt;使用场景：用户个性化设置模块，支持查询用户已选分类、添加分类、删除分类、批量更新分类和获取全部可选分类列表，被前端兴趣分类选择页面调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/user-category")
@CrossOrigin
@Slf4j
public class UserCategoryController {

    @Autowired
    private UserCategoryService userCategoryService;

    /**
     * 查询当前用户已选的兴趣分类列表
     * 业务逻辑：从request属性获取userId → 委托userCategoryService查询用户分类 → 返回分类名称列表
     * 异常场景：用户未选择任何分类时返回空列表；服务层异常时返回"获取分类失败"错误
     *
     * @param userId 当前用户ID（从@RequestAttribute获取，由拦截器注入，必填）
     * @return Result.data 为分类名称字符串列表，无分类时为空数组
     */
    @GetMapping
    public Result<List<String>> getUserCategories(@RequestAttribute("userId") Long userId) {
        try {
            List<String> categories = userCategoryService.getUserCategories(userId);
            return Result.success(categories);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取分类失败");
        }
    }

    /**
     * 为用户添加一个兴趣分类
     * 业务逻辑：从request属性获取userId → 校验categoryName非空 → 委托userCategoryService添加分类 → 返回提示
     * 异常场景：分类名称为空返回"分类名称不能为空"错误；分类已存在返回"添加失败，分类已存在"错误；服务层异常返回"添加失败"错误
     *
     * @param userId 当前用户ID（从@RequestAttribute获取，由拦截器注入，必填）
     * @param request 请求体，包含categoryName（分类名称，必填，如"穿搭"、"美食"等）
     * @return Result.data 为字符串"添加成功"；失败时返回error
     */
    @PostMapping("/add")
    public Result<String> addCategory(@RequestAttribute("userId") Long userId, @RequestBody Map<String, String> request) {
        try {
            String categoryName = request.get("categoryName");
            if (categoryName == null || categoryName.trim().isEmpty()) {
                return Result.error("分类名称不能为空");
            }
            boolean success = userCategoryService.addCategory(userId, categoryName);
            if (success) {
                return Result.success("添加成功");
            }
            return Result.error("添加失败，分类已存在");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加失败");
        }
    }

    /**
     * 删除用户的一个兴趣分类
     * 业务逻辑：从request属性获取userId → 校验categoryName非空 → "推荐"分类不可删除 → 委托userCategoryService删除分类 → 返回提示
     * 异常场景：分类名称为空返回"分类名称不能为空"错误；试图删除"推荐"分类返回"推荐分类不能删除"错误；删除失败返回"删除失败"错误
     *
     * @param userId 当前用户ID（从@RequestAttribute获取，由拦截器注入，必填）
     * @param request 请求体，包含categoryName（分类名称，必填）
     * @return Result.data 为字符串"删除成功"；失败时返回error
     */
    @PostMapping("/remove")
    public Result<String> removeCategory(@RequestAttribute("userId") Long userId, @RequestBody Map<String, String> request) {
        try {
            String categoryName = request.get("categoryName");
            if (categoryName == null || categoryName.trim().isEmpty()) {
                return Result.error("分类名称不能为空");
            }
            if ("推荐".equals(categoryName)) {
                return Result.error("推荐分类不能删除");
            }
            boolean success = userCategoryService.removeCategory(userId, categoryName);
            if (success) {
                return Result.success("删除成功");
            }
            return Result.error("删除失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败");
        }
    }

    /**
     * 批量更新用户的兴趣分类列表（全量替换）
     * 业务逻辑：从request属性获取userId → 校验categories列表非空 → 委托userCategoryService全量替换用户分类 → 返回提示
     * 异常场景：分类列表为空返回"分类列表不能为空"错误；更新失败返回"更新失败"错误
     *
     * @param userId 当前用户ID（从@RequestAttribute获取，由拦截器注入，必填）
     * @param request 请求体，包含categories（分类名称列表，必填，全量替换已有分类）
     * @return Result.data 为字符串"更新成功"；失败时返回error
     */
    @PutMapping
    public Result<String> updateCategories(@RequestAttribute("userId") Long userId, @RequestBody Map<String, List<String>> request) {
        try {
            List<String> categories = request.get("categories");
            if (categories == null || categories.isEmpty()) {
                return Result.error("分类列表不能为空");
            }
            boolean success = userCategoryService.updateCategories(userId, categories);
            if (success) {
                return Result.success("更新成功");
            }
            return Result.error("更新失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败");
        }
    }

    /**
     * 获取系统中全部可选兴趣分类列表（无需登录）
     * 业务逻辑：直接返回硬编码的预定义全部分类列表（54个分类，如推荐、穿搭、美食、日常等）
     * 异常场景：无
     *
     * @return Result.data 为所有可选分类名称的字符串列表
     */
    @GetMapping("/all")
    public Result<List<String>> getAllCategories() {
        List<String> allCategories = List.of(
                "推荐", "穿搭", "美食", "日常", "旅行", "美妆", "健身", "读书",
                "直播", "短剧", "头像", "音乐", "游戏", "舞蹈", "绘画", "摄影",
                "情感", "搞笑", "动漫", "壁纸", "手工", "影视", "学习", "健身塑型",
                "家装", "科技数码", "汽车", "男士", "明星", "科学", "职场", "减脂",
                "艺术", "家居", "婚礼", "母婴", "潮鞋", "护肤", "萌宠", "文化",
                "竞技", "机车", "户外", "心理", "体育", "文具", "综艺", "社科",
                "潮玩", "校园", "露营", "人文"
        );
        return Result.success(allCategories);
    }
}
