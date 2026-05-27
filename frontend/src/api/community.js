import request from '../utils/request'

// 社区相关API
export const communityApi = {
  // 获取热门话题列表
  getHotTopics: () => {
    return request({
      url: '/community/hot-topics',
      method: 'GET'
    })
  },

  // 按关键词搜索帖子（按评论数排序）
  searchPostsByKeyword: (keyword) => {
    return request({
      url: `/community/post/search/${encodeURIComponent(keyword)}`,
      method: 'GET'
    })
  }
}

// 帖子相关API
export const postApi = {
  // 获取帖子列表
  getPostList: (params) => {
    return request({
      url: '/community/post/list',
      method: 'GET',
      params
    })
  },
  
  // 获取帖子详情
  getPostDetail: (id) => {
    return request({
      url: `/community/post/${id}`,
      method: 'GET'
    })
  },
  
  // 发布帖子
  createPost: (data) => {
    return request({
      url: '/community/post',
      method: 'POST',
      data
    })
  },
  
  // 编辑帖子
  updatePost: (id, data) => {
    return request({
      url: `/community/post/${id}`,
      method: 'PUT',
      data
    })
  },
  
  // 删除帖子
  deletePost: (id) => {
    return request({
      url: `/community/post/${id}`,
      method: 'DELETE'
    })
  },
  
  // 点赞/取消点赞帖子
  likePost: (id) => {
    return request({
      url: `/community/post/${id}/like`,
      method: 'POST'
    })
  },
  
  // 收藏/取消收藏帖子
  collectPost: (id) => {
    return request({
      url: `/community/post/${id}/collect`,
      method: 'POST'
    })
  },
  
  // 获取帖子状态（是否点赞、收藏）
  getPostStatus: (id) => {
    return request({
      url: `/community/post/${id}/status`,
      method: 'GET'
    })
  },
  
  // 获取用户帖子列表
  getUserPosts: (userId) => {
    return request({
      url: `/community/post/user/${userId}`,
      method: 'GET'
    })
  },
  
  // 获取用户收藏列表
  getUserCollections: (userId) => {
    return request({
      url: `/community/post/collections/${userId}`,
      method: 'GET'
    })
  },
  
  // 获取用户点赞列表
  getUserLikes: (userId) => {
    return request({
      url: `/community/post/likes/${userId}`,
      method: 'GET'
    })
  },
  
  // 获取用户帖子数量
  getPostCountByUserId: (userId) => {
    return request({
      url: `/community/post/count/${userId}`,
      method: 'GET'
    })
  },
  
  // 获取帖子点赞数和收藏数
  getPostInfo: (postId) => {
    return request({
      url: `/community/post/info/${postId}`,
      method: 'GET'
    })
  }
}

// 评论相关API
export const commentApi = {
  // 获取评论列表
  getCommentList: (postId, params) => {
    return request({
      url: `/community/comment/list/${postId}`,
      method: 'GET',
      params
    })
  },
  
  // 发布评论/回复
  createComment: (data) => {
    return request({
      url: '/community/comment',
      method: 'POST',
      data
    })
  },
  
  // 点赞/取消点赞评论
  likeComment: (id, postId) => {
    return request({
      url: `/community/comment/${id}/like`,
      method: 'POST',
      params: { postId }
    })
  },
  
  // 获取评论点赞状态
  getCommentStatus: (id) => {
    return request({
      url: `/community/comment/${id}/status`,
      method: 'GET'
    })
  },
  
  // 删除评论
  deleteComment: (id) => {
    return request({
      url: `/community/comment/${id}`,
      method: 'DELETE'
    })
  }
}

