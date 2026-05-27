package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.entity.FollowNotice;
import com.cc.campuscrush.service.FollowNoticeService;
import com.cc.campuscrush.service.FollowService;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FollowController控制器
 * &lt;p&gt;核心功能：用户关注取关、关注列表和关注通知管理&lt;/p&gt;
 * &lt;p&gt;使用场景：社区社交模块的关注功能，支持关注/取消关注、分页查看关注和粉丝列表、检查关注状态、推荐用户和关注通知的已读/删除，被前端用户主页和关注列表页调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/community/follow")
@RequiredArgsConstructor
public class FollowController {

    @Autowired
    private FollowService followService;
    @Autowired
    private FollowNoticeService followNoticeService;

    /**
     * 关注/取消关注指定用户（toggle模式）
     * 业务逻辑：接收当前用户ID和目标用户ID → 委托followService执行关注/取消关注逻辑 → 返回操作后的关注状态
     * 异常场景：不能关注自己（服务层处理）；目标用户不存在时返回false
     *
     * @param followingId 要关注的目标用户ID（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为true表示当前已关注，false表示已取消关注
     */
    @PostMapping
    public Result<Boolean> followUser(
            @RequestParam Long followingId,
            @RequestHeader("X-User-Id") Long userId) {
        var result = followService.followUser(userId, followingId);
        return Result.success(result);
    }

    /**
     * 分页查询指定用户的关注列表或粉丝列表
     * 业务逻辑：根据isFollowing标识判断查询关注列表还是粉丝列表 → 委托followService分页查询并注入当前用户与列表中各用户的关注关系
     * 异常场景：用户无关注或无粉丝时返回空分页数据
     *
     * @param userId 目标用户ID（路径参数，必填）
     * @param pageNum 页码（可选，默认值为1）
     * @param pageSize 每页条数（可选，默认值为20）
     * @param isFollowing true查询该用户关注的人，false查询该用户的粉丝（可选，默认值为true）
     * @param currentUserId 当前登录用户ID（从X-User-Id请求头获取，必填，用于查询相互关注状态）
     * @return Result.data 为PageInfo分页对象，包含用户列表及分页信息
     */
    @GetMapping("/list/{userId}")
    public Result<PageInfo<?>> getFollowList(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "true") boolean isFollowing,
            @RequestHeader("X-User-Id") Long currentUserId) {
        var followList = followService.getFollowList(userId, currentUserId, pageNum, pageSize, isFollowing);
        return Result.success(followList);
    }

    /**
     * 查询当前用户是否已关注指定用户
     * 业务逻辑：接收当前用户ID和目标用户ID → 委托followService查询关注关系 → 返回布尔状态
     * 异常场景：未关注时返回false
     *
     * @param followingId 目标用户ID（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为true表示已关注，false表示未关注
     */
    @GetMapping("/status")
    public Result<Boolean> isFollowing(
            @RequestParam Long followingId,
            @RequestHeader("X-User-Id") Long userId) {
        var result = followService.isFollowing(userId, followingId);
        return Result.success(result);
    }

    /**
     * 分页获取推荐关注用户列表
     * 业务逻辑：接收当前用户ID和分页参数 → 委托followService查询推荐用户 → 未登录时返回空分页数据
     * 异常场景：未登录（userId为null）时返回空PageInfo而非报错
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @param pageNum 页码（可选，默认值为1）
     * @param pageSize 每页条数（可选，默认值为10）
     * @return Result.data 为PageInfo分页对象，包含推荐用户列表；未登录时为空PageInfo
     */
    @GetMapping("/recommend")
    public Result<PageInfo<?>> getRecommendUsers(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        if (userId == null) {
            return Result.success(new PageInfo<>());
        }
        var recommendUsers = followService.getRecommendUsers(userId, pageNum, pageSize);
        return Result.success(recommendUsers);
    }

    /**
     * 查询指定用户的关注数和粉丝数
     * 业务逻辑：根据目标用户ID → 分别查询关注数和粉丝数 → 组装成Map返回
     * 异常场景：用户不存在时两个计数均为0
     *
     * @param userId 目标用户ID（路径参数，必填）
     * @return Result.data 包含followingCount（关注数）和followerCount（粉丝数）两个字段
     */
    @GetMapping("/count/{userId}")
    public Result<java.util.Map<String, Integer>> getFollowCount(
            @PathVariable Long userId) {
        int followingCount = followService.getFollowingCount(userId);
        int followerCount = followService.getFollowerCount(userId);
        java.util.Map<String, Integer> countMap = new java.util.HashMap<>();
        countMap.put("followingCount", followingCount);
        countMap.put("followerCount", followerCount);
        return Result.success(countMap);
    }

    /**
     * 查询当前用户的新关注通知列表
     * 业务逻辑：从请求头获取userId → 委托followNoticeService查询该用户的所有关注通知 → 返回通知列表
     * 异常场景：无通知时返回空列表
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为FollowNotice列表，无通知时为空数组
     */
    @GetMapping("/notices")
    public Result<List<FollowNotice>> getFollowNotices(
            @RequestHeader("X-User-Id") Long userId) {
        List<FollowNotice> notices = followNoticeService.getNoticesByUserId(userId);
        return Result.success(notices);
    }

    /**
     * 将指定关注通知标记为已读
     * 业务逻辑：接收通知ID和用户ID → 委托followNoticeService校验权限并标记已读 → 返回成功
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
        followNoticeService.markAsRead(noticeId, userId);
        return Result.success();
    }

    /**
     * 将当前用户所有未读关注通知批量标记为已读
     * 业务逻辑：从请求头获取userId → 委托followNoticeService批量标记已读 → 返回成功
     * 异常场景：无未读通知时操作无效果但不报错
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为null，无返回数据
     */
    @PostMapping("/notices/read-all")
    public Result<Void> markAllNoticesAsRead(
            @RequestHeader("X-User-Id") Long userId) {
        followNoticeService.markAllAsRead(userId);
        return Result.success();
    }

    /**
     * 查询当前用户的未读关注通知数量
     * 业务逻辑：从请求头获取userId → 委托followNoticeService统计未读通知数 → 返回计数
     * 异常场景：无未读通知时count为0
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 包含count字段（未读通知数量）
     */
    @GetMapping("/notices/count")
    public Result<Map<String, Integer>> getFollowNoticeCount(
            @RequestHeader("X-User-Id") Long userId) {
        int count = followNoticeService.countUnreadNotices(userId);
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }

    /**
     * 查询当前用户未读关注通知数量（直接返回整数）
     * 业务逻辑：从请求头获取userId → 委托followNoticeService统计未读通知数 → 直接返回数值
     * 异常场景：无未读通知时返回0
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为未读关注通知的数量（整数）
     */
    @GetMapping("/notices/unread/count")
    public Result<Integer> getUnreadFollowNoticeCount(
            @RequestHeader("X-User-Id") Long userId) {
        int count = followNoticeService.countUnreadNotices(userId);
        return Result.success(count);
    }

    /**
     * 删除当前用户所有已读的关注通知
     * 业务逻辑：从请求头获取userId → 委托followNoticeService删除所有已读通知 → 返回成功
     * 异常场景：无已读通知时操作无效果但不报错
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为null，无返回数据
     */
    @DeleteMapping("/notices/read/all")
    public Result<Void> deleteAllReadNotices(
            @RequestHeader("X-User-Id") Long userId) {
        followNoticeService.deleteAllReadNotices(userId);
        return Result.success();
    }

}
