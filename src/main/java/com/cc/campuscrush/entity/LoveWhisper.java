package com.cc.campuscrush.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * LoveWhisper实体类
 * &lt;p&gt;核心功能：情侣私语消息存储，支持文本和图片两种消息类型及已读状态&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣用户间发送私密消息，构建二人专属聊天空间，被LoveWhisperController、LoveWhisperService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class LoveWhisper {
    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private String content;
    private Integer msgType; // 1-文本, 2-图片
    private Integer isRead;  // 0-未读, 1-已读
    private LocalDateTime createTime;
}
