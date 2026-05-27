package com.cc.campuscrush.entity;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * LoveCheckin实体类
 * &lt;p&gt;核心功能：情侣打卡帖子存储，支持图文内容发布、点赞和评论计数&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣用户在恋爱空间发布日常打卡动态，分享生活点滴，被LoveCheckinController、LoveCheckinService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class LoveCheckin {
    private Long id;
    private Long coupleId;
    private Long userId;
    private String nickname;
    private String avatar;
    private String content;

    @JsonRawValue
    private String images;

    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createTime;
}

