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
      <div v-if="!hasCouple" class="no-couple-card">
        <svg class="no-couple-icon" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M24 42.7L21.1 38.4C10.8 29.1 4 22.9 4 15.5C4 9.3 9.8 4.5 16 4.5C19.5 4.5 22.7 6.3 24 8.6C25.3 6.3 28.5 4.5 32 4.5C38.2 4.5 44 9.3 44 15.5C44 22.9 37.2 29.1 26.9 38.4L24 42.7Z" fill="#FFE4E9" stroke="#FFB6C1" stroke-width="1.5"/>
        </svg>
        <p class="no-couple-title">尚未绑定情侣关系</p>
        <p class="no-couple-desc">愿望清单需要先绑定情侣关系才能使用</p>
        <button class="no-couple-btn" @click="router.push('/settings')">去绑定</button>
      </div>
      <template v-else>
      <!-- 顶部总进度 -->
      <section class="progress-section">
        <div class="progress-header">
          <h2 class="page-title">
            <svg class="title-star" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <polygon points="12,2 15,9 22,9 16,14 18,22 12,17 6,22 8,14 2,9 9,9" fill="url(#starGrad)"/>
              <defs><linearGradient id="starGrad" x1="12" y1="2" x2="12" y2="22" gradientUnits="userSpaceOnUse"><stop stop-color="#FFD700"/><stop offset="1" stop-color="#FFEC8B"/></linearGradient></defs>
            </svg>
            愿望清单
          </h2>
          <button class="add-wish-btn" @click="openAddDialog">
            <el-icon><Plus /></el-icon>
            添加心愿
          </button>
        </div>
        <div class="progress-card">
          <div class="progress-info">
            <span class="progress-text">已完成 <strong>{{ completedCount }}</strong> / {{ wishlist.length }}</span>
            <span class="progress-percent">{{ completionPercent }}%</span>
          </div>
          <div class="progress-bar-wrapper">
            <div class="progress-bar" :style="{ width: completionPercent + '%' }"></div>
          </div>
        </div>
      </section>

      <!-- 心愿卡片列表 -->
      <section class="wishlist-grid">
        <div v-for="item in wishlist" :key="item.id" :class="['wish-card', { completed: item.completed }]">
          <div class="wish-card-top">
            <div class="wish-check" @click="toggleWish(item)">
              <svg v-if="item.completed" class="check-icon done" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" fill="#7ECB76" stroke="#5BAF4F" stroke-width="1.5"/>
                <path d="M7 12l3 3 7-7" stroke="#FFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <svg v-else class="check-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="12" r="10" stroke="#DDD" stroke-width="1.5"/>
              </svg>
            </div>
            <div class="wish-info">
              <h4 :class="['wish-title', { done: item.completed }]">{{ item.title }}</h4>
              <p class="wish-desc">{{ item.description }}</p>
              <span class="wish-date">{{ item.createTime }}</span>
            </div>
          </div>
          <div class="wish-card-bottom">
            <span :class="['wish-status-tag', item.completed ? 'completed' : 'pending']">
              {{ item.completed ? '已完成' : '未完成' }}
            </span>
            <button class="wish-delete-btn" @click="deleteWish(item.id)">
              <el-icon><Delete /></el-icon>
            </button>
          </div>
        </div>
      </section>
      </template>
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

    <!-- ==================== 添加心愿弹窗 ==================== -->
    <el-dialog v-model="showDialog" width="480px" :align-center="true" custom-class="wish-dialog">
      <template #title>
        <div class="dialog-title-row">
          <svg class="dialog-title-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <polygon points="12,2 15,9 22,9 16,14 18,22 12,17 6,22 8,14 2,9 9,9" fill="url(#dlgStar)"/>
            <defs><linearGradient id="dlgStar" x1="12" y1="2" x2="12" y2="22" gradientUnits="userSpaceOnUse"><stop stop-color="#FFD700"/><stop offset="1" stop-color="#FFEC8B"/></linearGradient></defs>
          </svg>
          <span>添加心愿</span>
        </div>
      </template>
      <div class="dialog-body">
        <el-form :model="wishForm" label-width="70px">
          <el-form-item label="心愿标题">
            <el-input v-model="wishForm.title" placeholder="请输入心愿标题" class="wish-input" />
          </el-form-item>
          <el-form-item label="心愿描述">
            <el-input v-model="wishForm.description" type="textarea" :rows="3" placeholder="描述一下这个心愿..." class="wish-textarea" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button class="dialog-cancel-btn" @click="showDialog = false">取消</el-button>
        <el-button class="dialog-submit-btn" @click="addWish">确认添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  HomeFilled, Edit, Star, ChatDotRound, DataAnalysis,
  Present, Picture, Setting, Plus, Delete
} from '@element-plus/icons-vue'
import { coupleApi, wishlistApi } from '../api/modules/checkin'
import { useSidebar } from '../composables/useSidebar'

