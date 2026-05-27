package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.Comment;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * CommentMapper数据访问层
 * <p>核心功能：管理社区帖子的评论，支持一级评论和嵌套回复、分页查询、批量删除及评论数统计</p>
 * <p>使用场景：帖子评论区、评论回复、评论列表分页、帖子删除时级联删除评论，被CommentService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface CommentMapper {

    /**
     * 查询帖子的全部一级评论
     *
     * @param postId 帖子ID（必填）
     * @return 该帖子的一级评论列表
     */
    List<Comment> selectByPostId(Long postId);

    /**
     * 根据ID查询单条评论
     *
     * @param id 评论ID（必填）
     * @return 评论实体，无记录时返回null
     */
    Comment selectById(Long id);

    /**
     * 新增一条评论
     *
     * @param comment 评论实体（必填）
     */
    void insert(Comment comment);

    /**
     * 根据ID更新评论内容
     *
     * @param comment 评论实体（必填，需包含id和更新内容）
     */
    void updateById(Comment comment);

    /**
     * 根据ID删除单条评论
     *
     * @param id 评论ID（必填）
     */
    void deleteById(Long id);

    /**
     * 分页查询帖子的评论列表（使用PageHelper分页）
     *
     * @param postId 帖子ID（必填）
     * @return 分页评论列表
     */
    Page<Comment> selectByPostIdWithPage(Long postId);

    /**
     * 删除指定帖子的所有评论
     *
     * @param postId 帖子ID（必填）
     */
    void deleteByPostId(Long postId);

    /**
     * 查询某条评论的所有子回复
     *
     * @param parentId 父评论ID（必填）
     * @return 子回复列表
     */
    List<Comment> selectByParentId(Long parentId);

    /**
     * 删除某条评论的所有子回复
     *
     * @param parentId 父评论ID（必填）
     */
    void deleteByParentId(Long parentId);

    /**
     * 查询帖子的全部评论（含所有层级）
     *
     * @param postId 帖子ID（必填）
     * @return 全部评论列表
     */
    List<Comment> selectByPostIdAll(Long postId);

    /**
     * 统计帖子的评论总数
     *
     * @param postId 帖子ID（必填）
     * @return 评论总数
     */
    int countByPostId(Long postId);

    /**
     * 统计某用户的评论总数
     *
     * @param userId 用户ID（必填）
     * @return 评论总数
     */
    long countByUserId(Long userId);
}
