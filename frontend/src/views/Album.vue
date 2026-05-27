<template>
  <div class="album-page">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="wall-texture"></div>
      <div class="hearts-decoration">
        <span v-for="n in 20" :key="n" class="float-heart" :style="getHeartStyle(n)">{{ getHeartEmoji() }}</span>
      </div>
    </div>

    <!-- 顶部导航栏 -->
    <PageHeader title="📸 恋爱相册">
      <template #right>
        <el-button type="primary" @click="handleAdd" class="add-btn">
          <el-icon><Plus /></el-icon>
          上传照片
        </el-button>
      </template>
    </PageHeader>

    <!-- 照片墙容器 -->
    <div class="photos-wall">
      <!-- 顶部灯带 -->
      <div class="fairy-lights">
        <div v-for="n in 24" :key="n" class="light-bulb" :style="getLightStyle(n)"></div>
        <div class="light-string"></div>
      </div>

      <!-- 照片网格 -->
      <div class="photos-grid">
        <div
          v-for="(item, index) in albumList"
          :key="item.id"
          class="polaroid-card"
          :style="getCardStyle(index)"
          @click="onPhotoClick(item, index, $event)"
        >
          <div class="polaroid-photo">
            <img v-lazy="item.photoUrl" :alt="item.photoName" class="photo-img" />
            <div class="photo-shadow"></div>
          </div>
          <div class="polaroid-caption">
            <span>{{ item.photoName }}</span>
            <span class="heart-icon">💕</span>
          </div>
          <div class="polaroid-clip">
            <div class="clip-top"></div>
            <div class="clip-bottom"></div>
          </div>
          <div class="card-hover-actions">
            <button class="mini-btn edit-btn" @click.stop="handleEdit(item)" title="编辑">
              <el-icon><EditPen /></el-icon>
            </button>
            <button class="mini-btn delete-btn" @click.stop="handleDelete(item.id)" title="删除">
              <el-icon><Delete /></el-icon>
            </button>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="albumList.length === 0" class="empty-state">
          <div class="empty-heart">💕</div>
          <p>还没有照片</p>
          <p class="empty-hint">快来上传你们的甜蜜回忆吧～</p>
        </div>
      </div>

      <!-- 底部装饰 -->
      <div class="bottom-decoration">
        <div class="teddy-bear">🧸</div>
        <div class="heart-light">💡</div>
        <div class="camera">📷</div>
        <div class="plant">🪴</div>
      </div>

      <!-- 底部文字 -->
      <div class="bottom-text">
        <p>未来的每一天，想和你一起度过💕</p>
        <p class="english-text">💕 Love you forever 💕</p>
      </div>
    </div>

    <!-- 便签装饰 -->
    <div class="sticky-notes">
      <div class="note note-1">
        <div class="note-icon">🐰</div>
        <p>爱你哟 ❤️</p>
      </div>
      <div class="note note-2">
        <p>Happy ❤️</p>
      </div>
    </div>

    <!-- 全屏照片预览 -->
    <Teleport to="body">
      <transition name="overlay-fade">
        <div
          v-if="previewVisible"
          class="preview-overlay"
          @click.self="closePreview"
        >
          <div class="preview-backdrop"></div>
          <div class="preview-content">
            <img :src="previewUrl" :alt="previewName" class="preview-img" />
            <div class="preview-caption">{{ previewName }}</div>
          </div>
          <button class="preview-close-btn" @click="closePreview">
            <el-icon><Close /></el-icon>
          </button>
        </div>
      </transition>
    </Teleport>

    <!-- 上传/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="dataForm" label-width="100px">
        <el-form-item label="照片名称" prop="photoName">
          <el-input v-model="form.photoName" placeholder="请输入照片名称" />
        </el-form-item>
        <el-form-item label="照片文件" prop="file">
          <div class="file-upload-container">
            <input type="file" accept="image/*" @change="handleFileChange" class="file-input" />
            <div class="folder-button">
              <el-icon><Folder /></el-icon>
              <span>{{ file ? file.name : '选择文件' }}</span>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Folder, Plus, Close, EditPen, Delete } from '@element-plus/icons-vue'
import { albumApi } from '../api'
import PageHeader from '../components/PageHeader.vue'
import { useFormatTime, useConfirmDelete } from '../composables/useCommon'

// 原有逻辑
const formatTime = useFormatTime()
const confirmDelete = useConfirmDelete()

const albumList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('上传照片')
const dataForm = ref(null)
const file = ref(null)

const form = ref({ id: null, photoName: '', photoUrl: '' })

const rules = {
  photoName: [{ required: true, message: '请输入照片名称', trigger: 'blur' }]
}

const handleFileChange = (event) => {
  file.value = event.target.files[0]
}

onMounted(() => {
  window.scrollTo({ top: 0, left: 0, behavior: 'instant' })
  loadData()
})

