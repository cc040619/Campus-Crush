package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.LoveDiary;
import com.cc.campuscrush.mapper.LoveDiaryMapper;
import com.cc.campuscrush.service.LoveDiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 【LoveDiaryServiceImpl】情侣日记服务层实现
 * &lt;p&gt;核心功能：情侣共享日记的增删改查管理&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间中的日记模块，被 LoveDiaryController 调用，支持按用户查询所有日记、按ID精确查询、日记新增编辑和软删除，同时统计用户日记总量&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class LoveDiaryServiceImpl implements LoveDiaryService {

    @Autowired
    private LoveDiaryMapper loveDiaryMapper;

    /**
     * 查询用户所有日记（被LoveDiaryController调用）
     * 业务逻辑：按userId查love_diary表返回全量列表
     * 异常场景：无日记时返回空列表
     *
     * @param userId 用户ID（必填）
     * @return 日记列表
     */
    @Override
    public List<LoveDiary> findAllByUserId(Long userId) {
        return loveDiaryMapper.findAllByUserId(userId);
    }

    /**
     * 按ID和用户ID精确查询日记（被LoveDiaryController调用）
     * 业务逻辑：按id和userId联合查询，保证数据归属安全
     * 异常场景：无匹配记录时返回null
     *
     * @param id     日记ID（必填）
     * @param userId 用户ID（必填，用于归属校验）
     * @return 日记实体，无记录返回null
     */
    @Override
    public LoveDiary findByIdAndUserId(Long id, Long userId) {
        return loveDiaryMapper.findByIdAndUserId(id, userId);
    }

    /**
     * 新增日记（被LoveDiaryController调用）
     * 业务逻辑：直接插入diary记录到MySQL
     * 异常场景：插入失败返回0
     *
     * @param diary 日记实体（必填）
     * @return 受影响行数
     */
    @Override
    public int insert(LoveDiary diary) {
        return loveDiaryMapper.insert(diary);
    }

    /**
     * 更新日记（被LoveDiaryController调用）
     * 业务逻辑：按diary实体的id字段更新对应记录
     * 异常场景：记录不存在时返回0
     *
     * @param diary 日记实体（必填，需含id）
     * @return 受影响行数
     */
    @Override
    public int update(LoveDiary diary) {
        return loveDiaryMapper.update(diary);
    }

    /**
     * 删除日记（软删除，被LoveDiaryController调用）
     * 业务逻辑：按id和userId联合删除，保证用户只能删除自己的日记
     * 异常场景：无匹配记录时返回0
     *
     * @param id     日记ID（必填）
     * @param userId 用户ID（必填，用于归属校验）
     * @return 受影响行数
     */
    @Override
    public int deleteByIdAndUserId(Long id, Long userId) {
        return loveDiaryMapper.deleteByIdAndUserId(id, userId);
    }

    /**
     * 统计用户日记总数（被LoveDiaryController和StatisticsServiceImpl调用）
     * 业务逻辑：按userId计数love_diary表记录
     * 异常场景：无日记时返回0
     *
     * @param userId 用户ID（必填）
     * @return 日记总数
     */
    @Override
    public long countByUserId(Long userId) {
        return loveDiaryMapper.countByUserId(userId);
    }
}
