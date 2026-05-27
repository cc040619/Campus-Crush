<template>
  <div class="user-home-page">
    <!-- 顶部导航栏 -->
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回社区
        </el-button>
        <h2>{{ userId === currentUserId ? '我的主页' : '用户主页' }}</h2>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="user-home-container">
      <!-- 用户信息区域 -->
      <div class="user-info-section" :class="{ loading: pageLoading }">
        <div v-if="pageLoading" class="skeleton-container">
          <div class="skeleton-profile">
            <div class="skeleton-avatar"></div>
            <div class="skeleton-details">
              <div class="skeleton-username"></div>
              <div class="skeleton-bio"></div>
              <div class="skeleton-stats">
                <div class="skeleton-stat"></div>
                <div class="skeleton-stat"></div>
                <div class="skeleton-stat"></div>
                <div class="skeleton-stat"></div>
              </div>
              <div class="skeleton-button"></div>
            </div>
          </div>
        </div>
        <div v-else class="user-profile content-fade-in">
          <div class="avatar">
            <img :src="(userInfo.avatar && userInfo.avatar.trim() !== '' && userInfo.avatar !== 'null' && userInfo.avatar !== 'undefined') ? userInfo.avatar : 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'" alt="用户头像" />
          </div>
          <div class="user-details">
            <h3 class="username">{{ userInfo.nickname || userInfo.username || '用户名称' }}</h3>
            <p class="bio">{{ userInfo.intro || '这是一个用户简介' }}</p>
            <div class="stats">
              <div class="stat-item">
                <span class="stat-value">{{ postCount }}</span>
                <span class="stat-label">作品</span>
              </div>
              <div class="stat-item" @click="handleShowFollowers">
                <span class="stat-value">{{ followerCount }}</span>
                <span class="stat-label">粉丝</span>
              </div>
              <div class="stat-item" @click="handleShowFollowing">
                <span class="stat-value">{{ followingCount }}</span>
                <span class="stat-label">关注</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">{{ likeAndCollectCount }}</span>
                <span class="stat-label">获赞与收藏</span>
              </div>
            </div>
            <div class="action-buttons">
              <el-button 
                v-if="userId !== currentUserId" 
                type="primary" 
                @click="toggleFollow"
                :loading="followLoading"
                :disabled="followLoading"
              >
                {{ isFollowing ? '已关注' : '关注' }}
              </el-button>
              <el-button v-else type="info" @click="openEditProfile">编辑资料</el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 关注/粉丝列表 -->
      <div class="followers-modal" v-if="showFollowers">
        <div class="modal-content">
          <div class="modal-header">
            <h3>粉丝列表</h3>
            <el-icon class="close-btn" @click="showFollowers = false"><Close /></el-icon>
          </div>
          <div class="follow-list">
            <div v-if="followers.length === 0" class="empty-list">
              <p>当前粉丝列表为空</p>
            </div>
            <div v-else v-for="follower in followers" :key="follower.id" class="follow-item">
              <div class="follow-info">
                <div class="avatar" @click="goToUserHome(follower.id)">
                  <img :src="(follower.avatar && follower.avatar.trim() !== '' && follower.avatar !== 'null' && follower.avatar !== 'undefined') ? follower.avatar : 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'" alt="粉丝头像" />
                </div>
                <div class="follow-details" @click="goToUserHome(follower.id)">
                  <h4>{{ follower.nickname || follower.username }}</h4>
                  <p class="follower-count">{{ follower.followerCount }} 粉丝</p>
                </div>
              </div>
              <el-button v-if="!follower.following" type="primary" size="small" @click.stop="followUser(follower.id)">关注</el-button>
            </div>
          </div>
        </div>
      </div>

      <div class="following-modal" v-if="showFollowing">
        <div class="modal-content">
          <div class="modal-header">
            <h3>关注列表</h3>
            <el-icon class="close-btn" @click="showFollowing = false"><Close /></el-icon>
          </div>
          <div class="follow-list">
            <div v-if="followings.length === 0" class="empty-list">
              <p>当前关注列表为空</p>
            </div>
            <div v-else v-for="following in followings" :key="following.id" class="follow-item">
              <div class="follow-info">
                <div class="avatar" @click="goToUserHome(following.id)">
                  <img :src="(following.avatar && following.avatar.trim() !== '' && following.avatar !== 'null' && following.avatar !== 'undefined') ? following.avatar : 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'" alt="关注用户头像" />
                </div>
                <div class="follow-details" @click="goToUserHome(following.id)">
                  <h4>{{ following.nickname || following.username || '默认用户' }}</h4>
                </div>
              </div>
              <el-button type="danger" size="small" @click.stop="unfollowUser(following.id)">取关</el-button>
            </div>
          </div>
        </div>
      </div>



      <!-- 内容Tab -->
      <div class="content-tabs">
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'posts' }" 
          @click="activeTab = 'posts'"
        >
          <el-icon><Grid /></el-icon>
          <span>作品</span>
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'collections' }" 
          @click="activeTab = 'collections'"
        >
          <el-icon><Collection /></el-icon>
          <span>收藏</span>
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 'likes' }" 
          @click="activeTab = 'likes'"
        >
          <el-icon><Star /></el-icon>
          <span>点赞</span>
        </div>
      </div>

      <!-- 内容列表 -->
      <div class="content-grid" :class="{ loading: contentLoading }">
        <div v-if="contentLoading" class="content-skeleton">
          <div v-for="i in 8" :key="i" class="skeleton-content-item"></div>
        </div>
        <template v-else>
          <div 
            v-for="post in currentContent" 
            :key="post.id"
            class="content-item"
            @click="goToPostDetail(post.id)"
          >
            <div class="content-image">
              <img 
                :src="Array.isArray(post.images) ? post.images[0] : (typeof post.images === 'string' ? JSON.parse(post.images)[0] : 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg')" 
                :alt="post.title" 
                @error="$event.target.src = 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'"
              />
              <div 
                v-if="activeTab === 'posts' && Number(userId) === Number(currentUserId)" 
                class="delete-btn" 
                @click.stop="handleDeletePost(post.id)"
              >
                <svg class="delete-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M3 6h18"></path>
                  <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
                  <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
                  <line x1="10" y1="11" x2="10" y2="17"></line>
                  <line x1="14" y1="11" x2="14" y2="17"></line>
                </svg>
              </div>
            </div>
            <div class="content-overlay">
              <div class="content-stats">
                <div class="stat-item">
                  <el-icon><Star /></el-icon>
                  <span>{{ post.likeCount || 0 }}</span>
                </div>
                <div class="stat-item">
                  <el-icon><Collection /></el-icon>
                  <span>{{ post.collectCount || 0 }}</span>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>

      <!-- 编辑资料弹窗 -->
      <el-dialog
        v-model="showEditProfile"
        title="编辑资料"
        width="500px"
        :close-on-click-modal="false"
        :destroy-on-close="true"
      >
        <el-form :model="editForm" label-width="80px" :rules="rules" ref="editFormRef">
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="editForm.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-select v-model="editForm.gender" placeholder="请选择性别" :allow-create="false">
              <el-option label="不愿透露" :value="0" />
              <el-option label="男" :value="1" />
              <el-option label="女" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="简介" prop="intro">
            <el-input
              v-model="editForm.intro"
              type="textarea"
              :rows="4"
              placeholder="请输入用户简介"
              maxlength="255"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="头像">
            <div class="edit-avatar-section">
              <img :src="editAvatarPreview || userInfo.avatar || 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'" class="edit-avatar-preview" />
              <el-button type="primary" size="small" @click="handleEditAvatarClick" :loading="avatarUploading">
                {{ editForm.avatarFile ? '已选择' : '更换头像' }}
              </el-button>
            </div>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="showEditProfile = false">取消</el-button>
            <el-button type="primary" @click="submitEditProfile" :loading="editLoading">提交</el-button>
          </span>
        </template>
      </el-dialog>
      <input type="file" ref="editAvatarInput" style="display: none" accept=".jpg,.png" @change="handleEditAvatarChange" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, Close, Grid, Collection, Star, ChatDotRound, Delete } from '@element-plus/icons-vue'
