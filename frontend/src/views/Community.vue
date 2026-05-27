<template>
  <div class="community-page">
    <!-- 顶部导航栏 -->
    <div class="page-header">
      <div class="header-content">
        <div class="back-btn-container">
          <el-button @click="goToHome" class="back-btn">
            <el-icon><ArrowLeft /></el-icon>
            返回首页
          </el-button>
        </div>
        <div class="title-container">
          <h2>🌸 分享社区</h2>
        </div>
        <div class="empty-container"></div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="community-container">
      <!-- 左侧导航栏 -->
      <div class="left-sidebar">
        <div class="sidebar-item active">
          <el-icon><Search /></el-icon>
          <span>发现</span>
        </div>
        <div class="sidebar-item" @click="toggleChatWindow" ref="sidebarFriendBtn">
          <el-icon><ChatLineRound /></el-icon>
          <span>好友</span>
        </div>
        <div class="sidebar-item" @click="goToPublish">
          <el-icon><Plus /></el-icon>
          <span>发布</span>
        </div>
        <div class="sidebar-item notice-item" @click="toggleNoticeMenu">
          <el-icon><Bell /></el-icon>
          <span>通知</span>
          <div class="unread-badge" v-if="unreadCount > 0">{{ unreadCount }}</div>
          <!-- 通知下拉菜单 -->
          <div class="notice-menu" v-if="showNoticeMenu">
            <div class="menu-item" @click="openFriendRequestModal">
              好友申请
              <div class="menu-badge" v-if="friendRequestCount > 0">{{ friendRequestCount }}</div>
            </div>
            <div class="menu-item" @click="openFollowNoticeModal">
              关注与取关
              <div class="menu-badge" v-if="followNoticeCount > 0">{{ followNoticeCount }}</div>
            </div>
            <div class="menu-item" @click="openInteractionNoticeModal">
              点赞与收藏
              <div class="menu-badge" v-if="interactionNoticeCount > 0">{{ interactionNoticeCount }}</div>
            </div>
            <div class="menu-item" @click="openCommentNoticeModal">
              评论通知
              <div class="menu-badge" v-if="commentNoticeCount > 0">{{ commentNoticeCount }}</div>
            </div>
          </div>
        </div>
        <div class="sidebar-item more-item" @click="toggleSidebarMoreMenu">
          <el-icon><More /></el-icon>
          <span>更多</span>
          <!-- 下拉菜单 -->
          <div class="more-menu" v-if="showSidebarMoreMenu">
            <div class="menu-item" @click="openAddFriendModal">添加好友</div>
            <div class="menu-item" @click="openHotVideos">热门视频</div>
          </div>
        </div>
      </div>

      <!-- 微信式聊天窗口 -->
      <div class="chat-window" v-if="showChatWindow" ref="chatWindowRef">
        <!-- 左侧好友列表 -->
        <div class="chat-left-panel">
          <!-- 搜索框 -->
          <div class="chat-search">
            <el-input
              v-model="chatSearchKeyword"
              placeholder="搜索好友"
              prefix-icon="el-icon-search"
            />
          </div>
          <!-- 好友列表 -->
          <div class="friend-list">
            <div v-if="friendListLoading" class="loading">
              <el-skeleton :rows="5" animated />
            </div>
            <div v-else-if="friendList.length === 0" class="empty-list">
              <div class="empty-illustration">
                <div class="empty-icon-bg">
                  <svg class="empty-friends-icon" viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <!-- 两个人物剪影 -->
                    <circle cx="42" cy="38" r="16" fill="#FFB8B8" opacity="0.8"/>
                    <path d="M16 88c0-14 12-24 26-24s26 10 26 24" fill="#FFB8B8" opacity="0.8"/>
                    <circle cx="78" cy="44" r="14" fill="#FFC8C8" opacity="0.7"/>
                    <path d="M54 92c0-12 10-22 24-22s24 10 24 22" fill="#FFC8C8" opacity="0.7"/>
                    <!-- 连接的心形 -->
                    <path d="M60 70c-1-3-5-5-8-2-2 2-2 5 0 7l8 8 8-8c2-2 2-5 0-7-3-3-7-1-8 2z" fill="#FF9999" opacity="0.9"/>
                  </svg>
                </div>
                <div class="empty-bubble bubble-1">💬</div>
                <div class="empty-bubble bubble-2">💕</div>
                <div class="empty-bubble bubble-3">✨</div>
              </div>
              <p class="empty-title">还没有好友哦</p>
              <p class="empty-desc">快去寻找你的小伙伴吧～</p>
              <button class="empty-action-btn" @click="openAddFriendModal">
                <span>+</span> 添加好友
              </button>
            </div>
            <div
              v-else
              v-for="friend in friendList"
              :key="friend.id"
              class="friend-item"
              :class="{ active: selectedFriendId === friend.id, top: friend.top }"
              @click="selectFriend(friend)"
            >
              <div class="friend-avatar">
                <SafeImage :src="friend.avatar" fallback="https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg" alt="好友头像" />
              </div>
              <div class="friend-info">
                <div class="friend-name">
                  {{ friend.nickname || friend.username }}
                  <span v-if="friend.top" class="top-badge">📌</span>
                </div>
                <div class="friend-last-message">{{ friend.lastMessage || '暂无消息' }}</div>
              </div>
              <div class="friend-time">{{ formatTime(friend.lastMessageTime) }}</div>
              <div class="unread-badge" v-if="friend.unreadCount > 0">{{ friend.unreadCount }}</div>
            </div>
          </div>
        </div>

        <!-- 右侧聊天窗口 -->
        <div class="chat-right-panel" v-if="selectedFriend">
          <!-- 聊天头部 -->
          <div class="chat-header">
            <div class="chat-header-info">
              <div class="chat-header-avatar">
                <SafeImage :src="selectedFriend.avatar" fallback="https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg" alt="好友头像" />
              </div>
              <div class="chat-header-name">{{ friendNickname || selectedFriend.nickname || selectedFriend.username }}</div>
            </div>
            <div class="chat-header-actions">
              <span class="chat-more" @click="toggleChatMoreMenu" ref="chatMoreBtn">更多</span>
              <el-icon class="chat-close" @click="closeChatWindow"><Close /></el-icon>
            </div>
            
            <!-- 更多菜单 -->
            <div v-if="showChatMoreMenu" class="chat-more-menu" ref="chatMoreMenu" @click.stop>
              <div class="menu-item" @click="handleSetNickname">
                <span class="menu-icon">✏️</span>
                <span>{{ friendNickname ? '修改备注' : '设置备注' }}</span>
              </div>
              <div class="menu-item" @click="handleSearchChat">
                <span class="menu-icon">🔍</span>
                <span>查找聊天记录</span>
              </div>
              <div class="menu-item" @click="handleToggleTop">
                <span class="menu-icon">📌</span>
                <span>{{ isChatTop ? '取消置顶聊天' : '置顶聊天' }}</span>
              </div>
              <div class="menu-divider"></div>
              <div class="menu-item menu-danger" @click="handleClearChat">
                <span class="menu-icon">🗑️</span>
                <span>清空聊天记录</span>
              </div>
              <div class="menu-item menu-danger" @click="handleComplaint">
                <span class="menu-icon">⚠️</span>
                <span>投诉</span>
              </div>
            </div>
          </div>

          <!-- 聊天内容 -->
          <div class="chat-content" ref="chatContent">
            <div v-if="chatLoading" class="loading">
              <el-skeleton :rows="10" animated />
            </div>
            <div v-else-if="chatMessages.length === 0" class="empty-chat">
              开始聊天吧
            </div>
            <div v-else class="message-list">
              <div
                v-for="(message, index) in chatMessages"
                :key="message.id || index"
                class="message-item"
                :class="{ 'self-message': message.fromId === currentUserId, 'other-message': message.fromId !== currentUserId }"
              >
                <div class="message-avatar" v-if="message.fromId !== currentUserId">
                  <SafeImage :src="selectedFriend.avatar" :fallback="'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'" alt="好友头像" :lazy="false" />
                </div>
                <div class="message-bubble" :class="{ 'self-bubble': message.fromId === currentUserId, 'other-bubble': message.fromId !== currentUserId }">
                  <div class="message-content">{{ message.content }}</div>
                  <div class="message-footer">
                    <div class="message-time">{{ formatTime(message.createTime) }}</div>
                    <div class="message-status" v-if="message.fromId === currentUserId">
                      <span v-if="message.status === 'sending'" class="status-sending">发送中...</span>
                      <span v-else-if="message.status === 'failed'" class="status-failed" @click="resendMessage(message)">发送失败，点击重试</span>
                      <span v-else-if="message.status === 'sent'" class="status-sent">已发送</span>
                    </div>
                  </div>
                </div>
                <div class="message-avatar" v-if="message.fromId === currentUserId">
                  <SafeImage :src="currentUser.avatar" :fallback="'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'" alt="我的头像" :lazy="false" />
                </div>
              </div>
            </div>
          </div>

          <!-- 聊天输入框 -->
          <div class="chat-input">
            <el-input
              v-model="chatInput"
              type="textarea"
              placeholder="输入消息..."
              :rows="1"
              @keyup.enter.exact="sendMessage"
            />
            <div class="chat-actions">
              <button class="emoji-btn" @click="toggleEmojiPicker" ref="emojiBtn">
                <span class="emoji-icon">😊</span>
              </button>
              <el-button type="primary" @click="sendMessage">发送</el-button>
            </div>
            
            <!-- 表情选择器 -->
            <div v-if="showEmojiPicker" class="emoji-picker" ref="emojiPicker">
              <div class="emoji-categories">
                <button 
                  v-for="cat in emojiCategories" 
                  :key="cat.name"
                  class="category-btn"
                  :class="{ active: activeEmojiCategory === cat.name }"
                  @click="activeEmojiCategory = cat.name"
                >
                  {{ cat.icon }}
                </button>
              </div>
              <div class="emoji-list">
                <span 
                  v-for="emoji in filteredEmojis" 
                  :key="emoji"
                  class="emoji-item"
                  @click="insertEmoji(emoji)"
                >{{ emoji }}</span>
              </div>
            </div>
          </div>

          <!-- 搜索聊天记录弹窗 -->
          <el-dialog
            title="查找聊天记录"
            v-model="showSearchModal"
            width="500px"
            :close-on-click-modal="true"
            @close="resetChatSearch"
          >
            <div class="search-chat-container">
              <div class="search-input-wrapper">
                <el-input
                  v-model="chatSearchModalKeyword"
                  placeholder="搜索消息内容..."
                  @input="handleSearchInput"
                  class="search-input"
                />
              </div>
              <div v-if="chatSearchLoading" class="search-loading">
                <el-spinner size="medium" />
                <span class="loading-text">搜索中...</span>
              </div>
              <div v-else-if="chatSearchResults.length > 0" class="search-results">
                <div
                  v-for="(msg, index) in chatSearchResults"
                  :key="index"
                  class="search-result-item"
                >
                  <div class="result-avatar">
                    <SafeImage :src="msg.avatar || 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'" fallback="https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg" alt="用户头像" />
                  </div>
                  <div class="result-info">
                    <div class="result-content">{{ msg.content }}</div>
                    <div class="result-time">{{ formatTime(msg.createTime) }}</div>
                  </div>
                </div>
              </div>
            </div>
          </el-dialog>

          <!-- 投诉弹窗 -->
          <el-dialog
            title="投诉"
            v-model="showComplaintModal"
            width="400px"
            :close-on-click-modal="true"
          >
            <div class="complaint-container">
              <p class="complaint-tip">请简要描述您的投诉原因：</p>
              <el-input
                v-model="complaintReason"
                type="textarea"
                :rows="4"
                placeholder="请输入投诉原因..."
                class="complaint-textarea"
              />
            </div>
            <template #footer>
              <el-button @click="showComplaintModal = false">取消</el-button>
              <el-button type="primary" @click="submitComplaint">提交投诉</el-button>
            </template>
          </el-dialog>

          <!-- 设置备注弹窗 -->
          <el-dialog
            title="设置备注"
            v-model="showNicknameModal"
            width="400px"
            :close-on-click-modal="true"
          >
            <div class="nickname-container">
              <p class="nickname-tip">请输入备注名称：</p>
              <el-input
                v-model="nicknameInput"
                placeholder="请输入备注..."
                class="nickname-input"
                maxlength="20"
              />
            </div>
            <template #footer>
              <el-button @click="showNicknameModal = false">取消</el-button>
              <el-button type="primary" @click="saveNickname">保存</el-button>
            </template>
          </el-dialog>
        </div>
        <div class="chat-right-panel empty-chat-panel" v-else>
          <div class="empty-chat-tip">
            <el-icon class="empty-icon"><ChatLineRound /></el-icon>
            <p>选择一个好友开始聊天</p>
          </div>
        </div>
      </div>

      <!-- 主内容区 -->
      <div class="main-content">
        <!-- 搜索栏 -->
        <div class="search-bar">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索内容、用户或标签"
            prefix-icon="el-icon-search"
            @keyup.enter="handleSearch"
          />
        </div>

        <!-- 分类标签栏 -->
        <div class="category-tabs-wrapper">
          <div 
            class="category-tabs"
            ref="categoryTabsRef"
            @wheel.prevent="handleWheelScroll"
          >
            <div 
              v-for="category in categories" 
              :key="category"
              class="category-tab"
              :class="{ active: activeCategory === category }"
              @click="switchCategory(category)"
            >
              {{ category }}
              <span 
                v-if="isEditing && category !== '推荐'" 
                class="category-delete" 
                @click.stop="removeCategory(category)"
              >
                ×
              </span>
            </div>
          </div>
          <div class="category-actions">
            <button 
              v-if="isEditing"
              class="add-category-btn"
              @click="openAddModal"
            >
              + 添加
            </button>
            <button 
              class="edit-categories-btn"
              @click="toggleEditMode"
            >
              {{ isEditing ? '完成' : '编辑' }}
            </button>
          </div>
        </div>

        <!-- 瀑布流帖子列表 -->
        <div class="post-masonry">
          <div v-if="loading && postList.length === 0" class="skeleton-container">
            <div v-for="i in 6" :key="i" class="skeleton-card">
              <div class="skeleton-image"></div>
              <div class="skeleton-content">
                <div class="skeleton-title"></div>
                <div class="skeleton-stats">
                  <div class="skeleton-stat">
                    <div class="skeleton-stat-icon"></div>
                    <div class="skeleton-stat-text"></div>
                  </div>
                  <div class="skeleton-stat">
                    <div class="skeleton-stat-icon"></div>
                    <div class="skeleton-stat-text"></div>
                  </div>
                  <div class="skeleton-stat">
                    <div class="skeleton-stat-icon"></div>
                    <div class="skeleton-stat-text"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div 
            v-for="post in postList" 
            :key="post.id"
            class="post-card"
            @click="goToPostDetail(post.id)"
          >
            <div class="post-image">
              <SafeImage 
                :src="getFirstImage(post.images)" 
                :alt="post.title" 
                :lazy="true"
                :preview="true"
                :showSkeleton="true"
                rootMargin="200px"
              />
            </div>
            <div class="post-info">
              <h3 class="post-title">{{ post.title }}</h3>
              <div class="post-stats">
                <div class="stat-item">
                  <span class="browse-icon">👁</span>
                  <span>{{ post.browseCount || 0 }}</span>
                </div>
                <div class="stat-item">
                  <el-icon><Star /></el-icon>
                  <span>{{ post.likeCount }}</span>
                </div>
                <div class="stat-item">
                  <el-icon><Collection /></el-icon>
                  <span>{{ post.collectCount }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载更多 -->
        <div class="load-more">
          <el-button 
            v-if="hasMore" 
            type="primary" 
            plain 
            @click="loadMore"
            :loading="loading"
            :disabled="loading"
          >
            加载更多
          </el-button>
          <div v-else class="no-more">
            没有更多内容了
          </div>
        </div>
        

      </div>

      <!-- 右侧信息栏 -->
      <div class="right-sidebar">
        <!-- 用户信息 -->
        <div class="user-info" @click="goToUserHome">
          <div class="avatar">
            <SafeImage :src="currentUser.avatar" fallback="https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg" alt="用户头像" />
          </div>
          <div class="user-details">
            <h3>我的主页</h3>
            <p>查看我的作品</p>
          </div>
        </div>

        <!-- 推荐关注 -->
        <div class="recommend-section">
          <h4>推荐关注</h4>
          <div class="recommend-list">
            <div v-for="user in recommendUsers" :key="user.id" class="recommend-item">
              <div class="recommend-avatar" @click="goToUserHome(user.id)">
                <SafeImage :src="user.avatar" fallback="https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg" alt="推荐用户" />
              </div>
              <div class="user-details" @click="goToUserHome(user.id)">
                <h5>{{ user.nickname || user.username }}</h5>
                <p>{{ user.followerCount }} 粉丝</p>
              </div>
            </div>
            <div v-if="recommendLoading" class="loading">
              <el-skeleton :rows="1" animated />
            </div>
            <div v-else-if="recommendUsers.length === 0 && !recommendLoading" class="no-recommend">
              暂无推荐用户
            </div>
          </div>
          <div v-if="hasMoreRecommend" class="more-recommend">
            <el-button type="text" @click="loadRecommendUsers">
              加载更多用户
            </el-button>
          </div>
        </div>

        <!-- 热门话题 -->
        <div class="hot-topics">
          <h4>热门话题</h4>
          <div 
            v-for="(topic, index) in hotTopics" 
            :key="index"
            class="topic-item"
            @click.stop="handleTopicClick(topic.name)"
          >
            <span class="topic-tag"># {{ topic.name }}</span>
            <span class="topic-count">{{ topic.count }} 讨论</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加好友弹窗 -->
    <el-dialog
      v-model="showAddFriendModal"
      title="添加好友"
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="add-friend-container">
        <div class="search-section">
          <el-input
            v-model="friendSearchKeyword"
            placeholder="搜索用户（用户名或昵称）"
            prefix-icon="el-icon-search"
            @keyup.enter="searchUsers"
          />
          <el-button type="primary" @click="searchUsers">搜索</el-button>
        </div>
        
        <div class="search-results">
          <div v-if="searchLoading" class="loading">
            <el-skeleton :rows="3" animated />
          </div>
          <div v-else-if="searchResults.length === 0" class="no-results">
            未查询到相关用户
          </div>
          <div v-else class="user-list">
            <div v-for="user in searchResults" :key="user.id" class="user-item">
              <div class="friend-user-info">
                  <div class="avatar">
                    <img :src="(user.avatar && user.avatar.trim() !== '' && user.avatar !== 'null' && user.avatar !== 'undefined') ? user.avatar : 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'" alt="用户头像" />
                  </div>
                  <div class="user-details">
                    <h4>{{ user.nickname || user.username }}</h4>
                    <p>{{ user.username }}</p>
                  </div>
                </div>
              <el-button v-if="user.friendStatus === 'accepted'" size="small" disabled>已添加</el-button>
              <el-button v-else-if="user.friendStatus === 'pending'" size="small" disabled>待确认</el-button>
              <el-button v-else type="primary" size="small" @click="sendFriendRequest(user.id)">添加好友</el-button>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 好友申请弹窗 -->
    <el-dialog
      v-model="showFriendRequestModal"
      title="好友申请"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="friend-request-container">
        <div v-if="noticeLoading" class="loading">
          <el-skeleton :rows="5" animated />
        </div>
        <div v-else-if="friendRequests.length === 0" class="no-notices">
          <div class="notice-empty-icon">📬</div>
          <p class="notice-empty-title">暂无好友申请</p>
          <p class="notice-empty-desc">新的好友申请会显示在这里</p>
        </div>
        <div v-else class="notice-items">
          <div 
            v-for="request in friendRequests" 
            :key="request.id"
            class="notice-item"
          >
            <div class="notice-avatar">
              <SafeImage :src="request.avatar" fallback="https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg" alt="用户头像" />
            </div>
            <div class="notice-content">
              <div class="notice-text">{{ request.nickname || request.username }} 请求添加你为好友</div>
              <div class="notice-time">{{ formatTime(request.createTime) }}</div>
            </div>
            <div class="notice-actions">
              <el-button type="primary" size="small" @click="agreeFriendRequest(request.id)">同意</el-button>
              <el-button size="small" @click="refuseFriendRequest(request.id)">拒绝</el-button>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 关注与取关通知弹窗 -->
    <el-dialog
      v-model="showFollowNoticeModal"
      title="关注与取关"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="friend-request-container">
        <div v-if="followNoticeLoading" class="loading">
          <el-skeleton :rows="5" animated />
        </div>
        <div v-else-if="followNotices.length === 0" class="no-notices">
          <div class="notice-empty-icon">👀</div>
          <p class="notice-empty-title">暂无关注动态</p>
          <p class="notice-empty-desc">关注和取关通知会显示在这里</p>
        </div>
        <div v-else>
          <div class="notice-header">
            <span>共 {{ followNotices.length }} 条通知</span>
            <div class="notice-actions">
              <el-button 
                type="danger" 
                size="small" 
                @click="deleteAllReadFollowNotices"
              >一键删除</el-button>
              <el-button type="primary" size="small" @click="markAllFollowNoticesAsRead">一键已读</el-button>
            </div>
          </div>
          <div class="notice-items">
            <div 
              v-for="notice in followNotices" 
              :key="notice.id"
              class="notice-item"
              :class="{ 'read': notice.isRead === 1 }"
            >
              <div class="notice-avatar">
                <SafeImage :src="notice.fromUserAvatar" fallback="https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg" alt="用户头像" />
              </div>
              <div class="notice-content">
                <div class="notice-text">{{ notice.fromUserNickname }} {{ notice.type === 1 ? '关注了你' : '取消关注你' }}</div>
                <div class="notice-time">{{ formatTime(notice.createTime) }}</div>
              </div>
              <div class="notice-actions">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="markFollowNoticeAsRead(notice)"
                  :disabled="notice.isRead === 1"
                >{{ notice.isRead === 1 ? '已读' : '已读' }}</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 互动通知弹窗（点赞与收藏） -->
    <el-dialog
      v-model="showInteractionNoticeModal"
      title="点赞与收藏"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="friend-request-container">
        <div v-if="interactionNoticeLoading" class="loading">
          <el-skeleton :rows="5" animated />
        </div>
        <div v-else-if="interactionNotices.length === 0" class="no-notices">
          <div class="notice-empty-icon">💖</div>
          <p class="notice-empty-title">暂无互动消息</p>
          <p class="notice-empty-desc">点赞和收藏通知会显示在这里</p>
        </div>
        <div v-else>
          <div class="notice-header">
            <span>共 {{ interactionNotices.length }} 条通知</span>
            <div class="notice-actions">
              <el-button 
                type="danger" 
                size="small" 
                @click="deleteAllReadInteractionNotices"
              >一键删除</el-button>
              <el-button type="primary" size="small" @click="markAllInteractionNoticesAsRead">一键已读</el-button>
            </div>
          </div>
          <div class="notice-items">
            <div 
              v-for="notice in interactionNotices" 
              :key="notice.id"
              class="notice-item"
              :class="{ 'read': notice.isRead === 1 }"
            >
              <div class="notice-avatar">
                <SafeImage :src="notice.fromUserAvatar" fallback="https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg" alt="用户头像" />
              </div>
              <div class="notice-content">
                <div class="notice-text">{{ notice.fromUserNickname }} {{ notice.type === 1 ? '点赞了你的帖子' : '收藏了你的帖子' }}《{{ notice.postTitle }}》</div>
                <div class="notice-time">{{ formatTime(notice.createTime) }}</div>
              </div>
              <div class="notice-actions">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="markInteractionNoticeAsRead(notice)"
                  :disabled="notice.isRead === 1"
                >{{ notice.isRead === 1 ? '已读' : '已读' }}</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 评论通知弹窗 -->
    <el-dialog
      v-model="showCommentNoticeModal"
      title="评论通知"
      width="600px"
      :close-on-click-modal="false"
    >
      <div class="friend-request-container">
        <div v-if="commentNoticeLoading" class="loading">
          <el-skeleton :rows="5" animated />
        </div>
        <div v-else-if="commentNotices.length === 0" class="no-notices">
          <div class="notice-empty-icon">💬</div>
          <p class="notice-empty-title">暂无评论消息</p>
          <p class="notice-empty-desc">新的评论会显示在这里</p>
        </div>
        <div v-else>
          <div class="notice-header">
            <span>共 {{ commentNotices.length }} 条通知</span>
            <div class="notice-actions">
              <el-button 
                type="danger" 
                size="small" 
                @click="deleteAllReadCommentNotices"
              >一键删除</el-button>
              <el-button type="primary" size="small" @click="markAllCommentNoticesAsRead">一键已读</el-button>
            </div>
          </div>
          <div class="notice-items">
            <div 
              v-for="notice in commentNotices" 
              :key="notice.id"
              class="notice-item"
              :class="{ 'read': notice.isRead === 1 }"
            >
              <div class="notice-avatar">
                <SafeImage :src="notice.fromUserAvatar" fallback="https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg" alt="用户头像" />
              </div>
              <div class="notice-content">
                <div class="notice-text">{{ notice.fromUserNickname }} 评论了你的帖子《{{ notice.postTitle }}》</div>
                <div class="notice-time">{{ formatTime(notice.createTime) }}</div>
              </div>
              <div class="notice-actions">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="markCommentNoticeAsRead(notice)"
                  :disabled="notice.isRead === 1"
                >{{ notice.isRead === 1 ? '已读' : '已读' }}</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 添加分类弹窗 -->
    <el-dialog
      title="添加分类"
      v-model="showAddModal"
      width="500px"
      :close-on-click-modal="true"
    >
      <div class="add-category-container">
        <div class="recommend-categories">
          <p class="recommend-title">选择分类</p>
          <div class="recommend-list">
            <span
              v-for="category in allCategories.filter(c => !categories.includes(c))"
              :key="category"
              class="recommend-item"
              @click="addFromRecommend(category)"
            >
              + {{ category }}
            </span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, Search, VideoCamera, Plus, Bell, More, Star, Collection, ChatLineRound, Close } from '@element-plus/icons-vue'
