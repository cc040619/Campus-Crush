package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.LoveCheckin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * LoveCheckinMapper数据访问层
 * <p>核心功能：管理情侣每日打卡记录，支持打卡发布、按日期范围查询、打卡天数统计及点赞数更新</p>
 * <p>使用场景：情侣空间每日打卡、打卡日历展示、打卡历史查询，被LoveCheckinService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface LoveCheckinMapper {

    /**
     * 查询某对情侣的全部打卡记录
     *
     * @param coupleId 情侣ID（必填）
     * @return 打卡记录列表
     */
    List<LoveCheckin> findByCoupleId(@Param("coupleId") Long coupleId);

    /**
     * 根据ID查询单条打卡记录
     *
     * @param id 打卡记录ID（必填）
     * @return 打卡记录实体，无记录时返回null
     */
    LoveCheckin findById(@Param("id") Long id);

    /**
     * 新增一条打卡记录
     *
     * @param checkin 打卡实体（必填）
     * @return 受影响行数
     */
    int insert(LoveCheckin checkin);

    /**
     * 更新打卡记录的点赞数
     *
     * @param id        打卡记录ID（必填）
     * @param likeCount 最新点赞数（必填）
     * @return 受影响行数
     */
    int updateLikeCount(@Param("id") Long id, @Param("likeCount") Integer likeCount);

    /**
     * 统计某对情侣的打卡总天数
     *
     * @param coupleId 情侣ID（必填）
     * @return 打卡总天数
     */
    int countByCoupleId(@Param("coupleId") Long coupleId);

    /**
     * 按日期范围查询某对情侣的打卡记录
     *
     * @param coupleId  情侣ID（必填）
     * @param startDate 开始日期（必填）
     * @param endDate   结束日期（必填）
     * @return 指定日期范围内的打卡记录列表
     */
    List<LoveCheckin> findByCoupleIdAndDateRange(@Param("coupleId") Long coupleId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    /**
     * 统计某对情侣在某日期的打卡记录数量
     *
     * @param coupleId 情侣ID（必填）
     * @param date     日期（必填）
     * @return 当日打卡记录数量
     */
    int countByCoupleIdAndDate(@Param("coupleId") Long coupleId,
                               @Param("date") LocalDate date);
}
