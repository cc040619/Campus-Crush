package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.LoveAlbum;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * LoveAlbumMapper数据访问层
 * <p>核心功能：管理情侣相册照片，支持照片的增删改查及按用户维度查询所有照片</p>
 * <p>使用场景：情侣空间相册功能、照片上传/删除、相册浏览，被LoveAlbumService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface LoveAlbumMapper {

    /**
     * 查询某用户的所有相册照片
     *
     * @param userId 用户ID（必填）
     * @return 相册照片列表
     */
    List<LoveAlbum> findAllByUserId(Long userId);

    /**
     * 根据照片ID和用户ID查询单张照片
     *
     * @param id     照片ID（必填）
     * @param userId 用户ID（必填）
     * @return 相册照片实体，无记录时返回null
     */
    LoveAlbum findByIdAndUserId(Long id, Long userId);

    /**
     * 新增一张相册照片
     *
     * @param album 相册实体（必填）
     * @return 受影响行数
     */
    int insert(LoveAlbum album);

    /**
     * 更新相册照片信息
     *
     * @param album 相册实体（必填，需包含id和更新内容）
     * @return 受影响行数
     */
    int update(LoveAlbum album);

    /**
     * 根据照片ID和用户ID删除照片
     *
     * @param id     照片ID（必填）
     * @param userId 用户ID（必填）
     * @return 受影响行数
     */
    int deleteByIdAndUserId(Long id, Long userId);

    /**
     * 统计某用户的相册照片总数
     *
     * @param userId 用户ID（必填）
     * @return 照片总数
     */
    long countByUserId(Long userId);
}