import { postApi, followApi, userApi } from '../api/community'
import { uploadAvatar } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
// 将userId转换为数字类型
const userId = Number(route.params.id) || 1
// 从localStorage中获取当前登录用户的真实ID
const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
const currentUserId = Number(currentUser.id) || 1

// 数据
const activeTab = ref('posts')
const userInfo = ref({})
const postCount = ref(0)
const followerCount = ref(0)
const followingCount = ref(0)
const likeAndCollectCount = ref(0)
const isFollowing = ref(false)
const showFollowers = ref(false)
const showFollowing = ref(false)
const showEditProfile = ref(false)

const posts = ref([])
const collections = ref([])
const likes = ref([])

const followers = ref([])
const followings = ref([])

const loading = ref(false)
const followLoading = ref(false)
const editLoading = ref(false)
const avatarUploading = ref(false)
const pageLoading = ref(true)
const contentLoading = ref(true)
const editAvatarInput = ref(null)
const editAvatarPreview = ref('')

// 编辑资料表单
const editForm = ref({
  nickname: '',
  gender: 0,
  intro: '',
  avatarFile: null
})

// 表单验证规则
const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  gender: [
    { required: false, message: '请选择性别', trigger: 'change' }
  ],
  intro: [
    { max: 255, message: '简介长度不能超过255个字符', trigger: 'blur' }
  ]
}

