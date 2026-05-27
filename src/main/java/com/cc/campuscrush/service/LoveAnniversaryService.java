package com.cc.campuscrush.service;

import com.cc.campuscrush.entity.LoveAnniversary;
import java.util.List;
import java.util.Map;

/**
 * 【LoveAnniversaryService】服务层接口
 * &lt;p&gt;核心功能：提供情侣纪念日的增删改查、下一个纪念日查询及纪念日类型管理功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于情侣空间纪念日管理场景，被LoveAnniversaryController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface LoveAnniversaryService {

    /**
     * 根据用户ID查询所有纪念日
     * 业务逻辑：查询该用户所属情侣空间的所有纪念日 → 按日期排序
     * 异常场景：无纪念日记录时返回空列表
     *
     * @param userId 用户ID（必填，通过用户ID定位其所属情侣空间）
     * @return 纪念日列表，无数据时返回空列表
     */
    List<LoveAnniversary> findAllByUserId(Long userId);

    /**
     * 根据纪念日ID和用户ID查询单条纪念日
     * 业务逻辑：查询指定ID的纪念日 → 校验该纪念日属于该用户的情侣空间
     * 异常场景：纪念日不存在或不属于该用户时返回null
     *
     * @param id     纪念日ID（必填）
     * @param userId 用户ID（必填，用于权限校验）
     * @return 纪念日实体，不存在时返回null
     */
    LoveAnniversary findByIdAndUserId(Long id, Long userId);

    /**
     * 新增一条纪念日记录
     * 业务逻辑：构建LoveAnniversary实体 → 设置日期、标题、类型等信息 → 保存到数据库
     * 异常场景：必填字段（如日期、标题）为空时保存失败
     *
     * @param anniversary 纪念日实体（必填，需包含userId、日期和标题）
     * @return 受影响的行数，1表示成功，0表示失败
     */
    int insert(LoveAnniversary anniversary);

    /**
     * 更新一条纪念日记录
     * 业务逻辑：校验纪念日归属 → 更新纪念日信息（日期、标题、描述等） → 保存更新
     * 异常场景：纪念日不存在或不属于该用户时更新失败
     *
     * @param anniversary 纪念日实体（必填，需包含id和userId）
     * @return 受影响的行数，1表示成功，0表示失败
     */
    int update(LoveAnniversary anniversary);

    /**
     * 根据纪念日ID和用户ID删除纪念日
     * 业务逻辑：校验纪念日归属 → 删除纪念日记录
     * 异常场景：纪念日不存在或不属于该用户时返回0
     *
     * @param id     纪念日ID（必填）
     * @param userId 用户ID（必填，用于权限校验）
     * @return 受影响的行数，1表示成功，0表示记录不存在或无权限
     */
    int deleteByIdAndUserId(Long id, Long userId);

    /**
     * 统计指定用户的纪念日数量
     * 业务逻辑：统计该用户所属情侣空间下的纪念日总数
     * 异常场景：用户无纪念日时返回0
     *
     * @param userId 用户ID（必填）
     * @return 纪念日数量，无数据时返回0
     */
    long countByUserId(Long userId);

    /**
     * 获取下一个最近的纪念日
     * 业务逻辑：按月份日期排序所有纪念日 → 忽略年份，找到距离当前日期最近的未来纪念日 → 返回纪念日信息和距离天数
     * 异常场景：无纪念日时返回空Map或错误提示
     *
     * @param userId 用户ID（必填）
     * @return 包含下一个纪念日详细信息和倒计天数的Map，无数据时返回空Map
     */
    Map<String, Object> getNextAnniversary(Long userId);

    /**
     * 获取纪念日类型列表
     * 业务逻辑：查询系统预定义的纪念日类型（如生日、相识日、恋爱纪念日等） → 返回类型编码和名称
     * 异常场景：无类型数据时返回空列表
     *
     * @return 纪念日类型列表，每个元素包含类型编码和名称，无数据时返回空列表
     */
    List<Map<String, String>> getTypes();
}
