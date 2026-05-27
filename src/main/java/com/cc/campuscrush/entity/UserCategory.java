package com.cc.campuscrush.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * UserCategory实体类
 * &lt;p&gt;核心功能：用户兴趣分类偏好记录，存储用户选择的兴趣类别列表&lt;/p&gt;
 * &lt;p&gt;使用场景：用户首次登录选择兴趣标签后保存偏好，用于个性化内容推荐，被UserCategoryController、UserCategoryService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class UserCategory {
    private Long id;
    private Long userId;
    private String categories;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}