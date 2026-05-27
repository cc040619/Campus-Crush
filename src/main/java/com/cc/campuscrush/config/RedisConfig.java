package com.cc.campuscrush.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 【RedisConfig】配置类
 * &lt;p&gt;核心功能：RedisTemplate序列化配置，使用Jackson2JsonRedisSerializer作为值序列化器并注册JavaTimeModule支持Java 8日期序列化&lt;/p&gt;
 * &lt;p&gt;使用场景：配置Redis的Key（字符串）和Value（JSON）序列化方式，确保Redis缓存数据的正确存取，被RedisContext工具类依赖注入使用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Configuration
public class RedisConfig {

    /**
     * 创建RedisTemplate Bean（主实例）
     * 配置：Key使用StringRedisSerializer字符串序列化，Value使用Jackson2JsonRedisSerializer JSON序列化（含JavaTimeModule支持LocalDateTime等日期类型），Hash Key/Value同理
     * 用途：全局默认RedisTemplate，被RedisContext等工具类注入使用，确保Redis中存储的数据可读性强
     *
     * @param factory Redis连接工厂（Spring自动注入）
     * @return 配置好序列化器的RedisTemplate实例
     */
    @Bean
    @Primary
    @SuppressWarnings("deprecation")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        
        // 创建ObjectMapper并配置Java 8日期时间支持
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // 创建Jackson序列化器
        Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jsonSerializer.setObjectMapper(objectMapper);
        
        // 配置序列化器
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jsonSerializer);
        
        return template;
    }
}