package com.cc.campuscrush.exception;

/**
 * 【TokenExpiredException】异常类
 * &lt;p&gt;核心功能：Token过期异常，继承BaseException，默认提示"登录信息已过期，请重新登录"&lt;/p&gt;
 * &lt;p&gt;使用场景：在JWT Token过期或失效时由UserLoginInterceptor拦截器或JwtUtil工具类抛出，被GlobalExceptionHandler捕获后返回统一错误响应给前端&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public class TokenExpiredException extends BaseException {

    public TokenExpiredException() {
        super("登录信息已过期，请重新登录");
    }

    public TokenExpiredException(String msg) {
        super(msg);
    }

}
