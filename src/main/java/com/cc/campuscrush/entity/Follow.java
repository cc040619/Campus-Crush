package com.cc.campuscrush.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Follow实体类
 * &lt;p&gt;核心功能：记录用户之间的关注关系，关注者与被关注者多对多关联&lt;/p&gt;
 * &lt;p&gt;使用场景：用户关注感兴趣的其他用户，构建社交关系链，被FollowController、FollowService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class Follow {
    private Long id;
    private Long followerId;
    private Long followingId;
    private LocalDateTime createTime;
}
