package com.cc.campuscrush.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Post实体类
 * &lt;p&gt;核心功能：社区帖子存储，支持图文内容、分类标签、位置标记、可见性控制和浏览量统计&lt;/p&gt;
 * &lt;p&gt;使用场景：用户在社区发布帖子、浏览信息流、搜索内容，是社区模块的核心实体，被PostController、PostService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class Post {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String images;
    private String categories;
    private String location;
    private Integer visibility;
    private Integer likeCount;
    private Integer collectCount;
    private Integer browseCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
