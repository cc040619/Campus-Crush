<template>
  <div class="home-container">
    <!-- 固定背景图片层 -->
    <div class="fixed-background" ref="backgroundRef"></div>
    <!-- Ballpit动态背景 -->
    <div class="ballpit-background" ref="ballpitRef"></div>
    
    <!-- Card Nav导航栏 -->
    <div class="card-nav-container">
      <div class="card-nav">
        <div class="card-nav-header">
            <div class="card-nav-brand" @click="navigateToHome">
              <span class="heart-icon">💕</span>
              <span class="brand-text">记录我们的恋爱瞬间</span>
              <button class="email-icon-btn" @click="showEmailModal = true">
                <span class="email-icon">📧</span>
              </button>
            </div>
          <div class="card-nav-controls">
            <div class="user-info">
              <span class="welcome-text" @click="navigateToPersonal">欢迎，{{ user?.nickname || 'Crush' }}</span>
              <el-button type="danger" size="small" @click="handleLogout" class="logout-btn">
                退出
              </el-button>
            </div>
            <button class="card-nav-menu-btn" @click="toggleMenu" :class="{ 'active': isMenuOpen }">
              <div class="menu-icon-btn">
                <span></span>
              </div>
            </button>
          </div>
        </div>
        
        <!-- 卡片导航 -->
        <div class="card-nav-content" :class="{ 'open': isMenuOpen }">
          <div class="card-nav-cards">
            <!-- 功能入口卡片 -->
            <div class="card-nav-card card-1" :class="{ 'active': activeCard === 0 }" @click="setActiveCard(0)">
              <h3 class="card-title">功能入口</h3>
              <ul class="card-menu">
                <li @click="navigateToStatistics"><span class="menu-icon">🏠</span> 首页统计</li>
                <li @click="router.push('/checkin')"><span class="menu-icon">💝</span> 恋爱打卡</li>
                <li @click="navigateToAnniversary"><span class="menu-icon">⭐</span> 恋爱纪念日</li>
              </ul>
            </div>
            
            <!-- 内容管理卡片 -->
            <div class="card-nav-card card-2" :class="{ 'active': activeCard === 1 }" @click="setActiveCard(1)">
              <h3 class="card-title">内容管理</h3>
              <ul class="card-menu">
                <li @click="navigateToAlbum"><span class="menu-icon">📷</span> 恋爱相册</li>
                <li @click="navigateToDiary"><span class="menu-icon">📝</span> 恋爱日记</li>
                <li @click="navigateToMore"><span class="menu-icon">➕</span> 更多功能</li>
              </ul>
            </div>
            
            <!-- 个人中心卡片 -->
            <div class="card-nav-card card-3" :class="{ 'active': activeCard === 2 }" @click="setActiveCard(2)">
              <h3 class="card-title">个人中心</h3>
              <ul class="card-menu">
                <li @click="navigateToPersonal"><span class="menu-icon">👤</span> 个人主页</li>
                <li @click="navigateToPersonal"><span class="menu-icon">✏️</span> 修改昵称</li>
                <li @click="navigateToPersonal"><span class="menu-icon">📸</span> 上传头像</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <el-container>
      <el-main class="main">
        <!-- 统计数据区域 -->
        <div class="statistics-section">
          <Statistics />
        </div>
        
        <!-- 功能卡片网格容器 - 2行3列布局 -->
        <div class="feature-cards-grid">
          <!-- 首页统计卡片 -->
          <router-link to="/statistics" class="feature-card" style="--delay: 0s;">
            <div class="card-inner">
              <div class="card-icon" style="background: linear-gradient(135deg, #FF9999, #FFB7B2);">
                <el-icon :size="40"><HomeFilled /></el-icon>
              </div>
              <h3 class="card-title">首页统计</h3>
              <p class="card-desc">查看我们的爱情数据</p>
              <div class="card-hearts"></div>
            </div>
          </router-link>

          <!-- 恋爱打卡卡片 -->
          <router-link to="/checkin" class="feature-card" style="--delay: 0.15s;">
            <div class="card-inner">
              <div class="card-icon" style="background: linear-gradient(135deg, #FF9999, #FFB7B2);">
                <el-icon :size="40"><Edit /></el-icon>
              </div>
              <h3 class="card-title">恋爱打卡</h3>
              <p class="card-desc">每日记录我们的故事</p>
              <div class="card-hearts"></div>
            </div>
          </router-link>

          <!-- 恋爱纪念日卡片 -->
          <router-link to="/anniversary" class="feature-card" style="--delay: 0.3s;">
            <div class="card-inner">
              <div class="card-icon" style="background: linear-gradient(135deg, #FF9999, #FFB7B2);">
                <el-icon :size="40"><Star /></el-icon>
              </div>
              <h3 class="card-title">恋爱纪念日</h3>
              <p class="card-desc">重要的日子不忘</p>
              <div class="card-hearts"></div>
            </div>
          </router-link>

          <!-- 恋爱相册卡片 -->
          <router-link to="/album" class="feature-card" style="--delay: 0.45s;">
            <div class="card-inner">
              <div class="card-icon" style="background: linear-gradient(135deg, #FF9999, #FFB7B2);">
                <el-icon :size="40"><Picture /></el-icon>
              </div>
              <h3 class="card-title">恋爱相册</h3>
              <p class="card-desc">珍藏每张照片</p>
              <div class="card-hearts"></div>
            </div>
          </router-link>

          <!-- 恋爱日记卡片 -->
          <router-link to="/diary" class="feature-card" style="--delay: 0.6s;">
            <div class="card-inner">
              <div class="card-icon" style="background: linear-gradient(135deg, #FF9999, #FFB7B2);">
                <el-icon :size="40"><EditPen /></el-icon>
              </div>
              <h3 class="card-title">恋爱日记</h3>
              <p class="card-desc">写下心里话</p>
              <div class="card-hearts"></div>
            </div>
          </router-link>

          <!-- 分享社区卡片 -->
          <router-link to="/community" class="feature-card" style="--delay: 0.75s;">
            <div class="card-inner">
              <div class="card-icon" style="background: linear-gradient(135deg, #FF9999, #FFB7B2);">
                <el-icon :size="40"><ChatLineSquare /></el-icon>
              </div>
              <h3 class="card-title">分享社区</h3>
              <p class="card-desc">分享我们的故事</p>
              <div class="card-hearts"></div>
            </div>
          </router-link>

          <!-- 关于我们卡片 -->
          <router-link to="/about" class="feature-card" style="--delay: 0.9s;">
            <div class="card-inner">
              <div class="card-icon" style="background: linear-gradient(135deg, #FF9999, #FFB7B2);">
                <el-icon :size="40"><UserFilled /></el-icon>
              </div>
              <h3 class="card-title">关于我们</h3>
              <p class="card-desc">了解这个温暖的小站</p>
              <div class="card-hearts"></div>
            </div>
          </router-link>
        </div>
      </el-main>
    </el-container>
    
    <!-- 邮箱设置与提醒弹窗 -->
    <el-dialog
      v-model="showEmailModal"
      width="520px"
      align-center
      :lock-scroll="false"
      :close-on-click-modal="true"
      :close-on-press-escape="true"
      class="email-modal"
    >
      <template #title>
        <div class="modal-title">
          <svg class="modal-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20 4H4C2.9 4 2 4.9 2 6V18C2 19.1 2.9 20 4 20H20C21.1 20 22 19.1 22 18V6C22 4.9 21.1 4 20 4ZM4 6L12 11L20 6H4ZM4 18V8L12 13L20 8V18H4Z" fill="url(#emailGrad)"/>
            <defs><linearGradient id="emailGrad" x1="0%" y1="0%" x2="0%" y2="100%"><stop stop-color="#FF8FA3"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs>
          </svg>
          <span>💕 邮箱订阅与纪念日提醒</span>
        </div>
      </template>
      
      <div class="email-content">
        <div class="email-tabs">
          <div 
            class="tab-item" 
            :class="{ active: activeTab === 'settings' }"
            @click="activeTab = 'settings'"
          >
            <span class="tab-icon">⚙️</span>
            <span>邮箱设置</span>
          </div>
          <div 
            class="tab-item" 
            :class="{ active: activeTab === 'reminders' }"
            @click="activeTab = 'reminders'"
          >
            <span class="tab-icon">🔔</span>
            <span>纪念日提醒</span>
          </div>
        </div>

        <!-- 邮箱设置面板 -->
        <div v-if="activeTab === 'settings'" class="tab-panel">
          <div class="panel-header">
            <h3 class="panel-title">设置接收邮箱</h3>
            <p class="panel-desc">填写邮箱地址，开启纪念日提醒服务</p>
          </div>
          
          <div class="email-form">
            <el-form :model="emailForm" label-width="100px">
              <el-form-item label="邮箱地址">
                <el-input 
                  v-model="emailForm.email" 
                  placeholder="请输入您的邮箱地址" 
                  class="email-input"
                />
              </el-form-item>
              <el-form-item>
                <el-switch 
                  v-model="emailForm.enabled" 
                  active-text="开启提醒" 
                  inactive-text="关闭提醒"
                  active-color="#FF8FA3"
                  inactive-color="#DCDFE6"
                />
              </el-form-item>
            </el-form>
          </div>
          
          <div class="email-info">
            <div class="info-item">
              <svg class="info-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M20 3H4C2.9 3 2 3.9 2 5V19C2 20.1 2.9 21 4 21H20C21.1 21 22 20.1 22 19V5C22 3.9 21.1 3 20 3ZM20 19H4V5H20V19Z" fill="#FF8FA3"/>
                <path d="M8 12L12 16L16 12" stroke="#FFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>当纪念日即将到来时，会发送提醒通知</span>
            </div>
            <div class="info-item">
              <svg class="info-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z" fill="#FF8FA3"/>
              </svg>
              <span>您可以在恋爱纪念日页面设置提前提醒天数</span>
            </div>
          </div>
        </div>

        <!-- 纪念日提醒面板 -->
        <div v-if="activeTab === 'reminders'" class="tab-panel">
          <div class="panel-header">
            <h3 class="panel-title">即将到来的纪念日</h3>
            <p class="panel-desc">以下是您设置了提醒的纪念日</p>
          </div>
          
          <div v-if="reminders.length === 0" class="empty-state">
            <svg class="empty-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M19 4h-1V2h-2v2H8V2H6v2H5c-1.11 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V9h14v11zM9 11H7v2h2v-2zm4 0h-2v2h2v-2zm4 0h-2v2h2v-2z" fill="#FFB6C1"/>
            </svg>
            <p>还没有设置任何纪念日提醒</p>
            <p class="empty-hint">去恋爱纪念日页面设置提醒吧~</p>
          </div>
          
          <div v-else class="reminders-list">
            <div 
              v-for="(reminder, index) in reminders" 
              :key="index" 
              class="reminder-item"
            >
              <div class="reminder-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.28 2 8.5C2 5.42 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.42 22 8.5C22 12.28 18.6 15.36 13.45 20.03L12 21.35Z" fill="url(#reminderHeart)"/>
                  <defs><linearGradient id="reminderHeart" x1="12" y1="3" x2="12" y2="21" gradientUnits="userSpaceOnUse"><stop stop-color="#FF8FA3"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs>
                </svg>
              </div>
              <div class="reminder-content">
                <div class="reminder-name">{{ reminder.name }}</div>
                <div class="reminder-date">{{ reminder.date }}</div>
              </div>
              <div class="reminder-countdown">
                <span class="countdown-number">{{ reminder.daysLeft }}</span>
                <span class="countdown-unit">天后</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="modal-footer">
          <el-button 
            v-if="activeTab === 'reminders'" 
            type="primary" 
            @click="goToAnniversary"
            class="modal-btn"
          >
            纪念日
          </el-button>
          <template v-else>
            <el-button @click="showEmailModal = false" class="modal-btn cancel-btn">取消</el-button>
            <el-button type="primary" @click="saveEmailSettings" class="modal-btn confirm-btn">保存设置</el-button>
          </template>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { gsap } from 'gsap'
