package com.cc.campuscrush.service;

import com.cc.campuscrush.entity.Comment;
import com.cc.campuscrush.vo.CommentVO;
import com.github.pagehelper.PageInfo;

/**
 * 【CommentService】服务层接口
 * &lt;p&gt;核心功能：提供帖子评论的分页查询、创建、点赞/取消点赞及删除功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于校园社区帖子评论互动场景，被CommentController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface CommentService {

    /**
     * 分页获取指定帖子的评论列表
     * 业务逻辑：使用PageHelper分页 → 查询帖子下的所有评论 → 封装为CommentVO（含用户信息、点赞状态）
     * 异常场景：帖子无评论时返回空PageInfo
     *
     * @param postId   帖子ID（必填）
     * @param pageNum  页码（必填，从1开始）
     * @param pageSize 每页条数（必填）
     * @return 评论分页数据，无数据时PageInfo的list为空
     */
    PageInfo<CommentVO> getCommentList(Long postId, int pageNum, int pageSize);

    /**
     * 创建一条新评论
     * 业务逻辑：校验评论内容 → 保存Comment实体 → 返回封装了用户信息的CommentVO
     * 异常场景：帖子不存在时抛出异常；内容为空时保存失败
     *
     * @param comment 评论实体（必填，需包含postId、userId、content等字段）
     * @return 创建成功的评论VO对象
     */
    CommentVO createComment(Comment comment);

    /**
     * 点赞或取消点赞一条评论
     * 业务逻辑：检查当前点赞状态 → 已点赞则取消，未点赞则添加 → 更新点赞计数
     * 异常场景：评论不存在时返回false
     *
     * @param commentId 评论ID（必填）
     * @param postId    帖子ID（必填，用于通知关联）
     * @param userId    操作用户ID（必填）
     * @return true表示操作成功，false表示操作失败（如评论不存在）
     */
    boolean likeComment(Long commentId, Long postId, Long userId);

    /**
     * 获取当前用户对指定评论的点赞状态
     * 业务逻辑：查询点赞记录表 → 判断是否存在该用户对该评论的点赞记录
     * 异常场景：评论不存在时返回false
     *
     * @param commentId 评论ID（必填）
     * @param userId    当前用户ID（必填）
     * @return true表示已点赞，false表示未点赞
     */
    boolean getCommentStatus(Long commentId, Long userId);

    /**
     * 删除指定评论
     * 业务逻辑：校验操作权限（仅评论作者可删除） → 删除评论记录及关联的点赞数据
     * 异常场景：评论不存在时静默处理；非作者操作时拒绝删除
     *
     * @param commentId 评论ID（必填）
     * @param userId    操作用户ID（必填，需为评论作者）
     */
    void deleteComment(Long commentId, Long userId);
}
