package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.LoveCoupleProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * LoveCoupleProfileMapper数据访问层
 * <p>核心功能：管理情侣关系档案，支持创建情侣绑定、查询情侣信息、更新档案、解绑及删除</p>
 * <p>使用场景：情侣空间绑定/解绑流程、情侣信息展示，被LoveCoupleProfileService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface LoveCoupleProfileMapper {

    /**
     * 根据情侣ID查询情侣档案
     *
     * @param coupleId 情侣ID（必填）
     * @return 情侣档案实体，无记录时返回null
     */
    LoveCoupleProfile findByCoupleId(Long coupleId);

    /**
     * 根据用户ID查询该用户所属的情侣档案
     *
     * @param userId 用户ID（必填）
     * @return 情侣档案实体，无记录时返回null
     */
    LoveCoupleProfile findByUserId(Long userId);

    /**
     * 新增一个情侣档案（创建情侣绑定）
     *
     * @param profile 情侣档案实体（必填）
     * @return 受影响行数
     */
    int insert(LoveCoupleProfile profile);

    /**
     * 更新情侣档案信息
     *
     * @param profile 情侣档案实体（必填，需包含id和更新内容）
     * @return 受影响行数
     */
    int update(LoveCoupleProfile profile);

    /**
     * 解除用户的情侣绑定关系
     *
     * @param userId 用户ID（必填）
     * @return 受影响行数
     */
    int unbind(Long userId);

    /**
     * 根据用户ID删除情侣档案记录
     *
     * @param userId 用户ID（必填）
     * @return 受影响行数
     */
    int deleteByUserId(Long userId);
}