import { postApi, followApi, friendApi, interactionApi, communityApi, userCategoryApi } from '../api/community'
import { ElMessage, ElMessageBox } from 'element-plus'
import SafeImage from '../components/SafeImage.vue'
import { useImagePreload } from '../composables/useImagePreload'

const router = useRouter()
const route = useRoute()

// 数据
const searchKeyword = ref('')
const activeCategory = ref('推荐')

// 用户自定义分类标签（可编辑）
const categories = ref([])

// 所有可选的分类（用于添加标签）
const allCategories = ref([])

// 加载用户分类
const loadUserCategories = async () => {
  try {
    const res = await userCategoryApi.getUserCategories()
    if (res.code === 200) {
      categories.value = res.data
    }
  } catch (error) {
    console.error('加载用户分类失败:', error)
    categories.value = [
      '推荐', '穿搭', '美食', '日常', '旅行', '美妆', '健身', '读书',
      '直播', '短剧', '头像', '音乐', '游戏', '舞蹈', '绘画', '摄影',
      '情感', '搞笑', '动漫', '壁纸', '手工', '影视', '学习', '健身塑型',
      '家装', '科技数码'
    ]
  }
}

// 加载所有可选分类
const loadAllCategories = async () => {
  try {
    const res = await userCategoryApi.getAllCategories()
    if (res.code === 200) {
      allCategories.value = res.data
    }
  } catch (error) {
    console.error('加载所有分类失败:', error)
    allCategories.value = [
      '推荐', '穿搭', '美食', '日常', '旅行', '美妆', '健身', '读书',
      '直播', '短剧', '头像', '音乐', '游戏', '舞蹈', '绘画', '摄影',
      '情感', '搞笑', '动漫', '壁纸', '手工', '影视', '学习', '健身塑型',
      '家装', '科技数码', '汽车', '男士', '明星', '科学', '职场', '减脂',
      '艺术', '家居', '婚礼', '母婴', '潮鞋', '护肤', '萌宠', '文化',
      '竞技', '机车', '户外', '心理', '体育', '文具', '综艺', '社科',
      '潮玩', '校园', '露营', '人文'
    ]
  }
}

