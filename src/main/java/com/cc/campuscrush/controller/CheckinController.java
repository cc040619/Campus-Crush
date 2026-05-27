package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.entity.LoveCheckin;
import com.cc.campuscrush.entity.LoveCoupleProfile;
import com.cc.campuscrush.entity.SysUser;
import com.cc.campuscrush.mapper.LoveCoupleProfileMapper;
import com.cc.campuscrush.mapper.SysUserMapper;
import com.cc.campuscrush.service.LoveCheckinService;
import com.cc.campuscrush.service.LoveWeekCheckinService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 /**
 * CheckinController控制器
 * &lt;p&gt;核心功能：情侣每日打卡和每周打卡管理&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间模块的打卡功能，支持打卡记录增删查改、点赞切换和本周打卡状态更新，被前端打卡页面调用&lt;/p&gt;
 *
 * @author zcongcong
 * &#064;date  2026-05-27
  */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class CheckinController {

    @Autowired
    private LoveCheckinService checkinService;

    @Autowired
    private LoveWeekCheckinService weekCheckinService;

    @Autowired
    private LoveCoupleProfileMapper profileMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 获取当前用户的情侣ID，未绑定返回null
     */
    private Long getCoupleId(Long userId) {
        if (userId == null) return null;
        LoveCoupleProfile profile = profileMapper.findByUserId(userId);
        return profile != null ? profile.getCoupleId() : null;
    }

    /**
     * 获取当前用户信息（昵称和头像）
     */
    private String[] getUserInfo(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        String nickname = user != null && user.getNickname() != null ? user.getNickname() : "用户";
        String avatar = user != null && user.getAvatar() != null ? user.getAvatar() : "https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg";
        return new String[]{nickname, avatar};
    }

    /**
     * 查询当前用户所在情侣的全部打卡记录列表
     * 业务逻辑：从请求头获取userId → 查询用户绑定的情侣ID → 查询该情侣的全部打卡记录并返回
     * 异常场景：未登录返回"未登录"错误；未绑定情侣关系返回"请先在设置中绑定情侣关系"错误
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选，未传则视为未登录）
     * @return Result.data 包含list字段（打卡记录列表），无打卡记录时list为空数组
     */
    @GetMapping("/checkin/list")
    public Result<Map<String, Object>> getList(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) return Result.error("未登录");
        Long coupleId = getCoupleId(userId);
        if (coupleId == null) return Result.error("请先在设置中绑定情侣关系");
        List<LoveCheckin> list = checkinService.getList(coupleId);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        return Result.success(data);
    }

    /**
     * 创建一条打卡记录
     * 业务逻辑：从请求头获取userId → 验证登录和情侣绑定 → 提取content、images等参数 → 将images列表序列化为JSON → 委托checkinService创建记录并返回
     * 异常场景：未登录返回"未登录"错误；未绑定情侣关系返回"请先在设置中绑定情侣关系"错误；images为空时默认存储空数组"[]"
     *
     * @param body 请求体，包含content（打卡内容，必填）和images（图片URL列表，可选）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为新创建的打卡记录对象
     * @throws JsonProcessingException 图片列表序列化失败时抛出
     */
    @PostMapping("/checkin/create")
    public Result<LoveCheckin> create(@RequestBody Map<String, Object> body,
                                       @RequestHeader(value = "X-User-Id", required = false) Long userId) throws JsonProcessingException {
        if (userId == null) return Result.error("未登录");
        Long coupleId = getCoupleId(userId);
        if (coupleId == null) return Result.error("请先在设置中绑定情侣关系");

        String content = (String) body.get("content");
        String[] userInfo = getUserInfo(userId);
        String nickname = userInfo[0];
        String avatar = userInfo[1];

        // 提取图片列表，转为JSON字符串
        String images = "[]";
        Object imagesObj = body.get("images");
        if (imagesObj instanceof List) {
            images = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(imagesObj);
        }

        LoveCheckin record = checkinService.create(coupleId, userId, nickname, avatar, content, images);
        return Result.success(record);
    }

    /**
     * 切换打卡记录的点赞状态（点赞/取消点赞）
     * 业务逻辑：从请求头获取userId → 从请求体提取recordId → 委托checkinService.toggleLike切换点赞状态并返回新状态
     * 异常场景：未登录返回"未登录"错误
     *
     * @param body 请求体，包含recordId（打卡记录ID，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 包含isLiked（是否已点赞）和likeCount（最新点赞数）两个字段
     */
    @PostMapping("/checkin/like")
    public Result<Map<String, Object>> like(@RequestBody Map<String, Object> body,
                                             @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) return Result.error("未登录");
        Long recordId = Long.valueOf(body.get("recordId").toString());
        Map<String, Object> result = checkinService.toggleLike(recordId, userId);
        return Result.success(result);
    }

    // ==================== 本周打卡 ====================

    /**
     * 查询当前自然周的打卡状态
     * 业务逻辑：从请求头获取userId → 查询用户绑定的情侣ID → 委托weekCheckinService查询本周每天的打卡完成情况
     * 异常场景：未登录返回"未登录"错误；未绑定情侣关系返回"请先在设置中绑定情侣关系"错误
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为本週每天的打卡状态数据（包含周一到周日各天的完成情况）
     */
    @GetMapping("/week/checkin")
    public Result<Map<String, Object>> getWeek(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) return Result.error("未登录");
        Long coupleId = getCoupleId(userId);
        if (coupleId == null) return Result.error("请先在设置中绑定情侣关系");
        return Result.success(weekCheckinService.getCurrentWeek(coupleId));
    }

    /**
     * 更新本周某一天的打卡完成状态
     * 业务逻辑：从请求头获取userId → 验证登录和情侣绑定 → 从请求体提取dayNum → 委托weekCheckinService更新指定天的打卡状态
     * 异常场景：未登录返回"未登录"错误；未绑定情侣关系返回"请先在设置中绑定情侣关系"错误
     *
     * @param body 请求体，包含dayNum（星期几的数字表示，如1=周一，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为null，无返回数据
     */
    @PostMapping("/week/checkin/update")
    public Result<Void> updateWeekDay(@RequestBody Map<String, Object> body,
                                       @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) return Result.error("未登录");
        Long coupleId = getCoupleId(userId);
        if (coupleId == null) return Result.error("请先在设置中绑定情侣关系");
        Integer dayNum = (Integer) body.get("dayNum");
        weekCheckinService.updateDay(coupleId, userId, dayNum);
        return Result.success();
    }
}
