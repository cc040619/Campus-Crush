package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.Follow;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * FollowMapper数据访问层
 * <p>核心功能：管理用户间的关注关系，支持关注、取消关注、关注/粉丝列表分页、批量统计关注数和粉丝数</p>
 * <p>使用场景：关注功能、粉丝列表、关注列表、用户主页关注数展示，被FollowService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface FollowMapper {

    /**
     * 根据关注者和被关注者ID查询关注关系
     *
     * @param followerId  关注者ID（必填）
     * @param followingId 被关注者ID（必填）
     * @return 关注关系实体，无记录时返回null
     */
    Follow selectByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * 新增一条关注记录
     *
     * @param follow 关注实体（必填）
     */
    void insert(Follow follow);

    /**
     * 根据ID删除关注记录（取消关注）
     *
     * @param id 关注记录ID（必填）
     */
    void deleteById(Long id);

    /**
     * 分页查询某用户的关注列表（使用PageHelper分页）
     *
     * @param followerId 关注者ID（必填）
     * @return 分页关注列表
     */
    Page<Follow> selectByFollowerId(Long followerId);

    /**
     * 分页查询某用户的粉丝列表（使用PageHelper分页）
     *
     * @param followingId 被关注者ID（必填）
     * @return 分页粉丝列表
     */
    Page<Follow> selectByFollowingId(Long followingId);

    /**
     * 统计关注者与被关注者之间的关注关系数量
     *
     * @param followerId  关注者ID（必填）
     * @param followingId 被关注者ID（必填）
     * @return 关注关系数量（0或1）
     */
    int countByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * 统计某用户的粉丝数量
     *
     * @param followingId 被关注者ID（必填）
     * @return 粉丝总数
     */
    long countByFollowingId(Long followingId);

    /**
     * 统计某用户的关注数量
     *
     * @param followerId 关注者ID（必填）
     * @return 关注总数
     */
    long countByFollowerId(Long followerId);

    /**
     * 批量统计指定用户ID列表的关注数
     *
     * @param userIds 用户ID列表（必填）
     * @return 每个用户的关注数统计，key为user_id
     */
    List<Map<String, Object>> countFollowerCounts(@Param("userIds") List<Long> userIds);

    /**
     * 批量统计指定用户ID列表的粉丝数
     *
     * @param userIds 用户ID列表（必填）
     * @return 每个用户的粉丝数统计，key为user_id
     */
    List<Map<String, Object>> countFollowingCounts(@Param("userIds") List<Long> userIds);

    /**
     * 查询某用户关注的所有用户ID列表
     *
     * @param userId 用户ID（必填）
     * @return 被关注用户ID列表
     */
    List<Long> selectFollowingIds(Long userId);
}
