package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.entity.InteractionNotice;
import com.cc.campuscrush.service.InteractionNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * InteractionNoticeController控制器
 * &lt;p&gt;核心功能：社区互动通知（点赞、评论）的查询和已读管理&lt;/p&gt;
 * &lt;p&gt;使用场景：社区消息通知中心，查询点赞和评论互动通知列表、获取未读数量、逐条或批量标记已读、删除已读通知，被前端通知中心页面调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/community/interaction")
@RequiredArgsConstructor
public class InteractionNoticeController {

    @Autowired
    private InteractionNoticeService interactionNoticeService;

    /**
     * 查询当前用户的互动通知列表（仅类型1点赞和类型2评论，过滤其他类型）
     * 业务逻辑：从请求头获取userId → 查询全部通知 → 过滤type为1或2的通知 → 返回过滤后的列表
     * 异常场景：无通知时返回空列表
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为InteractionNotice列表，仅包含点赞(type=1)和评论(type=2)类型的通知
     */
    @GetMapping("/notices")
    public Result<List<InteractionNotice>> getInteractionNotices(
            @RequestHeader("X-User-Id") Long userId) {
        List<InteractionNotice> allNotices = interactionNoticeService.getNoticesByUserId(userId);
        List<InteractionNotice> filteredNotices = allNotices.stream()
                .filter(n -> n.getType() == 1 || n.getType() == 2)
                .collect(Collectors.toList());
        return Result.success(filteredNotices);
    }

    /**
     * 查询当前用户未读互动通知的数量
     * 业务逻辑：从请求头获取userId → 委托interactionNoticeService统计未读通知数 → 返回计数
     * 异常场景：无未读通知时count为0
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 包含count字段（未读互动通知数量）
     */
    @GetMapping("/notices/count")
    public Result<Map<String, Integer>> getInteractionNoticeCount(
            @RequestHeader("X-User-Id") Long userId) {
        int count = interactionNoticeService.countUnreadNotices(userId);
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }

    /**
     * 将指定互动通知标记为已读
     * 业务逻辑：接收通知ID和用户ID → 委托interactionNoticeService校验权限并标记已读 → 返回成功
     * 异常场景：通知不存在或不属于当前用户时服务层处理
     *
     * @param noticeId 通知ID（路径参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填，用于校验权限）
     * @return Result.data 为null，无返回数据
     */
    @PostMapping("/notices/{noticeId}/read")
    public Result<Void> markNoticeAsRead(
            @PathVariable Long noticeId,
            @RequestHeader("X-User-Id") Long userId) {
        interactionNoticeService.markAsRead(noticeId, userId);
        return Result.success();
    }

    /**
     * 将当前用户所有未读互动通知批量标记为已读
     * 业务逻辑：从请求头获取userId → 委托interactionNoticeService批量标记已读 → 返回成功
     * 异常场景：无未读通知时操作无效果但不报错
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为null，无返回数据
     */
    @PostMapping("/notices/read-all")
    public Result<Void> markAllNoticesAsRead(
            @RequestHeader("X-User-Id") Long userId) {
        interactionNoticeService.markAllAsRead(userId);
        return Result.success();
    }

    /**
     * 查询当前用户的评论通知列表（type=3，对方回复了我的评论/帖子）
     * 业务逻辑：从请求头获取userId → 委托interactionNoticeService按类型3查询通知 → 返回通知列表
     * 异常场景：无通知时返回空列表
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为评论通知列表（type=3）
     */
    @GetMapping("/comment-notices")
    public Result<List<InteractionNotice>> getCommentNotices(
            @RequestHeader("X-User-Id") Long userId) {
        List<InteractionNotice> notices = interactionNoticeService.getNoticesByUserIdAndType(userId, 3L);
        return Result.success(notices);
    }

