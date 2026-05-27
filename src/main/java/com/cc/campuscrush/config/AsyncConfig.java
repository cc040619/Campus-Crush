package com.cc.campuscrush.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 【AsyncConfig】配置类
 * &lt;p&gt;核心功能：异步任务线程池配置，定义taskExecutor（8线程，CPU密集型）和ioExecutor（16线程，IO密集型）两个线程池&lt;/p&gt;
 * &lt;p&gt;使用场景：为@Async标注的方法提供线程池支持，CPU密集型任务如计算处理使用taskExecutor，IO密集型任务如文件上传、网络请求使用ioExecutor&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 创建CPU密集型任务线程池
     * 配置：固定8线程，守护线程模式，线程名前缀"async-task-"
     * 适用场景：计算密集型任务（如数据处理、编解码），线程数=CPU核心数
     *
     * @return 固定大小为8的守护线程池
     */
    @Bean(name = "taskExecutor")
    public ExecutorService taskExecutor() {
        return Executors.newFixedThreadPool(8, r -> {
            Thread thread = new Thread(r);
            thread.setName("async-task-");
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * 创建IO密集型任务线程池
     * 配置：固定16线程，守护线程模式，线程名前缀"io-task-"
     * 适用场景：文件上传、网络请求等IO阻塞型任务，线程数=CPU核心数*2
     *
     * @return 固定大小为16的守护线程池
     */
    @Bean(name = "ioExecutor")
    public ExecutorService ioExecutor() {
        return Executors.newFixedThreadPool(16, r -> {
            Thread thread = new Thread(r);
            thread.setName("io-task-");
            thread.setDaemon(true);
            return thread;
        });
    }
}