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
        <p class="no-couple-desc">数据统计需要先绑定情侣关系才能查看</p>
        <button class="no-couple-btn" @click="router.push('/settings')">去绑定</button>
      </div>
      <template v-else>
      <!-- 页面标题 -->
      <div class="stats-header">
        <h2 class="page-title">
          <el-icon><DataAnalysis /></el-icon>
          数据统计
        </h2>
      </div>

      <!-- 顶部数据卡片 -->
      <section class="stats-cards">
        <div class="stat-card">
          <div class="stat-icon-bg love-bg">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.28 2 8.5C2 5.42 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.42 22 8.5C22 12.28 18.6 15.36 13.45 20.03L12 21.35Z" fill="#FFF"/>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-number">{{ statsData.daysTogether }}</span>
            <span class="stat-label">相恋天数</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon-bg check-bg">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z" fill="#FFF"/>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-number">{{ statsData.totalCheckins }}</span>
            <span class="stat-label">累计打卡</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon-bg streak-bg">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z" fill="#FFF"/>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-number">{{ statsData.streak }}</span>
            <span class="stat-label">连续打卡</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon-bg wish-bg">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <polygon points="12,2 15,9 22,9 16,14 18,22 12,17 6,22 8,14 2,9 9,9" fill="#FFF"/>
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-number">{{ statsData.wishRate }}%</span>
            <span class="stat-label">愿望完成率</span>
          </div>
        </div>
      </section>

      <!-- 打卡趋势图表 -->
      <section class="chart-section">
        <div class="chart-card">
          <h3 class="chart-title">
            <el-icon><TrendCharts /></el-icon>
            近7天打卡趋势
          </h3>
          <div class="bar-chart">
            <div v-for="(bar, idx) in chartData" :key="idx" class="bar-column">
              <div class="bar-value">{{ bar.count }}</div>
              <div class="bar-fill-wrapper">
                <div class="bar-fill" :style="{ height: (bar.count / maxCount * 100) + '%' }"></div>
              </div>
              <div class="bar-label">{{ bar.day }}</div>
            </div>
          </div>
        </div>
      </section>

      <!-- 成就徽章 -->
      <section class="achievements-section">
        <h3 class="section-title">
          <svg class="section-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="8" r="5" stroke="#FFB6C1" stroke-width="1.5"/>
            <path d="M12 13V22M8 18l4-4 4 4" stroke="#FFB6C1" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          成就徽章
        </h3>
        <div class="badges-grid">
          <div v-for="badge in badges" :key="badge.id" :class="['badge-card', { unlocked: badge.unlocked }]" @click="openBadgeDetail(badge)">
            <div class="badge-icon" :style="{ background: badge.unlocked ? badge.color : '#E8E8E8' }">
              <el-icon :size="22" :color="badge.unlocked ? '#FFF' : '#CCC'"><component :is="badge.icon" /></el-icon>
            </div>
            <span class="badge-name">{{ badge.name }}</span>
            <span class="badge-desc">{{ badge.unlocked ? badge.desc : '???' }}</span>
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

    <!-- 徽章详情弹窗 -->
    <el-dialog v-model="showBadgeDialog" width="380px" :align-center="true" custom-class="badge-detail-dialog">
      <template #title>
        <div class="dialog-title-row badge-title" v-if="selectedBadge">
          <span :style="{ color: selectedBadge.unlocked ? selectedBadge.color : '#CCC' }">{{ selectedBadge.unlocked ? '🏅' : '🔒' }} {{ selectedBadge.name }}</span>
        </div>
      </template>
      <div class="badge-detail-body" v-if="selectedBadge">
        <div class="badge-detail-icon" :style="{ background: selectedBadge.unlocked ? selectedBadge.color : '#E8E8E8' }">
          <el-icon :size="40" :color="selectedBadge.unlocked ? '#FFF' : '#CCC'"><component :is="selectedBadge.icon" /></el-icon>
        </div>
        <p class="badge-detail-status" :style="{ color: selectedBadge.unlocked ? '#7ECB76' : '#CCC' }">
          {{ selectedBadge.unlocked ? '已获得' : '未获得' }}
        </p>
        <p class="badge-detail-desc">{{ selectedBadge.desc }}</p>
        <div class="badge-detail-condition">
          <span class="condition-label">达成条件</span>
          <span class="condition-text">{{ selectedBadge.condition }}</span>
        </div>
      </div>
      <template #footer>
        <el-button class="dialog-close-btn" @click="showBadgeDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  HomeFilled, Edit, Star, ChatDotRound, DataAnalysis,
  Present, Picture, Setting, TrendCharts, TrophyBase, Medal, Stamp, Timer, Moon
} from '@element-plus/icons-vue'
import { h } from 'vue'
import { coupleApi, statsApi } from '../api/modules/checkin'
import { useSidebar } from '../composables/useSidebar'

const router = useRouter()
const { anniversaries, weatherData, fetchAll: fetchSidebar } = useSidebar()

const hasCouple = ref(true)
const checkCouple = async () => {
  try {
    const res = await coupleApi.getInfo()
    if (res.code === 200 && res.data) hasCouple.value = res.data.hasCouple !== false
  } catch { hasCouple.value = false }
}

