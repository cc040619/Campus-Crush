package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.LoveDiary;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * LoveDiaryMapper数据访问层
 * <p>核心功能：管理情侣日记，支持日记的增删改查及按用户维度查询所有日记</p>
 * <p>使用场景：情侣空间日记功能、日记编写/编辑/删除、日记列表浏览，被LoveDiaryService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface LoveDiaryMapper {

    /**
     * 查询某用户的所有日记
     *
     * @param userId 用户ID（必填）
     * @return 日记列表
     */
    List<LoveDiary> findAllByUserId(Long userId);

    /**
     * 根据日记ID和用户ID查询单篇日记
     *
     * @param id     日记ID（必填）
     * @param userId 用户ID（必填）
     * @return 日记实体，无记录时返回null
     */
    LoveDiary findByIdAndUserId(Long id, Long userId);

    /**
     * 新增一篇日记
     *
     * @param diary 日记实体（必填）
     * @return 受影响行数
     */
    int insert(LoveDiary diary);

    /**
     * 更新日记内容
     *
     * @param diary 日记实体（必填，需包含id和更新内容）
     * @return 受影响行数
     */
    int update(LoveDiary diary);

    /**
     * 根据日记ID和用户ID删除日记
     *
     * @param id     日记ID（必填）
     * @param userId 用户ID（必填）
     * @return 受影响行数
     */
    int deleteByIdAndUserId(Long id, Long userId);

    /**
     * 统计某用户的日记总数
     *
     * @param userId 用户ID（必填）
     * @return 日记总数
     */
    long countByUserId(Long userId);
}
