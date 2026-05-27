<template>
  <div class="love-stats-page">
    <!-- 返回按钮 -->
    <button class="back-btn" @click="navigateTo('/')">
      ← 返回首页
    </button>

    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">统计中心</h1>
      <p class="page-subtitle">记录我们的恋爱点滴</p>
    </div>

    <!-- 社交统计卡片 -->
    <div class="stats-section">
      <h2 class="section-title">
        <svg class="section-icon" viewBox="0 0 24 24" width="24" height="24">
          <path d="M20 2H4C2.9 2 2 2.9 2 4v12c0 1.1.9 2 2 2h14l4 4V4c0-1.1-.9-2-2-2z" fill="#FF8FA3" opacity="0.9"/>
          <circle cx="8" cy="10" r="1.5" fill="white" opacity="0.8"/>
          <circle cx="12" cy="10" r="1.5" fill="white" opacity="0.8"/>
          <circle cx="16" cy="10" r="1.5" fill="white" opacity="0.8"/>
        </svg>
        社交互动
      </h2>
      <div class="stats-grid">
        <!-- 帖子 -->
        <div class="stat-card social-card">
          <svg class="stat-icon-svg" viewBox="0 0 24 24" width="28" height="28">
            <rect x="4" y="2" width="16" height="20" rx="3" fill="#FFB6C1"/>
            <rect x="8" y="6" width="8" height="1.5" rx="0.75" fill="#fff" opacity="0.9"/>
            <rect x="8" y="10" width="8" height="1.5" rx="0.75" fill="#fff" opacity="0.9"/>
            <rect x="8" y="14" width="7" height="1.5" rx="0.75" fill="#fff" opacity="0.6"/>
            <rect x="8" y="18" width="5" height="1.5" rx="0.75" fill="#fff" opacity="0.6"/>
            <path d="M15 2l0 5c0 .6.4 1 1 1h4l-5-6z" fill="#FF8FA3"/>
          </svg>
          <div class="stat-value">{{ statistics.postCount || 0 }}</div>
          <div class="stat-label">帖子</div>
        </div>
        <!-- 获赞 -->
        <div class="stat-card social-card">
          <svg class="stat-icon-svg" viewBox="0 0 24 24" width="28" height="28">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" fill="url(#heartGradient)"/>
            <defs>
              <linearGradient id="heartGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" stop-color="#FF8FA3"/>
                <stop offset="100%" stop-color="#FFB6C1"/>
              </linearGradient>
            </defs>
          </svg>
          <div class="stat-value">{{ statistics.likeCount || 0 }}</div>
          <div class="stat-label">获赞</div>
        </div>
        <!-- 评论 -->
        <div class="stat-card social-card">
          <svg class="stat-icon-svg" viewBox="0 0 24 24" width="28" height="28">
            <path d="M20 2H4C2.9 2 2 2.9 2 4v12c0 1.1.9 2 2 2h14l4 4V4c0-1.1-.9-2-2-2z" fill="url(#commentGradient)"/>
            <circle cx="8" cy="10" r="1.5" fill="#fff" opacity="0.8"/>
            <circle cx="12" cy="10" r="1.5" fill="#fff" opacity="0.8"/>
            <circle cx="16" cy="10" r="1.5" fill="#fff" opacity="0.8"/>
            <defs>
              <linearGradient id="commentGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" stop-color="#FF8FA3"/>
                <stop offset="100%" stop-color="#FFB6C1"/>
              </linearGradient>
            </defs>
          </svg>
          <div class="stat-value">{{ statistics.commentCount || 0 }}</div>
          <div class="stat-label">评论</div>
        </div>
        <!-- 收藏 -->
        <div class="stat-card social-card">
          <svg class="stat-icon-svg" viewBox="0 0 24 24" width="28" height="28">
            <path d="M17 3H7c-1.1 0-2 .9-2 2v16l7-3 7 3V5c0-1.1-.9-2-2-2z" fill="url(#collectGradient)"/>
            <defs>
              <linearGradient id="collectGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" stop-color="#FF8FA3"/>
                <stop offset="100%" stop-color="#FFB6C1"/>
              </linearGradient>
            </defs>
          </svg>
          <div class="stat-value">{{ statistics.collectCount || 0 }}</div>
          <div class="stat-label">收藏</div>
        </div>
        <!-- 粉丝 -->
        <div class="stat-card social-card">
          <svg class="stat-icon-svg" viewBox="0 0 24 24" width="28" height="28">
            <circle cx="9" cy="8" r="4" fill="url(#followerGradient)"/>
            <circle cx="17" cy="8" r="3" fill="url(#followerGradient2)"/>
            <path d="M1 20v-1c0-2.8 2.2-5 5-5h6c1.5 0 2.9.5 4 1.4" fill="none" stroke="#FF8FA3" stroke-width="2" stroke-linecap="round"/>
            <defs>
              <linearGradient id="followerGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" stop-color="#FF8FA3"/>
                <stop offset="100%" stop-color="#FFB6C1"/>
              </linearGradient>
              <linearGradient id="followerGradient2" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" stop-color="#FFB6C1"/>
                <stop offset="100%" stop-color="#FFE4E9"/>
              </linearGradient>
            </defs>
          </svg>
          <div class="stat-value">{{ statistics.followerCount || 0 }}</div>
          <div class="stat-label">粉丝</div>
        </div>
        <!-- 关注 -->
        <div class="stat-card social-card">
          <svg class="stat-icon-svg" viewBox="0 0 24 24" width="28" height="28">
            <circle cx="12" cy="8" r="4" fill="url(#followingGradient)"/>
            <path d="M4 20v-1c0-2.8 2.2-5 5-5h6c1.5 0 2.9.5 4 1.4" fill="none" stroke="#FF8FA3" stroke-width="2" stroke-linecap="round"/>
            <circle cx="20" cy="8" r="3" fill="#FFB6C1"/>
            <line x1="20" y1="6" x2="20" y2="10" stroke="#fff" stroke-width="1.5" stroke-linecap="round"/>
            <line x1="18" y1="8" x2="22" y2="8" stroke="#fff" stroke-width="1.5" stroke-linecap="round"/>
            <defs>
              <linearGradient id="followingGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" stop-color="#FF8FA3"/>
                <stop offset="100%" stop-color="#FFB6C1"/>
              </linearGradient>
            </defs>
          </svg>
          <div class="stat-value">{{ statistics.followingCount || 0 }}</div>
          <div class="stat-label">关注</div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <div class="charts-header">
        <h2 class="section-title">
          <svg class="section-icon" viewBox="0 0 24 24" width="24" height="24">
            <rect x="2" y="12" width="5" height="8" rx="1.5" fill="#FFB6C1"/>
            <rect x="9.5" y="7" width="5" height="13" rx="1.5" fill="#FF8FA3"/>
            <rect x="17" y="3" width="5" height="17" rx="1.5" fill="#FFB6C1"/>
          </svg>
          数据趋势
        </h2>
        <div class="time-range-selector">
          <label>选择时间范围：</label>
          <select v-model="selectedRange" class="range-select">
            <option value="6">最近6个月</option>
            <option value="12">最近12个月</option>
          </select>
        </div>
      </div>
      <div class="charts-grid">
        <!-- 近7天活跃度折线图 -->
        <div class="chart-card">
          <h3 class="chart-title">
            <svg class="chart-title-icon" viewBox="0 0 20 20" width="18" height="18">
              <polyline points="2,15 7,9 11,12 16,5 18,7" fill="none" stroke="#FF8FA3" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="2" cy="15" r="1.5" fill="#FFB6C1"/>
              <circle cx="7" cy="9" r="1.5" fill="#FFB6C1"/>
              <circle cx="11" cy="12" r="1.5" fill="#FFB6C1"/>
              <circle cx="16" cy="5" r="1.5" fill="#FFB6C1"/>
              <circle cx="18" cy="7" r="1.5" fill="#FF8FA3"/>
            </svg>
            近7天活跃度
          </h3>
          <div class="chart-container">
            <svg viewBox="0 0 400 200" class="line-chart">
              <defs>
                <linearGradient id="lineGradient" x1="0%" y1="0%" x2="0%" y2="100%">
                  <stop offset="0%" style="stop-color:#FFB6C1;stop-opacity:0.35" />
                  <stop offset="100%" style="stop-color:#FFB6C1;stop-opacity:0" />
                </linearGradient>
              </defs>
              <path :d="areaPath" fill="url(#lineGradient)" />
              <path :d="linePath" fill="none" stroke="#FF8FA3" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" />
              <circle v-for="(point, index) in chartPoints" :key="index"
                :cx="point.x" :cy="point.y" r="4" fill="#FF8FA3" opacity="0.8" class="chart-point" />
              <text v-for="(value, index) in (browseData.length > 0 ? browseData : weeklyActivity.data)" :key="'val-'+index"
                :x="chartPoints[index]?.x" :y="chartPoints[index]?.y - 10" 
                class="chart-value" text-anchor="middle">{{ value }}</text>
              <text v-for="(label, index) in (browseLabels.length > 0 ? browseLabels : weeklyActivity.labels)" :key="index"
                :x="(index * 57) + 25" y="190" class="chart-label">{{ label }}</text>
            </svg>
          </div>
        </div>

        <!-- 月度发帖柱状图 -->
        <div class="chart-card">
          <h3 class="chart-title">
            <svg class="chart-title-icon" viewBox="0 0 20 20" width="18" height="18">
              <rect x="2" y="9" width="3.5" height="9" rx="1" fill="#FFB6C1"/>
              <rect x="7.5" y="5" width="3.5" height="13" rx="1" fill="#FF8FA3"/>
              <rect x="13" y="1" width="3.5" height="17" rx="1" fill="#FFB6C1"/>
            </svg>
            月度发帖统计
          </h3>
          <div class="chart-container">
            <svg :viewBox="barChartViewBox" class="bar-chart">
              <rect v-for="(value, index) in displayMonthlyData" :key="index"
                :x="barXPositions[index]" :y="180 - (value * barHeightMultiplier)"
                :width="barWidth" :height="value * barHeightMultiplier"
                rx="4" ry="4" class="bar" :style="{ animationDelay: index * 0.04 + 's' }" />
              <text v-for="(value, index) in displayMonthlyData" :key="'bar-val-'+index"
                :x="barXPositions[index] + barWidth / 2" :y="180 - (value * barHeightMultiplier) - 8" 
                class="bar-value" text-anchor="middle">{{ value }}</text>
              <text v-for="(label, index) in displayMonthlyLabels" :key="index"
                :x="barXPositions[index] + barWidth / 2" y="195" 
                class="chart-label" 
                :class="{ 'chart-label-small': selectedRange === '12' }">
                {{ label }}</text>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品模块预留 -->
    <div class="stats-section product-section">
      <h2 class="section-title">
        <svg class="section-icon" viewBox="0 0 24 24" width="24" height="24">
          <rect x="3" y="8" width="18" height="13" rx="3" fill="none" stroke="#FFB6C1" stroke-width="2"/>
          <path d="M3 8l2.5-6h13L21 8" fill="none" stroke="#FFB6C1" stroke-width="2" stroke-linecap="round"/>
          <line x1="12" y1="8" x2="12" y2="21" stroke="#FFB6C1" stroke-width="1.5" opacity="0.5"/>
          <circle cx="12" cy="15" r="2" fill="#FF8FA3" opacity="0.7"/>
          <path d="M7 8v-3" stroke="#FFB6C1" stroke-width="2" stroke-linecap="round"/>
        </svg>
        恋爱商城 <span class="coming-soon">即将上线</span>
      </h2>
      <div class="stats-grid product-grid">
        <div class="stat-card placeholder-card">
          <svg class="stat-icon-svg placeholder-icon" viewBox="0 0 24 24" width="28" height="28">
            <circle cx="9" cy="21" r="2" fill="#c0b8b8"/>
            <circle cx="20" cy="21" r="2" fill="#c0b8b8"/>
            <path d="M1 1h4l2.68 13.39a2 2 0 002 1.61h9.72a2 2 0 002-1.61L23 6H6" fill="none" stroke="#bbb0b0" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <div class="stat-value">{{ statistics.productCount || 0 }}</div>
          <div class="stat-label">商品</div>
        </div>
        <div class="stat-card placeholder-card">
          <svg class="stat-icon-svg placeholder-icon" viewBox="0 0 24 24" width="28" height="28">
            <rect x="2" y="3" width="20" height="18" rx="3" fill="none" stroke="#bbb0b0" stroke-width="2"/>
            <line x1="2" y1="9" x2="22" y2="9" stroke="#bbb0b0" stroke-width="2"/>
            <line x1="12" y1="9" x2="12" y2="21" stroke="#bbb0b0" stroke-width="1.5" opacity="0.5"/>
          </svg>
          <div class="stat-value">{{ statistics.orderCount || 0 }}</div>
          <div class="stat-label">订单</div>
        </div>
        <div class="stat-card placeholder-card">
          <svg class="stat-icon-svg placeholder-icon" viewBox="0 0 24 24" width="28" height="28">
            <circle cx="12" cy="12" r="10" fill="none" stroke="#bbb0b0" stroke-width="2"/>
            <text x="12" y="16" text-anchor="middle" font-size="14" font-weight="bold" fill="#bbb0b0">¥</text>
          </svg>
          <div class="stat-value">¥{{ statistics.totalSpent || 0 }}</div>
          <div class="stat-label">消费</div>
        </div>
        <div class="stat-card placeholder-card">
          <svg class="stat-icon-svg placeholder-icon" viewBox="0 0 24 24" width="28" height="28">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" fill="#d5cdcd"/>
          </svg>
          <div class="stat-value">{{ statistics.wishlistCount || 0 }}</div>
          <div class="stat-label">心愿单</div>
        </div>
      </div>
    </div>

    <!-- 温馨寄语 -->
    <div class="message-section">
      <div class="message-card">
        <h3>
          <svg class="section-icon" viewBox="0 0 24 24" width="22" height="22">
            <rect x="2" y="4" width="20" height="16" rx="3" fill="#FFB6C1" opacity="0.7"/>
            <polygon points="12,15 3,20 3,7" fill="#FF8FA3" opacity="0.5"/>
            <polygon points="12,15 21,20 21,7" fill="#FFB6C1" opacity="0.5"/>
          </svg>
          温馨寄语
        </h3>
        <p>每一段爱情都值得被记录，每一个瞬间都值得被珍藏。</p>
        <p>在这里，留下你们最美好的回忆吧～</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { statisticsApi } from '../api/modules/statistics'

const router = useRouter()
const statistics = ref({})
const weeklyActivity = ref({ labels: [], data: [0, 0, 0, 0, 0, 0, 0], title: '', type: '' })
const dailyBrowseStats = ref([])
const monthlyPost = ref({ labels: [], data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0], title: '', type: '' })
const selectedRange = ref('6')