import { ElMessage } from 'element-plus'
import Statistics from './Statistics.vue'
import { userApi } from '../api/community'

const router = useRouter()
const user = ref(null)
const ballpitRef = ref(null)
let animationId = null
let backgroundRef = ref(null)

// Card Nav状态
const isMenuOpen = ref(false)
const activeCard = ref(0)

// 邮箱弹窗相关
const showEmailModal = ref(false)
const activeTab = ref('settings')
const emailForm = ref({
  email: '',
  subscription: 'all',
  enabled: false
})
const reminders = ref([])

const getUserHeader = () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  return user.id ? { 'X-User-Id': user.id.toString() } : {}
}

const loadEmailSettings = async () => {
  try {
    const headers = getUserHeader()
    const response = await fetch('/api/email/settings', { headers })
    const data = await response.json()
    if (data.code === 200) {
      emailForm.value = {
        email: data.data.email || '',
        subscription: data.data.subscription || 'all',
        enabled: data.data.enabled || false
      }
    }
  } catch (error) {
    console.error('加载邮箱设置失败:', error)
  }
}

const saveEmailSettings = async () => {
  try {
    const headers = {
      'Content-Type': 'application/json',
      ...getUserHeader()
    }
    const response = await fetch('/api/email/settings', {
      method: 'POST',
      headers,
      body: JSON.stringify(emailForm.value)
    })
    const data = await response.json()
    showEmailModal.value = false
    if (data.code === 200) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(data.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存邮箱设置失败:', error)
    ElMessage.error('保存失败')
  }
}

const loadReminders = async () => {
  try {
    const headers = getUserHeader()
    const response = await fetch('/api/email/reminders', { headers })
    const data = await response.json()
    if (data.code === 200) {
      reminders.value = data.data.reminders || []
    }
  } catch (error) {
    console.error('加载纪念日提醒失败:', error)
  }
}

const goToAnniversary = () => {
  showEmailModal.value = false
  router.push('/anniversary')
}

watch(showEmailModal, (val) => {
  if (val) {
    loadEmailSettings()
    loadReminders()
  }
})

// 弹窗打开时隐藏页面滚动条，关闭时恢复
watch(showEmailModal, (val) => {
  document.documentElement.style.overflow = val ? 'hidden' : ''
  document.body.style.overflow = val ? 'hidden' : ''
})

// Ballpit动态背景配置
const ballpitConfig = {
  count: 50,
  followCursor: false,
  colors: ["#FFC0CB", "#FF99CC", "#FFE6E6"]
}

// 球对象数组
const balls = []

// 初始化球池
const initBallpit = () => {
  const container = ballpitRef.value
  if (!container) return
  
  // 清空容器
  container.innerHTML = ''
  balls.length = 0
  
  // 创建球
  for (let i = 0; i < ballpitConfig.count; i++) {
    const ball = {
      element: document.createElement('div'),
      x: Math.random() * window.innerWidth,
      y: Math.random() * window.innerHeight,
      vx: (Math.random() - 0.5) * 2,
      vy: (Math.random() - 0.5) * 2,
      radius: Math.random() * 20 + 10,
      color: ballpitConfig.colors[Math.floor(Math.random() * ballpitConfig.colors.length)]
    }
    
    // 设置球的样式
    ball.element.style.position = 'absolute'
    ball.element.style.width = ball.radius * 2 + 'px'
    ball.element.style.height = ball.radius * 2 + 'px'
    ball.element.style.borderRadius = '50%'
    ball.element.style.backgroundColor = ball.color
    ball.element.style.opacity = '0.7'
    ball.element.style.pointerEvents = 'none'
    ball.element.style.transform = `translate(${ball.x - ball.radius}px, ${ball.y - ball.radius}px)`
    
    container.appendChild(ball.element)
    balls.push(ball)
  }
  
  // 开始动画
  animate()
}

let lastTime = 0
const targetFrameTime = 16.67

// 动画循环
const animate = (currentTime = 0) => {
  const deltaTime = currentTime - lastTime
  
  if (deltaTime < targetFrameTime / 2) {
    animationId = requestAnimationFrame(animate)
    return
  }
  
  lastTime = currentTime
  
  const timeScale = Math.min(deltaTime / targetFrameTime, 3)
  
  balls.forEach(ball => {
    const speed = 0.5
    const angle = Math.atan2(ball.vy, ball.vx)
    ball.vx = Math.cos(angle) * speed
    ball.vy = Math.sin(angle) * speed
    
    ball.x += ball.vx * timeScale
    ball.y += ball.vy * timeScale
    
    if (ball.x - ball.radius < 0) {
      ball.x = ball.radius
      ball.vx *= -1
    }
    if (ball.x + ball.radius > window.innerWidth) {
      ball.x = window.innerWidth - ball.radius
      ball.vx *= -1
    }
    if (ball.y - ball.radius < 0) {
      ball.y = ball.radius
      ball.vy *= -1
    }
    if (ball.y + ball.radius > window.innerHeight) {
      ball.y = window.innerHeight - ball.radius
      ball.vy *= -1
    }
    
    ball.element.style.transform = `translate(${ball.x - ball.radius}px, ${ball.y - ball.radius}px)`
  })
  
  animationId = requestAnimationFrame(animate)
}

onMounted(() => {
  const userInfo = localStorage.getItem('user')
  if (userInfo) {
    user.value = JSON.parse(userInfo)
  }
  
  // 初始化球池
  initBallpit()
  
  // 监听窗口大小变化
  window.addEventListener('resize', initBallpit)
})

onUnmounted(() => {
  // 清理动画
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  // 清理事件监听器
  window.removeEventListener('resize', initBallpit)
})

const handleLogout = async () => {
  try {
    await userApi.logout()
  } catch (error) {
    console.error('登出失败:', error)
  }
  localStorage.removeItem('user')
  router.push('/login')
}

const navigateToPersonal = () => {
  router.push('/personal')
}

// Card Nav方法
const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
  
  if (isMenuOpen.value) {
    // 展开动画
    gsap.to('.card-nav-content', {
      height: 'auto',
      opacity: 1,
      duration: 0.5,
      ease: 'power3.out'
    })
    
    // 卡片入场动画
    gsap.to('.card-nav-card', {
      y: 0,
      opacity: 1,
      duration: 0.5,
      stagger: 0.1,
      ease: 'back.out(1.7)'
    })
  } else {
    // 收起动画
    gsap.to('.card-nav-content', {
      height: 0,
      opacity: 0,
      duration: 0.3,
      ease: 'power3.in'
    })
  }
}