// 表单引用
const editFormRef = ref(null)

// 计算属性
const currentContent = computed(() => {
  switch (activeTab.value) {
    case 'posts':
      return posts.value
    case 'collections':
      return collections.value
    case 'likes':
      return likes.value
    default:
      return posts.value
  }
})

// 方法
const goBack = () => {
  // 从关注列表进入的用户主页，返回关注列表
  if (route.query.from === 'follow') {
    router.go(-1)
  } else {
    router.push('/community')
  }
}

const goToUserHome = (userId) => {
  router.push({
    path: `/community/user/${userId}`,
    query: { from: 'follow' }
  })
}

// 打开编辑资料弹窗
const openEditProfile = () => {
  editForm.value.nickname = userInfo.value.nickname || ''
  editForm.value.gender = userInfo.value.gender !== undefined ? userInfo.value.gender : null
  editForm.value.intro = userInfo.value.intro || ''
  editForm.value.avatarFile = null
  editAvatarPreview.value = ''
  showEditProfile.value = true
}

const handleEditAvatarClick = () => {
  if (editAvatarInput.value) {
    editAvatarInput.value.click()
  }
}

const handleEditAvatarChange = (e) => {
  const file = e.target.files[0]
  if (!file) return
  if (!file.name.endsWith('.jpg') && !file.name.endsWith('.png')) {
    ElMessage.error('仅支持jpg和png格式的图片')
    return
  }
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过10MB')
    return
  }
  const reader = new FileReader()
  reader.onload = (ev) => {
    editAvatarPreview.value = ev.target.result
  }
  reader.readAsDataURL(file)
  editForm.value.avatarFile = file
}