    /**
     * 查询当前用户未读评论通知的数量（type=3）
     * 业务逻辑：从请求头获取userId → 委托interactionNoticeService统计type=3的未读通知数 → 返回计数
     * 异常场景：无未读评论通知时count为0
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 包含count字段（未读评论通知数量）
     */
    @GetMapping("/comment-notices/count")
    public Result<Map<String, Integer>> getCommentNoticeCount(
            @RequestHeader("X-User-Id") Long userId) {
        int count = interactionNoticeService.countUnreadNoticesByType(userId, 3L);
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }

    /**
     * 将指定评论通知标记为已读
     * 业务逻辑：接收通知ID和用户ID → 委托interactionNoticeService校验权限并标记已读 → 返回成功
     * 异常场景：通知不存在或不属于当前用户时服务层处理
     *
     * @param noticeId 通知ID（路径参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填，用于校验权限）
     * @return Result.data 为null，无返回数据
     */
    @PostMapping("/comment-notices/{noticeId}/read")
    public Result<Void> markCommentNoticeAsRead(
            @PathVariable Long noticeId,
            @RequestHeader("X-User-Id") Long userId) {
        interactionNoticeService.markAsRead(noticeId, userId);
        return Result.success();
    }

    /**
     * 将当前用户所有未读评论通知批量标记为已读（type=3）
     * 业务逻辑：从请求头获取userId → 委托interactionNoticeService按type=3批量标记已读 → 返回成功
     * 异常场景：无未读评论通知时操作无效果但不报错
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为null，无返回数据
     */
    @PostMapping("/comment-notices/read-all")
    public Result<Void> markAllCommentNoticesAsRead(
            @RequestHeader("X-User-Id") Long userId) {
        interactionNoticeService.markAllAsReadByType(userId, 3L);
        return Result.success();
    }

    /**
     * 查询当前用户未读互动通知数量（直接返回整数）
     * 业务逻辑：从请求头获取userId → 委托interactionNoticeService统计未读通知数 → 直接返回数值
     * 异常场景：无未读通知时返回0
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为未读互动通知的数量（整数）
     */
    @GetMapping("/notices/unread/count")
    public Result<Integer> getUnreadInteractionNoticeCount(
            @RequestHeader("X-User-Id") Long userId) {
        int count = interactionNoticeService.countUnreadNotices(userId);
        return Result.success(count);
    }

    /**
     * 删除当前用户所有已读的互动通知（type=1和type=2）
     * 业务逻辑：从请求头获取userId → 分别删除type=1和type=2的已读通知 → 返回成功
     * 异常场景：无已读通知时操作无效果但不报错
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为null，无返回数据
     */
    @DeleteMapping("/notices/read/all")
    public Result<Void> deleteAllReadInteractionNotices(
            @RequestHeader("X-User-Id") Long userId) {
        interactionNoticeService.deleteAllReadNoticesByType(userId, 1L);
        interactionNoticeService.deleteAllReadNoticesByType(userId, 2L);
        return Result.success();
    }

    /**
     * 查询当前用户未读评论通知数量（type=3，直接返回整数）
     * 业务逻辑：从请求头获取userId → 委托interactionNoticeService按type=3统计未读数 → 直接返回数值
     * 异常场景：无未读评论通知时返回0
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为未读评论通知的数量（整数）
     */
    @GetMapping("/comment-notices/unread/count")
    public Result<Integer> getUnreadCommentNoticeCount(
            @RequestHeader("X-User-Id") Long userId) {
        int count = interactionNoticeService.countUnreadNoticesByType(userId, 3L);
        return Result.success(count);
    }

    /**
     * 删除当前用户所有已读的评论通知（type=3）
     * 业务逻辑：从请求头获取userId → 委托interactionNoticeService按type=3删除已读通知 → 返回成功
     * 异常场景：无已读评论通知时操作无效果但不报错
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为null，无返回数据
     */
    @DeleteMapping("/comment-notices/read/all")
    public Result<Void> deleteAllReadCommentNotices(
            @RequestHeader("X-User-Id") Long userId) {
        interactionNoticeService.deleteAllReadNoticesByType(userId, 3L);
        return Result.success();
    }
 }
