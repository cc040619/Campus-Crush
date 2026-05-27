<template>
  <div class="post-detail-page">
    <!-- 顶部导航栏 -->
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          {{ backText }}
        </el-button>
        <h2>帖子详情</h2>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="post-detail-container">
      <!-- 左侧帖子内容 -->
      <div class="post-content">
        <el-skeleton :loading="loading" animated>
          <!-- 帖子图片 -->
          <div class="post-images" v-if="!loading && post.images">
            <el-carousel :interval="5000" type="card" height="400px">
              <el-carousel-item v-for="(image, index) in (Array.isArray(post.images) ? post.images : JSON.parse(post.images))" :key="index">
                <div class="carousel-image" @click="previewImage(image)">
                  <img :src="image" :alt="post.title" />
                  <div class="image-hover-mask">
                    <el-icon :size="28"><ZoomIn /></el-icon>
                    <span>查看原图</span>
                  </div>
                </div>
              </el-carousel-item>
            </el-carousel>
            <!-- 下载图片按钮行 -->
            <div class="download-buttons" v-if="post.images && (Array.isArray(post.images) ? post.images : JSON.parse(post.images)).length > 0">
              <button
                v-for="(image, index) in (Array.isArray(post.images) ? post.images : JSON.parse(post.images))"
                :key="'dl-' + index"
                class="download-btn"
                @click="downloadImage(image, index)"
              >
                <el-icon><Download /></el-icon>
                下载图片 {{ index + 1 }}
              </button>
            </div>
          </div>
          <template v-else>
            <el-skeleton-item variant="image" style="width: 100%; height: 400px; border-radius: 12px; margin-bottom: 10px;" />
            <el-skeleton-item variant="image" style="width: 100%; height: 400px; border-radius: 12px;" />
          </template>

          <!-- 帖子信息 -->
          <div class="post-info" v-if="!loading && post.id">
            <div class="author-info">
              <div class="avatar">
                <img :src="(post.authorAvatar && post.authorAvatar.trim() !== '' && post.authorAvatar !== 'null' && post.authorAvatar !== 'undefined') ? post.authorAvatar : 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'" alt="作者头像" />
              </div>
              <div class="author-details">
                <h3>{{ post.authorName || '默认用户' }}</h3>
                <p>发布于 {{ formatTime(post.createTime) }}</p>
              </div>
              <el-button v-if="!isAuthor && !isFollowing" type="primary" @click="toggleFollow">关注</el-button>
              <el-button v-if="!isAuthor && isFollowing" type="default" @click="toggleFollow">已关注</el-button>
            </div>

            <h2 class="post-title">{{ post.title }}</h2>
            <div class="post-tags">
              <span class="tag">{{ post.category }}</span>
            </div>
            <p class="post-text">{{ post.content }}</p>
            <div class="post-location" v-if="post.location">
              <el-icon><MapLocation /></el-icon>
              <span>{{ post.location }}</span>
            </div>

            <!-- 互动按钮 -->
            <div class="interaction-buttons">
              <div class="button-item" @click="toggleLike">
                <el-icon :class="{ active: isLiked }">
                  <Star />
                </el-icon>
                <span>{{ post.likeCount }}</span>
              </div>
              <div class="button-item" @click="toggleCollect">
                <el-icon :class="{ active: isCollected }">
                  <Collection />
                </el-icon>
                <span>{{ post.collectCount }}</span>
              </div>
              <div class="button-item" @click="sharePost">
                <el-icon><Share /></el-icon>
                <span>分享</span>
              </div>
            </div>
          </div>
          <template v-else>
            <el-skeleton-item variant="p" style="width: 80%;" />
            <el-skeleton-item variant="text" style="width: 60%;" />
            <el-skeleton-item variant="text" style="width: 100%;" />
            <el-skeleton-item variant="text" style="width: 100%;" />
            <el-skeleton-item variant="text" style="width: 70%;" />
          </template>
        </el-skeleton>
      </div>

      <!-- 右侧评论区 -->
      <div class="comment-section">
        <h3>评论 ({{ totalCommentCount }})</h3>
        
        <!-- 评论输入框 -->
        <div class="comment-input">
          <div class="avatar">
            <img :src="(currentUser.avatar && currentUser.avatar.trim() !== '' && currentUser.avatar !== 'null' && currentUser.avatar !== 'undefined') ? currentUser.avatar : 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'" alt="我的头像" />
          </div>
          <el-input
            v-model="commentContent"
            placeholder="写下你的评论..."
            type="textarea"
            :rows="3"
            @keyup.enter.ctrl="submitComment"
          />
          <el-button type="primary" @click="submitComment">发布</el-button>
        </div>

        <!-- 评论列表 -->
        <div class="comment-list">
          <el-skeleton v-if="commentLoading" :rows="5" animated />
          <div v-else v-for="comment in commentList" :key="comment.id" class="comment-item">
            <div class="avatar">
              <img :src="(comment.avatar && comment.avatar.trim() !== '' && comment.avatar !== 'null' && comment.avatar !== 'undefined') ? comment.avatar : 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'" alt="评论者头像" />
            </div>
            <div class="comment-content">
              <div class="comment-header">
                <div class="commenter-info">
                  <span class="commenter-name">{{ comment.userName || '评论者' }}</span>
                  <span v-if="comment.userId === post.userId" class="author-tag">作者</span>
                </div>
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
              </div>
              <p class="comment-text">{{ comment.content }}</p>
              <div class="comment-actions">
                <span class="action-item" @click="likeComment(comment.id, postId)">
                  <el-icon :class="{ active: comment.isLiked }">
                    <Star />
                  </el-icon>
                  <span>{{ comment.likeCount }}</span>
                </span>
                <span class="action-item" @click="showReplyBox(comment)">回复</span>
                <span 
                  v-if="comment.userId === currentUser.id" 
                  class="action-item delete-action" 
                  @click="confirmDeleteComment(comment.id)"
                >
                  <el-icon><Delete /></el-icon>
                  删除
                </span>
              </div>
              
              <!-- 回复输入框 -->
              <div v-if="replyTarget && replyTarget.id === comment.id" class="reply-input">
                <input 
                  v-model="replyContent" 
                  type="text" 
                  :placeholder="`回复 @${comment.userName}...`"
                  @keyup.enter="submitReply"
                />
                <button class="reply-btn" @click="submitReply">回复</button>
                <button class="cancel-btn" @click="cancelReply">取消</button>
              </div>
              
              <!-- 子评论列表 -->
              <div v-if="comment.children && comment.children.length > 0" class="replies-list">
                <div v-for="reply in comment.children" :key="reply.id" class="reply-item">
                  <div class="reply-avatar">
                    <img :src="(reply.avatar && reply.avatar.trim() !== '' && reply.avatar !== 'null' && reply.avatar !== 'undefined') ? reply.avatar : 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'" alt="回复者头像" />
                  </div>
                  <div class="reply-content">
                    <span class="reply-name">{{ reply.userName || '回复者' }}</span>
                    <span class="reply-text">{{ reply.content }}</span>
                    <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                    <span 
                      v-if="reply.userId === currentUser.id" 
                      class="reply-delete" 
                      @click="confirmDeleteComment(reply.id)"
                    >
                      <el-icon><Delete /></el-icon>
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div v-if="!commentLoading && commentList.length === 0" class="no-comments">
            暂无评论，快来抢沙发吧！
          </div>
        </div>

        <!-- 加载更多评论 -->
        <div class="load-more">
          <el-button 
            v-if="hasMoreComments" 
            type="primary" 
            plain 
            @click="loadMoreComments"
            :loading="commentLoading"
            :disabled="commentLoading"
          >
            加载更多评论
          </el-button>
          <div v-else class="no-more">
            没有更多评论了
          </div>
        </div>
      </div>
    </div>

    <!-- 图片全屏预览 -->
    <Teleport to="body">
      <transition name="img-preview-fade">
        <div v-if="imagePreviewVisible" class="image-preview-overlay" @click="closeImagePreview">
          <div class="image-preview-backdrop"></div>
          <div class="image-preview-container" @click.stop>
            <img :src="imagePreviewUrl" :alt="'原图预览'" class="image-preview-content" />
            <div class="image-preview-toolbar">
              <button class="img-toolbar-btn" @click="downloadCurrentImage">
                <el-icon><Download /></el-icon>
                下载原图
              </button>
            </div>
          </div>
          <button class="image-preview-close-btn" @click="closeImagePreview">
            <el-icon><Close /></el-icon>
          </button>
        </div>
      </transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, Star, Collection, Share, MapLocation, Delete, ZoomIn, Download } from '@element-plus/icons-vue'
