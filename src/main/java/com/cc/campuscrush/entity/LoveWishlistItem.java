package com.cc.campuscrush.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * LoveWishlistItem实体类
 * &lt;p&gt;核心功能：情侣愿望清单条目存储，包含标题、描述和完成状态&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣用户共同创建和管理愿望清单/bucket list，标记已完成项目，被LoveWishlistController、LoveWishlistService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class LoveWishlistItem {
    private Long id;
    private Long coupleId;
    private Long userId;
    private String title;
    private String description;
    private Boolean completed;
    private LocalDateTime createTime;
}
