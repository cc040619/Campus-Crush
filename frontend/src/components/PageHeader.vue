<template>
  <div class="page-header" :class="{ 'fixed': fixed }">
    <div class="header-left">
      <el-button 
        v-if="showBack" 
        @click="$router.push('/')" 
        class="back-btn"
      >
        <el-icon><ArrowLeft /></el-icon>
        返回首页
      </el-button>
      <h2>{{ title }}</h2>
    </div>
    <slot name="right"></slot>
  </div>
</template>

<script setup>
import { ArrowLeft } from '@element-plus/icons-vue'

defineProps({
  title: {
    type: String,
    default: ''
  },
  showBack: {
    type: Boolean,
    default: true
  },
  fixed: {
    type: Boolean,
    default: true
  }
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 40px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 230, 230, 0.5);
  box-shadow: 0 2px 10px rgba(255, 153, 153, 0.1);
  z-index: 100;
  height: 80px;
}

.page-header.fixed {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.page-header h2 {
  color: #FF9999;
  font-size: 28px;
  text-shadow: 0 2px 4px rgba(255, 153, 153, 0.3);
  margin: 0;
}

.back-btn {
  background-color: #FF9999;
  border-color: #FF9999;
  color: white;
  border-radius: 25px;
  padding: 10px 20px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(255, 153, 153, 0.3);
}

.back-btn:hover {
  background-color: #FF8080;
  border-color: #FF8080;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 153, 153, 0.4);
  filter: brightness(1.1);
}

@media (max-width: 768px) {
  .page-header {
    padding: 10px 20px;
    height: auto;
  }
  
  .header-left {
    gap: 10px;
  }
  
  .page-header h2 {
    font-size: 20px;
  }
  
  .back-btn {
    padding: 8px 16px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .page-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .header-left {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
  
  .back-btn {
    padding: 6px 12px;
    font-size: 12px;
  }
}
</style>