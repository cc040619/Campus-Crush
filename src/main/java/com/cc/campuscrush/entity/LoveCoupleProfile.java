package com.cc.campuscrush.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * LoveCoupleProfile实体类
 * &lt;p&gt;核心功能：情侣档案存储，关联双方用户信息、头像和恋爱开始日期&lt;/p&gt;
 * &lt;p&gt;使用场景：建立情侣关系后生成共享档案，展示双方信息，被LoveCoupleController、LoveCoupleService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class LoveCoupleProfile {
    private Long id;
    private Long coupleId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long partnerId;
    private String partnerName;
    private String partnerAvatar;
    private LocalDate startDate;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
