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
      <!-- 未绑定情侣 -->
      <div v-if="!hasCouple" class="no-couple-card">
        <svg class="no-couple-icon" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M24 42.7L21.1 38.4C10.8 29.1 4 22.9 4 15.5C4 9.3 9.8 4.5 16 4.5C19.5 4.5 22.7 6.3 24 8.6C25.3 6.3 28.5 4.5 32 4.5C38.2 4.5 44 9.3 44 15.5C44 22.9 37.2 29.1 26.9 38.4L24 42.7Z" fill="#FFE4E9" stroke="#FFB6C1" stroke-width="1.5"/>
        </svg>
        <p class="no-couple-title">尚未绑定情侣关系</p>
        <p class="no-couple-desc">悄悄话是情侣之间的私密聊天空间，请先在设置中绑定情侣关系</p>
        <button class="no-couple-btn" @click="router.push('/settings')">去绑定</button>
      </div>

      <!-- 双栏聊天布局 -->
      <div v-else class="chat-layout">
        <!-- 左侧会话列表 -->
        <div class="chat-contacts">
          <h3 class="contacts-title">
            <svg class="contacts-title-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H7L3 21V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V15Z" stroke="#FF9999" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            悄悄话
          </h3>
          <div
            v-for="contact in contacts"
            :key="contact.id"
            :class="['contact-item', { active: activeContact === contact.id }]"
            @click="switchContact(contact.id)"
          >
            <div class="contact-avatar-wrapper">
              <img :src="contact.avatar" class="contact-avatar" />
              <div v-if="contact.online" class="online-dot"></div>
            </div>
            <div class="contact-info">
              <span class="contact-name">{{ contact.name }}</span>
              <span class="contact-last-msg">{{ contact.lastMsg }}</span>
            </div>
            <div class="contact-meta">
              <span class="contact-time">{{ formatContactTime(contact.lastTime) }}</span>
              <span v-if="contact.unread" class="unread-badge">{{ contact.unread }}</span>
            </div>
          </div>
        </div>

        <!-- 右侧聊天窗口 -->
        <div class="chat-window">
          <!-- 聊天头部 -->
          <div class="chat-header">
            <div class="chat-partner">
              <img :src="currentContact.avatar" class="partner-avatar" />
              <div>
                <span class="partner-name">{{ currentContact.name }}</span>
                <span class="partner-status">{{ currentContact.online ? '在线' : '离线' }}</span>
              </div>
            </div>
          </div>

          <!-- 消息列表 -->
          <div class="chat-messages" ref="msgContainer">
            <div v-if="currentMessages.length === 0" class="empty-chat-hint">
              <p>还没有消息，开始你们的悄悄话吧~</p>
            </div>
            <div v-for="(msg, idx) in currentMessages" :key="idx" :class="['msg-row', msg.from]">
              <img v-if="msg.from === 'partner'" :src="currentContact.avatar" class="msg-avatar" />
              <div :class="['msg-bubble', msg.from]">
                <p class="msg-text">{{ msg.text }}</p>
                <span class="msg-time">{{ formatMessageTime(msg.time) }}</span>
              </div>
              <img v-if="msg.from === 'me'" :src="myAvatar" class="msg-avatar" />
            </div>
          </div>

          <!-- 输入框 -->
          <div class="chat-input-area">
            <input
              v-model="inputMsg"
              class="chat-input"
              placeholder="说点什么吧..."
              @keyup.enter="sendMsg"
            />
            <button class="send-btn" @click="sendMsg">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M22 2L11 13" stroke="#FFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M22 2L15 22L11 13L2 9L22 2Z" stroke="#FFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
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
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { HomeFilled, Edit, Star, ChatDotRound, DataAnalysis, Present, Picture, Setting } from '@element-plus/icons-vue'
import { whisperApi } from '../api/modules/whisper'
import { useSidebar } from '../composables/useSidebar'

const router = useRouter()
const { anniversaries, weatherData, fetchAll: fetchSidebar } = useSidebar()

const activeNav = ref('whisper')
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