const browseLabels = computed(() => {
  return dailyBrowseStats.value.map(item => {
    const date = item.date
    return date ? date.substring(5) : ''
  })
})

const browseData = computed(() => {
  return dailyBrowseStats.value.map(item => item.count || 0)
})

const chartPoints = computed(() => {
  const data = browseData.value.length > 0 ? browseData.value : weeklyActivity.value.data
  const maxVal = Math.max(...data, 1)
  return data.map((val, index) => ({
    x: index * 57 + 25,
    y: 180 - (val / maxVal) * 150
  }))
})

const linePath = computed(() => {
  if (chartPoints.value.length === 0) return ''
  return chartPoints.value.map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`).join(' ')
})

const areaPath = computed(() => {
  if (chartPoints.value.length === 0) return ''
  const line = chartPoints.value.map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`).join(' ')
  const lastX = chartPoints.value[chartPoints.value.length - 1].x
  const firstX = chartPoints.value[0].x
  return `${line} L ${lastX} 180 L ${firstX} 180 Z`
})

const displayMonthlyData = computed(() => {
  const range = parseInt(selectedRange.value)
  const data = monthlyPost.value.data || []
  return data.slice(-range)
})

const displayMonthlyLabels = computed(() => {
  const range = parseInt(selectedRange.value)
  const labels = monthlyPost.value.labels || []
  return labels.slice(-range)
})