// 提交编辑资料
const submitEditProfile = async () => {
  if (!editFormRef.value) return

  try {
    await editFormRef.value.validate()
    editLoading.value = true

    // 如果选择了新头像，先上传头像
    if (editForm.value.avatarFile) {
      try {
        const avatarRes = await uploadAvatar(editForm.value.avatarFile)
        if (avatarRes.code !== 200) {
          ElMessage.error(avatarRes.msg || '头像上传失败')
          editLoading.value = false
          return
        }
        // 更新 localStorage 中的头像，确保社区页面等处实时同步
        const userStr = localStorage.getItem('user')
        if (userStr) {
          const userObj = JSON.parse(userStr)
          userObj.avatar = avatarRes.data
          localStorage.setItem('user', JSON.stringify(userObj))
        }
      } catch (e) {
        ElMessage.error('头像上传失败')
        editLoading.value = false
        return
      }
    }

    const submitData = {
      nickname: editForm.value.nickname,
      gender: editForm.value.gender !== null ? editForm.value.gender : 0,
      intro: editForm.value.intro
    }

    const response = await userApi.updateProfile(submitData)
    if (response.code === 200) {
      ElMessage.success('更新资料成功')
      showEditProfile.value = false
      await loadFollowCount()
      await loadUserInfo()
    } else {
      ElMessage.error(response.msg || '更新资料失败')
    }
  } catch (error) {
    console.error('更新资料失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    editLoading.value = false
  }
}

const goToPostDetail = (postId) => {
  sessionStorage.setItem('fromPath', `/community/user/${userId}`)
  router.push(`/community/post/${postId}`)
}

const handleDeletePost = async (postId) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该条帖子吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await postApi.deletePost(postId)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      // 重新加载用户帖子
      await loadUserPosts()
      await loadPostCount()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除帖子失败:', error)
      ElMessage.error('网络错误，请稍后重试')
    }
  }
}

