<template>
  <div class="diary-page">
    <!-- 背景装饰 -->
    <div class="bg-decorations">
      <div class="float-petal" v-for="n in 15" :key="'p'+n" :style="getPetalStyle(n)"></div>
    </div>

    <!-- 顶部导航 -->
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goToHome" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回首页
        </el-button>
        <h2>📔 恋爱日记</h2>
      </div>
      <el-button type="primary" @click="handleAdd" class="add-btn">
        <el-icon><EditPen /></el-icon>
        写日记
      </el-button>
    </div>

    <!-- 日记列表 -->
    <div v-if="diaryList.length > 0" class="diary-timeline">
      <!-- 时间线轴线 -->
      <div class="timeline-line"></div>

      <div
        v-for="(item, index) in diaryList"
        :key="item.id"
        class="diary-card-wrapper"
        :class="{ 'from-left': index % 2 === 0, 'from-right': index % 2 !== 0 }"
      >
        <!-- 时间线节点 -->
        <div class="timeline-node">
          <div class="node-inner">
            <span>{{ getDayFromTime(item.createTime) }}</span>
          </div>
        </div>

        <!-- 日记卡片 -->
        <div class="diary-card" @click="openDiaryDetail(item)">
          <!-- 有图片时展示图片 -->
          <div class="card-image" v-if="item.image">
            <img v-lazy="item.image" :alt="item.title" />
            <div class="image-overlay">
              <el-icon><ZoomIn /></el-icon>
            </div>
          </div>

          <div class="card-body">
            <div class="card-header-row">
              <h3 class="card-title">{{ item.title }}</h3>
              <div class="mood-badge" v-if="item.mood">
                <span class="mood-emoji">{{ getMoodEmoji(item.mood) }}</span>
                <span>{{ item.mood }}</span>
              </div>
            </div>

            <p class="card-preview">{{ getContentPreview(item.content) }}</p>

            <div class="card-footer">
              <span class="card-date">
                <el-icon><Calendar /></el-icon>
                {{ formatTime(item.createTime) }}
              </span>
              <div class="card-actions" @click.stop>
                <el-button type="primary" size="small" @click="handleEdit(item)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button type="danger" size="small" @click="handleDelete(item.id)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-container">
      <div class="empty-illustration">
        <div class="empty-book">
          <svg viewBox="0 0 100 80" class="book-svg">
            <path d="M10 10h60v55H10z" fill="#FFE4E4" stroke="#FFB8B8" stroke-width="1.5" rx="2"/>
            <path d="M70 10h20l-15 55H55z" fill="#FFD1DC" stroke="#FFB8B8" stroke-width="1.5" rx="2"/>
            <line x1="18" y1="20" x2="62" y2="20" stroke="#FFC0CB" stroke-width="2" stroke-linecap="round"/>
            <line x1="18" y1="28" x2="55" y2="28" stroke="#FFC0CB" stroke-width="2" stroke-linecap="round"/>
            <line x1="18" y1="36" x2="58" y2="36" stroke="#FFC0CB" stroke-width="2" stroke-linecap="round"/>
            <circle cx="82" cy="22" r="5" fill="#FFC0CB" opacity="0.6"/>
            <circle cx="86" cy="32" r="3" fill="#FFB8B8" opacity="0.5"/>
            <text x="78" y="50" font-size="5" fill="#FF9999" font-family="serif">❤</text>
          </svg>
        </div>
        <p class="empty-title">还没有写过日记呢</p>
        <p class="empty-desc">用文字记录你们的每一个甜蜜瞬间吧～</p>
        <el-button class="empty-write-btn" @click="handleAdd">
          <el-icon><EditPen /></el-icon>
          写第一篇日记
        </el-button>
      </div>
    </div>

    <!-- 底部装饰 -->
    <div class="bottom-deco">
      <span class="deco-text">💕 每一天都值得被记录 💕</span>
    </div>

    <!-- 日记详情弹窗 -->
    <div v-if="selectedDiary" class="diary-detail-overlay" @click="closeDiaryDetail">
      <div class="diary-detail-card" @click.stop>
        <div v-if="selectedDiary.image" class="detail-image">
          <img :src="selectedDiary.image" :alt="selectedDiary.title" />
        </div>
        <div class="detail-header">
          <h3>{{ selectedDiary.title }}</h3>
          <button class="close-btn" @click="closeDiaryDetail">
            <el-icon><Close /></el-icon>
          </button>
        </div>
        <div class="detail-meta">
          <span class="detail-mood" v-if="selectedDiary.mood">
            {{ getMoodEmoji(selectedDiary.mood) }} {{ selectedDiary.mood }}
          </span>
          <span class="detail-date">
            <el-icon><Calendar /></el-icon>
            {{ formatTime(selectedDiary.createTime) }}
          </span>
        </div>
        <div class="detail-content">
          <p v-for="(line, j) in selectedDiary.content.split('\n')" :key="j">{{ line }}</p>
        </div>
        <div class="detail-actions">
          <el-button @click="handleEdit(selectedDiary)">
            <el-icon><Edit /></el-icon> 编辑
          </el-button>
          <el-button type="danger" @click="handleDelete(selectedDiary.id)">
            <el-icon><Delete /></el-icon> 删除
          </el-button>
        </div>
      </div>
    </div>

    <!-- 写日记/编辑日记弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="680px"
      @close="resetForm"
      custom-class="diary-dialog"
    >
      <!-- 爱心装饰粒子 -->
      <div class="dialog-hearts">
        <span v-for="n in 12" :key="n" class="dh" :style="getDialogHeartStyle(n)">💕</span>
      </div>

      <el-form :model="form" :rules="rules" ref="dataForm" label-width="80px" class="diary-form">
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="form.title"
            placeholder="给今天的故事起个名字吧～"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="心情">
          <el-select v-model="form.mood" placeholder="今天心情怎么样？" style="width: 100%">
            <el-option label="😊 开心" value="开心" />
            <el-option label="😍 甜蜜" value="甜蜜" />
            <el-option label="🥰 幸福" value="幸福" />
            <el-option label="😘 感动" value="感动" />
            <el-option label="😢 难过" value="难过" />
            <el-option label="😤 生气" value="生气" />
            <el-option label="🤔 思考" value="思考" />
          </el-select>
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="9"
            placeholder="亲爱的，今天发生了什么呢？&#10;想写什么都可以，这是属于我们的小世界～"
          />
        </el-form-item>

        <!-- 可选图片上传 -->
        <el-form-item label="图片">
          <div class="image-upload-area">
            <div v-if="!imagePreview && !form.image" class="upload-trigger" @click="triggerFileInput">
              <input
                ref="fileInputRef"
                type="file"
                accept="image/*"
                class="hidden-input"
                @change="handleImageChange"
              />
              <div class="upload-icon-circle">
                <el-icon :size="28"><Picture /></el-icon>
              </div>
              <div class="upload-hint">
                <p class="hint-main">📷 想配张图吗？</p>
                <p class="hint-sub">不传也没关系，文字就足够美好～</p>
              </div>
            </div>
            <div v-else class="image-preview-box">
              <img :src="imagePreview || form.image" alt="预览" />
              <button class="remove-image-btn" @click="removeImage" title="移除图片">
                <el-icon><Close /></el-icon>
              </button>
              <div v-if="isUploading" class="uploading-mask">
                <el-icon class="spinner" :size="24"><Loading /></el-icon>
                <span>上传中...</span>
              </div>
            </div>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false" class="dialog-cancel-btn">再想想</el-button>
        <el-button type="primary" @click="handleSubmit" class="dialog-save-btn" :loading="isSubmitting">
          <span v-if="!isSubmitting">💝 保存下来</span>
          <span v-else>保存中...</span>
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { ArrowLeft, Plus, Close, EditPen, Edit, Delete, Calendar, Picture, ZoomIn, Loading } from '@element-plus/icons-vue'
import { getDiaryList, addDiary, updateDiary, deleteDiary } from '../api'
import request from '../utils/request'

