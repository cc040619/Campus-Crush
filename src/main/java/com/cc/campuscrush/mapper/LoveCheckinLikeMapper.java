package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.LoveCheckinLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * LoveCheckinLikeMapper数据访问层
 * <p>核心功能：管理情侣打卡记录的点赞，支持点赞、取消点赞及打卡记录点赞数统计</p>
 * <p>使用场景：情侣空间打卡点赞、打卡动态互动，被LoveCheckinLikeService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface LoveCheckinLikeMapper {

    /**
     * 根据打卡记录ID和用户ID查询点赞记录
     *
     * @param recordId 打卡记录ID（必填）
     * @param userId   用户ID（必填）
     * @return 点赞记录实体，无记录时返回null
     */
    LoveCheckinLike findByRecordIdAndUserId(@Param("recordId") Long recordId,
                                            @Param("userId") Long userId);

    /**
     * 新增一条打卡点赞记录
     *
     * @param like 打卡点赞实体（必填）
     * @return 受影响行数
     */
    int insert(LoveCheckinLike like);

    /**
     * 删除打卡点赞记录（取消点赞）
     *
     * @param recordId 打卡记录ID（必填）
     * @param userId   用户ID（必填）
     * @return 受影响行数
     */
    int delete(@Param("recordId") Long recordId, @Param("userId") Long userId);

    /**
     * 统计某打卡记录的点赞总数
     *
     * @param recordId 打卡记录ID（必填）
     * @return 点赞总数
     */
    int countByRecordId(@Param("recordId") Long recordId);
}
