<template>
  <div class="statistics-page">
    <h2 class="page-title">💝 我们的爱情数据</h2>
    
    <div class="stats-grid">



      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: #FFF0F5;">
            <el-icon :size="40"><Star /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.anniversaryCount || 0 }}</div>
            <div class="stat-label">纪念日数量</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: #FF9999;">
            <el-icon :size="40"><Picture /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.albumCount || 0 }}</div>
            <div class="stat-label">相册照片</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-content">
          <div class="stat-icon" style="background: #F8E1E1;">
            <el-icon :size="40"><Notebook /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.diaryCount || 0 }}</div>
            <div class="stat-label">恋爱日记</div>
          </div>
        </div>
      </el-card>
    </div>

    <div class="welcome-message">
      <el-card>
        <h3>💌 温馨寄语</h3>
        <p>每一段爱情都值得被记录，每一个瞬间都值得被珍藏。</p>
        <p>在这里，留下你们最美好的回忆吧～</p>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStatistics } from '../api'
import { ElMessage } from 'element-plus'

const statistics = ref({})

onMounted(async () => {
  try {
    const res = await getStatistics()
    statistics.value = res.data
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
})
</script>

<style scoped>
.statistics-page {
  animation: fadeIn 0.5s ease-in;
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

.page-title {
  color: #FF9999;
  font-size: 28px;
  margin-bottom: 30px;
  text-align: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  border-radius: 15px;
  border: 2px solid rgba(255, 192, 203, 0.8);
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  box-shadow: 0 8px 32px rgba(255, 192, 203, 0.3);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #FF9999;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.welcome-message {
  margin-top: 30px;
}

.welcome-message .el-card {
  border-radius: 15px;
  border: 2px solid rgba(255, 192, 203, 0.8);
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  box-shadow: 0 8px 32px rgba(255, 192, 203, 0.3);
}

.welcome-message h3 {
  color: #FF9999;
  margin-bottom: 15px;
  font-size: 20px;
}

.welcome-message p {
  color: #666;
  line-height: 1.8;
  margin-bottom: 10px;
  font-size: 15px;
}

:deep(.el-card__body) {
  padding: 25px;
}
</style>