onBeforeUnmount(() => {})

const loadData = async () => {
  try {
    const res = await albumApi.getAlbumList()
    albumList.value = res.data
  } catch (error) {
    ElMessage.error('获取数据失败')
  }
}

watch(() => albumList.value.length, () => {})

const handleAdd = () => {
  dialogTitle.value = '上传照片'
  dialogVisible.value = true
}

const handleEdit = (item) => {
  dialogTitle.value = '编辑照片'
  form.value = { ...item }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  await confirmDelete(async () => {
    await albumApi.deleteAlbum(id)
    loadData()
  }, '确定要删除这张照片吗？')
}

const handleSubmit = async () => {
  const valid = await dataForm.value.validate().catch(() => false)
  if (!valid) return

  if (!file.value) {
    ElMessage.error('请选择照片文件')
    return
  }

  try {
    const formData = new FormData()
    formData.append('file', file.value)
    formData.append('photoName', form.value.photoName)

    const userId = localStorage.getItem('userId') || ''

    if (form.value.id) {
      await albumApi.updateAlbum(form.value.id, formData, userId)
      ElMessage.success('更新成功')
    } else {
      await albumApi.addAlbum(formData, userId)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败：' + (error.message || '未知错误'))
  }
}

const resetForm = () => {
  form.value = { id: null, photoName: '', photoUrl: '' }
  file.value = null
  dataForm.value?.resetFields()
  const fileInput = document.querySelector('input[type="file"]')
  if (fileInput) fileInput.value = ''
}

// 照片卡片样式
const getCardStyle = (index) => {
  const rotations = [-3, -2, -1, 0, 1, 2, 3]
  const rotation = rotations[index % rotations.length]
  const delay = (index * 0.1) + 's'
  return {
    transform: `rotate(${rotation}deg)`,
    animationDelay: delay
  }
}

// 爱心装饰样式
const getHeartStyle = (n) => {
  const left = Math.random() * 90 + 5
  const top = Math.random() * 80 + 10
  const delay = Math.random() * 5
  const duration = 3 + Math.random() * 4
  return {
    left: left + '%',
    top: top + '%',
    animationDelay: delay + 's',
    animationDuration: duration + 's'
  }
}

const getHeartEmoji = () => {
  const emojis = ['💕', '💗', '💖', '💘', '💝', '💓']
  return emojis[Math.floor(Math.random() * emojis.length)]
}

// 灯带电灯样式
const getLightStyle = (n) => {
  const positions = [0, 4.17, 8.33, 12.5, 16.67, 20.83, 25, 29.17, 33.33, 37.5, 41.67, 45.83, 50, 54.17, 58.33, 62.5, 66.67, 70.83, 75, 79.17, 83.33, 87.5, 91.67, 95.83]
  return {
    left: positions[n - 1] + '%'
  }
}

// 照片预览
const previewVisible = ref(false)
const previewUrl = ref('')
const previewName = ref('')
const previewItem = ref(null)

const onPhotoClick = (item, index, event) => {
  previewUrl.value = item.photoUrl
  previewName.value = item.photoName
  previewItem.value = item
  previewVisible.value = true
}

const closePreview = () => {
  previewVisible.value = false
  setTimeout(() => {
    previewUrl.value = ''
    previewName.value = ''
    previewItem.value = null
  }, 300)
}
</script>

<style scoped>
/* 页面基底 */
.album-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #FFF5F5 0%, #FFE4E4 50%, #FFD6D6 100%);
  position: relative;
  overflow: hidden;
  padding-bottom: 120px;
}

/* 背景纹理 */
.background-decoration {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.wall-texture {
  position: absolute;
  inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)' opacity='0.03'/%3E%3C/svg%3E");
}

/* 浮动爱心装饰 */
.hearts-decoration {
  position: absolute;
  inset: 0;
}

.float-heart {
  position: absolute;
  font-size: 16px;
  opacity: 0.6;
  animation: floatHeart 4s ease-in-out infinite;
}

@keyframes floatHeart {
  0%, 100% {
    transform: translateY(0) scale(1);
    opacity: 0.6;
  }
  50% {
    transform: translateY(-20px) scale(1.2);
    opacity: 0.9;
  }
}

/* 顶部导航 */
:deep(.page-header) {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  padding: 12px 36px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(255, 180, 180, 0.4);
  z-index: 100;
  box-shadow: 0 2px 20px rgba(255, 153, 153, 0.1);
}

:deep(.page-header h2) {
  color: #FF7B7B;
  font-size: 26px;
  text-shadow: 0 2px 6px rgba(255, 153, 153, 0.2);
}

