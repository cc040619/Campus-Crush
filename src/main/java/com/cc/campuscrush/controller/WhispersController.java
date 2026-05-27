package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.entity.LoveCoupleProfile;
import com.cc.campuscrush.entity.LoveWhisper;
import com.cc.campuscrush.entity.SysUser;
import com.cc.campuscrush.mapper.LoveCoupleProfileMapper;
import com.cc.campuscrush.mapper.SysUserMapper;
import com.cc.campuscrush.service.LoveWhisperService;
import com.cc.campuscrush.websocket.WhisperWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * WhispersController控制器
 * &lt;p&gt;核心功能：情侣间悄悄话联系人列表和聊天记录&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间的私密聊天模块，查询情侣联系人（含在线状态和最后消息）、获取双方聊天历史记录并自动标记已读，通过WebSocket支持实时消息推送，被前端情侣悄悄话页面调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/whisper")
@CrossOrigin
public class WhispersController {

    @Autowired
    private LoveCoupleProfileMapper profileMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private LoveWhisperService whisperService;

    /**
     * 获取悄悄话联系人列表（即伴侣用户，含在线状态和最后一条消息摘要）
     * 业务逻辑：从请求头获取userId → 查询用户情侣档案获取伴侣ID → 查询伴侣用户信息 → 通过WebSocket检查伴侣在线状态 → 查询最后一条聊天消息 → 组装联系人数据 → 返回联系人列表及当前用户信息
     * 异常场景：未登录返回"未登录"错误；未绑定情侣返回"请先在设置中绑定情侣关系"错误；无聊天记录时显示默认提示"开始你们的悄悄话吧~"
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 包含contacts（联系人列表，含id/name/avatar/online/lastMsg/lastTime）、userId、userName、userAvatar字段
     */
    @GetMapping("/contacts")
    public Result<Map<String, Object>> getContacts(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) return Result.error("未登录");

        LoveCoupleProfile profile = profileMapper.findByUserId(userId);
        if (profile == null || profile.getPartnerId() == null) {
            return Result.error("请先在设置中绑定情侣关系");
        }

        Long partnerId = profile.getPartnerId();
        SysUser partner = sysUserMapper.selectById(partnerId);
        SysUser currentUser = sysUserMapper.selectById(userId);

        List<Map<String, Object>> contacts = new ArrayList<>();
        Map<String, Object> contact = new HashMap<>();
        contact.put("id", partnerId);
        contact.put("name", partner != null && partner.getNickname() != null ? partner.getNickname() : "TA");
        contact.put("avatar", partner != null && partner.getAvatar() != null ? partner.getAvatar() : "https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg");
        contact.put("online", WhisperWebSocketHandler.isUserOnline(partnerId));

        // 最后一条消息
        List<LoveWhisper> history = whisperService.getChatHistory(userId, partnerId);
        if (!history.isEmpty()) {
            LoveWhisper lastMsg = history.get(history.size() - 1);
            String txt = lastMsg.getContent();
            contact.put("lastMsg", txt.length() > 20 ? txt.substring(0, 20) + "..." : txt);
            contact.put("lastTime", lastMsg.getCreateTime() != null ? lastMsg.getCreateTime().toString() : "");
        } else {
            contact.put("lastMsg", "开始你们的悄悄话吧~");
            contact.put("lastTime", "");
        }

        contacts.add(contact);

        Map<String, Object> result = new HashMap<>();
        result.put("contacts", contacts);
        result.put("userId", userId);
        result.put("userName", currentUser != null && currentUser.getNickname() != null ? currentUser.getNickname() : "我");
        result.put("userAvatar", currentUser != null && currentUser.getAvatar() != null ? currentUser.getAvatar() : "https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg");
        return Result.success(result);
    }

    /**
     * 获取与伴侣的聊天历史记录，并自动标记消息为已读
     * 业务逻辑：从请求头获取userId → 查询情侣档案获取伴侣ID → 委托whisperService查询双方聊天历史 → 将所有消息标记为已读 → 转换消息格式（增加from字段标识me/partner） → 返回消息列表
     * 异常场景：未登录返回"未登录"错误；未绑定情侣返回"请先在设置中绑定情侣关系"错误；无聊天记录时返回空消息列表
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 包含messages（消息列表，每项含id/fromId/content/time/from）和partnerId字段
     */
    @GetMapping("/history")
    public Result<Map<String, Object>> getHistory(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) return Result.error("未登录");

        LoveCoupleProfile profile = profileMapper.findByUserId(userId);
        if (profile == null || profile.getPartnerId() == null) {
            return Result.error("请先在设置中绑定情侣关系");
        }

        Long partnerId = profile.getPartnerId();
        List<LoveWhisper> messages = whisperService.getChatHistory(userId, partnerId);

        // 标记为已读
        whisperService.markAsRead(userId, partnerId);

        List<Map<String, Object>> msgList = new ArrayList<>();
        for (LoveWhisper msg : messages) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", msg.getId());
            m.put("fromId", msg.getFromUserId());
            m.put("content", msg.getContent());
            m.put("time", msg.getCreateTime() != null ? msg.getCreateTime().toString() : "");
            m.put("from", msg.getFromUserId().equals(userId) ? "me" : "partner");
            msgList.add(m);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("messages", msgList);
        result.put("partnerId", partnerId);
        return Result.success(result);
    }
}
