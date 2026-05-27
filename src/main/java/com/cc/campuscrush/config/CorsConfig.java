package com.cc.campuscrush.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 【CorsConfig】配置类
 * &lt;p&gt;核心功能：跨域CORS过滤器配置，允许所有来源、所有请求头和所有请求方法，支持携带凭证（Cookie）&lt;/p&gt;
 * &lt;p&gt;使用场景：解决前后端分离部署时的浏览器跨域限制问题，前端开发环境可独立访问后端API接口，过滤器应用于所有路径&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Configuration
public class CorsConfig {

    /**
     * 创建CORS跨域过滤器
     * 配置：允许所有来源（OriginPattern）、所有请求头、所有HTTP方法，支持Cookie携带（AllowCredentials=true），预检缓存3600秒
     * 拦截范围：所有路径（/**），解决前后端分离部署时的浏览器跨域限制
     *
     * @return 全局CORS过滤器实例
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}