// 编辑模式状态
const isEditing = ref(false)
const showAddModal = ref(false)
const newCategoryName = ref('')
const postList = ref([])
const currentPage = ref(1)
const hasMore = ref(true)
const loading = ref(false)

// 推荐用户
const recommendUsers = ref([])

// 关注与取关通知
const showFollowNoticeModal = ref(false)
const followNotices = ref([])
const followNoticeLoading = ref(false)
const recommendLoading = ref(false)
const recommendPage = ref(1)
const hasMoreRecommend = ref(true)

// 添加好友相关
const showAddFriendModal = ref(false)
const friendSearchKeyword = ref('')
const searchResults = ref([])
const searchLoading = ref(false)

// 热门话题
const hotTopics = ref([])

// 通知相关
const showNoticeMenu = ref(false)
const showFriendRequestModal = ref(false)
const showInteractionNoticeModal = ref(false)
const friendRequests = ref([])
const noticeLoading = ref(false)
const unreadCount = ref(0)
const friendRequestCount = ref(0)
const followNoticeCount = ref(0)
const interactionNoticeCount = ref(0)
const interactionNotices = ref([])
const interactionNoticeLoading = ref(false)

// 评论通知相关
const showCommentNoticeModal = ref(false)
const commentNotices = ref([])
const commentNoticeLoading = ref(false)
const commentNoticeCount = ref(0)

// 侧边栏更多菜单相关
const showSidebarMoreMenu = ref(false)

// 聊天相关
const showChatWindow = ref(false)
const friendList = ref([])
const friendListLoading = ref(false)
const chatSearchKeyword = ref('')
const chatSearchModalKeyword = ref('')
const selectedFriendId = ref(null)
const selectedFriend = ref(null)
const chatMessages = ref([])
const chatLoading = ref(false)
const isChatCleared = ref(false)
const friendNickname = ref('')
const showNicknameModal = ref(false)
const nicknameInput = ref('')

// 聊天窗口更多菜单相关
const showChatMoreMenu = ref(false)
const isChatTop = ref(false)
const chatMoreMenu = ref(null)
const chatMoreBtn = ref(null)
const chatWindowRef = ref(null)
const sidebarFriendBtn = ref(null)

// 搜索聊天记录相关
const showSearchModal = ref(false)
const chatSearchResults = ref([])
const chatSearchLoading = ref(false)

// 投诉相关
const showComplaintModal = ref(false)
const complaintReason = ref('')

// 图片预加载相关
const { preloadImages, isImageLoaded } = useImagePreload()
const imagePreloading = ref(false)
const chatInput = ref('')
const chatContent = ref(null)

// WebSocket
let ws = null

const emojiBtn = ref(null)
const emojiPicker = ref(null)

// 表情相关
const showEmojiPicker = ref(false)
const activeEmojiCategory = ref('smileys')

const emojiCategories = [
  { name: 'smileys', icon: '😊' },
  { name: 'people', icon: '👫' },
  { name: 'animals', icon: '🐶' },
  { name: 'food', icon: '🍎' },
  { name: 'travel', icon: '✈️' },
  { name: 'activities', icon: '⚽' },
  { name: 'objects', icon: '💡' },
  { name: 'symbols', icon: '❤️' },
  { name: 'flags', icon: '🇨🇳' }
]

