package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.EmailSettings;
import com.cc.campuscrush.mapper.EmailSettingsMapper;
import com.cc.campuscrush.service.EmailSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 【EmailSettingsServiceImpl】邮箱设置服务层实现
 * &lt;p&gt;核心功能：用户邮箱通知偏好的增删改查管理&lt;/p&gt;
 * &lt;p&gt;使用场景：用户个人设置中配置邮箱提醒偏好，被 EmailSettingsController 调用，支持按用户查询、保存（存在则更新否则插入）和删除邮箱设置记录&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class EmailSettingsServiceImpl implements EmailSettingsService {

    @Autowired
    private EmailSettingsMapper emailSettingsMapper;

    /**
     * 根据用户ID查询邮箱设置（被EmailSettingsController调用）
     * 业务逻辑：直接查MySQL email_settings表按userId查询
     * 异常场景：用户不存在或无设置时返回null
     *
     * @param userId 用户ID（必填）
     * @return 邮箱设置实体，无记录时返回null
     */
    @Override
    public EmailSettings findByUserId(Long userId) {
        return emailSettingsMapper.findByUserId(userId);
    }

    /**
     * 保存或更新邮箱设置（被EmailSettingsController调用）
     * 业务逻辑：查当前用户是否已有设置记录 → 存在则用已有ID执行update → 不存在则执行insert
     * 异常场景：数据库操作失败时返回受影响行数
     *
     * @param settings 邮箱设置实体（必填，需含userId）
     * @return 受影响行数，大于0表示成功
     */
    @Override
    public int save(EmailSettings settings) {
        EmailSettings existing = emailSettingsMapper.findByUserId(settings.getUserId());
        if (existing != null) {
            settings.setId(existing.getId());
            return emailSettingsMapper.update(settings);
        }
        return emailSettingsMapper.insert(settings);
    }

    /**
     * 删除用户邮箱设置（被EmailSettingsController调用）
     * 业务逻辑：按userId删除email_settings表记录
     * 异常场景：无记录时返回0
     *
     * @param userId 用户ID（必填）
     * @return 受影响行数
     */
    @Override
    public int deleteByUserId(Long userId) {
        return emailSettingsMapper.deleteByUserId(userId);
    }
}