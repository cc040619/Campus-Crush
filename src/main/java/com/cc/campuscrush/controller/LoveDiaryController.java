package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.entity.LoveDiary;
import com.cc.campuscrush.service.LoveDiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * LoveDiaryController控制器
 * &lt;p&gt;核心功能：情侣日记的增删查改&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间日记模块，支持日记列表查询、单篇日记详情、新建日记、编辑日记和删除日记，被前端情侣日记页面调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/diary")
@CrossOrigin
public class LoveDiaryController {

    @Autowired
    private LoveDiaryService diaryService;

    private Long getCurrentUserId(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new RuntimeException("未登录");
        }
        return userId;
    }

    /**
     * 查询当前用户的所有日记列表
     * 业务逻辑：从请求头获取userId → 委托diaryService查询该用户的全部日记 → 返回日记列表
     * 异常场景：未登录时getCurrentUserId抛出RuntimeException；无日记时返回空列表
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为LoveDiary列表，无日记时为空数组
     */
    @GetMapping
    public Result<List<LoveDiary>> list(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        return Result.success(diaryService.findAllByUserId(currentUserId));
    }

    /**
     * 根据ID查询单篇日记详情（含权限校验）
     * 业务逻辑：从请求头获取userId → 委托diaryService按id和userId查询 → 判断是否有权访问 → 返回日记详情
     * 异常场景：未登录抛出RuntimeException；日记不存在或不属于当前用户返回"数据不存在或无权限访问"错误
     *
     * @param id 日记ID（路径参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选，用于权限校验）
     * @return Result.data 为LoveDiary对象；无权限时返回error
     */
    @GetMapping("/{id}")
    public Result<LoveDiary> getById(@PathVariable Long id, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        LoveDiary diary = diaryService.findByIdAndUserId(id, currentUserId);
        if (diary == null) {
            return Result.error("数据不存在或无权限访问");
        }
        return Result.success(diary);
    }

    /**
     * 新建一篇日记
     * 业务逻辑：从请求头获取userId并注入diary对象 → 委托diaryService插入数据库 → 返回成功
     * 异常场景：未登录抛出RuntimeException
     *
     * @param diary 日记请求体，包含title（标题，必填）、content（内容，必填）、mood（心情，可选）等字段
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为null，无返回数据
     */
    @PostMapping
    public Result<Void> add(@RequestBody LoveDiary diary, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        diary.setUserId(currentUserId);
        diaryService.insert(diary);
        return Result.success();
    }

    /**
     * 更新指定日记（含权限校验）
     * 业务逻辑：从请求头获取userId → 设置diary的id和userId → 委托diaryService更新 → 校验update返回值
     * 异常场景：未登录抛出RuntimeException；日记不存在或不属于当前用户时返回"数据不存在或无权限修改"错误
     *
     * @param id 日记ID（路径参数，必填）
     * @param diary 日记请求体，包含title、content、mood等需更新的字段
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选，用于权限校验）
     * @return Result.data 为null，无返回数据；无权限时返回error
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody LoveDiary diary, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        diary.setId(id);
        diary.setUserId(currentUserId);
        int result = diaryService.update(diary);
        if (result == 0) {
            return Result.error("数据不存在或无权限修改");
        }
        return Result.success();
    }

    /**
     * 删除指定日记（含权限校验）
     * 业务逻辑：从请求头获取userId → 委托diaryService按id和userId删除 → 校验delete返回值 → 返回成功
     * 异常场景：未登录抛出RuntimeException；日记不存在或不属于当前用户时返回"数据不存在或无权限删除"错误
     *
     * @param id 日记ID（路径参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选，用于权限校验）
     * @return Result.data 为null，无返回数据；无权限时返回error
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        int result = diaryService.deleteByIdAndUserId(id, currentUserId);
        if (result == 0) {
            return Result.error("数据不存在或无权限删除");
        }
        return Result.success();
    }
}
