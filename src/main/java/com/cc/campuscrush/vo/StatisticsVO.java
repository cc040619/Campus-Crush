package com.cc.campuscrush.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 【StatisticsVO】视图对象
 * &lt;p&gt;核心功能：统计视图对象，汇总用户个人数据指标（恋爱时光轴/纪念日/相册/日记/在一起天数）、社区互动数据（帖子/点赞/评论/收藏/粉丝数）、电商消费数据（商品/订单/总支出/心愿单）及图表展示数据&lt;/p&gt;
 * &lt;p&gt;使用场景：用于前端个人中心或管理后台的数据统计页面展示，内嵌ChartData内部类封装图表标签、数据集、标题和图表类型&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class StatisticsVO {
    private Long timelineCount;
    private Long anniversaryCount;
    private Long albumCount;
    private Long diaryCount;
    private Long togetherDays;

    private Long postCount;
    private Long likeCount;
    private Long commentCount;
    private Long collectCount;
    private Long followerCount;
    private Long followingCount;

    private Long productCount;
    private Long orderCount;
    private Double totalSpent;
    private Long wishlistCount;

    private ChartData weeklyActivity;
    private ChartData monthlyPost;
    private ChartData categoryDistribution;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartData {
        private List<String> labels;
        private List<Long> data;
        private String title;
        private String type;
    }
}