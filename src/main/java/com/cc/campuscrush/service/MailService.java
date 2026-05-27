package com.cc.campuscrush.service;

/**
 * 【MailService】服务层接口
 * &lt;p&gt;核心功能：提供邮箱验证码发送功能，支持用户注册、登录和绑定等场景的邮箱验证&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于用户身份验证和账号安全场景，被UserServiceImpl等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface MailService {

    /**
     * 发送邮箱验证码
     * 业务逻辑：生成邮件内容（含6位验证码） → 通过SMTP发送邮件到目标邮箱 → 记录发送日志
     * 异常场景：邮箱地址格式无效时静默失败；邮件服务不可用时发送失败
     *
     * @param toEmail 接收方邮箱地址（必填，需为有效邮箱格式）
     * @param code    6位数字验证码（必填）
     */
    void sendVerificationCode(String toEmail, String code);
}
