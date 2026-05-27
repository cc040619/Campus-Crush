package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.common.RedisConstant;
import com.cc.campuscrush.entity.SysUser;
import com.cc.campuscrush.mapper.FollowMapper;
import com.cc.campuscrush.mapper.FollowNoticeMapper;
import com.cc.campuscrush.mapper.InteractionNoticeMapper;
import com.cc.campuscrush.mapper.PostMapper;
import com.cc.campuscrush.mapper.SysUserMapper;
import com.cc.campuscrush.service.ImageCacheService;
import com.cc.campuscrush.service.MailService;
import com.cc.campuscrush.service.UserService;
import com.cc.campuscrush.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 【UserServiceImpl】用户服务层实现
 * &lt;p&gt;核心功能：用户注册、密码/验证码双模式登录、个人信息管理及安全防护（登录失败冻结、密码加密迁移）&lt;/p&gt;
 * &lt;p&gt;使用场景：平台用户体系的核心服务，被 UserController 调用，支持密码强度校验（8-20位含数字和字母）、BCrypt/MD5 密码兼容及自动迁移、QQ邮箱格式验证、Redis 验证码管理、登录失败计数与定时冻结机制、头像更新同步刷新缓存、修改昵称时联动更新所有通知表中的发送者昵称&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private FollowNoticeMapper followNoticeMapper;

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private InteractionNoticeMapper interactionNoticeMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ImageCacheService imageCacheService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 校验密码强度（8-20位且必须同时包含数字和字母，被register和updatePassword调用）
     * 业务逻辑：null或长度不在8~20返回提示 → 逐字符检查是否含数字和字母 → 全字母返回提示 → 全数字返回提示 → 无数字无字母返回提示 → 合法返回null
     * 异常场景：返回非null字符串表示校验失败，null表示通过
     *
     * @param password 明文密码（必填）
     * @return 校验成功返回null，失败返回错误提示字符串
     */
    @Override
    public String validatePasswordStrength(String password) {
        if (password == null || password.length() < 8 || password.length() > 20) {
            return "密码长度必须为8-20位";
        }
        boolean hasDigit = false;
        boolean hasLetter = false;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (Character.isLetter(c)) {
                hasLetter = true;
            } else {
                // 包含了非数字非字母字符
            }
            if (hasDigit && hasLetter) {
                break;
            }
        }
        if (!hasDigit && hasLetter) {
            return "密码不能为全字母，必须包含数字";
        }
        if (hasDigit && !hasLetter) {
            return "密码不能为全数字，必须包含字母";
        }
        if (!hasDigit && !hasLetter) {
            return "密码必须包含数字和字母";
        }
        return null;
    }

    /**
     * 用户密码登录（支持BCrypt/MD5密码兼容和自动迁移，被UserController调用）
     * 业务逻辑：查用户是否存在 → 检查loginFreezeUntil是否仍在冻结期（是则返回null） → 冻结过期自动重置失败计数 → 判断密码加密方式（BCrypt以$2a$/2b$/2y$开头且长度60） → MD5用户验证成功后自动迁移为BCrypt → 密码错误时递增失败计数，达到最大则设置冻结时间并同步Redis → 密码正确重置失败计数和Redis
     * 异常场景：用户不存在返回null；冻结中返回null；密码错误返回null
     *
     * @param username 用户名（必填）
     * @param password 明文密码（必填）
     * @return SysUser对象，登录失败返回null
     */
    @Override
    public SysUser login(String username, String password) {
        SysUser user = sysUserMapper.findByUsername(username);
        if (user == null) {
            return null;
        }

        // 检查是否已被冻结
        if (user.getLoginFreezeUntil() != null && user.getLoginFreezeUntil().isAfter(LocalDateTime.now())) {
            // 冻结中，直接返回 null（Controller 层会给出剩余时间提示）
            return null;
        }

        // 冻结已过期，重置失败计数（防止 Redis 过期后 DB 计数未清零导致一次失败就再次冻结）
        if (user.getLoginFreezeUntil() != null) {
            sysUserMapper.resetLoginFail(user.getId());
            user.setLoginFailCount(0);
            user.setLoginFreezeUntil(null);
        }

        String storedPassword = user.getPassword();
        boolean passwordMatch = false;

        if (isBCryptPassword(storedPassword)) {
            passwordMatch = passwordEncoder.matches(password, storedPassword);
        } else {
            passwordMatch = isMD5Password(password, storedPassword);
            if (passwordMatch) {
                // MD5 迁移到 BCrypt
                String newEncodedPassword = passwordEncoder.encode(password);
                sysUserMapper.updatePassword(user.getId(), newEncodedPassword);
            }
        }

        if (!passwordMatch) {
            // 密码错误：增加失败计数
            int failCount = user.getLoginFailCount() != null ? user.getLoginFailCount() + 1 : 1;
            LocalDateTime freezeUntil = null;
            if (failCount >= RedisConstant.LOGIN_FAIL_MAX) {
                freezeUntil = LocalDateTime.now().plusSeconds(RedisConstant.LOGIN_FREEZE_SECONDS);
                // 同步更新 Redis 缓存
                String redisKey = RedisConstant.LOGIN_FAIL_COUNT_KEY_PREFIX + username;
                redisTemplate.opsForValue().set(redisKey, "frozen",
                        Duration.ofSeconds(RedisConstant.LOGIN_FREEZE_SECONDS));
            }
            sysUserMapper.updateLoginFail(user.getId(), failCount, freezeUntil);
            return null;
        }

        // 密码正确：重置失败计数
        sysUserMapper.resetLoginFail(user.getId());
        String redisKey = RedisConstant.LOGIN_FAIL_COUNT_KEY_PREFIX + username;
        redisTemplate.delete(redisKey);

        return user;
    }

    /**
     * 邮箱验证码登录（被UserController调用）
     * 业务逻辑：从Redis获取验证码 → 验证码过期或不存在返回null → 验证码不匹配返回null → 验证通过后删除Redis验证码 → 查邮箱对应用户 → 用户不存在返回null → 重置登录失败计数
     * 异常场景：验证码过期/错误返回null；邮箱未注册返回null
     *
     * @param email QQ邮箱（必填）
     * @param code  6位验证码（必填）
     * @return SysUser对象，验证失败返回null
     */
    @Override
    public SysUser loginByCode(String email, String code) {
        // 从 Redis 获取验证码
        String codeKey = RedisConstant.EMAIL_CODE_KEY_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(codeKey);
        if (storedCode == null) {
            return null; // 验证码过期或不存在
        }
        if (!storedCode.equals(code)) {
            return null; // 验证码错误
        }
        // 验证通过，删除验证码
        redisTemplate.delete(codeKey);

        SysUser user = sysUserMapper.findByEmail(email);
        if (user == null) {
            return null;
        }

        // 验证码登录成功也重置失败计数
        sysUserMapper.resetLoginFail(user.getId());
        return user;
    }

    /**
     * 发送登录验证码（被UserController调用）
     * 业务逻辑：校验QQ邮箱格式（1开头的5-10位数字@qq.com） → 格式错误返回提示 → 查邮箱是否已注册 → 未注册返回提示 → 生成6位随机验证码 → 存入Redis（带TTL） → 异步发送邮件 → 成功返回null
     * 异常场景：邮箱格式错误返回"请输入正确的QQ邮箱地址"；未注册返回"该邮箱未注册"
     *
     * @param email QQ邮箱（必填，格式：QQ号@qq.com）
     * @return 成功返回null，失败返回错误提示字符串
     */
    @Override
    public String sendLoginCode(String email) {
        // 验证邮箱格式（QQ邮箱）
        if (email == null || !email.matches("^[1-9]\\d{4,10}@qq\\.com$")) {
            return "请输入正确的QQ邮箱地址";
        }

        // 检查该邮箱是否已注册
        SysUser user = sysUserMapper.findByEmail(email);
        if (user == null) {
            return "该邮箱未注册";
        }

        // 生成6位验证码
        String code = String.format("%06d", RANDOM.nextInt(1000000));
        String codeKey = RedisConstant.EMAIL_CODE_KEY_PREFIX + email;
        redisTemplate.opsForValue().set(codeKey, code, Duration.ofSeconds(RedisConstant.EMAIL_CODE_TTL_SECONDS));

        // 异步发送邮件
        mailService.sendVerificationCode(email, code);

        return null;
    }

    /**
     * 发送邮箱绑定验证码（被UserController调用）
     * 业务逻辑：校验QQ邮箱格式 → 格式错误返回提示 → 查邮箱是否已被其他账号绑定 → 已绑定返回提示 → 生成6位验证码 → 存入Redis（BIND_CODE_KEY_PREFIX前缀） → 异步发送邮件 → 成功返回null
     * 异常场景：格式错误返回"请输入正确的QQ邮箱地址"；已被绑定返回"该邮箱已被其他账号绑定"
     *
     * @param email QQ邮箱（必填）
     * @return 成功返回null，失败返回错误提示字符串
     */
    @Override
    public String sendBindCode(String email) {
        if (email == null || !email.matches("^[1-9]\\d{4,10}@qq\\.com$")) {
            return "请输入正确的QQ邮箱地址";
        }
        SysUser existing = sysUserMapper.findByEmail(email);
        if (existing != null) {
            return "该邮箱已被其他账号绑定";
        }
        String code = String.format("%06d", RANDOM.nextInt(1000000));
        String codeKey = RedisConstant.BIND_CODE_KEY_PREFIX + email;
        redisTemplate.opsForValue().set(codeKey, code, Duration.ofSeconds(RedisConstant.EMAIL_CODE_TTL_SECONDS));
        mailService.sendVerificationCode(email, code);
        return null;
    }

    /**
     * 验证并绑定邮箱（被UserController调用）
     * 业务逻辑：从Redis获取绑定验证码 → 验证码错误或过期返回提示 → 查邮箱是否已被其他用户绑定 → 已绑定返回提示 → 验证通过删除Redis验证码 → 更新用户email字段 → 成功返回null
     * 异常场景：验证码错误返回"验证码错误或已过期"；邮箱已被他人绑定返回"该邮箱已被其他账号绑定"
     *
     * @param userId 当前用户ID（必填）
     * @param email  QQ邮箱（必填）
     * @param code   6位验证码（必填）
     * @return 成功返回null，失败返回错误提示字符串
     */
    @Override
    public String bindEmail(Long userId, String email, String code) {
        String codeKey = RedisConstant.BIND_CODE_KEY_PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(codeKey);
        if (storedCode == null || !storedCode.equals(code)) {
            return "验证码错误或已过期";
        }
        SysUser existing = sysUserMapper.findByEmail(email);
        if (existing != null && !existing.getId().equals(userId)) {
            return "该邮箱已被其他账号绑定";
        }
        redisTemplate.delete(codeKey);
        sysUserMapper.updateEmail(userId, email);
        return null;
    }

    /**
     * 用户注册（被UserController调用）
     * 业务逻辑：校验密码强度 → QQ邮箱格式必填校验 → 检查用户名唯一性 → 检查手机号唯一性（非空时） → 检查邮箱唯一性 → 密码BCrypt加密 → 插入MySQL → 成功返回null
     * 异常场景：密码弱返回提示；邮箱格式错返回提示；用户名/手机号/邮箱重复返回对应提示；插入失败返回"注册失败"
     *
     * @param user 用户实体（必填，含username/password/email，phone可选）
     * @return 成功返回null，失败返回错误提示字符串
     */
    @Override
    public String register(SysUser user) {
        // 校验密码强度
        String pwdError = validatePasswordStrength(user.getPassword());
        if (pwdError != null) {
            return pwdError;
        }

        // QQ邮箱必填校验
        if (user.getEmail() == null || !user.getEmail().matches("^[1-9]\\d{4,10}@qq\\.com$")) {
            return "请输入正确的QQ邮箱地址（格式：QQ号@qq.com）";
        }

        // 检查用户名唯一性
        SysUser existing = sysUserMapper.findByUsername(user.getUsername());
        if (existing != null) {
            return "用户名已存在";
        }

        // 检查手机号唯一性
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
            SysUser phoneUser = sysUserMapper.findByPhone(user.getPhone().trim());
            if (phoneUser != null) {
                return "该手机号已被注册";
            }
        }

        // 检查邮箱唯一性
        SysUser emailUser = sysUserMapper.findByEmail(user.getEmail());
        if (emailUser != null) {
            return "该QQ邮箱已被注册";
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        int result = sysUserMapper.insert(user);
        return result > 0 ? null : "注册失败";
    }

    /**
     * 获取用户个人信息（被UserController调用）
     * 业务逻辑：查SysUser → 用户不存在返回null → 组装UserVO（含头像缓存、发帖数、获赞与收藏总数） → 若currentUserId非本人则查询是否关注了目标用户
     * 异常场景：用户不存在返回null
     *
     * @param userId        目标用户ID（必填）
     * @param currentUserId 当前登录用户ID（可选，为null或与userId相同时不查关注关系）
     * @return UserVO对象，用户不存在返回null
     */
    @Override
    public UserVO getUserInfo(Long userId, Long currentUserId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setNickname(user.getNickname());
        userVO.setAvatar(imageCacheService.getAvatar(userId));
        userVO.setPhone(user.getPhone());
        userVO.setEmail(user.getEmail());
        userVO.setGender(user.getGender());
        userVO.setIntro(user.getIntro());
        userVO.setPostCount(Math.toIntExact(postMapper.countByUserId(userId)));
        userVO.setFollowerCount(0);
        userVO.setFollowingCount(0);
        userVO.setLikeAndCollectCount(Math.toIntExact(postMapper.sumLikeAndCollectCountByUserId(userId)));
        // 根据当前用户是否关注了目标用户来设置 isFollowing
        if (currentUserId != null && !currentUserId.equals(userId)) {
            int count = followMapper.countByFollowerIdAndFollowingId(currentUserId, userId);
            userVO.setFollowing(count > 0);
        } else {
            userVO.setFollowing(false);
        }

        return userVO;
    }

    /**
     * 获取登录冻结剩余秒数（被UserController调用）
     * 业务逻辑：先查Redis快速查询（值为"frozen"则获取TTL） → Redis未命中查MySQL的loginFreezeUntil字段 → 计算距当前的秒数 → 不过冻结期返回0
     * 异常场景：未冻结时返回0
     *
     * @param username 用户名（必填）
     * @return 冻结剩余秒数，未冻结返回0
     */
    @Override
    public long getLoginFreezeRemaining(String username) {
        // 先从 Redis 快速查询
        String key = RedisConstant.LOGIN_FAIL_COUNT_KEY_PREFIX + username;
        String val = redisTemplate.opsForValue().get(key);
        if ("frozen".equals(val)) {
            Long ttl = redisTemplate.getExpire(key);
            return ttl != null && ttl > 0 ? ttl : 0;
        }
        // Redis 未命中，查数据库
        SysUser user = sysUserMapper.findByUsername(username);
        if (user != null && user.getLoginFreezeUntil() != null
                && user.getLoginFreezeUntil().isAfter(LocalDateTime.now())) {
            long seconds = Duration.between(LocalDateTime.now(), user.getLoginFreezeUntil()).getSeconds();
            return Math.max(seconds, 0);
        }
        return 0;
    }

    /**
     * 更新用户昵称（被UserController调用）
     * 业务逻辑：直接更新MySQL sys_user表nickname字段
     * 异常场景：用户不存在时返回false
     *
     * @param userId   用户ID（必填）
     * @param nickname 新昵称（必填）
     * @return true-更新成功，false-用户不存在
     */
    @Override
    public boolean updateNickname(Long userId, String nickname) {
        int result = sysUserMapper.updateNickname(userId, nickname);
        return result > 0;
    }

    /**
     * 修改密码（需旧密码验证，被UserController调用）
     * 业务逻辑：新密码强度校验（不通过返回false） → 查用户（含密码字段） → 用户不存在返回false → 判断旧密码加密方式（BCrypt/MD5）并验证 → 旧密码错误返回false → 新密码BCrypt加密 → 更新MySQL → 返回更新结果
     * 异常场景：用户不存在返回false；旧密码错误返回false；新密码弱返回false
     *
     * @param userId      用户ID（必填）
     * @param oldPassword 旧明文密码（必填）
     * @param newPassword 新明文密码（必填，8-20位含数字字母）
     * @return true-修改成功，false-验证失败
     */
    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        // 新密码强度校验
        String pwdError = validatePasswordStrength(newPassword);
        if (pwdError != null) {
            return false;
        }

        SysUser user = sysUserMapper.selectByIdWithPassword(userId);
        if (user == null) {
            return false;
        }

        String storedPassword = user.getPassword();
        boolean passwordMatch = false;

        if (isBCryptPassword(storedPassword)) {
            passwordMatch = passwordEncoder.matches(oldPassword, storedPassword);
        } else {
            passwordMatch = isMD5Password(oldPassword, storedPassword);
        }

        if (!passwordMatch) {
            return false;
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        int result = sysUserMapper.updatePassword(userId, encodedNewPassword);
        return result > 0;
    }

    /**
     * 更新用户头像（同步刷新Redis缓存，被UserController调用）
     * 业务逻辑：更新MySQL avatar字段 → 更新成功后同步调用ImageCacheService写入Redis头像缓存
     * 异常场景：用户不存在时返回false
     *
     * @param userId    用户ID（必填）
     * @param avatarUrl 头像URL（必填）
     * @return true-更新成功，false-用户不存在
     */
    @Override
    public boolean updateAvatar(Long userId, String avatarUrl) {
        int result = sysUserMapper.updateAvatar(userId, avatarUrl);
        if (result > 0) {
            imageCacheService.setAvatar(userId, avatarUrl);
        }
        return result > 0;
    }

    /**
     * 更新用户个人资料（昵称变更时联动更新所有通知表，被UserController调用）
     * 业务逻辑：更新MySQL sys_user表profile字段 → 若昵称非空则联动更新follow_notice表和interaction_notice表中所有from_user_nickname
     * 异常场景：用户不存在时返回false
     *
     * @param user 用户实体（必填，含id和更新字段）
     * @return true-更新成功，false-用户不存在
     */
    @Override
    public boolean updateProfile(SysUser user) {
        int result = sysUserMapper.updateProfile(user);
        if (result > 0 && user.getNickname() != null && !user.getNickname().isEmpty()) {
            followNoticeMapper.updateFromUserNickname(user.getId(), user.getNickname());
            interactionNoticeMapper.updateFromUserNickname(user.getId(), user.getNickname());
        }
        return result > 0;
    }

    private boolean isBCryptPassword(String password) {
        return password != null && password.length() == 60
            && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }

    private boolean isMD5Password(String rawPassword, String storedPassword) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(rawPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().equals(storedPassword);
        } catch (java.security.NoSuchAlgorithmException e) {
            return false;
        }
    }
}
