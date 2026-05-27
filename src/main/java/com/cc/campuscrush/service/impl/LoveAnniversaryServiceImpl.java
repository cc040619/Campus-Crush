package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.LoveAnniversary;
import com.cc.campuscrush.mapper.LoveAnniversaryMapper;
import com.cc.campuscrush.service.LoveAnniversaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 【LoveAnniversaryServiceImpl】纪念日服务层实现
 * &lt;p&gt;核心功能：情侣纪念日的增删改查、最近纪念日倒计时计算及纪念日类型预设管理&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间中的纪念日模块，被 LoveAnniversaryController 调用，支持自动计算下次纪念日剩余天数（跨年滚动）、类型分类（恋爱纪念日/生日/节日/其他）下拉选项及提醒开关默认值设置&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class LoveAnniversaryServiceImpl implements LoveAnniversaryService {

    @Autowired
    private LoveAnniversaryMapper loveAnniversaryMapper;

    /**
     * 查询用户所有纪念日（被LoveAnniversaryController调用）
     * 业务逻辑：按userId查love_anniversary表返回全量列表
     * 异常场景：无纪念日时返回空列表
     *
     * @param userId 用户ID（必填）
     * @return 纪念日列表
     */
    @Override
    public List<LoveAnniversary> findAllByUserId(Long userId) {
        return loveAnniversaryMapper.findAllByUserId(userId);
    }

    /**
     * 按ID和用户ID精确查询纪念日（被LoveAnniversaryController调用）
     * 业务逻辑：按id和userId联合查询，保证数据归属安全
     * 异常场景：无匹配记录时返回null
     *
     * @param id     纪念日ID（必填）
     * @param userId 用户ID（必填，用于归属校验）
     * @return 纪念日实体，无记录返回null
     */
    @Override
    public LoveAnniversary findByIdAndUserId(Long id, Long userId) {
        return loveAnniversaryMapper.findByIdAndUserId(id, userId);
    }

    /**
     * 新增纪念日（被LoveAnniversaryController调用）
     * 业务逻辑：type为null默认设为"love" → remindEnabled为null默认设为false → 插入MySQL
     * 异常场景：插入失败返回0
     *
     * @param anniversary 纪念日实体（必填）
     * @return 受影响行数
     */
    @Override
    public int insert(LoveAnniversary anniversary) {
        if (anniversary.getType() == null) {
            anniversary.setType("love");
        }
        if (anniversary.getRemindEnabled() == null) {
            anniversary.setRemindEnabled(false);
        }
        return loveAnniversaryMapper.insert(anniversary);
    }

    /**
     * 更新纪念日（被LoveAnniversaryController调用）
     * 业务逻辑：按anniversary实体的id字段更新对应记录
     * 异常场景：记录不存在时返回0
     *
     * @param anniversary 纪念日实体（必填，需含id）
     * @return 受影响行数
     */
    @Override
    public int update(LoveAnniversary anniversary) {
        return loveAnniversaryMapper.update(anniversary);
    }

    /**
     * 删除纪念日（被LoveAnniversaryController调用）
     * 业务逻辑：按id和userId联合删除，保证用户只能删除自己的纪念日
     * 异常场景：无匹配记录时返回0
     *
     * @param id     纪念日ID（必填）
     * @param userId 用户ID（必填，用于归属校验）
     * @return 受影响行数
     */
    @Override
    public int deleteByIdAndUserId(Long id, Long userId) {
        return loveAnniversaryMapper.deleteByIdAndUserId(id, userId);
    }

    /**
     * 统计用户纪念日数量（被LoveAnniversaryController和StatisticsServiceImpl调用）
     * 业务逻辑：按userId计数love_anniversary表记录
     * 异常场景：无纪念日时返回0
     *
     * @param userId 用户ID（必填）
     * @return 纪念日总数
     */
    @Override
    public long countByUserId(Long userId) {
        return loveAnniversaryMapper.countByUserId(userId);
    }

    /**
     * 获取用户最近的下一个纪念日及剩余天数（被LoveAnniversaryController调用）
     * 业务逻辑：查用户所有纪念日 → 忽略年份只看月日 → 计算每个纪念日距离今天的天数（已过则算明年） → 取剩余天数最小的 → 返回id/name/type/daysLeft
     * 异常场景：无纪念日或日期为null时返回null
     *
     * @param userId 用户ID（必填）
     * @return 包含最近纪念日信息的Map，无纪念日时返回null
     */
    @Override
    public Map<String, Object> getNextAnniversary(Long userId) {
        List<LoveAnniversary> list = loveAnniversaryMapper.findAllByUserId(userId);
        if (list.isEmpty()) {
            return null;
        }

        LocalDate today = LocalDate.now();
        LoveAnniversary closest = null;
        long closestDays = Long.MAX_VALUE;

        for (LoveAnniversary item : list) {
            if (item.getDate() == null) continue;
            // 不考虑年份，只看月份和日期
            LocalDate nextDate = LocalDate.of(today.getYear(), item.getDate().getMonth(), item.getDate().getDayOfMonth());
            // 如果今年已经过了，取明年
            if (nextDate.isBefore(today) || nextDate.isEqual(today)) {
                nextDate = nextDate.plusYears(1);
            }
            long daysLeft = ChronoUnit.DAYS.between(today, nextDate);
            if (daysLeft < closestDays) {
                closestDays = daysLeft;
                closest = item;
            }
        }

        if (closest == null) return null;

        Map<String, Object> result = new HashMap<>();
        result.put("id", closest.getId());
        result.put("name", closest.getName());
        result.put("type", closest.getType());
        result.put("daysLeft", closestDays);
        return result;
    }

    /**
     * 获取纪念日类型预设列表（前端下拉选项，被LoveAnniversaryController调用）
     * 业务逻辑：返回固定4种类型：恋爱纪念日(love)、生日(birthday)、节日(festival)、其他(other)，每种含value/label/icon
     * 异常场景：无异常，始终返回固定列表
     *
     * @return 类型预设列表
     */
    @Override
    public List<Map<String, String>> getTypes() {
        List<Map<String, String>> types = new ArrayList<>();

        Map<String, String> love = new HashMap<>();
        love.put("value", "love");
        love.put("label", "恋爱纪念日");
        love.put("icon", "\uD83D\uDC91");
        types.add(love);

        Map<String, String> birthday = new HashMap<>();
        birthday.put("value", "birthday");
        birthday.put("label", "生日");
        birthday.put("icon", "\uD83C\uDF82");
        types.add(birthday);

        Map<String, String> festival = new HashMap<>();
        festival.put("value", "festival");
        festival.put("label", "节日");
        festival.put("icon", "\uD83C\uDF81");
        types.add(festival);

        Map<String, String> other = new HashMap<>();
        other.put("value", "other");
        other.put("label", "其他");
        other.put("icon", "\u2B50");
        types.add(other);

        return types;
    }
}
