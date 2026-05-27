package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.LoveCheckin;
import com.cc.campuscrush.entity.LoveCheckinLike;
import com.cc.campuscrush.mapper.LoveCheckinLikeMapper;
import com.cc.campuscrush.mapper.LoveCheckinMapper;
import com.cc.campuscrush.mapper.LoveWeekCheckinMapper;
import com.cc.campuscrush.service.LoveCheckinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 【LoveCheckinServiceImpl】情侣打卡服务层实现
 * &lt;p&gt;核心功能：情侣每日打卡记录的创建查询和打卡互赞功能，同步自动更新本周打卡状态&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间中的每日打卡互动模块，被 LoveCheckinController 调用，创建打卡时自动计算当前周期并 upsert 本周打卡表，支持打卡点赞切换和实时点赞计数更新&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class LoveCheckinServiceImpl implements LoveCheckinService {

    @Autowired
    private LoveCheckinMapper checkinMapper;

    @Autowired
    private LoveCheckinLikeMapper likeMapper;

    @Autowired
    private LoveWeekCheckinMapper weekCheckinMapper;

    /**
     * 获取情侣所有打卡记录（被LoveCheckinController调用）
     * 业务逻辑：按coupleId查love_checkin表返回全量列表
     * 异常场景：无打卡记录时返回空列表
     *
     * @param coupleId 情侣关系ID（必填）
     * @return 打卡记录列表
     */
    @Override
    public List<LoveCheckin> getList(Long coupleId) {
        return checkinMapper.findByCoupleId(coupleId);
    }

    /**
     * 创建打卡记录并自动同步本周打卡状态（事务性，被LoveCheckinController调用）
     * 业务逻辑：构造LoveCheckin对象（昵称默认"小明"、头像默认占位图、images默认空数组）→ 插入打卡记录 → 计算本周起始日期和当前星期几 → upsert本周打卡表对应天为已打卡 → 返回完整打卡记录
     * 异常场景：事务内任何步骤失败均回滚
     *
     * @param coupleId 情侣关系ID（必填）
     * @param userId   打卡用户ID（必填）
     * @param nickname 用户昵称（可选，为null时默认"小明"）
     * @param avatar   用户头像URL（可选，为null时使用默认头像）
     * @param content  打卡内容（必填）
     * @param images   打卡图片JSON（可选，为null时默认"[]"）
     * @return 包含完整信息的打卡记录
     */
    @Override
    @Transactional
    public LoveCheckin create(Long coupleId, Long userId, String nickname, String avatar, String content, String images) {
        LoveCheckin checkin = new LoveCheckin();
        checkin.setCoupleId(coupleId);
        checkin.setUserId(userId);
        checkin.setNickname(nickname != null ? nickname : "小明");
        checkin.setAvatar(avatar != null ? avatar : "https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg");
        checkin.setContent(content);
        checkin.setImages(images != null ? images : "[]");
        checkinMapper.insert(checkin);

        // 自动更新本周打卡状态
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(WeekFields.ISO.getFirstDayOfWeek());
        int dayOfWeek = today.getDayOfWeek().getValue(); // 1=Mon, 7=Sun

        weekCheckinMapper.upsert(coupleId, weekStart, dayOfWeek, true, userId, today);

        return checkinMapper.findById(checkin.getId());
    }

    /**
     * 切换打卡点赞状态（事务性，被LoveCheckinController调用）
     * 业务逻辑：查当前用户是否已点赞该打卡 → 已点赞则删除点赞记录 → 未点赞则插入点赞记录 → 重新统计点赞总数并更新打卡记录的like_count字段 → 返回最新点赞数和点赞状态
     * 异常场景：事务内任何步骤失败均回滚
     *
     * @param recordId 打卡记录ID（必填）
     * @param userId   操作用户ID（必填）
     * @return Map含likeCount（最新点赞数）和liked（当前点赞状态）
     */
    @Override
    @Transactional
    public Map<String, Object> toggleLike(Long recordId, Long userId) {
        LoveCheckinLike existing = likeMapper.findByRecordIdAndUserId(recordId, userId);
        boolean liked;
        if (existing != null) {
            likeMapper.delete(recordId, userId);
            liked = false;
        } else {
            LoveCheckinLike like = new LoveCheckinLike();
            like.setRecordId(recordId);
            like.setUserId(userId);
            likeMapper.insert(like);
            liked = true;
        }
        int count = likeMapper.countByRecordId(recordId);
        checkinMapper.updateLikeCount(recordId, count);

        Map<String, Object> result = new HashMap<>();
        result.put("likeCount", count);
        result.put("liked", liked);
        return result;
    }
}
