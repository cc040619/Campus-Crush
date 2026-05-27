package com.cc.campuscrush.service;

import com.cc.campuscrush.entity.SysUser;
import com.cc.campuscrush.vo.UserVO;

/**
 * 【UserService】服务层接口
 * &lt;p&gt;核心功能：提供用户注册登录（密码/验证码）、个人信息管理、密码修改、头像更新及邮箱绑定功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于用户账号管理与认证场景，被UserController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface UserService {

    /**
     * 通过用户名和密码登录
     * 业务逻辑：校验账号是否冻结 → 验证密码是否正确 → 登录失败则记录失败次数并可能触发冻结 → 登录成功则清除失败记录
     * 异常场景：用户不存在时返回null；密码错误时返回null并累计失败次数；账号被冻结时返回null
     *
     * @param username 用户名（必填）
     * @param password 明文密码（必填，由后端进行加密比对）
     * @return 登录成功的用户实体，登录失败时返回null
     */
    SysUser login(String username, String password);

    /**
     * 通过邮箱验证码登录
     * 业务逻辑：验证邮箱验证码是否正确且未过期 → 查询该邮箱对应的用户 → 返回用户信息
     * 异常场景：验证码错误或过期时返回null；邮箱未注册时返回null
     *
     * @param email 邮箱地址（必填）
     * @param code  6位验证码（必填）
     * @return 登录成功的用户实体，登录失败时返回null
     */
    SysUser loginByCode(String email, String code);

    /**
     * 发送登录验证码到邮箱
     * 业务逻辑：校验邮箱是否已注册 → 检查发送频率限制（60秒内不可重复发送） → 生成6位验证码 → 通过邮件服务发送
     * 异常场景：邮箱未注册时返回错误信息；发送频率超限时返回错误信息
     *
     * @param email 邮箱地址（必填，需为已注册的邮箱）
     * @return null表示发送成功，否则返回具体的错误信息（如"邮箱未注册"、"请60秒后再试"）
     */
    String sendLoginCode(String email);

    /**
     * 注册新用户
     * 业务逻辑：校验用户名唯一性 → 校验密码强度 → 加密密码 → 保存用户信息 → 创建默认用户配置
     * 异常场景：用户名已存在时返回错误信息；密码强度不足时返回错误信息；必填字段为空时返回错误信息
     *
     * @param user 用户实体（必填，需包含username、password等字段）
     * @return null表示注册成功，否则返回具体的错误信息
     */
    String register(SysUser user);

    /**
     * 校验密码强度
     * 业务逻辑：检查密码长度（至少8位） → 检查是否包含数字、大小写字母、特殊字符 → 返回校验结果
     * 异常场景：密码为null或空时返回错误信息
     *
     * @param password 待校验的密码（必填）
     * @return null表示密码合法，否则返回具体的错误信息（如"密码长度不足8位"）
     */
    String validatePasswordStrength(String password);

    /**
     * 查询账号登录冻结剩余时间
     * 业务逻辑：查询该账号的冻结截止时间 → 计算与当前时间的差值 → 返回剩余冻结秒数
     * 异常场景：账号未被冻结时返回0
     *
     * @param username 用户名（必填）
     * @return 剩余冻结秒数，未冻结时返回0
     */
    long getLoginFreezeRemaining(String username);

    /**
     * 获取用户信息（含与其他用户的关系状态）
     * 业务逻辑：查询用户基本信息 → 附带关注关系、好友关系等状态 → 封装为UserVO返回
     * 异常场景：用户不存在时返回null
     *
     * @param userId        目标用户ID（必填）
     * @param currentUserId 当前登录用户ID（必填，用于判断关注/好友关系）
     * @return 用户信息VO，用户不存在时返回null
     */
    UserVO getUserInfo(Long userId, Long currentUserId);

    /**
     * 更新用户昵称
     * 业务逻辑：校验昵称合法性和唯一性 → 更新数据库中的昵称字段 → 同步更新缓存
     * 异常场景：昵称已被占用时返回false；昵称包含敏感词时返回false
     *
     * @param userId   用户ID（必填）
     * @param nickname 新昵称（必填，不能为空）
     * @return true表示更新成功，false表示更新失败
     */
    boolean updateNickname(Long userId, String nickname);

    /**
     * 修改用户密码
     * 业务逻辑：校验旧密码是否正确 → 校验新密码强度 → 加密新密码 → 更新数据库
     * 异常场景：旧密码错误时返回false；新密码强度不足时返回false
     *
     * @param userId      用户ID（必填）
     * @param oldPassword 旧密码（必填）
     * @param newPassword 新密码（必填，需满足强度要求）
     * @return true表示修改成功，false表示修改失败
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 更新用户头像
     * 业务逻辑：校验头像URL有效性 → 更新数据库中的头像字段 → 同步更新Redis缓存
     * 异常场景：头像URL格式无效时返回false
     *
     * @param userId    用户ID（必填）
     * @param avatarUrl 新头像URL（必填，需为有效的图片URL）
     * @return true表示更新成功，false表示更新失败
     */
    boolean updateAvatar(Long userId, String avatarUrl);

    /**
     * 更新用户个人资料
     * 业务逻辑：更新用户的性别、生日、简介、所在城市等非敏感资料字段 → 保存到数据库
     * 异常场景：用户不存在时返回false
     *
     * @param user 用户实体（必填，需包含userId和要更新的字段）
     * @return true表示更新成功，false表示更新失败
     */
    boolean updateProfile(SysUser user);

    /**
     * 发送绑定邮箱的验证码
     * 业务逻辑：校验邮箱是否已被其他账号绑定 → 检查发送频率限制 → 生成6位验证码 → 通过邮件服务发送
     * 异常场景：邮箱已被绑定且不是当前用户时返回错误信息；发送频率超限时返回错误信息
     *
     * @param email 邮箱地址（必填，需为有效的邮箱格式）
     * @return null表示发送成功，否则返回具体的错误信息
     */
    String sendBindCode(String email);

    /**
     * 绑定或修改当前用户的邮箱
     * 业务逻辑：校验验证码是否正确且未过期 → 校验邮箱未被其他用户绑定 → 更新用户邮箱 → 清除验证码
     * 异常场景：验证码错误或过期时返回错误信息；邮箱已被他人绑定时返回错误信息
     *
     * @param userId 用户ID（必填）
     * @param email  新邮箱地址（必填）
     * @param code   邮箱验证码（必填）
     * @return null表示绑定成功，否则返回具体的错误信息（如"验证码错误"、"邮箱已被绑定"）
     */
    String bindEmail(Long userId, String email, String code);
}
