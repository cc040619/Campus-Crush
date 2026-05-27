<template>
  <div class="anniversary-page">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goToHome" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回首页
        </el-button>
        <h2><svg class="star-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" fill="url(#starGradient)"/><defs><linearGradient id="starGradient" x1="12" y1="2" x2="12" y2="21" gradientUnits="userSpaceOnUse"><stop stop-color="#FF8FA3"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs></svg> 恋爱纪念日</h2>
      </div>
      <el-button type="primary" @click="handleAdd" class="add-btn">
        <el-icon><Plus /></el-icon>
        添加纪念日
      </el-button>
    </div>

    <div class="stats-section">
      <div class="stats-card">
        <div class="stat-item clickable" @click="openCoupleDialog" title="点击设置在一起日期">
          <div class="stat-icon-wrapper">
            <svg class="icon-heart" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M24 44.35L21.1 40.06C10.8 30.72 4 24.56 4 17C4 10.84 9.84 6 16 6C19.52 6 22.68 7.88 24 10.16C25.32 7.88 28.48 6 32 6C38.16 6 44 10.84 44 17C44 24.56 37.2 30.72 26.9 40.06L24 44.35Z" fill="url(#heartGradient)"/>
              <defs><linearGradient id="heartGradient" x1="24" y1="6" x2="24" y2="44" gradientUnits="userSpaceOnUse"><stop stop-color="#FF8FA3"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs>
            </svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ daysTogether }}</div>
            <div class="stat-label">在一起天数</div>
            <div class="stat-hint">点击设置</div>
          </div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-icon-wrapper">
            <svg class="icon-cake" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg"><rect x="10" y="22" width="28" height="18" rx="4" fill="url(#cakeGradient)"/><rect x="12" y="14" width="24" height="10" rx="3" fill="url(#cakeGradient)"/><rect x="16" y="6" width="16" height="10" rx="2" fill="url(#cakeGradient)"/><rect x="22" y="0" width="4" height="8" rx="1" fill="#FFF"/><ellipse cx="24" cy="2" rx="3" ry="2" fill="#FFD700"/><circle cx="16" cy="32" r="2" fill="#FF8FA3"/><circle cx="24" cy="34" r="2" fill="#FFB6C1"/><circle cx="32" cy="32" r="2" fill="#FF8FA3"/><circle cx="18" cy="22" r="1.5" fill="#FFB6C1"/><circle cx="30" cy="22" r="1.5" fill="#FFB6C1"/><defs><linearGradient id="cakeGradient" x1="0%" y1="0%" x2="0%" y2="100%"><stop stop-color="#FFE4E9"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs></svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ upcomingAnniversary }}</div>
            <div class="stat-label">下一个纪念日</div>
          </div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-icon-wrapper">
            <svg class="icon-calendar" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg"><rect x="6" y="10" width="36" height="34" rx="4" fill="url(#calendarBg)"/><rect x="6" y="10" width="36" height="34" rx="4" stroke="#FFB6C1" stroke-width="2"/><rect x="6" y="10" width="36" height="10" rx="4" fill="url(#calendarHeader)"/><text x="24" y="18" text-anchor="middle" font-size="7" fill="#FF6B8A" font-weight="bold">日期</text><line x1="6" y1="20" x2="42" y2="20" stroke="#FFB6C1" stroke-width="1.5"/><line x1="13" y1="20" x2="13" y2="44" stroke="#FFE4E9" stroke-width="1"/><line x1="20" y1="20" x2="20" y2="44" stroke="#FFE4E9" stroke-width="1"/><line x1="27" y1="20" x2="27" y2="44" stroke="#FFE4E9" stroke-width="1"/><line x1="34" y1="20" x2="34" y2="44" stroke="#FFE4E9" stroke-width="1"/><line x1="6" y1="27" x2="42" y2="27" stroke="#FFE4E9" stroke-width="1"/><line x1="6" y1="34" x2="42" y2="34" stroke="#FFE4E9" stroke-width="1"/><line x1="6" y1="41" x2="42" y2="41" stroke="#FFE4E9" stroke-width="1"/><circle cx="23.5" cy="30.5" r="3" fill="#FF8FA3"/><text x="23.5" y="33" text-anchor="middle" font-size="5" fill="#FFF" font-weight="bold">14</text><defs><linearGradient id="calendarBg" x1="0%" y1="0%" x2="0%" y2="100%"><stop stop-color="#FFF"/><stop offset="1" stop-color="#FFE4E9"/></linearGradient><linearGradient id="calendarHeader" x1="0%" y1="0%" x2="0%" y2="100%"><stop stop-color="#FFE4E9"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs></svg>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ totalAnniversaries }}</div>
            <div class="stat-label">纪念日总数</div>
          </div>
        </div>
      </div>
    </div>

    <div class="filter-bar">
      <el-select v-model="selectedType" placeholder="筛选类型" class="type-select">
        <el-option label="全部" value="" />
        <el-option v-for="type in anniversaryTypes" :key="type.value" :label="type.label" :value="type.value" />
      </el-select>
    </div>

    <div v-if="filteredAnniversaries.length > 0" class="anniversary-grid">
      <el-card
        v-for="item in filteredAnniversaries"
        :key="item.id"
        class="anniversary-card"
        shadow="hover"
      >
        <div class="card-content">
          <div class="card-header">
            <span class="type-badge" :class="item.type">
              {{ getTypeLabel(item.type) }}
            </span>
            <svg class="type-icon" :class="['type-icon-' + item.type]" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <template v-if="item.type === 'love'">
                <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.28 2 8.5C2 5.42 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.42 22 8.5C22 12.28 18.6 15.36 13.45 20.03L12 21.35Z" fill="url(#typeLove)"/>
                <defs><linearGradient id="typeLove" x1="12" y1="3" x2="12" y2="21" gradientUnits="userSpaceOnUse"><stop stop-color="#FF8FA3"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs>
              </template>
              <template v-else-if="item.type === 'birthday'">
                <rect x="4" y="10" width="16" height="12" rx="2" fill="url(#typeCake)"/>
                <rect x="6" y="6" width="12" height="6" rx="1.5" fill="url(#typeCake)"/>
                <rect x="11" y="0" width="2" height="6" rx="1" fill="#FFF"/>
                <ellipse cx="12" cy="1" rx="2" ry="1.5" fill="#FFD700"/>
                <defs><linearGradient id="typeCake" x1="0%" y1="0%" x2="0%" y2="100%"><stop stop-color="#FFE4E9"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs>
              </template>
              <template v-else-if="item.type === 'festival'">
                <polygon points="12,2 15,9 22,9 16,14 18,22 12,17 6,22 8,14 2,9 9,9" fill="url(#typeStar)"/>
                <defs><linearGradient id="typeStar" x1="12" y1="2" x2="12" y2="22" gradientUnits="userSpaceOnUse"><stop stop-color="#FFD700"/><stop offset="1" stop-color="#FFEC8B"/></linearGradient></defs>
              </template>
              <template v-else>
                <circle cx="12" cy="12" r="10" fill="url(#typeOther)"/>
                <defs><linearGradient id="typeOther" x1="0%" y1="0%" x2="100%" y2="100%"><stop stop-color="#FFE4E9"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs>
              </template>
            </svg>
          </div>
          <h3>{{ item.name }}</h3>
          <div class="date-info">
            <el-icon><Calendar /></el-icon>
            <span>{{ formatDate(item.date) }}</span>
          </div>
          <div 
            class="days-left clickable" 
            :class="{ 'passed': getDaysLeft(item.date) <= 0 && !displayMode[item.id], 'next-year': displayMode[item.id] }"
            @click="toggleDisplayMode(item.id, item.date)"
          >
            <template v-if="getDaysLeft(item.date) > 0">
              还有 {{ getDaysLeft(item.date) }} 天
            </template>
            <template v-else-if="displayMode[item.id]">
              还有 {{ getDaysUntilNextOccurrence(item.date) }} 天
            </template>
            <template v-else>
              已过去 {{ Math.abs(getDaysLeft(item.date)) }} 天
            </template>
          </div>
          <div class="remind-info" v-if="item.remindEnabled">
            <el-icon><Bell /></el-icon>
            <span>提前 {{ item.remindDays }} 天提醒</span>
          </div>
          <div class="card-actions">
            <el-button type="primary" size="small" @click="handleEdit(item)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(item.id)">删除</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <div v-else class="empty-container">
      <el-empty description="还没有纪念日，快来添加吧～" />
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="dataForm" label-width="120px">
        <el-form-item label="纪念日名称" prop="name">
          <el-input v-model="form.name" placeholder="例如：在一起纪念日" />
        </el-form-item>
        <el-form-item label="纪念日类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option v-for="type in anniversaryTypes" :key="type.value" :label="type.label" :value="type.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="form.date"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            class="date-picker-input"
          />
        </el-form-item>
        <el-form-item label="提前提醒">
          <el-switch v-model="form.remindEnabled" active-text="开启" inactive-text="关闭" />
        </el-form-item>
        <el-form-item label="提醒天数" v-if="form.remindEnabled">
          <el-input-number v-model="form.remindDays" :min="1" :max="30" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 设置在一起日期弹窗 -->
    <el-dialog
      v-model="coupleDialogVisible"
      width="450px"
      align-center
    >
      <template #title>
        <div class="dialog-title">
          <svg class="title-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.28 2 8.5C2 5.42 4.42 3 7.5 3C9.24 3 10.91 3.81 12 5.08C13.09 3.81 14.76 3 16.5 3C19.58 3 22 5.42 22 8.5C22 12.28 18.6 15.36 13.45 20.03L12 21.35Z" fill="url(#titleHeartGrad)"/>
            <defs><linearGradient id="titleHeartGrad" x1="12" y1="3" x2="12" y2="21" gradientUnits="userSpaceOnUse"><stop stop-color="#FF8FA3"/><stop offset="1" stop-color="#FFB6C1"/></linearGradient></defs>
          </svg>
          <span>设置在一起日期</span>
        </div>
      </template>
      <div class="couple-dialog-content">
        <p class="couple-tip">设置你们的恋爱开始日期，在一起天数将每天自动+1</p>
        <el-form label-width="100px">
          <el-form-item label="开始日期">
            <el-date-picker
              v-model="coupleFormDate"
              type="date"
              placeholder="选择在一起的那天"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="coupleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCoupleDate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAnniversaryList, getNextAnniversary, getTypes, addAnniversary, updateAnniversary, deleteAnniversary, getCouple, saveCouple } from '../api'
import { useRouter } from 'vue-router'

const router = useRouter()

const goToHome = () => {
  router.push('/')
}

const anniversaryTypes = ref([])
const displayMode = ref({})

const anniversaryList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加纪念日')
const dataForm = ref(null)
const selectedType = ref('')
const coupleStartDate = ref('')
const coupleDialogVisible = ref(false)
const coupleFormDate = ref('')

const form = ref({
  id: null,
  name: '',
  type: 'love',
  date: '',
  remindEnabled: false,
  remindDays: 7
})

const rules = {
  name: [{ required: true, message: '请输入纪念日名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择纪念日类型', trigger: 'change' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

const filteredAnniversaries = computed(() => {
  if (!selectedType.value) {
    return anniversaryList.value
  }
  return anniversaryList.value.filter(item => item.type === selectedType.value)
})

const daysTogether = computed(() => {
  if (!coupleStartDate.value) return 0
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const target = new Date(coupleStartDate.value)
  target.setHours(0, 0, 0, 0)
  const diff = today.getTime() - target.getTime()
  return Math.floor(diff / (1000 * 60 * 60 * 24))
})

const upcomingAnniversary = ref('')

const loadNextAnniversary = async () => {
  try {
    const res = await getNextAnniversary()
    if (res.data && res.data.name) {
      upcomingAnniversary.value = `${res.data.name} (${res.data.daysLeft}天)`
    } else {
      upcomingAnniversary.value = '-'
    }
  } catch {
    upcomingAnniversary.value = '-'
  }
}

const totalAnniversaries = computed(() => {
  return anniversaryList.value.length
})

onMounted(() => {
  window.scrollTo({
    top: 0,
    left: 0,
    behavior: 'instant'
  })
  const container = document.querySelector('.anniversary-page');
  if (container) {
    container.scrollTop = 0;
  }

  loadTypes()
  loadCouple()
  loadData()
  loadNextAnniversary()
  checkReminders()
})

const loadTypes = async () => {
  try {
    const res = await getTypes()
    anniversaryTypes.value = res.data || []
  } catch {
    // 使用默认类型兜底
    anniversaryTypes.value = [
      { value: 'love', label: '恋爱纪念日' },
      { value: 'birthday', label: '生日' },
      { value: 'festival', label: '节日' },
      { value: 'other', label: '其他' }
    ]
  }
}

const loadCouple = async () => {
  try {
    const res = await getCouple()
    if (res.data && res.data.startDate) {
      coupleStartDate.value = res.data.startDate
    }
  } catch {
    // 未设置恋爱日期
  }
}

const loadData = async () => {
  try {
    const res = await getAnniversaryList()
    anniversaryList.value = (res.data || []).map(item => ({
      ...item,
      remindEnabled: item.remindEnabled || false,
      remindDays: item.remindDays || 7,
      type: item.type || 'love'
    }))
  } catch {
    ElMessage.error('获取数据失败')
  }
}

const checkReminders = () => {
  anniversaryList.value.forEach(item => {
    if (item.remindEnabled && item.date) {
      const daysLeft = getDaysLeft(item.date)
      if (daysLeft === item.remindDays) {
        showReminder(item)
      }
    }
  })
}

const showReminder = (item) => {
  ElMessage({
    message: `🎀 温馨提醒：${item.name} 还有 ${item.remindDays} 天！`,
    type: 'success',
    duration: 5000
  })
}

const handleAdd = () => {
  dialogTitle.value = '添加纪念日'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (item) => {
  dialogTitle.value = '编辑纪念日'
  form.value = { 
    ...item,
    remindEnabled: item.remindEnabled || false,
    remindDays: item.remindDays || 7,
    type: item.type || 'love'
  }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个纪念日吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAnniversary(id)
    ElMessage.success('删除成功')
    loadData()
    loadNextAnniversary()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!form.value.date) {
    ElMessage.warning('请选择日期')
    return
  }

  try {
    if (form.value.id) {
      await updateAnniversary(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await addAnniversary(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
    loadNextAnniversary()
  } catch {
    ElMessage.error('操作失败')
  }
}

const openCoupleDialog = () => {
  coupleFormDate.value = coupleStartDate.value || ''
  coupleDialogVisible.value = true
}

const saveCoupleDate = async () => {
  if (!coupleFormDate.value) {
    ElMessage.warning('请选择日期')
    return
  }
  try {
    await saveCouple({ startDate: coupleFormDate.value })
    coupleStartDate.value = coupleFormDate.value
    coupleDialogVisible.value = false
    ElMessage.success('设置成功')
  } catch {
    ElMessage.error('设置失败')
  }
}

const resetForm = () => {
  form.value = {
    id: null,
    name: '',
    type: 'love',
    date: '',
    remindEnabled: false,
    remindDays: 7
  }
  dataForm.value?.resetFields()
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

const getDaysLeft = (date) => {
  if (!date) return 0
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const target = new Date(date)
  target.setHours(0, 0, 0, 0)
  const diff = target.getTime() - today.getTime()
  return Math.ceil(diff / (1000 * 60 * 60 * 24))
}

const getDaysUntilNextOccurrence = (date) => {
  if (!date) return 0
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const target = new Date(date)
  target.setHours(0, 0, 0, 0)
  
  const thisYearTarget = new Date(today.getFullYear(), target.getMonth(), target.getDate())
  
  if (thisYearTarget >= today) {
    const diff = thisYearTarget.getTime() - today.getTime()
    return Math.ceil(diff / (1000 * 60 * 60 * 24))
  } else {
    const nextYearTarget = new Date(today.getFullYear() + 1, target.getMonth(), target.getDate())
    const diff = nextYearTarget.getTime() - today.getTime()
    return Math.ceil(diff / (1000 * 60 * 60 * 24))
  }
}

const toggleDisplayMode = (id, date) => {
  displayMode.value[id] = !displayMode.value[id]
}

const getTypeLabel = (type) => {
  const found = anniversaryTypes.value.find(t => t.value === type)
  return found ? found.label : '其他'
}
</script>

<style scoped>
.anniversary-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #FFF5F5 0%, #FFEBEB 100%);
  padding: 30px;
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

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
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

.back-btn {
  background-color: #FFE6E6;
  border-color: #FFE6E6;
  color: #FF9999;
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(255, 153, 153, 0.2);
}

.back-btn:hover {
  background-color: #FFC0CB;
  border-color: #FFC0CB;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 153, 153, 0.3);
  filter: brightness(1.1);
}

.add-btn {
  background-color: #FF9999;
  border-color: #FF9999;
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(255, 153, 153, 0.2);
}

.add-btn:hover {
  background-color: #FF8080;
  border-color: #FF8080;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 153, 153, 0.3);
  filter: brightness(1.1);
}

.stats-section {
  margin-bottom: 30px;
}

.stats-card {
  background: white;
  border-radius: 20px;
  padding: 30px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 40px;
  box-shadow: 0 8px 30px rgba(255, 153, 153, 0.15);
  animation: fadeIn 0.6s ease-out;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon-wrapper {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #FFE6E6, #FFC0CB);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: pulse 2s ease-in-out infinite;
  transition: all 0.3s ease;
}

.stat-icon-wrapper:hover {
  transform: scale(1.1);
  background: linear-gradient(135deg, #FFB6C1, #FF8FA3);
}

.stat-icon-wrapper svg {
  width: 40px;
  height: 40px;
}

.star-icon {
  width: 28px;
  height: 28px;
  margin-right: 8px;
  vertical-align: middle;
  transition: all 0.3s ease;
}

.star-icon:hover {
  transform: scale(1.15) rotate(15deg);
}

.type-icon {
  width: 28px;
  height: 28px;
  transition: all 0.3s ease;
}

.type-icon:hover {
  transform: scale(1.15);
}

.type-icon-love {
  width: 26px;
  height: 26px;
}

.type-icon-birthday {
  width: 26px;
  height: 26px;
}

.type-icon-festival {
  width: 26px;
  height: 26px;
}

.type-icon-other {
  width: 24px;
  height: 24px;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 2.5rem;
  font-weight: bold;
  color: #FF9999;
}

.stat-label {
  color: #999;
  font-size: 0.9rem;
}

.stat-divider {
  width: 2px;
  height: 60px;
  background: #FFE6E6;
}

.filter-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.type-select {
  width: 150px;
  border-radius: 10px;
}

.anniversary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  padding: 20px 0;
  animation: fadeIn 0.8s ease-in-out forwards;
}

.anniversary-card {
  border-radius: 15px;
  border: 2px solid #F8E1E1;
  transition: all 0.3s;
  opacity: 0;
  transform: translateY(15px);
  animation: fadeInUp 0.3s ease-in-out forwards;
}

.anniversary-card:nth-child(1) { animation-delay: 0.1s; }
.anniversary-card:nth-child(2) { animation-delay: 0.3s; }
.anniversary-card:nth-child(3) { animation-delay: 0.5s; }
.anniversary-card:nth-child(4) { animation-delay: 0.7s; }
.anniversary-card:nth-child(5) { animation-delay: 0.9s; }
.anniversary-card:nth-child(6) { animation-delay: 1.1s; }
.anniversary-card:nth-child(7) { animation-delay: 1.3s; }
.anniversary-card:nth-child(8) { animation-delay: 1.5s; }
.anniversary-card:nth-child(9) { animation-delay: 1.7s; }
.anniversary-card:nth-child(10) { animation-delay: 1.9s; }

.empty-container {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}

.anniversary-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(255, 192, 203, 0.3);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.type-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.type-badge.love {
  background: linear-gradient(135deg, #FFE6E6, #FFC0CB);
  color: #FF6B6B;
}

.type-badge.birthday {
  background: linear-gradient(135deg, #FFF8E7, #FFE4C4);
  color: #FFA500;
}

.type-badge.festival {
  background: linear-gradient(135deg, #F0E6FF, #E6D4FF);
  color: #9966CC;
}

.type-badge.other {
  background: linear-gradient(135deg, #F5F5F5, #E8E8E8);
  color: #666;
}

.type-icon {
  font-size: 1.5rem;
}

.card-content h3 {
  color: #FF9999;
  font-size: 20px;
  margin-bottom: 15px;
}

.date-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  margin-bottom: 15px;
}

.days-left {
  background: #FFF0F5;
  color: #FF9999;
  padding: 8px 15px;
  border-radius: 20px;
  font-size: 14px;
  text-align: center;
  margin-bottom: 10px;
  font-weight: bold;
}

.days-left.passed {
  background: #F5F5F5;
  color: #999;
}

.days-left.clickable {
  cursor: pointer;
  transition: all 0.3s ease;
}

.days-left.clickable:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(255, 153, 153, 0.3);
}

.days-left.next-year {
  background: linear-gradient(135deg, #E8F5E9, #C8E6C9);
  color: #4CAF50;
}

.remind-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #FF9999;
  font-size: 13px;
  margin-bottom: 15px;
  padding: 6px 10px;
  background: rgba(255, 153, 153, 0.1);
  border-radius: 15px;
}

.card-actions {
  display: flex;
  gap: 10px;
}

:deep(.el-card__body) {
  padding: 25px;
}

/* 文件夹风格按钮样式 */
.back-btn {
  background: linear-gradient(135deg, #FFE6E6, #FFC0CB);
  border: 2px solid #FFB6C1;
  border-radius: 8px;
  box-shadow: 
    0 4px 0 #FFA07A,
    0 6px 12px rgba(255, 182, 193, 0.4);
  transition: all 0.3s ease;
  cursor: pointer;
  font-weight: 600;
  color: #7c7c7c;
  position: relative;
  overflow: hidden;
}

.back-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.back-btn:hover {
  transform: translateY(-2px);
  box-shadow: 
    0 6px 0 #FFA07A,
    0 8px 16px rgba(255, 182, 193, 0.5);
  background: linear-gradient(135deg, #FFD1DC, #FFB6C1);
}

.back-btn:hover::before {
  left: 100%;
}

.back-btn:active {
  transform: translateY(0);
  box-shadow: 
    0 2px 0 #FFA07A,
    0 4px 8px rgba(255, 182, 193, 0.3);
}

/* 添加按钮样式 */
.add-btn {
  background: linear-gradient(135deg, #FF9999, #FF8080);
  border: 2px solid #FF8080;
  border-radius: 8px;
  box-shadow: 
    0 4px 0 #FF6666,
    0 6px 12px rgba(255, 102, 102, 0.4);
  transition: all 0.3s ease;
  cursor: pointer;
  font-weight: 600;
  color: white;
  position: relative;
  overflow: hidden;
}

.add-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 
    0 6px 0 #FF6666,
    0 8px 16px rgba(255, 102, 102, 0.5);
  background: linear-gradient(135deg, #FF8080, #FF6666);
}

.add-btn:hover::before {
  left: 100%;
}

.add-btn:active {
  transform: translateY(0);
  box-shadow: 
    0 2px 0 #FF6666,
    0 4px 8px rgba(255, 102, 102, 0.3);
}

/* 卡片操作按钮样式 */
.card-actions .el-button {
  border-radius: 8px;
  padding: 6px 14px;
  font-size: 12px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.card-actions .el-button--primary {
  background: linear-gradient(135deg, #FF9999, #FF8080);
  border: 1px solid #FF8080;
  box-shadow: 0 2px 0 #FF6666;
}

.card-actions .el-button--primary:hover {
  background: linear-gradient(135deg, #FF8080, #FF6666);
  transform: translateY(-2px);
  box-shadow: 0 4px 0 #FF5050, 0 6px 12px rgba(255, 102, 102, 0.3);
}

.card-actions .el-button--danger {
  background: linear-gradient(135deg, #FF6B6B, #FF5252);
  border: 1px solid #FF5252;
  box-shadow: 0 2px 0 #FF3333;
}

.card-actions .el-button--danger:hover {
  background: linear-gradient(135deg, #FF5252, #FF3333);
  transform: translateY(-2px);
  box-shadow: 0 4px 0 #FF1A1A, 0 6px 12px rgba(255, 51, 51, 0.3);
}

/* 对话框按钮样式 */
:deep(.el-dialog__footer) {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px;
}

:deep(.el-dialog__footer .el-button) {
  padding: 10px 20px;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

:deep(.el-dialog__footer .el-button--default) {
  background: linear-gradient(135deg, #f8f9fa, #e9ecef);
  border: 1px solid #dee2e6;
  color: #495057;
}

:deep(.el-dialog__footer .el-button--default:hover) {
  background: linear-gradient(135deg, #e9ecef, #dee2e6);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

:deep(.el-dialog__footer .el-button--primary) {
  background: linear-gradient(135deg, #FF9999, #FF8080);
  border: 1px solid #FF8080;
  color: white;
  box-shadow: 0 4px 0 #FF6666;
}

:deep(.el-dialog__footer .el-button--primary:hover) {
  background: linear-gradient(135deg, #FF8080, #FF6666);
  transform: translateY(-2px);
  box-shadow: 0 6px 0 #FF5050, 0 8px 16px rgba(255, 102, 102, 0.4);
}

:deep(.el-dialog__footer .el-button--primary:active) {
  transform: translateY(0);
  box-shadow: 0 2px 0 #FF5050, 0 4px 8px rgba(255, 102, 102, 0.3);
}

/* 选择框样式 */
:deep(.type-select .el-select__wrapper) {
  border-radius: 10px;
  border: 2px solid #FFCCCC;
}

:deep(.type-select .el-select__wrapper:hover) {
  border-color: #FF9999;
}

:deep(.type-select .el-select__wrapper.is-focus) {
  border-color: #FF9999;
  box-shadow: 0 0 0 2px rgba(255, 153, 153, 0.2);
}

.date-picker-input {
  width: 100%;
}

:deep(.date-picker-input .el-date-picker) {
  width: 100%;
}

:deep(.date-picker-input .el-input__wrapper) {
  border-radius: 10px;
  border: 2px solid #FFCCCC;
  height: 44px;
  transition: all 0.3s ease;
}

:deep(.date-picker-input .el-input__wrapper:hover) {
  border-radius: 10px;
  border: 2px solid #FFCCCC;
  height: 44px;
  transition: all 0.3s ease;
}

:deep(.date-picker-input .el-input__wrapper:hover) {
  border-color: #FF9999;
  box-shadow: 0 2px 8px rgba(255, 153, 153, 0.2);
}

:deep(.date-picker-input .el-input__wrapper.is-focus) {
  border-color: #FF9999;
  box-shadow: 0 0 0 3px rgba(255, 153, 153, 0.25);
}

:deep(.date-picker-input .el-input__inner) {
  color: #606266;
  font-weight: 500;
}

.form-hint {
  margin-left: 10px;
  font-size: 12px;
  color: #999;
}

.stat-item.clickable {
  cursor: pointer;
  border-radius: 10px;
  padding: 8px;
  transition: all 0.3s ease;
}

.stat-item.clickable:hover {
  background: rgba(255, 192, 203, 0.15);
  transform: scale(1.05);
}

.stat-hint {
  color: #FF9999;
  font-size: 0.75rem;
  margin-top: 2px;
}

.couple-dialog-content {
  padding: 0 10px;
}

.dialog-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #FF8FA3;
  font-size: 18px;
  font-weight: 600;
}

.title-icon {
  width: 28px;
  height: 28px;
}

.couple-tip {
  color: #999;
  font-size: 14px;
  margin-bottom: 20px;
  text-align: center;
  line-height: 1.6;
}

/* 修复全局 box-sizing: border-box 导致 el-select 选中值不显示 */
:deep(.el-select__wrapper) {
  box-sizing: content-box;
  min-height: 30px;
}

:deep(.el-select__input) {
  box-sizing: content-box;
}

:deep(.el-select__selected-item) {
  box-sizing: content-box;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .anniversary-page {
    padding: 15px;
  }

  .header-left {
    gap: 10px;
  }

  .page-header h2 {
    font-size: 20px;
  }

  .back-btn,
  .add-btn {
    padding: 6px 12px;
    font-size: 14px;
  }

  .stats-card {
    flex-direction: column;
    gap: 20px;
    padding: 20px;
  }

  .stat-divider {
    width: 60px;
    height: 2px;
  }

  .stat-value {
    font-size: 1.8rem;
  }
}

@media (max-width: 480px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .header-left {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }

  .back-btn,
  .add-btn {
    padding: 4px 10px;
    font-size: 12px;
  }

  .filter-bar {
    justify-content: flex-start;
  }
}
</style>
