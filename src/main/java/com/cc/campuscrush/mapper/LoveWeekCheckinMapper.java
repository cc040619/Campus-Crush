package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.LoveWeekCheckin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * LoveWeekCheckinMapper数据访问层
 * <p>核心功能：管理情侣每周打卡任务，支持按周查询打卡状态、单日打卡、更新打卡进度及打卡插入或更新（upsert）</p>
 * <p>使用场景：情侣空间每周打卡挑战、打卡进度追踪，被LoveWeekCheckinService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface LoveWeekCheckinMapper {

    /**
     * 查询某对情侣在某周的全部打卡记录
     *
     * @param coupleId  情侣ID（必填）
     * @param weekStart 周起始日期（必填）
     * @return 该周所有天的打卡记录列表
     */
    List<LoveWeekCheckin> findByCoupleIdAndWeekStart(@Param("coupleId") Long coupleId,
                                                      @Param("weekStart") LocalDate weekStart);

    /**
     * 查询某对情侣在某周某天的单日打卡记录
     *
     * @param coupleId  情侣ID（必填）
     * @param weekStart 周起始日期（必填）
     * @param dayNum    星期几序号（必填）
     * @return 打卡记录实体，无记录时返回null
     */
    LoveWeekCheckin findByCoupleIdAndWeekDay(@Param("coupleId") Long coupleId,
                                              @Param("weekStart") LocalDate weekStart,
                                              @Param("dayNum") Integer dayNum);

    /**
     * 新增一条周打卡记录
     *
     * @param weekCheckin 周打卡实体（必填）
     * @return 受影响行数
     */
    int insert(LoveWeekCheckin weekCheckin);

    /**
     * 更新周打卡记录（打卡/取消打卡）
     *
     * @param coupleId    情侣ID（必填）
     * @param weekStart   周起始日期（必填）
     * @param dayNum      星期几序号（必填）
     * @param checked     是否已打卡（必填）
     * @param userId      打卡用户ID（必填）
     * @param checkinDate 打卡日期（必填）
     * @return 受影响行数
     */
    int update(@Param("coupleId") Long coupleId,
               @Param("weekStart") LocalDate weekStart,
               @Param("dayNum") Integer dayNum,
               @Param("checked") Boolean checked,
               @Param("userId") Long userId,
               @Param("checkinDate") LocalDate checkinDate);

    /**
     * 插入或更新周打卡记录（存在则更新，不存在则插入）
     *
     * @param coupleId    情侣ID（必填）
     * @param weekStart   周起始日期（必填）
     * @param dayNum      星期几序号（必填）
     * @param checked     是否已打卡（必填）
     * @param userId      打卡用户ID（必填）
     * @param checkinDate 打卡日期（必填）
     * @return 受影响行数
     */
    int upsert(@Param("coupleId") Long coupleId,
               @Param("weekStart") LocalDate weekStart,
               @Param("dayNum") Integer dayNum,
               @Param("checked") Boolean checked,
               @Param("userId") Long userId,
               @Param("checkinDate") LocalDate checkinDate);
}