const emojis = {
  smileys: ['😀', '😃', '😄', '😁', '😆', '😅', '🤣', '😂', '🙂', '😊', '😇', '🥰', '😍', '🤩', '😘', '😗', '😚', '😙', '🥲', '😋', '😛', '😜', '🤪', '😝', '🤑', '🤗', '🤭', '🤫', '🤔', '🤐', '🤨', '😐', '😑', '😶', '😏', '😒', '🙄', '😬', '🤥', '😌', '😔', '😪', '🤤', '😴', '😷', '🤒', '🤕', '🤢', '🤮', '🥵', '🥶', '🥴', '😵', '🤯', '🤠', '🥳', '🥸', '😎', '🤓', '🧐', '😕', '😟', '🙁', '☹️', '😮‍💨', '😌', '😤', '😡', '😠', '🤬', '😈', '👿', '💀', '☠️', '💩', '🤡', '👹', '👺', '👻', '👽', '👾', '🤖', '🎃', '😺', '😸', '😹', '😻', '😼', '😽', '🙀', '😿', '😾'],
  people: ['👋', '🤚', '🖐️', '✋', '🖖', '👌', '🤏', '✌️', '🤞', '🤟', '🤘', '🤙', '👈', '👉', '👆', '👇', '☝️', '✊', '🤛', '🤜', '🤲', '🤝', '🙏', '💪', '🦾', '🦵', '🦿', '🦶', '👂', '🦻', '👃', '🗣️', '👤', '👥', '👫', '👬', '👭', '🧑‍🤝‍🧑', '👪', '👨‍👩‍👧', '👨‍👩‍👧‍👦', '👨‍👩‍👦‍👦', '👨‍👩‍👧‍👧', '👩‍👩‍👧', '👩‍👩‍👧‍👦', '👩‍👩‍👦‍👦', '👩‍👩‍👧‍👧', '👨‍👨‍👧', '👨‍👨‍👧‍👦', '👨‍👨‍👦‍👦', '👨‍👨‍👧‍👧', '👨‍👩‍👧', '👨‍👨‍👧', '👩‍👩‍👧', '👨‍👩‍👦', '👨‍👨‍👦', '👩‍👩‍👦', '👨‍👩‍👧‍👦', '👨‍👨‍👧‍👦', '👩‍👩‍👧‍👦', '🧑', '👱', '👨', '👩', '👴', '👵', '🧓', '👲', '👳', '🧔', '👨‍🦰', '👨‍🦱', '👨‍🦳', '👨‍🦲', '👩‍🦰', '👩‍🦱', '👩‍🦳', '👩‍🦲', '🧑‍🦰', '🧑‍🦱', '🧑‍🦳', '🧑‍🦲'],
  animals: ['🐵', '🐒', '🦍', '🦧', '🐶', '🐕', '🦮', '🐕‍🦺', '🐩', '🐺', '🦊', '🦝', '🐱', '🐈', '🐈‍⬛', '🦁', '🐯', '🐅', '🐆', '🐴', '🫎', '🦄', '🐴', '🦓', '🦌', '🦬', '🐮', '🐂', '🐃', '🐄', '🐷', '🐖', '🐗', '🐽', '🐏', '🐑', '🐐', '🦙', '🦒', '🐘', '🦣', '🦏', '🐪', '🐫', '🦘', '🦥', '🦦', '🦨', '🦔', '🐭', '🐁', '🐀', '🐹', '🐰', '🐇', '🐿️', '🦫', '🦔', '🦇', '🐓', '🐔', '🐣', '🐤', '🐥', '🐦', '🐧', '🕊️', '🦅', '🦆', '🦢', '🦉', '🦤', '🦩', '🦚', '🦜', '🐸', '🐊', '🐢', '🐍', '🦎', '🦖', '🦕', '🐙', '🦑', '🦐', '🦞', '🦀', '🐡', '🐠', '🐟', '🐬', '🦈', '🐳', '🐋', '🦭', '🐊', '🐢', '🐍', '🦎', '🦖', '🦕'],
  food: ['🍎', '🍐', '🍊', '🍋', '🍌', '🍉', '🍇', '🍓', '🍑', '🍒', '🥝', '🍅', '🥑', '🍆', '🥔', '🥕', '🌽', '🌶️', '🥒', '🥬', '🥦', '🧅', '🧄', '🍄', '🥜', '🌰', '🍞', '🥐', '🥖', '🫓', '🥨', '🧀', '🍕', '🍔', '🍟', '🌭', '🍿', '🧂', '🥚', '🍳', '🥞', '🧇', '🍩', '🍪', '🎂', '🍰', '🧁', '🍫', '🍬', '🍭', '🍮', '🍯', '🥛', '🍼', '☕', '🍵', '🧋', '🍶', '🍾', '🍷', '🍸', '🍹', '🍺', '🍻', '🥂', '🥃', '🍦', '🍧', '🍨', '🍩', '🍪', '🎂', '🍰', '🧁'],
  travel: ['🚗', '🚕', '🚙', '🚌', '🚎', '🏎️', '🚓', '🚑', '🚒', '🚐', '🚚', '🚛', '🚜', '🛴', '🚲', '🛵', '🏍️', '🚨', '🚔', '🚍', '🚘', '🚖', '🚡', '🚠', '🚟', '🚃', '🚋', '🚞', '🚝', '🚄', '🚅', '🚆', '🚇', '🚈', '🚉', '🚊', '🛫', '🛬', '✈️', '🛩️', '🚁', '🛸', '🚀', '🛶', '🚤', '🛳️', '🚢', '⛵', '🚂', '🚋', '🚞', '🚝', '🚄', '🚅', '🚆', '🚇', '🚈', '🚉', '🚊'],
  activities: ['⚽', '🏀', '🏈', '⚾', '🎾', '🏐', '🏓', '🏸', '🎱', '🏏', '🏑', '🏒', '🥍', '🏓', '🏸', '🎳', '🏹', '🎣', '🤿', '🏊', '🏄', '🛹', '🚴', '🚵', '🤸', '🏋️', '🤼', '🤽', '🤾', '🏆', '🥇', '🥈', '🥉', '🎽', '🎿', '⛷️', '🏂', '🥌', '🎯', '🎮', '🎲', '🃏', '🎴', '🎰', '🎪', '🤹', '🎭', '🩰', '💃', '🕺', '👯', '🎤', '🎧', '🎼', '🎹', '🥁', '🎷', '🎺', '🎻', '🪕', '🎸', '🎹', '🎧', '🎤'],
  objects: ['💎', '👑', '👒', '🎩', '🎓', '🧢', '⛑️', '🎒', '👝', '👛', '👜', '💼', '🗂️', '📁', '📂', '📃', '📄', '📑', '📊', '📈', '📉', '📋', '📌', '📍', '📎', '🖇️', '📐', '📏', '🧮', '📱', '📲', '📞', '📟', '📠', '💻', '🖥️', '🖨️', '⌨️', '🖱️', '🖲️', '🎮', '🕹️', '📡', '💾', '💿', '📀', '📺', '📷', '📸', '📹', '🎥', '🎞️', '📼', '🔊', '🔉', '🔈', '🎙️', '🎚️', '🎛️', '📻', '🎧', '💡', '🔦', '🏮', '🗿', '🎁', '🎈', '🎀', '🎊', '🎉', '🎪', '🎭', '🎨', '🖼️', '🗽', '🏰', '🏯', '🏠', '🏡', '🏢', '🏬', '🏭', '🏥', '🏦', '🏨', '🏪', '🏫', '🏩', '💒', '🕌', '🛕', '🕍', '🕋', '⛪', '🏛️', '🗼', '🗽', '🏯', '🏰'],
  symbols: ['❤️', '🧡', '💛', '💚', '💙', '💜', '🖤', '🤍', '🤎', '💔', '❣️', '💕', '💞', '💓', '💗', '💖', '💘', '💝', '💟', '🖤', '💯', '💢', '💥', '💫', '💦', '💨', '🕳️', '💣', '💬', '💭', '💤', '🙋', '🙋‍♂️', '🙋‍♀️', '🙌', '🙏', '🤚', '👋', '✋', '🖐️', '🖖', '🤙', '👌', '👍', '👎', '👏', '🙌', '💪', '🤝', '✊', '✌️', '🤞', '🤟', '🤘', '👈', '👉', '👆', '👇', '☝️', '✋', '🤚', '🖐️', '🖖', '👌', '🤏', '✌️', '🤞', '🤟', '🤘', '👈', '👉', '👆', '👇', '☝️', '🔰', '⭕', '✅', '❌', '⚠️', '🚸', '⛔', '🚫', '🚳', '🚭', '🚯', '🚱', '🚷', '📵', '🔞', '💯', '💢', '💥', '💫', '💦', '💨', '🕳️', '💣', '💬', '💭', '💤'],
  flags: ['🇦🇺', '🇦🇹', '🇧🇪', '🇧🇷', '🇨🇦', '🇨🇳', '🇩🇪', '🇪🇸', '🇫🇷', '🇬🇧', '🇬🇪', '🇭🇰', '🇮🇳', '🇮🇱', '🇯🇵', '🇰🇷', '🇲🇴', '🇲🇽', '🇳🇿', '🇵🇭', '🇵🇹', '🇷🇺', '🇸🇬', '🇹🇭', '🇬🇧', '🇬🇪', '🇭🇰', '🇮🇳', '🇮🇱', '🇯🇵', '🇰🇷', '🇲🇴', '🇲🇽', '🇳🇿', '🇵🇭', '🇵🇹', '🇷🇺', '🇸🇬', '🇹🇭', '🇬🇧', '🇬🇪', '🇭🇰', '🇮🇳', '🇮🇱', '🇯🇵', '🇰🇷', '🇲🇴', '🇲🇽', '🇳🇿', '🇵🇭', '🇵🇹', '🇷🇺', '🇸🇬', '🇹🇭', '🇬🇧', '🇬🇪', '🇭🇰', '🇮🇳', '🇮🇱', '🇯🇵', '🇰🇷', '🇲🇴', '🇲🇽', '🇳🇿', '🇵🇭', '🇵🇹', '🇷🇺', '🇸🇬', '🇹🇭']
}

const filteredEmojis = computed(() => {
  return emojis[activeEmojiCategory.value] || []
})

const toggleEmojiPicker = () => {
  showEmojiPicker.value = !showEmojiPicker.value
  if (showEmojiPicker.value) {
    setTimeout(() => {
      document.addEventListener('click', closeEmojiPicker)
    }, 0)
  } else {
    document.removeEventListener('click', closeEmojiPicker)
  }
}

const closeEmojiPicker = (e) => {
  const picker = emojiPicker.value
  const btn = emojiBtn.value
  if (picker && btn && !picker.contains(e.target) && !btn.contains(e.target)) {
    showEmojiPicker.value = false
    document.removeEventListener('click', closeEmojiPicker)
  }
}

const insertEmoji = (emoji) => {
  chatInput.value += emoji
}

const handleEmojiClickOutside = (e) => {
  const picker = emojiPicker.value
  const btn = emojiBtn.value
  if (picker && btn && !picker.contains(e.target) && !btn.contains(e.target)) {
    showEmojiPicker.value = false
    document.removeEventListener('click', handleEmojiClickOutside)
  }
}

// 获取当前登录用户信息
const currentUser = computed(() => {
  const userStr = localStorage.getItem('user')
  return userStr ? JSON.parse(userStr) : {}
})

// 获取当前登录用户ID
const currentUserId = computed(() => {
  return Number(currentUser.value.id) || 0
})

// 方法
const goToHome = () => {
  router.push('/')
}

const goToUserHome = (userId) => {
  // 检查userId是否是事件对象
  let targetUserId
  if (userId && typeof userId === 'object' && userId.target) {
    // 如果是事件对象，使用当前登录用户的ID
    targetUserId = currentUser.value.id || 1
  } else {
    // 否则使用传递的userId
    targetUserId = userId || (currentUser.value.id || 1)
  }
  router.push(`/community/user/${targetUserId}`)
}

const goToPublish = () => {
  router.push('/community/publish')
}

const goToPostDetail = (postId) => {
  console.log('Go to post detail:', postId)
  sessionStorage.setItem('fromPath', '/community')
  router.push(`/community/post/${postId}`)
}

const goToTopicRanking = (topicName) => {
  sessionStorage.setItem('fromTopicRanking', '/community')
  router.push(`/community/topic/${encodeURIComponent(topicName)}`)
}

const handleTopicClick = (topicName) => {
  console.log('Topic clicked:', topicName)
  sessionStorage.setItem('fromTopicRanking', '/community')
  router.push(`/community/topic/${encodeURIComponent(topicName)}`)
}

const getFirstImage = (images) => {
  if (!images) return ''
  if (Array.isArray(images)) {
    return images[0] || ''
  }
  try {
    const parsed = JSON.parse(images)
    return Array.isArray(parsed) ? (parsed[0] || '') : ''
  } catch {
    return ''
  }
}

const preloadFirstScreenImages = async (posts) => {
  if (imagePreloading.value) return
  
  const firstScreenCount = Math.min(posts.length, 6)
  const imageUrls = posts.slice(0, firstScreenCount)
    .map(post => getFirstImage(post.images))
    .filter(url => url)

  if (imageUrls.length === 0) return

  imagePreloading.value = true
  try {
    await preloadImages(imageUrls, {
      concurrency: 3,
      priority: 'parallel',
      onProgress: (progress) => {
        console.log(`Preloaded ${progress.loaded}/${progress.total} images`)
      }
    })
    console.log('首屏图片预加载完成')
  } catch (error) {
    console.warn('图片预加载失败:', error)
  } finally {
    imagePreloading.value = false
  }
}

const handleSearch = () => {
  // 搜索逻辑
  currentPage.value = 1
  loadPosts()
}

const switchCategory = (category) => {
  activeCategory.value = category
  currentPage.value = 1
  postList.value = []
  loadPosts()
}

// 分类标签栏滚轮滚动
const categoryTabsRef = ref(null)
let scrollAnimationId = null
let accumulatedDelta = 0

const handleWheelScroll = (e) => {
  if (!categoryTabsRef.value) return
  
  accumulatedDelta += e.deltaY * 1.5
  
  if (!scrollAnimationId) {
    scrollAnimation()
  }
}

const scrollAnimation = () => {
  if (!categoryTabsRef.value) return
  
  if (Math.abs(accumulatedDelta) < 1) {
    accumulatedDelta = 0
    scrollAnimationId = null
    return
  }
  
  const scrollStep = accumulatedDelta * 0.15
  accumulatedDelta -= scrollStep
  
  categoryTabsRef.value.scrollLeft += scrollStep
  
  scrollAnimationId = requestAnimationFrame(scrollAnimation)
}

// 切换编辑模式
const toggleEditMode = () => {
  isEditing.value = !isEditing.value
}

const deletingCategories = ref(new Set())
const addingCategories = ref(new Set())

