package com.cc.campuscrush.service;

import java.util.List;

/**
 * 【ImageCacheService】服务层接口
 * &lt;p&gt;核心功能：提供用户头像和帖子图片的Redis缓存管理，支持读时回写和写时同步更新策略&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于需要高性能图片访问的社交场景，被UserServiceImpl和PostServiceImpl等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface ImageCacheService {

    // ==================== 头像缓存操作 ====================

    /**
     * 获取用户头像URL（带缓存）
     * 业务逻辑：先查Redis缓存 → 未命中则查MySQL数据库 → 回写Redis缓存 → 返回头像URL
     * 异常场景：头像不存在（用户未上传头像或用户不存在）时返回null
     *
     * @param userId 用户ID（必填）
     * @return 头像URL，无数据时返回null
     */
    String getAvatar(Long userId);

    /**
     * 设置用户头像URL（写时缓存）
     * 业务逻辑：更新MySQL中的头像URL → 同步写入Redis缓存
     * 异常场景：用户不存在时操作无效
     *
     * @param userId    用户ID（必填）
     * @param avatarUrl 头像URL（可为空，表示清除头像）
     */
    void setAvatar(Long userId, String avatarUrl);

    /**
     * 删除用户头像缓存
     * 业务逻辑：删除MySQL中的头像记录 → 同步删除Redis缓存Key
     * 异常场景：用户无头像缓存时Redis删除操作无影响
     *
     * @param userId 用户ID（必填）
     */
    void deleteAvatar(Long userId);

    // ==================== 帖子图片缓存操作 ====================

    /**
     * 获取帖子图片URL列表（带缓存）
     * 业务逻辑：先查Redis缓存 → 未命中则查MySQL数据库 → 批量写入Redis缓存 → 返回图片URL列表
     * 异常场景：帖子不存在或无图片时返回空列表
     *
     * @param postId 帖子ID（必填）
     * @return 图片URL列表，无数据时返回空列表
     */
    List<String> getPostImages(Long postId);

    /**
     * 设置帖子图片URL列表（懒加载 / Cache-Aside）
     * 业务逻辑：读取帖子时 → 将图片URL列表写入Redis缓存
     * 异常场景：帖子不存在时操作无效
     *
     * @param postId    帖子ID（必填）
     * @param imageUrls 图片URL列表（可为空列表）
     */
    void setPostImages(Long postId, List<String> imageUrls);

    /**
     * 更新帖子图片URL列表
     * 业务逻辑：更新MySQL中的帖子图片 → 同步更新Redis缓存中的图片列表
     * 异常场景：帖子不存在时操作无效
     *
     * @param postId    帖子ID（必填）
     * @param imageUrls 新的图片URL列表（可为空列表，表示清空图片）
     */
    void updatePostImages(Long postId, List<String> imageUrls);

    /**
     * 删除帖子图片缓存
     * 业务逻辑：删除MySQL中的帖子图片记录 → 同步删除Redis缓存Key
     * 异常场景：帖子无图片缓存时Redis删除操作无影响
     *
     * @param postId 帖子ID（必填）
     */
    void deletePostImages(Long postId);

    /**
     * 获取帖子中的单张图片URL
     * 业务逻辑：获取帖子全部图片列表 → 从缓存或数据库中按索引取出指定图片URL
     * 异常场景：索引越界或帖子无图片时返回null
     *
     * @param postId   帖子ID（必填）
     * @param imgIndex 图片索引（必填，从0开始）
     * @return 指定索引的图片URL，不存在时返回null
     */
    String getPostImage(Long postId, int imgIndex);
}