// 关注相关API
export const followApi = {
  // 关注/取消关注用户
  followUser: (data) => {
    return request({
      url: '/community/follow',
      method: 'POST',
      params: data
    })
  },
  
  // 获取关注/粉丝列表
  getFollowList: (userId, type) => {
    return request({
      url: `/community/follow/list/${userId}`,
      method: 'GET',
      params: { 
        isFollowing: type === 'following',
        pageNum: 1,
        pageSize: 20
      }
    })
  },
  
  // 获取推荐用户列表
  getRecommendUsers: (params) => {
    return request({
      url: '/community/follow/recommend',
      method: 'GET',
      params
    })
  },

  // 获取关注和粉丝数量
  getFollowCount: (userId) => {
    return request({
      url: `/community/follow/count/${userId}`,
      method: 'GET'
    })
  },
  
  // 获取关注与取关通知
  getFollowNotices: () => {
    return request({
      url: '/community/follow/notices',
      method: 'GET'
    })
  },

  // 标记单条关注通知为已读
  markFollowNoticeAsRead: (noticeId) => {
    return request({
      url: `/community/follow/notices/${noticeId}/read`,
      method: 'POST'
    })
  },

  // 标记所有关注通知为已读
  markAllFollowNoticesAsRead: () => {
    return request({
      url: '/community/follow/notices/read-all',
      method: 'POST'
    })
  },

  // 获取未读关注通知数量
  getUnreadFollowNoticeCount: () => {
    return request({
      url: '/community/follow/notices/unread/count',
      method: 'GET'
    })
  },

  // 删除所有已读关注通知
  deleteAllReadFollowNotices: () => {
    return request({
      url: '/community/follow/notices/read/all',
      method: 'DELETE'
    })
  }
}

// 互动通知相关API
export const interactionApi = {
  // 获取互动通知列表
  getInteractionNotices: () => {
    return request({
      url: '/community/interaction/notices',
      method: 'GET'
    })
  },

  // 标记单条互动通知为已读
  markInteractionNoticeAsRead: (noticeId) => {
    return request({
      url: `/community/interaction/notices/${noticeId}/read`,
      method: 'POST'
    })
  },

  // 标记所有互动通知为已读
  markAllInteractionNoticesAsRead: () => {
    return request({
      url: '/community/interaction/notices/read-all',
      method: 'POST'
    })
  },

  // 获取未读互动通知数量
  getUnreadInteractionNoticeCount: () => {
    return request({
      url: '/community/interaction/notices/unread/count',
      method: 'GET'
    })
  },

  // 删除所有已读互动通知
  deleteAllReadInteractionNotices: () => {
    return request({
      url: '/community/interaction/notices/read/all',
      method: 'DELETE'
    })
  },

  // 获取未读评论通知数量
  getUnreadCommentNoticeCount: () => {
    return request({
      url: '/community/interaction/comment-notices/unread/count',
      method: 'GET'
    })
  },

  // 删除所有已读评论通知
  deleteAllReadCommentNotices: () => {
    return request({
      url: '/community/interaction/comment-notices/read/all',
      method: 'DELETE'
    })
  },

  // 获取评论通知列表
  getCommentNotices: () => {
    return request({
      url: '/community/interaction/comment-notices',
      method: 'GET'
    })
  },

  // 标记单条评论通知为已读
  markCommentNoticeAsRead: (noticeId) => {
    return request({
      url: `/community/interaction/comment-notices/${noticeId}/read`,
      method: 'POST'
    })
  },

  // 标记所有评论通知为已读
  markAllCommentNoticesAsRead: () => {
    return request({
      url: '/community/interaction/comment-notices/read-all',
      method: 'POST'
    })
  }
}

