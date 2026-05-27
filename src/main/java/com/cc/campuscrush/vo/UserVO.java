package com.cc.campuscrush.vo;

import lombok.Data;

/**
 * 【UserVO】视图对象
 * &lt;p&gt;核心功能：用户信息视图对象，封装前端展示所需的用户基本信息（id/用户名/昵称/头像/手机/邮箱/性别/简介）、社交统计（帖子/粉丝/关注/获赞收藏数）和关注状态，已做脱敏处理不包含密码字段&lt;/p&gt;
 * &lt;p&gt;使用场景：用于前端用户主页、个人中心、用户列表等页面的数据展示，由UserService查询组装后返回，确保敏感信息不会泄露到前端&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private Integer gender; // 0-未知 1-男 2-女
    private String intro;
    private int postCount;
    private int followerCount;
    private int followingCount;
    private int likeAndCollectCount;
    private boolean isFollowing;
}
