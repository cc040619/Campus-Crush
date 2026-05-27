package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.LoveAnniversary;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * LoveAnniversaryMapper数据访问层
 * <p>核心功能：管理情侣纪念日，支持纪念日的增删改查及按用户维度查询所有纪念日</p>
 * <p>使用场景：情侣空间纪念日功能、重要日期记录与提醒，被LoveAnniversaryService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface LoveAnniversaryMapper {

    /**
     * 查询某用户的所有纪念日
     *
     * @param userId 用户ID（必填）
     * @return 纪念日列表
     */
    List<LoveAnniversary> findAllByUserId(Long userId);

    /**
     * 根据纪念日ID和用户ID查询单个纪念日
     *
     * @param id     纪念日ID（必填）
     * @param userId 用户ID（必填）
     * @return 纪念日实体，无记录时返回null
     */
    LoveAnniversary findByIdAndUserId(Long id, Long userId);

    /**
     * 新增一个纪念日
     *
     * @param anniversary 纪念日实体（必填）
     * @return 受影响行数
     */
    int insert(LoveAnniversary anniversary);

    /**
     * 更新纪念日信息
     *
     * @param anniversary 纪念日实体（必填，需包含id和更新内容）
     * @return 受影响行数
     */
    int update(LoveAnniversary anniversary);

    /**
     * 根据纪念日ID和用户ID删除纪念日
     *
     * @param id     纪念日ID（必填）
     * @param userId 用户ID（必填）
     * @return 受影响行数
     */
    int deleteByIdAndUserId(Long id, Long userId);

    /**
     * 统计某用户的纪念日总数
     *
     * @param userId 用户ID（必填）
     * @return 纪念日总数
     */
    long countByUserId(Long userId);
}
