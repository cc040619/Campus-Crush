package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.LoveAnniversary;
import com.cc.campuscrush.entity.LoveCoupleProfile;
import com.cc.campuscrush.mapper.*;
import com.cc.campuscrush.service.StatisticsService;

import java.util.HashMap;
import java.util.Map;
import com.cc.campuscrush.vo.StatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * 【StatisticsServiceImpl】用户统计服务层实现
 * &lt;p&gt;核心功能：用户个人数据统计聚合，涵盖情侣空间统计、社交互动统计及图表数据生成&lt;/p&gt;
 * &lt;p&gt;使用场景：用户个人中心数据看板，被 StatisticsController 调用，采用 CompletableFuture 异步并行查询纪念日、相册、日记、帖子、点赞、评论、收藏、关注等多项指标，支持周活跃度、月度发帖趋势和内容分类分布三类图表，同时提供日浏览量统计时间序列数据&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private LoveAnniversaryMapper anniversaryMapper;

    @Autowired
    private LoveAlbumMapper albumMapper;

    @Autowired
    private LoveDiaryMapper diaryMapper;

    @Autowired
    private LoveCoupleProfileMapper profileMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private FollowMapper followMapper;

    @Autowired
    @Qualifier("taskExecutor")
    private ExecutorService executor;

    /**
     * 获取用户完整统计数据（含情侣空间+社交互动+图表数据，被StatisticsController调用）
     * 业务逻辑：CompletableFuture异步并行查询11项指标（纪念日数、相册数、日记数、纪念日列表、情侣档案、帖子数、点赞数、评论数、收藏数、粉丝数、关注数）→ 从love_couple_profile计算相恋天数 → 生成周活跃度、月度发帖趋势、内容分类分布三类图表 → 异常时填充默认零值
     * 异常场景：任何异步查询异常时调用setDefaultValues填充零值
     *
     * @param userId 用户ID（必填）
     * @return StatisticsVO含所有统计指标和图表数据
     */
    @Override
    public StatisticsVO getStatistics(Long userId) {
        StatisticsVO vo = new StatisticsVO();

        CompletableFuture<Long> anniversaryFuture = CompletableFuture.supplyAsync(
                () -> anniversaryMapper.countByUserId(userId), executor);
        CompletableFuture<Long> albumFuture = CompletableFuture.supplyAsync(
                () -> albumMapper.countByUserId(userId), executor);
        CompletableFuture<Long> diaryFuture = CompletableFuture.supplyAsync(
                () -> diaryMapper.countByUserId(userId), executor);
        CompletableFuture<List<LoveAnniversary>> anniversariesFuture = CompletableFuture.supplyAsync(
                () -> anniversaryMapper.findAllByUserId(userId), executor);
        CompletableFuture<LoveCoupleProfile> coupleFuture = CompletableFuture.supplyAsync(
                () -> profileMapper.findByUserId(userId), executor);

        CompletableFuture<Long> postCountFuture = CompletableFuture.supplyAsync(
                () -> postMapper.countByUserId(userId), executor);
        CompletableFuture<Long> likeCountFuture = CompletableFuture.supplyAsync(
                () -> likeMapper.countByUserId(userId), executor);
        CompletableFuture<Long> commentCountFuture = CompletableFuture.supplyAsync(
                () -> commentMapper.countByUserId(userId), executor);
        CompletableFuture<Long> collectCountFuture = CompletableFuture.supplyAsync(
                () -> collectMapper.countByUserId(userId), executor);
        CompletableFuture<Long> followerCountFuture = CompletableFuture.supplyAsync(
                () -> followMapper.countByFollowingId(userId), executor);
        CompletableFuture<Long> followingCountFuture = CompletableFuture.supplyAsync(
                () -> followMapper.countByFollowerId(userId), executor);

        CompletableFuture.allOf(anniversaryFuture, albumFuture, diaryFuture, anniversariesFuture,
                coupleFuture, postCountFuture, likeCountFuture, commentCountFuture, collectCountFuture,
                followerCountFuture, followingCountFuture).join();

        try {
            vo.setTimelineCount(0L);
            vo.setAnniversaryCount(anniversaryFuture.get());
            vo.setAlbumCount(albumFuture.get());
            vo.setDiaryCount(diaryFuture.get());

            // 从 love_couple_profile 表获取在一起天数
            LoveCoupleProfile profile = coupleFuture.get();
            if (profile != null && profile.getStartDate() != null) {
                long days = ChronoUnit.DAYS.between(profile.getStartDate(), LocalDate.now());
                vo.setTogetherDays(days);
            } else {
                vo.setTogetherDays(0L);
            }

            vo.setPostCount(postCountFuture.get());
            vo.setLikeCount(likeCountFuture.get());
            vo.setCommentCount(commentCountFuture.get());
            vo.setCollectCount(collectCountFuture.get());
            vo.setFollowerCount(followerCountFuture.get());
            vo.setFollowingCount(followingCountFuture.get());

            vo.setProductCount(0L);
            vo.setOrderCount(0L);
            vo.setTotalSpent(0.0);
            vo.setWishlistCount(0L);

            vo.setWeeklyActivity(getWeeklyActivityData(userId));
            vo.setMonthlyPost(getMonthlyPostData(userId));
            vo.setCategoryDistribution(getCategoryDistributionData(userId));
        } catch (Exception e) {
            setDefaultValues(vo);
        }

        return vo;
    }

    /**
     * 获取情侣空间基础统计数据（被StatisticsController调用）
     * 业务逻辑：CompletableFuture异步并行查询纪念日数、相册数、日记数、纪念日列表、情侣档案 → 从profile计算相恋天数 → 异常时默认零值
     * 异常场景：任何异步查询异常时各项默认0
     *
     * @param userId 用户ID（必填）
     * @return StatisticsVO含情侣空间指标
     */
    @Override
    public StatisticsVO getBasicStats(Long userId) {
        StatisticsVO vo = new StatisticsVO();

        CompletableFuture<Long> anniversaryFuture = CompletableFuture.supplyAsync(
                () -> anniversaryMapper.countByUserId(userId), executor);
        CompletableFuture<Long> albumFuture = CompletableFuture.supplyAsync(
                () -> albumMapper.countByUserId(userId), executor);
        CompletableFuture<Long> diaryFuture = CompletableFuture.supplyAsync(
                () -> diaryMapper.countByUserId(userId), executor);
        CompletableFuture<List<LoveAnniversary>> anniversariesFuture = CompletableFuture.supplyAsync(
                () -> anniversaryMapper.findAllByUserId(userId), executor);
        CompletableFuture<LoveCoupleProfile> coupleFuture = CompletableFuture.supplyAsync(
                () -> profileMapper.findByUserId(userId), executor);

        CompletableFuture.allOf(anniversaryFuture, albumFuture, diaryFuture, anniversariesFuture, coupleFuture).join();

        try {
            vo.setTimelineCount(0L);
            vo.setAnniversaryCount(anniversaryFuture.get());
            vo.setAlbumCount(albumFuture.get());
            vo.setDiaryCount(diaryFuture.get());

            // 从 love_couple_profile 表获取在一起天数
            LoveCoupleProfile profile = coupleFuture.get();
            if (profile != null && profile.getStartDate() != null) {
                long days = ChronoUnit.DAYS.between(profile.getStartDate(), LocalDate.now());
                vo.setTogetherDays(days);
            } else {
                vo.setTogetherDays(0L);
            }
        } catch (Exception e) {
            vo.setTimelineCount(0L);
            vo.setAnniversaryCount(0L);
            vo.setAlbumCount(0L);
            vo.setDiaryCount(0L);
            vo.setTogetherDays(0L);
        }

        return vo;
    }

    /**
     * 获取社交互动统计数据（被StatisticsController调用）
     * 业务逻辑：CompletableFuture异步并行查询帖子数、获得点赞数、评论数、收藏数、粉丝数、关注数 → 异常时默认零值
     * 异常场景：任何异步查询异常时各项默认0
     *
     * @param userId 用户ID（必填）
     * @return StatisticsVO含社交互动指标
     */
    @Override
    public StatisticsVO getSocialStats(Long userId) {
        StatisticsVO vo = new StatisticsVO();

        CompletableFuture<Long> postCountFuture = CompletableFuture.supplyAsync(
                () -> postMapper.countByUserId(userId), executor);
        CompletableFuture<Long> likeCountFuture = CompletableFuture.supplyAsync(
                () -> likeMapper.countByUserId(userId), executor);
        CompletableFuture<Long> commentCountFuture = CompletableFuture.supplyAsync(
                () -> commentMapper.countByUserId(userId), executor);
        CompletableFuture<Long> collectCountFuture = CompletableFuture.supplyAsync(
                () -> collectMapper.countByUserId(userId), executor);
        CompletableFuture<Long> followerCountFuture = CompletableFuture.supplyAsync(
                () -> followMapper.countByFollowingId(userId), executor);
        CompletableFuture<Long> followingCountFuture = CompletableFuture.supplyAsync(
                () -> followMapper.countByFollowerId(userId), executor);

        CompletableFuture.allOf(postCountFuture, likeCountFuture, commentCountFuture, collectCountFuture,
                followerCountFuture, followingCountFuture).join();

        try {
            vo.setPostCount(postCountFuture.get());
            vo.setLikeCount(likeCountFuture.get());
            vo.setCommentCount(commentCountFuture.get());
            vo.setCollectCount(collectCountFuture.get());
            vo.setFollowerCount(followerCountFuture.get());
            vo.setFollowingCount(followingCountFuture.get());
        } catch (Exception e) {
            vo.setPostCount(0L);
            vo.setLikeCount(0L);
            vo.setCommentCount(0L);
            vo.setCollectCount(0L);
            vo.setFollowerCount(0L);
            vo.setFollowingCount(0L);
        }

        return vo;
    }

    /**
     * 获取商品统计占位数据（功能未上线，被StatisticsController调用）
     * 业务逻辑：返回默认零值的商品/订单/消费/心愿统计
     * 异常场景：无
     *
     * @param userId 用户ID（必填）
     * @return StatisticsVO含productCount=0, orderCount=0, totalSpent=0.0, wishlistCount=0
     */
    @Override
    public StatisticsVO getProductStats(Long userId) {
        StatisticsVO vo = new StatisticsVO();
        vo.setProductCount(0L);
        vo.setOrderCount(0L);
        vo.setTotalSpent(0.0);
        vo.setWishlistCount(0L);
        return vo;
    }

    /**
     * 按图表类型获取图表数据（被StatisticsController调用）
     * 业务逻辑：根据chartType路由 → "weeklyActivity"：近7天每天发帖数折线图 → "monthlyPost"：近12个月每月发帖数柱状图 → "categoryDistribution"：9个分类发帖分布饼图 → 其他返回null
     * 异常场景：未知chartType返回null
     *
     * @param userId    用户ID（必填）
     * @param chartType 图表类型：weeklyActivity/monthlyPost/categoryDistribution（必填）
     * @return ChartData对象，未知类型返回null
     */
    @Override
    public StatisticsVO.ChartData getChartData(Long userId, String chartType) {
        return switch (chartType) {
            case "weeklyActivity" -> getWeeklyActivityData(userId);
            case "monthlyPost" -> getMonthlyPostData(userId);
            case "categoryDistribution" -> getCategoryDistributionData(userId);
            default -> null;
        };
    }

    private StatisticsVO.ChartData getWeeklyActivityData(Long userId) {
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 6; i >= 0; i--) {
            LocalDateTime date = now.minusDays(i);
            String dateStr = date.format(DateTimeFormatter.ofPattern("MM-dd"));
            labels.add(dateStr);

            long count = postMapper.countByUserIdAndDate(userId, date.toLocalDate());
            data.add(count);
        }

        return new StatisticsVO.ChartData(labels, data, "近7天活跃度", "line");
    }

    private StatisticsVO.ChartData getMonthlyPostData(Long userId) {
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 11; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            String monthStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            labels.add(monthStr);

            long count = postMapper.countByUserIdAndMonth(userId, date.getYear(), date.getMonthValue());
            data.add(count);
        }

        return new StatisticsVO.ChartData(labels, data, "月度发帖统计", "bar");
    }

    private StatisticsVO.ChartData getCategoryDistributionData(Long userId) {
        List<String> labels = List.of("推荐", "穿搭", "美食", "日常", "旅行", "美妆", "健身", "读书", "其他");
        List<Long> data = new ArrayList<>();

        for (String category : labels) {
            long count = postMapper.countByUserIdAndCategory(userId, category);
            data.add(count);
        }

        return new StatisticsVO.ChartData(labels, data, "内容分类分布", "pie");
    }

    private void setDefaultValues(StatisticsVO vo) {
        vo.setTimelineCount(0L);
        vo.setAnniversaryCount(0L);
        vo.setAlbumCount(0L);
        vo.setDiaryCount(0L);
        vo.setTogetherDays(0L);
        vo.setPostCount(0L);
        vo.setLikeCount(0L);
        vo.setCommentCount(0L);
        vo.setCollectCount(0L);
        vo.setFollowerCount(0L);
        vo.setFollowingCount(0L);
        vo.setProductCount(0L);
        vo.setOrderCount(0L);
        vo.setTotalSpent(0.0);
        vo.setWishlistCount(0L);
    }

    /**
     * 获取用户每日浏览量时间序列数据（被StatisticsController调用）
     * 业务逻辑：查MySQL统计指定天数范围内的每日浏览量 → 以Map聚合by日期 → 按时间正序补全缺失日期（count=0） → 返回{date, count}列表
     * 异常场景：无浏览记录时所有日期count=0
     *
     * @param userId 用户ID（必填）
     * @param days   统计天数（必填，从今天往前推days天）
     * @return 每日浏览统计列表，按日期正序排列
     */
    @Override
    public List<Map<String, Object>> getDailyBrowseStats(Long userId, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        List<Map<String, Object>> result = postMapper.countDailyBrowseByUserId(userId, startDate, endDate);
        
        Map<String, Long> browseMap = new HashMap<>();
        for (Map<String, Object> item : result) {
            Object dateObj = item.get("date");
            String date;
            if (dateObj instanceof java.sql.Date) {
                date = ((java.sql.Date) dateObj).toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else {
                date = String.valueOf(dateObj);
            }
            Long count = (Long) item.get("count");
            browseMap.put(date, count);
        }
        
        List<Map<String, Object>> dailyStats = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = endDate.minusDays(i);
            String dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Map<String, Object> dayStats = new HashMap<>();
            dayStats.put("date", dateStr);
            dayStats.put("count", browseMap.getOrDefault(dateStr, 0L));
            dailyStats.add(dayStats);
        }
        
        return dailyStats;
    }
}