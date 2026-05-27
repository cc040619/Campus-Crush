package com.cc.campuscrush.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Friend实体类
 * &lt;p&gt;核心功能：好友关系记录，包含好友状态（待接受/已添加/已拒绝等）&lt;/p&gt;
 * &lt;p&gt;使用场景：用户添加好友、管理好友列表，构建好友社交网络，被FriendController、FriendService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class Friend {
    private Long id;
    private Long userId;
    private Long friendId;
    private String friendNickname;
    private Integer status;
    private LocalDateTime createTime;
}
