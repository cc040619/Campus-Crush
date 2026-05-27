package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.entity.LoveCoupleProfile;
import com.cc.campuscrush.entity.LoveCoupleRequest;
import com.cc.campuscrush.entity.LoveNotification;
import com.cc.campuscrush.entity.SysUser;
import com.cc.campuscrush.mapper.LoveCoupleProfileMapper;
import com.cc.campuscrush.mapper.LoveCoupleRequestMapper;
import com.cc.campuscrush.mapper.LoveNotificationMapper;
import com.cc.campuscrush.mapper.SysUserMapper;
import com.cc.campuscrush.service.LoveCoupleProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * LoveCoupleController控制器
 * &lt;p&gt;核心功能：情侣绑定申请、审批流程和关系管理&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣绑定设置模块，支持搜索用户、发送情侣申请、查看待处理申请、同意/拒绝申请、自动创建双方情侣档案、解除绑定和通知消息管理，被前端情侣绑定设置页调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/couple")
@CrossOrigin
public class LoveCoupleController {

    @Autowired
    private LoveCoupleProfileService profileService;

    @Autowired
    private LoveCoupleProfileMapper profileMapper;

    @Autowired
    private LoveCoupleRequestMapper requestMapper;

    @Autowired
    private LoveNotificationMapper notificationMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    private Long getCurrentUserId(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new RuntimeException("未登录");
        }
        return userId;
    }

    /**
     * 查询当前用户的情侣关系档案
     * 业务逻辑：从请求头获取userId → 委托profileMapper查询用户的情侣档案 → 返回档案对象（可能为null）
     * 异常场景：未登录抛出RuntimeException；未绑定情侣时返回null（非error）
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为LoveCoupleProfile对象，未绑定时为null
     */
    @GetMapping
    public Result<LoveCoupleProfile> get(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        LoveCoupleProfile profile = profileMapper.findByUserId(currentUserId);
        return Result.success(profile);
    }

    /**
     * 保存或更新当前用户的情侣档案（主要更新开始日期）
     * 业务逻辑：从请求头获取userId → 查询已有的情侣档案 → 如果有则更新startDate → 委托profileService保存
     * 异常场景：未登录抛出RuntimeException；无已有档案时不执行更新（静默返回成功）
     *
     * @param profile 情侣档案请求体，主要使用startDate字段（恋爱开始日期，可选）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为null，无返回数据
     */
    @PostMapping
    public Result<Void> save(@RequestBody LoveCoupleProfile profile, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        LoveCoupleProfile existing = profileMapper.findByUserId(currentUserId);
        if (existing != null) {
            if (profile.getStartDate() != null) {
                existing.setStartDate(profile.getStartDate());
            }
            profileService.saveOrUpdate(existing.getCoupleId(), existing);
        }
        return Result.success();
    }

    /**
     * 按关键词搜索用户（用于查找伴侣，排除自己）
     * 业务逻辑：从请求头获取userId → 校验关键词非空 → 委托sysUserMapper模糊搜索用户 → 过滤掉自己 → 返回用户列表
     * 异常场景：未登录抛出RuntimeException；关键词为空返回"请输入搜索关键词"错误；无匹配结果时返回空列表
     *
     * @param keyword 搜索关键词（请求参数，必填，用于模糊匹配手机号/账号）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为用户列表，每项包含id、username、nickname、phone、avatar
     */
    @GetMapping("/search")
    public Result<List<Map<String, Object>>> searchUser(
            @RequestParam("keyword") String keyword,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.error("请输入搜索关键词");
        }
        List<SysUser> users = sysUserMapper.searchForCouple(keyword.trim());
        List<Map<String, Object>> result = users.stream()
                .filter(u -> !u.getId().equals(currentUserId))
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", u.getId());
                    map.put("username", u.getUsername());
                    map.put("nickname", u.getNickname() != null ? u.getNickname() : "");
                    map.put("phone", u.getPhone() != null ? u.getPhone() : "");
                    map.put("avatar", u.getAvatar() != null ? u.getAvatar() : "https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg");
                    return map;
                })
                .collect(Collectors.toList());
        return Result.success(result);
    }

    // ==================== 情侣申请流程 ====================

    /**
     * 向指定用户发送情侣绑定申请
     * 业务逻辑：从请求头获取userId → 校验partnerUserId不能为自己 → 检查当前用户是否已绑定 → 检查是否已有待处理申请 → 检查是否有对方的反向申请 → 创建申请记录 → 向对方发送通知 → 返回申请ID
     * 异常场景：未登录抛出RuntimeException；partnerUserId为空返回"请指定伴侣用户"；绑定自己返回"不能和自己绑定为情侣"；已绑定返回"你已绑定情侣关系，请先解除"；已有待处理申请返回"已发送过情侣申请，请等待对方处理"；对方已发送申请返回"对方已向你发送了情侣申请，请前往通知处理"；目标用户不存在返回"目标用户不存在"
     *
     * @param body 请求体，包含partnerUserId（伴侣用户ID，必填）和startDate（恋爱开始日期，格式yyyy-MM-dd，可选，默认当天）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 包含requestId（申请ID）和message（成功提示）
     */
    @PostMapping("/request")
    public Result<Map<String, Object>> sendRequest(@RequestBody Map<String, Object> body,
                                                    @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        Long partnerUserId = body.get("partnerUserId") != null
                ? Long.valueOf(body.get("partnerUserId").toString()) : null;
        String startDateStr = body.get("startDate") != null ? body.get("startDate").toString() : null;

        if (partnerUserId == null) {
            return Result.error("请指定伴侣用户");
        }
        if (currentUserId.equals(partnerUserId)) {
            return Result.error("不能和自己绑定为情侣");
        }

        // 检查是否已绑定
        LoveCoupleProfile myProfile = profileMapper.findByUserId(currentUserId);
        if (myProfile != null && myProfile.getPartnerId() != null) {
            return Result.error("你已绑定情侣关系，请先解除");
        }

        // 检查是否已有待处理的申请
        LoveCoupleRequest pending = requestMapper.findPendingBetweenUsers(currentUserId, partnerUserId);
        if (pending != null) {
            return Result.error("已发送过情侣申请，请等待对方处理");
        }

        // 检查对方是否已向自己发送申请
        LoveCoupleRequest reversePending = requestMapper.findPendingBetweenUsers(partnerUserId, currentUserId);
        if (reversePending != null) {
            return Result.error("对方已向你发送了情侣申请，请前往通知处理");
        }

        SysUser currentUser = sysUserMapper.selectById(currentUserId);
        SysUser partnerUser = sysUserMapper.selectById(partnerUserId);
        if (partnerUser == null) {
            return Result.error("目标用户不存在");
        }

        // 创建申请
        LoveCoupleRequest request = new LoveCoupleRequest();
        request.setFromUserId(currentUserId);
        request.setToUserId(partnerUserId);
        request.setStartDate(startDateStr != null && !startDateStr.isEmpty()
                ? LocalDate.parse(startDateStr) : LocalDate.now());
        request.setStatus(0);
        requestMapper.insert(request);

        // 给对方发送通知
        LoveNotification notification = new LoveNotification();
        notification.setUserId(partnerUserId);
        notification.setType("couple_request");
        notification.setFromUserId(currentUserId);
        notification.setFromUserName(currentUser != null && currentUser.getNickname() != null
                ? currentUser.getNickname() : currentUser.getUsername());
        notification.setFromUserAvatar(currentUser != null ? currentUser.getAvatar() : null);
        notification.setContent("向你发送了情侣申请");
        notification.setRelatedId(request.getId());
        notificationMapper.insert(notification);

        Map<String, Object> result = new HashMap<>();
        result.put("requestId", request.getId());
        result.put("message", "情侣申请已发送");
        return Result.success(result);
    }

    /**
     * 查询当前用户收到的待处理（status=0）情侣申请列表
     * 业务逻辑：从请求头获取userId → 查询所有发给该用户的申请 → 过滤status=0的待处理申请 → 返回列表
     * 异常场景：未登录抛出RuntimeException；无待处理申请时返回空列表
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为LoveCoupleRequest列表，仅包含待处理（status=0）的申请
     */
    @GetMapping("/request/pending")
    public Result<List<LoveCoupleRequest>> getPendingRequests(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        List<LoveCoupleRequest> requests = requestMapper.findByToUserId(currentUserId)
                .stream()
                .filter(r -> r.getStatus() == 0)
                .collect(Collectors.toList());
        return Result.success(requests);
    }

    /**
     * 同意情侣申请，为双方创建情侣档案
     * 业务逻辑：从请求头获取userId → 校验requestId和申请合法性 → 标记申请为已接受(status=1) → 获取双方用户信息 → 创建共享coupleId → 分别为双方创建profile记录 → 通知发起者申请已同意
     * 异常场景：未登录抛出RuntimeException；requestId为空返回"请指定申请ID"；申请不存在或不属于当前用户返回"申请不存在"；申请已处理返回"该申请已处理"
     *
     * @param body 请求体，包含requestId（申请ID，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为当前用户的LoveCoupleProfile对象（新创建的情侣档案）
     */
    @PostMapping("/request/accept")
    public Result<LoveCoupleProfile> acceptRequest(@RequestBody Map<String, Object> body,
                                                    @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        Long requestId = body.get("requestId") != null
                ? Long.valueOf(body.get("requestId").toString()) : null;

        if (requestId == null) {
            return Result.error("请指定申请ID");
        }

        LoveCoupleRequest request = requestMapper.findById(requestId);
        if (request == null || !request.getToUserId().equals(currentUserId)) {
            return Result.error("申请不存在");
        }
        if (request.getStatus() != 0) {
            return Result.error("该申请已处理");
        }

        // 标记申请为已接受
        requestMapper.updateStatus(requestId, 1);

        Long partnerUserId = request.getFromUserId();
        SysUser currentUser = sysUserMapper.selectById(currentUserId);
        SysUser partnerUser = sysUserMapper.selectById(partnerUserId);

        String userName = currentUser != null && currentUser.getNickname() != null
                ? currentUser.getNickname() : "我";
        String userAvatar = currentUser != null && currentUser.getAvatar() != null
                ? currentUser.getAvatar() : "https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg";
        String partnerName = partnerUser != null && partnerUser.getNickname() != null
                ? partnerUser.getNickname() : "TA";
        String partnerAvatar = partnerUser != null && partnerUser.getAvatar() != null
                ? partnerUser.getAvatar() : "https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg";

        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();

        // 为双方创建profile记录（共享同一个couple_id）
        Long newCoupleId = System.currentTimeMillis();

        LoveCoupleProfile myProfile = new LoveCoupleProfile();
        myProfile.setCoupleId(newCoupleId);
        myProfile.setUserId(currentUserId);
        myProfile.setPartnerId(partnerUserId);
        myProfile.setUserName(userName);
        myProfile.setUserAvatar(userAvatar);
        myProfile.setPartnerName(partnerName);
        myProfile.setPartnerAvatar(partnerAvatar);
        myProfile.setStartDate(startDate);
        profileService.saveOrUpdate(newCoupleId, myProfile);

        LoveCoupleProfile partnerProfile = new LoveCoupleProfile();
        partnerProfile.setCoupleId(newCoupleId);
        partnerProfile.setUserId(partnerUserId);
        partnerProfile.setPartnerId(currentUserId);
        partnerProfile.setUserName(partnerName);
        partnerProfile.setUserAvatar(partnerAvatar);
        partnerProfile.setPartnerName(userName);
        partnerProfile.setPartnerAvatar(userAvatar);
        if (partnerProfile.getStartDate() == null) {
            partnerProfile.setStartDate(startDate);
        }
        profileService.saveOrUpdate(newCoupleId, partnerProfile);

        // 通知发起者：已同意
        LoveNotification notification = new LoveNotification();
        notification.setUserId(partnerUserId);
        notification.setType("couple_accept");
        notification.setFromUserId(currentUserId);
        notification.setFromUserName(userName);
        notification.setFromUserAvatar(userAvatar);
        notification.setContent("已同意你的情侣申请，你们已经成为情侣啦！");
        notificationMapper.insert(notification);

        return Result.success(myProfile);
    }

    /**
     * 拒绝情侣申请
     * 业务逻辑：从请求头获取userId → 校验requestId和申请合法性 → 标记申请为已拒绝(status=2) → 通知发起者申请被拒绝
     * 异常场景：未登录抛出RuntimeException；requestId为空返回"请指定申请ID"；申请不存在或不属于当前用户返回"申请不存在"；申请已处理返回"该申请已处理"
     *
     * @param body 请求体，包含requestId（申请ID，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为null，无返回数据
     */
    @PostMapping("/request/reject")
    public Result<Void> rejectRequest(@RequestBody Map<String, Object> body,
                                       @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        Long requestId = body.get("requestId") != null
                ? Long.valueOf(body.get("requestId").toString()) : null;

        if (requestId == null) {
            return Result.error("请指定申请ID");
        }

        LoveCoupleRequest request = requestMapper.findById(requestId);
        if (request == null || !request.getToUserId().equals(currentUserId)) {
            return Result.error("申请不存在");
        }
        if (request.getStatus() != 0) {
            return Result.error("该申请已处理");
        }

        // 标记为已拒绝
        requestMapper.updateStatus(requestId, 2);

        SysUser currentUser = sysUserMapper.selectById(currentUserId);

        // 通知发起者：已拒绝
        LoveNotification notification = new LoveNotification();
        notification.setUserId(request.getFromUserId());
        notification.setType("couple_reject");
        notification.setFromUserId(currentUserId);
        notification.setFromUserName(currentUser != null && currentUser.getNickname() != null
                ? currentUser.getNickname() : currentUser.getUsername());
        notification.setFromUserAvatar(currentUser != null ? currentUser.getAvatar() : null);
        notification.setContent("拒绝了你的情侣申请");
        notificationMapper.insert(notification);

        return Result.success();
    }

    // ==================== 通知 ====================

    /**
     * 查询当前用户的情侣相关通知列表
     * 业务逻辑：从请求头获取userId → 委托notificationMapper查询该用户的所有通知 → 返回列表
     * 异常场景：未登录抛出RuntimeException；无通知时返回空列表
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为LoveNotification列表
     */
    @GetMapping("/notifications")
    public Result<List<LoveNotification>> getNotifications(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        return Result.success(notificationMapper.findByUserId(currentUserId));
    }

    /**
     * 查询当前用户未读情侣通知数量
     * 业务逻辑：从请求头获取userId → 委托notificationMapper统计未读通知数 → 返回数值
     * 异常场景：未登录抛出RuntimeException；无未读通知时返回0
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为未读通知数量的整数
     */
    @GetMapping("/notifications/unread-count")
    public Result<Integer> getUnreadCount(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        return Result.success(notificationMapper.countUnread(currentUserId));
    }

    /**
     * 将当前用户所有未读情侣通知批量标记为已读
     * 业务逻辑：从请求头获取userId → 委托notificationMapper批量标记所有通知为已读 → 返回成功
     * 异常场景：未登录抛出RuntimeException；无未读通知时操作无效果但不报错
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为null，无返回数据
     */
    @PostMapping("/notifications/read-all")
    public Result<Void> readAllNotifications(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        notificationMapper.markAllAsRead(currentUserId);
        return Result.success();
    }

    // ==================== 解除绑定 ====================

    /**
     * 解除情侣关系（删除双方的profile记录）
     * 业务逻辑：从请求头获取userId → 查询用户的情侣档案 → 获取partnerId → 删除当前用户和伴侣双方的profile记录 → 返回成功
     * 异常场景：未登录抛出RuntimeException；未绑定情侣关系返回"未绑定情侣关系"错误
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为null，无返回数据
     */
    @PostMapping("/unbind")
    public Result<Void> unbind(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        LoveCoupleProfile myProfile = profileMapper.findByUserId(currentUserId);
        if (myProfile == null) {
            return Result.error("未绑定情侣关系");
        }

        Long partnerId = myProfile.getPartnerId();

        // 删除双方的profile记录
        profileMapper.deleteByUserId(currentUserId);
        if (partnerId != null) {
            profileMapper.deleteByUserId(partnerId);
        }

        return Result.success();
    }
}
