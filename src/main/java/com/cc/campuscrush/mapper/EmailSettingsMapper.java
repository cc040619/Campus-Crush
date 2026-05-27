package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.EmailSettings;
import org.apache.ibatis.annotations.*;

/**
 * EmailSettingsMapper数据访问层
 * <p>核心功能：管理用户的邮件通知设置，支持邮箱绑定、订阅偏好配置、启用/禁用通知</p>
 * <p>使用场景：用户邮箱设置、邮件通知偏好管理，被EmailSettingsService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface EmailSettingsMapper {

    /**
     * 根据用户ID查询邮件通知设置（SELECT）
     *
     * @param userId 用户ID（必填）
     * @return 邮件设置实体，无记录时返回null
     */
    @Select("SELECT * FROM email_settings WHERE user_id = #{userId}")
    EmailSettings findByUserId(Long userId);

    /**
     * 新增邮件通知设置（INSERT）
     *
     * @param settings 邮件设置实体（必填）
     * @return 受影响行数
     */
    @Insert("INSERT INTO email_settings (user_id, email, subscription, enabled, create_time, update_time) " +
            "VALUES (#{userId}, #{email}, #{subscription}, #{enabled}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(EmailSettings settings);

    /**
     * 更新邮件通知设置（UPDATE）
     *
     * @param settings 邮件设置实体（必填，需包含id和更新内容）
     * @return 受影响行数
     */
    @Update("UPDATE email_settings SET email = #{email}, subscription = #{subscription}, " +
            "enabled = #{enabled}, update_time = NOW() WHERE id = #{id}")
    int update(EmailSettings settings);

    /**
     * 根据用户ID删除邮件通知设置（DELETE）
     *
     * @param userId 用户ID（必填）
     * @return 受影响行数
     */
    @Delete("DELETE FROM email_settings WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
}