const barWidth = computed(() => {
  const range = parseInt(selectedRange.value)
  if (range === 6) return 50
  if (range === 12) return 25
  return 30
})

const barXPositions = computed(() => {
  const range = parseInt(selectedRange.value)
  const totalWidth = range === 12 ? 560 : 450
  const spacing = (totalWidth - (barWidth.value * range)) / (range + 1)
  return Array.from({ length: range }, (_, i) => spacing + i * (barWidth.value + spacing))
})

const barChartViewBox = computed(() => {
  const range = parseInt(selectedRange.value)
  const width = range === 12 ? 600 : 500
  return `0 0 ${width} 200`
})

const barHeightMultiplier = computed(() => {
  const maxVal = Math.max(...displayMonthlyData.value, 1)
  return maxVal > 0 ? 150 / maxVal : 10
})

const navigateTo = (path) => {
  router.push(path)
}

const loadStatistics = async () => {
  try {
    const [statsRes, weeklyRes, monthlyRes, browseRes] = await Promise.all([
      statisticsApi.getStatistics(),
      statisticsApi.getWeeklyActivity(),
      statisticsApi.getMonthlyPost(),
      statisticsApi.getDailyBrowseStats(7)
    ])
    
    statistics.value = statsRes.data
    weeklyActivity.value = weeklyRes.data
    monthlyPost.value = monthlyRes.data
    dailyBrowseStats.value = browseRes.data || []
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
/* ==================== 基础配色 — 白色系 + 系统粉色 ==================== */
.love-stats-page {
  --bg-page: #FFF8F8;
  --bg-card: #FFFFFF;
  --accent: #FF8FA3;
  --accent-soft: #FFB6C1;
  --accent-light: #FFE4E9;
  --text-primary: #4a4a4a;
  --text-secondary: #9b8e8e;
  --text-muted: #b8a8a8;
  --shadow-pink: rgba(255, 143, 163, 0.15);
  --shadow-pink-hover: rgba(255, 143, 163, 0.25);
  --radius-lg: 24px;
  --radius-md: 18px;
  --radius-sm: 12px;
  --font-family: "PingFang SC", "Microsoft YaHei", "Helvetica Neue", system-ui, -apple-system, sans-serif;

  min-height: 100vh;
  background: var(--bg-page);
  padding: 32px 36px;
  animation: pageIn 0.55s cubic-bezier(0.22, 0.61, 0.36, 1);
  position: relative;
  font-family: var(--font-family);
  color: var(--text-primary);
}

@keyframes pageIn {
  from { opacity: 0; transform: translateY(24px); }
  to   { opacity: 1; transform: translateY(0); }
}

/* ==================== 顶部导航：返回按钮 ==================== */
.back-btn {
  position: absolute;
  top: 32px;
  left: 36px;
  padding: 12px 26px;
  background: linear-gradient(135deg, #FFE0E5 0%, #FFD0D8 40%, #FFDCE4 100%);
  color: #d4687c;
  border: none;
  border-radius: 50px;
  font-size: 0.95rem;
  font-weight: 600;
  letter-spacing: 0.02em;
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow:
    0 4px 16px rgba(255, 143, 163, 0.2),
    0 2px 6px rgba(255, 143, 163, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.7);
}

.back-btn:hover {
  transform: translateY(-2px);
  background: linear-gradient(135deg, #FFDCE4 0%, #FFC8D3 40%, #FFD4DE 100%);
  box-shadow:
    0 8px 28px rgba(255, 143, 163, 0.3),
    0 4px 12px rgba(255, 143, 163, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

.back-btn:active {
  transform: translateY(0) scale(0.97);
  box-shadow:
    0 2px 8px rgba(255, 143, 163, 0.15),
    inset 0 2px 4px rgba(220, 140, 150, 0.12);
  transition: all 0.1s ease;
}

/* ==================== 页面标题 ==================== */
.page-header {
  text-align: center;
  margin: 0 auto 36px;
  padding-top: 60px;
}

.page-title {
  font-size: 2.6rem;
  font-weight: 700;
  color: #4a4040;
  margin-bottom: 8px;
  letter-spacing: 0.04em;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.8);
}

.page-subtitle {
  color: var(--text-secondary);
  font-size: 1.05rem;
  font-weight: 400;
  letter-spacing: 0.03em;
}

/* ==================== 通用段落 ==================== */
.stats-section {
  margin-bottom: 36px;
}

.section-title {
  font-size: 1.35rem;
  font-weight: 650;
  color: #4a4040;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  letter-spacing: 0.03em;
}

.coming-soon {
  font-size: 0.75rem;
  color: var(--accent);
  background: var(--accent-light);
  padding: 3px 12px;
  border-radius: 50px;
  font-weight: 500;
  letter-spacing: 0.02em;
}

/* SVG 图标 */
.stat-icon-svg,
.section-icon,
.chart-title-icon {
  flex-shrink: 0;
}

.stat-icon-svg { margin-bottom: 14px; }
.section-icon { margin-right: 2px; vertical-align: middle; }
.chart-title-icon { margin-right: 6px; vertical-align: middle; }

.placeholder-icon { opacity: 0.4; }

/* ==================== 6列社交卡片 —— 玻璃拟态 ==================== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 18px;
}

/* ---------- 通用卡片底座 ---------- */
.stat-card {
  background: var(--bg-card);
  border: 1px solid rgba(255, 220, 225, 0.5);
  border-radius: var(--radius-md);
  padding: 26px 18px 22px;
  text-align: center;
  cursor: default;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow:
    0 4px 12px var(--shadow-pink),
    0 1px 3px rgba(0, 0, 0, 0.02);
}

/* ---------- 社交卡片：玻璃拟态 ---------- */
.social-card {
  background: linear-gradient(
    160deg,
    rgba(255, 255, 255, 0.85) 0%,
    rgba(255, 245, 247, 0.6) 100%
  );
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 220, 225, 0.6);
  border-radius: var(--radius-lg);
  box-shadow:
    0 6px 20px var(--shadow-pink),
    0 2px 6px rgba(0, 0, 0, 0.02);
}

.social-card:hover {
  transform: translateY(-6px);
  background: linear-gradient(
    160deg,
    rgba(255, 255, 255, 0.9) 0%,
    rgba(255, 240, 243, 0.7) 100%
  );
  box-shadow:
    0 14px 36px var(--shadow-pink-hover),
    0 4px 10px var(--shadow-pink);
}

/* ---------- 数值 ---------- */
.stat-value {
  font-size: 2.2rem;
  font-weight: 750;
  color: var(--accent);
  margin-bottom: 6px;
  letter-spacing: 0.02em;
  text-shadow: 0 0 18px rgba(255, 143, 163, 0.25);
  transition: text-shadow 0.3s ease;
}

.social-card:hover .stat-value {
  text-shadow: 0 0 28px rgba(255, 143, 163, 0.4);
}

.stat-label {
  color: var(--text-secondary);
  font-size: 0.88rem;
  font-weight: 500;
  letter-spacing: 0.04em;
}

/* ==================== 商品区域（占位） ==================== */
.product-grid {
  opacity: 0.5;
  filter: grayscale(0.2);
}

.placeholder-card {
  background: #fafafa;
  border: 1px solid rgba(220, 210, 210, 0.35);
  cursor: not-allowed;
  box-shadow:
    0 3px 10px rgba(0, 0, 0, 0.04);
}

.placeholder-card:hover {
  transform: none;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.04);
}

/* ==================== 图表区域 ==================== */
.charts-section {
  margin-bottom: 36px;
}

/* ---------- 顶部栏 ---------- */
.charts-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 22px;
  flex-wrap: wrap;
  gap: 12px;
}

.time-range-selector {
  display: flex;
  align-items: center;
  gap: 12px;
}

.time-range-selector label {
  color: var(--text-secondary);
  font-weight: 500;
  font-size: 0.92rem;
}

.range-select {
  padding: 10px 20px;
  border: 1px solid rgba(255, 182, 193, 0.4);
  border-radius: 50px;
  background: var(--bg-card);
  color: var(--accent);
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  outline: none;
  transition: all 0.3s ease;
  box-shadow:
    0 3px 10px var(--shadow-pink);
  appearance: none;
  -webkit-appearance: none;
  padding-right: 36px;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='8'%3E%3Cpath d='M1 1l5 5 5-5' stroke='%23FF8FA3' stroke-width='1.8' fill='none' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 14px center;
}

.range-select:hover {
  border-color: rgba(255, 143, 163, 0.5);
  box-shadow:
    0 6px 18px var(--shadow-pink-hover);
  transform: translateY(-1px);
}

/* ---------- 图表卡片网格 ---------- */
.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(360px, 1fr));
  gap: 22px;
}