const router = useRouter()
const goToHome = () => router.push('/')

const diaryList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('写日记')
const dataForm = ref(null)
const selectedDiary = ref(null)
const fileInputRef = ref(null)

const imagePreview = ref('')
const isUploading = ref(false)
const isSubmitting = ref(false)

const form = ref({
  id: null,
  title: '',
  content: '',
  mood: '',
  image: ''
})

const rules = {
  title: [{ required: true, message: '给日记起个名字吧', trigger: 'blur' }],
  content: [{ required: true, message: '写点什么吧，哪怕一句话也好～', trigger: 'blur' }]
}

// Helper functions
const getMoodEmoji = (mood) => {
  const map = { '开心': '😊', '甜蜜': '😍', '幸福': '🥰', '感动': '😘', '难过': '😢', '生气': '😤', '思考': '🤔' }
  return map[mood] || '💝'
}

const getContentPreview = (content) => {
  if (!content) return ''
  return content.length > 60 ? content.substring(0, 60) + '...' : content
}

const getDayFromTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  return String(d.getDate()).padStart(2, '0')
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

// Decorative petals
const getPetalStyle = (n) => {
  const hue = 340 + (n % 20) * 2
  const left = ((n * 37 + 13) % 100)
  const delay = (n * 0.7) % 6
  const duration = 8 + (n % 5)
  const size = 8 + (n % 12)
  return {
    left: left + '%',
    width: size + 'px',
    height: size + 'px',
    background: `hsla(${hue}, 80%, 82%, ${0.3 + (n % 5) * 0.1})`,
    animationDelay: delay + 's',
    animationDuration: duration + 's'
  }
}

