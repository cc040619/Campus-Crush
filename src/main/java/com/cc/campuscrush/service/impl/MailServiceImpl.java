package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.service.MailService;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 【MailServiceImpl】邮件发送服务层实现
 * &lt;p&gt;核心功能：异步发送登录验证码等HTML格式邮件&lt;/p&gt;
 * &lt;p&gt;使用场景：用户登录验证码发送和邮箱绑定验证，被 UserServiceImpl 调用，采用 ioExecutor 线程池异步发送避免阻塞主流程，使用 MimeMessageHelper 构建 \"予你平安\" 品牌化 HTML 邮件模板&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 异步发送HTML格式登录/绑定验证码邮件（使用ioExecutor线程池，被UserServiceImpl调用）
     * 业务逻辑：构建MimeMessage → 设置发件人/收件人/主题"予你平安 - 登录验证码" → 生成品牌化HTML模板（渐变背景、验证码高亮展示） → 发送邮件
     * 异常场景：发送失败抛出RuntimeException("验证码发送失败，请稍后重试")
     *
     * @param toEmail 收件人邮箱（必填）
     * @param code    6位验证码（必填）
     * @throws RuntimeException 邮件发送失败时抛出
     */
    @Async("ioExecutor")
    @Override
    public void sendVerificationCode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject("予你平安 - 登录验证码");
            String content = buildEmailContent(code);
            helper.setText(content, true);
            mailSender.send(message);
            log.info("验证码已发送至 {}", toEmail);
        } catch (MessagingException e) {
            log.error("发送验证码至 {} 失败: {}", toEmail, e.getMessage());
            throw new RuntimeException("验证码发送失败，请稍后重试");
        }
    }

    private String buildEmailContent(String code) {
        return """
            <div style="max-width:600px;margin:0 auto;padding:30px;font-family:Arial,sans-serif;
                        background:linear-gradient(135deg,#fff5f5,#fff0f5);border-radius:16px;">
                <div style="text-align:center;padding:20px 0;">
                    <h1 style="color:#FF9AA2;margin:0;">予你平安</h1>
                    <p style="color:#D66078;font-size:14px;">Campus Crush</p>
                </div>
                <div style="background:#fff;border-radius:12px;padding:30px;margin:20px 0;
                            box-shadow:0 2px 12px rgba(255,154,162,0.15);">
                    <p style="color:#666;font-size:15px;">您的登录验证码为：</p>
                    <div style="text-align:center;padding:20px;">
                        <span style="font-size:36px;font-weight:bold;letter-spacing:8px;
                                     color:#FF9AA2;background:#fff5f5;padding:12px 28px;
                                     border-radius:8px;border:2px dashed #FFB7B2;">
                            """ + code + """
                        </span>
                    </div>
                    <p style="color:#999;font-size:13px;text-align:center;">
                        验证码 5 分钟内有效，请勿泄露给他人
                    </p>
                </div>
                <div style="text-align:center;padding:10px;">
                    <p style="color:#bbb;font-size:12px;">
                        如果这不是您本人的操作，请忽略此邮件
                    </p>
                </div>
            </div>
            """;
    }
}
