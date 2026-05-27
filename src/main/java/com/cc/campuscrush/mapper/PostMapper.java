package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.Post;
import com.cc.campuscrush.entity.PostQueryCondition;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * PostMapper数据访问层
 * <p>核心功能：管理社区帖子全生命周期，支持帖子CRUD、多条件筛选搜索、浏览记录追踪、热门话题统计及用户维度数据聚合</p>
 * <p>使用场景：社区帖子发布/编辑/删除、帖子广场浏览、帖子搜索与筛选、个人主页帖子列表、浏览/点赞/收藏数据统计，被PostService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface PostMapper {

    /**
     * 查询全部帖子列表（SELECT）
     *
     * @return 全部帖子列表
     */
    List<Post> selectAll();

    /**
     * 根据ID查询单条帖子（SELECT）
     *
     * @param id 帖子ID（必填）
     * @return 帖子实体，无记录时返回null
     */
    Post selectById(Long id);

    /**
     * 新增一条帖子（INSERT）
     *
     * @param post 帖子实体（必填）
     */
    void insert(Post post);

    /**
     * 根据ID更新帖子信息（UPDATE）
     *
     * @param post 帖子实体（必填，需包含id和更新内容）
     */
    void updateById(Post post);

    /**
     * 根据ID删除帖子（DELETE）
     *
     * @param id 帖子ID（必填）
     */
    void deleteById(Long id);

    /**
     * 根据多条件筛选查询帖子列表（SELECT）
     *
     * @param condition 查询条件对象（必填）
     * @return 符合条件的帖子列表
     */
    List<Post> selectByCondition(PostQueryCondition condition);

    /**
     * 查询某用户发布的所有帖子（SELECT）
     *
     * @param userId 用户ID（必填）
     * @return 帖子列表
     */
    List<Post> selectByUserId(Long userId);

    /**
     * 查询某用户收藏的所有帖子（SELECT）
     *
     * @param userId 用户ID（必填）
     * @return 收藏的帖子列表
     */
    List<Post> selectByCollectionUserId(Long userId);

    /**
     * 查询某用户点赞的所有帖子（SELECT）
     *
     * @param userId 用户ID（必填）
     * @return 点赞的帖子列表
     */
    List<Post> selectByLikeUserId(Long userId);

    /**
     * 统计某用户的帖子总数
     *
     * @param userId 用户ID（必填）
     * @return 帖子总数
     */
    long countByUserId(Long userId);

    /**
     * 统计某用户的帖子总数
     *
     * @param userId 用户ID（必填）
     * @return 帖子总数
     */
    long getPostCountByUserId(Long userId);

    /**
     * 批量统计指定用户ID列表的帖子数量（SELECT）
     *
     * @param userIds 用户ID列表（必填）
     * @return 每个用户的帖子数统计，key为user_id
     */
    @MapKey("user_id")
    List<Map<String, Object>> countPostCounts(@Param("userIds") List<Long> userIds);

    /**
     * 统计某用户在某日发布的帖子数量
     *
     * @param userId 用户ID（必填）
     * @param date   日期（必填）
     * @return 当日帖子数量
     */
    long countByUserIdAndDate(@Param("userId") Long userId, @Param("date") java.time.LocalDate date);

    /**
     * 统计某用户在某年某月发布的帖子数量
     *
     * @param userId 用户ID（必填）
     * @param year   年份（必填）
     * @param month  月份（必填）
     * @return 当月帖子数量
     */
    long countByUserIdAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    /**
     * 统计某用户在某分类下发布的帖子数量
     *
     * @param userId   用户ID（必填）
     * @param category 分类名称（必填）
     * @return 该分类下帖子数量
     */
    long countByUserIdAndCategory(@Param("userId") Long userId, @Param("category") String category);

    /**
     * 递增帖子的浏览数（UPDATE）
     *
     * @param id 帖子ID（必填）
     */
    void incrementBrowseCount(Long id);

    /**
     * 新增帖子浏览记录（INSERT）
     *
     * @param postId 帖子ID（必填）
     * @param userId 浏览用户ID（必填）
     */
    @Insert("INSERT INTO t_post_browse (post_id, user_id, browse_time) VALUES (#{postId}, #{userId}, NOW())")
    void insertBrowseRecord(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 统计某用户帖子的每日浏览数（SELECT）
     *
     * @param userId    帖子作者用户ID（必填）
     * @param startDate 统计开始日期（必填）
     * @param endDate   统计结束日期（必填）
     * @return 每日浏览数统计列表，key包含date和count
     */
    @Select("SELECT DATE(b.browse_time) AS date, COUNT(*) AS count FROM t_post_browse b JOIN t_post p ON b.post_id = p.id WHERE p.user_id = #{userId} AND DATE(b.browse_time) >= #{startDate} AND DATE(b.browse_time) <= #{endDate} GROUP BY DATE(b.browse_time) ORDER BY DATE(b.browse_time)")
    List<Map<String, Object>> countDailyBrowseByUserId(@Param("userId") Long userId, @Param("startDate") java.time.LocalDate startDate, @Param("endDate") java.time.LocalDate endDate);

    /**
     * 根据关键词搜索帖子，按评论数排序（SELECT）
     *
     * @param keyword 搜索关键词（必填）
     * @return 按评论数排序的帖子列表
     */
    List<Post> searchByKeywordOrderByComment(@Param("keyword") String keyword);

    /**
     * 获取热门话题统计（SELECT）
     *
     * @return 热门话题列表，key为categories
     */
    @MapKey("categories")
    List<Map<String, Object>> getHotTopics();

    /**
     * 统计某用户所有帖子的点赞数和收藏数总和
     *
     * @param userId 用户ID（必填）
     * @return 点赞数与收藏数之和
     */
    long sumLikeAndCollectCountByUserId(Long userId);

    /**
     * 根据条件筛选包含可见性判断的帖子列表（SELECT）
     *
     * @param condition 查询条件对象（必填）
     * @return 符合条件的帖子列表（含可见性过滤）
     */
    List<Post> selectWithVisibility(@Param("condition") PostQueryCondition condition);

    /**
     * 查询某用户的帖子列表，同时判断当前登录用户对帖子的可见性（SELECT）
     *
     * @param userId        帖子作者用户ID（必填）
     * @param currentUserId 当前登录用户ID（必填）
     * @return 帖子列表（含可见性字段）
     */
    List<Post> selectByUserIdWithVisibility(@Param("userId") Long userId, @Param("currentUserId") Long currentUserId);
}
