package com.cc.campuscrush.service;

import com.cc.campuscrush.entity.LoveDiary;

import java.util.List;

/**
 * 【LoveDiaryService】服务层接口
 * &lt;p&gt;核心功能：提供恋爱日记的增删改查和数量统计功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于情侣空间日记记录场景，被LoveDiaryController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface LoveDiaryService {

    /**
     * 根据用户ID查询所有恋爱日记
     * 业务逻辑：查询该用户所属情侣空间的所有日记 → 按创建时间倒序排列
     * 异常场景：无日记记录时返回空列表
     *
     * @param userId 用户ID（必填，通过用户ID定位其所属情侣空间）
     * @return 日记列表，无数据时返回空列表
     */
    List<LoveDiary> findAllByUserId(Long userId);

    /**
     * 根据日记ID和用户ID查询单条日记
     * 业务逻辑：查询指定ID的日记 → 校验该日记属于该用户的情侣空间
     * 异常场景：日记不存在或不属于该用户时返回null
     *
     * @param id     日记ID（必填）
     * @param userId 用户ID（必填，用于权限校验）
     * @return 日记实体，不存在时返回null
     */
    LoveDiary findByIdAndUserId(Long id, Long userId);

    /**
     * 新增一条恋爱日记
     * 业务逻辑：构建LoveDiary实体 → 设置标题、内容、作者等信息 → 保存到数据库
     * 异常场景：必填字段（如标题、内容）为空时保存失败
     *
     * @param diary 日记实体（必填，需包含userId、标题和内容）
     * @return 受影响的行数，1表示成功，0表示失败
     */
    int insert(LoveDiary diary);

    /**
     * 更新一条恋爱日记
     * 业务逻辑：校验日记归属 → 更新日记标题或内容 → 保存更新
     * 异常场景：日记不存在或不属于该用户时更新失败
     *
     * @param diary 日记实体（必填，需包含id和userId）
     * @return 受影响的行数，1表示成功，0表示失败
     */
    int update(LoveDiary diary);

    /**
     * 根据日记ID和用户ID删除日记
     * 业务逻辑：校验日记归属 → 删除日记记录
     * 异常场景：日记不存在或不属于该用户时返回0
     *
     * @param id     日记ID（必填）
     * @param userId 用户ID（必填，用于权限校验）
     * @return 受影响的行数，1表示成功，0表示记录不存在或无权限
     */
    int deleteByIdAndUserId(Long id, Long userId);

    /**
     * 统计指定用户的恋爱日记数量
     * 业务逻辑：统计该用户所属情侣空间下的日记总数
     * 异常场景：用户无日记时返回0
     *
     * @param userId 用户ID（必填）
     * @return 日记数量，无数据时返回0
     */
    long countByUserId(Long userId);
}
