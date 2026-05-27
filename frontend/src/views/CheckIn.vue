<template>
  <div class="checkin-page">
    <!-- ==================== 左侧导航栏 ==================== -->
    <aside class="left-sidebar">
      <div class="sidebar-header">
        <svg class="logo-heart" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.28 2 8.5C2 5.42 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.42 22 8.5C22 12.28 18.6 15.36 13.45 20.03L12 21.35Z" fill="url(#sidebarHeartGrad)"/>
          <defs><linearGradient id="sidebarHeartGrad" x1="12" y1="3" x2="12" y2="21" gradientUnits="userSpaceOnUse"><stop stop-color="#FF8FA3"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs>
        </svg>
        <span class="sidebar-title">恋爱记录</span>
      </div>

      <nav class="sidebar-nav">
        <a
          v-for="item in navItems"
          :key="item.key"
          :class="['nav-item', { active: activeNav === item.key }]"
          @click="handleNavClick(item.key)"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </a>
      </nav>

      <div class="sidebar-footer-card">
        <div class="footer-card-inner">
          <svg class="footer-heart" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M24 44.35L21.1 40.06C10.8 30.72 4 24.56 4 17C4 10.84 9.84 6 16 6C19.52 6 22.68 7.88 24 10.16C25.32 7.88 28.48 6 32 6C38.16 6 44 10.84 44 17C44 24.56 37.2 30.72 26.9 40.06L24 44.35Z" fill="url(#footerHeartGrad)"/>
            <defs><linearGradient id="footerHeartGrad" x1="24" y1="6" x2="24" y2="44" gradientUnits="userSpaceOnUse"><stop stop-color="#FF8FA3"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs>
          </svg>
          <p class="footer-card-title">我们的故事</p>
          <p class="footer-card-desc">每一天都值得被记录</p>
        </div>
      </div>
    </aside>

    <!-- ==================== 中间主内容区 ==================== -->
    <main class="main-content">
      <!-- 未绑定提示 -->
      <div v-if="!hasCouple" class="no-couple-card">
        <svg class="no-couple-icon" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M24 42.7L21.1 38.4C10.8 29.1 4 22.9 4 15.5C4 9.3 9.8 4.5 16 4.5C19.5 4.5 22.7 6.3 24 8.6C25.3 6.3 28.5 4.5 32 4.5C38.2 4.5 44 9.3 44 15.5C44 22.9 37.2 29.1 26.9 38.4L24 42.7Z" fill="#FFE4E9" stroke="#FFB6C1" stroke-width="1.5"/>
        </svg>
        <p class="no-couple-title">尚未绑定情侣关系</p>
        <p class="no-couple-desc">打卡功能需要先绑定情侣关系，才能记录你们的甜蜜日常</p>
        <button class="no-couple-btn" @click="router.push('/settings')">去绑定</button>
      </div>

      <!-- 已绑定：顶部情侣信息栏 -->
      <section v-else class="couple-info-bar">
        <div class="couple-avatars">
          <div class="avatar-wrapper">
            <img :src="coupleData.userAvatar || 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'" alt="我" class="avatar-img" />
          </div>
          <div class="avatars-heart">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.28 2 8.5C2 5.42 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.42 22 8.5C22 12.28 18.6 15.36 13.45 20.03L12 21.35Z" fill="url(#avatarsHeartGrad)"/>
              <defs><linearGradient id="avatarsHeartGrad" x1="12" y1="3" x2="12" y2="21" gradientUnits="userSpaceOnUse"><stop stop-color="#FF8FA3"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs>
            </svg>
          </div>
          <div class="avatar-wrapper">
            <img :src="coupleData.partnerAvatar || 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'" alt="伴侣" class="avatar-img" />
          </div>
        </div>
        <div class="couple-names">{{ coupleData.userName || '我' }} & {{ coupleData.partnerName || 'TA' }}</div>
        <div class="couple-days">
          <span class="days-number">{{ coupleData.daysTogether }}</span>
          <span class="days-unit">天</span>
        </div>
        <div class="couple-date-info">{{ coupleData.startDate }} 我们在一起</div>
        <button class="checkin-btn-main" @click="showCheckinDialog = true">
          <svg class="btn-heart-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.28 2 8.5C2 5.42 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.42 22 8.5C22 12.28 18.6 15.36 13.45 20.03L12 21.35Z" fill="#FFF"/>
          </svg>
          立即打卡
        </button>
      </section>

      <!-- 中部功能模块 -->
      <section v-if="hasCouple" class="feature-cards">
        <!-- 本周打卡日历 -->
        <div class="card weekly-checkin-card">
          <h3 class="card-title">
            <el-icon><Calendar /></el-icon>
            本周打卡
          </h3>
          <div class="week-calendar">
            <div
              v-for="(day, index) in weekDays"
              :key="index"
              :class="['week-day-item', { checked: day.checked, today: day.isToday }]"
            >
              <span class="day-label">{{ day.label }}</span>
              <span class="day-num">{{ day.num }}</span>
            </div>
          </div>
          <div class="week-summary">本周已打卡 <strong>{{ weeklyCheckedCount }}</strong> 天</div>
        </div>

        <!-- 愿望清单进度 -->
        <div class="card wishlist-card">
          <h3 class="card-title">
            <svg class="wish-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <polygon points="12,2 15,9 22,9 16,14 18,22 12,17 6,22 8,14 2,9 9,9" fill="url(#wishStarGrad)"/>
              <defs><linearGradient id="wishStarGrad" x1="12" y1="2" x2="12" y2="22" gradientUnits="userSpaceOnUse"><stop stop-color="#FFD700"/><stop offset="1" stop-color="#FFEC8B"/></linearGradient></defs>
            </svg>
            愿望清单进度
          </h3>
          <div class="wishlist-stats">
            <span class="wishlist-text">已完成 <strong>{{ wishlistData.completed }}</strong> / {{ wishlistData.total }}</span>
            <span class="wishlist-percent">{{ wishlistData.percent }}%</span>
          </div>
          <div class="progress-bar-wrapper">
            <div class="progress-bar" :style="{ width: wishlistData.percent + '%' }"></div>
          </div>
          <button class="wishlist-btn" @click="router.push('/wishlist')">去看看</button>
        </div>
      </section>

      <!-- 打卡记录时间轴 -->
      <section v-if="hasCouple" class="timeline-section">
        <h3 class="section-title">
          <el-icon><Tickets /></el-icon>
          打卡记录
        </h3>
        <div class="timeline-list">
          <div v-for="(record, index) in checkinRecords" :key="index" class="timeline-item">
            <!-- 时间轴竖线 -->
            <div class="timeline-line">
              <div class="timeline-dot"></div>
              <div v-if="index < checkinRecords.length - 1" class="timeline-bar"></div>
            </div>
            <!-- 记录卡片 -->
            <div class="timeline-card">
              <div class="record-header">
                <div class="record-user">
                  <img :src="record.avatar" class="record-avatar" />
                  <span class="record-nickname">{{ record.nickname }}</span>
                </div>
                <span class="record-time">{{ record.timeText }}</span>
              </div>
              <p class="record-content">{{ record.content }}</p>
              <div v-if="record.images && record.images.length" class="record-images">
                <div v-for="(img, i) in record.images" :key="i" class="record-img-wrapper">
                  <img
                    v-if="typeof img === 'string' || img instanceof String"
                    :src="img"
                    class="record-img-real"
                    @click="previewImage(img)"
                  />
                  <div v-else class="record-img-placeholder" :style="{ background: img.color || '#FFE4E9' }">
                    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <rect x="3" y="3" width="18" height="18" rx="2" stroke="#FFF" stroke-width="1.5"/>
                      <circle cx="8.5" cy="8.5" r="1.5" fill="#FFF"/>
                      <path d="M3 16l5-5 4 4 3-3 6 6" stroke="#FFF" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                  </div>
                </div>
              </div>
              <div class="record-actions">
                <span class="action-item" @click="handleLike(record)">
                  <svg :class="['action-icon', { liked: record.liked }]" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.28 2 8.5C2 5.42 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.42 22 8.5C22 12.28 18.6 15.36 13.45 20.03L12 21.35Z" :fill="record.liked ? '#FF8FA3' : 'none'" stroke="#FF9999" stroke-width="1.5"/>
                  </svg>
                  {{ record.likeCount }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>

    <!-- ==================== 右侧信息卡片栏 ==================== -->
    <aside class="right-sidebar">
      <!-- 我们的纪念日 -->
      <div class="side-card anniversaries-card">
        <div class="side-card-header">
          <h4>
            <svg class="side-card-title-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect x="3" y="4" width="18" height="18" rx="2" stroke="#FF9999" stroke-width="1.5"/>
              <line x1="3" y1="10" x2="21" y2="10" stroke="#FF9999" stroke-width="1.5"/>
              <line x1="8" y1="2" x2="8" y2="6" stroke="#FF9999" stroke-width="1.5"/>
              <line x1="16" y1="2" x2="16" y2="6" stroke="#FF9999" stroke-width="1.5"/>
            </svg>
            我们的纪念日
          </h4>
          <a class="view-all" @click="router.push('/anniversary')">全部 &gt;</a>
        </div>
        <div class="anniversary-list">
          <div v-for="(item, index) in upcomingAnniversaries" :key="index" class="anniversary-item">
            <div class="anniversary-dot" :class="item.type"></div>
            <div class="anniversary-info">
              <span class="anniversary-name">{{ item.name }}</span>
              <span class="anniversary-date">{{ item.date }}</span>
            </div>
            <span class="anniversary-days">还有 <strong>{{ item.daysLeft }}</strong> 天</span>
          </div>
        </div>
      </div>

      <!-- 今日天气 -->
      <div class="side-card weather-card">
        <div class="side-card-header">
          <h4>
            <svg class="side-card-title-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="4" fill="#FFB6C1" stroke="#FF9999" stroke-width="1"/>
              <line x1="12" y1="2" x2="12" y2="5" stroke="#FFB6C1" stroke-width="2" stroke-linecap="round"/>
              <line x1="12" y1="19" x2="12" y2="22" stroke="#FFB6C1" stroke-width="2" stroke-linecap="round"/>
              <line x1="2" y1="12" x2="5" y2="12" stroke="#FFB6C1" stroke-width="2" stroke-linecap="round"/>
              <line x1="19" y1="12" x2="22" y2="12" stroke="#FFB6C1" stroke-width="2" stroke-linecap="round"/>
            </svg>
            今日天气
          </h4>
        </div>
        <div class="weather-content">
          <div class="weather-main">
            <svg class="weather-icon-svg" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="20" cy="22" r="10" fill="#FFD700" opacity="0.8"/>
              <line x1="10" y1="38" x2="37" y2="38" stroke="#FFB6C1" stroke-width="4" stroke-linecap="round"/>
              <circle cx="24" cy="36" r="10" fill="#FFF" stroke="#FFE4E9" stroke-width="2"/>
              <circle cx="30" cy="36" r="8" fill="#FFF" stroke="#FFE4E9" stroke-width="2"/>
            </svg>
            <div class="weather-info">
              <span class="weather-temp">{{ weatherData.temp }}°</span>
              <span class="weather-desc">{{ weatherData.desc }}</span>
            </div>
          </div>
          <p class="weather-tip">{{ weatherData.tip }}</p>
        </div>
      </div>

      <!-- 互相陪伴的时光 -->
      <div class="side-card companion-card">
        <div class="companion-illustration">
          <svg viewBox="0 0 180 120" fill="none" xmlns="http://www.w3.org/2000/svg">
            <!-- 男孩 -->
            <circle cx="55" cy="30" r="14" fill="#FFC0CB" stroke="#FFB6C1" stroke-width="1.5"/>
            <rect x="42" y="44" width="26" height="30" rx="4" fill="#FFB6C1"/>
            <rect x="36" y="72" width="8" height="24" rx="3" fill="#FFB6C1"/>
            <rect x="66" y="72" width="8" height="24" rx="3" fill="#FFB6C1"/>
            <!-- 女孩 -->
            <circle cx="125" cy="30" r="14" fill="#FFE4E9" stroke="#FFC0CB" stroke-width="1.5"/>
            <rect x="112" y="44" width="26" height="30" rx="4" fill="#FFC0CB"/>
            <rect x="106" y="72" width="8" height="24" rx="3" fill="#FFC0CB"/>
            <rect x="136" y="72" width="8" height="24" rx="3" fill="#FFC0CB"/>
            <!-- 中间爱心 -->
            <path d="M90 55L87 50C77 42 72 38 72 33C72 29 75 27 78 27C80 27 82 28 83 30L90 40L97 30C98 28 100 27 102 27C105 27 108 29 108 33C108 38 103 42 93 50L90 55Z" fill="#FF8FA3" opacity="0.9"/>
            <!-- 围巾 -->
            <rect x="107" y="61" width="34" height="4" rx="2" fill="#FF9999"/>
          </svg>
        </div>
        <p class="companion-text">每一天都因为有你</p>
        <p class="companion-text">变得更有意义</p>
        <svg class="companion-heart" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.28 2 8.5C2 5.42 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.42 22 8.5C22 12.28 18.6 15.36 13.45 20.03L12 21.35Z" fill="#FF8FA3"/>
        </svg>
      </div>
    </aside>

    <!-- ==================== 打卡弹窗 ==================== -->
    <el-dialog
      v-model="showCheckinDialog"
      width="520px"
      :align-center="true"
      custom-class="checkin-dialog"
    >
      <template #title>
        <div class="dialog-title-row">
          <svg class="dialog-title-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.28 2 8.5C2 5.42 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.42 22 8.5C22 12.28 18.6 15.36 13.45 20.03L12 21.35Z" fill="url(#dialogHeartGrad)"/>
            <defs><linearGradient id="dialogHeartGrad" x1="12" y1="3" x2="12" y2="21" gradientUnits="userSpaceOnUse"><stop stop-color="#FF8FA3"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs>
          </svg>
          <span>今日打卡</span>
        </div>
      </template>
      <div class="dialog-body">
        <div class="dialog-date-row">
          <el-icon><Calendar /></el-icon>
          <span>{{ todayStr }}</span>
        </div>
        <el-input
          v-model="checkinForm.content"
          type="textarea"
          :rows="5"
          placeholder="记录今天的心情和故事..."
          class="checkin-textarea"
        />
        <div class="dialog-image-upload">
          <div class="upload-images-preview" v-if="checkinImages.length > 0">
            <div v-for="(img, i) in checkinImages" :key="i" class="upload-preview-item">
              <img :src="img" class="upload-preview-img" />
              <el-icon class="upload-remove-icon" @click="checkinImages.splice(i, 1)"><Close /></el-icon>
            </div>
          </div>
          <div class="upload-placeholder" @click="triggerUpload" v-if="checkinImages.length < 9">
            <el-icon v-if="!uploading"><Picture /></el-icon>
            <el-icon v-else class="uploading-spin"><Loading /></el-icon>
            <span>{{ uploading ? '上传中...' : '添加图片' }}</span>
          </div>
          <input ref="fileInput" type="file" accept="image/jpeg,image/png" multiple hidden @change="handleFileChange" />
        </div>
      </div>
      <template #footer>
        <el-button class="dialog-cancel-btn" @click="showCheckinDialog = false">取消</el-button>
        <el-button class="dialog-submit-btn" @click="handleSubmitCheckin">发布打卡</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  HomeFilled, Edit, Star, ChatDotRound, DataAnalysis,
  Present, Picture, Setting, Calendar, Tickets, Close, Loading
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { coupleApi, checkinApi, weekCheckinApi, wishlistApi } from '../api/modules/checkin'
import { useSidebar } from '../composables/useSidebar'
import request from '../utils/request'

const router = useRouter()
const { anniversaries: upcomingAnniversaries, weatherData, fetchAll: fetchSidebar } = useSidebar()

// ==================== 导航 ====================
const activeNav = ref('checkin')

const navRouteMap = {
  home: '/',
  checkin: '/checkin',
  wishlist: '/wishlist',
  whisper: '/whisper',
  stats: '/stats',
  anniversary: '/anniversary',
  album: '/album',
  settings: '/settings'
}

const navItems = [
  { key: 'home', label: '首页', icon: HomeFilled },
  { key: 'checkin', label: '打卡', icon: Edit },
  { key: 'wishlist', label: '愿望清单', icon: Star },
  { key: 'whisper', label: '悄悄话', icon: ChatDotRound },
  { key: 'stats', label: '数据统计', icon: DataAnalysis },
  { key: 'anniversary', label: '纪念日', icon: Present },
  { key: 'album', label: '相册', icon: Picture },
  { key: 'settings', label: '设置', icon: Setting }
]

const handleNavClick = (key) => {
  activeNav.value = key
  const path = navRouteMap[key]
  if (path && path !== router.currentRoute.value.path) {
    router.push(path)
  }
}

// ==================== 情侣数据 ====================
const hasCouple = ref(false)
const coupleData = ref({
  daysTogether: 0,
  startDate: '',
  userName: '',
  userAvatar: '',
  partnerName: '',
  partnerAvatar: ''
})

// ==================== 本周日历 ====================
const weekDays = ref([])
const weeklyCheckedCount = computed(() => weekDays.value.filter(d => d.checked).length)

// ==================== 愿望清单 ====================
const wishlistData = ref({
  completed: 0,
  total: 0,
  percent: 0
})

// ==================== 打卡记录 ====================
const checkinRecords = ref([])

// ==================== 打卡弹窗 ====================
const showCheckinDialog = ref(false)
const checkinForm = ref({ content: '' })
const checkinImages = ref([])
const uploading = ref(false)
const fileInput = ref(null)
const submitting = ref(false)

const triggerUpload = () => {
  fileInput.value?.click()
}

const handleFileChange = async (e) => {
  const files = e.target.files
  if (!files || files.length === 0) return

  uploading.value = true
  const uploadedUrls = []
  try {
    for (const file of files) {
      if (!file.type.startsWith('image/')) continue
      if (file.size > 10 * 1024 * 1024) {
        ElMessage.warning(`图片 ${file.name} 超过10MB限制`)
        continue
      }
      const formData = new FormData()
      formData.append('file', file)
      const res = await request.post('/oss/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
      if (res.code === 200 && res.data) {
        uploadedUrls.push(res.data)
      }
    }
    checkinImages.value.push(...uploadedUrls)
    if (uploadedUrls.length > 0) {
      ElMessage.success(`成功上传 ${uploadedUrls.length} 张图片`)
    }
  } catch (err) {
    ElMessage.error('图片上传失败')
    console.warn('[CheckIn] 上传失败:', err)
  } finally {
    uploading.value = false
    e.target.value = ''
  }
}

const todayStr = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日`
})

// ==================== 数据加载 ====================
const loadCoupleData = async () => {
  try {
    const res = await coupleApi.getInfo()
    if (res.code === 200 && res.data) {
      if (res.data.hasCouple) {
        hasCouple.value = true
        coupleData.value = {
          daysTogether: res.data.daysTogether || 0,
          startDate: res.data.startDate || '',
          userName: res.data.userName || '',
          userAvatar: res.data.userAvatar || '',
          partnerName: res.data.partnerName || '',
          partnerAvatar: res.data.partnerAvatar || ''
        }
      } else {
        hasCouple.value = false
      }
    }
  } catch (e) {
    console.warn('[CheckIn] 获取情侣信息失败:', e.message)
    hasCouple.value = false
  }
}

const loadWeekCheckin = async () => {
  try {
    const res = await weekCheckinApi.getWeek()
    if (res.code === 200 && res.data && res.data.days) {
      weekDays.value = res.data.days
    }
  } catch (e) {
    console.warn('[CheckIn] 获取周打卡失败:', e.message)
  }
}

const loadWishlistSummary = async () => {
  try {
    const res = await wishlistApi.getInfo()
    if (res.code === 200 && res.data) {
      wishlistData.value = {
        completed: res.data.completed,
        total: res.data.total,
        percent: res.data.percent
      }
    }
  } catch (e) {
    console.warn('[CheckIn] 获取愿望清单失败:', e.message)
  }
}

const loadCheckinRecords = async () => {
  try {
    const res = await checkinApi.getList()
    if (res.code === 200 && res.data && res.data.list) {
      checkinRecords.value = res.data.list
    }
  } catch (e) {
    console.warn('[CheckIn] 获取打卡记录失败:', e.message)
  }
}

onMounted(async () => {
  await loadCoupleData()
  fetchSidebar()
  if (hasCouple.value) {
    loadWeekCheckin()
    loadWishlistSummary()
    loadCheckinRecords()
  }
})

// ==================== 打卡操作 ====================
const handleSubmitCheckin = async () => {
  if (!checkinForm.value.content.trim()) return
  if (!hasCouple.value) {
    ElMessage.warning('请先在设置中绑定情侣关系')
    return
  }
  submitting.value = true
  try {
    const res = await checkinApi.create({
      content: checkinForm.value.content.trim(),
      images: checkinImages.value
    })
    if (res.code === 200) {
      checkinRecords.value.unshift(res.data)
      ElMessage.success('打卡成功')
      // 刷新周打卡
      loadWeekCheckin()
    }
  } catch (e) {
    ElMessage.error('打卡失败: ' + (e.message || '网络错误'))
  } finally {
    submitting.value = false
    checkinForm.value.content = ''
    checkinImages.value = []
    showCheckinDialog.value = false
  }
}

const previewImage = (url) => {
  window.open(url, '_blank')
}

// ==================== 点赞操作 ====================
const handleLike = async (record) => {
  try {
    const res = await checkinApi.like({ recordId: record.id })
    if (res.code === 200) {
      record.likeCount = res.data.likeCount
      record.liked = res.data.liked
    }
  } catch (e) {
    console.warn('[CheckIn] 点赞失败:', e.message)
  }
}
</script>

<style scoped>
/* ==================== 全局布局 ==================== */
.checkin-page {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #FFF5F5 0%, #FFEBEB 50%, #FFF0F5 100%);
  font-family: 'Microsoft YaHei', Arial, sans-serif;
}

/* ==================== 左侧导航栏 ==================== */
.left-sidebar {
  width: 220px;
  min-width: 220px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-right: 1px solid #FFE6E6;
  padding: 24px 0;
  display: flex;
  flex-direction: column;
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
  z-index: 10;
  box-shadow: 2px 0 20px rgba(255, 192, 203, 0.1);
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 20px 20px;
  border-bottom: 1px solid #FFE6E6;
  margin-bottom: 8px;
}

.logo-heart {
  width: 28px;
  height: 28px;
  flex-shrink: 0;
}

.sidebar-title {
  font-size: 18px;
  font-weight: 700;
  color: #FF8FA3;
  letter-spacing: 1px;
}

.sidebar-nav {
  flex: 1;
  padding: 8px 12px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 16px;
  border-radius: 10px;
  color: #999;
  cursor: pointer;
  transition: all 0.25s ease;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
}

.nav-item:hover {
  background: #FFF0F5;
  color: #FF9999;
}

.nav-item.active {
  background: linear-gradient(135deg, #FFE6E6, #FFD1DC);
  color: #FF8FA3;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(255, 153, 153, 0.15);
}

.nav-item .el-icon {
  font-size: 18px;
}

.sidebar-footer-card {
  margin: 16px 16px 0;
  background: linear-gradient(135deg, #FFF0F5, #FFE6E9);
  border-radius: 14px;
  padding: 20px;
  text-align: center;
  border: 1px solid #FFE4E9;
}

.footer-heart {
  width: 36px;
  height: 36px;
  margin-bottom: 8px;
}

.footer-card-title {
  font-size: 14px;
  font-weight: 600;
  color: #FF8FA3;
  margin-bottom: 4px;
}

.footer-card-desc {
  font-size: 12px;
  color: #CC9999;
}

/* ==================== 中间主内容区 ==================== */
.main-content {
  flex: 1;
  padding: 28px 32px;
  min-width: 0;
  overflow-y: auto;
  height: 100vh;
}

/* 未绑定提示卡片 */
.no-couple-card { display: flex; flex-direction: column; align-items: center; justify-content: center; text-align: center; padding: 60px 40px; background: #FFF; border-radius: 20px; box-shadow: 0 8px 30px rgba(255,192,203,0.12); margin-top: 40px; }
.no-couple-icon { width: 80px; height: 80px; margin-bottom: 20px; }
.no-couple-title { font-size: 20px; font-weight: 700; color: #888; margin-bottom: 10px; }
.no-couple-desc { font-size: 14px; color: #BBB; line-height: 1.6; margin-bottom: 28px; max-width: 320px; }
.no-couple-btn { display: inline-flex; align-items: center; gap: 6px; padding: 12px 36px; background: linear-gradient(135deg, #FFB6C1, #FF8FA3); border: none; border-radius: 28px; color: #FFF; font-size: 15px; font-weight: 600; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 4px 16px rgba(255,143,163,0.3); }
.no-couple-btn:hover { background: linear-gradient(135deg, #FF8FA3, #FF7A95); transform: translateY(-2px); box-shadow: 0 8px 24px rgba(255,143,163,0.45); }


/* 顶部情侣信息栏 */
.couple-info-bar {
  background: #FFF;
  border-radius: 20px;
  padding: 32px 28px 28px;
  text-align: center;
  box-shadow: 0 8px 30px rgba(255, 192, 203, 0.12);
  margin-bottom: 24px;
  position: relative;
  overflow: hidden;
}

.couple-info-bar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #FFB6C1, #FF8FA3, #FFB6C1);
}

.couple-avatars {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0;
  margin-bottom: 14px;
}

.avatar-wrapper {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #FFE6E6;
  box-shadow: 0 4px 16px rgba(255, 192, 203, 0.25);
  flex-shrink: 0;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatars-heart {
  width: 32px;
  height: 32px;
  margin: 0 -2px;
  z-index: 1;
  filter: drop-shadow(0 2px 4px rgba(255, 143, 163, 0.3));
  animation: heartPulse 2s ease-in-out infinite;
}

@keyframes heartPulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.12); }
}

.avatars-heart svg {
  width: 100%;
  height: 100%;
}

.couple-names {
  font-size: 18px;
  font-weight: 600;
  color: #666;
  margin-bottom: 8px;
}

.couple-days {
  margin-bottom: 6px;
}

.days-number {
  font-size: 52px;
  font-weight: 800;
  color: #FF8FA3;
  line-height: 1;
  letter-spacing: 2px;
}

.days-unit {
  font-size: 20px;
  color: #FFB6C1;
  font-weight: 600;
  margin-left: 4px;
}

.couple-date-info {
  font-size: 13px;
  color: #999;
  margin-bottom: 20px;
}

/* 立即打卡按钮 */
.checkin-btn-main {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 36px;
  background: linear-gradient(135deg, #FF8FA3, #FFB6C1);
  border: none;
  border-radius: 50px;
  color: #FFF;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 6px 20px rgba(255, 143, 163, 0.35);
  transition: all 0.3s ease;
  letter-spacing: 1px;
}

.checkin-btn-main:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 143, 163, 0.45);
  background: linear-gradient(135deg, #FF7A95, #FF8FA3);
}

.checkin-btn-main:active {
  transform: translateY(0);
  box-shadow: 0 4px 12px rgba(255, 143, 163, 0.3);
}

.btn-heart-icon {
  width: 18px;
  height: 18px;
}

/* 中部功能模块 */
.feature-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
  margin-bottom: 24px;
}

.card {
  background: #FFF;
  border-radius: 16px;
  padding: 22px;
  box-shadow: 0 4px 16px rgba(255, 192, 203, 0.08);
  transition: all 0.3s ease;
}

.card:hover {
  box-shadow: 0 8px 24px rgba(255, 192, 203, 0.15);
  transform: translateY(-1px);
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #555;
  margin-bottom: 16px;
}

.card-title .el-icon {
  color: #FF9999;
  font-size: 18px;
}

/* 本周打卡日历 */
.week-calendar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 14px;
}

.week-day-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 8px 10px;
  border-radius: 12px;
  transition: all 0.25s ease;
  cursor: default;
}

.week-day-item.today {
  background: #FFF0F5;
}

.week-day-item.checked .day-num {
  background: #FF8FA3;
  color: #FFF;
}

.day-label {
  font-size: 12px;
  color: #999;
  font-weight: 500;
}

.week-day-item.today .day-label {
  color: #FF8FA3;
  font-weight: 600;
}

.day-num {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 14px;
  font-weight: 600;
  color: #666;
  background: #F8F8F8;
  transition: all 0.25s ease;
}

.week-day-item.today .day-num {
  border: 2px solid #FFB6C1;
  background: #FFF;
  color: #FF8FA3;
}

.week-summary {
  text-align: center;
  font-size: 13px;
  color: #999;
  padding-top: 12px;
  border-top: 1px solid #F5F5F5;
}

.week-summary strong {
  color: #FF8FA3;
  font-size: 18px;
  margin: 0 2px;
}

/* 愿望清单 */
.wish-icon {
  width: 18px;
  height: 18px;
}

.wishlist-stats {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 10px;
}

.wishlist-text {
  font-size: 14px;
  color: #666;
}

.wishlist-text strong {
  color: #FF8FA3;
  font-size: 22px;
}

.wishlist-percent {
  font-size: 20px;
  font-weight: 700;
  color: #FF8FA3;
}

.progress-bar-wrapper {
  width: 100%;
  height: 10px;
  background: #FFF0F5;
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 14px;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #FFB6C1, #FF8FA3);
  border-radius: 10px;
  transition: width 1s ease-in-out;
}

.wishlist-btn {
  padding: 7px 22px;
  border: 1px solid #FFB6C1;
  border-radius: 20px;
  background: #FFF;
  color: #FF9999;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
  display: block;
  margin: 0 auto;
}

.wishlist-btn:hover {
  background: #FF8FA3;
  color: #FFF;
  border-color: #FF8FA3;
  box-shadow: 0 4px 12px rgba(255, 143, 163, 0.3);
}

/* ==================== 打卡记录时间轴 ==================== */
.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 17px;
  font-weight: 600;
  color: #555;
  margin-bottom: 18px;
}

.section-title .el-icon {
  color: #FF9999;
  font-size: 20px;
}

.timeline-list {
  position: relative;
}

.timeline-item {
  display: flex;
  gap: 16px;
  margin-bottom: 0;
}

/* 时间轴竖线 */
.timeline-line {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 18px;
  flex-shrink: 0;
}

.timeline-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #FFB6C1;
  border: 3px solid #FFE6E6;
  box-shadow: 0 0 0 3px rgba(255, 182, 193, 0.2);
  flex-shrink: 0;
  margin-top: 6px;
}

.timeline-bar {
  width: 2px;
  flex: 1;
  background: linear-gradient(to bottom, #FFB6C1, #FFE6E9);
  margin: 4px 0;
  min-height: 20px;
}

/* 记录卡片 */
.timeline-card {
  flex: 1;
  background: #FFF;
  border-radius: 14px;
  padding: 18px 20px;
  box-shadow: 0 4px 16px rgba(255, 192, 203, 0.08);
  margin-bottom: 16px;
  transition: all 0.3s ease;
}

.timeline-card:hover {
  box-shadow: 0 6px 20px rgba(255, 192, 203, 0.15);
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.record-user {
  display: flex;
  align-items: center;
  gap: 8px;
}

.record-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #FFE6E6;
}

.record-nickname {
  font-size: 14px;
  font-weight: 600;
  color: #FF9999;
}

.record-time {
  font-size: 12px;
  color: #BBB;
}

.record-content {
  font-size: 14px;
  color: #666;
  line-height: 1.7;
  margin-bottom: 12px;
}

/* 图片预览 */
.record-images {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.record-img-wrapper {
  flex-shrink: 0;
}

.record-img-real { width: 80px; height: 80px; border-radius: 10px; object-fit: cover; cursor: pointer; transition: all 0.3s ease; }
.record-img-real:hover { transform: scale(1.06); box-shadow: 0 4px 16px rgba(255,192,203,0.3); }
.record-img-placeholder {
  width: 80px;
  height: 80px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  cursor: pointer;
}

.record-img-placeholder svg {
  width: 28px;
  height: 28px;
  opacity: 0.6;
}

.record-img-placeholder:hover {
  transform: scale(1.06);
  box-shadow: 0 4px 16px rgba(255, 192, 203, 0.3);
}

/* 点赞评论 */
.record-actions {
  display: flex;
  align-items: center;
  gap: 20px;
  padding-top: 10px;
  border-top: 1px solid #F8F0F0;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #999;
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
}

.action-item:hover {
  color: #FF8FA3;
}

.action-icon {
  width: 18px;
  height: 18px;
  transition: all 0.2s ease;
}

.action-icon.liked {
  animation: heartBeat 0.4s ease;
}

@keyframes heartBeat {
  0% { transform: scale(1); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1); }
}

/* ==================== 右侧信息卡片栏 ==================== */
.right-sidebar {
  width: 280px;
  min-width: 280px;
  padding: 24px 20px 24px 0;
  display: flex;
  flex-direction: column;
  gap: 18px;
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
}

.side-card {
  background: #FFF;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(255, 192, 203, 0.08);
  transition: all 0.3s ease;
}

.side-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.side-card-header h4 {
  display: flex;
  align-items: center;
  gap: 7px;
  font-size: 14px;
  font-weight: 600;
  color: #555;
}

.side-card-title-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

.view-all {
  font-size: 12px;
  color: #FFB6C1;
  cursor: pointer;
  text-decoration: none;
  transition: color 0.2s;
}

.view-all:hover {
  color: #FF8FA3;
}

/* 纪念日列表 */
.anniversary-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #FFF5F5;
}

.anniversary-item:last-child {
  border-bottom: none;
}

.anniversary-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #FFB6C1;
  flex-shrink: 0;
}

.anniversary-dot.love {
  background: #FF8FA3;
}

.anniversary-dot.birthday {
  background: #FFD700;
}

.anniversary-info {
  flex: 1;
  min-width: 0;
}

.anniversary-name {
  display: block;
  font-size: 13px;
  color: #666;
  font-weight: 500;
  margin-bottom: 2px;
}

.anniversary-date {
  display: block;
  font-size: 11px;
  color: #BBB;
}

.anniversary-days {
  font-size: 12px;
  color: #999;
  flex-shrink: 0;
}

.anniversary-days strong {
  color: #FF8FA3;
  font-size: 14px;
}

/* 天气卡片 */
.weather-main {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 12px;
}

.weather-icon-svg {
  width: 56px;
  height: 56px;
  flex-shrink: 0;
}

.weather-info {
  display: flex;
  flex-direction: column;
}

.weather-temp {
  font-size: 32px;
  font-weight: 700;
  color: #FF8FA3;
  line-height: 1;
}

.weather-desc {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}

.weather-tip {
  font-size: 13px;
  color: #FFB6C1;
  text-align: center;
  padding: 10px;
  background: #FFF5F5;
  border-radius: 10px;
  font-weight: 500;
}

/* 陪伴时光卡片 */
.companion-card {
  text-align: center;
  background: linear-gradient(180deg, #FFF 0%, #FFF5F5 100%);
}

.companion-illustration {
  padding: 12px 0 8px;
}

.companion-illustration svg {
  width: 160px;
  height: auto;
}

.companion-text {
  font-size: 13px;
  color: #999;
  line-height: 1.8;
}

.companion-heart {
  width: 20px;
  height: 20px;
  margin-top: 8px;
}

/* ==================== 打卡弹窗 ==================== */
.dialog-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #FF8FA3;
  font-size: 18px;
  font-weight: 600;
}

.dialog-title-icon {
  width: 24px;
  height: 24px;
}

.dialog-body {
  padding: 8px 0;
}

.dialog-date-row {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #999;
  font-size: 14px;
  margin-bottom: 16px;
}

.dialog-date-row .el-icon {
  color: #FFB6C1;
}

.checkin-textarea :deep(.el-textarea__inner) {
  border-radius: 12px;
  border: 2px solid #FFE4E9;
  background: #FFFAFA;
  font-size: 14px;
  color: #666;
  resize: vertical;
  transition: all 0.3s ease;
}

.checkin-textarea :deep(.el-textarea__inner):hover {
  border-color: #FFB6C1;
}

.checkin-textarea :deep(.el-textarea__inner):focus {
  border-color: #FF9999;
  box-shadow: 0 0 0 3px rgba(255, 153, 153, 0.1);
}

.dialog-image-upload {
  margin-top: 14px;
}

.upload-images-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
}

.upload-preview-item {
  width: 80px;
  height: 80px;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  border: 2px solid #FFE4E9;
}

.upload-preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-remove-icon {
  position: absolute;
  top: 2px;
  right: 2px;
  background: rgba(0,0,0,0.4);
  color: #FFF;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 10px;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  width: 80px;
  height: 80px;
  border: 2px dashed #FFD1DC;
  border-radius: 12px;
  color: #FFB6C1;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 12px;
}

.upload-placeholder:hover {
  border-color: #FF9999;
  background: #FFF5F5;
}

.upload-placeholder .el-icon {
  font-size: 22px;
}

.uploading-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 弹窗按钮 */
.dialog-cancel-btn {
  background: #F5F5F5;
  border: 1px solid #E8E8E8;
  color: #999;
  border-radius: 8px;
  padding: 8px 20px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.dialog-cancel-btn:hover {
  background: #E8E8E8;
  color: #666;
}

.dialog-submit-btn {
  background: linear-gradient(135deg, #FF8FA3, #FFB6C1);
  border: none;
  color: #FFF;
  border-radius: 8px;
  padding: 8px 20px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(255, 143, 163, 0.3);
  transition: all 0.3s ease;
}

.dialog-submit-btn:hover {
  background: linear-gradient(135deg, #FF7A95, #FF8FA3);
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(255, 143, 163, 0.4);
}

/* ==================== Element Plus 弹窗深度覆盖 ==================== */
:deep(.checkin-dialog) {
  border-radius: 16px !important;
  overflow: hidden;
}

:deep(.checkin-dialog .el-dialog__header) {
  background: linear-gradient(135deg, #FFF0F5, #FFE6E9);
  padding: 20px 24px !important;
  border-bottom: 1px solid #FFE4E9;
}

:deep(.checkin-dialog .el-dialog__body) {
  padding: 24px !important;
}

:deep(.checkin-dialog .el-dialog__footer) {
  padding: 16px 24px 24px !important;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* ==================== 滚动条 ==================== */
.main-content::-webkit-scrollbar,
.right-sidebar::-webkit-scrollbar,
.left-sidebar::-webkit-scrollbar {
  width: 6px;
}

.main-content::-webkit-scrollbar-track,
.right-sidebar::-webkit-scrollbar-track,
.left-sidebar::-webkit-scrollbar-track {
  background: transparent;
}

.main-content::-webkit-scrollbar-thumb,
.right-sidebar::-webkit-scrollbar-thumb,
.left-sidebar::-webkit-scrollbar-thumb {
  background: #FFD1DC;
  border-radius: 3px;
}

.main-content::-webkit-scrollbar-thumb:hover,
.right-sidebar::-webkit-scrollbar-thumb:hover,
.left-sidebar::-webkit-scrollbar-thumb:hover {
  background: #FFB6C1;
}

/* ==================== 响应式 ==================== */
@media (max-width: 1200px) {
  .right-sidebar {
    width: 250px;
    min-width: 250px;
    padding: 20px 14px 20px 0;
  }

  .main-content {
    padding: 22px 24px;
  }

  .feature-cards {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 991px) {
  .left-sidebar {
    width: 180px;
    min-width: 180px;
  }

  .right-sidebar {
    display: none;
  }

  .main-content {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .left-sidebar {
    display: none;
  }

  .main-content {
    padding: 16px;
    max-width: 100%;
  }

  .couple-info-bar {
    padding: 24px 16px 20px;
  }

  .days-number {
    font-size: 40px;
  }

  .feature-cards {
    grid-template-columns: 1fr;
  }
}
</style>
