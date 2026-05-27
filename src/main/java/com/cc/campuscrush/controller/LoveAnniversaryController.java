package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.entity.LoveAnniversary;
import com.cc.campuscrush.service.LoveAnniversaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * LoveAnniversaryController控制器
 * &lt;p&gt;核心功能：纪念日的增删查改和下一个纪念日查询&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间纪念日模块，支持纪念日列表、详情、新增、编辑、删除、纪念日类型查询和最近纪念日倒计时，被前端纪念日管理页面调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/anniversary")
@CrossOrigin
public class LoveAnniversaryController {

    @Autowired
    private LoveAnniversaryService anniversaryService;

    private Long getCurrentUserId(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new RuntimeException("未登录");
        }
        return userId;
    }

    /**
     * 查询当前用户的所有纪念日列表
     * 业务逻辑：从请求头获取userId → 委托anniversaryService查询该用户的全部纪念日 → 返回列表
     * 异常场景：未登录时getCurrentUserId抛出RuntimeException；无纪念日时返回空列表
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为LoveAnniversary列表，无纪念日时为空数组
     */
    @GetMapping
    public Result<List<LoveAnniversary>> list(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        return Result.success(anniversaryService.findAllByUserId(currentUserId));
    }

    /**
     * 查询当前用户最近的下一个纪念日信息（含倒计时天数）
     * 业务逻辑：从请求头获取userId → 委托anniversaryService计算最近的下一个纪念日 → 返回含倒计时天数的信息
     * 异常场景：未登录抛出RuntimeException；无纪念日时返回空或默认数据
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为Map，包含最近纪念日名称、日期和剩余天数等字段
     */
    @GetMapping("/next")
    public Result<Map<String, Object>> next(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        Map<String, Object> next = anniversaryService.getNextAnniversary(currentUserId);
        return Result.success(next);
    }

    /**
     * 获取所有纪念日类型列表（如生日、恋爱纪念日、结婚纪念日等）
     * 业务逻辑：委托anniversaryService查询预定义的纪念日类型 → 返回类型列表（无需登录）
     * 异常场景：无
     *
     * @return Result.data 为纪念日类型列表，每项包含typeId和typeName
     */
    @GetMapping("/types")
    public Result<List<Map<String, String>>> types() {
        return Result.success(anniversaryService.getTypes());
    }

    /**
     * 根据ID查询单个纪念日详情（含权限校验）
     * 业务逻辑：从请求头获取userId → 委托anniversaryService按id和userId查询 → 判断是否有权访问 → 返回纪念日详情
     * 异常场景：未登录抛出RuntimeException；纪念日不存在或不属于当前用户返回"数据不存在或无权限访问"错误
     *
     * @param id 纪念日ID（路径参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选，用于权限校验）
     * @return Result.data 为LoveAnniversary对象；无权限时返回error
     */
    @GetMapping("/{id}")
    public Result<LoveAnniversary> getById(@PathVariable Long id, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        LoveAnniversary anniversary = anniversaryService.findByIdAndUserId(id, currentUserId);
        if (anniversary == null) {
            return Result.error("数据不存在或无权限访问");
        }
        return Result.success(anniversary);
    }

    /**
     * 新增一个纪念日
     * 业务逻辑：从请求头获取userId并注入anniversary对象 → 委托anniversaryService插入数据库 → 返回成功
     * 异常场景：未登录抛出RuntimeException
     *
     * @param anniversary 纪念日请求体，包含name（名称，必填）、date（日期，必填）、typeId（类型ID，可选）、remindEnabled（是否提醒，可选）、remindDays（提前提醒天数，可选）等字段
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为null，无返回数据
     */
    @PostMapping
    public Result<Void> add(@RequestBody LoveAnniversary anniversary, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        anniversary.setUserId(currentUserId);
        anniversaryService.insert(anniversary);
        return Result.success();
    }

    /**
     * 更新指定纪念日（含权限校验）
     * 业务逻辑：从请求头获取userId → 设置anniversary的id和userId → 委托anniversaryService更新 → 校验update返回值
     * 异常场景：未登录抛出RuntimeException；纪念日不存在或不属于当前用户时返回"数据不存在或无权限修改"错误
     *
     * @param id 纪念日ID（路径参数，必填）
     * @param anniversary 纪念日请求体，包含name、date、typeId等需更新的字段
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选，用于权限校验）
     * @return Result.data 为null，无返回数据；无权限时返回error
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody LoveAnniversary anniversary, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        anniversary.setId(id);
        anniversary.setUserId(currentUserId);
        int result = anniversaryService.update(anniversary);
        if (result == 0) {
            return Result.error("数据不存在或无权限修改");
        }
        return Result.success();
    }

    /**
     * 删除指定纪念日（含权限校验）
     * 业务逻辑：从请求头获取userId → 委托anniversaryService按id和userId删除 → 校验delete返回值 → 返回成功
     * 异常场景：未登录抛出RuntimeException；纪念日不存在或不属于当前用户时返回"数据不存在或无权限删除"错误
     *
     * @param id 纪念日ID（路径参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选，用于权限校验）
     * @return Result.data 为null，无返回数据；无权限时返回error
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        int result = anniversaryService.deleteByIdAndUserId(id, currentUserId);
        if (result == 0) {
            return Result.error("数据不存在或无权限删除");
        }
        return Result.success();
    }
}