/* ---------- 图表卡片 ---------- */
.chart-card {
  background: var(--bg-card);
  border: 1px solid rgba(255, 220, 225, 0.45);
  border-radius: var(--radius-lg);
  padding: 28px 24px 22px;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow:
    0 5px 16px var(--shadow-pink),
    0 2px 5px rgba(0, 0, 0, 0.02);
}

.chart-card:hover {
  box-shadow:
    0 12px 32px var(--shadow-pink-hover),
    0 4px 10px var(--shadow-pink);
}

.chart-title {
  font-size: 1.1rem;
  font-weight: 650;
  color: #4a4040;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  letter-spacing: 0.03em;
}

.chart-container {
  width: 100%;
  height: 230px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background:
    linear-gradient(to bottom, rgba(255, 182, 193, 0.06) 1px, transparent 1px),
    linear-gradient(to right, rgba(255, 182, 193, 0.04) 1px, transparent 1px);
  background-size: 100% 20%, 14.28% 100%;
}

/* ---------- SVG 图表 ---------- */
.line-chart,
.bar-chart {
  width: 100%;
  height: 100%;
}

/* 柱状图 */
.bar {
  fill: var(--accent);
  opacity: 0.8;
  transition: opacity 0.2s ease;
  animation: barRise 0.4s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}