:deep(.back-btn) {
  background: linear-gradient(135deg, #FFB3B3, #FF9999);
  border: none;
  color: #fff;
  border-radius: 22px;
  padding: 8px 18px;
  font-weight: 600;
  font-size: 14px;
  box-shadow: 0 3px 0 #E87070;
  transition: all 0.25s ease;
}

:deep(.back-btn:hover) {
  transform: translateY(-2px);
  box-shadow: 0 5px 0 #E86060, 0 8px 20px rgba(255, 136, 136, 0.3);
}

.add-btn {
  background: linear-gradient(135deg, #FFB3B3, #FF9999);
  border: none;
  color: #fff;
  border-radius: 22px;
  padding: 8px 20px;
  font-weight: 600;
  font-size: 14px;
  box-shadow: 0 3px 0 #E87070;
  transition: all 0.25s ease;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 0 #E86060, 0 8px 20px rgba(255, 136, 136, 0.3);
}

/* 照片墙容器 */
.photos-wall {
  position: relative;
  z-index: 1;
  padding: 120px 20px 40px;
  max-width: 1400px;
  margin: 0 auto;
}

/* 灯带 */
.fairy-lights {
  position: relative;
  height: 60px;
  margin-bottom: 30px;
}

.light-string {
  position: absolute;
  top: 20px;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #D4A574, #F5DEB3, #D4A574);
}

.light-bulb {
  position: absolute;
  top: 8px;
  width: 24px;
  height: 24px;
  background: radial-gradient(circle, #FFF9C4 0%, #FFEB3B 40%, #FFD700 100%);
  border-radius: 50%;
  box-shadow: 0 0 15px #FFEB3B, 0 0 30px #FFD700, 0 0 45px #FFA000;
  animation: twinkle 2s ease-in-out infinite;
}

.light-bulb:nth-child(odd) {
  animation-delay: 0.5s;
}

@keyframes twinkle {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.6;
    transform: scale(0.9);
  }
}

/* 照片网格 */
.photos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 260px));
  gap: 30px;
  justify-content: center;
  padding: 20px 0;
}

