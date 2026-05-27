package com.cc.campuscrush.exception;

/**
 * 【TokenInvalidException】异常类
 * &lt;p&gt;核心功能：Token无效异常，继承BaseException，默认提示"Token无效，请重新登录"&lt;/p&gt;
 * &lt;p&gt;使用场景：在JWT Token为空、格式错误或解析失败时由UserLoginInterceptor拦截器抛出，被GlobalExceptionHandler捕获后返回统一错误响应给前端&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public class TokenInvalidException extends BaseException {

    public TokenInvalidException() {
        super("Token无效，请重新登录");
    }

    public TokenInvalidException(String msg) {
        super(msg);
    }

}
