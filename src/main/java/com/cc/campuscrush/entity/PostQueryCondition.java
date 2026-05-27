package com.cc.campuscrush.entity;

import lombok.Data;

/**
 * PostQueryCondition实体类
 * &lt;p&gt;核心功能：帖子查询过滤条件DTO，支持关键字、分类筛选及当前用户上下文&lt;/p&gt;
 * &lt;p&gt;使用场景：社区帖子列表检索时传入查询参数，被PostService、PostMapper的查询方法调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class PostQueryCondition {
    private String keyword;
    private String category;
    private Long currentUserId;
}
