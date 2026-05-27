package com.cc.campuscrush.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Collect实体类
 * &lt;p&gt;核心功能：记录用户收藏帖子的关联关系&lt;/p&gt;
 * &lt;p&gt;使用场景：用户在社区浏览帖子时收藏感兴趣的内容，被CollectController、CollectService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class Collect {
    private Long id;
    private Long postId;
    private Long userId;
    private LocalDateTime createTime;
}