// 删除分类（推荐不可删除）
const removeCategory = async (category) => {
  if (category === '推荐') return
  
  if (deletingCategories.value.has(category)) {
    return
  }
  
  if (!categories.value.includes(category)) {
    ElMessage.warning('该分类不存在')
    return
  }
  
  deletingCategories.value.add(category)
  
  try {
    const res = await userCategoryApi.removeCategory(category)
    if (res.code === 200) {
      const index = categories.value.indexOf(category)
      if (index !== -1) {
        categories.value.splice(index, 1)
        
        if (activeCategory.value === category) {
          activeCategory.value = '推荐'
          currentPage.value = 1
          postList.value = []
          loadPosts()
        }
      }
      ElMessage.success('删除成功')
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除分类失败:', error)
    ElMessage.error('删除失败')
  } finally {
    deletingCategories.value.delete(category)
  }
}

// 打开添加分类弹窗
const openAddModal = () => {
  console.log('openAddModal called, showAddModal before:', showAddModal.value)
  showAddModal.value = true
  console.log('openAddModal called, showAddModal after:', showAddModal.value)
}

// 添加分类
const addCategory = async () => {
  const name = newCategoryName.value.trim()
  if (!name) {
    ElMessage.warning('请输入分类名称')
    return
  }
  
  if (categories.value.includes(name)) {
    ElMessage.warning('该分类已存在')
    return
  }
  
  try {
    const res = await userCategoryApi.addCategory(name)
    if (res.code === 200) {
      categories.value.push(name)
      newCategoryName.value = ''
      showAddModal.value = false
      ElMessage.success('添加成功')
    } else {
      ElMessage.error(res.message || '添加失败')
    }
  } catch (error) {
    console.error('添加分类失败:', error)
    ElMessage.error('添加失败')
  }
}

// 从推荐分类中添加
const addFromRecommend = async (category) => {
  if (category === '推荐') return
  
  if (addingCategories.value.has(category)) {
    return
  }
  
  if (categories.value.includes(category)) {
    ElMessage.warning('该分类已存在')
    return
  }
  
  addingCategories.value.add(category)
  
  try {
    const res = await userCategoryApi.addCategory(category)
    if (res.code === 200) {
      categories.value.push(category)
      ElMessage.success('添加成功')
    } else {
      ElMessage.error(res.message || '添加失败')
    }
  } catch (error) {
    console.error('添加分类失败:', error)
    ElMessage.error('添加失败')
  } finally {
    addingCategories.value.delete(category)
  }
}

const loadPosts = async () => {
  if (loading.value) return
  
  loading.value = true
  try {
    const response = await postApi.getPostList({
      pageNum: currentPage.value,
      pageSize: 10,
      keyword: searchKeyword.value,
      category: activeCategory.value
    })
    
    if (response.code === 200) {
      // 检查返回数据的结构
      const posts = response.data.list || response.data || []
      console.log('Posts data:', posts)
      
      if (currentPage.value === 1) {
        postList.value = posts
      } else {
        postList.value = [...postList.value, ...posts]
      }
      
      // 检查是否还有更多数据
      hasMore.value = posts.length === 10

      await nextTick()
      await preloadFirstScreenImages(posts)
    } else {
      ElMessage.error(response.msg || '获取帖子失败')
    }
  } catch (error) {
    console.error('加载帖子失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (!loading.value && hasMore.value) {
    currentPage.value++
    loadPosts()
  }
}

// 加载推荐用户
const loadRecommendUsers = async () => {
  if (recommendLoading.value) return
  
  recommendLoading.value = true
  try {
    const response = await followApi.getRecommendUsers({
      pageNum: recommendPage.value,
      pageSize: 3
    })
    
    if (response.code === 200) {
      const newUsers = response.data.list || []
      if (recommendPage.value === 1) {
        recommendUsers.value = newUsers
      } else {
        recommendUsers.value = [...recommendUsers.value, ...newUsers]
      }

      const total = response.data.total || 0
      hasMoreRecommend.value = total > recommendUsers.value.length
      recommendPage.value++
    } else {
      ElMessage.error(response.msg || '加载推荐用户失败')
    }
  } catch (error) {
    console.error('加载推荐用户失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    recommendLoading.value = false
  }
}

// 关注用户
const toggleFollow = async (userId) => {
  try {
    const response = await followApi.followUser({ followingId: userId })
    
    if (response.code === 200) {
      // 更新推荐用户列表中的关注状态
      const user = recommendUsers.value.find(u => u.id === userId)
      if (user) {
        user.isFollowing = response.data
        if (response.data) {
          user.followerCount++
        } else {
          user.followerCount--
        }
      }
      
      ElMessage.success(response.data ? '关注成功' : '取消关注成功')
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    console.error('关注操作失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 打开添加好友弹窗
const openAddFriendModal = () => {
  showSidebarMoreMenu.value = false
  showAddFriendModal.value = true
  // 清空搜索关键词和结果
  friendSearchKeyword.value = ''
  searchResults.value = []
}

// 打开热门视频
const openHotVideos = () => {
  showSidebarMoreMenu.value = false
  window.open('https://www.douyin.com', '_blank')
}

// 搜索用户
const searchUsers = async () => {
  if (!friendSearchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  
  searchLoading.value = true
  try {
    const response = await friendApi.searchUsers(friendSearchKeyword.value)
    if (response.code === 200) {
      searchResults.value = response.data || []
    } else {
      ElMessage.error(response.msg || '搜索失败')
    }
  } catch (error) {
    console.error('搜索用户失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    searchLoading.value = false
  }
}

// 发送好友申请
const sendFriendRequest = async (friendId) => {
  try {
    const response = await friendApi.sendFriendRequest(friendId)
    if (response.code === 200) {
      ElMessage.success('好友申请已发送')
      // 标记为待确认状态
      const user = searchResults.value.find(u => u.id === friendId)
      if (user) user.friendStatus = 'pending'
    } else {
      ElMessage.error(response.msg || '发送好友申请失败')
    }
  } catch (error) {
    console.error('发送好友申请失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 切换通知菜单
const toggleNoticeMenu = () => {
  showNoticeMenu.value = !showNoticeMenu.value
  // 点击空白处关闭菜单
  if (showNoticeMenu.value) {
    loadFriendRequests()
    setTimeout(() => {
      document.addEventListener('click', closeNoticeMenu)
    }, 0)
  }
}

// 打开好友申请弹窗
const openFriendRequestModal = () => {
  showNoticeMenu.value = false
  showFriendRequestModal.value = true
  loadFriendRequests()
}

// 打开关注与取关通知弹窗
const openFollowNoticeModal = () => {
  showNoticeMenu.value = false
  showFollowNoticeModal.value = true
  loadFollowNotices()
}

// 打开互动通知弹窗
const openInteractionNoticeModal = () => {
  showNoticeMenu.value = false
  showInteractionNoticeModal.value = true
  loadInteractionNotices()
}

// 打开评论通知弹窗
const openCommentNoticeModal = () => {
  showNoticeMenu.value = false
  showCommentNoticeModal.value = true
  loadCommentNotices()
}

// 加载关注与取关通知
const loadFollowNotices = async () => {
  followNoticeLoading.value = true
  try {
    const response = await followApi.getFollowNotices()
    if (response.code === 200) {
      followNotices.value = response.data || []
      // 只统计未读通知的数量
      followNoticeCount.value = followNotices.value.filter(n => n.isRead === 0).length
      unreadCount.value = friendRequestCount.value + followNoticeCount.value + interactionNoticeCount.value
    } else {
      ElMessage.error(response.msg || '获取关注与取关通知失败')
    }
  } catch (error) {
    console.error('获取关注与取关通知失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    followNoticeLoading.value = false
  }
}

// 处理关注与取关通知
const handleFollowNotice = (notice) => {
  ElMessage.success('已查看通知')
}

// 标记单条关注通知为已读
const markFollowNoticeAsRead = async (notice) => {
  if (notice.isRead === 1) return
  
  try {
    const response = await followApi.markFollowNoticeAsRead(notice.id)
    if (response.code === 200) {
      notice.isRead = 1
      if (followNoticeCount.value > 0) {
        followNoticeCount.value--
        unreadCount.value = friendRequestCount.value + followNoticeCount.value + interactionNoticeCount.value + commentNoticeCount.value
      }
      ElMessage.success('已标记为已读')
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 标记所有关注通知为已读
const markAllFollowNoticesAsRead = async () => {
  try {
    const response = await followApi.markAllFollowNoticesAsRead()
    if (response.code === 200) {
      followNotices.value.forEach(notice => {
        notice.isRead = 1
      })
      unreadCount.value = friendRequestCount.value + interactionNoticeCount.value + commentNoticeCount.value
      followNoticeCount.value = 0
      ElMessage.success('已全部标记为已读')
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    console.error('标记全部已读失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

const deleteAllReadFollowNotices = async () => {
  try {
    const response = await followApi.getUnreadFollowNoticeCount()
    if (response.code === 200) {
      const unreadCount = response.data
      if (unreadCount > 0) {
        ElMessage.warning('还有信息未读')
        return
      }
      
      await ElMessageBox.confirm(
        '删除后将无法找回数据，确定删除？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      const deleteResponse = await followApi.deleteAllReadFollowNotices()
      if (deleteResponse.code === 200) {
        await loadFollowNotices()
        ElMessage.success('删除成功')
      } else {
        ElMessage.error(deleteResponse.msg || '删除失败')
      }
    } else {
      ElMessage.error(response.msg || '获取未读数量失败')
    }
  } catch (error) {
    if (!(error instanceof Error && error.message === 'cancel')) {
      console.error('删除已读通知失败:', error)
      ElMessage.error('网络错误，请稍后重试')
    }
  }
}

// 加载互动通知
const loadInteractionNotices = async () => {
  interactionNoticeLoading.value = true
  try {
    const response = await interactionApi.getInteractionNotices()
    if (response.code === 200) {
      interactionNotices.value = response.data || []
      // 只统计未读通知的数量
      interactionNoticeCount.value = interactionNotices.value.filter(n => n.isRead === 0).length
      unreadCount.value = friendRequestCount.value + followNoticeCount.value + interactionNoticeCount.value + commentNoticeCount.value
    } else {
      ElMessage.error(response.msg || '获取互动通知失败')
    }
  } catch (error) {
    console.error('获取互动通知失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    interactionNoticeLoading.value = false
  }
}

// 标记单条互动通知为已读
const markInteractionNoticeAsRead = async (notice) => {
  if (notice.isRead === 1) return
  
  try {
    const response = await interactionApi.markInteractionNoticeAsRead(notice.id)
    if (response.code === 200) {
      notice.isRead = 1
      if (interactionNoticeCount.value > 0) {
        interactionNoticeCount.value--
        unreadCount.value = friendRequestCount.value + followNoticeCount.value + interactionNoticeCount.value + commentNoticeCount.value
      }
      ElMessage.success('已标记为已读')
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 标记所有互动通知为已读
const markAllInteractionNoticesAsRead = async () => {
  try {
    const response = await interactionApi.markAllInteractionNoticesAsRead()
    if (response.code === 200) {
      interactionNotices.value.forEach(notice => {
        notice.isRead = 1
      })
      unreadCount.value = friendRequestCount.value + followNoticeCount.value + commentNoticeCount.value
      interactionNoticeCount.value = 0
      ElMessage.success('已全部标记为已读')
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    console.error('标记全部已读失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 删除所有已读互动通知
const deleteAllReadInteractionNotices = async () => {
  try {
    const response = await interactionApi.getUnreadInteractionNoticeCount()
    if (response.code === 200) {
      const unreadCount = response.data
      if (unreadCount > 0) {
        ElMessage.warning('还有信息未读')
        return
      }
      
      await ElMessageBox.confirm(
        '删除后将无法找回数据，确定删除？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      const deleteResponse = await interactionApi.deleteAllReadInteractionNotices()
      if (deleteResponse.code === 200) {
        await loadInteractionNotices()
        ElMessage.success('删除成功')
      } else {
        ElMessage.error(deleteResponse.msg || '删除失败')
      }
    } else {
      ElMessage.error(response.msg || '获取未读数量失败')
    }
  } catch (error) {
    if (!(error instanceof Error && error.message === 'cancel')) {
      console.error('删除已读通知失败:', error)
      ElMessage.error('网络错误，请稍后重试')
    }
  }
}

// 加载评论通知
const loadCommentNotices = async () => {
  commentNoticeLoading.value = true
  try {
    const response = await interactionApi.getCommentNotices()
    if (response.code === 200) {
      commentNotices.value = response.data || []
      commentNoticeCount.value = commentNotices.value.filter(n => n.isRead === 0).length
      unreadCount.value = friendRequestCount.value + followNoticeCount.value + interactionNoticeCount.value + commentNoticeCount.value
    } else {
      ElMessage.error(response.msg || '获取评论通知失败')
    }
  } catch (error) {
    console.error('获取评论通知失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    commentNoticeLoading.value = false
  }
}

// 标记单条评论通知为已读
const markCommentNoticeAsRead = async (notice) => {
  if (notice.isRead === 1) return
  
  try {
    const response = await interactionApi.markCommentNoticeAsRead(notice.id)
    if (response.code === 200) {
      notice.isRead = 1
      if (commentNoticeCount.value > 0) {
        commentNoticeCount.value--
        unreadCount.value = friendRequestCount.value + followNoticeCount.value + interactionNoticeCount.value + commentNoticeCount.value
      }
      ElMessage.success('已标记为已读')
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 标记所有评论通知为已读
const markAllCommentNoticesAsRead = async () => {
  try {
    const response = await interactionApi.markAllCommentNoticesAsRead()
    if (response.code === 200) {
      commentNotices.value.forEach(notice => {
        notice.isRead = 1
      })
      unreadCount.value = friendRequestCount.value + followNoticeCount.value + interactionNoticeCount.value
      commentNoticeCount.value = 0
      ElMessage.success('已全部标记为已读')
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    console.error('标记全部已读失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 删除所有已读评论通知
const deleteAllReadCommentNotices = async () => {
  try {
    const response = await interactionApi.getUnreadCommentNoticeCount()
    if (response.code === 200) {
      const unreadCount = response.data
      if (unreadCount > 0) {
        ElMessage.warning('还有信息未读')
        return
      }
      
      await ElMessageBox.confirm(
        '删除后将无法找回数据，确定删除？',
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      const deleteResponse = await interactionApi.deleteAllReadCommentNotices()
      if (deleteResponse.code === 200) {
        await loadCommentNotices()
        ElMessage.success('删除成功')
      } else {
        ElMessage.error(deleteResponse.msg || '删除失败')
      }
    } else {
      ElMessage.error(response.msg || '获取未读数量失败')
    }
  } catch (error) {
    if (!(error instanceof Error && error.message === 'cancel')) {
      console.error('删除已读通知失败:', error)
      ElMessage.error('网络错误，请稍后重试')
    }
  }
}

// 关闭通知菜单
const closeNoticeMenu = (e) => {
  const noticeItem = document.querySelector('.notice-item')
  if (!noticeItem || !noticeItem.contains(e.target)) {
    showNoticeMenu.value = false
    document.removeEventListener('click', closeNoticeMenu)
  }
}

// 切换聊天窗口
const toggleChatWindow = () => {
  showChatWindow.value = !showChatWindow.value
  if (showChatWindow.value) {
    loadFriendList()
    setTimeout(() => {
      document.addEventListener('click', closeChatWindowOnClickOutside)
    }, 0)
  } else {
    document.removeEventListener('click', closeChatWindowOnClickOutside)
  }
}

// 点击空白处关闭聊天窗口
const closeChatWindowOnClickOutside = (e) => {
  const win = chatWindowRef.value
  const btn = sidebarFriendBtn.value
  if (win && !win.contains(e.target) && btn && !btn.contains(e.target)) {
    closeChatWindow()
    document.removeEventListener('click', closeChatWindowOnClickOutside)
  }
}

// 关闭聊天窗口
const closeChatWindow = () => {
  showChatWindow.value = false
  selectedFriendId.value = null
  selectedFriend.value = null
  chatMessages.value = []
  chatInput.value = ''
  document.removeEventListener('click', closeChatWindowOnClickOutside)
}

// ==================== WebSocket ====================
const connectWebSocket = () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  const userId = user.id
  if (!userId) return

  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  const wsUrl = `${protocol}//${window.location.host}/ws/chat?userId=${userId}`

  ws = new WebSocket(wsUrl)

  ws.onopen = () => {
    console.log('[Community] WebSocket 已连接')
  }

  ws.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data)
      if (data.type === 'message') {
        const fromId = data.fromId
        const content = data.content
        const timeStr = data.time || ''

        // 如果正在和该好友聊天，直接追加到消息列表
        if (selectedFriendId.value === fromId) {
          chatMessages.value.push({
            fromId: fromId,
            toId: currentUserId.value,
            content: content,
            createTime: timeStr,
            status: 'sent'
          })
          // 标记已读
          friendApi.markRead(fromId).catch(() => {})
          nextTick(() => scrollToBottom())
        }

        // 更新好友列表中的最后消息
        const friend = friendList.value.find(f => f.id === fromId)
        if (friend) {
          friend.lastMessage = content
          friend.lastMessageTime = timeStr
          if (selectedFriendId.value !== fromId) {
            friend.unreadCount = (friend.unreadCount || 0) + 1
          }
        }
      }
    } catch (e) {
      console.warn('[Community] 消息解析失败:', e)
    }
  }

  ws.onclose = () => {
    console.log('[Community] WebSocket 已断开')
  }

  ws.onerror = (e) => {
    console.warn('[Community] WebSocket 错误:', e)
  }
}

// 加载好友列表
const loadFriendList = async () => {
  friendListLoading.value = true
  try {
    const response = await friendApi.getFriendListForChat()
    if (response.code === 200) {
      friendList.value = response.data || []
    } else {
      ElMessage.error(response.msg || '获取好友列表失败')
    }
  } catch (error) {
    console.error('获取好友列表失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    friendListLoading.value = false
  }
}

// 选择好友
const selectFriend = async (friend) => {
  selectedFriendId.value = friend.id
  selectedFriend.value = friend
  await getFriendNickname(friend.id)
  await loadChatHistory(friend.id)
}

// 加载聊天记录
const loadChatHistory = async (friendId) => {
  chatLoading.value = true
  try {
    const response = await friendApi.getChatHistory(friendId)
    if (response.code === 200) {
      chatMessages.value = response.data || []
      
      await friendApi.markRead(friendId)
      
      const friendIndex = friendList.value.findIndex(item => item.id === friendId)
      if (friendIndex !== -1) {
        friendList.value[friendIndex].unreadCount = 0
      }
      
      await nextTick()
      setTimeout(() => {
        scrollToBottom()
      }, 50)
    } else {
      ElMessage.error(response.msg || '获取聊天记录失败')
    }
  } catch (error) {
    console.error('获取聊天记录失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    chatLoading.value = false
  }
}

// 发送消息（通过 WebSocket）
const sendMessage = async () => {
  if (!chatInput.value.trim() || !selectedFriendId.value) return

  if (!ws || ws.readyState !== WebSocket.OPEN) {
    ElMessage.warning('连接已断开，请刷新页面')
    return
  }

  const message = chatInput.value.trim()
  chatInput.value = ''

  const tempId = Date.now() + Math.random().toString(36).substr(2, 9)

  const tempMessage = {
    id: tempId,
    fromId: currentUserId.value,
    toId: selectedFriendId.value,
    content: message,
    createTime: new Date(),
    status: 'sending'
  }

  chatMessages.value.push(tempMessage)

  await nextTick()
  scrollToBottom()

  try {
    ws.send(JSON.stringify({ toId: selectedFriendId.value, content: message }))
    // WebSocket 发送成功，更新状态
    const index = chatMessages.value.findIndex(msg => msg.id === tempId)
    if (index !== -1) {
      chatMessages.value[index].status = 'sent'
    }
    // 更新好友列表中的最后消息
    const friendIndex = friendList.value.findIndex(item => item.id === selectedFriendId.value)
    if (friendIndex !== -1) {
      friendList.value[friendIndex].lastMessage = message
      friendList.value[friendIndex].lastMessageTime = new Date()
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    const index = chatMessages.value.findIndex(msg => msg.id === tempId)
    if (index !== -1) {
      chatMessages.value[index].status = 'failed'
    }
    ElMessage.error('发送失败，请稍后重试')
  }
}

// 重发消息
const resendMessage = async (message) => {
  if (!ws || ws.readyState !== WebSocket.OPEN) {
    ElMessage.warning('连接已断开，请刷新页面')
    return
  }
  message.status = 'sending'

  try {
    ws.send(JSON.stringify({ toId: message.toId, content: message.content }))
    message.status = 'sent'
    // 更新好友列表
    const friendIndex = friendList.value.findIndex(item => item.id === selectedFriendId.value)
    if (friendIndex !== -1) {
      friendList.value[friendIndex].lastMessage = message.content
      friendList.value[friendIndex].lastMessageTime = new Date()
    }
  } catch (error) {
    console.error('重发消息失败:', error)
    message.status = 'failed'
    ElMessage.error('发送失败，请稍后重试')
  }
}

// 滚动到聊天窗口底部
const scrollToBottom = async () => {
  await nextTick()
  if (chatContent.value) {
    requestAnimationFrame(() => {
      chatContent.value.scrollTop = chatContent.value.scrollHeight
    })
  }
}

// 格式化时间
const formatTime = (timeString) => {
  if (!timeString) return '';
  
  const date = new Date(timeString);
  const now = new Date();
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const yesterday = new Date(today);
  yesterday.setDate(yesterday.getDate() - 1);
  const messageDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());
  
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  const year = date.getFullYear();
  
  if (messageDate.getTime() === today.getTime()) {
    // 今天
    return `${hours}:${minutes}`;
  } else if (messageDate.getTime() === yesterday.getTime()) {
    // 昨天
    return `昨天 ${hours}:${minutes}`;
  } else if (date.getFullYear() === now.getFullYear()) {
    // 今年
    return `${month}-${day}`;
  } else {
    // 其他年份
    return `${year}-${month}-${day}`;
  }
}

// 加载好友申请列表
const loadFriendRequests = async () => {
  noticeLoading.value = true
  try {
    const response = await friendApi.getFriendRequests()
    if (response.code === 200) {
      friendRequests.value = response.data || []
      friendRequestCount.value = friendRequests.value.length
      unreadCount.value = friendRequestCount.value + followNoticeCount.value + interactionNoticeCount.value + commentNoticeCount.value
    } else {
      ElMessage.error(response.msg || '获取好友申请失败')
    }
  } catch (error) {
    console.error('加载好友申请失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    noticeLoading.value = false
  }
}

// 同意好友申请
const agreeFriendRequest = async (friendId) => {
  try {
    const response = await friendApi.agreeFriendRequest(friendId)
    if (response.code === 200) {
      ElMessage.success('已同意好友申请')
      // 从好友申请列表中移除该请求
      friendRequests.value = friendRequests.value.filter(request => request.id !== friendId)
      friendRequestCount.value = friendRequests.value.length
      unreadCount.value = friendRequestCount.value + followNoticeCount.value + interactionNoticeCount.value + commentNoticeCount.value
    } else {
      ElMessage.error(response.msg || '同意好友申请失败')
    }
  } catch (error) {
    console.error('同意好友申请失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 拒绝好友申请
const refuseFriendRequest = async (friendId) => {
  try {
    const response = await friendApi.refuseFriendRequest(friendId)
    if (response.code === 200) {
      ElMessage.success('已拒绝好友申请')
      // 从好友申请列表中移除该请求
      friendRequests.value = friendRequests.value.filter(request => request.id !== friendId)
      friendRequestCount.value = friendRequests.value.length
      unreadCount.value = friendRequestCount.value + followNoticeCount.value + interactionNoticeCount.value + commentNoticeCount.value
    } else {
      ElMessage.error(response.msg || '拒绝好友申请失败')
    }
  } catch (error) {
    console.error('拒绝好友申请失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 加载热门话题
const loadHotTopics = async () => {
  try {
    const response = await communityApi.getHotTopics()
    if (response.code === 200) {
      hotTopics.value = response.data.slice(0, 5)
    }
  } catch (error) {
    console.error('获取热门话题失败:', error)
  }
}

// 生命周期
onMounted(async () => {
  await loadUserCategories()
  await loadAllCategories()
  loadPosts()
  loadRecommendUsers()
  loadFriendRequests()
  loadFollowNotices()
  loadInteractionNotices()
  loadCommentNotices()
  loadHotTopics()
  connectWebSocket()
})

onUnmounted(() => {
  if (ws) {
    ws.close()
    ws = null
  }
})

// 监听路由变化，当从帖子详情页返回时刷新帖子列表
watch(() => route.path, (newPath, oldPath) => {
  if (newPath === '/community' && oldPath.startsWith('/community/post/')) {
    loadPosts()
  }
})

// 切换聊天窗口更多菜单
const toggleChatMoreMenu = async (e) => {
  e.stopPropagation()
  showChatMoreMenu.value = !showChatMoreMenu.value
  if (showChatMoreMenu.value && selectedFriendId.value) {
    const response = await friendApi.getChatTopStatus(selectedFriendId.value)
    if (response.code === 200) {
      isChatTop.value = response.data
    }
  }
  if (showChatMoreMenu.value) {
    setTimeout(() => {
      document.addEventListener('click', closeChatMoreMenu)
    }, 0)
  }
}

// 关闭聊天窗口更多菜单
const closeChatMoreMenu = (e) => {
  const menu = chatMoreMenu.value
  const btn = chatMoreBtn.value
  if (menu && btn && !menu.contains(e.target) && !btn.contains(e.target)) {
    showChatMoreMenu.value = false
    document.removeEventListener('click', closeChatMoreMenu)
  }
}

// 查找聊天记录
const handleSearchChat = () => {
  showChatMoreMenu.value = false
  showSearchModal.value = true
}

// 搜索聊天记录
const searchChat = async () => {
  if (!chatSearchModalKeyword.value.trim() || !selectedFriendId.value) return

  // 设置加载状态
  chatSearchLoading.value = true

  try {
    const response = await friendApi.searchChatHistory(selectedFriendId.value, chatSearchModalKeyword.value, 1, 50)
    if (response.code === 200) {
      chatSearchResults.value = response.data || []
    } else {
      ElMessage.error(response.msg || '搜索失败')
      chatSearchResults.value = []
    }
  } catch (error) {
    console.error('搜索聊天记录失败:', error)
    ElMessage.error('网络错误，请稍后重试')
    chatSearchResults.value = []
  } finally {
    // 清除加载状态
    chatSearchLoading.value = false
  }
}

// 防抖函数
const debounce = (fn, delay = 300) => {
  let timer = null
  return (...args) => {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

// 实时搜索处理
const handleSearchInput = debounce(() => {
  if (chatSearchModalKeyword.value.trim()) {
    searchChat()
  } else {
    chatSearchResults.value = []
  }
}, 300)

// 重置搜索状态
const resetChatSearch = () => {
  chatSearchModalKeyword.value = ''
  chatSearchResults.value = []
}

// 切换置顶状态
const handleToggleTop = async () => {
  showChatMoreMenu.value = false
  try {
    const response = await friendApi.setChatTop(selectedFriendId.value, !isChatTop.value)
    if (response.code === 200) {
      isChatTop.value = !isChatTop.value
      ElMessage.success(isChatTop.value ? '已置顶聊天' : '已取消置顶')
      // 刷新好友列表以更新排序
      await loadFriendList()
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    console.error('设置置顶失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 清空聊天记录
const handleClearChat = () => {
  showChatMoreMenu.value = false
  ElMessageBox.confirm(
    '确定要清空与该好友的聊天记录吗？此操作不可恢复！',
    '确认清空',
    {
      confirmButtonText: '确定清空',
      cancelButtonText: '取消',
      type: 'danger'
    }
  ).then(async () => {
    try {
      const response = await friendApi.clearChatHistory(selectedFriendId.value)
      if (response.code === 200) {
        chatMessages.value = []
        // 更新好友列表中的最后消息为暂无消息
        const friendIndex = friendList.value.findIndex(item => item.id === selectedFriendId.value)
        if (friendIndex !== -1) {
          friendList.value[friendIndex].lastMessage = null
          friendList.value[friendIndex].lastMessageTime = null
          friendList.value[friendIndex].unreadCount = 0
        }
        ElMessage.success('聊天记录已清空')
      } else {
        ElMessage.error(response.msg || '清空失败')
      }
    } catch (error) {
      console.error('清空聊天记录失败:', error)
      ElMessage.error('网络错误，请稍后重试')
    }
  }).catch(() => {
    ElMessage.info('已取消清空')
  })
}

// 投诉
const handleComplaint = () => {
  showChatMoreMenu.value = false
  showComplaintModal.value = true
}

// 提交投诉
const submitComplaint = async () => {
  if (!complaintReason.value.trim()) {
    ElMessage.warning('请填写投诉原因')
    return
  }
  try {
    const response = await friendApi.submitComplaint(selectedFriendId.value, null, complaintReason.value)
    if (response.code === 200) {
      ElMessage.success('投诉已提交，我们会尽快处理')
      showComplaintModal.value = false
      complaintReason.value = ''
    } else {
      ElMessage.error(response.msg || '投诉提交失败')
    }
  } catch (error) {
    console.error('提交投诉失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 打开设置备注弹窗
const handleSetNickname = () => {
  showChatMoreMenu.value = false
  nicknameInput.value = friendNickname.value || ''
  showNicknameModal.value = true
}

// 保存备注
const saveNickname = async () => {
  if (!nicknameInput.value.trim()) {
    ElMessage.warning('请输入备注')
    return
  }
  try {
    console.log('saveNickname - friendId:', selectedFriendId.value, 'nickname:', nicknameInput.value.trim())
    const response = await friendApi.updateFriendNickname(selectedFriendId.value, nicknameInput.value.trim())
    console.log('saveNickname response:', response)
    if (response.code === 200 && response.data === true) {
      friendNickname.value = nicknameInput.value.trim()
      showNicknameModal.value = false
      await loadFriendList()
      ElMessage.success('备注设置成功')
    } else {
      ElMessage.error(response.msg || '设置失败，请确保好友关系已建立')
    }
  } catch (error) {
    console.error('设置备注失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 获取好友备注
const getFriendNickname = async (friendId) => {
  try {
    const response = await friendApi.getFriendNickname(friendId)
    if (response.code === 200) {
      friendNickname.value = response.data || ''
    }
  } catch (error) {
    console.error('获取备注失败:', error)
  }
}

// 切换侧边栏更多菜单
const toggleSidebarMoreMenu = () => {
  showSidebarMoreMenu.value = !showSidebarMoreMenu.value
  if (showSidebarMoreMenu.value) {
    setTimeout(() => {
      document.addEventListener('click', closeSidebarMoreMenu)
    }, 0)
  }
}

// 关闭侧边栏更多菜单
const closeSidebarMoreMenu = (e) => {
  const moreItem = document.querySelector('.more-item')
  if (!moreItem || !moreItem.contains(e.target)) {
    showSidebarMoreMenu.value = false
    document.removeEventListener('click', closeSidebarMoreMenu)
  }
}
</script>

<style>
body {
  margin: 0;
  padding: 0;
  overflow: hidden;
}

* {
  box-sizing: border-box;
}
</style>

<style scoped>
.community-page {
  height: 100vh;
  background: #FFFDFD;
  padding: 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.page-header {
  position: relative;
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  width: 100%;
  height: 60px;
}

.header-content {
  display: flex;
  align-items: center;
  width: 100%;
}

.back-btn-container {
  display: flex;
  align-items: center;
  z-index: 1;
}

.title-container {
  position: absolute;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.page-header h2 {
  color: #FF9999;
  font-size: 28px;
  margin: 0;
}

.community-container {
  display: flex;
  width: 100%;
  gap: 30px;
  flex: 1;
  overflow: hidden;
}

/* 左侧导航栏 */
.left-sidebar {
  width: 80px;
  display: flex;
  flex-direction: column;
  gap: 30px;
  align-items: center;
  flex-shrink: 0;
  position: relative;
}

.sidebar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 15px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #666;
}

.sidebar-item:hover {
  background: #FFE6E6;
  color: #FF9999;
}

.sidebar-item.active {
  background: #FFE6E6;
  color: #FF9999;
  font-weight: bold;
}

/* 中间内容区 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  height: calc(100vh - 100px);
  min-width: 0;
}

.search-bar {
  margin-bottom: 15px;
  flex-shrink: 0;
}

.search-bar .el-input {
  border-radius: 20px;
  border: 2px solid #F8E1E1;
}

.search-bar .el-input__wrapper {
  border-radius: 20px;
}

/* 分类标签栏 */
.category-tabs-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 15px;
  position: relative;
}

.category-tabs {
  flex: 1;
  display: flex;
  gap: 12px;
  overflow-x: auto;
  overflow-y: hidden;
  padding: 5px 0;
  flex-shrink: 0;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.category-tabs::-webkit-scrollbar {
  display: none;
}

.category-tabs:active {
  cursor: grabbing;
}



.category-tab {
  padding: 8px 16px;
  border-radius: 20px;
  background: #F8F8F8;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  position: relative;
  display: flex;
  align-items: center;
  gap: 5px;
}

.category-tab:hover {
  background: #FFE6E6;
  color: #FF9999;
}

.category-tab.active {
  background: #FF9999;
  color: white;
  font-weight: bold;
}

.category-delete {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: rgba(255, 153, 153, 0.8);
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.category-delete:hover {
  background: #FF6666;
}

.category-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.add-category-btn {
  padding: 6px 12px;
  border-radius: 15px;
  border: 1px solid #FF9999;
  background: white;
  color: #FF9999;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.add-category-btn:hover {
  background: #FFF0F0;
}

.edit-categories-btn {
  padding: 6px 12px;
  border-radius: 15px;
  border: 1px solid #FF9999;
  background: white;
  color: #FF9999;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.edit-categories-btn:hover {
  background: #FFF0F0;
}

/* 添加分类弹窗样式 */
.add-category-container {
  padding: 10px;
}

.custom-input-section {
  margin-bottom: 20px;
}

.new-category-input {
  width: 100%;
}

.recommend-categories {
  margin-top: 10px;
}

.recommend-title {
  font-size: 14px;
  color: #999;
  margin-bottom: 10px;
}

.recommend-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.recommend-item {
  padding: 6px 12px;
  border-radius: 15px;
  background: #F8F8F8;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.recommend-item:hover {
  background: #FFE6E6;
  color: #FF9999;
}

/* 瀑布流布局 */
.post-masonry {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 15px;
}

/* 自定义滚动条 */
.post-masonry::-webkit-scrollbar {
  width: 6px;
}

.post-masonry::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.post-masonry::-webkit-scrollbar-thumb {
  background: #FF9999;
  border-radius: 3px;
}

.post-masonry::-webkit-scrollbar-thumb:hover {
  background: #FF6666;
}

.post-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  height: 280px;
}

.post-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(255, 192, 203, 0.2);
}

.post-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.post-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.post-card:hover .post-image img {
  transform: scale(1.05);
}

.post-info {
  padding: 10px 15px;
}

.post-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.post-stats {
  display: flex;
  gap: 20px;
  color: #999;
  font-size: 14px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

/* 骨架屏样式 */
.skeleton-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  width: 100%;
}

.skeleton-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: 280px;
  display: flex;
  flex-direction: column;
}

.skeleton-image {
  width: 100%;
  height: 200px;
  background: #f5f5f5;
  position: relative;
  overflow: hidden;
}

.skeleton-image::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.6) 50%,
    transparent 100%
  );
  animation: shimmer 1.5s infinite;
}

.skeleton-content {
  flex: 1;
  padding: 12px 15px;
  display: flex;
  flex-direction: column;
}

.skeleton-title {
  height: 18px;
  width: 85%;
  background: #f0f0f0;
  border-radius: 3px;
  margin-bottom: 12px;
  position: relative;
  overflow: hidden;
}

.skeleton-title::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.8) 50%,
    transparent 100%
  );
  animation: shimmer 1.5s infinite;
}

.skeleton-stats {
  display: flex;
  gap: 22px;
  margin-top: auto;
}

.skeleton-stat {
  display: flex;
  align-items: center;
  gap: 5px;
}

.skeleton-stat-icon {
  width: 16px;
  height: 16px;
  background: #f0f0f0;
  border-radius: 3px;
  position: relative;
  overflow: hidden;
}

.skeleton-stat-icon::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.8) 50%,
    transparent 100%
  );
  animation: shimmer 1.5s infinite;
}

.skeleton-stat-text {
  height: 14px;
  width: 25px;
  background: #f0f0f0;
  border-radius: 3px;
  position: relative;
  overflow: hidden;
}

.skeleton-stat-text::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.8) 50%,
    transparent 100%
  );
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% {
    left: -100%;
  }
  100% {
    left: 100%;
  }
}

/* 加载更多 */
.load-more {
  text-align: center;
  margin-top: 30px;
}

.no-more {
  color: #999;
  font-size: 14px;
  padding: 20px 0;
}

/* 右侧信息栏 */
.right-sidebar {
  width: 280px;
  display: flex;
  flex-direction: column;
  gap: 30px;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: #F8F8F8;
  border-radius: 12px;
}

.avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-details h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #333;
}

.user-details p {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.recommend-section, .hot-topics {
  background: #F8F8F8;
  border-radius: 12px;
  padding: 20px;
}

.recommend-section h4, .hot-topics h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #333;
}

.recommend-list {
  max-height: 170px;
  overflow-y: auto;
}

.recommend-list::-webkit-scrollbar {
  width: 4px;
}

.recommend-list::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 2px;
}

.recommend-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  justify-content: space-between;
}

.recommend-item:last-child {
  margin-bottom: 0;
}

.recommend-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  flex-shrink: 0;
}

.recommend-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.recommend-item .user-details {
  flex: 1;
  min-width: 0;
}

.recommend-item .user-details h5 {
  margin: 0 0 3px 0;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recommend-item .user-details p {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.more-recommend {
  margin-top: 12px;
  text-align: center;
}

.more-recommend .el-button {
  font-size: 13px;
  color: #666;
}

.topic-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.topic-item:hover {
  background-color: #f5f5f5;
}

.topic-tag {
  font-size: 14px;
  color: #FF9999;
  font-weight: 500;
}

.topic-count {
  font-size: 12px;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .right-sidebar {
    display: none;
  }
}

@media (max-width: 768px) {
  .community-page {
    padding: 10px;
  }

  .left-sidebar {
    display: none;
  }

  .post-masonry {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .category-tabs {
    gap: 10px;
  }

  .community-container {
    gap: 15px;
  }
  
  .category-tab {
    padding: 6px 12px;
    font-size: 14px;
  }
}

/* 更多菜单样式 */
.more-item {
  position: relative;
}

.more-menu {
  position: absolute;
  top: 50%;
  left: calc(100% + 8px);
  transform: translateY(-50%);
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  padding: 6px;
  min-width: 130px;
  z-index: 100;
}

.more-menu .menu-item {
  padding: 10px 16px;
  text-align: center;
  white-space: nowrap;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s ease;
  color: #333;
  font-size: 14px;
}

/* 通知菜单样式 */
.notice-item {
  position: relative;
}

.unread-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #FF6666;
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
  z-index: 1;
}

.notice-menu {
  position: absolute;
  top: 50%;
  left: 100%;
  transform: translateY(-50%);
  margin-left: 10px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  padding: 10px 0;
  min-width: 120px;
  z-index: 100;
}

.menu-item {
  padding: 10px 20px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.menu-item:hover {
  background: #FFE6E6;
  color: #FF6666;
}

.menu-badge {
  background: #FF6666;
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

/* 好友申请弹窗样式 */
.friend-request-container {
  padding: 20px;
}

.no-notices {
  text-align: center;
  padding: 50px 20px;
  color: #999;
  font-size: 16px;
}

.notice-empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  filter: grayscale(0.3);
  animation: noticeFloat 2.5s ease-in-out infinite;
}

@keyframes noticeFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6px); }
}

.notice-empty-title {
  font-size: 17px;
  font-weight: 600;
  color: #666;
  margin: 0 0 8px 0;
}

.notice-empty-desc {
  font-size: 13px;
  color: #bbb;
  margin: 0;
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding: 10px 15px;
  background: #F8F8F8;
  border-radius: 8px;
}

.notice-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #F8F8F8;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.notice-item:hover {
  background: #FFE6E6;
}

.notice-item.read {
  background: #F0F0F0;
  opacity: 0.7;
}

.notice-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.notice-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.notice-content {
  flex: 1;
  min-width: 0;
}

.notice-text {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-time {
  font-size: 12px;
  color: #999;
}

.notice-actions {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

/* 聊天窗口样式 */
.chat-window {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  width: 800px;
  background: white;
  box-shadow: -2px 0 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  display: flex;
  flex-direction: row;
}

/* 左侧好友列表 */
.chat-left-panel {
  width: 300px;
  border-right: 1px solid #E8E8E8;
  display: flex;
  flex-direction: column;
}

.chat-search {
  padding: 10px;
  border-bottom: 1px solid #E8E8E8;
}

.friend-list {
  flex: 1;
  overflow-y: auto;
}

/* 好友列表空状态 */
.empty-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 360px;
  padding: 30px 20px;
  text-align: center;
  background: linear-gradient(180deg, #FFFBFB 0%, #FFF5F5 50%, #FFF8F8 100%);
}

.empty-illustration {
  position: relative;
  margin-bottom: 20px;
}

.empty-icon-bg {
  width: 120px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle, rgba(255,184,184,0.15) 0%, transparent 70%);
  border-radius: 50%;
  animation: emptyPulse 3s ease-in-out infinite;
}

@keyframes emptyPulse {
  0%, 100% { transform: scale(1); background: radial-gradient(circle, rgba(255,184,184,0.15) 0%, transparent 70%); }
  50% { transform: scale(1.05); background: radial-gradient(circle, rgba(255,184,184,0.25) 0%, transparent 70%); }
}

.empty-friends-icon {
  width: 90px;
  height: 90px;
}

.empty-bubble {
  position: absolute;
  font-size: 20px;
  animation: bubbleFloat 2s ease-in-out infinite;
  opacity: 0.8;
}

.bubble-1 {
  top: -5px;
  right: -10px;
  animation-delay: 0s;
}

.bubble-2 {
  bottom: 5px;
  left: -15px;
  animation-delay: 0.5s;
}

.bubble-3 {
  top: 30px;
  left: -8px;
  animation-delay: 1s;
  font-size: 16px;
}

@keyframes bubbleFloat {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-8px) scale(1.15); }
}

.empty-title {
  font-size: 17px;
  font-weight: 600;
  color: #cc7777;
  margin: 0 0 8px 0;
}

.empty-desc {
  font-size: 13px;
  color: #d4a0a0;
  margin: 0 0 22px 0;
  letter-spacing: 0.5px;
}

.empty-action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 10px 24px;
  background: linear-gradient(135deg, #FFB8B8, #FF9999);
  border: none;
  border-radius: 24px;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 3px 12px rgba(255,153,153,0.35);
  transition: all 0.3s ease;
}

.empty-action-btn span {
  font-size: 18px;
  font-weight: 400;
  margin-right: 2px;
}

.empty-action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255,130,130,0.45);
  background: linear-gradient(135deg, #FFA8A8, #FF8888);
}

.empty-action-btn:active {
  transform: translateY(0);
}

.friend-item {
  display: flex;
  align-items: center;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.friend-item:hover {
  background: #F8F8F8;
}

.friend-item.active {
  background: #FFE6E6;
}

.friend-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.friend-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.friend-info {
  flex: 1;
  margin-left: 15px;
  min-width: 0;
}

.friend-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: flex;
  align-items: center;
  gap: 5px;
}

.top-badge {
  font-size: 14px;
  flex-shrink: 0;
}

.friend-item.top {
  background: #FFF9E6;
  border-left: 3px solid #FFB800;
}

.friend-item.top:hover {
  background: #FFF3CC;
}

.friend-last-message {
  font-size: 14px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.friend-time {
  font-size: 12px;
  color: #999;
  flex-shrink: 0;
  align-self: flex-end;
  margin-bottom: 5px;
  margin-left: 10px;
}

/* 右侧聊天窗口 */
.chat-right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 15px 20px;
  border-bottom: 1px solid #E8E8E8;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #F8F8F8;
  position: relative;
}

.chat-header-info {
  display: flex;
  align-items: center;
}

.chat-header-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 15px;
}

.chat-header-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.chat-header-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.chat-close {
  font-size: 20px;
  color: #999;
  cursor: pointer;
  transition: color 0.3s ease;
}

.chat-close:hover {
  color: #FF6666;
}

.chat-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #F8F8F8;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.message-item {
  display: flex;
  align-items: flex-end;
  gap: 10px;
}

.self-message {
  justify-content: flex-end;
}

.other-message {
  justify-content: flex-start;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-bubble {
  max-width: 60%;
  padding: 10px 15px;
  border-radius: 18px;
  position: relative;
}

.self-bubble {
  background: #FF9999;
  color: white;
  border-bottom-right-radius: 4px;
}

.other-bubble {
  background: white;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.message-content {
  font-size: 14px;
  line-height: 1.4;
  word-wrap: break-word;
}

.message-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
  margin-top: 5px;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.message-status {
  font-size: 12px;
}

.status-sending {
  color: #999;
}

.status-failed {
  color: #FF6666;
  cursor: pointer;
}

.status-failed:hover {
  text-decoration: underline;
}

.status-sent {
  color: #999;
}

.chat-input {
  padding: 15px 20px;
  border-top: 1px solid #E8E8E8;
  background: white;
  display: flex;
  align-items: flex-end;
  gap: 10px;
  position: relative;
}

.chat-input .el-input {
  flex: 1;
  min-height: 40px;
}

.chat-input .el-textarea__inner {
  resize: none;
  min-height: 40px;
  max-height: 120px;
  border-radius: 20px;
  padding: 10px 15px;
}

.chat-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.emoji-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: #F5F5F5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.emoji-btn:hover {
  background: #E8E8E8;
  transform: scale(1.1);
}

.emoji-icon {
  font-size: 20px;
}

.emoji-picker {
  position: absolute;
  bottom: 100%;
  right: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.15);
  padding: 15px;
  width: 350px;
  margin-bottom: 10px;
  z-index: 1000;
}

.emoji-categories {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #F0F0F0;
}

.category-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.category-btn:hover {
  background: #F5F5F5;
}

.category-btn.active {
  background: #FFE6E6;
}

.emoji-list {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 5px;
  max-height: 250px;
  overflow-y: auto;
}

.emoji-list::-webkit-scrollbar {
  width: 4px;
}

.emoji-list::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 2px;
}

.emoji-item {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.15s ease;
}

.emoji-item:hover {
  background: #F0F0F0;
  transform: scale(1.3);
}

.empty-chat {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 16px;
}

.empty-chat-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F8F8F8;
}

.empty-chat-tip {
  text-align: center;
  color: #999;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-chat-tip p {
  font-size: 16px;
  margin: 0;
}

/* 聊天窗口滚动条 */
.chat-content::-webkit-scrollbar,
.friend-list::-webkit-scrollbar {
  width: 6px;
}

.chat-content::-webkit-scrollbar-track,
.friend-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.chat-content::-webkit-scrollbar-thumb,
.friend-list::-webkit-scrollbar-thumb {
  background: #FF9999;
  border-radius: 3px;
}

.chat-content::-webkit-scrollbar-thumb:hover,
.friend-list::-webkit-scrollbar-thumb:hover {
  background: #FF6666;
}

/* 添加好友弹窗样式 */
.add-friend-container {
  padding: 20px;
}

.search-section {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-section .el-input {
  flex: 1;
  border-radius: 20px;
  border: 2px solid #F8E1E1;
}

.search-section .el-input__wrapper {
  border-radius: 20px;
}

.search-results {
  max-height: 400px;
  overflow-y: auto;
}

.user-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.user-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  background: #F8F8F8;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.user-item:hover {
  background: #FFE6E6;
}

.friend-user-info {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1;
}

.user-details h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #333;
}

.user-details p {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.no-results {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  font-size: 16px;
}

.loading {
  padding: 20px 0;
}

/* 聊天头部操作区域 */
.chat-header-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.chat-more {
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  padding: 5px 14px;
  border-radius: 6px;
  border: 1px solid #e5e5e5;
  color: #666;
  background: transparent;
  letter-spacing: 0.5px;
  user-select: none;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.chat-more:hover {
  color: #333;
  background: #fafafa;
  border-color: #ccc;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.chat-more:active {
  background: #f0f0f0;
  border-color: #bbb;
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.06);
  transform: scale(0.97);
}

/* 聊天窗口更多菜单样式 */
.chat-more-menu {
  position: absolute;
  top: 60px;
  right: 10px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  padding: 6px;
  min-width: 160px;
  z-index: 1000;
}

.chat-more-menu .menu-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 16px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.chat-more-menu .menu-item:hover {
  background: #f5f5f5;
}

.chat-more-menu .menu-icon {
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chat-more-menu .menu-danger {
  color: #ff4d4f;
}

.chat-more-menu .menu-danger:hover {
  background: #fff2f0;
}

.menu-divider {
  height: 1px;
  background: #f0f0f0;
  margin: 8px 0;
}

/* 搜索聊天记录弹窗样式 */
.search-chat-container {
  padding: 10px;
}

.search-input-wrapper {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input-wrapper .search-input {
  flex: 1;
}

.search-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px;
  gap: 10px;
}

.search-loading .loading-text {
  font-size: 14px;
  color: #999;
}

.search-results {
  max-height: 300px;
  overflow-y: auto;
}

.search-result-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 15px;
  border-bottom: 1px solid #f0f0f0;
}

.search-result-item:last-child {
  border-bottom: none;
}

.result-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.result-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.result-info {
  flex: 1;
  min-width: 0;
}

.result-content {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
  word-wrap: break-word;
}

.result-time {
  font-size: 12px;
  color: #999;
}

.empty-results {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  font-size: 14px;
}

/* 投诉弹窗样式 */
.complaint-container {
  padding: 10px;
}

.complaint-tip {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #666;
}

.complaint-textarea {
  width: 100%;
}

/* 设置备注弹窗样式 */
.nickname-container {
  padding: 10px;
}

.nickname-tip {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #666;
}

.nickname-input {
  width: 100%;
}
</style>
