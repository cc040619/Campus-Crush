package com.cc.campuscrush.mapper;

import com.cc.campuscrush.entity.Friend;
import com.github.pagehelper.Page;

/**
 * FriendMapper数据访问层
 * <p>核心功能：管理好友关系，支持好友申请、接受/拒绝、删除好友、好友列表分页、好友昵称备注及状态管理</p>
 * <p>使用场景：好友添加流程、好友列表、好友昵称设置、私聊入口好友校验，被FriendService调用</p>
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface FriendMapper {

    /**
     * 根据用户ID和好友ID查询好友关系
     *
     * @param userId   用户ID（必填）
     * @param friendId 好友ID（必填）
     * @return 好友关系实体，无记录时返回null
     */
    Friend selectByUserIdAndFriendId(Long userId, Long friendId);

    /**
     * 新增一条好友关系记录
     *
     * @param friend 好友实体（必填）
     */
    void insert(Friend friend);

    /**
     * 删除两人之间的好友关系
     *
     * @param userId   用户ID（必填）
     * @param friendId 好友ID（必填）
     * @return 受影响行数
     */
    int deleteByUserIdAndFriendId(Long userId, Long friendId);

    /**
     * 分页查询某用户的好友列表（使用PageHelper分页）
     *
     * @param userId 用户ID（必填）
     * @return 分页好友列表
     */
    Page<Friend> selectByUserId(Long userId);

    /**
     * 统计两人之间的好友关系数量
     *
     * @param userId   用户ID（必填）
     * @param friendId 好友ID（必填）
     * @return 好友关系数量（0或1）
     */
    int countByUserIdAndFriendId(Long userId, Long friendId);

    /**
     * 查询某用户收到的指定状态的好友申请列表
     *
     * @param friendId 被申请者（接收方）用户ID（必填）
     * @param status   申请状态（必填）
     * @return 好友申请列表
     */
    java.util.List<Friend> selectByFriendIdAndStatus(Long friendId, Integer status);

    /**
     * 查询某用户发出的指定状态的好友申请列表
     *
     * @param userId 申请者（发起方）用户ID（必填）
     * @param status 申请状态（必填）
     * @return 好友申请列表
     */
    java.util.List<Friend> selectByUserIdAndStatus(Long userId, Integer status);

    /**
     * 更新好友申请状态（接受/拒绝）
     *
     * @param friend 好友实体（必填，需包含id和更新后的status）
     */
    void updateStatus(Friend friend);

    /**
     * 更新好友备注昵称
     *
     * @param userId         用户ID（必填）
     * @param friendId       好友ID（必填）
     * @param friendNickname 备注昵称（必填）
     * @return 受影响行数
     */
    int updateFriendNickname(Long userId, Long friendId, String friendNickname);

    /**
     * 查询好友备注昵称
     *
     * @param userId   用户ID（必填）
     * @param friendId 好友ID（必填）
     * @return 备注昵称，无记录时返回null
     */
    String selectFriendNickname(Long userId, Long friendId);
}