.bar:hover { opacity: 1; }

@keyframes barRise {
  from { transform: scaleY(0); transform-origin: bottom; opacity: 0; }
  to   { transform: scaleY(1); opacity: 0.8; }
}

/* 折线图数据点 */
.chart-point {
  transition: r 0.2s ease;
}

.chart-point:hover { r: 6; }

/* 图表标签 */
.chart-label {
  font-size: 0.7rem;
  fill: var(--text-secondary);
  text-anchor: middle;
  font-weight: 500;
}

.chart-label-small { font-size: 0.55rem; }

.chart-value {
  font-size: 0.7rem;
  fill: var(--accent);
  font-weight: 650;
}

.bar-value {
  font-size: 0.75rem;
  fill: var(--accent);
  font-weight: 650;
}

/* ==================== 温馨寄语 ==================== */
.message-section {
  margin-top: 36px;
}

.message-card {
  background: var(--bg-card);
  border: 1px solid rgba(255, 220, 225, 0.4);
  border-radius: var(--radius-lg);
  padding: 34px 28px;
  text-align: center;
  box-shadow:
    0 5px 18px var(--shadow-pink),
    0 2px 5px rgba(0, 0, 0, 0.02);
  transition: box-shadow 0.4s ease;
}

.message-card:hover {
  box-shadow:
    0 10px 30px var(--shadow-pink-hover),
    0 3px 8px var(--shadow-pink);
}

.message-card h3 {
  margin-bottom: 14px;
  font-size: 1.25rem;
  font-weight: 650;
  color: #4a4040;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.message-card p {
  margin: 10px 0;
  line-height: 1.9;
  color: var(--text-secondary);
  font-size: 0.98rem;
}

/* ==================== 响应式 ==================== */
@media (max-width: 768px) {
  .love-stats-page {
    padding: 18px 14px;
  }

  .back-btn {
    position: relative;
    top: auto;
    left: auto;
    margin-bottom: 12px;
    display: inline-flex;
  }

  .page-header {
    padding-top: 0;
    margin-bottom: 24px;
  }

  .page-title { font-size: 1.8rem; }

  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 10px;
  }

  .stat-card { padding: 16px 8px; }
  .stat-value { font-size: 1.4rem; }
  .stat-label { font-size: 0.75rem; }

  .charts-grid { grid-template-columns: 1fr; }

  .charts-header {
    flex-direction: column;
    gap: 14px;
    align-items: flex-start;
  }

  .chart-container { height: 190px; }

  .message-card { padding: 22px 16px; }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 8px;
  }
}
</style>