// 好友相关API
export const friendApi = {
  // 添加/删除好友
  manageFriend: (data) => {
    return request({
      url: '/community/friend',
      method: 'POST',
      data
    })
  },
  
  // 获取好友列表
  getFriendList: (userId) => {
    return request({
      url: `/community/friend/list/${userId}`,
      method: 'GET'
    })
  },
  
  // 搜索用户
  searchUsers: (keyword) => {
    return request({
      url: `/community/friend/search?keyword=${keyword}`,
      method: 'GET'
    })
  },
  
  // 发送好友申请
  sendFriendRequest: (friendId) => {
    return request({
      url: `/community/friend/apply?friendId=${friendId}`,
      method: 'POST'
    })
  },
  
  // 获取好友申请列表
  getFriendRequests: () => {
    return request({
      url: '/community/friend/apply/list',
      method: 'GET'
    })
  },
  
  // 同意好友申请
  agreeFriendRequest: (friendId) => {
    return request({
      url: `/community/friend/agree?friendId=${friendId}`,
      method: 'POST'
    })
  },
  
  // 拒绝好友申请
  refuseFriendRequest: (friendId) => {
    return request({
      url: `/community/friend/refuse?friendId=${friendId}`,
      method: 'POST'
    })
  },
  // 获取好友列表
  getFriends: () => {
    return request({
      url: '/community/friend/list/accepted',
      method: 'GET'
    })
  },
  // 获取好友列表（聊天用）
  getFriendListForChat: () => {
    return request({
      url: '/community/friend/list/chat',
      method: 'GET'
    })
  },
  // 发送消息
  sendMessage: (toId, content) => {
    return request({
      url: '/community/friend/chat/send',
      method: 'POST',
      params: {
        toId,
        content
      }
    })
  },
  // 获取聊天记录
  getChatHistory: (toId) => {
    return request({
      url: `/community/friend/chat/history/${toId}`,
      method: 'GET'
    })
  },
  // 标记消息为已读
  markRead: (friendId) => {
    return request({
      url: '/community/friend/chat/mark-read',
      method: 'POST',
      params: {
        friendId
      }
    })
  },
  // 查找聊天记录
  searchChatHistory: (targetUserId, keyword, page, size) => {
    return request({
      url: '/community/friend/chat/search',
      method: 'GET',
      params: {
        targetUserId,
        keyword,
        page,
        size
      }
    })
  },
  // 设置聊天置顶
  setChatTop: (targetUserId, isTop) => {
    return request({
      url: '/community/friend/chat/top',
      method: 'POST',
      params: {
        targetUserId,
        isTop
      }
    })
  },
  // 获取聊天置顶状态
  getChatTopStatus: (targetUserId) => {
    return request({
      url: '/community/friend/chat/top/status',
      method: 'GET',
      params: {
        targetUserId
      }
    })
  },
  // 清空聊天记录
  clearChatHistory: (targetUserId) => {
    return request({
      url: '/community/friend/chat/clear',
      method: 'DELETE',
      params: {
        targetUserId
      }
    })
  },
  // 投诉用户
  submitComplaint: (targetUserId, sessionId, reason) => {
    return request({
      url: '/community/friend/chat/complaint',
      method: 'POST',
      params: {
        targetUserId,
        sessionId,
        reason
      }
    })
  },
  // 设置好友备注
  updateFriendNickname: (friendId, nickname) => {
    return request({
      url: '/community/friend/nickname',
      method: 'POST',
      params: {
        friendId,
        nickname
      }
    })
  },
  // 获取好友备注
  getFriendNickname: (friendId) => {
    return request({
      url: '/community/friend/nickname',
      method: 'GET',
      params: {
        friendId
      }
    })
  }
}

// 用户相关API
export const userApi = {
  // 获取用户主页信息
  getUserInfo: (userId) => {
    return request({
      url: `/community/user/${userId}`,
      method: 'GET'
    })
  },
  
  // 获取用户头像（优先从Redis缓存获取）
  getUserAvatar: (userId) => {
    return request({
      url: `/community/user/avatar/${userId}`,
      method: 'GET'
    })
  },

  // 更新用户资料
  updateProfile: (data) => {
    return request({
      url: '/community/user/profile',
      method: 'PUT',
      data
    })
  },

  // 登出
  logout: () => {
    return request({
      url: '/user/logout',
      method: 'POST'
    })
  }
}

// 用户分类相关API
export const userCategoryApi = {
  // 获取用户分类列表
  getUserCategories: () => {
    return request({
      url: '/user-category',
      method: 'GET'
    })
  },

  // 添加分类
  addCategory: (categoryName) => {
    return request({
      url: '/user-category/add',
      method: 'POST',
      data: { categoryName }
    })
  },

  // 删除分类
  removeCategory: (categoryName) => {
    return request({
      url: '/user-category/remove',
      method: 'POST',
      data: { categoryName }
    })
  },

  // 更新分类列表
  updateCategories: (categories) => {
    return request({
      url: '/user-category',
      method: 'PUT',
      data: { categories }
    })
  },

  // 获取所有可选分类
  getAllCategories: () => {
    return request({
      url: '/user-category/all',
      method: 'GET'
    })
  }
}

// OSS上传API
export const ossApi = {
  // 上传图片
  uploadImage: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/oss/upload',
      method: 'POST',
      data: formData
    })
  }
}
