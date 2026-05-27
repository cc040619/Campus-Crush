package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.Friend;
import com.cc.campuscrush.entity.SysUser;
import com.cc.campuscrush.mapper.FriendMapper;
import com.cc.campuscrush.mapper.SysUserMapper;
import com.cc.campuscrush.service.FriendService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 【FriendServiceImpl】好友关系服务层实现
 * &lt;p&gt;核心功能：好友申请的发送、审批、拒绝，好友列表查询，用户搜索及好友备注管理&lt;/p&gt;
 * &lt;p&gt;使用场景：社区好友体系的建立与维护，被 FriendController 和 ChatController 调用，支持双向好友关系去重合并、搜索用户时标记好友状态（已添加/待确认/未添加）、好友备注的双向存储和更新&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class FriendServiceImpl implements FriendService {

    private static final Logger logger = LoggerFactory.getLogger(FriendServiceImpl.class);

    @Autowired
    private FriendMapper friendMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    public FriendServiceImpl(FriendMapper friendMapper, SysUserMapper sysUserMapper) {
        this.friendMapper = friendMapper;
        this.sysUserMapper = sysUserMapper;
    }

    /**
     * 添加好友（直接设置为已确认状态，被ChatController调用）
     * 业务逻辑：查是否已存在好友关系 → 已存在返回false → 不存在则插入status=1（已确认）的记录
     * 异常场景：已存在好友或待确认关系时返回false
     *
     * @param userId   用户ID（必填）
     * @param friendId 好友ID（必填）
     * @return true-添加成功，false-已存在好友关系
     */
    @Override
    public boolean addFriend(Long userId, Long friendId) {
        // 检查是否已经是好友
        var friend = friendMapper.selectByUserIdAndFriendId(userId, friendId);

        if (friend != null) {
            // 已经是好友或待确认
            return false;
        } else {
            // 添加好友请求
            var newFriend = new Friend();
            newFriend.setUserId(userId);
            newFriend.setFriendId(friendId);
            newFriend.setStatus(1); // 待确认
            friendMapper.insert(newFriend);
            return true;
        }
    }

    /**
     * 删除好友关系（被FriendController调用）
     * 业务逻辑：按userId和friendId删除记录
     * 异常场景：无记录时返回false
     *
     * @param userId   用户ID（必填）
     * @param friendId 好友ID（必填）
     * @return true-删除成功，false-未找到记录
     */
    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        return friendMapper.deleteByUserIdAndFriendId(userId, friendId) > 0;
    }

    /**
     * 获取用户好友列表（分页，被FriendController调用）
     * 业务逻辑：PageHelper分页查userId方向的好友记录
     * 异常场景：无好友时返回空PageInfo
     *
     * @param userId   用户ID（必填）
     * @param pageNum  页码（必填）
     * @param pageSize 每页条数（必填）
     * @return 好友分页对象
     */
    @Override
    public PageInfo<Friend> getFriendList(Long userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        var friends = friendMapper.selectByUserId(userId);
        return PageInfo.of(friends);
    }

    /**
     * 判断两人是否为好友（被FriendController调用）
     * 业务逻辑：查friend表按userId-friendId计数 → count>0返回true
     * 异常场景：非好友关系返回false
     *
     * @param userId   用户ID（必填）
     * @param friendId 好友ID（必填）
     * @return true-是好友，false-不是好友
     */
    @Override
    public boolean isFriend(Long userId, Long friendId) {
        int count = friendMapper.countByUserIdAndFriendId(userId, friendId);
        return count > 0;
    }

    /**
     * 按关键词搜索用户（被FriendController调用）
     * 业务逻辑：直接查sys_user表按关键词模糊匹配
     * 异常场景：无匹配结果返回空列表
     *
     * @param keyword 搜索关键词（必填）
     * @return 匹配的用户列表
     */
    @Override
    public List<SysUser> searchUsers(String keyword) {
        return sysUserMapper.searchByKeyword(keyword);
    }

    /**
     * 搜索用户并标记好友状态（用于添加好友弹窗）
     * 业务逻辑：按关键词搜索用户 → 过滤掉自己 → 双向查friend表标记好友状态（accepted/pending/none）→ 加载用户头像（默认占位图）
     * 异常场景：无匹配结果返回空列表；头像为null时使用默认头像URL
     *
     * @param currentUserId 当前登录用户ID（必填，用于过滤自己和标记好友状态）
     * @param keyword       搜索关键词（必填）
     * @return 用户信息Map列表，含id/username/nickname/phone/avatar/friendStatus
     */
    @Override
    public List<java.util.Map<String, Object>> searchUsersWithStatus(Long currentUserId, String keyword) {
        List<SysUser> users = sysUserMapper.searchByKeyword(keyword);
        List<java.util.Map<String, Object>> result = new ArrayList<>();
        for (SysUser user : users) {
            if (user.getId().equals(currentUserId)) continue; // 过滤自己
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", user.getId());
            map.put("username", user.getUsername());
            map.put("nickname", user.getNickname());
            map.put("phone", user.getPhone());
            map.put("avatar", user.getAvatar() != null ? user.getAvatar() : "https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg");
            // 检查好友状态
            int count = friendMapper.countByUserIdAndFriendId(currentUserId, user.getId());
            if (count > 0) {
                Friend friend = friendMapper.selectByUserIdAndFriendId(currentUserId, user.getId());
                if (friend != null && friend.getStatus() == 1) {
                    map.put("friendStatus", "accepted"); // 已添加
                } else if (friend != null && friend.getStatus() == 0) {
                    map.put("friendStatus", "pending"); // 待确认
                }
            } else {
                // 检查反向
                Friend reverse = friendMapper.selectByUserIdAndFriendId(user.getId(), currentUserId);
                if (reverse != null && reverse.getStatus() == 1) {
                    map.put("friendStatus", "accepted");
                } else if (reverse != null && reverse.getStatus() == 0) {
                    map.put("friendStatus", "pending");
                } else {
                    map.put("friendStatus", "none");
                }
            }
            result.add(map);
        }
        return result;
    }

    /**
     * 发送好友申请（status=0待确认，被FriendController调用）
     * 业务逻辑：校验不能添加自己 → 查是否已存在申请 → 已存在返回false → 不存在则插入status=0的记录
     * 异常场景：添加自己返回false；重复申请返回false
     *
     * @param userId   申请发起者ID（必填）
     * @param friendId 申请接收者ID（必填）
     * @return true-发送成功，false-添加自己或重复申请
     */
    @Override
    public boolean sendFriendRequest(Long userId, Long friendId) {
        // 检查是否是自己
        if (userId.equals(friendId)) {
            return false;
        }
        
        // 检查是否已经发送过好友申请
        var friend = friendMapper.selectByUserIdAndFriendId(userId, friendId);
        if (friend != null) {
            // 已经发送过好友申请
            return false;
        } else {
            // 发送好友申请
            var newFriend = new Friend();
            newFriend.setUserId(userId);
            newFriend.setFriendId(friendId);
            newFriend.setStatus(0); // 0-待确认
            friendMapper.insert(newFriend);
            return true;
        }
    }

    /**
     * 获取用户收到的好友申请列表（status=0待确认，被FriendController调用）
     * 业务逻辑：查friend表friendId为userId且status=0的记录 → 映射查出申请者SysUser信息 → 过滤null用户
     * 异常场景：无申请时返回空列表
     *
     * @param userId 当前用户ID（必填，作为申请接收者）
     * @return 申请者SysUser列表
     */
    @Override
    public List<SysUser> getFriendRequests(Long userId) {
        // 获取所有friend_id为当前用户且status为0的好友申请
        var friendRequests = friendMapper.selectByFriendIdAndStatus(userId, 0);
        return friendRequests.stream()
                .map(request -> sysUserMapper.selectById(request.getUserId()))
                .filter(user -> user != null)
                .toList();
    }

    /**
     * 同意好友申请（被FriendController调用）
     * 业务逻辑：查friend表找申请记录（发起者friendId→接收者userId方向且status=0） → 找到则更新status=1（已确认）
     * 异常场景：申请不存在或非待确认状态返回false
     *
     * @param userId   当前用户ID（必填，申请接收者）
     * @param friendId 申请发起者ID（必填）
     * @return true-同意成功，false-申请不存在或状态异常
     */
    @Override
    public boolean agreeFriendRequest(Long userId, Long friendId) {
        // 查找好友申请记录
        var friend = friendMapper.selectByUserIdAndFriendId(friendId, userId);
        if (friend != null && friend.getStatus() == 0) {
            // 更新状态为已同意
            friend.setStatus(1);
            friendMapper.updateStatus(friend);
            return true;
        }
        return false;
    }

    /**
     * 拒绝好友申请（被FriendController调用）
     * 业务逻辑：查friend表找申请记录 → 找到且status=0则更新status=2（已拒绝）
     * 异常场景：申请不存在或非待确认状态返回false
     *
     * @param userId   当前用户ID（必填，申请接收者）
     * @param friendId 申请发起者ID（必填）
     * @return true-拒绝成功，false-申请不存在或状态异常
     */
    @Override
    public boolean refuseFriendRequest(Long userId, Long friendId) {
        // 查找好友申请记录
        var friend = friendMapper.selectByUserIdAndFriendId(friendId, userId);
        if (friend != null && friend.getStatus() == 0) {
            // 更新状态为已拒绝
            friend.setStatus(2);
            friendMapper.updateStatus(friend);
            return true;
        }
        return false;
    }

    /**
     * 获取用户所有已确认的好友（status=1，双向查询去重，被FriendController调用）
     * 业务逻辑：查userId方向status=1的记录 → 查friendId方向status=1的记录 → 合并两个方向的friendId并去重 → 排除自己 → 逐条查SysUser并组装列表
     * 异常场景：无好友时返回空列表
     *
     * @param userId 用户ID（必填）
     * @return 好友SysUser列表（去重）
     */
    @Override
    public List<SysUser> getFriends(Long userId) {
        // 获取所有user_id为当前用户且status为1的好友
        var friends1 = friendMapper.selectByUserIdAndStatus(userId, 1);
        // 获取所有friend_id为当前用户且status为1的好友
        var friends2 = friendMapper.selectByFriendIdAndStatus(userId, 1);
        
        // 合并两个列表并去重
        Set<Long> friendIds = new HashSet<>();
        List<SysUser> result = new ArrayList<>();
        
        // 处理第一个列表
        for (Friend friend : friends1) {
            Long friendId = friend.getFriendId();
            if (!friendId.equals(userId) && friendIds.add(friendId)) {
                var user = sysUserMapper.selectById(friendId);
                if (user != null) {
                    result.add(user);
                }
            }
        }
        
        // 处理第二个列表
        for (Friend friend : friends2) {
            Long friendId = friend.getUserId();
            if (!friendId.equals(userId) && friendIds.add(friendId)) {
                var user = sysUserMapper.selectById(friendId);
                if (user != null) {
                    result.add(user);
                }
            }
        }
        
        return result;
    }

    /**
     * 更新好友备注昵称（被FriendController调用）
     * 业务逻辑：尝试更新已有好友记录的friend_nickname字段 → 若无记录（affectedRows=0）则创建新的status=1好友记录并设置备注
     * 异常场景：数据库异常时返回false
     *
     * @param userId         用户ID（必填）
     * @param friendId       好友ID（必填）
     * @param friendNickname 备注昵称（必填）
     * @return true-更新成功，false-操作失败
     */
    @Override
    public boolean updateFriendNickname(Long userId, Long friendId, String friendNickname) {
        logger.info("updateFriendNickname called - userId: {}, friendId: {}, nickname: {}", userId, friendId, friendNickname);
        try {
            int affectedRows = friendMapper.updateFriendNickname(userId, friendId, friendNickname);
            if (affectedRows == 0) {
                // 当前方向没有记录，创建新记录而不是修改反向记录
                Friend newFriend = new Friend();
                newFriend.setUserId(userId);
                newFriend.setFriendId(friendId);
                newFriend.setFriendNickname(friendNickname);
                newFriend.setStatus(1);
                friendMapper.insert(newFriend);
                affectedRows = 1;
            }
            logger.info("updateFriendNickname result - affectedRows: {}", affectedRows);
            return affectedRows > 0;
        } catch (Exception e) {
            logger.error("updateFriendNickname error", e);
            return false;
        }
    }

    /**
     * 获取好友备注昵称（被FriendController和ChatServiceImpl调用）
     * 业务逻辑：查friend表按userId-friendId查询friend_nickname字段
     * 异常场景：无记录或异常时返回null
     *
     * @param userId   用户ID（必填）
     * @param friendId 好友ID（必填）
     * @return 备注昵称，无备注时返回null
     */
    @Override
    public String getFriendNickname(Long userId, Long friendId) {
        try {
            return friendMapper.selectFriendNickname(userId, friendId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