const setActiveCard = (index) => {
  activeCard.value = index
}

// 导航方法
const navigateToHome = () => {
  router.push('/')
}

const navigateToStatistics = () => {
  router.push('/statistics')
}

const navigateToAnniversary = () => {
  router.push('/anniversary')
}

const navigateToAlbum = () => {
  router.push('/album')
}

const navigateToDiary = () => {
  router.push('/diary')
}

const navigateToMore = () => {
  // 更多功能，暂时无路由
  console.log('更多功能')
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

/* 固定背景图片层 */
.fixed-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: -2;
  pointer-events: none;
  background-image: url('https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/75EF5E4BAC2515E09307119477372502.png');
  background-size: cover;
  background-position: center center;
  background-repeat: no-repeat;
  opacity: 0.9;
}

/* Ballpit动态背景 */
.ballpit-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: -1;
  pointer-events: none;
}



/* ========== Card Nav导航栏 ========== */
.card-nav-container {
  position: relative;
  z-index: 10;
  margin-bottom: 20px;
}

.card-nav {
  background: #FFE6E6;
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(255, 192, 203, 0.3);
  overflow: hidden;
  margin: 0 20px;
}

.card-nav-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  background: #FFE6E6;
  border-bottom: 2px solid rgba(255, 192, 203, 0.3);
}

