package com.cc.campuscrush.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Like实体类
 * &lt;p&gt;核心功能：点赞记录存储，通过type字段区分点赞对象（帖子或评论）&lt;/p&gt;
 * &lt;p&gt;使用场景：用户对帖子或评论点赞/取消点赞，被LikeController、LikeService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class Like {
    private Long id;
    private Long postId;
    private Long commentId;
    private Long userId;
    private Integer type;
    private LocalDateTime createTime;
}
