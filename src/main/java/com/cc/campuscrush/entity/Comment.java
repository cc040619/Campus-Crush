package com.cc.campuscrush.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Comment实体类
 * &lt;p&gt;核心功能：帖子评论存储，支持一级评论与二级回复（通过parentId实现嵌套）&lt;/p&gt;
 * &lt;p&gt;使用场景：用户在帖子详情页发表评论或回复他人评论，被CommentController、CommentService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class Comment {
    private Long id;
    private Long postId;
    private Long userId;
    private Long parentId;
    private String content;
    private Integer likeCount;
    private LocalDateTime createTime;
}
