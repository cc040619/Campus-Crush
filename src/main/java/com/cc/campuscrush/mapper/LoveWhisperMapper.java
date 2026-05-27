package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.LoveWhisper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * LoveWhisperMapper数据访问层
 * <p>核心功能：管理情侣间的悄悄话私密消息，支持发送消息、查询双方全部消息及标记消息已读</p>
 * <p>使用场景：情侣空间悄悄话功能、私密消息互动，被LoveWhisperService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface LoveWhisperMapper {

    /**
     * 新增一条悄悄话消息
     *
     * @param whisper 悄悄话实体（必填）
     * @return 受影响行数
     */
    int insert(LoveWhisper whisper);

    /**
     * 查询两人之间的全部悄悄话消息
     *
     * @param userId    当前用户ID（必填）
     * @param partnerId 对方用户ID（必填）
     * @return 双方全部悄悄话消息列表
     */
    List<LoveWhisper> selectByUserIdAndPartnerId(Long userId, Long partnerId);

    /**
     * 将某人发给另一人的所有悄悄话标记为已读
     *
     * @param fromUserId 发送者ID（必填）
     * @param toUserId   接收者ID（必填）
     * @return 受影响行数
     */
    int updateIsRead(Long fromUserId, Long toUserId);
}
