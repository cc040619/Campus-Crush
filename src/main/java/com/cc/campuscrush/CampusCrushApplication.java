package com.cc.campuscrush;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 【CampusCrushApplication】应用启动类
 * &lt;p&gt;核心功能：Spring Boot应用主启动类，自动配置并启动校园速配平台后端服务&lt;/p&gt;
 * &lt;p&gt;使用场景：应用部署的入口点，由SpringApplication.run启动整个Spring容器上下文，扫描MyBatis Mapper接口&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@SpringBootApplication
@MapperScan("com.cc.campuscrush.mapper")
public class CampusCrushApplication {

    /**
     * 应用启动入口
     * 启动Spring Boot容器，自动扫描组件、配置类、Mapper接口，初始化数据库连接池、Redis、WebSocket等基础设施
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(CampusCrushApplication.class, args);
    }

}
