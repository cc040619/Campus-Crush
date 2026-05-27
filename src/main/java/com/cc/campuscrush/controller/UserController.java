package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.RedisConstant;
import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.entity.SysUser;
import com.cc.campuscrush.service.UserService;
import com.cc.campuscrush.utils.AliyunOSSOperator;
import com.cc.campuscrush.utils.JwtUtil;
import com.cc.campuscrush.utils.RateLimiterUtil;
import com.cc.campuscrush.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * UserController控制器
 * &lt;p&gt;核心功能：用户登录注册、个人信息管理和安全设置&lt;/p&gt;
 * &lt;p&gt;使用场景：用户系统的核心控制器，支持账号密码登录、验证码登录（QQ邮箱）、用户注册、个人信息查询、昵称修改、密码修改、头像上传、邮箱绑定和登出，含IP限流和登录失败冻结机制，被前端登录页、注册页、个人中心页和设置页调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    @Autowired
    private RateLimiterUtil rateLimiterUtil;

    /**
     * 用户账号密码登录（含IP限流和登录失败冻结机制）
     * 业务逻辑：获取客户端IP → 检查IP登录限流（每分钟最多10次） → 检查账号是否被冻结 → 执行用户名密码登录 → 成功后构建JWT token并设置HttpOnly Cookie → 失败后再次检查冻结状态 → 返回UserVO
     * 异常场景：IP限流返回"登录请求过于频繁，请稍后再试"；账号冻结返回"账号已被冻结，请N分钟后重试"；连续5次密码错误触发15分钟冻结；用户名或密码错误返回对应提示
     *
     * @param user 登录请求体，包含username（用户名/账号，必填）和password（密码，必填）
     * @param request HTTP请求对象（用于获取客户端IP）
     * @param response HTTP响应对象（用于设置JWT Cookie）
     * @return Result.data 为UserVO对象，包含用户ID、昵称、头像等基本信息
     */
    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody SysUser user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String clientIp = getClientIp(request);

            // 登录接口限流：每IP每分钟最多10次
            String rateLimitKey = RedisConstant.RATELIMIT_LOGIN_KEY_PREFIX + clientIp;
            if (!rateLimiterUtil.isAllowed(rateLimitKey,
                    RedisConstant.RATELIMIT_LOGIN_MAX, RedisConstant.RATELIMIT_LOGIN_WINDOW_SECONDS)) {
                return Result.error("登录请求过于频繁，请稍后再试");
            }

            // 检查账号是否已被冻结
            long freezeRemaining = userService.getLoginFreezeRemaining(user.getUsername());
            if (freezeRemaining > 0) {
                long minutes = freezeRemaining / 60;
                if (minutes > 0) {
                    return Result.error("账号已被冻结，请 " + minutes + " 分钟后重试");
                }
                return Result.error("账号已被冻结，请 " + freezeRemaining + " 秒后重试");
            }

            SysUser loggedIn = userService.login(user.getUsername(), user.getPassword());
            if (loggedIn != null) {
                return buildLoginSuccess(loggedIn, request, response);
            }

            // 登录失败后再次检查冻结状态（可能刚好被第5次错误触发冻结）
            freezeRemaining = userService.getLoginFreezeRemaining(user.getUsername());
            if (freezeRemaining > 0) {
                return Result.error("密码错误次数过多，账号已被冻结15分钟");
            }

            return Result.error("用户名或密码错误");
        } catch (Exception e) {
            log.error("登录失败", e);
            return Result.error("登录失败，请稍后重试");
        }
    }

    /**
     * 向QQ邮箱发送登录验证码（含IP和邮箱双重限流）
     * 业务逻辑：获取邮箱和客户端IP → IP限流检查（每小时每IP最多5次） → 邮箱限流检查（每60秒同一邮箱最多1次） → 委托userService发送验证码邮件 → 返回提示
     * 异常场景：邮箱为空返回"请输入QQ邮箱地址"；IP限流返回"验证码发送过于频繁，请N秒后再试"；邮箱限流返回"验证码已发送，请N秒后再试"；发送失败返回具体错误信息
     *
     * @param body 请求体，包含email（QQ邮箱地址，必填）
     * @param request HTTP请求对象（用于获取客户端IP）
     * @return Result.data 为字符串"验证码已发送至 xxx@qq.com"；失败时返回error
     */
    @PostMapping("/send-login-code")
    public Result<String> sendLoginCode(@RequestBody Map<String, String> body, HttpServletRequest request) {
        try {
            String email = body.get("email");
            if (email == null || email.trim().isEmpty()) {
                return Result.error("请输入QQ邮箱地址");
            }
            email = email.trim();

            String clientIp = getClientIp(request);

            // IP限流：每小时每IP最多5次
            String ipKey = RedisConstant.RATELIMIT_SENDCODE_IP_KEY_PREFIX + clientIp;
            if (!rateLimiterUtil.isAllowed(ipKey,
                    RedisConstant.RATELIMIT_SENDCODE_IP_MAX, RedisConstant.RATELIMIT_SENDCODE_IP_WINDOW_SECONDS)) {
                long remain = rateLimiterUtil.getRemainingTTL(ipKey);
                return Result.error("验证码发送过于频繁，请 " + remain + " 秒后再试");
            }

            // 邮箱限流：每60秒同一邮箱只能发1次
            String emailKey = RedisConstant.RATELIMIT_SENDCODE_EMAIL_KEY_PREFIX + email;
            if (!rateLimiterUtil.isAllowed(emailKey,
                    RedisConstant.RATELIMIT_SENDCODE_EMAIL_MAX, RedisConstant.RATELIMIT_SENDCODE_EMAIL_WINDOW_SECONDS)) {
                long remain = rateLimiterUtil.getRemainingTTL(emailKey);
                return Result.error("验证码已发送，请 " + remain + " 秒后再试");
            }

            String error = userService.sendLoginCode(email);
            if (error == null) {
                return Result.success("验证码已发送至 " + email);
            }
            return Result.error(error);
        } catch (Exception e) {
            log.error("发送验证码失败", e);
            return Result.error("验证码发送失败，请稍后重试");
        }
    }

    /**
     * 使用邮箱验证码登录
     * 业务逻辑：获取邮箱和验证码 → IP限流检查 → 委托userService校验验证码登录 → 成功后构建JWT token并设置HttpOnly Cookie → 返回UserVO
     * 异常场景：邮箱为空返回"请输入QQ邮箱地址"；验证码为空返回"请输入验证码"；IP限流返回"登录请求过于频繁，请稍后再试"；验证码错误或过期返回"验证码错误或已过期"
     *
     * @param body 请求体，包含email（QQ邮箱地址，必填）和code（验证码，必填）
     * @param request HTTP请求对象（用于获取客户端IP）
     * @param response HTTP响应对象（用于设置JWT Cookie）
     * @return Result.data 为UserVO对象，包含用户基本信息
     */
    @PostMapping("/login-by-code")
    public Result<UserVO> loginByCode(@RequestBody Map<String, String> body, HttpServletRequest request, HttpServletResponse response) {
        try {
            String email = body.get("email");
            String code = body.get("code");

            if (email == null || email.trim().isEmpty()) {
                return Result.error("请输入QQ邮箱地址");
            }
            if (code == null || code.trim().isEmpty()) {
                return Result.error("请输入验证码");
            }

            String clientIp = getClientIp(request);
            // 登录接口限流
            String rateLimitKey = RedisConstant.RATELIMIT_LOGIN_KEY_PREFIX + clientIp;
            if (!rateLimiterUtil.isAllowed(rateLimitKey,
                    RedisConstant.RATELIMIT_LOGIN_MAX, RedisConstant.RATELIMIT_LOGIN_WINDOW_SECONDS)) {
                return Result.error("登录请求过于频繁，请稍后再试");
            }

            SysUser loggedIn = userService.loginByCode(email.trim(), code.trim());
            if (loggedIn != null) {
                return buildLoginSuccess(loggedIn, request, response);
            }
            return Result.error("验证码错误或已过期");
        } catch (Exception e) {
            log.error("验证码登录失败", e);
            return Result.error("登录失败，请稍后重试");
        }
    }

    /**
     * 用户注册（含IP限流）
     * 业务逻辑：获取客户端IP → 注册接口限流检查（每IP每小时最多3次） → 委托userService执行注册逻辑（校验用户名、密码强度等） → 返回提示
     * 异常场景：IP限流返回"注册请求过于频繁，请N分钟后再试"；用户名已存在、密码不符合要求等返回具体错误信息
     *
     * @param user 注册请求体，包含username（用户名，必填）、password（密码，必填）等字段
     * @param request HTTP请求对象（用于获取客户端IP）
     * @return Result.data 为字符串"注册成功"；失败时返回error
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody SysUser user, HttpServletRequest request) {
        try {
            String clientIp = getClientIp(request);

            // 注册接口限流：每IP每小时最多3次
            String rateLimitKey = RedisConstant.RATELIMIT_REGISTER_KEY_PREFIX + clientIp;
            if (!rateLimiterUtil.isAllowed(rateLimitKey,
                    RedisConstant.RATELIMIT_REGISTER_MAX, RedisConstant.RATELIMIT_REGISTER_WINDOW_SECONDS)) {
                long remain = rateLimiterUtil.getRemainingTTL(rateLimitKey);
                long minutes = remain / 60;
                if (minutes > 0) {
                    return Result.error("注册请求过于频繁，请 " + minutes + " 分钟后再试");
                }
                return Result.error("注册请求过于频繁，请稍后再试");
            }

            String error = userService.register(user);
            if (error == null) {
                return Result.success("注册成功");
            }
            return Result.error(error);
        } catch (Exception e) {
            log.error("注册失败", e);
            return Result.error("注册失败，请稍后重试");
        }
    }

    /**
     * 查询当前登录用户的个人信息
     * 业务逻辑：从request属性获取userId → 委托userService查询用户信息（currentUserId传null，不查关注状态） → 返回UserVO
     * 异常场景：用户不存在时返回"获取用户信息失败"错误
     *
     * @param userId 当前用户ID（从@RequestAttribute获取，由拦截器注入，必填）
     * @return Result.data 为UserVO对象，包含昵称、头像、邮箱、手机号、简介等个人信息
     */
    @GetMapping("/info")
    public Result<UserVO> getUserInfo(@RequestAttribute("userId") Long userId) {
        try {
            // 获取自己的信息，currentUserId 传 null 即可（不需要查关注状态）
            UserVO userInfo = userService.getUserInfo(userId, null);
            if (userInfo != null) {
                return Result.success(userInfo);
            }
            return Result.error("获取用户信息失败");
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error("获取用户信息失败");
        }
    }

    /**
     * 修改当前用户的昵称
     * 业务逻辑：从request属性获取userId → 校验nickname非空 → 委托userService更新昵称 → 返回提示
     * 异常场景：昵称为空返回"昵称不能为空"错误；更新失败返回"昵称修改失败"错误
     *
     * @param userId 当前用户ID（从@RequestAttribute获取，由拦截器注入，必填）
     * @param request 请求体，包含nickname（新昵称，必填）
     * @return Result.data 为字符串"昵称修改成功"；失败时返回error
     */
    @PutMapping("/nickname")
    public Result<String> updateNickname(@RequestAttribute("userId") Long userId, @RequestBody Map<String, String> request) {
        try {
            String nickname = request.get("nickname");
            if (nickname == null || nickname.trim().isEmpty()) {
                return Result.error("昵称不能为空");
            }
            boolean success = userService.updateNickname(userId, nickname);
            if (success) {
                return Result.success("昵称修改成功");
            }
            return Result.error("昵称修改失败");
        } catch (Exception e) {
            log.error("昵称修改失败", e);
            return Result.error("昵称修改失败");
        }
    }

    /**
     * 修改当前用户的登录密码（需校验原密码和新密码强度）
     * 业务逻辑：从request属性获取userId → 校验oldPassword和newPassword非空 → 校验新密码强度 → 委托userService验证原密码并更新 → 返回提示
     * 异常场景：原密码为空返回"原密码不能为空"；新密码为空返回"新密码不能为空"；新密码不符合强度要求返回具体错误；原密码错误返回"原密码错误"
     *
     * @param userId 当前用户ID（从@RequestAttribute获取，由拦截器注入，必填）
     * @param request 请求体，包含oldPassword（原密码，必填）和newPassword（新密码，必填，需满足强度要求）
     * @return Result.data 为字符串"密码修改成功"；失败时返回error
     */
    @PutMapping("/password")
    public Result<String> updatePassword(@RequestAttribute("userId") Long userId, @RequestBody Map<String, String> request) {
        try {
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                return Result.error("原密码不能为空");
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return Result.error("新密码不能为空");
            }

            // 新密码强度校验
            String pwdError = userService.validatePasswordStrength(newPassword);
            if (pwdError != null) {
                return Result.error(pwdError);
            }

            boolean success = userService.updatePassword(userId, oldPassword, newPassword);
            if (success) {
                return Result.success("密码修改成功");
            }
            return Result.error("原密码错误");
        } catch (Exception e) {
            log.error("密码修改失败", e);
            return Result.error("密码修改失败");
        }
    }

    /**
     * 用户登出（清除JWT Cookie）
     * 业务逻辑：构造过期Cookie（Max-Age=0）→ 通过response清除token Cookie → 返回成功提示
     * 异常场景：异常时返回"登出失败"错误
     *
     * @param request HTTP请求对象（用于判断是否HTTPS连接）
     * @param response HTTP响应对象（用于设置清除Cookie的响应头）
     * @return Result.data 为字符串"登出成功"
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            String secureFlag = request.isSecure() ? "; Secure" : "";
            String cookieValue = String.format("token=; HttpOnly%s; Path=/; Max-Age=0; SameSite=Strict", secureFlag);
            response.addHeader("Set-Cookie", cookieValue);
            return Result.success("登出成功");
        } catch (Exception e) {
            log.error("登出失败", e);
            return Result.error("登出失败");
        }
    }

    /**
     * 上传并更新用户头像
     * 业务逻辑：从request属性获取userId → 校验文件非空 → 校验文件类型为jpg/png → 校验文件大小不超过10MB → 上传至OSS → 委托userService更新头像URL → 返回头像URL
     * 异常场景：文件为空返回"请选择要上传的文件"；格式不对返回"仅支持jpg和png格式的图片"；超过10MB返回"文件大小不能超过10MB"；更新失败返回"头像上传失败"
     *
     * @param userId 当前用户ID（从@RequestAttribute获取，由拦截器注入，必填）
     * @param file 头像图片文件（请求参数，必填，表单字段名"file"，仅支持jpg/png，最大10MB）
     * @return Result.data 为OSS上的头像图片访问URL字符串
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestAttribute("userId") Long userId, @RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            String originalFilename = file.getOriginalFilename();
            if (!originalFilename.endsWith(".jpg") && !originalFilename.endsWith(".png")) {
                return Result.error("仅支持jpg和png格式的图片");
            }

            if (file.getSize() > 10 * 1024 * 1024) {
                return Result.error("文件大小不能超过10MB");
            }

            byte[] fileContent = file.getBytes();
            String avatarUrl = aliyunOSSOperator.upload(fileContent, originalFilename);

            boolean success = userService.updateAvatar(userId, avatarUrl);
            if (success) {
                return Result.success(avatarUrl);
            }
            return Result.error("头像上传失败");
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败");
        } catch (Exception e) {
            log.error("头像上传失败", e);
            return Result.error("头像上传失败: " + e.getMessage());
        }
    }

    // ==================== 私有方法 ====================

    private Result<UserVO> buildLoginSuccess(SysUser loggedIn, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", loggedIn.getId());
        claims.put("username", loggedIn.getUsername());
        String token = jwtUtil.generateToken(claims, loggedIn.getUsername());

        String secureFlag = request.isSecure() ? "; Secure" : "";
        String cookieValue = String.format(
            "token=%s; HttpOnly%s; Path=/; Max-Age=%d; SameSite=Strict",
            token, secureFlag, 7 * 24 * 60 * 60);
        response.addHeader("Set-Cookie", cookieValue);

        UserVO userVO = new UserVO();
        userVO.setId(loggedIn.getId());
        userVO.setUsername(loggedIn.getUsername());
        userVO.setNickname(loggedIn.getNickname());
        userVO.setAvatar(loggedIn.getAvatar());
        userVO.setPhone(loggedIn.getPhone());
        userVO.setEmail(loggedIn.getEmail());
        userVO.setGender(loggedIn.getGender());
        userVO.setIntro(loggedIn.getIntro());
        return Result.success(userVO);
    }

    /**
     * 发送绑定/修改邮箱的验证码（含IP和邮箱双重限流）
     * 业务逻辑：获取邮箱和客户端IP → IP限流和邮箱限流检查 → 委托userService发送绑定验证码邮件 → 返回提示
     * 异常场景：邮箱为空返回"请输入QQ邮箱地址"；IP限流返回"验证码发送过于频繁，请N秒后再试"；邮箱限流返回"验证码已发送，请N秒后再试"；发送失败返回具体错误信息
     *
     * @param body 请求体，包含email（QQ邮箱地址，必填）
     * @param request HTTP请求对象（用于获取客户端IP）
     * @return Result.data 为字符串"验证码已发送至 xxx@qq.com"；失败时返回error
     */
    @PostMapping("/send-bind-code")
    public Result<String> sendBindCode(@RequestBody Map<String, String> body, HttpServletRequest request) {
        try {
            String email = body.get("email");
            if (email == null || email.trim().isEmpty()) {
                return Result.error("请输入QQ邮箱地址");
            }
            email = email.trim();

            String clientIp = getClientIp(request);
            String ipKey = RedisConstant.RATELIMIT_SENDCODE_IP_KEY_PREFIX + clientIp;
            if (!rateLimiterUtil.isAllowed(ipKey,
                    RedisConstant.RATELIMIT_SENDCODE_IP_MAX, RedisConstant.RATELIMIT_SENDCODE_IP_WINDOW_SECONDS)) {
                long remain = rateLimiterUtil.getRemainingTTL(ipKey);
                return Result.error("验证码发送过于频繁，请 " + remain + " 秒后再试");
            }

            String emailKey = RedisConstant.RATELIMIT_SENDCODE_EMAIL_KEY_PREFIX + email;
            if (!rateLimiterUtil.isAllowed(emailKey,
                    RedisConstant.RATELIMIT_SENDCODE_EMAIL_MAX, RedisConstant.RATELIMIT_SENDCODE_EMAIL_WINDOW_SECONDS)) {
                long remain = rateLimiterUtil.getRemainingTTL(emailKey);
                return Result.error("验证码已发送，请 " + remain + " 秒后再试");
            }

            String error = userService.sendBindCode(email);
            if (error == null) {
                return Result.success("验证码已发送至 " + email);
            }
            return Result.error(error);
        } catch (Exception e) {
            log.error("发送绑定验证码失败", e);
            return Result.error("验证码发送失败，请稍后重试");
        }
    }

    /**
     * 绑定或修改用户邮箱（需验证码校验）
     * 业务逻辑：从请求头获取userId → 校验email和code非空 → 委托userService校验验证码并绑定邮箱 → 返回提示
     * 异常场景：邮箱为空返回"请输入QQ邮箱地址"；验证码为空返回"请输入验证码"；验证码错误或过期返回具体错误信息
     *
     * @param body 请求体，包含email（QQ邮箱地址，必填）和code（验证码，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为字符串"邮箱绑定成功"；失败时返回error
     */
    @PostMapping("/bind-email")
    public Result<String> bindEmail(@RequestBody Map<String, String> body,
                                     @RequestHeader("X-User-Id") Long userId) {
        try {
            String email = body.get("email");
            String code = body.get("code");
            if (email == null || email.trim().isEmpty()) {
                return Result.error("请输入QQ邮箱地址");
            }
            if (code == null || code.trim().isEmpty()) {
                return Result.error("请输入验证码");
            }

            String error = userService.bindEmail(userId, email.trim(), code.trim());
            if (error == null) {
                return Result.success("邮箱绑定成功");
            }
            return Result.error(error);
        } catch (Exception e) {
            log.error("绑定邮箱失败", e);
            return Result.error("绑定失败，请稍后重试");
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            return xff.split(",")[0].trim();
        }
        String xri = request.getHeader("X-Real-IP");
        if (xri != null && !xri.isEmpty()) {
            return xri.trim();
        }
        return request.getRemoteAddr();
    }
}
