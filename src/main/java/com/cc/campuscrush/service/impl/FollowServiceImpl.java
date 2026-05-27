package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.Follow;
import com.cc.campuscrush.entity.SysUser;
import com.cc.campuscrush.exception.BaseException;
import com.cc.campuscrush.mapper.FollowMapper;
import com.cc.campuscrush.mapper.PostMapper;
import com.cc.campuscrush.mapper.SysUserMapper;
import com.cc.campuscrush.service.FollowNoticeService;
import com.cc.campuscrush.service.FollowService;
import com.cc.campuscrush.service.ImageCacheService;
import com.cc.campuscrush.vo.UserVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * 【FollowServiceImpl】用户关注服务层实现
 * &lt;p&gt;核心功能：用户关注/取关关系管理、关注列表查询、粉丝列表查询及用户推荐算法&lt;/p&gt;
 * &lt;p&gt;使用场景：社区社交关系的建立与维护，被 FollowController 调用，采用 CompletableFuture 异步并行加载粉丝数、关注数、帖子数等统计信息，支持分页展示和推荐用户排序&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowMapper followMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private FollowNoticeService followNoticeService;
    @Autowired
    @Qualifier("taskExecutor")
    private ExecutorService executor;
    @Autowired
    private ImageCacheService imageCacheService;

    public FollowServiceImpl(FollowMapper followMapper, SysUserMapper sysUserMapper, PostMapper postMapper, FollowNoticeService followNoticeService) {
        this.followMapper = followMapper;
        this.sysUserMapper = sysUserMapper;
        this.postMapper = postMapper;
        this.followNoticeService = followNoticeService;
    }

    /**
     * 关注/取关用户（被FollowController调用）
     * 业务逻辑：校验follower是否存在 → 校验following是否存在（不存在抛BaseException） → 获取关注者昵称和头像 → 查是否已存在关注关系 → 已关注则删除记录并发送取关通知(type=2) → 未关注则插入记录并发送关注通知(type=1)
     * 异常场景：关注者不存在返回false；被关注者不存在抛出BaseException("当前用户已注销或者不存在")
     *
     * @param followerId  关注者用户ID（必填）
     * @param followingId 被关注者用户ID（必填）
     * @return true-关注成功，false-取关成功或关注者不存在
     * @throws BaseException 被关注用户不存在时抛出
     */
    @Override
    public boolean followUser(Long followerId, Long followingId) {
        SysUser follower = sysUserMapper.selectById(followerId);
        if (follower == null) {
            return false;
        }

        // 校验被关注用户是否存在
        SysUser following = sysUserMapper.selectById(followingId);
        if (following == null) {
            throw new BaseException("当前用户已注销或者不存在");
        }

        String followerNickname = follower.getNickname() != null ? follower.getNickname() : follower.getUsername();
        String followerAvatar = imageCacheService.getAvatar(followerId);

        var follow = followMapper.selectByFollowerIdAndFollowingId(followerId, followingId);

        if (follow != null) {
            followMapper.deleteById(follow.getId());
            followNoticeService.createNotice(followingId, followerId, followerNickname, followerAvatar, 2);
            return false;
        } else {
            var newFollow = new Follow();
            newFollow.setFollowerId(followerId);
            newFollow.setFollowingId(followingId);
            followMapper.insert(newFollow);
            followNoticeService.createNotice(followingId, followerId, followerNickname, followerAvatar, 1);
            return true;
        }
    }

    /**
     * 获取用户的关注列表或粉丝列表（被FollowController调用）
     * 业务逻辑：根据isFollowing查关注方向 → 获取用户ID列表 → 若无结果返回空PageInfo → 使用CompletableFuture异步并行加载用户信息、粉丝数、关注数、帖子数 → 对currentUserId标记是否关注每个用户 → 手动分页返回
     * 异常场景：用户ID列表为空返回空PageInfo；异步异常返回空PageInfo
     *
     * @param userId        目标用户ID（必填）
     * @param currentUserId 当前登录用户ID（可选，用于标记关注状态）
     * @param pageNum       页码（必填）
     * @param pageSize      每页条数（必填）
     * @param isFollowing   true-查询关注列表，false-查询粉丝列表
     * @return 用户VO分页对象
     */
    @Override
    public PageInfo<UserVO> getFollowList(Long userId, Long currentUserId, int pageNum, int pageSize, boolean isFollowing) {
        List<Follow> follows = isFollowing ? followMapper.selectByFollowerId(userId) : followMapper.selectByFollowingId(userId);

        List<Long> userIds = follows.stream()
                .map(follow -> isFollowing ? follow.getFollowingId() : follow.getFollowerId())
                .distinct()
                .collect(Collectors.toList());

        if (userIds.isEmpty()) {
            return PageInfo.of(List.of());
        }

        CompletableFuture<List<SysUser>> usersFuture = CompletableFuture.supplyAsync(
                () -> sysUserMapper.selectByIds(userIds), executor);
        CompletableFuture<Map<Long, Integer>> followerCountFuture = CompletableFuture.supplyAsync(
                () -> {
                    Map<Long, Integer> countMap = new HashMap<>();
                    List<Map<String, Object>> counts = followMapper.countFollowerCounts(userIds);
                    counts.forEach(map -> countMap.put((Long) map.get("user_id"), ((Number) map.get("count")).intValue()));
                    return countMap;
                }, executor);
        CompletableFuture<Map<Long, Integer>> followingCountFuture = CompletableFuture.supplyAsync(
                () -> {
                    Map<Long, Integer> countMap = new HashMap<>();
                    List<Map<String, Object>> counts = followMapper.countFollowingCounts(userIds);
                    counts.forEach(map -> countMap.put((Long) map.get("user_id"), ((Number) map.get("count")).intValue()));
                    return countMap;
                }, executor);
        CompletableFuture<Map<Long, Integer>> postCountFuture = CompletableFuture.supplyAsync(
                () -> {
                    Map<Long, Integer> countMap = new HashMap<>();
                    List<Map<String, Object>> counts = postMapper.countPostCounts(userIds);
                    counts.forEach(map -> countMap.put((Long) map.get("user_id"), ((Number) map.get("count")).intValue()));
                    return countMap;
                }, executor);

        CompletableFuture.allOf(usersFuture, followerCountFuture, followingCountFuture, postCountFuture).join();

        try {
            List<SysUser> users = usersFuture.get();
            Map<Long, Integer> followerCounts = followerCountFuture.get();
            Map<Long, Integer> followingCounts = followingCountFuture.get();
            Map<Long, Integer> postCounts = postCountFuture.get();

            Map<Long, Boolean> followingMap = new HashMap<>();
            if (currentUserId != null) {
                List<Long> followingIds = followMapper.selectFollowingIds(currentUserId);
                followingIds.forEach(id -> followingMap.put(id, true));
            }

            List<UserVO> userVOs = users.stream().map(user -> {
                UserVO userVO = new UserVO();
                userVO.setId(user.getId());
                userVO.setUsername(user.getUsername());
                userVO.setNickname(user.getNickname());
                userVO.setAvatar(imageCacheService.getAvatar(user.getId()));
                userVO.setFollowerCount(followerCounts.getOrDefault(user.getId(), 0));
                userVO.setFollowingCount(followingCounts.getOrDefault(user.getId(), 0));
                userVO.setPostCount(postCounts.getOrDefault(user.getId(), 0));
                userVO.setFollowing(followingMap.getOrDefault(user.getId(), false));
                return userVO;
            }).collect(Collectors.toList());

            PageHelper.startPage(pageNum, pageSize);
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, userVOs.size());
            List<UserVO> pageUsers = startIndex < userVOs.size() ? userVOs.subList(startIndex, endIndex) : List.of();

            return PageInfo.of(pageUsers);
        } catch (Exception e) {
            return PageInfo.of(List.of());
        }
    }

    /**
     * 判断两个用户之间是否存在关注关系（被FollowController调用）
     * 业务逻辑：查follow表计数 → count>0返回true
     * 异常场景：无关注关系时返回false
     *
     * @param followerId  关注者ID（必填）
     * @param followingId 被关注者ID（必填）
     * @return true-已关注，false-未关注
     */
    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        int count = followMapper.countByFollowerIdAndFollowingId(followerId, followingId);
        return count > 0;
    }

    /**
     * 获取推荐用户列表（排除自己和已关注用户，按发帖数降序，被FollowController调用）
     * 业务逻辑：CompletableFuture并行查全量用户和已关注ID → 过滤掉自己及已关注用户 → 按用户帖子数降序排序 → 手动分页 → 转换为UserVO（含粉丝数、头像）
     * 异常场景：异步异常返回空PageInfo
     *
     * @param userId   当前用户ID（必填，用于排除已关注和自己）
     * @param pageNum  页码（必填）
     * @param pageSize 每页条数（必填）
     * @return 推荐用户VO分页对象
     */
    @Override
    public PageInfo<UserVO> getRecommendUsers(Long userId, int pageNum, int pageSize) {
        CompletableFuture<List<SysUser>> allUsersFuture = CompletableFuture.supplyAsync(
                () -> sysUserMapper.selectAll(), executor);
        CompletableFuture<List<Long>> followingIdsFuture = CompletableFuture.supplyAsync(
                () -> followMapper.selectFollowingIds(userId), executor);

        CompletableFuture.allOf(allUsersFuture, followingIdsFuture).join();

        try {
            List<SysUser> allUsers = allUsersFuture.get();
            List<Long> followingIds = followingIdsFuture.get();

            Set<Long> followingSet = new HashSet<>(followingIds);
            followingSet.add(userId);

            List<SysUser> recommendUsers = allUsers.stream()
                    .filter(user -> !followingSet.contains(user.getId()))
                    .collect(Collectors.toList());

            CompletableFuture<Map<Long, Integer>> postCountsFuture = CompletableFuture.supplyAsync(() -> {
                Map<Long, Integer> countMap = new HashMap<>();
                List<Long> userIds = recommendUsers.stream().map(SysUser::getId).collect(Collectors.toList());
                if (!userIds.isEmpty()) {
                    List<Map<String, Object>> counts = postMapper.countPostCounts(userIds);
                    counts.forEach(map -> countMap.put((Long) map.get("user_id"), ((Number) map.get("count")).intValue()));
                }
                return countMap;
            }, executor);

            recommendUsers.sort((u1, u2) -> {
                long count1 = postMapper.countByUserId(u1.getId());
                long count2 = postMapper.countByUserId(u2.getId());
                return Long.compare(count2, count1);
            });

            int totalCount = recommendUsers.size();
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalCount);
            List<SysUser> pageUsers = startIndex < totalCount ? recommendUsers.subList(startIndex, endIndex) : List.of();

            Map<Long, Integer> postCounts = postCountsFuture.get();

            List<UserVO> userVOs = pageUsers.stream().map(user -> {
                UserVO userVO = new UserVO();
                userVO.setId(user.getId());
                userVO.setUsername(user.getUsername());
                userVO.setNickname(user.getNickname());
                userVO.setAvatar(imageCacheService.getAvatar(user.getId()));
                long followerCount = followMapper.countByFollowingId(user.getId());
                userVO.setFollowerCount((int) followerCount);
                return userVO;
            }).collect(Collectors.toList());

            PageInfo<UserVO> pageInfo = new PageInfo<>();
            pageInfo.setList(userVOs);
            pageInfo.setTotal(totalCount);
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(pageSize);
            pageInfo.setPages((totalCount + pageSize - 1) / pageSize);
            return pageInfo;
        } catch (Exception e) {
            return PageInfo.of(List.of());
        }
    }

    /**
     * 获取用户关注的人数（被FollowController调用）
     * 业务逻辑：查follow表按followerId计数
     * 异常场景：无记录返回0
     *
     * @param userId 用户ID（必填）
     * @return 关注数量
     */
    @Override
    public int getFollowingCount(Long userId) {
        return Math.toIntExact(followMapper.countByFollowerId(userId));
    }

    /**
     * 获取用户的粉丝数量（被FollowController调用）
     * 业务逻辑：查follow表按followingId计数
     * 异常场景：无记录返回0
     *
     * @param userId 用户ID（必填）
     * @return 粉丝数量
     */
    @Override
    public int getFollowerCount(Long userId) {
        return Math.toIntExact(followMapper.countByFollowingId(userId));
    }
}