package com.cc.campuscrush.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * LoveDiary实体类
 * &lt;p&gt;核心功能：恋爱日记条目存储，支持标题、正文、心情标签和图片&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣用户记录恋爱中的心情和重要时刻，被LoveDiaryController、LoveDiaryService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class LoveDiary {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String mood;
    private String image;
    private LocalDateTime createTime;
}
