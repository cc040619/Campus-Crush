package com.cc.campuscrush.websocket;

import com.cc.campuscrush.entity.Chat;
import com.cc.campuscrush.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 【ChatWebSocketHandler】WebSocket处理器
 * &lt;p&gt;核心功能：好友聊天WebSocket处理器，基于ConcurrentHashMap管理在线用户会话连接，接收文本消息后持久化到t_chat表并实时转发给目标用户&lt;/p&gt;
 * &lt;p&gt;使用场景：处理社区好友聊天消息的实时收发，前端通过/ws/chat建立长连接，连接建立时注册用户会话，消息到达时存入数据库并推送给在线接收者&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final ChatService chatService;

    public ChatWebSocketHandler(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * WebSocket连接建立成功回调
     * 业务逻辑：从会话attributes中提取userId → userId非空时注册到sessions ConcurrentHashMap（key=userId, value=WebSocketSession）
     * 异常场景：userId为null时不注册（握手拦截器未提取到身份信息），静默跳过
     *
     * @param session WebSocket会话对象
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            sessions.put(userId, session);
        }
    }

    /**
     * 处理接收到的文本消息
     * 业务逻辑：
     * 1. 从会话attributes获取发送者userId，为空直接返回
     * 2. 解析JSON消息体提取toId（接收者ID）和content（消息内容）
     * 3. 调用ChatService.sendMessage()将消息持久化到t_chat表
     * 4. 从sessions查找接收者会话 → 在线且连接打开时实时转发消息（含type/message, fromId, content, time）
     * 异常场景：fromUserId为null时静默丢弃消息；接收者不在线时仅存库不推送
     *
     * @param session WebSocket会话对象
     * @param message 文本消息对象
     * @throws IOException JSON解析失败或消息发送失败时抛出
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Long fromUserId = (Long) session.getAttributes().get("userId");
        if (fromUserId == null) return;

        Map<String, Object> msgData = objectMapper.readValue(message.getPayload(), Map.class);
        Long toId = Long.valueOf(msgData.get("toId").toString());
        String content = (String) msgData.get("content");

        // 保存到数据库
        chatService.sendMessage(fromUserId, toId, content);

        // 转发给接收者（如果在线）
        WebSocketSession targetSession = sessions.get(toId);
        if (targetSession != null && targetSession.isOpen()) {
            Map<String, Object> response = new java.util.LinkedHashMap<>();
            response.put("type", "message");
            response.put("fromId", fromUserId);
            response.put("content", content);
            response.put("time", java.time.LocalDateTime.now().toString());
            targetSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        }
    }

    /**
     * WebSocket连接关闭回调
     * 业务逻辑：从会话attributes提取userId → 从sessions Map中移除该用户会话
     * 异常场景：userId为null时静默跳过
     *
     * @param session WebSocket会话对象
     * @param status  关闭状态码和原因
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            sessions.remove(userId);
        }
    }

    /**
     * 查询用户是否在线
     * 业务逻辑：从sessions查找用户WebSocketSession → 判断会话存在且连接已打开
     * 异常场景：用户未注册会话或连接已关闭返回false
     *
     * @param userId 用户ID
     * @return true在线，false离线
     */
    public static boolean isUserOnline(Long userId) {
        WebSocketSession session = sessions.get(userId);
        return session != null && session.isOpen();
    }
}
