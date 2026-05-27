package com.cc.campuscrush.websocket;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * 【WebSocketHandshakeInterceptor】WebSocket处理器
 * &lt;p&gt;核心功能：WebSocket握手拦截器，在beforeHandshake阶段从HTTP请求参数中提取userId并存入WebSocket会话属性Map&lt;/p&gt;
 * &lt;p&gt;使用场景：在WebSocket连接建立前拦截握手请求，提取用户身份信息并写入session attributes供后续ChatWebSocketHandler和WhisperWebSocketHandler识别消息发送者&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * WebSocket握手前拦截——提取用户身份信息
     * 业务逻辑：
     * 1. 判断request是否为ServletServerHttpRequest类型（非Servlet请求不做处理）
     * 2. 从HTTP请求参数中提取"userId"
     * 3. userId存在 → 解析为Long并存入WebSocket会话attributes，供后续ChatWebSocketHandler和WhisperWebSocketHandler识别消息发送者
     * 4. 始终返回true允许握手继续，userId缺失时仅跳过不阻止连接（后续消息处理中会校验fromUserId）
     *
     * @param request    WebSocket握手请求
     * @param response   WebSocket握手响应
     * @param wsHandler  目标WebSocket处理器
     * @param attributes WebSocket会话属性Map（用于传递userId）
     * @return true继续握手，暂不返回false
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                    WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest req = servletRequest.getServletRequest();
            String userId = req.getParameter("userId");
            if (userId != null) {
                attributes.put("userId", Long.parseLong(userId));
            }
        }
        return true;
    }

    /**
     * WebSocket握手后回调
     * 业务逻辑：当前为空实现，保留后续扩展点（如记录连接日志、统计在线人数等）
     *
     * @param request   WebSocket握手请求
     * @param response  WebSocket握手响应
     * @param wsHandler 目标WebSocket处理器
     * @param exception 握手过程中发生的异常，正常为null
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                WebSocketHandler wsHandler, Exception exception) {
    }
}