import { postApi, commentApi, followApi } from '../api/community'
import { ElMessage, ElCarousel, ElCarouselItem, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const postId = route.params.id

// 数据
const post = ref({})
const commentList = ref([])
const commentContent = ref('')
const replyContent = ref('')
const replyTarget = ref(null)
const isLiked = ref(false)
const isCollected = ref(false)
const isFollowing = ref(false)
const hasMoreComments = ref(true)
const currentPage = ref(1)
const totalCommentCount = ref(0)
const loading = ref(false)
const commentLoading = ref(false)

// 图片预览
const imagePreviewVisible = ref(false)
const imagePreviewUrl = ref('')

const previewImage = (url) => {
  imagePreviewUrl.value = url
  imagePreviewVisible.value = true
}

const closeImagePreview = () => {
  imagePreviewVisible.value = false
  setTimeout(() => { imagePreviewUrl.value = '' }, 300)
}

const downloadCurrentImage = () => {
  downloadImage(imagePreviewUrl.value)
}

const downloadImage = async (url, index) => {
  try {
    const response = await fetch(url)
    const blob = await response.blob()
    const blobUrl = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = blobUrl
    // 从URL中提取文件名，或使用默认名称
    const urlParts = url.split('/')
    const filename = urlParts[urlParts.length - 1] || `image_${index !== undefined ? index + 1 : Date.now()}.jpg`
    a.download = filename.includes('.') ? filename : filename + '.jpg'
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(blobUrl)
  } catch (error) {
    // 降级：直接打开原图链接
    window.open(url, '_blank')
  }
}

// 获取当前登录用户信息
const currentUser = computed(() => {
  const userStr = localStorage.getItem('user')
  return userStr ? JSON.parse(userStr) : {}
})

// 判断是否是作者本人
const isAuthor = computed(() => {
  return post.value.userId && currentUser.value.id && post.value.userId === currentUser.value.id
})

// 返回文字，根据来源页面动态显示
const backText = computed(() => {
  const fromPath = sessionStorage.getItem('fromPath')
  if (fromPath && fromPath.startsWith('/community/topic/')) {
    return '返回话题'
  } else if (fromPath && fromPath.startsWith('/community/user/')) {
    return '返回主页'
  }
  return '返回社区'
})

// 方法
const goBack = () => {
  const fromPath = sessionStorage.getItem('fromPath')
  if (fromPath && (fromPath.startsWith('/community/topic/') || fromPath.startsWith('/community/user/'))) {
    router.push(fromPath)
  } else {
    router.push('/community')
  }
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const toggleFollow = async () => {
  try {
    const response = await followApi.followUser({
      followingId: post.value.userId
    })
    if (response.code === 200) {
      isFollowing.value = response.data
      ElMessage.success(isFollowing.value ? '关注成功' : '取消关注成功')
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    console.error('关注操作失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

const checkFollowStatus = async () => {
  if (isAuthor.value || !post.value.userId || !currentUser.value.id) return
  
  try {
    const response = await followApi.getFollowList(currentUser.value.id, 'following')
    if (response.code === 200) {
      const followingList = response.data.list || []
      isFollowing.value = followingList.some(user => user.id === post.value.userId)
    }
  } catch (error) {
    console.error('检查关注状态失败:', error)
  }
}

const toggleLike = async () => {
  try {
    const response = await postApi.likePost(postId)
    if (response.code === 200) {
      isLiked.value = !isLiked.value
      post.value.likeCount += isLiked.value ? 1 : -1
      ElMessage.success(response.msg || '操作成功')
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

const toggleCollect = async () => {
  try {
    const response = await postApi.collectPost(postId)
    if (response.code === 200) {
      isCollected.value = !isCollected.value
      post.value.collectCount += isCollected.value ? 1 : -1
      ElMessage.success(response.msg || '操作成功')
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

const sharePost = () => {
  navigator.clipboard.writeText(window.location.href)
  ElMessage.success('链接已复制到剪贴板')
}

const submitComment = async () => {
  if (!commentContent.value.trim()) return
  
  try {
    const response = await commentApi.createComment({
      postId: postId,
      content: commentContent.value,
      parentId: null
    })
    
    if (response.code === 200) {
      const newComment = response.data
      newComment.createTime = new Date().toISOString()
      newComment.isLiked = false
      newComment.likeCount = 0
      newComment.children = []
      commentList.value.unshift(newComment)
      totalCommentCount.value++
      commentContent.value = ''
      ElMessage.success('评论发布成功')
    } else {
      ElMessage.error(response.msg || '评论发布失败')
    }
  } catch (error) {
    console.error('评论发布失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

const submitReply = async () => {
  if (!replyContent.value.trim() || !replyTarget.value) return
  
  try {
    const response = await commentApi.createComment({
      postId: postId,
      content: replyContent.value,
      parentId: replyTarget.value.id
    })
    
    if (response.code === 200) {
      const newReply = response.data
      newReply.createTime = new Date().toISOString()
      newReply.isLiked = false
      newReply.likeCount = 0
      
      // 添加到父评论的children数组中
      const parentComment = commentList.value.find(c => c.id === replyTarget.value.id)
      if (parentComment) {
        if (!parentComment.children) {
          parentComment.children = []
        }
        parentComment.children.push(newReply)
      }
      
      totalCommentCount.value++
      replyContent.value = ''
      replyTarget.value = null
      ElMessage.success('回复成功')
    } else {
      ElMessage.error(response.msg || '回复失败')
    }
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

const showReplyBox = (comment) => {
  if (replyTarget.value && replyTarget.value.id === comment.id) {
    replyTarget.value = null
    replyContent.value = ''
  } else {
    replyTarget.value = comment
    replyContent.value = ''
  }
}

const cancelReply = () => {
  replyTarget.value = null
  replyContent.value = ''
}

const confirmDeleteComment = async (commentId) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该评论吗？删除后所有回复也会被删除。',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteComment(commentId)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const deleteComment = async (commentId) => {
  try {
    const response = await commentApi.deleteComment(commentId)
    if (response.code === 200) {
      // 从列表中移除评论
      commentList.value = commentList.value.filter(c => c.id !== commentId)
      
      // 检查是否是子评论
      commentList.value.forEach(comment => {
        if (comment.children) {
          comment.children = comment.children.filter(r => r.id !== commentId)
        }
      })
      
      totalCommentCount.value--
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    console.error('删除评论失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

const likeComment = async (commentId, postId) => {
  try {
    const response = await commentApi.likeComment(commentId, postId)
    if (response.code === 200) {
      // 查找评论并更新状态
      const findComment = (comments) => {
        for (const comment of comments) {
          if (comment.id === commentId) {
            comment.isLiked = response.data
            comment.likeCount += comment.isLiked ? 1 : -1
            return true
          }
          if (comment.children) {
            if (findComment(comment.children)) return true
          }
        }
        return false
      }
      findComment(commentList.value)
      ElMessage.success(response.msg || '操作成功')
    } else {
      ElMessage.error(response.msg || '操作失败')
    }
  } catch (error) {
    console.error('评论点赞失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

const loadMoreComments = async () => {
  if (commentLoading.value) return
  
  commentLoading.value = true
  try {
    const response = await commentApi.getCommentList(postId, {
      pageNum: currentPage.value + 1,
      pageSize: 10
    })
    
    if (response.code === 200) {
      const newComments = (response.data.list || []).map(comment => ({
        ...comment,
        isLiked: false,
        children: comment.children || []
      }))
      
      // 获取每个新评论的点赞状态
      for (const comment of newComments) {
        try {
          const statusResponse = await commentApi.getCommentStatus(comment.id)
          if (statusResponse.code === 200) {
            comment.isLiked = statusResponse.data
          }
        } catch (error) {
          console.error(`获取评论${comment.id}的点赞状态失败:`, error)
        }
      }
      
      commentList.value = [...commentList.value, ...newComments]
      currentPage.value++
      hasMoreComments.value = newComments.length === 10
    } else {
      ElMessage.error(response.msg || '加载评论失败')
    }
  } catch (error) {
    console.error('加载评论失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    commentLoading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadPostDetail()
  loadComments()
})

const loadPostDetail = async () => {
  loading.value = true
  try {
    const response = await postApi.getPostDetail(postId)
    if (response.code === 200) {
      post.value = response.data
      
      const statusResponse = await postApi.getPostStatus(postId)
      if (statusResponse.code === 200) {
        isLiked.value = statusResponse.data.isLiked
        isCollected.value = statusResponse.data.isCollected
      }
      
      await checkFollowStatus()
    } else {
      ElMessage.error(response.msg || '获取帖子详情失败')
    }
  } catch (error) {
    console.error('加载帖子详情失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  commentLoading.value = true
  try {
    const response = await commentApi.getCommentList(postId, {
      pageNum: 1,
      pageSize: 10
    })
    
    if (response.code === 200) {
      commentList.value = (response.data.list || []).map(comment => ({
        ...comment,
        isLiked: false,
        children: comment.children || []
      }))
      totalCommentCount.value = response.data.total || commentList.value.length
      hasMoreComments.value = response.data.list?.length === 10
      
      // 获取每个评论的点赞状态
      for (const comment of commentList.value) {
        try {
          const statusResponse = await commentApi.getCommentStatus(comment.id)
          if (statusResponse.code === 200) {
            comment.isLiked = statusResponse.data
          }
        } catch (error) {
          console.error(`获取评论${comment.id}的点赞状态失败:`, error)
        }
      }
    } else {
      ElMessage.error(response.msg || '加载评论失败')
    }
  } catch (error) {
    console.error('加载评论失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    commentLoading.value = false
  }
}
</script>

<style scoped>
.post-detail-page {
  min-height: 100vh;
  background: #FFFDFD;
  padding: 40px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  max-width: 1200px;
  margin: 0 auto 30px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.page-header h2 {
  color: #FF9999;
  font-size: 28px;
  margin: 0;
}

.post-detail-container {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  gap: 40px;
}

/* 左侧帖子内容 */
.post-content {
  flex: 1;
  max-width: 600px;
}

.post-images {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}

.post-images img {
  width: 100%;
  border-radius: 12px;
  object-fit: cover;
}

.carousel-image {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border-radius: 12px;
}

.carousel-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px;
}

.post-info {
  background: #F8F8F8;
  border-radius: 12px;
  padding: 20px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.avatar {
  width: 50px !important;
  height: 50px !important;
  border-radius: 50% !important;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
}

.avatar img {
  width: 100% !important;
  height: 100% !important;
  object-fit: cover !important;
  border-radius: 50% !important;
}

.author-details h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #333;
}

.author-details p {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.post-title {
  font-size: 24px;
  font-weight: bold;
  margin: 0 0 15px 0;
  color: #333;
}

.post-tags {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.tag {
  padding: 4px 12px;
  background: #FFE6E6;
  color: #FF9999;
  border-radius: 15px;
  font-size: 14px;
}

.post-text {
  font-size: 16px;
  line-height: 1.6;
  color: #333;
  margin-bottom: 15px;
}

.post-location {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #999;
  margin-bottom: 20px;
}

.interaction-buttons {
  display: flex;
  gap: 30px;
  padding-top: 20px;
  border-top: 1px solid #E8E8E8;
}

.button-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #666;
}

.button-item:hover {
  color: #FF9999;
}

.button-item .el-icon.active,
.action-item .el-icon.active {
  color: rgb(254, 169, 169);
}

/* 右侧评论区 */
.comment-section {
  width: 400px;
  background: #F8F8F8;
  border-radius: 12px;
  padding: 20px;
  max-height: 80vh;
  overflow-y: auto;
}

.comment-section h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #333;
}

.comment-input {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
}

.comment-input .el-input {
  flex: 1;
}

.comment-input .el-textarea__inner {
  border-radius: 16px;
  border: 2px solid #F8E1E1;
  padding: 15px;
  font-size: 14px;
  line-height: 1.5;
  transition: all 0.3s ease;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
}

.comment-input .el-textarea__inner:focus {
  border-color: #FF9999;
  box-shadow: 0 0 0 3px rgba(255, 153, 153, 0.1);
  outline: none;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 15px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.commenter-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.commenter-name {
  font-weight: bold;
  font-size: 14px;
  color: #333;
}

.author-tag {
  background-color: #FF9999;
  color: white;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 500;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-text {
  font-size: 14px;
  line-height: 1.5;
  color: #333;
  margin-bottom: 10px;
}

.comment-actions {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #999;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.action-item:hover {
  color: #FF9999;
}

.delete-action {
  color: #ff6b6b;
}

.delete-action:hover {
  color: #ff4757;
}

/* 回复输入框 */
.reply-input {
  display: flex;
  gap: 10px;
  margin-top: 10px;
  padding: 10px;
  background: #fff;
  border-radius: 12px;
}

.reply-input input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #E8E8E8;
  border-radius: 8px;
  font-size: 13px;
}

.reply-input input:focus {
  outline: none;
  border-color: #FF9999;
}

.reply-btn {
  padding: 8px 16px;
  background: #FF9999;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
}

.reply-btn:hover {
  background: #ff8080;
}

.cancel-btn {
  padding: 8px 16px;
  background: #fff;
  color: #999;
  border: 1px solid #E8E8E8;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
}

.cancel-btn:hover {
  background: #f8f8f8;
}

/* 子评论列表 */
.replies-list {
  margin-top: 15px;
  padding-left: 15px;
  border-left: 2px solid #E8E8E8;
}

.reply-item {
  display: flex;
  gap: 10px;
  margin-bottom: 12px;
}

.reply-item:last-child {
  margin-bottom: 0;
}

.reply-avatar {
  width: 32px !important;
  height: 32px !important;
  border-radius: 50% !important;
  overflow: hidden;
  flex-shrink: 0;
}

.reply-avatar img {
  width: 100% !important;
  height: 100% !important;
  object-fit: cover !important;
  border-radius: 50% !important;
}

.reply-content {
  flex: 1;
  background: #fff;
  padding: 10px;
  border-radius: 12px;
}

.reply-name {
  font-weight: bold;
  font-size: 12px;
  color: #333;
  margin-right: 8px;
}

.reply-text {
  font-size: 13px;
  color: #666;
}

.reply-time {
  display: block;
  font-size: 11px;
  color: #999;
  margin-top: 5px;
}

.reply-delete {
  float: right;
  color: #999;
  cursor: pointer;
  font-size: 12px;
}

.reply-delete:hover {
  color: #ff4757;
}

.load-more {
  text-align: center;
  margin-top: 20px;
}

.no-more {
  color: #999;
  font-size: 14px;
  padding: 20px 0;
}

.no-comments {
  text-align: center;
  color: #999;
  font-size: 14px;
  padding: 40px 0;
}

/* ========== 图片查看和下载样式 ========== */
.carousel-image {
  cursor: pointer;
  position: relative;
}

.image-hover-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #fff;
  font-size: 14px;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 12px;
}

.carousel-image:hover .image-hover-mask {
  opacity: 1;
}

.download-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 16px;
}

.download-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #FFE6E6, #FFD1DC);
  border: 1px solid #FFC0CB;
  border-radius: 20px;
  color: #FF8888;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.download-btn:hover {
  background: linear-gradient(135deg, #FFD1DC, #FFB6C1);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 153, 153, 0.3);
}

/* 全屏图片预览 */
.image-preview-overlay {
  position: fixed;
  inset: 0;
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-preview-backdrop {
  position: absolute;
  inset: 0;
  background: rgba(10, 5, 8, 0.92);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
}

.image-preview-container {
  position: relative;
  z-index: 2;
  max-width: 90vw;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.image-preview-content {
  max-width: 90vw;
  max-height: 78vh;
  object-fit: contain;
  border-radius: 10px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

.image-preview-toolbar {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.img-toolbar-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 24px;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(12px);
}

.img-toolbar-btn:hover {
  background: rgba(255, 255, 255, 0.22);
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
}

.image-preview-close-btn {
  position: fixed;
  top: 20px;
  right: 24px;
  z-index: 10;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(12px);
  color: #fff;
  font-size: 22px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.image-preview-close-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: scale(1.1) rotate(90deg);
}

.img-preview-fade-enter-active,
.img-preview-fade-leave-active {
  transition: opacity 0.3s ease;
}
.img-preview-fade-enter-from,
.img-preview-fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .post-detail-container {
    flex-direction: column;
  }
  
  .post-content {
    max-width: none;
  }
  
  .comment-section {
    width: 100%;
    max-height: none;
  }
}

@media (max-width: 768px) {
  .post-detail-page {
    padding: 20px;
  }
  
  .post-images img {
    border-radius: 8px;
  }
  
  .post-info {
    padding: 15px;
  }
  
  .comment-section {
    padding: 15px;
  }
}
</style>
