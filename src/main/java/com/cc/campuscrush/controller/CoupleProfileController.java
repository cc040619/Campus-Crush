package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.entity.LoveCoupleProfile;
import com.cc.campuscrush.entity.SysUser;
import com.cc.campuscrush.mapper.LoveCoupleProfileMapper;
import com.cc.campuscrush.mapper.SysUserMapper;
import com.cc.campuscrush.service.LoveCoupleProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * CoupleProfileController控制器
 * &lt;p&gt;核心功能：情侣信息展示和资料更新&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间首页的数据展示，查询双方昵称头像、计算在一起天数，支持更新恋爱开始日期等资料，被前端情侣主页调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/couple")
@CrossOrigin
public class CoupleProfileController {

    @Autowired
    private LoveCoupleProfileService profileService;

    @Autowired
    private LoveCoupleProfileMapper profileMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 获取当前用户的情侣主页信息（含双方资料和在一起天数）
     * 业务逻辑：从请求头获取userId → 查询用户的情侣档案 → 从sys_user实时获取双方昵称和头像 → 计算在一起天数 → 同步更新profile中的用户信息 → 返回完整的情侣主页数据
     * 异常场景：未登录返回"未登录"错误；未绑定情侣关系时返回hasCouple=false且各项为空字段的默认结构（非error）
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 包含hasCouple、userName、userAvatar、partnerName、partnerAvatar、startDate、daysTogether等字段；未绑定情侣时hasCouple为false
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getInfo(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return Result.error("未登录");
        }

        LoveCoupleProfile profile = profileMapper.findByUserId(userId);
        if (profile == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("hasCouple", false);
            result.put("daysTogether", 0);
            result.put("startDate", "");
            result.put("userName", "");
            result.put("userAvatar", "");
            result.put("partnerName", "");
            result.put("partnerAvatar", "");
            return Result.success(result);
        }

        // 从sys_user获取当前用户实时信息
        SysUser currentUser = sysUserMapper.selectById(userId);
        String userName = currentUser != null && currentUser.getNickname() != null
                ? currentUser.getNickname() : "我";
        String userAvatar = currentUser != null && currentUser.getAvatar() != null
                ? currentUser.getAvatar() : "https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg";

        // 从sys_user获取伴侣实时信息
        Long partnerId = profile.getPartnerId();
        String partnerName = "TA";
        String partnerAvatar = "https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg";
        if (partnerId != null) {
            SysUser partner = sysUserMapper.selectById(partnerId);
            if (partner != null) {
                partnerName = partner.getNickname() != null ? partner.getNickname() : "TA";
                partnerAvatar = partner.getAvatar() != null ? partner.getAvatar() : "https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg";
            }
        }

        LocalDate startDate = profile.getStartDate();
        long daysTogether = startDate != null ? ChronoUnit.DAYS.between(startDate, LocalDate.now()) : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("hasCouple", true);
        result.put("coupleId", profile.getCoupleId());
        result.put("userId", userId);
        result.put("userName", userName);
        result.put("userAvatar", userAvatar);
        result.put("partnerId", partnerId);
        result.put("partnerName", partnerName);
        result.put("partnerAvatar", partnerAvatar);
        result.put("startDate", startDate != null ? startDate.toString() : "");
        result.put("daysTogether", (int) daysTogether);

        // 同步更新profile中的用户信息
        profile.setUserName(userName);
        profile.setUserAvatar(userAvatar);
        if (partnerId != null) {
            profile.setPartnerName(partnerName);
            profile.setPartnerAvatar(partnerAvatar);
        }
        profileService.saveOrUpdate(profile.getCoupleId(), profile);

        return Result.success(result);
    }

    /**
     * 更新情侣档案信息（如开始日期），并同步用户最新昵称和头像
     * 业务逻辑：从请求头获取userId → 查询用户情侣档案 → 更新startDate（如传入）→ 从sys_user同步双方最新昵称和头像 → 委托profileService保存
     * 异常场景：未登录返回"未登录"错误；未绑定情侣关系返回"请先在设置中绑定情侣关系"错误；body中无startDate时不更新日期仅同步用户信息
     *
     * @param body 请求体，可包含startDate字段（开始日期字符串，格式yyyy-MM-dd，可选）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为null，无返回数据
     */
    @PostMapping("/update")
    public Result<Void> update(@RequestBody Map<String, Object> body,
                                @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return Result.error("未登录");
        }

        LoveCoupleProfile profile = profileMapper.findByUserId(userId);
        if (profile == null) {
            return Result.error("请先在设置中绑定情侣关系");
        }

        if (body.containsKey("startDate") && body.get("startDate") != null) {
            String dateStr = body.get("startDate").toString();
            profile.setStartDate(LocalDate.parse(dateStr));
        }

        // 同步最新的用户信息
        SysUser currentUser = sysUserMapper.selectById(userId);
        if (currentUser != null) {
            profile.setUserName(currentUser.getNickname() != null ? currentUser.getNickname() : "我");
            profile.setUserAvatar(currentUser.getAvatar() != null ? currentUser.getAvatar() : "https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg");
        }
        if (profile.getPartnerId() != null) {
            SysUser partner = sysUserMapper.selectById(profile.getPartnerId());
            if (partner != null) {
                profile.setPartnerName(partner.getNickname() != null ? partner.getNickname() : "TA");
                profile.setPartnerAvatar(partner.getAvatar() != null ? partner.getAvatar() : "https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg");
            }
        }

        profileService.saveOrUpdate(profile.getCoupleId(), profile);
        return Result.success();
    }
}