// ==================== 时间格式化 ====================
const formatMessageTime = (timeStr) => {
  if (!timeStr) return ''
  // 统一解析为 Date
  let date
  if (typeof timeStr === 'string') {
    // 服务器格式 "2026-05-20T14:30:00" 或已截取的 "14:30"
    if (timeStr.length <= 5) {
      const now = new Date()
      date = new Date(now.getFullYear(), now.getMonth(), now.getDate(), parseInt(timeStr.substring(0, 2)), parseInt(timeStr.substring(3, 5)))
    } else {
      date = new Date(timeStr.replace(' ', 'T'))
    }
  } else {
    return timeStr
  }
  if (isNaN(date.getTime())) return timeStr

  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const yesterday = new Date(today); yesterday.setDate(today.getDate() - 1)
  const msgDay = new Date(date.getFullYear(), date.getMonth(), date.getDate())

  const hh = String(date.getHours()).padStart(2, '0')
  const mm = String(date.getMinutes()).padStart(2, '0')
  const MM = String(date.getMonth() + 1).padStart(2, '0')
  const dd = String(date.getDate()).padStart(2, '0')

  if (msgDay.getTime() === today.getTime()) {
    return `${hh}:${mm}`
  } else if (msgDay.getTime() === yesterday.getTime()) {
    return `昨天 ${hh}:${mm}`
  } else if (date.getFullYear() === now.getFullYear()) {
    return `${MM}-${dd}`
  } else {
    return `${date.getFullYear()}-${MM}-${dd}`
  }
}

const formatContactTime = (timeStr) => {
  if (!timeStr) return ''
  let date
  if (typeof timeStr === 'string') {
    if (timeStr.length <= 5) return timeStr
    date = new Date(timeStr.replace(' ', 'T'))
  } else {
    return timeStr
  }
  if (isNaN(date.getTime())) return timeStr

  const now = new Date()
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const yesterday = new Date(today); yesterday.setDate(today.getDate() - 1)
  const msgDay = new Date(date.getFullYear(), date.getMonth(), date.getDate())

  const hh = String(date.getHours()).padStart(2, '0')
  const mm = String(date.getMinutes()).padStart(2, '0')

  if (msgDay.getTime() === today.getTime()) {
    return `${hh}:${mm}`
  } else if (msgDay.getTime() === yesterday.getTime()) {
    return '昨天'
  } else {
    return `${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  }
}

// ==================== WebSocket ====================
let ws = null
const hasCouple = ref(false)
const myAvatar = ref('https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg')
const activeContact = ref(null)
const inputMsg = ref('')
const msgContainer = ref(null)
const contacts = ref([])
const allMessages = ref({})

const currentContact = computed(() => contacts.value.find(c => c.id === activeContact.value) || contacts.value[0] || {})
const currentMessages = computed(() => allMessages.value[activeContact.value] || [])

const connectWebSocket = () => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  const userId = user.id
  if (!userId) return

  const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
  const wsUrl = `${protocol}//${window.location.host}/ws/whisper?userId=${userId}`

  ws = new WebSocket(wsUrl)

  ws.onopen = () => {
    console.log('[Whisper] WebSocket 已连接')
  }

  ws.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data)
      if (data.type === 'message') {
        const contactId = data.fromId
        if (!allMessages.value[contactId]) {
          allMessages.value[contactId] = []
        }
        const timeStr = data.time || ''
        allMessages.value[contactId].push({
          from: 'partner',
          text: data.content,
          time: timeStr
        })
        // 更新联系人最后消息
        const c = contacts.value.find(c => c.id === contactId)
        if (c) {
          c.lastMsg = data.content
          c.lastTime = timeStr
          if (activeContact.value !== contactId) {
            c.unread = (c.unread || 0) + 1
          }
        }
        if (activeContact.value === contactId) {
          nextTick(() => scrollToBottom())
        }
      }
    } catch (e) {
      console.warn('[Whisper] 消息解析失败:', e)
    }
  }

  ws.onclose = () => {
    console.log('[Whisper] WebSocket 已断开')
  }

  ws.onerror = (e) => {
    console.warn('[Whisper] WebSocket 错误:', e)
  }
}

// ==================== 聊天数据加载 ====================
const loadContacts = async () => {
  try {
    const res = await whisperApi.getContacts()
    if (res.code === 200 && res.data) {
      hasCouple.value = true
      myAvatar.value = res.data.userAvatar || 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg'
      contacts.value = (res.data.contacts || []).map(c => ({
        ...c,
        unread: c.unread || 0
      }))
      if (contacts.value.length > 0) {
        activeContact.value = contacts.value[0].id
        loadHistory()
      }
    }
  } catch (e) {
    console.warn('[Whisper] 获取联系人失败:', e.message)
    // 可能是未绑定情侣关系
    if (e.response && e.response.status === 500) {
      hasCouple.value = false
    }
  }
}

