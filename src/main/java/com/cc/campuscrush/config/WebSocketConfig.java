package com.cc.campuscrush.config;

import com.cc.campuscrush.websocket.ChatWebSocketHandler;
import com.cc.campuscrush.websocket.WhisperWebSocketHandler;
import com.cc.campuscrush.websocket.WebSocketHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 【WebSocketConfig】配置类
 * &lt;p&gt;核心功能：WebSocket端点注册配置，将ChatWebSocketHandler绑定到/ws/chat路径（好友聊天）和WhisperWebSocketHandler绑定到/ws/whisper路径（情侣悄悄话），并注入握手拦截器&lt;/p&gt;
 * &lt;p&gt;使用场景：初始化WebSocket通道，前端通过这两个WebSocket路径建立长连接，握手时由WebSocketHandshakeInterceptor提取用户身份信息&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatHandler;
    private final WhisperWebSocketHandler whisperHandler;
    private final WebSocketHandshakeInterceptor handshakeInterceptor;

    public WebSocketConfig(ChatWebSocketHandler chatHandler,
                           WhisperWebSocketHandler whisperHandler,
                           WebSocketHandshakeInterceptor handshakeInterceptor) {
        this.chatHandler = chatHandler;
        this.whisperHandler = whisperHandler;
        this.handshakeInterceptor = handshakeInterceptor;
    }

    /**
     * 注册WebSocket处理器端点
     * 注册规则：
     * 1. /ws/chat 端点绑定 ChatWebSocketHandler（好友聊天，消息持久化到t_chat表）
     * 2. /ws/whisper 端点绑定 WhisperWebSocketHandler（情侣悄悄话，消息双写到love_whisper表和Redis）
     * 3. 两个端点均注入WebSocketHandshakeInterceptor握手拦截器（提取userId身份信息）
     * 4. 允许所有来源（setAllowedOrigins("*")），跨域WebSocket连接
     *
     * @param registry WebSocket处理器注册表（Spring自动注入）
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 好友聊天 WebSocket（t_chat 表）
        registry.addHandler(chatHandler, "/ws/chat")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("*");
        // 悄悄话 WebSocket（love_whisper 表）
        registry.addHandler(whisperHandler, "/ws/whisper")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("*");
    }
}
