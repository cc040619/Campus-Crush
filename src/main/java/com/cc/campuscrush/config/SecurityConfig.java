package com.cc.campuscrush.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 【SecurityConfig】配置类
 * &lt;p&gt;核心功能：Spring Security安全配置，禁用CSRF防护并设置无状态会话管理（STATELESS），提供BCryptPasswordEncoder密码加密器&lt;/p&gt;
 * &lt;p&gt;使用场景：控制API访问安全策略，当前配置允许所有请求通过（permitAll），JWT认证由UserLoginInterceptor拦截器独立处理，BCryptPasswordEncoder供用户注册和登录Service使用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 创建BCrypt密码编码器
     * 用途：对用户密码进行单向哈希加密，供注册和登录Service使用
     *
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置Spring Security过滤链
     * 配置：禁用CSRF防护（前后端分离无需CSRF Token），设置无状态会话策略（STATELESS，JWT认证无需服务端Session），允许所有请求通过（permitAll）
     * 说明：JWT鉴权由UserLoginInterceptor独立处理，Security只负责基础安全配置
     *
     * @param http HttpSecurity构建器（Spring自动注入）
     * @return 配置好的SecurityFilterChain
     * @throws Exception 构建过程中可能抛出的异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        return http.build();
    }
}