.email-icon-btn {
  background: linear-gradient(135deg, #FF9999, #FFB7B2);
  border: none;
  border-radius: 50%;
  width: 45px;
  height: 45px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(255, 192, 203, 0.4);
  margin-left: 20px;
  flex-shrink: 0;
}

.email-icon-btn:hover {
  transform: translateY(-3px) scale(1.1);
  box-shadow: 0 8px 20px rgba(255, 192, 203, 0.5);
  background: linear-gradient(135deg, #FFB7B2, #FF9999);
}

.email-icon-btn:active {
  transform: translateY(-1px) scale(0.98);
}

.email-icon {
  font-size: 22px;
}

.card-nav-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.card-nav-brand:hover {
  transform: translateY(-2px);
}

.heart-icon {
  font-size: 24px;
  animation: heartbeat 1.5s ease-in-out infinite;
}

@keyframes heartbeat {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.brand-text {
  font-size: 20px;
  font-weight: bold;
  color: #FF6666;
  text-shadow: 0 2px 4px rgba(255, 192, 203, 0.3);
}

.card-nav-controls {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 14px;
}

.welcome-text {
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 5px 10px;
  border-radius: 20px;
  color: #FF6666;
  font-weight: 500;
}

.welcome-text:hover {
  background-color: rgba(255, 192, 203, 0.3);
  transform: translateY(-2px);
}

.logout-btn {
  background-color: #FF9999;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 6px 16px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(255, 192, 203, 0.3);
}

.logout-btn:hover {
  background-color: #FF6666;
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 6px 16px rgba(255, 192, 203, 0.4);
}

.card-nav-menu-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-nav-menu-btn:hover {
  background-color: rgba(255, 192, 203, 0.3);
}

.card-nav-menu-btn.active {
  background-color: transparent;
}

/* 汉堡菜单图标 */
.menu-icon-btn {
  width: 24px;
  height: 20px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-icon-btn span {
  width: 24px;
  height: 2px;
  background-color: #FF6666;
  position: relative;
  transition: all 0.3s ease;
}

.menu-icon-btn span::before,
.menu-icon-btn span::after {
  content: '';
  position: absolute;
  width: 24px;
  height: 2px;
  background-color: #FF6666;
  transition: all 0.3s ease;
}

.menu-icon-btn span::before {
  top: -8px;
}

.menu-icon-btn span::after {
  top: 8px;
}

.card-nav-menu-btn.active .menu-icon-btn span {
  background-color: transparent;
}

.card-nav-menu-btn.active .menu-icon-btn span::before {
  transform: rotate(45deg);
  top: 0;
}

.card-nav-menu-btn.active .menu-icon-btn span::after {
  transform: rotate(-45deg);
  top: 0;
}



.card-nav-content {
  height: 0;
  overflow: hidden;
  opacity: 0;
  transition: all 0.3s ease;
}

.card-nav-content.open {
  height: auto;
  opacity: 1;
}

.card-nav-cards {
  display: flex;
  gap: 20px;
  padding: 20px 30px;
  flex-wrap: wrap;
}

.card-nav-card {
  flex: 1;
  min-width: 250px;
  border-radius: 15px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  transform: translateY(20px);
  opacity: 0;
  box-shadow: 0 4px 15px rgba(255, 192, 203, 0.2);
}

.card-nav-card.card-1 {
  background: #FFC0CB;
}

.card-nav-card.card-2 {
  background: #FFC0CB;
}

.card-nav-card.card-3 {
  background: #FFC0CB;
}

.card-nav-card:hover {
  transform: translateY(-5px) scale(1.02);
  box-shadow: 0 8px 25px rgba(255, 192, 203, 0.4);
}

.card-nav-card.active {
  transform: translateY(-5px) scale(1.02);
  box-shadow: 0 8px 25px rgba(255, 192, 203, 0.4);
  border: 2px solid #FF6666;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.card-menu {
  list-style: none;
  padding: 0;
  margin: 0;
}

.card-menu li {
  padding: 10px 15px;
  margin-bottom: 8px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 10px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  font-weight: 500;
}

.card-menu li:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateX(5px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.menu-icon {
  font-size: 16px;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .card-nav {
    margin: 0 10px;
  }
  
  .card-nav-header {
    padding: 10px 20px;
  }
  
  .brand-text {
    font-size: 16px;
  }
  
  .card-nav-cards {
    flex-direction: column;
    padding: 15px 20px;
  }
  
  .card-nav-card {
    min-width: 100%;
  }
  
  .user-info span {
    display: none;
  }
}

/* ========== 主内容区 ========== */
.main {
  padding: 40px 40px 60px;
  position: relative;
  z-index: 1;
  min-height: calc(100vh - 60px);
}

/* 统计数据区域 */
.statistics-section {
  max-width: 1200px;
  margin: 0 auto 50px;
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ========== 功能卡片网格布局 - 2行4列 ========== */
.feature-cards-grid {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  grid-template-rows: repeat(2, auto);
  gap: 25px;
  padding: 20px;
  position: relative;
}

/* ========== 功能卡片基础样式 ========== */
.feature-card {
  text-decoration: none;
  display: block;
  position: relative;
  animation: cardEntrance 0.8s cubic-bezier(0.34, 1.56, 0.64, 1) var(--delay) both;
  aspect-ratio: 4/5;
  display: flex;
  align-items: stretch;
}

/* 卡片内部容器 */
.card-inner {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 2px solid rgba(255, 192, 203, 0.8);
  border-radius: 20px;
  padding: 30px 20px;
  text-align: center;
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 
    0 8px 32px rgba(255, 192, 203, 0.3),
    0 2px 8px rgba(0, 0, 0, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 15px;
}

/* 卡片悬停效果 */
.feature-card:hover .card-inner {
  transform: translateY(-8px) scale(1.03);
  background: rgba(255, 245, 247, 0.9);
  border-color: rgba(255, 192, 203, 0.6);
  box-shadow: 
    0 20px 60px rgba(255, 192, 203, 0.4),
    0 8px 24px rgba(255, 153, 153, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 1);
  animation: breathe 2s ease-in-out infinite;
}

/* 呼吸动画 */
@keyframes breathe {
  0%, 100% {
    transform: translateY(-8px) scale(1.03);
  }
  50% {
    transform: translateY(-8px) scale(1.05);
  }
}

/* 卡片点击效果 */
.feature-card:active .card-inner {
  transform: translateY(-4px) scale(0.98);
  transition-duration: 0.1s;
  animation: none;
}

/* ========== 卡片爱心旋转特效 ========== */
.card-hearts {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 1;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.feature-card:hover .card-hearts {
  opacity: 1;
  animation: heartsRotate 10s linear infinite;
}

/* 爱心旋转动画 */
@keyframes heartsRotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 爱心生成 */
.card-hearts::before,
.card-hearts::after {
  content: '💕';
  position: absolute;
  font-size: 16px;
  color: #FFC0CB;
  opacity: 0.7;
  animation: heartFloat 3s ease-in-out infinite;
}

/* 生成多个爱心 */
.card-hearts::before {
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.card-hearts::after {
  top: 10%;
  right: 10%;
  animation-delay: 0.5s;
}

/* 更多爱心 */
.card-hearts {
  counter-reset: heart;
}

.card-hearts::before,
.card-hearts::after,
.card-hearts:nth-child(n)::before,
.card-hearts:nth-child(n)::after {
  counter-increment: heart;
}

/* 爱心浮动动画 */
@keyframes heartFloat {
  0%, 100% {
    transform: translateY(0) scale(1);
    opacity: 0.7;
  }
  50% {
    transform: translateY(-10px) scale(1.2);
    opacity: 1;
  }
}

/* 额外的爱心 */
.card-inner:hover::before,
.card-inner:hover::after {
  content: '💕';
  position: absolute;
  font-size: 14px;
  color: #FFC0CB;
  opacity: 0.6;
  animation: heartFloat 2.5s ease-in-out infinite;
  pointer-events: none;
  z-index: 1;
}

.card-inner:hover::before {
  top: 20%;
  left: 5%;
  animation-delay: 1s;
}

.card-inner:hover::after {
  top: 20%;
  right: 5%;
  animation-delay: 1.5s;
}

/* 底部爱心 */
.card-inner:hover {
  position: relative;
}

.card-inner:hover::before,
.card-inner:hover::after {
  content: '💕';
  position: absolute;
  font-size: 12px;
  color: #FFC0CB;
  opacity: 0.5;
  animation: heartFloat 3s ease-in-out infinite;
  pointer-events: none;
  z-index: 1;
}

.card-inner:hover::before {
  bottom: 10%;
  left: 15%;
  animation-delay: 2s;
}

.card-inner:hover::after {
  bottom: 10%;
  right: 15%;
  animation-delay: 2.5s;
}

/* ========== 卡片图标 ========== */
.card-icon {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  color: white;
  box-shadow: 0 8px 24px rgba(255, 192, 203, 0.3);
  transition: all 0.4s ease;
}

.feature-card:hover .card-icon {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 12px 32px rgba(255, 153, 153, 0.4);
}

/* 占位卡片样式 */
.placeholder-card .card-inner {
  background: rgba(255, 245, 247, 0.5);
  border: 2px dashed rgba(255, 192, 203, 0.4);
  cursor: default;
}

.placeholder-icon {
  background: linear-gradient(135deg, #FF9999, #FFB7B2);
  color: #FF9999;
}

.placeholder-card .card-title {
  color: #FFB7B2;
}

.placeholder-card .card-desc {
  color: #CC9999;
}

/* ========== 卡片标题和描述 ========== */
.card-title {
  color: #FF9999;
  font-size: 22px;
  font-weight: 600;
  margin: 0 0 10px;
  transition: all 0.3s ease;
}

.feature-card:hover .card-title {
  color: #FF8080;
  transform: translateY(-2px);
}

.card-desc {
  color: #999;
  font-size: 14px;
  margin: 0;
  transition: all 0.3s ease;
}

.feature-card:hover .card-desc {
  color: #666;
}

/* ========== 入场动画 ========== */
@keyframes cardEntrance {
  0% {
    opacity: 0;
    transform: translateY(50px);
  }
  60% {
    transform: translateY(-10px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ========== 响应式适配 ========== */
@media (min-width: 1200px) {
  .feature-cards-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 1199px) and (min-width: 992px) {
  .feature-cards-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 25px;
  }
}

@media (max-width: 991px) and (min-width: 768px) {
  .feature-cards-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 25px;
  }
}

@media (max-width: 768px) {
  .main {
    padding: 40px 20px;
  }

  .feature-cards-grid {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 10px;
  }

  .feature-card {
    aspect-ratio: 16/9;
  }

  .card-inner {
    padding: 30px 20px;
  }

  .card-icon {
    width: 80px;
    height: 80px;
  }

  .card-title {
    font-size: 20px;
  }
}

@media (max-width: 480px) {
  .header-content h1 {
    font-size: 18px;
  }

  .user-info span {
    display: none;
  }
  
  .feature-card {
    aspect-ratio: 16/10;
  }

  .email-icon-btn {
    width: 38px;
    height: 38px;
    margin-right: 10px;
  }

  .email-icon {
    font-size: 18px;
  }
}

/* ========== 邮箱弹窗样式 ========== */
.email-modal {
  border-radius: 20px;
  overflow: hidden;
}

:deep(.email-modal .el-dialog) {
  margin: 0;
  width: 520px !important;
}

:deep(.email-modal .el-dialog__body) {
  padding: 0;
  background: rgba(255, 245, 247, 0.95);
  max-height: 70vh;
  overflow-y: auto;
}

:deep(.email-modal .el-dialog__body)::-webkit-scrollbar {
  display: none;
}

:deep(.email-modal .el-dialog__header) {
  background: linear-gradient(135deg, #FFB7B2, #FFB7B2);
  padding: 18px 25px;
  border-bottom: 2px solid rgba(255, 143, 163, 0.3);
}

:deep(.email-modal .el-dialog__headerbtn) {
  color: #FF8FA3;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
}

:deep(.email-modal .el-dialog__headerbtn:hover) {
  background: rgba(255, 143, 163, 0.2);
}

.modal-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: bold;
  color: #999;
}

.modal-icon {
  width: 24px;
  height: 24px;
}

.email-content {
  padding: 0;
}

.email-tabs {
  display: flex;
  border-bottom: 2px solid #FFE6E6;
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 15px;
  cursor: pointer;
  color: #999;
  font-size: 14px;
  transition: all 0.3s ease;
  border-bottom: 2px solid transparent;
}

.tab-item:hover {
  color: #FF8FA3;
}

.tab-item.active {
  color: #FF8FA3;
  border-bottom-color: #FF8FA3;
  background: rgba(255, 143, 163, 0.1);
}

.tab-icon {
  font-size: 16px;
}

.tab-panel {
  padding: 25px;
}

.panel-header {
  text-align: center;
  margin-bottom: 25px;
}

.panel-title {
  font-size: 20px;
  font-weight: bold;
  color: #FF8FA3;
  margin: 0 0 8px;
}

.panel-desc {
  color: #999;
  font-size: 14px;
  margin: 0;
}

.email-form {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 15px rgba(255, 143, 163, 0.15);
  margin-bottom: 20px;
}

:deep(.email-modal .el-form-item) {
  margin-bottom: 18px;
}

:deep(.email-modal .el-form-item__label) {
  color: #FF8FA3;
  font-weight: 500;
  font-size: 14px;
}

:deep(.email-modal .el-input__wrapper) {
  border-radius: 10px;
  border: 2px solid #FFE4E9;
  transition: all 0.3s ease;
}

:deep(.email-modal .el-input__wrapper:hover) {
  border-color: #FFB6C1;
}

:deep(.email-modal .el-input__wrapper.is-focus) {
  border-color: #FF8FA3;
  box-shadow: 0 0 0 2px rgba(255, 143, 163, 0.2);
}

.email-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #888;
  font-size: 13px;
}

.info-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.empty-state {
  text-align: center;
  padding: 30px 0;
}

.empty-icon {
  width: 64px;
  height: 64px;
  margin-bottom: 15px;
}

.empty-state p {
  color: #999;
  margin: 0 0 8px;
}

.empty-hint {
  color: #FFB6C1 !important;
  font-size: 13px !important;
}

.reminders-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.reminder-item {
  display: flex;
  align-items: center;
  gap: 15px;
  background: white;
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0 2px 10px rgba(255, 143, 163, 0.1);
  transition: all 0.3s ease;
}

.reminder-item:hover {
  transform: translateX(5px);
  box-shadow: 0 4px 15px rgba(255, 143, 163, 0.2);
}

.reminder-icon {
  width: 48px;
  height: 48px;
  flex-shrink: 0;
}

.reminder-content {
  flex: 1;
}

.reminder-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.reminder-date {
  font-size: 12px;
  color: #999;
}

.reminder-countdown {
  text-align: center;
}

.countdown-number {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #FF8FA3;
}

.countdown-unit {
  font-size: 12px;
  color: #999;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.modal-btn {
  padding: 10px 24px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.modal-btn.cancel-btn {
  color: #999;
  border: 1px solid #E8E8E8;
}

.modal-btn.cancel-btn:hover {
  background: #F8F8F8;
}

.modal-btn.confirm-btn {
  background: linear-gradient(135deg, #FF8FA3, #FFB6C1);
  border: none;
  color: white;
}

.modal-btn.confirm-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(255, 143, 163, 0.4);
}

:deep(.email-modal .el-select__wrapper:hover) {
  border-color: #FFC0CB;
}

:deep(.email-modal .el-select__wrapper.is-focus) {
  border-color: #FF9999;
  box-shadow: 0 0 0 2px rgba(255, 153, 153, 0.2);
}

:deep(.email-modal .el-switch__core) {
  background: #FFE6E6;
  border-color: #FFC0CB;
}

:deep(.email-modal .el-switch__core.is-checked) {
  background: linear-gradient(135deg, #FF9999, #FF6666);
  border-color: #FF6666;
}

:deep(.email-modal .el-switch__label) {
  color: #FF9999;
}

.email-info {
  display: flex;
  justify-content: center;
  gap: 30px;
  flex-wrap: wrap;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 13px;
}

.info-icon {
  font-size: 18px;
}

:deep(.email-modal .el-dialog__footer) {
  background: rgba(255, 245, 247, 0.8);
  padding: 20px 30px;
  border-top: 2px solid rgba(255, 192, 203, 0.3);
  display: flex;
  justify-content: flex-end;
  gap: 15px;
}

.modal-btn {
  padding: 12px 30px;
  border-radius: 30px;
  font-weight: 600;
  transition: all 0.3s ease;
  border: none;
}

.modal-btn.cancel-btn {
  background: #F5F5F5;
  color: #666;
}

.modal-btn.cancel-btn:hover {
  background: #E8E8E8;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.modal-btn.confirm-btn {
  background: linear-gradient(135deg, #FF9999, #FF6666);
  color: white;
  box-shadow: 0 4px 12px rgba(255, 153, 153, 0.4);
}

.modal-btn.confirm-btn:hover {
  background: linear-gradient(135deg, #FF8080, #FF5050);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 153, 153, 0.5);
}

.modal-btn.confirm-btn:active {
  transform: translateY(-1px);
}
</style>