const getDialogHeartStyle = (n) => {
  return {
    left: ((n * 73 + 11) % 100) + '%',
    top: ((n * 37 + 7) % 90) + '%',
    fontSize: (10 + (n % 14)) + 'px',
    animationDelay: (n * 0.3) + 's',
    opacity: 0.15 + (n % 5) * 0.08
  }
}

onMounted(() => {
  window.scrollTo({ top: 0, left: 0, behavior: 'instant' })
  loadData()
})

const loadData = async () => {
  try {
    const res = await getDiaryList()
    diaryList.value = res.data
    if (selectedDiary.value) {
      const exists = diaryList.value.some(item => item.id === selectedDiary.value.id)
      if (!exists) selectedDiary.value = null
    }
  } catch (error) {
    ElMessage.error('获取数据失败')
  }
}

const openDiaryDetail = (item) => { selectedDiary.value = item }
const closeDiaryDetail = () => { selectedDiary.value = null }

const handleAdd = () => {
  dialogTitle.value = '写日记'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (item) => {
  dialogTitle.value = '编辑日记'
  form.value = { ...item }
  imagePreview.value = ''
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇日记吗？', '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
    })
    await deleteDiary(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const triggerFileInput = () => {
  fileInputRef.value?.click()
}

const handleImageChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  // Preview
  const reader = new FileReader()
  reader.onload = (e) => { imagePreview.value = e.target.result }
  reader.readAsDataURL(file)
  // Upload
  isUploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await request.post('/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    if (res.code === 200 && res.data && res.data.url) {
      form.value.image = res.data.url
      ElMessage.success('图片上传成功')
    }
  } catch (error) {
    ElMessage.error('图片上传失败，请重试')
    imagePreview.value = ''
  } finally {
    isUploading.value = false
    // Reset file input
    if (fileInputRef.value) fileInputRef.value.value = ''
  }
}

const removeImage = () => {
  imagePreview.value = ''
  form.value.image = ''
}

