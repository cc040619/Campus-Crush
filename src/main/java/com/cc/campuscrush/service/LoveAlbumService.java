package com.cc.campuscrush.service;

import com.cc.campuscrush.entity.LoveAlbum;
import java.util.List;

/**
 * 【LoveAlbumService】服务层接口
 * &lt;p&gt;核心功能：提供情侣相册的增删改查和数量统计功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于情侣空间相册管理场景，被LoveAlbumController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface LoveAlbumService {

    /**
     * 根据用户ID查询所有相册记录
     * 业务逻辑：查询该用户所属情侣空间的所有相册 → 按创建时间排列
     * 异常场景：无相册记录时返回空列表
     *
     * @param userId 用户ID（必填，通过用户ID定位其所属情侣空间）
     * @return 相册列表，无数据时返回空列表
     */
    List<LoveAlbum> findAllByUserId(Long userId);

    /**
     * 根据相册ID和用户ID查询单条相册记录
     * 业务逻辑：查询指定ID的相册 → 校验该相册属于该用户的情侣空间
     * 异常场景：相册不存在或不属于该用户时返回null
     *
     * @param id     相册ID（必填）
     * @param userId 用户ID（必填，用于权限校验）
     * @return 相册实体，不存在时返回null
     */
    LoveAlbum findByIdAndUserId(Long id, Long userId);

    /**
     * 新增一条相册记录
     * 业务逻辑：构建LoveAlbum实体 → 设置用户关联信息 → 保存到数据库
     * 异常场景：必填字段（如用户ID、图片URL）为空时保存失败
     *
     * @param album 相册实体（必填，需包含userId和图片信息）
     * @return 受影响的行数，1表示成功，0表示失败
     */
    int insert(LoveAlbum album);

    /**
     * 更新一条相册记录
     * 业务逻辑：校验相册归属 → 更新相册信息（如描述、封面等） → 保存更新
     * 异常场景：相册不存在或不属于该用户时更新失败
     *
     * @param album 相册实体（必填，需包含id和userId）
     * @return 受影响的行数，1表示成功，0表示失败
     */
    int update(LoveAlbum album);

    /**
     * 根据相册ID和用户ID删除相册记录
     * 业务逻辑：校验相册归属 → 删除相册记录及关联的图片数据
     * 异常场景：相册不存在或不属于该用户时返回0
     *
     * @param id     相册ID（必填）
     * @param userId 用户ID（必填，用于权限校验）
     * @return 受影响的行数，1表示成功，0表示记录不存在或无权限
     */
    int deleteByIdAndUserId(Long id, Long userId);

    /**
     * 统计指定用户的相册数量
     * 业务逻辑：统计该用户所属情侣空间下的相册总数
     * 异常场景：用户无相册时返回0
     *
     * @param userId 用户ID（必填）
     * @return 相册数量，无数据时返回0
     */
    long countByUserId(Long userId);
}
