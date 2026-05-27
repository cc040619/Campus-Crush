package com.cc.campuscrush.entity;

import java.time.LocalDateTime;

/**
 * InteractionNotice实体类
 * &lt;p&gt;核心功能：互动通知消息存储，记录点赞、评论等社交互动行为及已读状态&lt;/p&gt;
 * &lt;p&gt;使用场景：用户收到点赞或评论通知时展示在消息中心，被InteractionNoticeService调用；该实体采用传统POJO风格手动编写getter/setter&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public class InteractionNotice {

    private Long id;
    private Long userId;
    private Long fromUserId;
    private String fromUserNickname;
    private String fromUserAvatar;
    private Long postId;
    private String postTitle;
    private Integer type;
    private LocalDateTime createTime;
    private Integer isRead;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserNickname() {
        return fromUserNickname;
    }

    public void setFromUserNickname(String fromUserNickname) {
        this.fromUserNickname = fromUserNickname;
    }

    public String getFromUserAvatar() {
        return fromUserAvatar;
    }

    public void setFromUserAvatar(String fromUserAvatar) {
        this.fromUserAvatar = fromUserAvatar;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }
}