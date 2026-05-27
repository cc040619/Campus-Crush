<template>
  <div class="page-container">
    <!-- ==================== 左侧导航栏 ==================== -->
    <aside class="left-sidebar">
      <div class="sidebar-header">
        <svg class="logo-heart" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.28 2 8.5C2 5.42 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.42 22 8.5C22 12.28 18.6 15.36 13.45 20.03L12 21.35Z" fill="url(#sbHeart)"/>
          <defs><linearGradient id="sbHeart" x1="12" y1="3" x2="12" y2="21" gradientUnits="userSpaceOnUse"><stop stop-color="#FF8FA3"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs>
        </svg>
        <span class="sidebar-title">恋爱记录</span>
      </div>
      <nav class="sidebar-nav">
        <a v-for="item in navItems" :key="item.key" :class="['nav-item', { active: activeNav === item.key }]" @click="handleNavClick(item.key)">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </a>
      </nav>
      <div class="sidebar-footer-card">
        <div class="footer-card-inner">
          <svg class="footer-heart" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M24 44.35L21.1 40.06C10.8 30.72 4 24.56 4 17C4 10.84 9.84 6 16 6C19.52 6 22.68 7.88 24 10.16C25.32 7.88 28.48 6 32 6C38.16 6 44 10.84 44 17C44 24.56 37.2 30.72 26.9 40.06L24 44.35Z" fill="url(#ftHeart)"/>
            <defs><linearGradient id="ftHeart" x1="24" y1="6" x2="24" y2="44" gradientUnits="userSpaceOnUse"><stop stop-color="#FF8FA3"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs>
          </svg>
          <p class="footer-card-title">我们的故事</p>
          <p class="footer-card-desc">每一天都值得被记录</p>
        </div>
      </div>
    </aside>

    <!-- ==================== 中间主内容区 ==================== -->
    <main class="main-content">
      <div class="settings-header">
        <h2 class="page-title">
          <el-icon><Setting /></el-icon>
          设置
        </h2>
      </div>

      <!-- 绑定情侣关系 -->
      <section v-if="!hasCouple" class="settings-card">
        <div class="setting-row" style="flex-direction: column; align-items: stretch; gap: 16px;">
          <div class="setting-info">
            <span class="setting-label">
              <svg class="setting-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.28 2 8.5C2 5.42 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.42 22 8.5C22 12.28 18.6 15.36 13.45 20.03L12 21.35Z" fill="url(#setHeart)"/>
                <defs><linearGradient id="setHeart" x1="12" y1="3" x2="12" y2="21" gradientUnits="userSpaceOnUse"><stop stop-color="#FF8FA3"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs>
              </svg>
              情侣绑定
            </span>
            <span class="setting-desc">搜索对方手机号或账号，发送情侣申请，待对方同意后即可绑定</span>
          </div>
          <div style="display: flex; flex-direction: column; gap: 12px;">
            <!-- 搜索伴侣 -->
            <div style="position: relative;">
              <div style="display: flex; align-items: center; gap: 8px;">
                <span style="font-size: 14px; color: #666; white-space: nowrap;">搜索：</span>
                <el-input
                  v-model="searchKeyword"
                  placeholder="输入对方手机号或账号"
                  style="flex: 1;"
                  clearable
                  @clear="searchResults = []"
                  @keyup.enter="handleSearch"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
                <button class="search-btn" @click="handleSearch">
                  <el-icon><Search /></el-icon>
                  搜索
                </button>
              </div>
              <div v-if="searchResults.length > 0" class="search-dropdown">
                <div v-for="user in searchResults" :key="user.id" :class="['search-result-item', { selected: selectedPartner && selectedPartner.id === user.id }]" @click="selectPartner(user)">
                  <img :src="user.avatar" class="search-result-avatar" />
                  <div class="search-result-info">
                    <span class="search-result-name">{{ user.nickname || user.username }}</span>
                    <span class="search-result-detail">{{ user.username }}<span v-if="user.phone"> · {{ user.phone }}</span></span>
                  </div>
                  <el-icon v-if="selectedPartner && selectedPartner.id === user.id" color="#FF8FA3"><Check /></el-icon>
                </div>
              </div>
              <div v-if="searched && searchResults.length === 0 && searchKeyword.trim()" class="search-dropdown">
                <div class="search-empty">未找到匹配的用户</div>
              </div>
            </div>
            <div v-if="selectedPartner" class="selected-partner-card">
              <img :src="selectedPartner.avatar" class="selected-partner-avatar" />
              <div class="selected-partner-info">
                <span class="selected-partner-name">{{ selectedPartner.nickname || selectedPartner.username }}</span>
                <span class="selected-partner-detail">{{ selectedPartner.username }}<span v-if="selectedPartner.phone"> · {{ selectedPartner.phone }}</span></span>
              </div>
              <el-icon class="remove-partner" @click="selectedPartner = null"><Close /></el-icon>
            </div>
            <div style="display: flex; align-items: center; gap: 12px;">
              <span style="font-size: 14px; color: #666; white-space: nowrap;">开始日期：</span>
              <el-date-picker v-model="requestForm.startDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" class="date-picker" style="flex: 1;" />
            </div>
            <button class="setting-action-btn" @click="handleSendRequest" style="align-self: flex-end;" :disabled="!selectedPartner || sendingRequest">发送申请</button>
          </div>
        </div>
      </section>

      <!-- 收到的情侣申请 -->
      <section v-if="pendingRequests.length > 0" class="settings-card requests-card">
        <div class="setting-row" style="flex-direction: column; align-items: stretch; gap: 12px;">
          <div class="setting-info">
            <span class="setting-label">
              <svg class="setting-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9" stroke="#FF9999" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M13.73 21a2 2 0 01-3.46 0" stroke="#FF9999" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
              收到的情侣申请
            </span>
          </div>
          <div v-for="req in pendingRequests" :key="req.id" class="request-item">
            <img :src="req.fromUserAvatar || 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'" class="request-avatar" />
            <div class="request-info">
              <span class="request-name">{{ req.fromUserName || '用户' }}</span>
              <span class="request-detail" v-if="req.startDate">开始日期: {{ req.startDate }}</span>
            </div>
            <div class="request-actions">
              <button class="accept-btn" @click="handleAccept(req.id)">同意</button>
              <button class="reject-btn" @click="handleReject(req.id)">拒绝</button>
            </div>
          </div>
        </div>
      </section>

      <!-- 修改恋爱纪念日 -->
      <section class="settings-card">
        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-label">
              <svg class="setting-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.28 2 8.5C2 5.42 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.42 22 8.5C22 12.28 18.6 15.36 13.45 20.03L12 21.35Z" fill="url(#setHeart2)"/>
                <defs><linearGradient id="setHeart2" x1="12" y1="3" x2="12" y2="21" gradientUnits="userSpaceOnUse"><stop stop-color="#FF8FA3"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs>
              </svg>
              恋爱纪念日
            </span>
            <span class="setting-desc">设置你们的恋爱开始日期，相恋天数将根据该日期自动计算</span>
          </div>
          <div class="setting-control">
            <el-date-picker
              v-model="settingsForm.startDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              class="date-picker"
            />
            <button class="setting-action-btn" @click="saveStartDate">保存</button>
          </div>
        </div>
      </section>

      <!-- 情侣信息展示 -->
      <section v-if="hasCouple" class="settings-card">
        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-label">
              <svg class="setting-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="8" r="4" stroke="#FF9999" stroke-width="1.5"/>
                <path d="M4 20c0-4 4-7 8-7s8 3 8 7" stroke="#FF9999" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
              情侣信息
            </span>
            <span class="setting-desc">以下信息来源于个人资料设置</span>
          </div>
          <div class="setting-control nicknames-control">
            <div class="nickname-row">
              <img :src="settingsForm.userAvatar" style="width: 32px; height: 32px; border-radius: 50%; object-fit: cover;" />
              <span style="font-size: 14px; color: #555;">{{ settingsForm.userName }}</span>
              <span style="font-size: 12px; color: #BBB;">(我)</span>
            </div>
            <div class="nickname-row">
              <img :src="settingsForm.partnerAvatar" style="width: 32px; height: 32px; border-radius: 50%; object-fit: cover;" />
              <span style="font-size: 14px; color: #555;">{{ settingsForm.partnerName }}</span>
              <span style="font-size: 12px; color: #BBB;">(伴侣)</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 消息通知 -->
      <section class="settings-card">
        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-label">
              <svg class="setting-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M18 8A6 6 0 006 8c0 7-3 9-3 9h18s-3-2-3-9" stroke="#FF9999" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M13.73 21a2 2 0 01-3.46 0" stroke="#FF9999" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
              消息通知
            </span>
            <span class="setting-desc">开启后将接收打卡提醒、纪念日通知和对方的消息推送</span>
          </div>
          <div class="setting-control">
            <el-switch
              v-model="settingsForm.notifications"
              active-text="已开启"
              inactive-text="已关闭"
              class="notify-switch"
              inline-prompt
            />
          </div>
        </div>
      </section>

      <!-- 打卡提醒 -->
      <section class="settings-card">
        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-label">
              <svg class="setting-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="9" stroke="#FF9999" stroke-width="1.5"/>
                <polyline points="12,7 12,12 15,15" stroke="#FF9999" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              打卡提醒
            </span>
            <span class="setting-desc">每天定点提醒你完成打卡，记录恋爱日常</span>
          </div>
          <div class="setting-control">
            <el-time-select
              v-model="settingsForm.reminderTime"
              placeholder="选择时间"
              start="08:00"
              step="00:30"
              end="23:00"
              class="time-select"
            />
          </div>
        </div>
      </section>

      <!-- 解除情侣关系 -->
      <section v-if="hasCouple" class="settings-card danger-card">
        <div class="setting-row">
          <div class="setting-info">
            <span class="setting-label danger-label">
              <svg class="setting-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="#FF6B6B" stroke-width="1.5"/>
                <line x1="15" y1="9" x2="9" y2="15" stroke="#FF6B6B" stroke-width="1.5" stroke-linecap="round"/>
                <line x1="9" y1="9" x2="15" y2="15" stroke="#FF6B6B" stroke-width="1.5" stroke-linecap="round"/>
              </svg>
              解除情侣关系
            </span>
            <span class="setting-desc danger">解除后所有共享数据将被清除，该操作不可逆</span>
          </div>
          <div class="setting-control">
            <button class="danger-btn" @click="showUnbindDialog = true">解除关系</button>
          </div>
        </div>
      </section>
    </main>

    <!-- ==================== 右侧信息卡片栏 ==================== -->
    <aside class="right-sidebar">
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
          <div v-for="(item, i) in anniversaries" :key="i" class="anniversary-item">
            <div class="anniversary-dot" :class="item.type"></div>
            <div class="anniversary-info">
              <span class="anniversary-name">{{ item.name }}</span>
              <span class="anniversary-date">{{ item.date }}</span>
            </div>
            <span class="anniversary-days">还有 <strong>{{ item.daysLeft }}</strong> 天</span>
          </div>
        </div>
      </div>
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
      <div class="side-card companion-card">
        <div class="companion-illustration">
          <svg viewBox="0 0 180 120" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="55" cy="30" r="14" fill="#FFC0CB" stroke="#FFB6C1" stroke-width="1.5"/>
            <rect x="42" y="44" width="26" height="30" rx="4" fill="#FFB6C1"/>
            <rect x="36" y="72" width="8" height="24" rx="3" fill="#FFB6C1"/>
            <rect x="66" y="72" width="8" height="24" rx="3" fill="#FFB6C1"/>
            <circle cx="125" cy="30" r="14" fill="#FFE4E9" stroke="#FFC0CB" stroke-width="1.5"/>
            <rect x="112" y="44" width="26" height="30" rx="4" fill="#FFC0CB"/>
            <rect x="106" y="72" width="8" height="24" rx="3" fill="#FFC0CB"/>
            <rect x="136" y="72" width="8" height="24" rx="3" fill="#FFC0CB"/>
            <path d="M90 55L87 50C77 42 72 38 72 33C72 29 75 27 78 27C80 27 82 28 83 30L90 40L97 30C98 28 100 27 102 27C105 27 108 29 108 33C108 38 103 42 93 50L90 55Z" fill="#FF8FA3" opacity="0.9"/>
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

    <!-- ==================== 解除关系确认弹窗 ==================== -->
    <el-dialog v-model="showUnbindDialog" width="400px" :align-center="true" custom-class="unbind-dialog">
      <template #title>
        <div class="dialog-title-row danger-title">
          <span>⚠️ 解除情侣关系</span>
        </div>
      </template>
      <div class="dialog-body">
        <p class="unbind-warning-text">确定要解除情侣关系吗？解除后：</p>
        <ul class="unbind-warning-list">
          <li>所有共享打卡记录将被清空</li>
          <li>纪念日数据将被删除</li>
          <li>愿望清单将无法共同查看</li>
          <li>该操作不可恢复</li>
        </ul>
      </div>
      <template #footer>
        <el-button class="dialog-cancel-btn" @click="showUnbindDialog = false">取消</el-button>
        <el-button class="dialog-danger-btn" @click="handleUnbind">确认解除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  HomeFilled, Edit, Star, ChatDotRound, DataAnalysis,
  Present, Picture, Setting, Search, Check, Close
} from '@element-plus/icons-vue'
import { coupleApi } from '../api/modules/checkin'
import { coupleApi as legacyCoupleApi } from '../api/modules/anniversary'
import { useSidebar } from '../composables/useSidebar'

const router = useRouter()
const { anniversaries, weatherData, fetchAll: fetchSidebar } = useSidebar()

const activeNav = ref('settings')
const navRouteMap = {
  home: '/', checkin: '/checkin', wishlist: '/wishlist', whisper: '/whisper',
  stats: '/stats', anniversary: '/anniversary', album: '/album', settings: '/settings'
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
  if (path && path !== router.currentRoute.value.path) router.push(path)
}

// ==================== 设置表单 ====================
const hasCouple = ref(false)
const settingsForm = ref({
  startDate: '',
  userName: '',
  userAvatar: 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg',
  partnerName: '',
  partnerAvatar: 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg',
  notifications: true,
  reminderTime: '20:00'
})

// ==================== 搜索伴侣 ====================
const searchKeyword = ref('')
const searchResults = ref([])
const searched = ref(false)
const selectedPartner = ref(null)
const sendingRequest = ref(false)
const pendingRequests = ref([])

const handleSearch = async () => {
  const kw = searchKeyword.value.trim()
  if (!kw) {
    ElMessage.warning('请输入手机号或账号')
    return
  }
  searched.value = true
  try {
    const res = await coupleApi.search(kw)
    if (res.code === 200) {
      searchResults.value = res.data || []
    }
  } catch (e) {
    console.warn('[Settings] 搜索用户失败:', e.message)
  }
}

const selectPartner = (user) => {
  selectedPartner.value = user
  searchResults.value = []
  searchKeyword.value = ''
}

const requestForm = ref({
  startDate: ''
})

const showUnbindDialog = ref(false)

const loadSettings = async () => {
  try {
    // 先从 /api/couple 获取恋爱关系数据（包含startDate）
    const coupleRes = await legacyCoupleApi.getCouple()
    if (coupleRes.code === 200 && coupleRes.data) {
      hasCouple.value = true
      settingsForm.value.startDate = coupleRes.data.startDate || ''
    } else {
      hasCouple.value = false
    }
  } catch (e) {
    hasCouple.value = false
    console.warn('[Settings] /api/couple 获取失败:', e.message)
  }

  // 再获取情侣详细信息（昵称、头像）
  try {
    const infoRes = await coupleApi.getInfo()
    if (infoRes.code === 200 && infoRes.data && infoRes.data.hasCouple) {
      hasCouple.value = true
      settingsForm.value.userName = infoRes.data.userName || ''
      settingsForm.value.userAvatar = infoRes.data.userAvatar || 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'
      settingsForm.value.partnerName = infoRes.data.partnerName || ''
      settingsForm.value.partnerAvatar = infoRes.data.partnerAvatar || 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'
      if (!settingsForm.value.startDate && infoRes.data.startDate) {
        settingsForm.value.startDate = infoRes.data.startDate
      }
    }
  } catch (e) {
    console.warn('[Settings] /api/couple/info 获取失败:', e.message)
  }
}

onMounted(() => {
  loadSettings()
  loadPendingRequests()
  fetchSidebar()
})

// ==================== 操作 ====================
const handleSendRequest = async () => {
  if (!selectedPartner.value) {
    ElMessage.warning('请先搜索并选择伴侣')
    return
  }
  sendingRequest.value = true
  try {
    const res = await coupleApi.sendRequest({
      partnerUserId: selectedPartner.value.id,
      startDate: requestForm.value.startDate || undefined
    })
    if (res.code === 200) {
      ElMessage.success('情侣申请已发送，等待对方同意')
      selectedPartner.value = null
      requestForm.value.startDate = ''
    }
  } catch (e) {
    ElMessage.error('发送失败: ' + (e.message || '网络错误'))
  } finally {
    sendingRequest.value = false
  }
}

const loadPendingRequests = async () => {
  try {
    const res = await coupleApi.getPendingRequests()
    if (res.code === 200) {
      pendingRequests.value = res.data || []
    }
  } catch (e) {
    console.warn('[Settings] 获取待处理申请失败:', e.message)
  }
}

const handleAccept = async (requestId) => {
  try {
    const res = await coupleApi.acceptRequest(requestId)
    if (res.code === 200) {
      ElMessage.success('已同意，你们现在是情侣啦！')
      hasCouple.value = true
      pendingRequests.value = []
      loadSettings()
    }
  } catch (e) {
    ElMessage.error('操作失败: ' + (e.message || '网络错误'))
  }
}

const handleReject = async (requestId) => {
  try {
    const res = await coupleApi.rejectRequest(requestId)
    if (res.code === 200) {
      ElMessage.success('已拒绝该申请')
      pendingRequests.value = pendingRequests.value.filter(r => r.id !== requestId)
    }
  } catch (e) {
    ElMessage.error('操作失败: ' + (e.message || '网络错误'))
  }
}

const saveStartDate = async () => {
  try {
    // 如果已绑定情侣关系，通过 /api/couple 保存
    const res = await legacyCoupleApi.saveCouple({ startDate: settingsForm.value.startDate })
    if (res.code === 200) {
      ElMessage.success('恋爱纪念日已更新')
    }
  } catch (e) {
    ElMessage.error('保存失败: ' + (e.message || '网络错误'))
  }
}

const handleUnbind = async () => {
  showUnbindDialog.value = false
  try {
    const res = await coupleApi.unbind()
    if (res.code === 200) {
      ElMessage.success('情侣关系已解除')
      hasCouple.value = false
      settingsForm.value.startDate = ''
      settingsForm.value.userName = ''
      settingsForm.value.partnerName = ''
    }
  } catch (e) {
    ElMessage.error('解除失败: ' + (e.message || '网络错误'))
  }
}
</script>

<style scoped>
/* ===== 布局 + 侧栏 + 右栏 (同 CheckIn.vue) ===== */
.page-container { display: flex; min-height: 100vh; background: linear-gradient(135deg, #FFF5F5 0%, #FFEBEB 50%, #FFF0F5 100%); font-family: 'Microsoft YaHei', Arial, sans-serif; }
.left-sidebar { width: 220px; min-width: 220px; background: rgba(255,255,255,0.85); backdrop-filter: blur(12px); -webkit-backdrop-filter: blur(12px); border-right: 1px solid #FFE6E6; padding: 24px 0; display: flex; flex-direction: column; position: sticky; top: 0; height: 100vh; overflow-y: auto; z-index: 10; box-shadow: 2px 0 20px rgba(255,192,203,0.1); }
.sidebar-header { display: flex; align-items: center; gap: 10px; padding: 0 20px 20px; border-bottom: 1px solid #FFE6E6; margin-bottom: 8px; }
.logo-heart { width: 28px; height: 28px; flex-shrink: 0; }
.sidebar-title { font-size: 18px; font-weight: 700; color: #FF8FA3; letter-spacing: 1px; }
.sidebar-nav { flex: 1; padding: 8px 12px; display: flex; flex-direction: column; gap: 2px; }
.nav-item { display: flex; align-items: center; gap: 12px; padding: 11px 16px; border-radius: 10px; color: #999; cursor: pointer; transition: all 0.25s ease; font-size: 14px; font-weight: 500; text-decoration: none; }
.nav-item:hover { background: #FFF0F5; color: #FF9999; }
.nav-item.active { background: linear-gradient(135deg, #FFE6E6, #FFD1DC); color: #FF8FA3; font-weight: 600; box-shadow: 0 2px 8px rgba(255,153,153,0.15); }
.nav-item .el-icon { font-size: 18px; }
.sidebar-footer-card { margin: 16px 16px 0; background: linear-gradient(135deg, #FFF0F5, #FFE6E9); border-radius: 14px; padding: 20px; text-align: center; border: 1px solid #FFE4E9; }
.footer-heart { width: 36px; height: 36px; margin-bottom: 8px; }
.footer-card-title { font-size: 14px; font-weight: 600; color: #FF8FA3; margin-bottom: 4px; }
.footer-card-desc { font-size: 12px; color: #CC9999; }

.main-content { flex: 1; padding: 28px 32px; min-width: 0; overflow-y: auto; height: 100vh; }
.right-sidebar { width: 280px; min-width: 280px; padding: 24px 20px 24px 0; display: flex; flex-direction: column; gap: 18px; position: sticky; top: 0; height: 100vh; overflow-y: auto; }
.side-card { background: #FFF; border-radius: 16px; padding: 20px; box-shadow: 0 4px 16px rgba(255,192,203,0.08); transition: all 0.3s ease; }
.side-card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.side-card-header h4 { display: flex; align-items: center; gap: 7px; font-size: 14px; font-weight: 600; color: #555; }
.side-card-title-icon { width: 18px; height: 18px; flex-shrink: 0; }
.view-all { font-size: 12px; color: #FFB6C1; cursor: pointer; }
.view-all:hover { color: #FF8FA3; }
.anniversary-item { display: flex; align-items: center; gap: 10px; padding: 10px 0; border-bottom: 1px solid #FFF5F5; }
.anniversary-item:last-child { border-bottom: none; }
.anniversary-dot { width: 8px; height: 8px; border-radius: 50%; background: #FFB6C1; flex-shrink: 0; }
.anniversary-dot.love { background: #FF8FA3; }
.anniversary-dot.birthday { background: #FFD700; }
.anniversary-info { flex: 1; min-width: 0; }
.anniversary-name { display: block; font-size: 13px; color: #666; font-weight: 500; margin-bottom: 2px; }
.anniversary-date { display: block; font-size: 11px; color: #BBB; }
.anniversary-days { font-size: 12px; color: #999; flex-shrink: 0; }
.anniversary-days strong { color: #FF8FA3; font-size: 14px; }
.weather-main { display: flex; align-items: center; gap: 14px; margin-bottom: 12px; }
.weather-icon-svg { width: 56px; height: 56px; flex-shrink: 0; }
.weather-info { display: flex; flex-direction: column; }
.weather-temp { font-size: 32px; font-weight: 700; color: #FF8FA3; line-height: 1; }
.weather-desc { font-size: 13px; color: #999; margin-top: 4px; }
.weather-tip { font-size: 13px; color: #FFB6C1; text-align: center; padding: 10px; background: #FFF5F5; border-radius: 10px; font-weight: 500; }
.companion-card { text-align: center; background: linear-gradient(180deg, #FFF 0%, #FFF5F5 100%); }
.companion-illustration { padding: 12px 0 8px; }
.companion-illustration svg { width: 160px; height: auto; }
.companion-text { font-size: 13px; color: #999; line-height: 1.8; }
.companion-heart { width: 20px; height: 20px; margin-top: 8px; }

/* ===== 设置主内容 ===== */
.settings-header { margin-bottom: 20px; }
.page-title { display: flex; align-items: center; gap: 10px; font-size: 22px; font-weight: 700; color: #555; }
.page-title .el-icon { color: #FF9999; font-size: 24px; }

.settings-card { background: #FFF; border-radius: 16px; padding: 22px 24px; box-shadow: 0 4px 16px rgba(255,192,203,0.08); margin-bottom: 16px; transition: all 0.3s ease; }
.settings-card:hover { box-shadow: 0 8px 24px rgba(255,192,203,0.12); }
.danger-card { border: 1px solid #FFE0E0; }

.setting-row { display: flex; align-items: center; justify-content: space-between; gap: 20px; }
.setting-info { flex: 1; min-width: 0; }
.setting-label { display: flex; align-items: center; gap: 8px; font-size: 15px; font-weight: 600; color: #555; margin-bottom: 4px; }
.danger-label { color: #FF6B6B; }
.setting-icon { width: 20px; height: 20px; flex-shrink: 0; }
.setting-desc { font-size: 12px; color: #BBB; line-height: 1.5; }
.setting-desc.danger { color: #FFAAAA; }
.setting-control { display: flex; align-items: center; gap: 10px; flex-shrink: 0; }

.setting-action-btn { padding: 8px 18px; background: linear-gradient(135deg, #FFB6C1, #FF8FA3); border: none; border-radius: 20px; color: #FFF; font-size: 13px; font-weight: 600; cursor: pointer; transition: all 0.3s; white-space: nowrap; }
.setting-action-btn:hover { background: linear-gradient(135deg, #FF8FA3, #FF7A95); transform: translateY(-1px); box-shadow: 0 4px 12px rgba(255,143,163,0.3); }

.nicknames-control { flex-direction: column; align-items: stretch; gap: 8px; }
.nickname-row { display: flex; align-items: center; gap: 8px; }
.nickname-label { font-size: 18px; width: 28px; text-align: center; }
.nickname-input :deep(.el-input__wrapper) { border-radius: 10px; border: 2px solid #FFE4E9; transition: all 0.3s; }
.nickname-input :deep(.el-input__wrapper):hover { border-color: #FFB6C1; }
.nickname-input :deep(.el-input__wrapper).is-focus { border-color: #FF9999; box-shadow: 0 0 0 3px rgba(255,153,153,0.1); }

/* 开关 */
.notify-switch :deep(.el-switch__core) { background: #DDD; border-radius: 20px; }
.notify-switch :deep(.el-switch__core .is-checked) { background: #7ECB76; }
.notify-switch :deep(.el-switch__label) { color: #999; font-size: 12px; }

/* 日期和时间 */
.date-picker :deep(.el-input__wrapper) { border-radius: 10px; border: 2px solid #FFE4E9; transition: all 0.3s; }
.date-picker :deep(.el-input__wrapper):hover { border-color: #FFB6C1; }
.date-picker :deep(.el-input__wrapper).is-focus { border-color: #FF9999; box-shadow: 0 0 0 3px rgba(255,153,153,0.1); }
.time-select :deep(.el-select__wrapper) { border-radius: 10px; border: 2px solid #FFE4E9; transition: all 0.3s; min-width: 130px; }
.time-select :deep(.el-select__wrapper):hover { border-color: #FFB6C1; }

/* 解除按钮 */
.danger-btn { padding: 10px 20px; background: #FFF; border: 2px solid #FF6B6B; border-radius: 20px; color: #FF6B6B; font-size: 13px; font-weight: 600; cursor: pointer; transition: all 0.3s; white-space: nowrap; }
.danger-btn:hover { background: #FF6B6B; color: #FFF; box-shadow: 0 4px 12px rgba(255,107,107,0.3); }

/* ===== 解除弹窗 ===== */
.dialog-title-row.danger-title { color: #FF6B6B; font-size: 16px; font-weight: 600; }
.unbind-warning-text { font-size: 14px; color: #666; margin-bottom: 12px; }
.unbind-warning-list { margin: 0; padding-left: 20px; }
.unbind-warning-list li { font-size: 13px; color: #999; margin-bottom: 6px; }
.dialog-cancel-btn { background: #F5F5F5; border: 1px solid #E8E8E8; color: #999; border-radius: 8px; padding: 8px 20px; font-weight: 500; transition: all 0.3s; }
.dialog-cancel-btn:hover { background: #E8E8E8; color: #666; }
.dialog-danger-btn { background: linear-gradient(135deg, #FF6B6B, #FF5252); border: none; color: #FFF; border-radius: 8px; padding: 8px 20px; font-weight: 600; box-shadow: 0 4px 12px rgba(255,107,107,0.3); transition: all 0.3s; }
.dialog-danger-btn:hover { background: linear-gradient(135deg, #FF5252, #E04040); transform: translateY(-1px); box-shadow: 0 6px 16px rgba(255,107,107,0.4); }
:deep(.unbind-dialog) { border-radius: 16px !important; overflow: hidden; }
:deep(.unbind-dialog .el-dialog__header) { background: #FFF5F5; padding: 20px 24px !important; border-bottom: 1px solid #FFE4E4; }
:deep(.unbind-dialog .el-dialog__body) { padding: 24px !important; }
:deep(.unbind-dialog .el-dialog__footer) { padding: 16px 24px 24px !important; display: flex; justify-content: flex-end; gap: 12px; }

/* ===== 搜索伴侣 ===== */
.search-btn { display: inline-flex; align-items: center; gap: 4px; padding: 8px 14px; background: linear-gradient(135deg, #FFB6C1, #FF8FA3); border: none; border-radius: 20px; color: #FFF; font-size: 13px; font-weight: 600; cursor: pointer; transition: all 0.3s; white-space: nowrap; flex-shrink: 0; }
.search-btn:hover { background: linear-gradient(135deg, #FF8FA3, #FF7A95); transform: translateY(-1px); box-shadow: 0 4px 12px rgba(255,143,163,0.3); }
.search-dropdown { position: absolute; top: 100%; left: 0; right: 0; background: #FFF; border: 1px solid #FFE4E9; border-radius: 12px; box-shadow: 0 8px 24px rgba(255,192,203,0.15); z-index: 100; max-height: 240px; overflow-y: auto; margin-top: 4px; }
.search-empty { padding: 14px; text-align: center; color: #BBB; font-size: 13px; }
.search-result-item { display: flex; align-items: center; gap: 10px; padding: 10px 14px; cursor: pointer; transition: background 0.2s; }
.search-result-item:hover { background: #FFF5F5; }
.search-result-item.selected { background: #FFF0F5; }
.search-result-avatar { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; border: 2px solid #FFE6E6; flex-shrink: 0; }
.search-result-info { flex: 1; min-width: 0; display: flex; flex-direction: column; }
.search-result-name { font-size: 14px; font-weight: 600; color: #555; }
.search-result-detail { font-size: 12px; color: #BBB; }
.selected-partner-card { display: flex; align-items: center; gap: 10px; padding: 10px 14px; background: #FFF0F5; border-radius: 12px; border: 1px solid #FFE4E9; }
.selected-partner-avatar { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; border: 2px solid #FFB6C1; flex-shrink: 0; }
.selected-partner-info { flex: 1; min-width: 0; display: flex; flex-direction: column; }
.selected-partner-name { font-size: 14px; font-weight: 600; color: #FF8FA3; }
.selected-partner-detail { font-size: 12px; color: #BBB; }
.remove-partner { cursor: pointer; color: #CCC; font-size: 16px; transition: color 0.2s; flex-shrink: 0; }
.remove-partner:hover { color: #FF6B6B; }

/* ===== 收到的申请 ===== */
.requests-card { border-left: 3px solid #FF8FA3; }
.request-item { display: flex; align-items: center; gap: 10px; padding: 10px 12px; background: #FFF5F5; border-radius: 12px; }
.request-avatar { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; border: 2px solid #FFB6C1; flex-shrink: 0; }
.request-info { flex: 1; min-width: 0; display: flex; flex-direction: column; }
.request-name { font-size: 14px; font-weight: 600; color: #555; }
.request-detail { font-size: 12px; color: #BBB; margin-top: 2px; }
.request-actions { display: flex; gap: 8px; flex-shrink: 0; }
.accept-btn { padding: 6px 16px; background: linear-gradient(135deg, #FFB6C1, #FF8FA3); border: none; border-radius: 16px; color: #FFF; font-size: 12px; font-weight: 600; cursor: pointer; transition: all 0.2s; }
.accept-btn:hover { background: linear-gradient(135deg, #FF8FA3, #FF7A95); }
.reject-btn { padding: 6px 16px; background: #FFF; border: 1px solid #DDD; border-radius: 16px; color: #999; font-size: 12px; font-weight: 500; cursor: pointer; transition: all 0.2s; }
.reject-btn:hover { border-color: #FF6B6B; color: #FF6B6B; }

/* ===== 滚动条 & 响应式 ===== */
.main-content::-webkit-scrollbar, .right-sidebar::-webkit-scrollbar, .left-sidebar::-webkit-scrollbar { width: 6px; }
.main-content::-webkit-scrollbar-track, .right-sidebar::-webkit-scrollbar-track, .left-sidebar::-webkit-scrollbar-track { background: transparent; }
.main-content::-webkit-scrollbar-thumb, .right-sidebar::-webkit-scrollbar-thumb, .left-sidebar::-webkit-scrollbar-thumb { background: #FFD1DC; border-radius: 3px; }
.main-content::-webkit-scrollbar-thumb:hover, .right-sidebar::-webkit-scrollbar-thumb:hover, .left-sidebar::-webkit-scrollbar-thumb:hover { background: #FFB6C1; }

@media (max-width: 1200px) { .right-sidebar { width: 250px; min-width: 250px; padding: 20px 14px 20px 0; } .main-content { padding: 22px 24px; } }
@media (max-width: 991px) { .left-sidebar { width: 180px; min-width: 180px; } .right-sidebar { display: none; } .main-content { max-width: 100%; } .setting-row { flex-direction: column; align-items: flex-start; } }
@media (max-width: 768px) { .left-sidebar { display: none; } .main-content { padding: 16px; max-width: 100%; } }
</style>
