package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.common.RedisConstant;
import com.cc.campuscrush.entity.Post;
import com.cc.campuscrush.entity.SysUser;
import com.cc.campuscrush.mapper.PostMapper;
import com.cc.campuscrush.mapper.SysUserMapper;
import com.cc.campuscrush.service.ImageCacheService;
import com.cc.campuscrush.utils.RedisContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 【ImageCacheServiceImpl】图片缓存服务层实现
 * &lt;p&gt;核心功能：基于 Redis 的头像和帖子图片缓存管理，内置缓存穿透和缓存雪崩防护机制&lt;/p&gt;
 * &lt;p&gt;使用场景：全局头像和帖子图片的快速读取与回写，被 PostServiceImpl、FollowServiceImpl、UserServiceImpl 等多个服务调用，采用空值缓存防穿透、随机 TTL 偏移防雪崩，支持列表缓存和单图缓存的批量管理&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Slf4j
@Service
public class ImageCacheServiceImpl implements ImageCacheService {

    @Autowired
    private RedisContext redisContext;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 空值标记，用于标识缓存中存储的是空值（防止穿透）
     */
    private static final String NULL_VALUE_MARKER = "__NULL__";

    // ==================== 头像缓存操作 ====================

    /**
     * 获取用户头像URL（Redis缓存优先，被多个Service调用）
     * 业务逻辑：userId为null返回null → 查Redis缓存 → 命中且为NULL标记返回null（防穿透） → 命中有效值直接返回 → 未命中查MySQL sys_user表 → 头像为null/空写入NULL标记缓存 → 头像有效写入缓存（带随机TTL偏移防雪崩）
     * 异常场景：DB查询异常返回null降级处理；Redis异常仅记录日志不影响业务
     *
     * @param userId 用户ID（必填）
     * @return 头像URL，无头像时返回null
     */
    @Override
    public String getAvatar(Long userId) {
        if (userId == null) {
            return null;
        }

        String cacheKey = buildAvatarKey(userId);
        
        // 1. 先查Redis缓存
        String cachedUrl = getFromCache(cacheKey);
        
        // 2. 命中缓存
        if (cachedUrl != null) {
            // 如果是空值标记，返回null
            if (NULL_VALUE_MARKER.equals(cachedUrl)) {
                log.debug("头像缓存命中空值，userId: {}", userId);
                return null;
            }
            log.debug("头像缓存命中，userId: {}", userId);
            return cachedUrl;
        }

        // 3. 缓存未命中，查DB
        String avatarUrl = null;
        try {
            SysUser user = sysUserMapper.selectById(userId);
            if (user != null) {
                avatarUrl = user.getAvatar();
            }
        } catch (Exception e) {
            log.error("从DB获取头像失败，userId: {}", userId, e);
            // 降级处理：返回null，不抛异常
            return null;
        }

        // 4. 回写Redis（带随机TTL偏移，防雪崩）
        try {
            long ttl = RedisConstant.AVATAR_TTL_SECONDS + getRandomOffset();
            if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
                // 空URL也缓存，防止穿透
                setToCache(cacheKey, NULL_VALUE_MARKER, RedisConstant.NULL_VALUE_TTL_SECONDS);
                log.debug("头像为空，写入空值缓存，userId: {}", userId);
            } else {
                setToCache(cacheKey, avatarUrl, ttl);
                log.debug("头像回写缓存成功，userId: {}, ttl: {}s", userId, ttl);
            }
        } catch (Exception e) {
            // Redis异常不影响业务，仅记录日志
            log.warn("头像回写缓存失败，userId: {}", userId, e);
        }

