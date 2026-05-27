package com.cc.campuscrush.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * SysUser实体类
 * &lt;p&gt;核心功能：系统用户核心实体，包含账号认证信息、个人资料、登录安全控制及聊天摘要数据&lt;/p&gt;
 * &lt;p&gt;使用场景：用户注册登录、个人信息管理、好友聊天列表展示等核心业务，被SysUserController、SysUserService、AuthService等多个模块调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class SysUser {
    private Long id;
    private String username;
    /** WRITE_ONLY: 反序列化时接受密码（登录/注册），序列化时不输出密码到 JSON */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String phone;
    private String email;
    private Integer loginFailCount;
    private java.time.LocalDateTime loginFreezeUntil;
    private String nickname;
    private String avatar;
    private Integer gender; // 0-未知 1-男 2-女
    private String intro;
    private LocalDateTime createTime;
    // 聊天相关字段
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
    private Boolean top;
}