const loadHistory = async () => {
  try {
    const res = await whisperApi.getHistory()
    if (res.code === 200 && res.data && res.data.messages) {
      const partnerId = res.data.partnerId
      allMessages.value[partnerId] = res.data.messages.map(m => ({
        from: m.from,
        text: m.content,
        time: m.time || ''
      }))
      nextTick(() => scrollToBottom())
    }
  } catch (e) {
    console.warn('[Whisper] 获取聊天历史失败:', e.message)
  }
}

const switchContact = (id) => {
  activeContact.value = id
  const c = contacts.value.find(c => c.id === id)
  if (c) c.unread = 0
  nextTick(() => scrollToBottom())
}

const sendMsg = () => {
  const text = inputMsg.value.trim()
  if (!text) return
  if (!ws || ws.readyState !== WebSocket.OPEN) {
    ElMessage.warning('连接已断开，请刷新页面重试')
    return
  }
  const now = new Date()
  const time = now.toISOString()
  const toId = activeContact.value

  // 发送到WebSocket
  ws.send(JSON.stringify({
    toId: toId,
    content: text
  }))

  // 本地显示
  if (!allMessages.value[toId]) {
    allMessages.value[toId] = []
  }
  allMessages.value[toId].push({ from: 'me', text, time })

  // 更新联系人最后消息
  const c = contacts.value.find(c => c.id === toId)
  if (c) { c.lastMsg = text; c.lastTime = time }

  inputMsg.value = ''
  nextTick(() => scrollToBottom())
}

const scrollToBottom = () => {
  if (msgContainer.value) {
    msgContainer.value.scrollTop = msgContainer.value.scrollHeight
  }
}

onMounted(() => {
  loadContacts()
  connectWebSocket()
  fetchSidebar()
})

onUnmounted(() => {
  if (ws) {
    ws.close()
  }
})
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

.main-content { flex: 1; padding: 28px 32px; min-width: 0; overflow-y: auto; height: 100vh; display: flex; }
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

