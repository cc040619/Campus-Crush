<template>
  <div class="topic-ranking-page">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h2>{{ currentTopic ? '话题排行榜' : '热门话题' }}</h2>
      </div>
    </div>

    <div class="topic-container">
      <!-- 热门话题列表 -->
      <div v-if="!currentTopic" class="topics-sidebar">
        <h3 class="sidebar-title">热门话题</h3>
        <div 
          v-for="(topic, index) in hotTopics" 
          :key="index"
          class="topic-item"
          :class="{ active: selectedTopic === topic.name }"
          @click="selectTopic(topic.name, topic.count)"
        >
          <span class="topic-rank">{{ index + 1 }}</span>
          <span class="topic-name">{{ topic.name }}</span>
          <span class="topic-count">{{ topic.count }} 讨论</span>
        </div>
      </div>

      <!-- 帖子排行榜 -->
      <div class="posts-content">
        <div v-if="currentTopic" class="topic-header">
          <h3 class="topic-title"># {{ currentTopic }}</h3>
          <p class="topic-subtitle">共 {{ postList.length }} 条相关帖子</p>
        </div>
        <div v-else class="default-tip">
          <el-icon size="48" class="tip-icon"><Folder /></el-icon>
          <p>请选择一个热门话题查看相关帖子排行榜</p>
        </div>

        <div v-if="currentTopic && postList.length === 0" class="no-posts">
          暂无相关帖子
        </div>

        <div v-if="currentTopic && postList.length > 0" class="posts-list">
          <div 
            v-for="(post, index) in postList" 
            :key="post.id"
            class="post-item"
            @click="goToPostDetail(post.id)"
          >
            <div class="post-rank" :class="getRankClass(index)">
              {{ index + 1 }}
            </div>
            <div class="post-content">
              <h4 class="post-title">{{ post.title }}</h4>
              <p class="post-preview">{{ post.content }}</p>
              <div class="post-meta">
                <span class="meta-item">
                  <el-icon><Message /></el-icon>
                  {{ post.commentCount || 0 }}
                </span>
                <span class="meta-item">
                  <el-icon><Star /></el-icon>
                  {{ post.likeCount || 0 }}
                </span>
                <span class="meta-item">
                  <el-icon><VideoCamera /></el-icon>
                  {{ post.browseCount || 0 }}
                </span>
              </div>
            </div>
            <div class="post-image" v-if="post.images">
              <img :src="getFirstImage(post.images)" alt="帖子图片" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, Message, Star, VideoCamera, Folder } from '@element-plus/icons-vue'
import { communityApi } from '../api/community'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const hotTopics = ref([])
const selectedTopic = ref('')
const currentTopic = ref('')
const topicCount = ref(0)
const postList = ref([])

const goBack = () => {
  const fromPath = sessionStorage.getItem('fromTopicRanking')
  if (fromPath && fromPath.startsWith('/community')) {
    sessionStorage.removeItem('fromTopicRanking')
    router.push('/community')
  } else {
    router.back()
  }
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

const getRankClass = (index) => {
  if (index === 0) return 'rank-gold'
  if (index === 1) return 'rank-silver'
  if (index === 2) return 'rank-bronze'
  return ''
}

const loadHotTopics = async () => {
  try {
    const response = await communityApi.getHotTopics()
    if (response.code === 200) {
      hotTopics.value = response.data.slice(0, 10)
      
      if (route.params.topic) {
        selectTopic(route.params.topic, 0)
      } else if (hotTopics.value.length > 0) {
        selectTopic(hotTopics.value[0].name, hotTopics.value[0].count)
      }
    } else {
      ElMessage.error(response.msg || '获取热门话题失败')
    }
  } catch (error) {
    console.error('获取热门话题失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

const selectTopic = async (topicName, count) => {
  selectedTopic.value = topicName
  currentTopic.value = topicName
  topicCount.value = count
  
  await loadTopicPosts(topicName)
}

const loadTopicPosts = async (keyword) => {
  try {
    const response = await communityApi.searchPostsByKeyword(keyword)
    if (response.code === 200) {
      postList.value = response.data || []
    } else {
      ElMessage.error(response.msg || '获取帖子失败')
    }
  } catch (error) {
    console.error('获取帖子失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

const goToPostDetail = (postId) => {
  sessionStorage.setItem('fromPath', `/community/topic/${encodeURIComponent(currentTopic.value)}`)
  router.push(`/community/post/${postId}`)
}

onMounted(() => {
  loadHotTopics()
})
</script>

<style scoped>
.topic-ranking-page {
  min-height: 100vh;
  background: #F5F5F5;
}

.page-header {
  background: #fff;
  padding: 15px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.back-btn {
  padding: 8px 15px;
  font-size: 14px;
}

.header-left h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.topic-container {
  display: flex;
  gap: 30px;
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.topics-sidebar {
  width: 280px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}

.sidebar-title {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.topic-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 8px;
}

.topic-item:hover {
  background: #F8F8F8;
}

.topic-item.active {
  background: #FFF0F0;
}

.topic-rank {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F0F0F0;
  border-radius: 50%;
  font-size: 12px;
  font-weight: 600;
  color: #999;
}

.topic-name {
  flex: 1;
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.topic-count {
  font-size: 12px;
  color: #999;
}

.posts-content {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  min-height: 400px;
}

.topic-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #F0F0F0;
}

.topic-title {
  margin: 0 0 5px 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.topic-subtitle {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.default-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  color: #999;
}

.tip-icon {
  margin-bottom: 15px;
  color: #DDD;
}

.no-posts {
  text-align: center;
  padding: 60px 0;
  color: #999;
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.post-item {
  display: flex;
  gap: 15px;
  padding: 20px;
  background: #FAFAFA;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.post-item:hover {
  background: #F0F0F0;
  transform: translateX(5px);
}

.post-rank {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #F0F0F0;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 700;
  color: #999;
  flex-shrink: 0;
}

.post-rank.rank-gold {
  background: linear-gradient(135deg, #FFD700, #FFA500);
  color: #fff;
}

.post-rank.rank-silver {
  background: linear-gradient(135deg, #C0C0C0, #A8A8A8);
  color: #fff;
}

.post-rank.rank-bronze {
  background: linear-gradient(135deg, #CD7F32, #B87333);
  color: #fff;
}

.post-content {
  flex: 1;
  min-width: 0;
}

.post-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.post-preview {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-meta {
  display: flex;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #999;
}

.post-image {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.post-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

@media (max-width: 768px) {
  .topic-container {
    flex-direction: column;
    padding: 15px;
  }
  
  .topics-sidebar {
    width: 100%;
  }
  
  .post-item {
    flex-direction: column;
  }
  
  .post-image {
    width: 100%;
    height: 150px;
  }
}
</style>