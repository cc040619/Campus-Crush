package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.LoveCoupleRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * LoveCoupleRequestMapper数据访问层
 * <p>核心功能：管理情侣配对请求，支持发起请求、查询待处理/已发送请求、更新请求状态及检查重复请求</p>
 * <p>使用场景：情侣配对申请流程、请求列表展示、防止重复申请，被LoveCoupleRequestService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface LoveCoupleRequestMapper {

    /**
     * 新增一条情侣配对请求
     *
     * @param request 配对请求实体（必填）
     * @return 受影响行数
     */
    int insert(LoveCoupleRequest request);

    /**
     * 根据ID查询单条配对请求
     *
     * @param id 请求ID（必填）
     * @return 配对请求实体，无记录时返回null
     */
    LoveCoupleRequest findById(Long id);

    /**
     * 查询某用户收到的所有配对请求
     *
     * @param toUserId 接收方用户ID（必填）
     * @return 配对请求列表
     */
    List<LoveCoupleRequest> findByToUserId(Long toUserId);

    /**
     * 查询某用户发出的所有配对请求
     *
     * @param fromUserId 发起方用户ID（必填）
     * @return 配对请求列表
     */
    List<LoveCoupleRequest> findByFromUserId(Long fromUserId);

    /**
     * 更新配对请求的状态（接受/拒绝）
     *
     * @param id     请求ID（必填）
     * @param status 新状态（必填）
     * @return 受影响行数
     */
    int updateStatus(Long id, Integer status);

    /**
     * 检查两人之间是否已有待处理的配对请求（防止重复申请）
     *
     * @param fromUserId 发起方用户ID（必填）
     * @param toUserId   接收方用户ID（必填）
     * @return 待处理的请求实体，无记录时返回null
     */
    LoveCoupleRequest findPendingBetweenUsers(Long fromUserId, Long toUserId);
}
