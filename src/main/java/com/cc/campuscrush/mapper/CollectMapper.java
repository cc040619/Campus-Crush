package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.Collect;
import org.apache.ibatis.annotations.Mapper;

/**
 * CollectMapper数据访问层
 * <p>核心功能：管理用户对社区帖子的收藏操作，支持收藏、取消收藏、查询收藏状态及收藏数统计</p>
 * <p>使用场景：帖子收藏功能、个人收藏列表、帖子收藏数展示，被CollectService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface CollectMapper {

    /**
     * 根据帖子ID和用户ID查询收藏记录
     *
     * @param postId 帖子ID（必填）
     * @param userId 用户ID（必填）
     * @return 收藏记录实体，无记录时返回null
     */
    Collect selectByPostIdAndUserId(Long postId, Long userId);

    /**
     * 新增一条收藏记录
     *
     * @param collect 收藏实体（必填）
     */
    void insert(Collect collect);

    /**
     * 根据ID删除收藏记录（取消收藏）
     *
     * @param id 收藏记录ID（必填）
     */
    void deleteById(Long id);

    /**
     * 统计某用户对某帖子的收藏数量
     *
     * @param postId 帖子ID（必填）
     * @param userId 用户ID（必填）
     * @return 收藏数量
     */
    int countByPostIdAndUserId(Long postId, Long userId);

    /**
     * 删除指定帖子的所有收藏记录
     *
     * @param postId 帖子ID（必填）
     */
    void deleteByPostId(Long postId);

    /**
     * 统计某用户的收藏总数
     *
     * @param userId 用户ID（必填）
     * @return 收藏总数
     */
    long countByUserId(Long userId);
}
