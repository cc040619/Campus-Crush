package com.cc.campuscrush.config;

import com.cc.campuscrush.interceptor.UserLoginInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * 【WebMvcConfiguration】配置类
 * &lt;p&gt;核心功能：Spring MVC配置类，注册UserLoginInterceptor拦截器到/api/**路径（排除登录/注册等公开接口），并配置ObjectMapper序列化器支持Java 8日期时间&lt;/p&gt;
 * &lt;p&gt;使用场景：扩展WebMvcConfigurationSupport以自定义拦截器链，控制API访问权限——所有/api/**请求必须携带有效JWT Token（登录注册等公开端点除外），ObjectMapper供全局JSON序列化使用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private UserLoginInterceptor userLoginInterceptor;

    /**
     * 创建ObjectMapper Bean
     * 配置：注册JavaTimeModule模块，支持LocalDate、LocalDateTime等Java 8日期时间类型的JSON序列化和反序列化
     *
     * @return 支持Java 8日期时间的ObjectMapper实例
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 添加Java 8日期时间支持
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        return mapper;
    }

    /**
     * 注册用户登录拦截器
     * 拦截规则：拦截所有/api/**路径请求，排除登录、注册、验证码获取等公开接口（/api/user/login、/api/user/register、/api/user/send-login-code、/api/user/login-by-code、/api/community/user/**、/api/community/post/*）
     * 异常场景：拦截器中Token验证失败时抛出TokenInvalidException或TokenExpiredException，由GlobalExceptionHandler统一处理
     *
     * @param registry 拦截器注册表（Spring自动注入）
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(userLoginInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/user/login")
                .excludePathPatterns("/api/user/register")
                .excludePathPatterns("/api/user/send-login-code")
                .excludePathPatterns("/api/user/login-by-code")
                .excludePathPatterns("/api/community/user/**")
                .excludePathPatterns("/api/community/post/*");
    }
}

