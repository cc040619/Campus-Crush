package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.LoveWishlistItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * LoveWishlistItemMapper数据访问层
 * <p>核心功能：管理情侣心愿清单，支持心愿项的增删查及完成状态标记</p>
 * <p>使用场景：情侣空间心愿清单功能、心愿添加/完成/删除，被LoveWishlistItemService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface LoveWishlistItemMapper {

    /**
     * 查询某对情侣的全部心愿清单
     *
     * @param coupleId 情侣ID（必填）
     * @return 心愿清单列表
     */
    List<LoveWishlistItem> findByCoupleId(@Param("coupleId") Long coupleId);

    /**
     * 根据ID查询单条心愿项
     *
     * @param id 心愿项ID（必填）
     * @return 心愿项实体，无记录时返回null
     */
    LoveWishlistItem findById(@Param("id") Long id);

    /**
     * 新增一条心愿项
     *
     * @param item 心愿项实体（必填）
     * @return 受影响行数
     */
    int insert(LoveWishlistItem item);

    /**
     * 更新心愿项的完成状态
     *
     * @param id        心愿项ID（必填）
     * @param completed 是否已完成（必填）
     * @return 受影响行数
     */
    int updateCompleted(@Param("id") Long id, @Param("completed") Boolean completed);

    /**
     * 根据ID删除心愿项
     *
     * @param id 心愿项ID（必填）
     * @return 受影响行数
     */
    int deleteById(@Param("id") Long id);
}