const activeNav = ref('stats')
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

// ==================== 统计数据 ====================
const statsData = ref({
  daysTogether: 0,
  totalCheckins: 0,
  streak: 0,
  wishRate: 0
})

const loadStats = async () => {
  try {
    const res = await statsApi.getOverview()
    if (res.code === 200 && res.data) {
      statsData.value = res.data
    }
  } catch (e) {
    console.warn('[Stats] 获取统计数据失败:', e.message)
  }
}

// ==================== 打卡趋势图 ====================
const chartData = ref([])

const maxCount = computed(() => Math.max(...chartData.value.map(b => b.count), 1))

const loadChart = async () => {
  try {
    const res = await statsApi.getChart()
    if (res.code === 200 && res.data) {
      chartData.value = res.data
    }
  } catch (e) {
    console.warn('[Stats] 获取图表数据失败:', e.message)
  }
}

onMounted(async () => {
  await checkCouple()
  fetchSidebar()
  if (hasCouple.value) {
    loadStats()
    loadChart()
  }
})

// ==================== 成就徽章（动态计算） ====================
const badgeDefs = [
  { id: 1, name: '初次打卡', desc: '完成第一次打卡', condition: '累计打卡 ≥ 1 次', color: '#FFB6C1', icon: Stamp, check: (s) => s.totalCheckins >= 1 },
  { id: 2, name: '连续7天', desc: '连续打卡7天', condition: '连续打卡 ≥ 7 天', color: '#FF8FA3', icon: Timer, check: (s) => s.streak >= 7 },
  { id: 3, name: '打卡达人', desc: '累计打卡50次', condition: '累计打卡 ≥ 50 次', color: '#FFD700', icon: Medal, check: (s) => s.totalCheckins >= 50 },
  { id: 4, name: '百日纪念', desc: '相恋超过100天', condition: '相恋天数 ≥ 100 天', color: '#FF8FA3', icon: TrophyBase, check: (s) => s.daysTogether >= 100 },
  { id: 5, name: '愿望达人', desc: '完成10个心愿', condition: '愿望完成率100%且完成数≥10', color: '#FFB6C1', icon: Moon, check: (s) => s.wishRate >= 100 },
  { id: 6, name: '一年之约', desc: '相恋满365天', condition: '相恋天数 ≥ 365 天', color: '#FFD700', icon: Present, check: (s) => s.daysTogether >= 365 },
  { id: 7, name: '满分打卡', desc: '连续打卡30天', condition: '连续打卡 ≥ 30 天', color: '#FFB6C1', icon: Star, check: (s) => s.streak >= 30 },
  { id: 8, name: '永恒之约', desc: '相恋满500天', condition: '相恋天数 ≥ 500 天', color: '#FFD700', icon: Medal, check: (s) => s.daysTogether >= 500 }
]
const badges = computed(() => badgeDefs.map(b => ({
  ...b,
  unlocked: b.check(statsData.value),
  color: b.check(statsData.value) ? b.color : '#E8E8E8'
})))

