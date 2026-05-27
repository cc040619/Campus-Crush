package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.LoveAlbum;
import com.cc.campuscrush.mapper.LoveAlbumMapper;
import com.cc.campuscrush.service.LoveAlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 【LoveAlbumServiceImpl】情侣相册服务层实现
 * &lt;p&gt;核心功能：情侣共享相册照片的增删改查管理&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间中的相册模块，被 LoveAlbumController 调用，支持按用户查询所有照片、按ID精确查询、照片新增编辑和软删除，同时统计用户照片总量&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class LoveAlbumServiceImpl implements LoveAlbumService {

    @Autowired
    private LoveAlbumMapper loveAlbumMapper;

    /**
     * 查询用户所有相册照片（被LoveAlbumController调用）
     * 业务逻辑：按userId查love_album表返回全量列表
     * 异常场景：无照片时返回空列表
     *
     * @param userId 用户ID（必填）
     * @return 相册照片列表
     */
    @Override
    public List<LoveAlbum> findAllByUserId(Long userId) {
        return loveAlbumMapper.findAllByUserId(userId);
    }

    /**
     * 按ID和用户ID精确查询单张照片（被LoveAlbumController调用）
     * 业务逻辑：按id和userId联合查询，保证数据归属安全
     * 异常场景：无匹配记录时返回null
     *
     * @param id     照片ID（必填）
     * @param userId 用户ID（必填，用于归属校验）
     * @return 照片实体，无记录返回null
     */
    @Override
    public LoveAlbum findByIdAndUserId(Long id, Long userId) {
        return loveAlbumMapper.findByIdAndUserId(id, userId);
    }

    /**
     * 新增相册照片（被LoveAlbumController调用）
     * 业务逻辑：直接插入album记录到MySQL
     * 异常场景：插入失败返回0
     *
     * @param album 照片实体（必填）
     * @return 受影响行数
     */
    @Override
    public int insert(LoveAlbum album) {
        return loveAlbumMapper.insert(album);
    }

    /**
     * 更新相册照片信息（被LoveAlbumController调用）
     * 业务逻辑：按album实体的id字段更新对应记录
     * 异常场景：记录不存在时返回0
     *
     * @param album 照片实体（必填，需含id）
     * @return 受影响行数
     */
    @Override
    public int update(LoveAlbum album) {
        return loveAlbumMapper.update(album);
    }

    /**
     * 删除照片（软删除，被LoveAlbumController调用）
     * 业务逻辑：按id和userId联合删除，保证用户只能删除自己的照片
     * 异常场景：无匹配记录时返回0
     *
     * @param id     照片ID（必填）
     * @param userId 用户ID（必填，用于归属校验）
     * @return 受影响行数
     */
    @Override
    public int deleteByIdAndUserId(Long id, Long userId) {
        return loveAlbumMapper.deleteByIdAndUserId(id, userId);
    }

    /**
     * 统计用户照片总数（被LoveAlbumController和StatisticsServiceImpl调用）
     * 业务逻辑：按userId计数love_album表记录
     * 异常场景：无照片时返回0
     *
     * @param userId 用户ID（必填）
     * @return 照片总数
     */
    @Override
    public long countByUserId(Long userId) {
        return loveAlbumMapper.countByUserId(userId);
    }
}
