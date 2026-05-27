package com.cc.campuscrush.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Chat实体类
 * &lt;p&gt;核心功能：实时聊天消息存储，支持文本与图片消息类型及用户级软删除&lt;/p&gt;
 * &lt;p&gt;使用场景：用户间一对一私聊，被ChatController、ChatService、ChatWebSocketHandler调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class Chat {
    private Long id;
    private Long fromId;
    private Long toId;
    private String content;
    private LocalDateTime createTime;
    private Integer isRead;
    private Integer msgType;
    private String avatar;
    // 用户级删除标记（0-未删除，1-已删除）
    private Integer deletedByFrom;
    private Integer deletedByTo;
}