const toggleFollow = async () => {
  followLoading.value = true
  try {
    const response = await followApi.followUser({
      followingId: userId
    })
    if (response.code === 200) {
      isFollowing.value = !isFollowing.value
      // 重新加载关注和粉丝数量
      await loadFollowCount()
      ElMessage.success(response.msg || '操作成功')
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    console.error('关注操作失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    followLoading.value = false
  }
}

const followUser = async (targetUserId) => {
  try {
    const response = await followApi.followUser({
      followingId: targetUserId
    })
    if (response.code === 200) {
      // 更新粉丝列表中对应用户的following状态和粉丝数量
      const follower = followers.value.find(f => f.id === targetUserId)
      if (follower) {
        follower.following = true
        follower.followerCount = (follower.followerCount || 0) + 1
      }
      // 更新关注数量
      followingCount.value++
      ElMessage.success('关注成功')
    } else {
      ElMessage.error(response.msg || '关注失败')
    }
  } catch (error) {
    console.error('关注操作失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

const unfollowUser = async (targetUserId) => {
  try {
    const response = await followApi.followUser({
      followingId: targetUserId
    })
    if (response.code === 200) {
      // 从关注列表中移除该用户
      followings.value = followings.value.filter(user => user.id !== targetUserId)
      // 重新加载关注和粉丝数量
      await loadFollowCount()
      ElMessage.success('取消关注成功')
    } else {
      ElMessage.error(response.msg || '取消关注失败')
    }
  } catch (error) {
    console.error('取消关注操作失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 生命周期
onMounted(() => {
  loadAllData()
})

const loadAllData = async () => {
  pageLoading.value = true
  contentLoading.value = true
  
  try {
    await Promise.all([
      loadUserInfo(),
      loadPostCount(),
      loadFollowCount()
    ])
    pageLoading.value = false
    
    await Promise.all([
      loadUserPosts(),
      loadUserCollections(),
      loadUserLikes()
    ])
    contentLoading.value = false
  } catch (error) {
    console.error('加载数据失败:', error)
    pageLoading.value = false
    contentLoading.value = false
  }
}

const loadUserInfo = async () => {
  loading.value = true
  try {
    const response = await userApi.getUserInfo(userId)
    if (response.code === 200) {
      userInfo.value = response.data
      // 不更新followerCount和followingCount，由loadFollowCount方法负责更新
      isFollowing.value = userInfo.value.isFollowing
      likeAndCollectCount.value = userInfo.value.likeAndCollectCount || 0
    } else {
      ElMessage.error(response.msg || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

const loadPostCount = async () => {
  try {
    const response = await postApi.getPostCountByUserId(userId)
    if (response.code === 200) {
      postCount.value = response.data
    } else {
      console.error('获取帖子数量失败:', response.msg)
    }
  } catch (error) {
    console.error('获取帖子数量失败:', error)
  }
}

const loadUserPosts = async () => {
  try {
    const response = await postApi.getUserPosts(userId)
    if (response.code === 200) {
      posts.value = response.data
      // 为每个帖子获取点赞数和收藏数
      for (const post of posts.value) {
        await loadPostInfo(post)
      }
    } else {
      ElMessage.error(response.msg || '获取用户帖子失败')
    }
  } catch (error) {
    console.error('获取用户帖子失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

const loadUserCollections = async () => {
  try {
    const response = await postApi.getUserCollections(userId)
    if (response.code === 200) {
      collections.value = response.data
      // 为每个帖子获取点赞数和收藏数
      for (const post of collections.value) {
        await loadPostInfo(post)
      }
    } else {
      ElMessage.error(response.msg || '获取用户收藏失败')
    }
  } catch (error) {
    console.error('获取用户收藏失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

const loadUserLikes = async () => {
  try {
    const response = await postApi.getUserLikes(userId)
    if (response.code === 200) {
      likes.value = response.data
      // 为每个帖子获取点赞数和收藏数
      for (const post of likes.value) {
        await loadPostInfo(post)
      }
    } else {
      ElMessage.error(response.msg || '获取用户点赞失败')
    }
  } catch (error) {
    console.error('获取用户点赞失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 获取帖子的点赞数和收藏数
const loadPostInfo = async (post) => {
  try {
    const response = await postApi.getPostInfo(post.id)
    if (response.code === 200) {
      post.likeCount = response.data.like_count || 0
      post.collectCount = response.data.collect_count || 0
    }
  } catch (error) {
    console.error(`获取帖子${post.id}的信息失败:`, error)
    // 失败时设置默认值
    post.likeCount = 0
    post.collectCount = 0
  }
}

const loadFollowCount = async () => {
  try {
    const response = await followApi.getFollowCount(userId)
    if (response.code === 200) {
      followingCount.value = response.data.followingCount || 0
      followerCount.value = response.data.followerCount || 0
    } else {
      console.error('获取关注和粉丝数量失败:', response.msg)
    }
  } catch (error) {
    console.error('获取关注和粉丝数量失败:', error)
  }
}

const loadFollowers = async () => {
  try {
    const response = await followApi.getFollowList(userId, 'followers')
    if (response.code === 200) {
      followers.value = response.data.list || []
    } else {
      ElMessage.error(response.msg || '获取粉丝列表失败')
    }
  } catch (error) {
    console.error('获取粉丝列表失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

const loadFollowings = async () => {
  try {
    const response = await followApi.getFollowList(userId, 'following')
    if (response.code === 200) {
      followings.value = response.data.list || []
    } else {
      ElMessage.error(response.msg || '获取关注列表失败')
    }
  } catch (error) {
    console.error('获取关注列表失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 显示粉丝/关注列表时加载数据
const handleShowFollowers = () => {
  showFollowers.value = !showFollowers.value
  if (showFollowers.value) {
    loadFollowers()
  }
}

const handleShowFollowing = () => {
  showFollowing.value = !showFollowing.value
  if (showFollowing.value) {
    loadFollowings()
  }
}
</script>

<style scoped>
.user-home-page {
  min-height: 100vh;
  background: #FFFDFD;
  padding: 40px;
}

/* 骨架屏样式 */
.skeleton-container {
  padding: 30px;
}

.skeleton-profile {
  display: flex;
  align-items: center;
  gap: 30px;
}

.skeleton-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
}

.skeleton-details {
  flex: 1;
}

.skeleton-username {
  width: 150px;
  height: 28px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  border-radius: 4px;
  margin-bottom: 12px;
}

.skeleton-bio {
  width: 200px;
  height: 18px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  border-radius: 4px;
  margin-bottom: 20px;
}

.skeleton-stats {
  display: flex;
  gap: 40px;
  margin-bottom: 20px;
}

.skeleton-stat {
  width: 60px;
  height: 24px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  border-radius: 4px;
}

.skeleton-button {
  width: 100px;
  height: 36px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  border-radius: 4px;
}

.content-skeleton {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.skeleton-content-item {
  aspect-ratio: 1;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: skeleton-loading 1.5s infinite;
  border-radius: 8px;
}

@keyframes skeleton-loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* 过渡动画 */
.content-fade-in {
  animation: fade-in 0.4s ease-out;
}

@keyframes fade-in {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.content-list-enter-active {
  animation: slide-up 0.3s ease-out;
}

.content-list-leave-active {
  animation: slide-down 0.2s ease-in;
}

@keyframes slide-up {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slide-down {
  from {
    opacity: 1;
    transform: translateY(0);
  }
  to {
    opacity: 0;
    transform: translateY(-10px);
  }
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  max-width: 1000px;
  margin: 0 auto 30px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.page-header h2 {
  color: #FF9999;
  font-size: 24px;
  margin: 0;
}

.user-home-container {
  max-width: 1000px;
  margin: 0 auto;
}

/* 用户信息区域 */
.user-info-section {
  background: #F8F8F8;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 30px;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  border: 4px solid #FFE6E6;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-details {
  flex: 1;
}

.username {
  font-size: 24px;
  font-weight: bold;
  margin: 0 0 10px 0;
  color: #333;
}

.bio {
  font-size: 16px;
  color: #666;
  margin: 0 0 20px 0;
}

.stats {
  display: flex;
  gap: 40px;
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: color 0.3s ease;
}

.stat-item:hover {
  color: #FF9999;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

/* 关注/粉丝列表 */
.followers-modal, .following-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 400px;
  max-height: 600px;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #E8E8E8;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.close-btn {
  cursor: pointer;
  font-size: 20px;
  color: #999;
}

.follow-list {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.follow-item {
  display: flex;
  align-items: center;
  gap: 15px;
  justify-content: space-between;
}

.follow-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.follow-item .avatar {
  width: 40px;
  height: 40px;
  border: none;
}

.follow-details h4 {
  margin: 0;
  font-size: 14px;
  color: #333;
}

.empty-list {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.empty-list p {
  margin: 0;
  font-size: 16px;
  color: #999;
  font-weight: 400;
}

/* 内容Tab */
.content-tabs {
  display: flex;
  border-bottom: 1px solid #E8E8E8;
  margin-bottom: 20px;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 15px 30px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #666;
}

.tab-item:hover {
  color: #FF9999;
}

.tab-item.active {
  color: #FF9999;
  border-bottom: 2px solid #FF9999;
  font-weight: bold;
}

/* 内容列表 */
.content-grid {
  display: grid !important;
  grid-template-columns: repeat(4, 1fr) !important;
  grid-auto-flow: row !important;
  gap: 15px;
  max-width: 100%;
  overflow: visible !important;
}

.content-item {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.content-item:hover {
  transform: translateY(-5px);
}

.content-image {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  overflow: hidden;
}

.content-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.delete-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: rgba(0, 0, 0, 0.4);
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s ease;
}

.delete-btn:hover {
  background: rgba(255, 0, 0, 0.8);
  transform: scale(1.1);
}

.delete-btn .delete-icon {
  width: 16px;
  height: 16px;
  color: white;
}

.content-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
  padding: 15px;
  color: white;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.content-item:hover .content-overlay {
  opacity: 1;
}

.content-stats {
  display: flex;
  gap: 20px;
}

.content-stats .stat-item {
  color: white;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
}

.content-stats .stat-value {
  color: white;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-home-page {
    padding: 20px;
  }
  
  .user-profile {
    flex-direction: column;
    text-align: center;
  }
  
  .stats {
    justify-content: center;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .content-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
  
  .modal-content {
    width: 90%;
    max-height: 80vh;
  }
}

.edit-avatar-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.edit-avatar-preview {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #f0f0f0;
}

@media (max-width: 480px) {
  .content-grid {
    grid-template-columns: 1fr;
  }

  .stats {
    gap: 20px;
  }
}
</style>
