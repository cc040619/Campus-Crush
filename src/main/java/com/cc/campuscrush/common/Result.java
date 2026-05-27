package com.cc.campuscrush.common;

import lombok.Data;

/**
 * 【Result】通用类
 * &lt;p&gt;核心功能：统一API响应包装类，封装接口返回的状态码code、提示信息msg和业务数据data&lt;/p&gt;
 * &lt;p&gt;使用场景：所有Controller层接口统一使用此类返回响应数据，被GlobalExceptionHandler全局异常处理器及所有业务Controller调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    /**
     * 创建成功响应（带数据）
     * 业务逻辑：新建Result实例 → 设置状态码200 → 设置提示信息"成功" → 封装业务数据
     *
     * @param <T>  响应数据类型
     * @param data 业务数据（可为null）
     * @return 包含状态码200和业务数据的Result对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("成功");
        result.setData(data);
        return result;
    }

    /**
     * 创建成功响应（无数据）
     * 业务逻辑：委托success(null)，返回仅含状态码200和提示信息的Result，data为null
     *
     * @param <T> 响应数据类型
     * @return 包含状态码200、data为null的Result对象
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 创建错误响应
     * 业务逻辑：新建Result实例 → 设置状态码500 → 设置自定义错误提示信息
     * 异常场景：msg为null时前端展示空字符串
     *
     * @param <T> 响应数据类型
     * @param msg 错误提示信息（必填，展示给前端用户）
     * @return 包含状态码500和错误信息的Result对象（data为null）
     */
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
}
