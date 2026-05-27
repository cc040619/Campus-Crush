package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * SysUserMapper数据访问层
 * <p>核心功能：管理系统用户账户，支持用户注册、多种方式登录查询（用户名/手机/邮箱）、个人资料更新、密码管理、头像设置及用户搜索</p>
 * <p>使用场景：用户注册/登录认证、个人资料编辑、用户搜索、情侣配对搜索、登录失败冻结，被SysUserService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface SysUserMapper {

    /**
     * 根据用户名查询用户（SELECT）
     *
     * @param username 用户名（必填）
     * @return 用户实体，无记录时返回null
     */
    SysUser findByUsername(String username);

    /**
     * 根据手机号查询用户（SELECT）
     *
     * @param phone 手机号（必填）
     * @return 用户实体，无记录时返回null
     */
    SysUser findByPhone(String phone);

    /**
     * 根据邮箱查询用户（SELECT）
     *
     * @param email 邮箱地址（必填）
     * @return 用户实体，无记录时返回null
     */
    SysUser findByEmail(String email);

    /**
     * 更新用户的邮箱地址（UPDATE）
     *
     * @param userId 用户ID（必填）
     * @param email  新邮箱地址（必填）
     * @return 受影响行数
     */
    int updateEmail(@Param("userId") Long userId, @Param("email") String email);

    /**
     * 新增一个用户账户（用户注册）（INSERT）
     *
     * @param user 用户实体（必填）
     * @return 受影响行数
     */
    int insert(SysUser user);

    /**
     * 更新登录失败次数及冻结时间（登录安全控制）（UPDATE）
     *
     * @param id          用户ID（必填）
     * @param failCount   失败次数（必填）
     * @param freezeUntil 冻结截止时间（必填）
     * @return 受影响行数
     */
    int updateLoginFail(@Param("id") Long id, @Param("failCount") int failCount, @Param("freezeUntil") java.time.LocalDateTime freezeUntil);

    /**
     * 重置登录失败计数（登录成功后调用）（UPDATE）
     *
     * @param id 用户ID（必填）
     * @return 受影响行数
     */
    int resetLoginFail(@Param("id") Long id);

    /**
     * 根据ID查询用户（不含密码字段）（SELECT）
     *
     * @param id 用户ID（必填）
     * @return 用户实体（不含密码），无记录时返回null
     */
    SysUser selectById(Long id);

    /**
     * 根据ID查询用户（含密码字段），仅用于密码校验场景（登录/修改密码）（SELECT）
     *
     * @param id 用户ID（必填）
     * @return 用户实体（含密码），无记录时返回null
     */
    SysUser selectByIdWithPassword(Long id);

    /**
     * 更新用户昵称（UPDATE）
     *
     * @param userId   用户ID（必填）
     * @param nickname 新昵称（必填）
     * @return 受影响行数
     */
    int updateNickname(Long userId, String nickname);

    /**
     * 更新用户密码（UPDATE）
     *
     * @param userId   用户ID（必填）
     * @param password 新密码（必填）
     * @return 受影响行数
     */
    int updatePassword(Long userId, String password);

    /**
     * 更新用户头像（UPDATE）
     *
     * @param userId    用户ID（必填）
     * @param avatarUrl 新头像URL（必填）
     * @return 受影响行数
     */
    int updateAvatar(Long userId, String avatarUrl);

    /**
     * 更新用户个人资料（UPDATE）
     *
     * @param user 用户实体（必填，需包含id和更新内容）
     * @return 受影响行数
     */
    int updateProfile(SysUser user);

    /**
     * 查询全部用户列表（SELECT）
     *
     * @return 全部用户列表
     */
    List<SysUser> selectAll();

    /**
     * 根据关键词模糊搜索用户（SELECT）
     *
     * @param keyword 搜索关键词（必填）
     * @return 匹配的用户列表
     */
    List<SysUser> searchByKeyword(String keyword);

    /**
     * 根据关键词搜索可配对的用户（情侣配对搜索）（SELECT）
     *
     * @param keyword 搜索关键词（必填）
     * @return 匹配的可配对用户列表
     */
    List<SysUser> searchForCouple(String keyword);

    /**
     * 根据ID列表批量查询用户（SELECT）
     *
     * @param ids 用户ID列表（必填）
     * @return 用户列表
     */
    List<SysUser> selectByIds(@Param("ids") List<Long> ids);
}
