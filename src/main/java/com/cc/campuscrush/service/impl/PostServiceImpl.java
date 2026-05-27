package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.Collect;
import com.cc.campuscrush.entity.Like;
import com.cc.campuscrush.entity.Post;
import com.cc.campuscrush.entity.PostQueryCondition;
import com.cc.campuscrush.entity.SysUser;
import com.cc.campuscrush.mapper.CollectMapper;
import com.cc.campuscrush.mapper.CommentMapper;
import com.cc.campuscrush.mapper.LikeMapper;
import com.cc.campuscrush.mapper.PostMapper;
import com.cc.campuscrush.mapper.SysUserMapper;
import com.cc.campuscrush.service.ImageCacheService;
import com.cc.campuscrush.service.InteractionNoticeService;
import com.cc.campuscrush.service.PostService;
import com.cc.campuscrush.utils.RedisContext;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 【PostServiceImpl】社区帖子服务层实现
 * &lt;p&gt;核心功能：社区帖子的增删改查、点赞收藏浏览、热门话题聚合及帖子列表/详情的 Redis 缓存管理&lt;/p&gt;
 * &lt;p&gt;使用场景：社区内容核心模块，被 PostController 调用，采用 CompletableFuture 异步并行加载作者信息、点赞数、评论数等关联数据，支持按分类和关键词检索、可见性过滤、浏览去重（排除作者自刷），话题热度通过评论数聚合排序 Top10，点赞和收藏操作异步生成互动通知&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
@Slf4j
public class PostServiceImpl implements PostService {