/* ===== 未绑定提示 ===== */
.no-couple-card { display: flex; flex-direction: column; align-items: center; justify-content: center; width: 100%; text-align: center; padding: 60px 40px; background: #FFF; border-radius: 20px; box-shadow: 0 8px 30px rgba(255,192,203,0.12); margin-top: 40px; }
.no-couple-icon { width: 80px; height: 80px; margin-bottom: 20px; }
.no-couple-title { font-size: 20px; font-weight: 700; color: #888; margin-bottom: 10px; }
.no-couple-desc { font-size: 14px; color: #BBB; margin-bottom: 28px; max-width: 320px; line-height: 1.6; }
.no-couple-btn { display: inline-flex; align-items: center; gap: 6px; padding: 12px 36px; background: linear-gradient(135deg, #FFB6C1, #FF8FA3); border: none; border-radius: 28px; color: #FFF; font-size: 15px; font-weight: 600; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 4px 16px rgba(255,143,163,0.3); }
.no-couple-btn:hover { background: linear-gradient(135deg, #FF8FA3, #FF7A95); transform: translateY(-2px); box-shadow: 0 8px 24px rgba(255,143,163,0.45); }

/* ===== 聊天布局 ===== */
.chat-layout { display: flex; width: 100%; height: calc(100vh - 56px); gap: 0; background: #FFF; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 16px rgba(255,192,203,0.08); }

/* 左侧联系人列表 */
.chat-contacts { width: 220px; min-width: 220px; border-right: 1px solid #FFE6E6; background: #FFFAFA; display: flex; flex-direction: column; }
.contacts-title { display: flex; align-items: center; gap: 8px; padding: 18px 16px; font-size: 16px; font-weight: 600; color: #FF8FA3; border-bottom: 1px solid #FFE6E6; }
.contacts-title-icon { width: 20px; height: 20px; }
.contact-item { display: flex; align-items: center; gap: 10px; padding: 14px 16px; cursor: pointer; transition: all 0.2s ease; border-bottom: 1px solid #FFF5F5; }
.contact-item:hover { background: #FFF0F5; }
.contact-item.active { background: linear-gradient(135deg, #FFE6E6, #FFD1DC); }
.contact-avatar-wrapper { position: relative; flex-shrink: 0; }
.contact-avatar { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; border: 2px solid #FFE6E6; }
.online-dot { position: absolute; bottom: 1px; right: 1px; width: 10px; height: 10px; border-radius: 50%; background: #7ECB76; border: 2px solid #FFF; }
.contact-info { flex: 1; min-width: 0; }
.contact-name { display: block; font-size: 14px; font-weight: 600; color: #555; margin-bottom: 3px; }
.contact-last-msg { display: block; font-size: 12px; color: #BBB; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.contact-meta { display: flex; flex-direction: column; align-items: flex-end; gap: 4px; }
.contact-time { font-size: 11px; color: #CCC; }
.unread-badge { background: #FF8FA3; color: #FFF; font-size: 10px; padding: 2px 6px; border-radius: 10px; font-weight: 600; }

/* 右侧聊天窗口 */
.chat-window { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.chat-header { padding: 14px 20px; border-bottom: 1px solid #FFE6E6; background: #FFF; }
.chat-partner { display: flex; align-items: center; gap: 10px; }
.partner-avatar { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; border: 2px solid #FFE6E6; }
.partner-name { display: block; font-size: 15px; font-weight: 600; color: #555; }
.partner-status { font-size: 12px; color: #7ECB76; }

/* 消息区 */
.chat-messages { flex: 1; padding: 20px; overflow-y: auto; background: #FFFAFA; display: flex; flex-direction: column; gap: 14px; }
.msg-row { display: flex; gap: 8px; align-items: flex-end; }
.msg-row.partner { justify-content: flex-start; }
.msg-row.me { justify-content: flex-end; }
.msg-avatar { width: 30px; height: 30px; border-radius: 50%; object-fit: cover; flex-shrink: 0; }
.msg-bubble { max-width: 65%; padding: 10px 14px; border-radius: 16px; position: relative; }
.msg-bubble.partner { background: #FFF; border: 1px solid #F0E0E0; border-bottom-left-radius: 4px; }
.msg-bubble.me { background: linear-gradient(135deg, #FFB6C1, #FF8FA3); border-bottom-right-radius: 4px; }
.msg-text { font-size: 13px; color: #666; line-height: 1.6; margin-bottom: 4px; }
.msg-bubble.me .msg-text { color: #FFF; }
.msg-time { font-size: 10px; color: #CCC; float: right; }
.msg-bubble.me .msg-time { color: rgba(255,255,255,0.7); }

.empty-chat-hint { display: flex; align-items: center; justify-content: center; flex: 1; }
.empty-chat-hint p { color: #CCC; font-size: 14px; }

/* 输入区 */
.chat-input-area { display: flex; align-items: center; gap: 10px; padding: 14px 20px; border-top: 1px solid #FFE6E6; background: #FFF; }
.chat-input { flex: 1; border: 2px solid #FFE4E9; border-radius: 24px; padding: 10px 18px; font-size: 13px; outline: none; color: #666; background: #FFFAFA; transition: border-color 0.3s; font-family: inherit; }
.chat-input:focus { border-color: #FFB6C1; }
.chat-input::placeholder { color: #CCC; }
.send-btn { width: 38px; height: 38px; border-radius: 50%; background: linear-gradient(135deg, #FFB6C1, #FF8FA3); border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.3s; flex-shrink: 0; box-shadow: 0 4px 12px rgba(255,143,163,0.25); }
.send-btn:hover { background: linear-gradient(135deg, #FF8FA3, #FF7A95); transform: scale(1.05); }
.send-btn svg { width: 16px; height: 16px; }

/* ===== 滚动条 & 响应式 ===== */
.chat-messages::-webkit-scrollbar { width: 5px; }
.chat-messages::-webkit-scrollbar-track { background: transparent; }
.chat-messages::-webkit-scrollbar-thumb { background: #FFD1DC; border-radius: 3px; }
.main-content::-webkit-scrollbar, .right-sidebar::-webkit-scrollbar, .left-sidebar::-webkit-scrollbar { width: 6px; }
.main-content::-webkit-scrollbar-track, .right-sidebar::-webkit-scrollbar-track, .left-sidebar::-webkit-scrollbar-track { background: transparent; }
.main-content::-webkit-scrollbar-thumb, .right-sidebar::-webkit-scrollbar-thumb, .left-sidebar::-webkit-scrollbar-thumb { background: #FFD1DC; border-radius: 3px; }
.main-content::-webkit-scrollbar-thumb:hover, .right-sidebar::-webkit-scrollbar-thumb:hover, .left-sidebar::-webkit-scrollbar-thumb:hover { background: #FFB6C1; }

@media (max-width: 1200px) { .right-sidebar { width: 250px; min-width: 250px; padding: 20px 14px 20px 0; } .main-content { padding: 22px 24px; } }
@media (max-width: 991px) { .left-sidebar { width: 180px; min-width: 180px; } .right-sidebar { display: none; } .main-content { max-width: 100%; } }
@media (max-width: 768px) { .left-sidebar { display: none; } .main-content { padding: 16px; } .chat-contacts { width: 160px; min-width: 160px; } }
</style>
