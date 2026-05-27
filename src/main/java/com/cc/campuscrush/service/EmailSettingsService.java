package com.cc.campuscrush.service;

import com.cc.campuscrush.entity.EmailSettings;

/**
 * 【EmailSettingsService】服务层接口
 * &lt;p&gt;核心功能：提供用户邮件通知设置的查询、保存和删除功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于用户个性化邮件通知偏好配置场景，被EmailSettingsController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface EmailSettingsService {

    /**
     * 根据用户ID查询邮件通知设置
     * 业务逻辑：查询email_settings表 → 返回用户的邮件偏好配置
     * 异常场景：用户未配置过邮件设置时返回null
     *
     * @param userId 用户ID（必填）
     * @return 邮件设置实体，未配置时返回null
     */
    EmailSettings findByUserId(Long userId);

    /**
     * 保存或更新邮件通知设置
     * 业务逻辑：判断设置记录是否存在 → 存在则更新，不存在则新增
     * 异常场景：用户ID无效时保存失败
     *
     * @param settings 邮件设置实体（必填，需包含userId和各项通知开关）
     * @return 受影响的行数，1表示成功，0表示失败
     */
    int save(EmailSettings settings);

    /**
     * 根据用户ID删除邮件通知设置
     * 业务逻辑：删除该用户的所有邮件偏好配置记录
     * 异常场景：用户无邮件设置记录时返回0
     *
     * @param userId 用户ID（必填）
     * @return 受影响的行数，1表示成功，0表示记录不存在
     */
    int deleteByUserId(Long userId);
}
