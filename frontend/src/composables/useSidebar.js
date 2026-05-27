import { ref } from 'vue'
import { anniversaryApi } from '../api/modules/anniversary'
import { weatherApi } from '../api/modules/checkin'

// 模块级单例 — 所有页面共享同一份数据，只加载一次
const anniversaries = ref([])
const weatherData = ref({ temp: 26, desc: '多云转晴', tip: '适合出门走走哦~' })
const loading = ref(false)
let loaded = false

/**
 * 共享的右侧栏数据（纪念日 + 天气）
 * 全局单例，首次调用 fetchAll 后数据缓存，后续页面复用
 */
export function useSidebar() {
  const fetchAnniversaries = async () => {
    try {
      const res = await anniversaryApi.getAnniversaryList()
      if (res.code === 200 && res.data) {
        anniversaries.value = Array.isArray(res.data) ? res.data : []
      }
    } catch (e) {
      console.warn('[useSidebar] 获取纪念日失败:', e.message)
    }
  }

  const fetchWeather = async () => {
    try {
      const res = await weatherApi.getToday()
      if (res.code === 200 && res.data) {
        weatherData.value = res.data
      }
    } catch (e) {
      console.warn('[useSidebar] 获取天气失败:', e.message)
    }
  }

  const fetchAll = async () => {
    if (loaded) return
    loading.value = true
    await Promise.all([fetchAnniversaries(), fetchWeather()])
    loaded = true
    loading.value = false
  }

  return {
    anniversaries,
    weatherData,
    loading,
    fetchAnniversaries,
    fetchWeather,
    fetchAll
  }
}