/* 拍立得卡片 */
.polaroid-card {
  position: relative;
  animation: cardAppear 0.6s ease-out forwards;
  opacity: 0;
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.polaroid-card:hover {
  transform: rotate(0deg) scale(1.05) translateY(-5px);
  z-index: 10;
}

@keyframes cardAppear {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.polaroid-photo {
  position: relative;
  background: #fff;
  padding: 12px;
  box-shadow: 
    0 4px 20px rgba(0, 0, 0, 0.15),
    0 8px 30px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
}

.photo-img {
  width: 100%;
  aspect-ratio: 4/3;
  object-fit: cover;
  display: block;
}

.photo-shadow {
  position: absolute;
  inset: 0;
  box-shadow: inset 0 0 20px rgba(0, 0, 0, 0.05);
  pointer-events: none;
}

.polaroid-caption {
  background: #fff;
  padding: 15px 12px 25px;
  text-align: center;
  margin-top: -5px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border-radius: 0 0 4px 4px;
}

.polaroid-caption span:first-child {
  font-family: '楷体', 'KaiTi', serif;
  font-size: 16px;
  color: #555;
  letter-spacing: 2px;
}

.heart-icon {
  margin-left: 8px;
  font-size: 14px;
}

/* 木夹子 */
.polaroid-clip {
  position: absolute;
  top: -18px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 5;
}

.clip-top {
  width: 36px;
  height: 12px;
  background: linear-gradient(180deg, #C9A06B 0%, #A67C52 100%);
  border-radius: 3px 3px 0 0;
  position: relative;
}

.clip-top::before,
.clip-top::after {
  content: '';
  position: absolute;
  top: 0;
  width: 8px;
  height: 8px;
  background: linear-gradient(180deg, #8B6914 0%, #6B4423 100%);
  border-radius: 50%;
}

.clip-top::before {
  left: 4px;
}

.clip-top::after {
  right: 4px;
}

.clip-bottom {
  width: 0;
  height: 0;
  border-left: 18px solid transparent;
  border-right: 18px solid transparent;
  border-top: 8px solid #A67C52;
}

/* Hover操作按钮 */
.card-hover-actions {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 15;
}

.polaroid-card:hover .card-hover-actions {
  opacity: 1;
}

.mini-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 14px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.edit-btn {
  background: rgba(255, 255, 255, 0.95);
  color: #FF8888;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
}

.delete-btn {
  background: rgba(255, 80, 80, 0.9);
  color: #fff;
  box-shadow: 0 2px 10px rgba(255, 80, 80, 0.3);
}

.mini-btn:hover {
  transform: scale(1.1);
}

/* 空状态 */
.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
}

.empty-heart {
  font-size: 80px;
  margin-bottom: 20px;
  animation: heartbeat 1.5s ease-in-out infinite;
}

@keyframes heartbeat {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.empty-state p {
  font-size: 20px;
  color: #FF9999;
  margin: 0 0 10px;
}

.empty-hint {
  font-size: 16px;
  color: #FFBBBB;
}

/* 底部装饰 */
.bottom-decoration {
  display: flex;
  justify-content: center;
  align-items: flex-end;
  gap: 20px;
  margin-top: 40px;
  padding: 20px;
}

.teddy-bear {
  font-size: 50px;
  animation: teddyWave 3s ease-in-out infinite;
}

@keyframes teddyWave {
  0%, 100% {
    transform: rotate(-5deg);
  }
  50% {
    transform: rotate(5deg);
  }
}

.heart-light {
  font-size: 40px;
  animation: glow 2s ease-in-out infinite;
}

@keyframes glow {
  0%, 100% {
    filter: brightness(1);
  }
  50% {
    filter: brightness(1.3);
  }
}

.camera {
  font-size: 45px;
}

.plant {
  font-size: 40px;
}

/* 底部文字 */
.bottom-text {
  text-align: center;
  margin-top: 20px;
  padding: 20px;
}

.bottom-text p {
  font-family: '楷体', 'KaiTi', serif;
  font-size: 24px;
  color: #FF8888;
  margin: 0 0 10px;
  letter-spacing: 4px;
}

.english-text {
  font-family: 'Georgia', serif;
  font-size: 18px !important;
  color: #FFAAAA !important;
  letter-spacing: 6px !important;
}

/* 便签装饰 */
.sticky-notes {
  position: fixed;
  top: 120px;
  z-index: 5;
  pointer-events: none;
}

.note {
  background: linear-gradient(135deg, #FFF9C4 0%, #FFF59D 100%);
  padding: 15px 20px;
  border-radius: 2px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  transform: rotate(-8deg);
}

.note-1 {
  left: 20px;
  top: 50px;
}

.note-2 {
  right: 30px;
  top: 80px;
  transform: rotate(5deg);
  background: linear-gradient(135deg, #FFCDD2 0%, #FFB7BD 100%);
}

.note-icon {
  font-size: 20px;
  margin-bottom: 5px;
}

.note p {
  margin: 0;
  font-size: 14px;
  color: #555;
  font-weight: 500;
}

/* 全屏预览 */
.preview-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-backdrop {
  position: absolute;
  inset: 0;
  background: rgba(10, 5, 10, 0.9);
  backdrop-filter: blur(10px);
}

.preview-content {
  position: relative;
  z-index: 2;
  max-width: 90vw;
  max-height: 90vh;
  text-align: center;
}

.preview-img {
  max-width: 100%;
  max-height: 80vh;
  border-radius: 8px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

.preview-caption {
  margin-top: 20px;
  font-family: '楷体', 'KaiTi', serif;
  font-size: 20px;
  color: #fff;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
}

.preview-close-btn {
  position: fixed;
  top: 30px;
  right: 30px;
  z-index: 10;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  color: #fff;
  font-size: 22px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.25s ease;
}

.preview-close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.1) rotate(90deg);
}

/* 上传对话框样式 */
.file-upload-container {
  position: relative;
  width: 100%;
}

.file-input {
  position: absolute;
  inset: 0;
  opacity: 0;
  cursor: pointer;
  z-index: 10;
}

.folder-button {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  background: linear-gradient(135deg, #FFE6E6, #FFC0CB);
  border: 2px solid #FFB6C1;
  border-radius: 8px;
  box-shadow: 0 4px 0 #FFA07A;
  transition: all 0.3s ease;
  cursor: pointer;
  font-weight: 600;
  color: #555;
}

.folder-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 0 #FFA07A, 0 8px 16px rgba(255, 182, 193, 0.5);
}

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
}

:deep(.el-dialog__footer .el-button--primary) {
  background: linear-gradient(135deg, #FF9999, #FF8080);
  border: 1px solid #FF8080;
  color: white;
}

/* 过渡动画 */
.overlay-fade-enter-active,
.overlay-fade-leave-active {
  transition: opacity 0.35s ease;
}

.overlay-fade-enter-from,
.overlay-fade-leave-to {
  opacity: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  :deep(.page-header) {
    padding: 10px 20px;
  }
  
  :deep(.page-header h2) {
    font-size: 20px;
  }

  .photos-grid {
    grid-template-columns: repeat(auto-fill, minmax(180px, 220px));
    gap: 20px;
  }

  .polaroid-caption span:first-child {
    font-size: 14px;
  }

  .bottom-text p {
    font-size: 18px;
    letter-spacing: 2px;
  }

  .english-text {
    font-size: 14px !important;
  }

  .teddy-bear {
    font-size: 40px;
  }

  .heart-light, .camera, .plant {
    font-size: 32px;
  }
}

@media (max-width: 480px) {
  .photos-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 180px));
    gap: 15px;
  }

  .sticky-notes {
    display: none;
  }
}
</style>