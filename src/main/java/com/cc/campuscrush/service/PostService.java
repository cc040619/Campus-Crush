package com.cc.campuscrush.service;

import com.cc.campuscrush.entity.Post;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * 【PostService】服务层接口
 * &lt;p&gt;核心功能：提供帖子的增删改查、点赞收藏、浏览统计、热门话题及可见性控制功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于校园社区内容发布与互动场景，被PostController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface PostService {

    /**
     * 分页获取帖子列表（公开访问）
     * 业务逻辑：使用PageHelper分页 → 按关键词和分类筛选 → 仅返回公开可见的帖子 → 按时间倒序排列
     * 异常场景：无匹配帖子时返回空PageInfo
     *
     * @param pageNum  页码（必填，从1开始）
     * @param pageSize 每页条数（必填）
     * @param keyword  搜索关键词（可为空，为空时不筛选）
     * @param category 帖子分类（可为空，为空时不筛选）
     * @return 帖子分页数据，无数据时PageInfo的list为空
     */
    PageInfo<Post> getPostList(int pageNum, int pageSize, String keyword, String category);

    /**
     * 分页获取帖子列表（含可见性控制）
     * 业务逻辑：使用PageHelper分页 → 按当前用户的可见权限过滤帖子 → 按关键词和分类筛选 → 按时间倒序排列
     * 异常场景：无可见帖子时返回空PageInfo
     *
     * @param pageNum       页码（必填，从1开始）
     * @param pageSize      每页条数（必填）
     * @param keyword       搜索关键词（可为空）
     * @param category      帖子分类（可为空）
     * @param currentUserId 当前登录用户ID（必填，用于可见性判断）
     * @return 帖子分页数据，无数据时PageInfo的list为空
     */
    PageInfo<Post> getPostListWithVisibility(int pageNum, int pageSize, String keyword, String category, Long currentUserId);

    /**
     * 获取指定用户的帖子列表（含可见性控制）
     * 业务逻辑：查询该用户的所有帖子 → 根据currentUserId过滤不可见的帖子 → 返回可见帖子列表
     * 异常场景：用户无帖子或所有帖子均不可见时返回空列表
     *
     * @param userId        目标用户ID（必填）
     * @param currentUserId 当前登录用户ID（必填，用于可见性判断）
     * @return 帖子列表，无数据时返回空列表
     */
    java.util.List<Post> getUserPostsWithVisibility(Long userId, Long currentUserId);

    /**
     * 获取帖子详情
     * 业务逻辑：查询帖子基本信息 → 附带作者信息、图片列表、点赞收藏状态 → 返回完整的帖子详情Map
     * 异常场景：帖子不存在时返回空Map或null
     *
     * @param postId 帖子ID（必填）
     * @return Map包含帖子详情、作者信息、互动状态等，帖子不存在时返回空Map
     */
    Map<String, Object> getPostDetail(Long postId);

    /**
     * 创建一篇新帖子
     * 业务逻辑：构建Post实体 → 设置标题、内容、图片、分类、可见性等 → 保存到数据库 → 返回完整帖子信息
     * 异常场景：必填字段（如标题、内容、作者ID）为空时创建失败
     *
     * @param post 帖子实体（必填，需包含userId、标题和内容）
     * @return 创建成功的帖子实体（含自增ID）
     */
    Post createPost(Post post);

    /**
     * 更新一篇帖子
     * 业务逻辑：校验帖子归属（仅作者可编辑） → 更新标题、内容、图片、可见性等字段 → 保存更新
     * 异常场景：帖子不存在时返回null；非作者操作时拒绝更新
     *
     * @param postId 帖子ID（必填）
     * @param post   帖子实体（必填，包含要更新的字段）
     * @return 更新后的帖子实体，帖子不存在时返回null
     */
    Post updatePost(Long postId, Post post);

    /**
     * 删除一篇帖子
     * 业务逻辑：校验帖子归属（仅作者可删除） → 删除帖子及关联的评论、点赞、收藏、图片等数据
     * 异常场景：帖子不存在时静默处理；非作者操作时拒绝删除
     *
     * @param postId 帖子ID（必填）
     */
    void deletePost(Long postId);

    /**
     * 点赞或取消点赞一篇帖子
     * 业务逻辑：检查当前点赞状态 → 已点赞则取消（删除记录），未点赞则点赞（新增记录） → 更新点赞计数
     * 异常场景：帖子不存在时返回false
     *
     * @param postId 帖子ID（必填）
     * @param userId 操作用户ID（必填）
     * @return true表示操作成功，false表示操作失败
     */
    boolean likePost(Long postId, Long userId);

    /**
     * 收藏或取消收藏一篇帖子
     * 业务逻辑：检查当前收藏状态 → 已收藏则取消（删除记录），未收藏则收藏（新增记录） → 更新收藏计数
     * 异常场景：帖子不存在时返回false
     *
     * @param postId 帖子ID（必填）
     * @param userId 操作用户ID（必填）
     * @return true表示操作成功，false表示操作失败
     */
    boolean collectPost(Long postId, Long userId);

    /**
     * 获取当前用户对帖子的互动状态
     * 业务逻辑：查询点赞记录和收藏记录 → 返回是否已点赞和是否已收藏两个布尔值
     * 异常场景：帖子不存在时各状态均为false
     *
     * @param postId 帖子ID（必填）
     * @param userId 当前用户ID（必填）
     * @return Map包含liked（是否点赞）和collected（是否收藏）状态
     */
    Map<String, Boolean> getPostStatus(Long postId, Long userId);

    /**
     * 获取指定用户发布的所有帖子
     * 业务逻辑：查询该用户创建的所有帖子 → 按时间倒序排列
     * 异常场景：用户无帖子时返回空列表
     *
     * @param userId 用户ID（必填）
     * @return 帖子列表，无数据时返回空列表
     */
    java.util.List<Post> getUserPosts(Long userId);

    /**
     * 获取指定用户收藏的所有帖子
     * 业务逻辑：查询该用户的所有收藏记录 → 获取关联的帖子详情 → 按收藏时间倒序排列
     * 异常场景：用户无收藏时返回空列表
     *
     * @param userId 用户ID（必填）
     * @return 收藏的帖子列表，无数据时返回空列表
     */
    java.util.List<Post> getUserCollections(Long userId);

    /**
     * 获取指定用户点赞的所有帖子
     * 业务逻辑：查询该用户的所有点赞记录 → 获取关联的帖子详情 → 按点赞时间倒序排列
     * 异常场景：用户无点赞时返回空列表
     *
     * @param userId 用户ID（必填）
     * @return 点赞的帖子列表，无数据时返回空列表
     */
    java.util.List<Post> getUserLikes(Long userId);

    /**
     * 统计指定用户发布的帖子总数
     * 业务逻辑：统计该用户创建的帖子数量
     * 异常场景：用户无帖子时返回0
     *
     * @param userId 用户ID（必填）
     * @return 帖子数量，无数据时返回0
     */
    int getPostCountByUserId(Long userId);

    /**
     * 获取帖子的互动统计数据
     * 业务逻辑：查询帖子的点赞数、收藏数、评论数、浏览数 → 封装为Map返回
     * 异常场景：帖子不存在时返回各字段均为0的Map
     *
     * @param postId 帖子ID（必填）
     * @return Map包含likeCount、collectCount、commentCount、browseCount等统计字段
     */
    Map<String, Integer> getPostInfo(Long postId);

    /**
     * 增加帖子浏览次数
     * 业务逻辑：检查用户是否在冷却期内（防止刷量） → 不在冷却期则浏览量+1 → 记录该用户的浏览时间
     * 异常场景：帖子不存在时不执行操作；用户在冷却期内则忽略本次浏览
     *
     * @param postId 帖子ID（必填）
     * @param userId 浏览用户ID（必填，用于防刷判断）
     */
    void incrementBrowseCount(Long postId, Long userId);

    /**
     * 按关键词搜索帖子
     * 业务逻辑：在帖子标题和内容中模糊匹配关键词 → 返回匹配的帖子列表
     * 异常场景：无匹配结果时返回空列表
     *
     * @param keyword 搜索关键词（必填）
     * @return 匹配的帖子列表，无数据时返回空列表
     */
    List<Post> searchPostsByKeyword(String keyword);

    /**
     * 获取热门话题列表
     * 业务逻辑：统计近期帖子的关键词频率和互动热度 → 按热度排序 → 返回Top话题列表
     * 异常场景：无帖子数据时返回空列表
     *
     * @return 热门话题列表，每个元素包含话题名称和热度值，无数据时返回空列表
     */
    List<Map<String, Object>> getHotTopics();
}