        return avatarUrl;
    }

    /**
     * 写入/更新用户头像缓存（写时缓存策略，被UserServiceImpl调用）
     * 业务逻辑：userId为null直接返回 → avatarUrl为null/空写入NULL标记 → 有效URL写入Redis（带随机TTL偏移）
     * 异常场景：Redis异常仅记录日志
     *
     * @param userId    用户ID（必填）
     * @param avatarUrl 头像URL（可选，为null或空时缓存空值标记）
     */
    @Override
    public void setAvatar(Long userId, String avatarUrl) {
        if (userId == null) {
            return;
        }

        String cacheKey = buildAvatarKey(userId);
        
        try {
            // 写时缓存：更新MySQL后同步写入Redis（覆盖旧值）
            long ttl = RedisConstant.AVATAR_TTL_SECONDS + getRandomOffset();
            
            if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
                setToCache(cacheKey, NULL_VALUE_MARKER, RedisConstant.NULL_VALUE_TTL_SECONDS);
            } else {
                setToCache(cacheKey, avatarUrl, ttl);
            }
            log.debug("头像写入缓存成功，userId: {}", userId);
        } catch (Exception e) {
            // Redis异常不影响业务，仅记录日志
            log.warn("头像写入缓存失败，userId: {}", userId, e);
        }
    }

    /**
     * 删除用户头像缓存（使缓存失效，被UserServiceImpl调用）
     * 业务逻辑：userId为null直接返回 → 从Redis删除avatar缓存key
     * 异常场景：Redis异常仅记录日志
     *
     * @param userId 用户ID（必填）
     */
    @Override
    public void deleteAvatar(Long userId) {
        if (userId == null) {
            return;
        }

        String cacheKey = buildAvatarKey(userId);
        
        try {
            redisContext.delete(cacheKey);
            log.debug("头像缓存删除成功，userId: {}", userId);
        } catch (Exception e) {
            // Redis异常不影响业务，仅记录日志
            log.warn("头像缓存删除失败，userId: {}", userId, e);
        }
    }

    // ==================== 帖子图片缓存操作 ====================

    /**
     * 获取帖子图片URL列表（Redis缓存优先，被PostServiceImpl等调用）
     * 业务逻辑：postId为null返回空列表 → 查Redis列表缓存 → 命中NULL标记返回空列表 → 命中有效数据返回 → 未命中查MySQL post表并解析images JSON字段 → 空数据写入NULL标记 → 有效数据批量写入列表缓存和单张缓存（带随机TTL偏移）
     * 异常场景：DB查询异常返回空列表降级；JSON解析失败返回空列表；Redis异常仅记录日志
     *
     * @param postId 帖子ID（必填）
     * @return 图片URL列表，无图片时返回空列表
     */
    @Override
    public List<String> getPostImages(Long postId) {
        if (postId == null) {
            return Collections.emptyList();
        }

        String listCacheKey = buildPostImageListKey(postId);
        
        // 1. 先查Redis缓存
        List<String> cachedUrls = getPostImagesFromCache(listCacheKey);
        
        // 2. 命中缓存
        if (cachedUrls != null) {
            // 如果是空值标记，返回空列表
            if (cachedUrls.size() == 1 && NULL_VALUE_MARKER.equals(cachedUrls.get(0))) {
                log.debug("帖子图片缓存命中空值，postId: {}", postId);
                return Collections.emptyList();
            }
            log.debug("帖子图片缓存命中，postId: {}", postId);
            return cachedUrls;
        }

        // 3. 缓存未命中，查DB
        List<String> imageUrls = null;
        try {
            Post post = postMapper.selectById(postId);
            if (post != null && post.getImages() != null && !post.getImages().isEmpty()) {
                // 假设images字段存储的是JSON数组格式
                imageUrls = parseImagesJson(post.getImages());
            }
        } catch (Exception e) {
            log.error("从DB获取帖子图片失败，postId: {}", postId, e);
            // 降级处理：返回空列表，不抛异常
            return Collections.emptyList();
        }

        // 4. 回写Redis（带随机TTL偏移，防雪崩）
        try {
            long ttl = RedisConstant.POST_IMAGE_TTL_SECONDS + getRandomOffset();
            if (imageUrls == null || imageUrls.isEmpty()) {
                // 空URL列表也缓存，防止穿透
                setPostImagesToCache(listCacheKey, Collections.singletonList(NULL_VALUE_MARKER), 
                    RedisConstant.NULL_VALUE_TTL_SECONDS);
                log.debug("帖子图片为空，写入空值缓存，postId: {}", postId);
            } else {
                // 批量写入Redis：同时写入列表缓存和单张图片缓存
                setPostImagesToCache(listCacheKey, imageUrls, ttl);
                
                // 写入单张图片缓存
                for (int i = 0; i < imageUrls.size(); i++) {
                    String singleKey = buildPostImageKey(postId, i);
                    setToCache(singleKey, imageUrls.get(i), ttl);
                }
                log.debug("帖子图片回写缓存成功，postId: {}, imageCount: {}, ttl: {}s", 
                    postId, imageUrls.size(), ttl);
            }
        } catch (Exception e) {
            // Redis异常不影响业务，仅记录日志
            log.warn("帖子图片回写缓存失败，postId: {}", postId, e);
        }

        return imageUrls != null ? imageUrls : Collections.emptyList();
    }

    /**
     * 写入帖子图片缓存（写时缓存策略，被PostServiceImpl调用）
     * 业务逻辑：postId为null直接返回 → imageUrls为空写入NULL标记 → 非空则序列化为JSON写入列表缓存并为每张图片写入单独缓存（带随机TTL偏移）
     * 异常场景：Redis异常仅记录日志
     *
     * @param postId    帖子ID（必填）
     * @param imageUrls 图片URL列表（可选，为空时缓存空值标记）
     */
    @Override
    public void setPostImages(Long postId, List<String> imageUrls) {
        if (postId == null) {
            return;
        }

        String listCacheKey = buildPostImageListKey(postId);
        
        try {
            long ttl = RedisConstant.POST_IMAGE_TTL_SECONDS + getRandomOffset();
            
            if (imageUrls == null || imageUrls.isEmpty()) {
                setPostImagesToCache(listCacheKey, Collections.singletonList(NULL_VALUE_MARKER), 
                    RedisConstant.NULL_VALUE_TTL_SECONDS);
            } else {
                // 批量写入Redis
                setPostImagesToCache(listCacheKey, imageUrls, ttl);
                
                // 写入单张图片缓存
                for (int i = 0; i < imageUrls.size(); i++) {
                    String singleKey = buildPostImageKey(postId, i);
                    setToCache(singleKey, imageUrls.get(i), ttl);
                }
            }
            log.debug("帖子图片写入缓存成功，postId: {}", postId);
        } catch (Exception e) {
            // Redis异常不影响业务，仅记录日志
            log.warn("帖子图片写入缓存失败，postId: {}", postId, e);
        }
    }

    /**
     * 更新帖子图片缓存（覆盖写入，被PostServiceImpl调用）
     * 业务逻辑：直接委托setPostImages覆盖缓存
     *
     * @param postId    帖子ID（必填）
     * @param imageUrls 新的图片URL列表
     */
    @Override
    public void updatePostImages(Long postId, List<String> imageUrls) {
        // 更新操作与设置操作相同，直接覆盖
        setPostImages(postId, imageUrls);
    }

    /**
     * 删除帖子所有图片缓存（被PostServiceImpl调用）
     * 业务逻辑：postId为null直接返回 → 删除列表缓存key → 通过pattern匹配删除所有单张图片缓存key
     * 异常场景：Redis异常仅记录日志
     *
     * @param postId 帖子ID（必填）
     */
    @Override
    public void deletePostImages(Long postId) {
        if (postId == null) {
            return;
        }

        try {
            // 删除列表缓存
            String listCacheKey = buildPostImageListKey(postId);
            redisContext.delete(listCacheKey);
            
            // 删除所有单张图片缓存（通过匹配模式删除）
            String pattern = buildPostImageKey(postId, "*");
            redisContext.deletePattern(pattern);
            
            log.debug("帖子图片缓存删除成功，postId: {}", postId);
        } catch (Exception e) {
            // Redis异常不影响业务，仅记录日志
            log.warn("帖子图片缓存删除失败，postId: {}", postId, e);
        }
    }

    /**
     * 获取帖子单张图片URL（被PostServiceImpl等调用）
     * 业务逻辑：校验postId和imgIndex → 先查单张图片Redis缓存 → 命中有效值返回 → 未命中则降级查整个列表缓存 → 按索引取对应URL
     * 异常场景：postId为null或imgIndex<0返回null；索引越界返回null
     *
     * @param postId   帖子ID（必填）
     * @param imgIndex 图片索引（必填，从0开始）
     * @return 单张图片URL，索引越界或无数据返回null
     */
    @Override
    public String getPostImage(Long postId, int imgIndex) {
        if (postId == null || imgIndex < 0) {
            return null;
        }

        String cacheKey = buildPostImageKey(postId, imgIndex);
        
        // 先查单张图片缓存
        String cachedUrl = getFromCache(cacheKey);
        if (cachedUrl != null && !NULL_VALUE_MARKER.equals(cachedUrl)) {
            return cachedUrl;
        }

        // 未命中则获取整个帖子图片列表
        List<String> images = getPostImages(postId);
        if (imgIndex < images.size()) {
            return images.get(imgIndex);
        }

        return null;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 构建头像缓存Key
     * 格式：Campus-Crush:user:avatar:{userId}
     */
    private String buildAvatarKey(Long userId) {
        return RedisConstant.USER_AVATAR_KEY_PREFIX + userId;
    }

    /**
     * 构建帖子图片列表缓存Key
     * 格式：Campus-Crush:post:imgList:{postId}
     */
    private String buildPostImageListKey(Long postId) {
        return RedisConstant.POST_IMAGE_LIST_KEY_PREFIX + postId;
    }

    /**
     * 构建单张帖子图片缓存Key
     * 格式：Campus-Crush:post:img:{postId}:{imgIndex}
     */
    private String buildPostImageKey(Long postId, int imgIndex) {
        return RedisConstant.POST_IMAGE_KEY_PREFIX + postId + ":" + imgIndex;
    }

    /**
     * 构建单张帖子图片缓存Key（用于匹配模式）
     */
    private String buildPostImageKey(Long postId, String pattern) {
        return RedisConstant.POST_IMAGE_KEY_PREFIX + postId + ":" + pattern;
    }

    /**
     * 从缓存获取字符串值
     */
    private String getFromCache(String key) {
        try {
            Object value = redisContext.get(key);
            return value != null ? String.valueOf(value) : null;
        } catch (Exception e) {
            log.warn("从Redis获取值失败，key: {}", key, e);
            return null;
        }
    }

    /**
     * 写入缓存字符串值
     */
    private void setToCache(String key, String value, long ttlSeconds) {
        try {
            redisContext.set(key, value, ttlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("写入Redis失败，key: {}", key, e);
        }
    }

    /**
     * 从缓存获取帖子图片列表
     */
    @SuppressWarnings("unchecked")
    private List<String> getPostImagesFromCache(String key) {
        try {
            Object value = redisContext.get(key);
            if (value == null) {
                return null;
            }
            
            // 尝试解析为List<String>
            if (value instanceof List) {
                return (List<String>) value;
            } else if (value instanceof String) {
                // 如果是JSON字符串，解析为List
                return objectMapper.readValue((String) value, 
                    new TypeReference<List<String>>() {});
            }
            return null;
        } catch (JsonProcessingException e) {
            log.warn("解析帖子图片缓存失败，key: {}", key, e);
            return null;
        } catch (Exception e) {
            log.warn("从Redis获取帖子图片列表失败，key: {}", key, e);
            return null;
        }
    }

    /**
     * 写入帖子图片列表到缓存
     */
    private void setPostImagesToCache(String key, List<String> imageUrls, long ttlSeconds) {
        try {
            // 使用JSON序列化存储列表
            String jsonValue = objectMapper.writeValueAsString(imageUrls);
            redisContext.set(key, jsonValue, ttlSeconds, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            log.warn("序列化帖子图片列表失败，key: {}", key, e);
        } catch (Exception e) {
            log.warn("写入Redis帖子图片列表失败，key: {}", key, e);
        }
    }

    /**
     * 解析Post的images字段（JSON数组格式）
     */
    private List<String> parseImagesJson(String imagesJson) {
        if (imagesJson == null || imagesJson.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        try {
            return objectMapper.readValue(imagesJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.warn("解析图片JSON失败: {}", imagesJson, e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取随机TTL偏移值（±5分钟）
     * 用于防止缓存雪崩
     */
    private long getRandomOffset() {
        // 生成 [-TTL_RANDOM_OFFSET_SECONDS, TTL_RANDOM_OFFSET_SECONDS] 范围内的随机数
        long offset = (long) (Math.random() * 2 * RedisConstant.TTL_RANDOM_OFFSET_SECONDS);
        return offset - RedisConstant.TTL_RANDOM_OFFSET_SECONDS;
    }
}