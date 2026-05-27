package com.cc.campuscrush.handler;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 【GlobalExceptionHandler】全局异常处理器
 * &lt;p&gt;核心功能：使用@RestControllerAdvice全局拦截Controller层抛出的BaseException业务异常和Exception系统异常，返回统一的Result错误响应&lt;/p&gt;
 * &lt;p&gt;使用场景：统一处理所有Controller抛出的自定义业务异常（返回warn级别日志和友好提示）和未预期系统异常（返回error级别日志和通用错误提示），避免异常堆栈直接暴露给前端&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义业务异常（BaseException）
     * 业务逻辑：捕获BaseException及其子类异常 → 记录warn级别日志（含异常消息） → 返回包含具体错误信息的Result（code=500）
     * 异常场景：将异常消息直接透传给前端，用于友好的业务错误提示（如"用户名已存在"、"Token无效"等）
     *
     * @param e 业务异常对象
     * @return 包含异常提示信息的Result错误响应
     */
    @ExceptionHandler(BaseException.class)
    public Result<?> handleBaseException(BaseException e) {
        logger.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 处理未预期的系统异常（Exception）
     * 业务逻辑：捕获所有未被BaseException处理的异常 → 记录error级别日志（含完整堆栈） → 返回通用错误提示Result（code=500），不暴露内部异常细节
     * 异常场景：兜底处理器，将未预期的系统异常转换为统一的安全提示"系统内部错误，请稍后重试"，避免暴露堆栈信息
     *
     * @param e 系统异常对象
     * @return 包含通用错误提示的Result错误响应
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        logger.error("系统异常:", e);
        return Result.error("系统内部错误，请稍后重试");
    }

}