const router = useRouter()
const { anniversaries, weatherData, fetchAll: fetchSidebar } = useSidebar()

const activeNav = ref('wishlist')
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

// ==================== 愿望清单数据 ====================
const hasCouple = ref(true)
const wishlist = ref([])
const loading = ref(false)

const checkCouple = async () => {
  try {
    const res = await coupleApi.getInfo()
    if (res.code === 200 && res.data) {
      hasCouple.value = res.data.hasCouple !== false
    }
  } catch { hasCouple.value = false }
}

const completedCount = computed(() => wishlist.value.filter(w => w.completed).length)
const completionPercent = computed(() => wishlist.value.length ? Math.round((completedCount.value / wishlist.value.length) * 100) : 0)

const loadWishlist = async () => {
  loading.value = true
  try {
    const res = await wishlistApi.getInfo()
    if (res.code === 200 && res.data && res.data.items) {
      wishlist.value = res.data.items
    }
  } catch (e) {
    console.warn('[WishList] 获取愿望清单失败:', e.message)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await checkCouple()
  fetchSidebar()
  if (hasCouple.value) loadWishlist()
})

// ==================== 愿望清单操作 ====================
const toggleWish = async (item) => {
  try {
    const res = await wishlistApi.update({ action: 'toggle', wishId: item.id })
    if (res.code === 200) {
      item.completed = !item.completed
    }
  } catch (e) {
    ElMessage.error('操作失败: ' + (e.message || '网络错误'))
  }
}

const deleteWish = async (id) => {
  try {
    const res = await wishlistApi.update({ action: 'delete', wishId: id })
    if (res.code === 200) {
      loadWishlist()
      ElMessage.success('心愿已删除')
    }
  } catch (e) {
    ElMessage.error('删除失败: ' + (e.message || '网络错误'))
  }
}

const showDialog = ref(false)
const wishForm = ref({ title: '', description: '' })

const openAddDialog = () => {
  wishForm.value = { title: '', description: '' }
  showDialog.value = true
}

