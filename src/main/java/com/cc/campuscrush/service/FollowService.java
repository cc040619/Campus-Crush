package com.cc.campuscrush.service;


import com.cc.campuscrush.vo.UserVO;
import com.github.pagehelper.PageInfo;

/**
 * 【FollowService】服务层接口
 * &lt;p&gt;核心功能：提供用户关注/取关、关注列表与粉丝列表分页查询、推荐用户及关注统计功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于用户社交关系建立与发现场景，被FollowController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface FollowService {

    /**
     * 关注或取消关注指定用户
     * 业务逻辑：检查当前关注状态 → 已关注则取消（删除记录），未关注则关注（新增记录）
     * 异常场景：关注自己时返回false；目标用户不存在时返回false
     *
     * @param followerId  关注者用户ID（必填）
     * @param followingId 被关注者用户ID（必填）
     * @return true表示操作成功，false表示操作失败
     */
    boolean followUser(Long followerId, Long followingId);

    /**
     * 分页获取关注列表或粉丝列表
     * 业务逻辑：使用PageHelper分页 → 根据isFollowing参数查询关注列表或粉丝列表 → 封装为UserVO
     * 异常场景：无数据时返回空PageInfo
     *
     * @param userId        目标用户ID（必填，查询该用户的关注/粉丝）
     * @param currentUserId 当前登录用户ID（必填，用于判断关注关系状态）
     * @param pageNum       页码（必填，从1开始）
     * @param pageSize      每页条数（必填）
     * @param isFollowing   true查询关注列表，false查询粉丝列表
     * @return 用户分页数据，无数据时PageInfo的list为空
     */
    PageInfo<UserVO> getFollowList(Long userId, Long currentUserId, int pageNum, int pageSize, boolean isFollowing);

    /**
     * 判断当前用户是否已关注目标用户
     * 业务逻辑：查询关注记录表 → 判断是否存在对应的关注关系
     * 异常场景：任一用户不存在时返回false
     *
     * @param followerId  关注者用户ID（必填）
     * @param followingId 被关注者用户ID（必填）
     * @return true表示已关注，false表示未关注
     */
    boolean isFollowing(Long followerId, Long followingId);

    /**
     * 分页获取推荐用户列表
     * 业务逻辑：使用PageHelper分页 → 基于用户兴趣分类和关注关系推荐相似用户 → 排除已关注和黑名单用户
     * 异常场景：无推荐用户时返回空PageInfo
     *
     * @param userId   当前用户ID（必填，用于个性化推荐）
     * @param pageNum  页码（必填，从1开始）
     * @param pageSize 每页条数（必填）
     * @return 推荐用户分页数据，无数据时PageInfo的list为空
     */
    PageInfo<UserVO> getRecommendUsers(Long userId, int pageNum, int pageSize);

    /**
     * 获取指定用户的关注数量
     * 业务逻辑：统计该用户作为关注者的所有记录 → 返回计数
     * 异常场景：用户不存在时返回0
     *
     * @param userId 用户ID（必填）
     * @return 关注数量，无数据时返回0
     */
    int getFollowingCount(Long userId);

    /**
     * 获取指定用户的粉丝数量
     * 业务逻辑：统计该用户作为被关注者的所有记录 → 返回计数
     * 异常场景：用户不存在时返回0
     *
     * @param userId 用户ID（必填）
     * @return 粉丝数量，无数据时返回0
     */
    int getFollowerCount(Long userId);
}
