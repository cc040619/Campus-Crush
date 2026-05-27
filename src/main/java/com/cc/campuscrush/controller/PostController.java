package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.entity.Post;
import com.cc.campuscrush.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * PostController控制器
 * &lt;p&gt;核心功能：社区帖子的发布、编辑、删除、点赞、收藏和个人帖子管理&lt;/p&gt;
 * &lt;p&gt;使用场景：社区内容模块的核心控制器，支持帖子详情浏览（含浏览量统计）、创建和编辑帖子、删除帖子、点赞/收藏操作及状态查询、查看指定用户的帖子/收藏/点赞列表、帖子数量统计和信息概览，被前端帖子详情页、发帖编辑页、个人主页和收藏页调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/community/post")
@RequiredArgsConstructor
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 查询帖子详情（同时增加浏览量）
     * 业务逻辑：从request属性或header获取userId → 如有userId则调用incrementBrowseCount增加浏览量 → 委托postService查询帖子详情 → 返回帖子数据
     * 异常场景：用户未登录时仅查询详情不增加浏览量；帖子不存在时服务层返回null或空数据
     *
     * @param id 帖子ID（路径参数，必填）
     * @param request HTTP请求对象（用于从request属性获取userId，优先于header）
     * @param headerUserId 当前用户ID（从X-User-Id请求头获取，可选，作为request属性的后备）
     * @return Result.data 为Map，包含帖子详情（标题、内容、作者、点赞数、评论数等）；帖子不存在时数据为空
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getPostDetail(@PathVariable Long id, HttpServletRequest request, @RequestHeader(value = "X-User-Id", required = false) Long headerUserId) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            userId = headerUserId;
        }
        if (userId != null) {
            postService.incrementBrowseCount(id, userId);
        }
        var post = postService.getPostDetail(id);
        return Result.success(post);
    }

    /**
     * 创建一篇新帖子
     * 业务逻辑：从请求头获取userId → 构建Post对象（注入标题、内容、分类、位置、可见性、图片等） → 委托postService创建帖子 → 返回创建后的帖子对象
     * 异常场景：userId为空时服务层可能报错（前端请求头必须携带X-User-Id）
     *
     * @param title 帖子标题（请求参数，必填）
     * @param content 帖子正文内容（请求参数，必填）
     * @param categories 帖子分类（请求参数，必填，如"穿搭,美食"逗号分隔）
     * @param location 发布位置（请求参数，必填）
     * @param visibility 可见性（请求参数，必填，0=公开/1=仅好友可见/2=仅自己可见）
     * @param images 图片URL列表的JSON字符串（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为新创建的Post对象，包含id、创建时间等完整信息
     */
    @PostMapping
    public Result<Post> createPost(@RequestParam("title") String title,
                                   @RequestParam("content") String content,
                                   @RequestParam("categories") String categories,
                                   @RequestParam("location") String location,
                                   @RequestParam("visibility") Integer visibility,
                                   @RequestParam("images") String images,
                                   @RequestHeader("X-User-Id") Long userId) {
        // 构建帖子对象
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(title);
        post.setContent(content);
        post.setCategories(categories);
        post.setLocation(location);
        post.setVisibility(visibility);
        post.setImages(images);

        var createdPost = postService.createPost(post);
        return Result.success(createdPost);
    }

    /**
     * 编辑更新指定帖子（仅作者可编辑）
     * 业务逻辑：从请求头获取userId → 构建Post对象（所有字段均可更新） → 委托postService更新帖子 → 返回更新后的帖子对象
     * 异常场景：帖子不存在或非作者编辑时服务层处理
     *
     * @param id 帖子ID（路径参数，必填）
     * @param title 新的帖子标题（请求参数，必填）
     * @param content 新的帖子内容（请求参数，必填）
     * @param categories 新的分类（请求参数，必填）
     * @param location 新的位置（请求参数，必填）
     * @param visibility 新的可见性（请求参数，必填）
     * @param images 新的图片JSON（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填，用于校验是否为作者）
     * @return Result.data 为更新后的Post对象
     */
    @PutMapping("/{id}")
    public Result<Post> updatePost(@PathVariable Long id,
                                   @RequestParam("title") String title,
                                   @RequestParam("content") String content,
                                   @RequestParam("categories") String categories,
                                   @RequestParam("location") String location,
                                   @RequestParam("visibility") Integer visibility,
                                   @RequestParam("images") String images,
                                   @RequestHeader("X-User-Id") Long userId) {
        // 构建帖子对象
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(title);
        post.setContent(content);
        post.setCategories(categories);
        post.setLocation(location);
        post.setVisibility(visibility);
        post.setImages(images);

        var updatedPost = postService.updatePost(id, post);
        return Result.success(updatedPost);
    }

    /**
     * 删除指定帖子（仅作者可删除）
     * 业务逻辑：接收帖子ID → 委托postService删除帖子 → 返回成功
     * 异常场景：帖子不存在时服务层处理（静默返回或抛异常）
     *
     * @param id 帖子ID（路径参数，必填）
     * @return Result.data 为null，无返回数据
     */
    @DeleteMapping("/{id}")
    public Result<?> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return Result.success();
    }

    /**
     * 切换帖子的点赞状态（点赞/取消点赞）
     * 业务逻辑：接收帖子ID和用户ID → 委托postService执行点赞切换逻辑 → 返回当前点赞状态
     * 异常场景：帖子不存在时服务层返回false
     *
     * @param id 帖子ID（路径参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为true表示当前已点赞，false表示未点赞
     */
    @PostMapping("/{id}/like")
    public Result<Boolean> likePost(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        var result = postService.likePost(id, userId);
        return Result.success(result);
    }

    /**
     * 切换帖子的收藏状态（收藏/取消收藏）
     * 业务逻辑：接收帖子ID和用户ID → 委托postService执行收藏切换逻辑 → 返回当前收藏状态
     * 异常场景：帖子不存在时服务层返回false
     *
     * @param id 帖子ID（路径参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为true表示当前已收藏，false表示未收藏
     */
    @PostMapping("/{id}/collect")
    public Result<Boolean> collectPost(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        var result = postService.collectPost(id, userId);
        return Result.success(result);
    }

    /**
     * 查询当前用户对指定帖子的互动状态（点赞和收藏）
     * 业务逻辑：接收帖子ID和用户ID → 委托postService查询用户的点赞和收藏状态 → 返回状态Map
     * 异常场景：帖子不存在时返回默认状态（均为false）
     *
     * @param id 帖子ID（路径参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为Map，包含isLiked（是否已点赞）和isCollected（是否已收藏）两个布尔字段
     */
    @GetMapping("/{id}/status")
    public Result<Map<String, Boolean>> getPostStatus(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        var status = postService.getPostStatus(id, userId);
        return Result.success(status);
    }

    /**
     * 查询指定用户发布的帖子列表（含可见性过滤）
     * 业务逻辑：接收目标用户ID和当前用户ID → 委托postService根据可见性规则查询目标用户的帖子 → 返回帖子列表
     * 异常场景：目标用户无帖子时返回空列表；不可见的帖子被过滤
     *
     * @param userId 目标用户ID（路径参数，必填）
     * @param currentUserId 当前登录用户ID（从X-User-Id请求头获取，可选，用于可见性判断）
     * @return Result.data 为Post列表，包含可见的帖子；无帖子时为空数组
     */
    @GetMapping("/user/{userId}")
    public Result<List<Post>> getUserPosts(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {
        var posts = postService.getUserPostsWithVisibility(userId, currentUserId);
        return Result.success(posts);
    }

    /**
     * 查询指定用户收藏的帖子列表
     * 业务逻辑：接收目标用户ID → 委托postService查询该用户的收藏帖子 → 返回帖子列表
     * 异常场景：无收藏时返回空列表
     *
     * @param userId 目标用户ID（路径参数，必填）
     * @return Result.data 为Post列表，无收藏时为空数组
     */
    @GetMapping("/collections/{userId}")
    public Result<List<Post>> getUserCollections(@PathVariable Long userId) {
        var collections = postService.getUserCollections(userId);
        return Result.success(collections);
    }

    /**
     * 查询指定用户点赞过的帖子列表
     * 业务逻辑：接收目标用户ID → 委托postService查询该用户点赞的帖子 → 返回帖子列表
     * 异常场景：无点赞记录时返回空列表
     *
     * @param userId 目标用户ID（路径参数，必填）
     * @return Result.data 为Post列表，无点赞时为空数组
     */
    @GetMapping("/likes/{userId}")
    public Result<List<Post>> getUserLikes(@PathVariable Long userId) {
        var likes = postService.getUserLikes(userId);
        return Result.success(likes);
    }

    /**
     * 查询指定用户发布的帖子总数
     * 业务逻辑：接收目标用户ID → 委托postService统计帖子数量 → 返回计数
     * 异常场景：用户无帖子时返回0
     *
     * @param userId 目标用户ID（路径参数，必填）
     * @return Result.data 为该用户的帖子总数量（整数）
     */
    @GetMapping("/count/{userId}")
    public Result<Integer> getPostCountByUserId(@PathVariable Long userId) {
        var count = postService.getPostCountByUserId(userId);
        return Result.success(count);
    }

    /**
     * 查询指定帖子的互动统计信息（点赞数、评论数等）
     * 业务逻辑：接收帖子ID → 委托postService查询帖子的互动统计数据 → 返回统计Map
     * 异常场景：帖子不存在时返回默认数据（各计数为0）
     *
     * @param postId 帖子ID（路径参数，必填）
     * @return Result.data 为Map，包含likeCount（点赞数）、commentCount（评论数）等统计字段
     */
    @GetMapping("/info/{postId}")
    public Result<Map<String, Integer>> getPostInfo(@PathVariable Long postId) {
        var info = postService.getPostInfo(postId);
        return Result.success(info);
    }

}
