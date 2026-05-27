package com.cc.campuscrush.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * LoveCheckinLike实体类
 * &lt;p&gt;核心功能：情侣打卡帖子的点赞记录&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣用户对打卡动态点赞互动，被LoveCheckinLikeService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class LoveCheckinLike {
    private Long id;
    private Long recordId;
    private Long userId;
    private LocalDateTime createTime;
}
