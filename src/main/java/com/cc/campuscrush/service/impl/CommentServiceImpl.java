package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.Comment;
import com.cc.campuscrush.entity.Like;
import com.cc.campuscrush.entity.Post;
import com.cc.campuscrush.entity.SysUser;
import com.cc.campuscrush.mapper.CommentMapper;
import com.cc.campuscrush.mapper.LikeMapper;
import com.cc.campuscrush.mapper.PostMapper;
import com.cc.campuscrush.mapper.SysUserMapper;
import com.cc.campuscrush.service.CommentService;
import com.cc.campuscrush.service.ImageCacheService;
import com.cc.campuscrush.service.InteractionNoticeService;
import com.cc.campuscrush.utils.RedisContext;
import com.cc.campuscrush.vo.CommentVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.cc.campuscrush.common.RedisConstant.CACHE_NULL_TTL_MINUTES;
import static com.cc.campuscrush.common.RedisConstant.CACHE_TTL_MINUTES;
import static com.cc.campuscrush.common.RedisConstant.COMMENT_DETAIL_KEY_PREFIX;
import static com.cc.campuscrush.common.RedisConstant.COMMENT_LIST_KEY_PREFIX;

/**
 * 【CommentServiceImpl】帖子评论服务层实现
 * &lt;p&gt;核心功能：帖子评论的增删查改、嵌套子评论、点赞切换及评论列表 Redis 缓存管理&lt;/p&gt;
 * &lt;p&gt;使用场景：社区帖子详情页的评论互动，被 CommentController 调用，支持分页评论列表缓存、父子评论递归删除、点赞通知触发，整合缓存防穿透策略&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private LikeMapper likeMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private InteractionNoticeService interactionNoticeService;
    @Autowired
    private RedisContext redisContext;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ImageCacheService imageCacheService;


    /**
     * 获取帖子的分页评论列表（含嵌套子评论，被CommentController调用）
     * 业务逻辑：构造缓存key → 查Redis缓存 → 命中且为NULL标记则返回空 → 命中且有效则反序列化PageInfo返回 → 未命中则PageHelper分页查MySQL → 转换为VO并加载每个评论的子评论 → 写入Redis缓存 → 返回分页结果
     * 异常场景：JSON反序列化失败回退查DB；空数据写入"NULL"标记防穿透；缓存写入失败不影响业务
     *
     * @param postId   帖子ID（必填）
     * @param pageNum  页码（必填，从1开始）
     * @param pageSize 每页条数（必填）
     * @return 评论VO分页对象，无数据时返回空的PageInfo
     */
    @Override
    public PageInfo<CommentVO> getCommentList(Long postId, int pageNum, int pageSize) {
        String cacheKey = COMMENT_LIST_KEY_PREFIX + postId + ":" + pageNum + ":" + pageSize;
        
        // 先查Redis缓存
        Object cachedObj = redisContext.get(cacheKey);
        String cachedData = cachedObj != null ? cachedObj.toString() : null;
        if (cachedData != null) {
            if ("NULL".equals(cachedData)) {
                return new PageInfo<>();
            }
            try {
                return objectMapper.readValue(cachedData, new TypeReference<PageInfo<CommentVO>>() {});
            } catch (JsonProcessingException e) {
                // 缓存数据格式错误，继续查DB
            }
        }

        // Redis未命中，查询数据库
        PageHelper.startPage(pageNum, pageSize);
        var comments = commentMapper.selectByPostIdWithPage(postId);
        
        if (comments == null || comments.isEmpty()) {
            redisContext.setEx(cacheKey, "NULL", CACHE_NULL_TTL_MINUTES * 60);
            return new PageInfo<>();
        }

        // 转换为VO并加载子评论
        List<CommentVO> commentVOs = comments.stream().map(comment -> {
            CommentVO vo = convertToVO(comment);
            // 加载子评论
            List<Comment> childComments = commentMapper.selectByParentId(comment.getId());
            if (childComments != null && !childComments.isEmpty()) {
                List<CommentVO> childVOs = childComments.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());
                vo.setChildren(childVOs);
            }
            return vo;
        }).collect(Collectors.toList());
        
        PageInfo<CommentVO> pageInfo = PageInfo.of(commentVOs);

        // 写入Redis缓存
        try {
            String jsonData = objectMapper.writeValueAsString(pageInfo);
            redisContext.setEx(cacheKey, jsonData, CACHE_TTL_MINUTES * 60);
        } catch (JsonProcessingException e) {
            // 缓存写入失败不影响业务
        }

        return pageInfo;
    }

    /**
     * 创建帖子评论（事务性，被CommentController调用）
     * 业务逻辑：插入评论到MySQL → 查询帖子作者 → 若评论者非帖子作者则异步发送互动通知（类型3=评论） → 清除帖子所有评论列表缓存 → 返回评论VO
     * 异常场景：事务回滚时评论和缓存清除均不生效；通知发送使用异步异步方式不阻塞主流程
     *
     * @param comment 评论实体（必填，含postId、userId、content、parentId等）
     * @return 包含用户昵称和头像的评论VO
     */
    @Override
    @Transactional
    public CommentVO createComment(Comment comment) {
        commentMapper.insert(comment);
        
        Post post = postMapper.selectById(comment.getPostId());
        if (post != null && !post.getUserId().equals(comment.getUserId())) {
            SysUser commentUser = sysUserMapper.selectById(comment.getUserId());
            String nickname = commentUser != null && commentUser.getNickname() != null 
                ? commentUser.getNickname() : commentUser.getUsername();
            String avatar = imageCacheService.getAvatar(comment.getUserId());
            
            interactionNoticeService.createNotice(
                post.getUserId(),
                comment.getUserId(),
                nickname,
                avatar,
                comment.getPostId(),
                post.getTitle(),
                3
            );
        }
        
        deleteCommentCache(comment.getPostId());
        return convertToVO(comment);
    }

    /**
     * 切换评论点赞状态（事务性，被CommentController调用）
     * 业务逻辑：查当前用户是否已点赞该评论 → 已点赞则删除点赞记录并点赞数-1（最小0） → 未点赞则插入点赞记录并点赞数+1 → 更新评论实体 → 清除评论列表缓存
     * 异常场景：事务内任何步骤失败均回滚
     *
     * @param commentId 评论ID（必填）
     * @param postId    帖子ID（必填，用于点赞记录关联）
     * @param userId    操作用户ID（必填）
     * @return true-已点赞（点赞后状态），false-已取消点赞
     */
    @Override
    @Transactional
    public boolean likeComment(Long commentId, Long postId, Long userId) {
        var like = likeMapper.selectByCommentIdAndUserId(commentId, userId);
        if (like != null) {
            likeMapper.deleteById(like.getId());
            var comment = commentMapper.selectById(commentId);
            comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
            commentMapper.updateById(comment);
            updateCommentLikeCountInCache(commentId, comment.getLikeCount());
            return false;
        } else {
            var newLike = new Like();
            newLike.setPostId(postId);
            newLike.setCommentId(commentId);
            newLike.setUserId(userId);
            newLike.setType(2);
            likeMapper.insert(newLike);
            var comment = commentMapper.selectById(commentId);
            comment.setLikeCount(comment.getLikeCount() + 1);
            commentMapper.updateById(comment);
            updateCommentLikeCountInCache(commentId, comment.getLikeCount());
            return true;
        }
    }

    /**
     * 查询当前用户是否已点赞某条评论（被CommentController调用）
     * 业务逻辑：查like表中commentId和userId的关联记录 → 存在则返回true
     * 异常场景：无记录时返回false
     *
     * @param commentId 评论ID（必填）
     * @param userId    用户ID（必填）
     * @return true-已点赞，false-未点赞
     */
    @Override
    public boolean getCommentStatus(Long commentId, Long userId) {
        var like = likeMapper.selectByCommentIdAndUserId(commentId, userId);
        return like != null;
    }

    /**
     * 删除评论及其所有子评论（事务性，被CommentController调用）
     * 业务逻辑：检查评论是否存在 → 校验是否为评论作者（非作者抛出异常） → 递归删除所有子评论及其点赞 → 删除该评论所有点赞 → 删除评论本身 → 清除帖子相关缓存
     * 异常场景：评论不存在时静默返回；非评论作者删除时抛出RuntimeException("无权限删除此评论")
     *
     * @param commentId 评论ID（必填）
     * @param userId    操作用户ID（必填，必须为评论作者）
     * @throws RuntimeException 非评论作者时抛出
     */
    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        // 检查评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            return;
        }

        // 检查是否是评论作者
        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此评论");
        }
        Long postId = comment.getPostId();
        // 删除该评论的所有子评论
        deleteCommentAndReplies(commentId);
        // 删除该评论的所有点赞
        likeMapper.deleteByPostId(commentId);
        // 删除评论本身
        commentMapper.deleteById(commentId);
        // 删除相关缓存
        deleteCommentCache(postId);
    }

    private void deleteCommentAndReplies(Long parentId) {
        // 获取所有子评论
        List<Comment> childComments = commentMapper.selectByParentId(parentId);
        
        // 递归删除子评论及其回复
        for (Comment child : childComments) {
            deleteCommentAndReplies(child.getId());
            // 删除子评论的点赞
            likeMapper.deleteByPostId(child.getId());
            // 删除子评论
            commentMapper.deleteById(child.getId());
        }
        
        // 删除直接子评论（可选，因为上面已经递归删除了）
        commentMapper.deleteByParentId(parentId);
    }

    private void deleteCommentCache(Long postId) {
        // 删除帖子的所有评论列表缓存
        redisContext.deletePattern(COMMENT_LIST_KEY_PREFIX + postId + ":*");
        // 删除所有评论详情缓存（如果有的话）
        redisContext.deletePattern(COMMENT_DETAIL_KEY_PREFIX + "*");
    }

    private void updateCommentLikeCountInCache(Long commentId, int newCount) {
        // 这里可以扩展更新缓存中的点赞数
        // 为了简化，可以选择直接删除相关缓存，让下次查询重新加载
        redisContext.deletePattern(COMMENT_LIST_KEY_PREFIX + "*");
    }

    private CommentVO convertToVO(Comment comment) {
        CommentVO commentVO = new CommentVO();
        commentVO.setId(comment.getId());
        commentVO.setPostId(comment.getPostId());
        commentVO.setUserId(comment.getUserId());
        commentVO.setParentId(comment.getParentId());
        commentVO.setContent(comment.getContent());
        commentVO.setCreateTime(comment.getCreateTime());
        commentVO.setLikeCount(comment.getLikeCount());
        
        SysUser user = sysUserMapper.selectById(comment.getUserId());
        if (user != null) {
            commentVO.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
            commentVO.setAvatar(imageCacheService.getAvatar(comment.getUserId()));
        } else {
            commentVO.setUserName("未知用户");
            commentVO.setAvatar(null);
        }
        return commentVO;
    }
}