const handleSubmit = async () => {
  const valid = await dataForm.value.validate().catch(() => false)
  if (!valid) return

  isSubmitting.value = true
  try {
    if (form.value.id) {
      await updateDiary(form.value.id, form.value)
      ElMessage.success('日记更新啦～')
    } else {
      await addDiary(form.value)
      ElMessage.success('日记保存好了，以后可以随时回来看哦～')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('保存失败，再试一次吧')
  } finally {
    isSubmitting.value = false
  }
}

const resetForm = () => {
  form.value = { id: null, title: '', content: '', mood: '', image: '' }
  imagePreview.value = ''
  dataForm.value?.resetFields()
}
</script>

<style scoped>
.diary-page {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(180deg, #FFF8F8 0%, #FFF0F3 30%, #FFE8EC 60%, #FFF0F3 100%);
  padding: 20px 40px 80px;
  overflow-x: hidden;
}

/* ========== 背景花瓣装饰 ========== */
.bg-decorations {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}
.float-petal {
  position: absolute;
  top: -20px;
  border-radius: 50% 0 50% 0;
  animation: petalFall linear infinite;
}
@keyframes petalFall {
  0% { transform: translateY(-20px) rotate(0deg); opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { transform: translateY(100vh) rotate(720deg); opacity: 0; }
}

/* ========== 顶栏 ========== */
.page-header {
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1000px;
  margin: 0 auto 30px;
}
.header-left { display: flex; align-items: center; gap: 20px; }
.page-header h2 { color: #dd7777; font-size: 26px; margin: 0; }
.back-btn, .add-btn {
  border-radius: 22px; padding: 8px 20px; font-weight: 600; font-size: 14px;
  border: none; cursor: pointer; transition: all 0.3s ease;
}
.back-btn {
  background: linear-gradient(135deg, #FFD6D6, #FFC0CB);
  color: #cc7777; box-shadow: 0 3px 0 #e8a0a0, 0 5px 12px rgba(255,180,180,0.3);
}
.back-btn:hover { transform: translateY(-2px); box-shadow: 0 5px 0 #e09090, 0 8px 18px rgba(255,160,160,0.4); }
.add-btn {
  background: linear-gradient(135deg, #FFA8A8, #FF8888);
  color: #fff; box-shadow: 0 3px 0 #d06868, 0 5px 12px rgba(255,140,140,0.3);
}
.add-btn:hover { transform: translateY(-2px); box-shadow: 0 5px 0 #c05858, 0 8px 18px rgba(255,130,130,0.4); }

/* ========== 时间线布局 ========== */
.diary-timeline {
  position: relative;
  z-index: 2;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px 0 60px;
}
.timeline-line {
  position: absolute;
  left: 50%;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(180deg, transparent 0%, #FFC0CB 5%, #FFC0CB 95%, transparent 100%);
  transform: translateX(-50%);
}
.diary-card-wrapper {
  position: relative;
  display: flex;
  align-items: flex-start;
  margin-bottom: 36px;
  width: 100%;
}
.from-left { justify-content: flex-start; padding-right: calc(50% + 40px); }
.from-right { justify-content: flex-end; padding-left: calc(50% + 40px); }

/* 时间线节点 */
.timeline-node {
  position: absolute;
  left: 50%;
  top: 16px;
  transform: translate(-50%, 0);
  z-index: 3;
}
.node-inner {
  width: 36px; height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFB8B8, #FF9999);
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 10px rgba(255,153,153,0.4);
  border: 3px solid #FFF0F3;
}

/* ========== 日记卡片 ========== */
.diary-card {
  width: 100%;
  background: #fff;
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(200,150,150,0.12);
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.diary-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(200,140,140,0.22);
}
.card-image {
  position: relative;
  width: 100%; height: 180px;
  overflow: hidden;
  background: #FFE8EC;
}
.card-image img {
  width: 100%; height: 100%;
  object-fit: cover;
  transition: transform 0.4s ease;
}
.diary-card:hover .card-image img { transform: scale(1.05); }
.image-overlay {
  position: absolute; inset: 0;
  background: rgba(0,0,0,0.08);
  display: flex; align-items: center; justify-content: center;
  opacity: 0; transition: opacity 0.3s ease;
  color: #fff;
}
.diary-card:hover .image-overlay { opacity: 1; }

.card-body { padding: 18px 20px; }
.card-header-row { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.card-title { font-size: 17px; color: #cc6666; margin: 0; flex: 1; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.mood-badge {
  display: flex; align-items: center; gap: 4px;
  background: #FFF0F3; color: #dd8888;
  padding: 3px 10px; border-radius: 14px;
  font-size: 12px; white-space: nowrap; flex-shrink: 0;
}
.mood-emoji { font-size: 14px; }
.card-preview { font-size: 14px; color: #999; line-height: 1.6; margin: 0 0 14px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.card-footer { display: flex; justify-content: space-between; align-items: center; }
.card-date { display: flex; align-items: center; gap: 4px; font-size: 12px; color: #ccc; }
.card-actions { display: flex; gap: 6px; }
.card-actions .el-button { padding: 5px 8px; border-radius: 8px; }

/* ========== 空状态 ========== */
.empty-container {
  position: relative; z-index: 2;
  min-height: 60vh;
  display: flex; align-items: center; justify-content: center;
}
.empty-illustration {
  text-align: center;
  animation: fadeInUp 0.6s ease;
}
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(24px); }
  to { opacity: 1; transform: translateY(0); }
}
.book-svg { width: 140px; height: 110px; }
.empty-title { font-size: 20px; color: #cc8888; margin: 16px 0 8px; font-weight: 600; }
.empty-desc { font-size: 14px; color: #d4a0a0; margin: 0 0 24px; }
.empty-write-btn {
  background: linear-gradient(135deg, #FFB8B8, #FF9999);
  border: none; border-radius: 24px;
  padding: 12px 28px; color: #fff; font-size: 15px; font-weight: 600;
  cursor: pointer; transition: all 0.3s ease;
  box-shadow: 0 4px 14px rgba(255,153,153,0.35);
}
.empty-write-btn:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(255,140,140,0.45); }

/* ========== 底部 ========== */
.bottom-deco { position: relative; z-index: 2; text-align: center; padding: 20px; }
.deco-text { color: #d4a0a0; font-size: 14px; letter-spacing: 1px; }

/* ========== 详情弹窗 ========== */
.diary-detail-overlay {
  position: fixed; inset: 0; z-index: 1000;
  background: rgba(15,5,5,0.6);
  backdrop-filter: blur(6px);
  display: flex; align-items: center; justify-content: center;
  animation: fadeIn 0.25s ease;
}
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
.diary-detail-card {
  background: #fff; border-radius: 20px;
  max-width: 620px; width: 90vw; max-height: 88vh;
  overflow-y: auto; padding: 28px;
  box-shadow: 0 20px 50px rgba(0,0,0,0.25);
  animation: popIn 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}
@keyframes popIn { from { opacity: 0; transform: scale(0.9); } to { opacity: 1; transform: scale(1); } }
.detail-image { width: 100%; border-radius: 12px; overflow: hidden; margin-bottom: 20px; }
.detail-image img { width: 100%; max-height: 300px; object-fit: cover; display: block; }
.detail-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.detail-header h3 { color: #cc6666; font-size: 22px; margin: 0; }
.close-btn { background: none; border: none; color: #ccc; font-size: 20px; cursor: pointer; padding: 4px; border-radius: 50%; transition: all 0.25s; }
.close-btn:hover { color: #999; background: #f5f5f5; }
.detail-meta { display: flex; gap: 16px; margin-bottom: 18px; font-size: 13px; color: #bbb; align-items: center; }
.detail-mood { display: flex; align-items: center; gap: 4px; background: #FFF0F3; color: #dd8888; padding: 4px 12px; border-radius: 14px; }
.detail-date { display: flex; align-items: center; gap: 4px; }
.detail-content { background: #FFFAFA; border-radius: 12px; padding: 20px; line-height: 1.9; color: #666; font-size: 15px; margin-bottom: 20px; }
.detail-content p { margin: 0 0 8px; }
.detail-content p:last-child { margin-bottom: 0; }
.detail-actions { display: flex; gap: 10px; justify-content: flex-end; }

/* ========== 写日记弹窗 ========== */
:deep(.diary-dialog) { border-radius: 18px; overflow: hidden; }
:deep(.diary-dialog .el-dialog__header) {
  background: linear-gradient(135deg, #FFC0CB, #FF99AA);
  padding: 18px 24px;
}
:deep(.diary-dialog .el-dialog__title) { color: #fff; font-size: 20px; font-weight: 700; }
:deep(.diary-dialog .el-dialog__close) { color: #fff; }
:deep(.diary-dialog .el-dialog__body) { padding: 24px; position: relative; background: #FFFBFB; }

.dialog-hearts { position: absolute; inset: 0; pointer-events: none; overflow: hidden; z-index: 0; }
.dh { position: absolute; animation: heartFloat 4s ease-in-out infinite; }
@keyframes heartFloat {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-14px) scale(1.2); }
}

.diary-form { position: relative; z-index: 1; }
:deep(.diary-form .el-form-item__label) { color: #cc8888; font-weight: 500; }

/* 图片上传区域 */
.image-upload-area { width: 100%; }
.upload-trigger {
  border: 2px dashed #FFD1DC;
  border-radius: 14px;
  padding: 28px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #FFFDFD;
}
.upload-trigger:hover {
  border-color: #FFB0C0;
  background: #FFF8F9;
  transform: translateY(-2px);
}
.hidden-input { display: none; }
.upload-icon-circle {
  width: 56px; height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFE0E5, #FFD0D8);
  color: #cc8888;
  display: flex; align-items: center; justify-content: center;
  margin: 0 auto 12px;
}
.upload-hint { text-align: center; }
.hint-main { font-size: 15px; color: #cc8888; margin: 0 0 4px; font-weight: 500; }
.hint-sub { font-size: 13px; color: #d4a8a8; margin: 0; }

.image-preview-box {
  position: relative;
  border-radius: 14px; overflow: hidden;
  background: #f8f8f8;
}
.image-preview-box img { width: 100%; max-height: 200px; object-fit: cover; display: block; }
.remove-image-btn {
  position: absolute; top: 8px; right: 8px;
  width: 28px; height: 28px;
  border-radius: 50%; border: none;
  background: rgba(0,0,0,0.5); color: #fff;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  font-size: 14px; transition: all 0.2s;
}
.remove-image-btn:hover { background: rgba(255,60,60,0.7); }
.uploading-mask {
  position: absolute; inset: 0;
  background: rgba(255,255,255,0.7);
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 8px; color: #cc8888; font-size: 14px;
}
.spinner { animation: spin 1s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

/* 对话框底部按钮 */
:deep(.diary-dialog .el-dialog__footer) { padding: 16px 24px; border-top: 1px solid #FFF0F0; }
.dialog-cancel-btn {
  background: #f5f5f5; border: none; color: #999;
  border-radius: 20px; padding: 10px 24px; font-size: 14px;
}
.dialog-cancel-btn:hover { background: #eee; }
.dialog-save-btn {
  background: linear-gradient(135deg, #FFA0B0, #FF8090);
  border: none; border-radius: 20px; padding: 10px 28px;
  font-size: 14px; font-weight: 600;
}
.dialog-save-btn:hover { background: linear-gradient(135deg, #FF90A0, #FF7080); }

/* 响应式 */
@media (max-width: 768px) {
  .diary-page { padding: 20px 16px 60px; }
  .from-left, .from-right { padding: 0 0 0 44px; }
  .timeline-line { left: 18px; }
  .timeline-node { left: 18px; }
  .node-inner { width: 28px; height: 28px; font-size: 11px; }
  .page-header { flex-direction: column; gap: 12px; align-items: flex-start; }
  .detail-actions { flex-direction: column; }
}
</style>
