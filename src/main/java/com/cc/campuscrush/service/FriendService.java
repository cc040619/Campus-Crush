package com.cc.campuscrush.service;

import com.cc.campuscrush.entity.Friend;
import com.cc.campuscrush.entity.SysUser;
import com.github.pagehelper.PageInfo;
import java.util.List;
import java.util.Map;

/**
 * 【FriendService】服务层接口
 * &lt;p&gt;核心功能：提供好友添加删除、好友列表查询、好友申请与审批、昵称备注及用户搜索功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于用户好友关系全生命周期管理场景，被FriendController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface FriendService {

    /**
     * 直接添加好友（无需申请审批）
     * 业务逻辑：校验双方非同一用户 → 检查是否已是好友 → 双向插入好友关系记录
     * 异常场景：已是好友时返回false；添加自己为好友时返回false
     *
     * @param userId   当前用户ID（必填）
     * @param friendId 好友用户ID（必填）
     * @return true表示添加成功，false表示添加失败
     */
    boolean addFriend(Long userId, Long friendId);

    /**
     * 删除好友关系
     * 业务逻辑：删除双方互为好友的关系记录 → 清理关联的聊天置顶等数据
     * 异常场景：非好友关系时返回false
     *
     * @param userId   当前用户ID（必填）
     * @param friendId 好友用户ID（必填）
     * @return true表示删除成功，false表示删除失败
     */
    boolean deleteFriend(Long userId, Long friendId);

    /**
     * 分页获取好友列表
     * 业务逻辑：使用PageHelper分页 → 查询该用户的所有好友 → 包含昵称备注信息
     * 异常场景：无好友时返回空PageInfo
     *
     * @param userId   用户ID（必填）
     * @param pageNum  页码（必填，从1开始）
     * @param pageSize 每页条数（必填）
     * @return 好友分页数据，无数据时PageInfo的list为空
     */
    PageInfo<Friend> getFriendList(Long userId, int pageNum, int pageSize);

    /**
     * 判断两人是否为好友关系
     * 业务逻辑：查询好友关系表 → 检查是否存在双向好友记录
     * 异常场景：任一用户不存在时返回false
     *
     * @param userId   当前用户ID（必填）
     * @param friendId 目标用户ID（必填）
     * @return true表示是好友，false表示不是好友
     */
    boolean isFriend(Long userId, Long friendId);

    /**
     * 按关键词搜索用户
     * 业务逻辑：在用户表中模糊匹配昵称或用户名 → 返回匹配的用户列表
     * 异常场景：无匹配结果时返回空列表
     *
     * @param keyword 搜索关键词（必填）
     * @return 匹配的用户列表，无数据时返回空列表
     */
    List<SysUser> searchUsers(String keyword);

    /**
     * 按关键词搜索用户并附带好友关系状态
     * 业务逻辑：模糊匹配昵称或用户名 → 查询结果中标记与当前用户的好友关系状态 → 返回带状态的用户信息
     * 异常场景：无匹配结果时返回空列表
     *
     * @param currentUserId 当前用户ID（必填，用于判断好友状态）
     * @param keyword       搜索关键词（必填）
     * @return 带好友状态标记的用户列表，无数据时返回空列表
     */
    List<Map<String, Object>> searchUsersWithStatus(Long currentUserId, String keyword);

    /**
     * 发送好友申请
     * 业务逻辑：检查是否已是好友 → 检查是否已有待处理的申请 → 创建好友申请记录
     * 异常场景：已是好友时返回false；已有待处理申请时返回false
     *
     * @param userId   申请发起方用户ID（必填）
     * @param friendId 申请接收方用户ID（必填）
     * @return true表示发送成功，false表示发送失败
     */
    boolean sendFriendRequest(Long userId, Long friendId);

    /**
     * 获取当前用户收到的好友申请列表
     * 业务逻辑：查询所有待审批的好友申请 → 返回发起方用户信息列表
     * 异常场景：无待处理申请时返回空列表
     *
     * @param userId 当前用户ID（必填，作为申请接收方）
     * @return 申请人用户列表，无数据时返回空列表
     */
    List<SysUser> getFriendRequests(Long userId);

    /**
     * 同意好友申请
     * 业务逻辑：校验申请记录存在 → 创建双向好友关系 → 更新申请状态为已同意
     * 异常场景：申请记录不存在时返回false
     *
     * @param userId   当前用户ID（必填，申请接收方）
     * @param friendId 申请人用户ID（必填）
     * @return true表示同意成功，false表示操作失败
     */
    boolean agreeFriendRequest(Long userId, Long friendId);

    /**
     * 拒绝好友申请
     * 业务逻辑：校验申请记录存在 → 更新申请状态为已拒绝 → 不创建好友关系
     * 异常场景：申请记录不存在时返回false
     *
     * @param userId   当前用户ID（必填，申请接收方）
     * @param friendId 申请人用户ID（必填）
     * @return true表示拒绝成功，false表示操作失败
     */
    boolean refuseFriendRequest(Long userId, Long friendId);

    /**
     * 获取当前用户的所有好友（不分页）
     * 业务逻辑：查询所有已建立好友关系的用户 → 返回完整好友列表
     * 异常场景：无好友时返回空列表
     *
     * @param userId 用户ID（必填）
     * @return 好友用户列表，无数据时返回空列表
     */
    List<SysUser> getFriends(Long userId);

    /**
     * 更新好友的备注昵称
     * 业务逻辑：查询好友关系记录 → 更新friendNickname字段
     * 异常场景：非好友关系时返回false
     *
     * @param userId         当前用户ID（必填）
     * @param friendId       好友用户ID（必填）
     * @param friendNickname 备注昵称（可为空，为空表示取消备注）
     * @return true表示更新成功，false表示更新失败
     */
    boolean updateFriendNickname(Long userId, Long friendId, String friendNickname);

    /**
     * 获取好友的备注昵称
     * 业务逻辑：查询好友关系记录 → 返回存储的备注昵称
     * 异常场景：非好友关系或无备注时返回null
     *
     * @param userId   当前用户ID（必填）
     * @param friendId 好友用户ID（必填）
     * @return 备注昵称，无备注或非好友时返回null
     */
    String getFriendNickname(Long userId, Long friendId);
}
