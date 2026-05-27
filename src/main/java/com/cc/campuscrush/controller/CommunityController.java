package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.entity.SysUser;
import com.cc.campuscrush.service.PostService;
import com.cc.campuscrush.service.UserService;
import com.cc.campuscrush.vo.UserVO;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * CommunityController控制器
 * &lt;p&gt;核心功能：社区首页帖子列表浏览、用户个人信息查询和资料编辑&lt;/p&gt;
 * &lt;p&gt;使用场景：社区模块的首页展示，支持帖子分页搜索和分类筛选、查看他人主页、编辑个人资料、关键词搜索和热门话题推荐，被前端社区首页、用户主页和个人设置页调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;

    /**
     * 分页查询社区帖子列表（支持关键词搜索和分类筛选，含可见性过滤）
     * 业务逻辑：接收分页参数、关键词、分类和当前用户ID → 委托postService分页查询帖子列表，根据可见性规则过滤 → 返回分页结果
     * 异常场景：无匹配帖子时返回空分页数据
     *
     * @param pageNum 页码（可选，默认值为1）
     * @param pageSize 每页条数（可选，默认值为10）
     * @param keyword 搜索关键词（可选，用于模糊匹配帖子标题）
     * @param category 帖子分类（可选，用于按分类筛选）
     * @param currentUserId 当前登录用户ID（从X-User-Id请求头获取，可选，用于可见性判断）
     * @return Result.data 为PageInfo分页对象，包含帖子列表及分页信息；无帖子时列表为空
     */
    @GetMapping("/post/list")
    public Result<PageInfo<?>> getPostList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        var postList = postService.getPostListWithVisibility(pageNum, pageSize, keyword, category, currentUserId);
        return Result.success(postList);
    }

    /**
     * 查询指定用户的社区主页信息（含关注状态）
     * 业务逻辑：接收目标用户ID和当前用户ID → 委托userService查询目标用户的公开信息及当前用户与该用户的关注关系 → 返回用户VO
     * 异常场景：目标用户不存在时返回"获取用户信息失败"错误；服务层异常时捕获并返回错误提示
     *
     * @param userId 目标用户ID（路径参数，必填）
     * @param currentUserId 当前登录用户ID（从X-User-Id请求头获取，可选，用于查询关注状态）
     * @return Result.data 为UserVO对象，包含昵称、头像、帖子数、关注数和是否已关注等；目标用户不存在时返回error
     */
    @GetMapping("/user/{userId}")
    public Result<UserVO> getCommunityUserInfo(@PathVariable Long userId,
                                               @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        try {
            UserVO userInfo = userService.getUserInfo(userId, currentUserId);
            if (userInfo != null) {
                return Result.success(userInfo);
            }
            return Result.error("获取用户信息失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取用户信息失败");
        }
    }

    /**
     * 更新当前登录用户的个人资料（仅允许修改自己的资料）
     * 业务逻辑：从请求头获取userId → 强制将请求体中的id设置为当前用户ID（防止越权修改他人资料）→ 委托userService更新资料
     * 异常场景：未登录（currentUserId为null）返回"用户未登录"错误；更新失败时返回"更新用户资料失败"错误
     *
     * @param user 用户资料请求体，包含昵称、简介、头像等字段（id字段会被强制覆写为当前用户ID）
     * @param currentUserId 当前登录用户ID（从X-User-Id请求头获取，可选，用于校验登录状态和防止越权）
     * @return Result.data 为null，无返回数据
     */
    @PutMapping("/user/profile")
    public Result<Void> updateProfile(
            @RequestBody SysUser user,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        try {
            // 从X-User-Id请求头获取当前登录用户ID
            if (currentUserId == null) {
                return Result.error("用户未登录");
            }
            
            // 确保只能修改自己的资料
            user.setId(currentUserId);
            
            // 更新用户资料
            userService.updateProfile(user);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新用户资料失败");
        }
    }

    /**
     * 根据关键词搜索社区帖子
     * 业务逻辑：接收搜索关键词 → 委托postService按关键词模糊匹配帖子 → 返回匹配的帖子列表
     * 异常场景：无匹配结果时返回空列表
     *
     * @param keyword 搜索关键词（路径参数，必填，用于模糊匹配帖子标题和内容）
     * @return Result.data 为匹配的帖子列表，无匹配时为空数组
     */
    @GetMapping("/post/search/{keyword}")
    public Result<?> searchPostsByKeyword(@PathVariable String keyword) {
        var posts = postService.searchPostsByKeyword(keyword);
        return Result.success(posts);
    }

    /**
     * 获取社区热门话题推荐列表
     * 业务逻辑：委托postService查询当前热门话题 → 返回话题列表
     * 异常场景：无热门话题时返回空列表
     *
     * @return Result.data 为热门话题列表，通常为字符串数组；无话题时为空数组
     */
    @GetMapping("/hot-topics")
    public Result<?> getHotTopics() {
        var topics = postService.getHotTopics();
        return Result.success(topics);
    }

}
