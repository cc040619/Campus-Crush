package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.entity.Comment;
import com.cc.campuscrush.service.CommentService;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * CommentController控制器
 * &lt;p&gt;核心功能：社区帖子评论的增删查改和点赞管理&lt;/p&gt;
 * &lt;p&gt;使用场景：社区帖子详情页的评论功能，支持分页查询评论列表、创建评论、点赞切换和删除评论，被前端帖子详情页和个人评论页调用&lt;/p&gt;
 *
 * @author zcongcong
 * &#064;date  2026-05-27
 */
@RestController
@RequestMapping("/api/community/comment")
@RequiredArgsConstructor
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 分页查询指定帖子的评论列表
     * 业务逻辑：接收帖子ID和分页参数 → 委托commentService分页查询该帖子的评论 → 返回分页结果
     * 异常场景：postId对应的帖子不存在时返回空分页数据
     *
     * @param postId 帖子ID（路径参数，必填）
     * @param pageNum 页码（可选，默认值为1）
     * @param pageSize 每页条数（可选，默认值为20）
     * @return Result.data 为PageInfo分页对象，包含评论列表、总页数、总条数等；无评论时列表为空
     */
    @GetMapping("/list/{postId}")
    public Result<PageInfo<?>> getCommentList(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        var commentList = commentService.getCommentList(postId, pageNum, pageSize);
        return Result.success(commentList);
    }

    /**
     * 创建一条新评论
     * 业务逻辑：从请求头获取userId并注入comment对象 → 委托commentService创建评论 → 返回创建的评论对象
     * 异常场景：userId为空时服务层可能报错（前端请求头必须携带X-User-Id）
     *
     * @param comment 评论请求体，包含postId（关联帖子ID，必填）、content（评论内容，必填）等字段
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为新创建的评论对象，包含id、内容、创建时间等完整信息
     */
    @PostMapping
    public Result<Comment> createComment(@RequestBody Comment comment, @RequestHeader("X-User-Id") Long userId) {
        comment.setUserId(userId);
        var createdComment = commentService.createComment(comment);
        return Result.success(createdComment);
    }

    /**
     * 切换评论的点赞状态（点赞/取消点赞）
     * 业务逻辑：接收评论ID、帖子ID和用户ID → 委托commentService执行点赞切换逻辑 → 返回当前点赞状态
     * 异常场景：评论不存在或已删除时服务层返回false
     *
     * @param id 评论ID（路径参数，必填）
     * @param postId 帖子ID（请求参数，必填，用于关联查询和通知）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为true表示当前已点赞，false表示未点赞
     */
    @PostMapping("/{id}/like")
    public Result<Boolean> likeComment(@PathVariable Long id, @RequestParam Long postId, @RequestHeader("X-User-Id") Long userId) {
        var result = commentService.likeComment(id, postId, userId);
        return Result.success(result);
    }

    /**
     * 查询当前用户对指定评论的点赞状态
     * 业务逻辑：根据评论ID和用户ID → 委托commentService查询该用户是否已点赞该评论 → 返回布尔状态
     * 异常场景：评论不存在时返回false
     *
     * @param id 评论ID（路径参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填）
     * @return Result.data 为true表示已点赞，false表示未点赞
     */
    @GetMapping("/{id}/status")
    public Result<Boolean> getCommentStatus(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        var result = commentService.getCommentStatus(id, userId);
        return Result.success(result);
    }

    /**
     * 删除指定评论（仅允许评论作者删除）
     * 业务逻辑：接收评论ID和用户ID → 委托commentService校验权限并执行删除 → 返回成功
     * 异常场景：评论不存在或非本人评论时服务层会拒绝删除
     *
     * @param id 评论ID（路径参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，必填，用于校验是否为评论作者）
     * @return Result.data 为null，无返回数据
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id, @RequestHeader("X-User-Id") Long userId) {
        commentService.deleteComment(id, userId);
        return Result.success();
    }

}
