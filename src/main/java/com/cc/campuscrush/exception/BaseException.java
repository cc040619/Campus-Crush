package com.cc.campuscrush.exception;

/**
 * 【BaseException】异常类
 * &lt;p&gt;核心功能：自定义业务异常基类，继承RuntimeException，提供无参和有参构造器便于子类传递异常消息&lt;/p&gt;
 * &lt;p&gt;使用场景：被TokenExpiredException和TokenInvalidException等具体业务异常类继承，由GlobalExceptionHandler全局异常处理器@ExceptionHandler统一拦截并返回前端友好提示&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public class BaseException extends RuntimeException {

    public BaseException() {
    }

    public BaseException(String msg) {
        super(msg);
    }

}
