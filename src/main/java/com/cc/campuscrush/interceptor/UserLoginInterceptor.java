package com.cc.campuscrush.interceptor;

import com.cc.campuscrush.exception.TokenExpiredException;
import com.cc.campuscrush.exception.TokenInvalidException;
import com.cc.campuscrush.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 【UserLoginInterceptor】拦截器
 * &lt;p&gt;核心功能：JWT登录拦截器，从请求Cookie中提取Token并交由JwtUtil验证用户身份，验证通过后将userId存入request属性供后续Controller使用&lt;/p&gt;
 * &lt;p&gt;使用场景：由WebMvcConfiguration注册到拦截器链，拦截/api/**路径下的所有请求（排除登录、注册等公开接口），验证失败时抛出TokenInvalidException或TokenExpiredException&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Component
@Slf4j
public class UserLoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 请求前置拦截——JWT Token验证
     * 业务逻辑：
     * 1. 判断handler是否为HandlerMethod（非静态资源请求），非HandlerMethod直接放行
     * 2. 从请求Cookie中提取名为"token"的JWT Token
     * 3. Token为空 → 抛出TokenInvalidException("Token不能为空")
     * 4. 使用JwtUtil解析Token获取username，再校验签名和过期时间
     * 5. 验证通过 → 提取userId存入request.setAttribute("userId")供Controller使用，返回true放行
     * 异常场景：
     * - Cookie中无token或为空：抛出TokenInvalidException
     * - Token签名无效或已过期：抛出TokenExpiredException
     * - Token解析过程发生其他异常：捕获后抛出TokenInvalidException("Token解析失败")
     *
     * @param request  HTTP请求对象
     * @param response HTTP响应对象
     * @param handler  处理器对象
     * @return true放行，false拦截（异常时直接抛出）
     * @throws TokenInvalidException Token为空或解析失败
     * @throws TokenExpiredException Token无效或已过期
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null || token.isEmpty()) {
            throw new TokenInvalidException("Token不能为空");
        }

        try {
            String username = jwtUtil.extractUsername(token);
            if (!jwtUtil.validateToken(token, username)) {
                throw new TokenExpiredException("Token无效或已过期");
            }

            Long userId = jwtUtil.extractUserId(token);
            request.setAttribute("userId", userId);

            return true;
        } catch (TokenExpiredException | TokenInvalidException e) {
            log.warn("Token验证失败: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.warn("Token验证失败: {}", e.getMessage());
            throw new TokenInvalidException("Token解析失败");
        }
    }

}