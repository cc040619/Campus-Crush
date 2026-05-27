package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.UserCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserCategoryMapper数据访问层
 * <p>核心功能：管理用户偏好的内容分类，支持查询用户分类设置、新增/更新分类偏好及删除</p>
 * <p>使用场景：用户注册时选择兴趣分类、个人中心偏好设置、内容推荐，被UserCategoryService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Mapper
public interface UserCategoryMapper {

    /**
     * 根据用户ID查询分类偏好设置（SELECT）
     *
     * @param userId 用户ID（必填）
     * @return 用户分类实体，无记录时返回null
     */
    UserCategory selectByUserId(Long userId);

    /**
     * 新增用户分类偏好（INSERT）
     *
     * @param userCategory 用户分类实体（必填）
     * @return 受影响行数
     */
    int insert(UserCategory userCategory);

    /**
     * 更新用户分类偏好（UPDATE）
     *
     * @param userId     用户ID（必填）
     * @param categories 分类偏好字符串（必填）
     * @return 受影响行数
     */
    int updateCategories(Long userId, String categories);

    /**
     * 删除用户的分类偏好（DELETE）
     *
     * @param userId 用户ID（必填）
     * @return 受影响行数
     */
    int deleteByUserId(Long userId);
}
