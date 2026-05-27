package com.cc.campuscrush.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 【RateLimiterUtil】工具类
 * &lt;p&gt;核心功能：基于Redis INCR命令的简易滑动窗口限流器，提供isAllowed方法判断请求是否超过阈值（首次访问自动设置Key过期时间），getRemainingTTL方法查询剩余冷却秒数&lt;/p&gt;
 * &lt;p&gt;使用场景：配合RedisConstant中的限流常量，用于登录、注册、发送验证码等高频接口的防刷保护，达到阈值后返回false并由调用方返回限流提示&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Component
@Slf4j
public class RateLimiterUtil {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 滑动窗口限流检查（基于Redis INCR命令）
     * 业务逻辑：
     * 1. Redis原子自增（INCR）指定Key
     * 2. 首次访问（count=1）时设置Key的TTL过期时间windowSeconds，形成滑动窗口
     * 3. 计数超过maxRequests阈值 → 记录warn日志 → 返回false（被限流）
     * 4. 未超阈值 → 返回true（放行）
     * 异常场景：Redis连接异常时count为null，返回true（降级放行，防止误拦所有请求）
     *
     * @param key           限流Key（必填，如"Campus-Crush:ratelimit:login:192.168.1.1"）
     * @param maxRequests   时间窗口内允许的最大请求次数（必填）
     * @param windowSeconds 时间窗口大小，单位秒（必填，仅首次请求时设置TTL）
     * @return true允许通过，false触发限流
     */
    public boolean isAllowed(String key, long maxRequests, long windowSeconds) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count == null) {
            return true;
        }
        // 首次请求设置过期时间
        if (count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
        }
        if (count > maxRequests) {
            log.warn("限流触发: key={}, count={}, max={}", key, count, maxRequests);
            return false;
        }
        return true;
    }

    /**
     * 查询限流Key的剩余冷却时间
     * 业务逻辑：获取Redis Key的剩余TTL秒数 → 有效值直接返回，null/负数返回0
     * 用途：告知用户还需等待多少秒后才能再次请求
     *
     * @param key 限流Key（必填）
     * @return 剩余冷却秒数，Key不存在或已过期返回0
     */
    public long getRemainingTTL(String key) {
        Long ttl = redisTemplate.getExpire(key);
        return ttl != null && ttl > 0 ? ttl : 0;
    }
}
