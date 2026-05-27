package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.Like;
import org.apache.ibatis.annotations.Mapper;

/**
 * LikeMapper数据访问层
 * <p>核心功能：管理用户对帖子及评论的点赞记录，支持点赞、取消点赞、点赞状态查询及按帖子/评论统计点赞数</p>
 * <p>使用场景：帖子点赞、评论点赞、点赞数展示、用户点赞列表，被LikeService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface LikeMapper {

    /**
     * 根据帖子ID、用户ID和点赞类型查询点赞记录
     *
     * @param postId 帖子ID（必填）
     * @param userId 用户ID（必填）
     * @param type   点赞类型（必填）
     * @return 点赞记录实体，无记录时返回null
     */
    Like selectByPostIdAndUserId(Long postId, Long userId, Integer type);

    /**
     * 根据评论ID和用户ID查询评论点赞记录
     *
     * @param commentId 评论ID（必填）
     * @param userId    用户ID（必填）
     * @return 点赞记录实体，无记录时返回null
     */
    Like selectByCommentIdAndUserId(Long commentId, Long userId);

    /**
     * 新增一条点赞记录
     *
     * @param like 点赞实体（必填）
     */
    void insert(Like like);

    /**
     * 根据ID删除点赞记录（取消点赞）
     *
     * @param id 点赞记录ID（必填）
     */
    void deleteById(Long id);

    /**
     * 统计某用户对某类型帖子的点赞数量
     *
     * @param postId 帖子ID（必填）
     * @param userId 用户ID（必填）
     * @param type   点赞类型（必填）
     * @return 点赞数量
     */
    int countByPostIdAndUserId(Long postId, Long userId, Integer type);

    /**
     * 删除指定帖子的所有点赞记录
     *
     * @param postId 帖子ID（必填）
     */
    void deleteByPostId(Long postId);

    /**
     * 删除指定评论的所有点赞记录
     *
     * @param commentId 评论ID（必填）
     */
    void deleteByCommentId(Long commentId);

    /**
     * 统计帖子的点赞总数
     *
     * @param postId 帖子ID（必填）
     * @return 点赞总数
     */
    int countByPostId(Long postId);

    /**
     * 统计某用户的点赞总数
     *
     * @param userId 用户ID（必填）
     * @return 点赞总数
     */
    long countByUserId(Long userId);
}
