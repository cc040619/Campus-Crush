package com.cc.campuscrush.common;

 /**
 * 【RedisConstant】通用类
 * &lt;p&gt;核心功能：Redis缓存常量配置，集中管理缓存Key前缀、TTL过期时间、随机偏移时间和限流参数，涵盖头像/帖子图片/评论/验证码/登录失败计数等多业务场景&lt;/p&gt;
 * &lt;p&gt;使用场景：被RateLimiterUtil限流工具、邮箱验证码Service、用户头像缓存Service等所有需要操作Redis缓存Key前缀和过期时间的模块引用，统一维护避免硬编码分散&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public class RedisConstant {

    // ==================== Key前缀 ====================
    
    // 头像缓存Key前缀：Campus-Crush:user:avatar:{userId}
    public static final String USER_AVATAR_KEY_PREFIX = "Campus-Crush:user:avatar:";
    
    // 帖子图片缓存Key前缀：Campus-Crush:post:img:{postId}:{imgIndex}
    public static final String POST_IMAGE_KEY_PREFIX = "Campus-Crush:post:img:";
    
    // 帖子图片列表缓存Key前缀：Campus-Crush:post:imgList:{postId}
    public static final String POST_IMAGE_LIST_KEY_PREFIX = "Campus-Crush:post:imgList:";
    
    // ==================== TTL配置（单位：秒） ====================
    
    // 头像过期时间：24小时（用户很少换头像）
    public static final long AVATAR_TTL_SECONDS = 24 * 60 * 60;
    
    // 帖子图片过期时间：12小时（读多写少，防一致性问题）
    public static final long POST_IMAGE_TTL_SECONDS = 12 * 60 * 60;
    
    // 空值缓存过期时间：5分钟（防止缓存穿透）
    public static final long NULL_VALUE_TTL_SECONDS = 5 * 60;
    
    // ==================== 随机偏移时间（单位：秒） ====================
    
    // TTL随机偏移范围：±5分钟，防止缓存雪崩
    public static final long TTL_RANDOM_OFFSET_SECONDS = 5 * 60;
    
    // ==================== 限流Key ====================

    // 注册限流Key前缀：Campus-Crush:ratelimit:register:{ip}
    public static final String RATELIMIT_REGISTER_KEY_PREFIX = "Campus-Crush:ratelimit:register:";
    // 注册限流窗口：1小时内最多3次
    public static final long RATELIMIT_REGISTER_MAX = 3;
    public static final long RATELIMIT_REGISTER_WINDOW_SECONDS = 3600;

    // 登录限流Key前缀：Campus-Crush:ratelimit:login:{ip}
    public static final String RATELIMIT_LOGIN_KEY_PREFIX = "Campus-Crush:ratelimit:login:";
    // 登录限流：每分钟最多10次
    public static final long RATELIMIT_LOGIN_MAX = 10;
    public static final long RATELIMIT_LOGIN_WINDOW_SECONDS = 60;

    // 发送验证码限流Key前缀（按IP）：Campus-Crush:ratelimit:sendcode:ip:{ip}
    public static final String RATELIMIT_SENDCODE_IP_KEY_PREFIX = "Campus-Crush:ratelimit:sendcode:ip:";
    // 发送验证码限流：每小时每IP最多5次
    public static final long RATELIMIT_SENDCODE_IP_MAX = 5;
    public static final long RATELIMIT_SENDCODE_IP_WINDOW_SECONDS = 3600;

    // 发送验证码限流Key前缀（按邮箱）：Campus-Crush:ratelimit:sendcode:email:{email}
    public static final String RATELIMIT_SENDCODE_EMAIL_KEY_PREFIX = "Campus-Crush:ratelimit:sendcode:email:";
    // 发送验证码限流：每60秒同一邮箱只能发1次
    public static final long RATELIMIT_SENDCODE_EMAIL_MAX = 1;
    public static final long RATELIMIT_SENDCODE_EMAIL_WINDOW_SECONDS = 60;

    // ==================== 邮箱验证码Key ====================

    // 邮箱验证码Key前缀：Campus-Crush:email:code:{email}
    public static final String EMAIL_CODE_KEY_PREFIX = "Campus-Crush:email:code:";
    // 绑定邮箱验证码Key前缀：Campus-Crush:bind:code:{email}
    public static final String BIND_CODE_KEY_PREFIX = "Campus-Crush:bind:code:";
    // 验证码有效期：5分钟
    public static final long EMAIL_CODE_TTL_SECONDS = 300;

    // ==================== 登录失败计数Key ====================

    // 登录失败计数Key前缀（Redis缓存，辅助快速判断）：Campus-Crush:login:fail:{username}
    public static final String LOGIN_FAIL_COUNT_KEY_PREFIX = "Campus-Crush:login:fail:";
    // 登录失败冻结时间：15分钟
    public static final long LOGIN_FREEZE_SECONDS = 900;
    // 登录失败最大次数
    public static final long LOGIN_FAIL_MAX = 5;

    // ==================== 评论缓存Key ====================
    public static final String COMMENT_LIST_KEY_PREFIX = "Campus-Crush:comment:list:";
    public static final String COMMENT_DETAIL_KEY_PREFIX = "Campus-Crush:comment:detail:";
    public static final long CACHE_TTL_MINUTES = 5;
    public static final long CACHE_NULL_TTL_MINUTES = 1;
}