const addWish = async () => {
  if (!wishForm.value.title.trim()) return
  try {
    const res = await wishlistApi.update({
      action: 'add',
      title: wishForm.value.title.trim(),
      description: wishForm.value.description || '期待实现的那一天~'
    })
    if (res.code === 200) {
      loadWishlist()
      ElMessage.success('心愿添加成功')
      showDialog.value = false
    }
  } catch (e) {
    ElMessage.error('添加失败: ' + (e.message || '网络错误'))
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
.no-couple-card { display: flex; flex-direction: column; align-items: center; justify-content: center; text-align: center; padding: 60px 40px; background: #FFF; border-radius: 20px; box-shadow: 0 8px 30px rgba(255,192,203,0.12); margin-top: 40px; }
.no-couple-icon { width: 80px; height: 80px; margin-bottom: 20px; }
.no-couple-title { font-size: 20px; font-weight: 700; color: #888; margin-bottom: 10px; }
.no-couple-desc { font-size: 14px; color: #BBB; line-height: 1.6; margin-bottom: 28px; max-width: 320px; }
.no-couple-btn { display: inline-flex; align-items: center; gap: 6px; padding: 12px 36px; background: linear-gradient(135deg, #FFB6C1, #FF8FA3); border: none; border-radius: 28px; color: #FFF; font-size: 15px; font-weight: 600; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 4px 16px rgba(255,143,163,0.3); }
.no-couple-btn:hover { background: linear-gradient(135deg, #FF8FA3, #FF7A95); transform: translateY(-2px); box-shadow: 0 8px 24px rgba(255,143,163,0.45); }
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

/* ===== 愿望清单主内容区 ===== */
.progress-section { margin-bottom: 24px; }
.page-title { display: flex; align-items: center; gap: 10px; font-size: 22px; font-weight: 700; color: #555; margin-bottom: 0; }
.title-star { width: 24px; height: 24px; }
.progress-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.add-wish-btn { display: inline-flex; align-items: center; gap: 6px; padding: 10px 22px; background: linear-gradient(135deg, #FF8FA3, #FFB6C1); border: none; border-radius: 50px; color: #FFF; font-size: 14px; font-weight: 600; cursor: pointer; box-shadow: 0 4px 14px rgba(255,143,163,0.3); transition: all 0.3s ease; }
.add-wish-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255,143,163,0.4); background: linear-gradient(135deg, #FF7A95, #FF8FA3); }
.progress-card { background: #FFF; border-radius: 16px; padding: 20px 22px; box-shadow: 0 4px 16px rgba(255,192,203,0.08); }
.progress-info { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 10px; }
.progress-text { font-size: 14px; color: #666; }
.progress-text strong { color: #FF8FA3; font-size: 24px; margin: 0 2px; }
.progress-percent { font-size: 22px; font-weight: 700; color: #FF8FA3; }
.progress-bar-wrapper { width: 100%; height: 10px; background: #FFF0F5; border-radius: 10px; overflow: hidden; }
.progress-bar { height: 100%; background: linear-gradient(90deg, #FFB6C1, #FF8FA3); border-radius: 10px; transition: width 0.6s ease-in-out; }

/* 愿望卡片网格 */
.wishlist-grid { display: flex; flex-direction: column; gap: 14px; }
.wish-card { background: #FFF; border-radius: 14px; padding: 18px 20px; box-shadow: 0 4px 16px rgba(255,192,203,0.08); transition: all 0.3s ease; border: 2px solid transparent; display: flex; flex-direction: column; gap: 14px; }
.wish-card:hover { box-shadow: 0 8px 24px rgba(255,192,203,0.15); transform: translateY(-2px); }
.wish-card.completed { border-color: #E8F5E9; background: #FAFFFA; }
.wish-card-top { display: flex; gap: 14px; }
.wish-check { cursor: pointer; flex-shrink: 0; padding-top: 2px; }
.check-icon { width: 24px; height: 24px; transition: transform 0.2s ease; }
.check-icon.done { animation: checkPop 0.3s ease; }
@keyframes checkPop { 0% { transform: scale(0.8); } 50% { transform: scale(1.15); } 100% { transform: scale(1); } }
.wish-info { flex: 1; min-width: 0; }
.wish-title { font-size: 15px; font-weight: 600; color: #555; margin-bottom: 6px; }
.wish-title.done { color: #BBB; text-decoration: line-through; }
.wish-desc { font-size: 13px; color: #999; line-height: 1.6; margin-bottom: 6px; }
.wish-date { font-size: 11px; color: #CCC; }
.wish-card-bottom { display: flex; justify-content: space-between; align-items: center; padding-top: 10px; border-top: 1px solid #F8F0F0; }
.wish-status-tag { font-size: 12px; padding: 4px 12px; border-radius: 20px; font-weight: 500; }
.wish-status-tag.completed { background: #E8F5E9; color: #4CAF50; }
.wish-status-tag.pending { background: #F5F5F5; color: #BBB; }
.wish-delete-btn { background: none; border: none; color: #DDD; cursor: pointer; padding: 4px; border-radius: 6px; transition: all 0.2s; }
.wish-delete-btn:hover { color: #FF6B6B; background: #FFF0F0; }

/* ===== 弹窗 ===== */
.dialog-title-row { display: flex; align-items: center; gap: 8px; color: #FF8FA3; font-size: 18px; font-weight: 600; }
.dialog-title-icon { width: 24px; height: 24px; }
.dialog-body { padding: 8px 0; }
.wish-input :deep(.el-input__wrapper) { border-radius: 10px; border: 2px solid #FFE4E9; transition: all 0.3s; }
.wish-input :deep(.el-input__wrapper):hover { border-color: #FFB6C1; }
.wish-input :deep(.el-input__wrapper).is-focus { border-color: #FF9999; box-shadow: 0 0 0 3px rgba(255,153,153,0.1); }
.wish-textarea :deep(.el-textarea__inner) { border-radius: 10px; border: 2px solid #FFE4E9; background: #FFFAFA; resize: vertical; transition: all 0.3s; }
.wish-textarea :deep(.el-textarea__inner):hover { border-color: #FFB6C1; }
.wish-textarea :deep(.el-textarea__inner):focus { border-color: #FF9999; box-shadow: 0 0 0 3px rgba(255,153,153,0.1); }
.dialog-cancel-btn { background: #F5F5F5; border: 1px solid #E8E8E8; color: #999; border-radius: 8px; padding: 8px 20px; font-weight: 500; transition: all 0.3s; }
.dialog-cancel-btn:hover { background: #E8E8E8; color: #666; }
.dialog-submit-btn { background: linear-gradient(135deg, #FF8FA3, #FFB6C1); border: none; color: #FFF; border-radius: 8px; padding: 8px 20px; font-weight: 600; box-shadow: 0 4px 12px rgba(255,143,163,0.3); transition: all 0.3s; }
.dialog-submit-btn:hover { background: linear-gradient(135deg, #FF7A95, #FF8FA3); transform: translateY(-1px); box-shadow: 0 6px 16px rgba(255,143,163,0.4); }
:deep(.wish-dialog) { border-radius: 16px !important; overflow: hidden; }
:deep(.wish-dialog .el-dialog__header) { background: linear-gradient(135deg, #FFF0F5, #FFE6E9); padding: 20px 24px !important; border-bottom: 1px solid #FFE4E9; }
:deep(.wish-dialog .el-dialog__body) { padding: 24px !important; }
:deep(.wish-dialog .el-dialog__footer) { padding: 16px 24px 24px !important; display: flex; justify-content: flex-end; gap: 12px; }

/* ===== 滚动条 & 响应式 ===== */
.main-content::-webkit-scrollbar, .right-sidebar::-webkit-scrollbar, .left-sidebar::-webkit-scrollbar { width: 6px; }
.main-content::-webkit-scrollbar-track, .right-sidebar::-webkit-scrollbar-track, .left-sidebar::-webkit-scrollbar-track { background: transparent; }
.main-content::-webkit-scrollbar-thumb, .right-sidebar::-webkit-scrollbar-thumb, .left-sidebar::-webkit-scrollbar-thumb { background: #FFD1DC; border-radius: 3px; }
.main-content::-webkit-scrollbar-thumb:hover, .right-sidebar::-webkit-scrollbar-thumb:hover, .left-sidebar::-webkit-scrollbar-thumb:hover { background: #FFB6C1; }

@media (max-width: 1200px) { .right-sidebar { width: 250px; min-width: 250px; padding: 20px 14px 20px 0; } .main-content { padding: 22px 24px; } }
@media (max-width: 991px) { .left-sidebar { width: 180px; min-width: 180px; } .right-sidebar { display: none; } .main-content { max-width: 100%; } }
@media (max-width: 768px) { .left-sidebar { display: none; } .main-content { padding: 16px; max-width: 100%; } }
</style>