// ==================== 徽章详情弹窗 ====================
const showBadgeDialog = ref(false)
const selectedBadge = ref(null)
const openBadgeDetail = (badge) => {
  selectedBadge.value = badge
  showBadgeDialog.value = true
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

/* ===== 数据统计主内容 ===== */
.stats-header { margin-bottom: 20px; }
.page-title { display: flex; align-items: center; gap: 10px; font-size: 22px; font-weight: 700; color: #555; }
.page-title .el-icon { color: #FF9999; font-size: 24px; }

/* 数据卡片 */
.stats-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; margin-bottom: 24px; }
.stat-card { background: #FFF; border-radius: 14px; padding: 18px 14px; box-shadow: 0 4px 16px rgba(255,192,203,0.08); text-align: center; transition: all 0.3s ease; }
.stat-card:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(255,192,203,0.15); }
.stat-icon-bg { width: 44px; height: 44px; border-radius: 12px; display: inline-flex; align-items: center; justify-content: center; margin-bottom: 10px; }
.stat-icon-bg svg { width: 22px; height: 22px; }
.love-bg { background: linear-gradient(135deg, #FF8FA3, #FFB6C1); }
.check-bg { background: linear-gradient(135deg, #7ECB76, #A5D6A7); }
.streak-bg { background: linear-gradient(135deg, #FFB74D, #FFD54F); }
.wish-bg { background: linear-gradient(135deg, #BA68C8, #CE93D8); }
.stat-info { display: flex; flex-direction: column; }
.stat-number { font-size: 26px; font-weight: 800; color: #FF8FA3; line-height: 1.2; }
.stat-label { font-size: 12px; color: #999; margin-top: 2px; }

/* 图表 */
.chart-section { margin-bottom: 24px; }
.chart-card { background: #FFF; border-radius: 16px; padding: 22px; box-shadow: 0 4px 16px rgba(255,192,203,0.08); }
.chart-title { display: flex; align-items: center; gap: 8px; font-size: 15px; font-weight: 600; color: #555; margin-bottom: 20px; }
.chart-title .el-icon { color: #FF9999; font-size: 18px; }
.bar-chart { display: flex; justify-content: space-around; align-items: flex-end; height: 180px; gap: 18px; }
.bar-column { display: flex; flex-direction: column; align-items: center; flex: 1; height: 100%; }
.bar-value { font-size: 14px; font-weight: 700; color: #FF8FA3; margin-bottom: 4px; }
.bar-fill-wrapper { flex: 1; width: 100%; max-width: 44px; background: #FFF0F5; border-radius: 10px 10px 0 0; position: relative; display: flex; align-items: flex-end; overflow: hidden; }
.bar-fill { width: 100%; background: linear-gradient(180deg, #FF8FA3, #FFB6C1); border-radius: 10px 10px 0 0; transition: height 0.8s ease-in-out; min-height: 0; }
.bar-label { font-size: 12px; color: #999; margin-top: 6px; }

/* 成就徽章 */
.achievements-section { margin-bottom: 24px; }
.section-title { display: flex; align-items: center; gap: 8px; font-size: 17px; font-weight: 600; color: #555; margin-bottom: 16px; }
.section-icon { width: 22px; height: 22px; }
.badges-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 14px; }
.badge-card { background: #FFF; border-radius: 14px; padding: 18px 12px; box-shadow: 0 4px 16px rgba(255,192,203,0.08); text-align: center; transition: all 0.3s ease; display: flex; flex-direction: column; align-items: center; gap: 8px; cursor: pointer; }
.badge-card:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(255,192,203,0.15); }
.badge-card:not(.unlocked) { opacity: 0.45; }
.badge-card.unlocked { border: 2px solid #FFD1DC; background: linear-gradient(180deg, #FFF 0%, #FFF5F5 100%); }
.badge-icon { width: 44px; height: 44px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #FFF; font-size: 20px; }
.badge-name { font-size: 13px; font-weight: 600; color: #555; }
.badge-desc { font-size: 11px; color: #BBB; }
.badge-card:not(.unlocked) .badge-name { color: #CCC; }

/* 徽章详情弹窗 */
.badge-detail-body { display: flex; flex-direction: column; align-items: center; text-align: center; padding: 10px 0; }
.badge-detail-icon { width: 72px; height: 72px; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-bottom: 14px; }
.badge-detail-status { font-size: 16px; font-weight: 600; margin-bottom: 8px; }
.badge-detail-desc { font-size: 14px; color: #666; margin-bottom: 16px; }
.badge-detail-condition { background: #FFF5F5; border-radius: 12px; padding: 12px 20px; display: flex; flex-direction: column; gap: 4px; width: 100%; }
.condition-label { font-size: 12px; color: #BBB; }
.condition-text { font-size: 14px; font-weight: 600; color: #FF8FA3; }
.dialog-close-btn { background: #F5F5F5; border: 1px solid #E8E8E8; color: #999; border-radius: 8px; padding: 8px 24px; font-weight: 500; transition: all 0.3s; }
.dialog-close-btn:hover { background: #E8E8E8; color: #666; }
:deep(.badge-detail-dialog) { border-radius: 16px !important; overflow: hidden; }
:deep(.badge-detail-dialog .el-dialog__header) { background: #FFFAFA; padding: 20px 24px !important; border-bottom: 1px solid #FFE4E9; }
:deep(.badge-detail-dialog .el-dialog__body) { padding: 24px !important; }
:deep(.badge-detail-dialog .el-dialog__footer) { padding: 16px 24px 24px !important; display: flex; justify-content: center; }
.badge-title { font-size: 16px; font-weight: 600; }

/* ===== 滚动条 & 响应式 ===== */
.main-content::-webkit-scrollbar, .right-sidebar::-webkit-scrollbar, .left-sidebar::-webkit-scrollbar { width: 6px; }
.main-content::-webkit-scrollbar-track, .right-sidebar::-webkit-scrollbar-track, .left-sidebar::-webkit-scrollbar-track { background: transparent; }
.main-content::-webkit-scrollbar-thumb, .right-sidebar::-webkit-scrollbar-thumb, .left-sidebar::-webkit-scrollbar-thumb { background: #FFD1DC; border-radius: 3px; }
.main-content::-webkit-scrollbar-thumb:hover, .right-sidebar::-webkit-scrollbar-thumb:hover, .left-sidebar::-webkit-scrollbar-thumb:hover { background: #FFB6C1; }

@media (max-width: 1200px) { .right-sidebar { width: 250px; min-width: 250px; padding: 20px 14px 20px 0; } .main-content { padding: 22px 24px; } .stats-cards { grid-template-columns: repeat(2, 1fr); } .badges-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 991px) { .left-sidebar { width: 180px; min-width: 180px; } .right-sidebar { display: none; } .main-content { max-width: 100%; } }
@media (max-width: 768px) { .left-sidebar { display: none; } .main-content { padding: 16px; max-width: 100%; } .stats-cards { grid-template-columns: repeat(2, 1fr); } .badges-grid { grid-template-columns: repeat(2, 1fr); } }
</style>