    private static final String POST_LIST_CACHE_KEY = "Campus-Crush:post:list:";
    private static final String POST_DETAIL_CACHE_KEY = "Campus-Crush:post:detail:";
    private static final int CACHE_EXPIRE_MINUTES = 30;

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private CollectMapper collectMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private InteractionNoticeService interactionNoticeService;
    @Autowired
    @Qualifier("taskExecutor")
    private ExecutorService executor;
    @Autowired
    private ImageCacheService imageCacheService;
    @Autowired
    private RedisContext redisContext;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 获取帖子列表（分页+关键词+分类，Redis缓存，被PostController调用）
     * 业务逻辑：构建缓存key → 查Redis缓存 → 命中直接反序列化返回 → 未命中则PageHelper分页查MySQL（按PostQueryCondition条件） → 结果写入Redis（30分钟TTL） → 返回
     * 异常场景：缓存反序列化失败回退查DB；缓存写入失败不影响业务
     *
     * @param pageNum  页码（必填）
     * @param pageSize 每页条数（必填）
     * @param keyword  搜索关键词（可选，为null/空时不过滤）
     * @param category 帖子分类（可选，为null/空时不过滤）
     * @return 帖子分页对象
     */
    @Override
    public PageInfo<Post> getPostList(int pageNum, int pageSize, String keyword, String category) {
        String cacheKey = buildPostListCacheKey(pageNum, pageSize, keyword, category, null);
        
        try {
            String cachedData = (String) redisContext.get(cacheKey);
            if (cachedData != null) {
                log.debug("从Redis缓存获取帖子列表: key={}", cacheKey);
                PageInfo<Post> cachedPage = objectMapper.readValue(cachedData, new TypeReference<PageInfo<Post>>() {});
                return cachedPage;
            }
        } catch (Exception e) {
            log.warn("从Redis缓存获取帖子列表失败: {}", e.getMessage());
        }

        log.debug("从数据库获取帖子列表: pageNum={}, pageSize={}", pageNum, pageSize);
        PageHelper.startPage(pageNum, pageSize);
        PostQueryCondition condition = new PostQueryCondition();
        condition.setKeyword(keyword);
        condition.setCategory(category);
        var posts = postMapper.selectByCondition(condition);
        PageInfo<Post> pageInfo = PageInfo.of(posts);

        try {
            String dataJson = objectMapper.writeValueAsString(pageInfo);
            redisContext.set(cacheKey, dataJson, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            log.debug("帖子列表写入Redis缓存: key={}", cacheKey);
        } catch (Exception e) {
            log.warn("帖子列表写入Redis缓存失败: {}", e.getMessage());
        }

        return pageInfo;
    }

    /**
     * 获取帖子列表（带可见性过滤，Redis缓存，被PostController调用）
     * 业务逻辑：构建含userId的缓存key → 查Redis → 命中返回 → 未命中则PageHelper分页查MySQL（PostQueryCondition含currentUserId过滤可见性） → 写入Redis（30分钟TTL） → 返回
     * 异常场景：缓存反序列化失败回退查DB
     *
     * @param pageNum       页码（必填）
     * @param pageSize      每页条数（必填）
     * @param keyword       搜索关键词（可选）
     * @param category      帖子分类（可选）
     * @param currentUserId 当前登录用户ID（必填，用于可见性过滤）
     * @return 帖子分页对象
     */
    @Override
    public PageInfo<Post> getPostListWithVisibility(int pageNum, int pageSize, String keyword, String category, Long currentUserId) {
        String cacheKey = buildPostListCacheKey(pageNum, pageSize, keyword, category, currentUserId);

        try {
            String cachedData = (String) redisContext.get(cacheKey);
            if (cachedData != null) {
                log.debug("从Redis缓存获取帖子列表(带可见性): key={}", cacheKey);
                PageInfo<Post> cachedPage = objectMapper.readValue(cachedData, new TypeReference<PageInfo<Post>>() {});
                return cachedPage;
            }
        } catch (Exception e) {
            log.warn("从Redis缓存获取帖子列表(带可见性)失败: {}", e.getMessage());
        }

        log.debug("从数据库获取帖子列表(带可见性): pageNum={}, pageSize={}, userId={}", pageNum, pageSize, currentUserId);
        PageHelper.startPage(pageNum, pageSize);
        PostQueryCondition condition = new PostQueryCondition();
        condition.setKeyword(keyword);
        condition.setCategory(category);
        condition.setCurrentUserId(currentUserId);
        var posts = postMapper.selectWithVisibility(condition);
        PageInfo<Post> pageInfo = PageInfo.of(posts);

        try {
            String dataJson = objectMapper.writeValueAsString(pageInfo);
            redisContext.set(cacheKey, dataJson, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            log.debug("帖子列表(带可见性)写入Redis缓存: key={}", cacheKey);
        } catch (Exception e) {
            log.warn("帖子列表(带可见性)写入Redis缓存失败: {}", e.getMessage());
        }

        return pageInfo;
    }

    /**
     * 获取指定用户的帖子列表（带可见性过滤，被PostController调用）
     * 业务逻辑：按userId和currentUserId查MySQL，根据可见性规则过滤
     * 异常场景：无帖子时返回空列表
     *
     * @param userId        目标用户ID（必填）
     * @param currentUserId 当前登录用户ID（必填）
     * @return 过滤后的帖子列表
     */
    @Override
    public java.util.List<Post> getUserPostsWithVisibility(Long userId, Long currentUserId) {
        return postMapper.selectByUserIdWithVisibility(userId, currentUserId);
    }

    /**
     * 获取帖子详情（Redis缓存+异步并行加载关联数据，被PostController调用）
     * 业务逻辑：查Redis缓存 → 命中返回 → 未命中则异步查帖子本体 → 帖子不存在返回null → 并行异步加载作者信息、点赞数、评论数 → 组装Map含作者昵称/头像/图片/分类/位置/可见性/统计数据 → 写入Redis缓存 → 返回
     * 异常场景：帖子不存在返回null；异步异常返回null
     *
     * @param postId 帖子ID（必填）
     * @return 帖子详情Map，帖子不存在时返回null
     */
    @Override
    public Map<String, Object> getPostDetail(Long postId) {
        String cacheKey = POST_DETAIL_CACHE_KEY + postId;
        
        try {
            String cachedData = (String) redisContext.get(cacheKey);
            if (cachedData != null) {
                log.debug("从Redis缓存获取帖子详情: key={}", cacheKey);
                return objectMapper.readValue(cachedData, new TypeReference<Map<String, Object>>() {});
            }
        } catch (Exception e) {
            log.warn("从Redis缓存获取帖子详情失败: {}", e.getMessage());
        }

        CompletableFuture<Post> postFuture = CompletableFuture.supplyAsync(
                () -> postMapper.selectById(postId), executor);

        try {
            Post post = postFuture.get();
            if (post == null) {
                return null;
            }

            CompletableFuture<SysUser> authorFuture = CompletableFuture.supplyAsync(
                    () -> sysUserMapper.selectById(post.getUserId()), executor);
            CompletableFuture<Integer> likeCountFuture = CompletableFuture.supplyAsync(
                    () -> likeMapper.countByPostId(postId), executor);
            CompletableFuture<Integer> commentCountFuture = CompletableFuture.supplyAsync(
                    () -> commentMapper.countByPostId(postId), executor);

            CompletableFuture.allOf(authorFuture, likeCountFuture, commentCountFuture).join();

            Map<String, Object> result = new HashMap<>();
            result.put("id", post.getId());
            result.put("userId", post.getUserId());
            result.put("title", post.getTitle());
            result.put("content", post.getContent());
            List<String> imageUrls = imageCacheService.getPostImages(postId);
            result.put("images", imageUrls);
            result.put("categories", post.getCategories());
            result.put("location", post.getLocation());
            result.put("visibility", post.getVisibility());
            result.put("likeCount", post.getLikeCount());
            result.put("collectCount", post.getCollectCount());
            result.put("createTime", post.getCreateTime());
            result.put("updateTime", post.getUpdateTime());

            SysUser author = authorFuture.get();
            if (author != null) {
                result.put("authorName", author.getNickname() != null ? author.getNickname() : author.getUsername());
                result.put("authorAvatar", imageCacheService.getAvatar(author.getId()));
            } else {
                result.put("authorName", "默认用户");
                result.put("authorAvatar", null);
            }

            result.put("commentCount", commentCountFuture.get());

            try {
                String dataJson = objectMapper.writeValueAsString(result);
                redisContext.set(cacheKey, dataJson, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
                log.debug("帖子详情写入Redis缓存: key={}", cacheKey);
            } catch (Exception e) {
                log.warn("帖子详情写入Redis缓存失败: {}", e.getMessage());
            }

            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 创建帖子（被PostController调用）
     * 业务逻辑：插入帖子到MySQL → 清除所有帖子列表缓存（使缓存失效） → 返回含自增ID的帖子对象
     * 异常场景：插入失败由MyBatis抛出异常
     *
     * @param post 帖子实体（必填）
     * @return 含自增ID的帖子对象
     */
    @Override
    public Post createPost(Post post) {
        postMapper.insert(post);
        clearPostListCache();
        log.debug("帖子创建成功，清除帖子列表缓存");
        return post;
    }

    /**
     * 更新帖子（被PostController调用）
     * 业务逻辑：设置帖子id → 更新MySQL → 删除帖子详情Redis缓存 → 返回帖子对象
     * 异常场景：帖子不存在时update影响0行
     *
     * @param postId 帖子ID（必填）
     * @param post   帖子实体（必填，含更新字段）
     * @return 更新后的帖子对象
     */
    @Override
    public Post updatePost(Long postId, Post post) {
        post.setId(postId);
        postMapper.updateById(post);
        clearPostCache(postId);
        log.debug("帖子更新成功，清除缓存: postId={}", postId);
        return post;
    }

    /**
     * 删除帖子（事务性级联删除，被PostController调用）
     * 业务逻辑：依次删除关联评论、点赞、收藏记录 → 删除帖子本身 → 删除帖子图片缓存 → 清除Redis中的帖子详情和列表缓存
     * 异常场景：事务内任何步骤失败均回滚；帖子不存在时评论/点赞/收藏删除0行
     *
     * @param postId 帖子ID（必填）
     */
    @Override
    @Transactional
    public void deletePost(Long postId) {
        commentMapper.deleteByPostId(postId);
        likeMapper.deleteByPostId(postId);
        collectMapper.deleteByPostId(postId);
        postMapper.deleteById(postId);
        imageCacheService.deletePostImages(postId);
        
        clearPostCache(postId);
        clearPostListCache();
        log.debug("帖子删除成功，清除缓存: postId={}", postId);
    }

    /**
     * 切换帖子点赞状态（被PostController调用）
     * 业务逻辑：查当前用户是否已点赞该帖子(type=1) → 已点赞则删除记录并点赞数-1（最小0） → 未点赞则插入记录并点赞数+1 → 更新帖子实体 → 刷新详情缓存 → 非作者点赞时异步发送点赞通知(type=1)
     * 异常场景：通知发送异步执行，失败静默忽略；缓存刷新失败不影响业务
     *
     * @param postId 帖子ID（必填）
     * @param userId 操作用户ID（必填）
     * @return true-已点赞（点赞后状态），false-已取消点赞
     */
    @Override
    public boolean likePost(Long postId, Long userId) {
        var like = likeMapper.selectByPostIdAndUserId(postId, userId, 1);

        if (like != null) {
            likeMapper.deleteById(like.getId());
            var post = postMapper.selectById(postId);
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            postMapper.updateById(post);
            updatePostDetailCache(postId);
            return false;
        } else {
            var newLike = new Like();
            newLike.setPostId(postId);
            newLike.setUserId(userId);
            newLike.setType(1);
            likeMapper.insert(newLike);
            var post = postMapper.selectById(postId);
            post.setLikeCount(post.getLikeCount() + 1);
            postMapper.updateById(post);
            updatePostDetailCache(postId);

            if (!userId.equals(post.getUserId())) {
                createLikeNoticeAsync(post.getUserId(), userId, postId, post.getTitle());
            }

            return true;
        }
    }

    /**
     * 异步创建点赞互动通知（被likePost调用，使用Spring @Async）
     * 业务逻辑：查发起者用户信息 → 调用interactionNoticeService创建type=1（点赞）通知 → 异常静默忽略
     * 异常场景：任何步骤失败仅忽略，不影响点赞主流程
     *
     * @param userId     帖子作者ID（通知接收者，必填）
     * @param fromUserId 点赞者ID（必填）
     * @param postId     帖子ID（必填）
     * @param postTitle  帖子标题（必填）
     */
    @Async
    public void createLikeNoticeAsync(Long userId, Long fromUserId, Long postId, String postTitle) {
        try {
            SysUser fromUser = sysUserMapper.selectById(fromUserId);
            interactionNoticeService.createNotice(
                    userId,
                    fromUserId,
                    fromUser.getNickname() != null ? fromUser.getNickname() : fromUser.getUsername(),
                    imageCacheService.getAvatar(fromUserId),
                    postId,
                    postTitle,
                    1
            );
        } catch (Exception e) {
        }
    }

    /**
     * 切换帖子收藏状态（被PostController调用）
     * 业务逻辑：查当前用户是否已收藏该帖子 → 已收藏则删除记录并收藏数-1（最小0） → 未收藏则插入记录并收藏数+1 → 更新帖子实体 → 刷新详情缓存 → 非作者收藏时异步发送收藏通知(type=2)
     * 异常场景：通知发送异步执行，失败静默忽略
     *
     * @param postId 帖子ID（必填）
     * @param userId 操作用户ID（必填）
     * @return true-已收藏（收藏后状态），false-已取消收藏
     */
    @Override
    public boolean collectPost(Long postId, Long userId) {
        var collect = collectMapper.selectByPostIdAndUserId(postId, userId);

        if (collect != null) {
            collectMapper.deleteById(collect.getId());
            var post = postMapper.selectById(postId);
            post.setCollectCount(Math.max(0, post.getCollectCount() - 1));
            postMapper.updateById(post);
            updatePostDetailCache(postId);
            return false;
        } else {
            var newCollect = new Collect();
            newCollect.setPostId(postId);
            newCollect.setUserId(userId);
            collectMapper.insert(newCollect);
            var post = postMapper.selectById(postId);
            post.setCollectCount(post.getCollectCount() + 1);
            postMapper.updateById(post);
            updatePostDetailCache(postId);

            if (!userId.equals(post.getUserId())) {
                createCollectNoticeAsync(post.getUserId(), userId, postId, post.getTitle());
            }

            return true;
        }
    }

    /**
     * 异步创建收藏互动通知（被collectPost调用，使用Spring @Async）
     * 业务逻辑：查发起者用户信息 → 调用interactionNoticeService创建type=2（收藏）通知 → 异常静默忽略
     * 异常场景：任何步骤失败仅忽略，不影响收藏主流程
     *
     * @param userId     帖子作者ID（通知接收者，必填）
     * @param fromUserId 收藏者ID（必填）
     * @param postId     帖子ID（必填）
     * @param postTitle  帖子标题（必填）
     */
    @Async
    public void createCollectNoticeAsync(Long userId, Long fromUserId, Long postId, String postTitle) {
        try {
            SysUser fromUser = sysUserMapper.selectById(fromUserId);
            interactionNoticeService.createNotice(
                    userId,
                    fromUserId,
                    fromUser.getNickname() != null ? fromUser.getNickname() : fromUser.getUsername(),
                    imageCacheService.getAvatar(fromUserId),
                    postId,
                    postTitle,
                    2
            );
        } catch (Exception e) {
        }
    }

    /**
     * 获取当前用户对帖子的点赞和收藏状态（被PostController调用）
     * 业务逻辑：CompletableFuture并行查like表和collect表 → 等待完成 → 组装{isLiked, isCollected}
     * 异常场景：异步异常时默认均为false
     *
     * @param postId 帖子ID（必填）
     * @param userId 用户ID（必填）
     * @return Map含isLiked和isCollected布尔值
     */
    @Override
    public Map<String, Boolean> getPostStatus(Long postId, Long userId) {
        Map<String, Boolean> status = new HashMap<>();

        CompletableFuture<Integer> likeFuture = CompletableFuture.supplyAsync(
                () -> likeMapper.countByPostIdAndUserId(postId, userId, 1), executor);
        CompletableFuture<Integer> collectFuture = CompletableFuture.supplyAsync(
                () -> collectMapper.countByPostIdAndUserId(postId, userId), executor);

        CompletableFuture.allOf(likeFuture, collectFuture).join();

        try {
            status.put("isLiked", likeFuture.get() > 0);
            status.put("isCollected", collectFuture.get() > 0);
        } catch (Exception e) {
            status.put("isLiked", false);
            status.put("isCollected", false);
        }

        return status;
    }

    /**
     * 获取用户发布的所有帖子（被PostController调用）
     * 业务逻辑：按userId查post表返回全量列表
     * 异常场景：无帖子时返回空列表
     *
     * @param userId 用户ID（必填）
     * @return 帖子列表
     */
    @Override
    public java.util.List<Post> getUserPosts(Long userId) {
        return postMapper.selectByUserId(userId);
    }

    /**
     * 获取用户收藏的所有帖子（被PostController调用）
     * 业务逻辑：通过关联查询userId收藏的帖子列表
     * 异常场景：无收藏时返回空列表
     *
     * @param userId 用户ID（必填）
     * @return 收藏的帖子列表
     */
    @Override
    public java.util.List<Post> getUserCollections(Long userId) {
        return postMapper.selectByCollectionUserId(userId);
    }

    /**
     * 获取用户点赞的所有帖子（被PostController调用）
     * 业务逻辑：通过关联查询userId点赞的帖子列表
     * 异常场景：无点赞时返回空列表
     *
     * @param userId 用户ID（必填）
     * @return 点赞的帖子列表
     */
    @Override
    public java.util.List<Post> getUserLikes(Long userId) {
        return postMapper.selectByLikeUserId(userId);
    }

    /**
     * 获取用户发帖总数（被PostController调用）
     * 业务逻辑：查post表按userId计数
     * 异常场景：无帖子时返回0
     *
     * @param userId 用户ID（必填）
     * @return 帖子总数
     */
    @Override
    public int getPostCountByUserId(Long userId) {
        return Math.toIntExact(postMapper.getPostCountByUserId(userId));
    }

    /**
     * 获取帖子的赞/收藏/浏览统计数据（被PostController调用）
     * 业务逻辑：查帖子实体 → 提取like_count/collect_count/browse_count → 帖子不存在时返回默认0值
     * 异常场景：帖子不存在时三个计数均返回0
     *
     * @param postId 帖子ID（必填）
     * @return Map含like_count、collect_count、browse_count
     */
    @Override
    public Map<String, Integer> getPostInfo(Long postId) {
        Map<String, Integer> info = new HashMap<>();
        Post post = postMapper.selectById(postId);
        if (post != null) {
            info.put("like_count", post.getLikeCount());
            info.put("collect_count", post.getCollectCount());
            info.put("browse_count", post.getBrowseCount());
        } else {
            info.put("like_count", 0);
            info.put("collect_count", 0);
            info.put("browse_count", 0);
        }
        return info;
    }

    /**
     * 增加帖子浏览量（去重排除作者，被PostController调用）
     * 业务逻辑：帖子不存在则返回 → 访问者为作者本人则跳过（不自增） → 增加MySQL浏览计数 → 插入浏览记录（用于去重） → 刷新帖子详情缓存
     * 异常场景：帖子不存在时静默返回；作者浏览不计数；DB异常仅记录日志
     *
     * @param postId 帖子ID（必填）
     * @param userId 浏览用户ID（必填）
     */
    @Override
    public void incrementBrowseCount(Long postId, Long userId) {
        try {
            Post post = postMapper.selectById(postId);
            if (post == null) {
                log.warn("浏览量增加失败：帖子不存在，postId={}", postId);
                return;
            }
            if (userId.equals(post.getUserId())) {
                log.debug("浏览量未增加：访问者是帖子作者，postId={}, userId={}", postId, userId);
                return;
            }
            postMapper.incrementBrowseCount(postId);
            postMapper.insertBrowseRecord(postId, userId);
            
            updatePostDetailCache(postId);
            log.info("浏览量增加成功：postId={}, userId={}", postId, userId);
        } catch (Exception e) {
            log.error("浏览量增加失败：postId={}, userId={}, error={}", postId, userId, e.getMessage());
        }
    }

    /**
     * 按关键词搜索帖子（按评论数排序，被PostController调用）
     * 业务逻辑：按关键词模糊匹配帖子，结果按评论数降序排列
     * 异常场景：无匹配时返回空列表
     *
     * @param keyword 搜索关键词（必填）
     * @return 帖子列表（按评论数降序）
     */
    @Override
    public List<Post> searchPostsByKeyword(String keyword) {
        return postMapper.searchByKeywordOrderByComment(keyword);
    }

    /**
     * 获取热门话题Top10（按话题关联帖子的评论总数排序，被PostController调用）
     * 业务逻辑：查MySQL获取帖子分类和评论数原始数据 → 解析每个帖子的categories JSON数组 → 排除"推荐"分类 → 按分类聚合评论数 → 降序排列取前10 → 返回{name, count}
     * 异常场景：无帖子时返回空列表；JSON解析失败仅记录日志
     *
     * @return 热门话题列表，每项含name和count
     */
    @Override
    public List<Map<String, Object>> getHotTopics() {
        PageHelper.clearPage();
        List<Map<String, Object>> rawTopics = postMapper.getHotTopics();
        
        Map<String, Long> topicCommentCount = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        
        for (Map<String, Object> item : rawTopics) {
            String categories = (String) item.get("categories");
            Long commentCount = item.get("comment_count") != null ? ((Number) item.get("comment_count")).longValue() : 0L;
            
            if (categories != null && !categories.isEmpty() && !"[]".equals(categories)) {
                try {
                    List<String> categoryList = objectMapper.readValue(categories, new TypeReference<List<String>>() {});
                    for (String category : categoryList) {
                        if (!"推荐".equals(category)) {
                            topicCommentCount.merge(category, commentCount, Long::sum);
                        }
                    }
                } catch (Exception e) {
                    log.error("解析分类失败: {}", e.getMessage());
                }
            }
        }
        
        return topicCommentCount.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("name", entry.getKey());
                    result.put("count", entry.getValue());
                    return result;
                })
                .collect(Collectors.toList());
    }

    private String buildPostListCacheKey(int pageNum, int pageSize, String keyword, String category, Long userId) {
        StringBuilder key = new StringBuilder(POST_LIST_CACHE_KEY);
        key.append(pageNum).append(":").append(pageSize);
        if (keyword != null && !keyword.isEmpty()) {
            key.append(":").append(keyword);
        } else {
            key.append(":-");
        }
        if (category != null && !category.isEmpty()) {
            key.append(":").append(category);
        } else {
            key.append(":-");
        }
        if (userId != null) {
            key.append(":").append(userId);
        } else {
            key.append(":-");
        }
        return key.toString();
    }

    private void clearPostListCache() {
        try {
            redisContext.deletePattern(POST_LIST_CACHE_KEY + "*");
            log.debug("清除所有帖子列表缓存");
        } catch (Exception e) {
            log.warn("清除帖子列表缓存失败: {}", e.getMessage());
        }
    }

    private void clearPostCache(Long postId) {
        try {
            redisContext.delete(POST_DETAIL_CACHE_KEY + postId);
            log.debug("清除帖子详情缓存: postId={}", postId);
        } catch (Exception e) {
            log.warn("清除帖子详情缓存失败: {}", e.getMessage());
        }
    }

    private void updatePostDetailCache(Long postId) {
        try {
            redisContext.delete(POST_DETAIL_CACHE_KEY + postId);
            getPostDetail(postId);
        } catch (Exception e) {
            log.warn("更新帖子详情缓存失败: {}", e.getMessage());
        }
    }
}