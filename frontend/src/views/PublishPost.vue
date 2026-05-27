<template>
  <div class="publish-post-page">
    <!-- 顶部导航栏 -->
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <h2>{{ isEdit ? '编辑帖子' : '发布帖子' }}</h2>
      </div>
      <el-button type="primary" @click="submitPost" :loading="isSubmitting">{{ isEdit ? '更新' : '发布' }}</el-button>
    </div>

    <!-- 主内容区 -->
    <div class="publish-container">
      <!-- 图片上传区域 -->
      <div class="image-upload-section">
        <h3>添加图片/视频</h3>
        <div class="upload-area">
          <el-upload
            class="upload-demo"
            action="#"
            :multiple="true"
            :on-change="handleImageChange"
            :auto-upload="false"
            list-type="picture-card"
            :limit="9"
            :disabled="isUploading"
          >
            <el-icon v-if="!isUploading"><Plus /></el-icon>
            <el-icon v-else class="uploading-icon"><Loading /></el-icon>
          </el-upload>
          
        </div>
      </div>

      <!-- 内容编辑区域 -->
      <div class="content-section">
        <!-- 标题输入 -->
        <div class="form-item">
          <el-input
            v-model="form.title"
            placeholder="添加标题"
            class="title-input"
          />
        </div>

        <!-- 分类选择 -->
        <div class="form-item">
          <span class="label">选择分类：</span>
          <div class="category-select-wrapper">
            <div class="selected-categories">
              <span 
                v-for="category in form.categories" 
                :key="category"
                class="selected-category"
              >
                {{ category }}
                <span class="remove-category" @click="toggleCategory(category)">×</span>
              </span>
              <span 
                v-if="form.categories.length === 0" 
                class="placeholder"
              >请选择分类（可多选）</span>
            </div>
            <el-button 
              type="primary" 
              size="small" 
              @click="showCategoryModal = true"
              class="select-category-btn"
            >
              <Plus class="icon" />
              选择分类
            </el-button>
          </div>
        </div>

        <!-- 内容输入 -->
        <div class="form-item">
          <el-input
            v-model="form.content"
            placeholder="分享你的故事..."
            type="textarea"
            :rows="8"
            class="content-textarea"
          />
        </div>

        <!-- 位置选择 -->
        <div class="form-item">
          <div class="location-select-container">
            <el-cascader
              v-model="locationValue"
              :options="locationData"
              placeholder="请选择省/市"
              class="location-select"
              :props="{ checkStrictly: false }"
              @change="handleLocationChange"
            />
            <el-button
              type="primary"
              @click="getCurrentLocation"
              :loading="isLocating"
              class="location-btn"
            >
              <MapLocation class="icon" />
              {{ isLocating ? '定位中...' : '一键定位' }}
            </el-button>
          </div>
        </div>

        <!-- 可见性设置 -->
        <div class="form-item">
          <span class="label">可见性：</span>
          <el-radio-group v-model="form.visibility">
            <el-radio :label="1">公开</el-radio>
            <el-radio :label="2">仅好友可见</el-radio>
            <el-radio :label="3">私密</el-radio>
          </el-radio-group>
        </div>

        <!-- 草稿保存 -->
        <div class="form-item">
          <el-button type="info" @click="saveDraft">保存草稿</el-button>
        </div>
      </div>
    </div>

    <!-- 分类选择弹窗 -->
    <div class="category-modal-overlay" v-if="showCategoryModal" @click.self="showCategoryModal = false">
      <div class="category-modal">
        <div class="modal-header">
          <h3>选择分类</h3>
          <span class="close-btn" @click="showCategoryModal = false">×</span>
        </div>
        
        <div class="modal-search">
          <el-input 
            v-model="categorySearchKeyword" 
            placeholder="搜索分类" 
            class="search-input"
          />
        </div>

        <div class="modal-body">
          <div class="category-grid">
            <span 
              v-for="category in filteredCategories" 
              :key="category"
              class="category-item"
              :class="{ selected: isCategorySelected(category) }"
              @click="toggleCategory(category)"
            >
              <Check v-if="isCategorySelected(category)" class="check-icon" />
              {{ category }}
            </span>
          </div>
        </div>

        <div class="modal-footer">
          <el-button @click="clearCategories" class="clear-btn">清空选择</el-button>
          <el-button type="primary" @click="confirmCategories" class="confirm-btn">确认选择</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft, Plus, Close, MapLocation, Check } from '@element-plus/icons-vue'
import { postApi, ossApi, userCategoryApi } from '../api/community'
import { ElMessage } from 'element-plus'
import { locationData } from '../data/locationData'

const router = useRouter()
const route = useRoute()
const postId = route.params.id
const isEdit = !!postId

// 数据
const form = ref({
  title: '',
  content: '',
  categories: [],
  location: '',
  visibility: 1
})

const isLocating = ref(false)
const locationValue = ref([])

const images = ref([])
const isSubmitting = ref(false)
const isUploading = ref(false)

// 分类相关
const allCategories = ref([])
const showCategoryModal = ref(false)
const categorySearchKeyword = ref('')

// 加载所有分类
const loadAllCategories = async () => {
  try {
    const res = await userCategoryApi.getAllCategories()
    if (res.code === 200) {
      allCategories.value = res.data.filter(c => c !== '推荐')
    }
  } catch (error) {
    console.error('加载分类失败:', error)
    allCategories.value = [
      '穿搭', '美食', '日常', '旅行', '美妆', '健身', '读书',
      '直播', '短剧', '头像', '音乐', '游戏', '舞蹈', '绘画', '摄影',
      '情感', '搞笑', '动漫', '壁纸', '手工', '影视', '学习', '健身塑型',
      '家装', '科技数码', '汽车', '男士', '明星', '科学', '职场', '减脂',
      '艺术', '家居', '婚礼', '母婴', '潮鞋', '护肤', '萌宠', '文化',
      '竞技', '机车', '户外', '心理', '体育', '文具', '综艺', '社科',
      '潮玩', '校园', '露营', '人文'
    ]
  }
}

// 筛选后的分类
const filteredCategories = computed(() => {
  if (!categorySearchKeyword.value) {
    return allCategories.value
  }
  return allCategories.value.filter(c => 
    c.includes(categorySearchKeyword.value)
  )
})

// 切换分类选择
const toggleCategory = (category) => {
  const index = form.value.categories.indexOf(category)
  if (index === -1) {
    form.value.categories.push(category)
  } else {
    form.value.categories.splice(index, 1)
  }
}

// 检查分类是否已选中
const isCategorySelected = (category) => {
  return form.value.categories.includes(category)
}

// 确认选择分类
const confirmCategories = () => {
  showCategoryModal.value = false
}

// 清空已选分类
const clearCategories = () => {
  form.value.categories = []
}

// 方法
const goBack = () => {
  if (isEdit) {
    router.push(`/community/post/${postId}`)
  } else {
    router.push('/community')
  }
}

// 一键定位功能
const getCurrentLocation = async () => {
  isLocating.value = true
  
  try {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        async (position) => {
          try {
            const { latitude, longitude } = position.coords
            // 使用百度地图API逆地理编码获取地址
            const response = await fetch(`https://api.map.baidu.com/reverse_geocoding/v3/?ak=vM5QpsM4Gcw5cvKQUK6GzEUGhN17AqTq&output=json&coordtype=wgs84ll&location=${latitude},${longitude}`)
            const data = await response.json()
            
            if (data.status === 0) {
              const address = data.result.formatted_address
              form.value.location = address
              ElMessage.success('定位成功')
              
              // 更新级联选择器的值
              updateLocationValue(address)
            } else {
              await getLocationByIP()
            }
          } catch (error) {
            console.error('获取地址失败:', error)
            await getLocationByIP()
          } finally {
            isLocating.value = false
          }
        },
        (error) => {
          console.error('GPS定位失败:', error)
          getLocationByIP()
          isLocating.value = false
        },
        {
          enableHighAccuracy: true,
          timeout: 10000,
          maximumAge: 0
        }
      )
    } else {
      // 浏览器不支持Geolocation，直接使用IP定位
      await getLocationByIP()
    }
  } catch (error) {
    console.error('定位异常:', error)
    ElMessage.error('定位失败，请手动选择位置')
    isLocating.value = false
  }
}

// 通过IP获取地址（使用百度地图IP定位API）
const getLocationByIP = async () => {
  try {
    const response = await fetch('https://api.map.baidu.com/location/ip?ak=vM5QpsM4Gcw5cvKQUK6GzEUGhN17AqTq&coor=bd09ll')
    const data = await response.json()
    
    if (data.status === 0) {
      const address = data.content.address
      if (address) {
        form.value.location = address
        ElMessage.success('已通过IP获取位置')
        updateLocationValue(address)
      }
    } else {
      ElMessage.error('无法获取位置，请手动选择')
    }
  } catch (error) {
    console.error('通过IP获取位置失败:', error)
    ElMessage.error('无法获取位置，请手动选择')
  }
}

// 根据地址更新级联选择器的值
const updateLocationValue = (address) => {
  if (!address) return
  
  let province = ''
  let city = ''
  
  // 尝试从地址中提取省和市
  const provinceList = ['北京市', '天津市', '上海市', '重庆市', '河北省', '山西省', '内蒙古自治区', '辽宁省', '吉林省', '黑龙江省', '江苏省', '浙江省', '安徽省', '福建省', '江西省', '山东省', '河南省', '湖北省', '湖南省', '广东省', '广西壮族自治区', '海南省', '四川省', '贵州省', '云南省', '西藏自治区', '陕西省', '甘肃省', '青海省', '宁夏回族自治区', '新疆维吾尔自治区', '香港特别行政区', '澳门特别行政区', '台湾省']
  
  for (const p of provinceList) {
    if (address.includes(p)) {
      province = p
      break
    }
  }
  
  // 如果找到了省份，尝试找城市
  if (province) {
    const provinceData = locationData.find(item => item.value === province)
    if (provinceData && provinceData.children) {
      for (const c of provinceData.children) {
        if (address.includes(c.value.replace('市', '')) || address.includes(c.value)) {
          city = c.value
          break
        }
      }
    }
  }
  
  // 更新级联选择器的值
  if (province && city) {
    locationValue.value = [province, city]
  } else if (province) {
    locationValue.value = [province]
  }
}

// 级联选择器值变化处理
const handleLocationChange = (value) => {
  if (value && value.length > 0) {
    form.value.location = value.join(' / ')
  } else {
    form.value.location = ''
  }
}

const handleImageChange = async (file, fileList) => {
  isUploading.value = true
  try {
    // 上传图片到OSS
    const response = await ossApi.uploadImage(file.raw)
    if (response.code === 200) {
      images.value.push(response.data)
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error(response.msg || '图片上传失败')
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    isUploading.value = false
  }
}

const saveDraft = () => {
  // 保存草稿逻辑
  localStorage.setItem('draft', JSON.stringify({
    form: form.value,
    images: images.value
  }))
  ElMessage.success('草稿已保存')
}

const submitPost = async () => {
  if (!form.value.title || !form.value.content || images.value.length === 0 || form.value.categories.length === 0) {
    ElMessage.error('请填写标题、内容、选择分类并添加图片')
    return
  }

  isSubmitting.value = true
  try {
    const formData = new FormData()
    formData.append('title', form.value.title)
    formData.append('content', form.value.content)
    formData.append('categories', JSON.stringify(form.value.categories))
    formData.append('location', form.value.location)
    formData.append('visibility', form.value.visibility)
    formData.append('images', JSON.stringify(images.value))

    let response
    if (isEdit) {
      response = await postApi.updatePost(postId, formData)
    } else {
      response = await postApi.createPost(formData)
    }

    if (response.code === 200) {
      // 清除草稿
      localStorage.removeItem('draft')
      
      // 跳转到帖子详情页
      if (isEdit) {
        router.push(`/community/post/${postId}`)
      } else {
        router.push('/community')
      }
      ElMessage.success(isEdit ? '帖子更新成功' : '帖子发布成功')
    } else {
      ElMessage.error(response.msg || (isEdit ? '更新失败' : '发布失败'))
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    isSubmitting.value = false
  }
}

// 生命周期
onMounted(async () => {
  await loadAllCategories()
  
  // 加载草稿
  const draft = localStorage.getItem('draft')
  if (draft) {
    const parsedDraft = JSON.parse(draft)
    form.value = parsedDraft.form
    images.value = parsedDraft.images
  }

  // 如果是编辑模式，加载帖子数据
  if (isEdit) {
    loadPostData()
  }
})

const loadPostData = async () => {
  try {
    const response = await postApi.getPostDetail(postId)
    if (response.code === 200) {
      const post = response.data
      form.value = {
        title: post.title,
        content: post.content,
        categories: post.categories ? (Array.isArray(post.categories) ? post.categories : JSON.parse(post.categories)) : [],
        location: post.location,
        visibility: post.visibility
      }
      images.value = post.images
    } else {
      ElMessage.error(response.msg || '加载帖子数据失败')
    }
  } catch (error) {
    console.error('加载帖子数据失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}
</script>

<style scoped>
.publish-post-page {
  min-height: 100vh;
  background: #FFFDFD;
  padding: 40px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  max-width: 800px;
  margin: 0 auto 30px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.page-header h2 {
  color: #FF9999;
  font-size: 24px;
  margin: 0;
}

.publish-container {
  max-width: 800px;
  margin: 0 auto;
  background: #F8F8F8;
  border-radius: 12px;
  padding: 30px;
}

/* 图片上传区域 */
.image-upload-section {
  margin-bottom: 30px;
}

.image-upload-section h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #333;
}

.upload-area {
  border: 2px dashed #F8E1E1;
  border-radius: 12px;
  padding: 20px;
  background: white;
}

.uploaded-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
}

.uploaded-images {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 20px;
}

.uploading-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 内容编辑区域 */
.content-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
}

.title-input .el-input__wrapper {
  border-radius: 8px;
  border: 2px solid #F8E1E1;
}

.title-input .el-input__inner {
  font-size: 18px;
  font-weight: bold;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.tag {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: #FFE6E6;
  color: #FF9999;
  border-radius: 15px;
  font-size: 14px;
}

.tag-remove {
  cursor: pointer;
  font-size: 12px;
}

.content-textarea .el-textarea__inner {
  border-radius: 8px;
  border: 2px solid #F8E1E1;
  resize: vertical;
  min-height: 200px;
}

.label {
  font-size: 16px;
  color: #333;
  margin-right: 20px;
}

.category-select {
  width: 200px;
}

.location-select-container {
  display: flex;
  gap: 15px;
  align-items: center;
}

.location-select {
  flex: 1;
}

.location-btn {
  padding: 0 20px;
  background: linear-gradient(135deg, #FF69B4 0%, #FF8FAB 100%);
  border: none;
  border-radius: 8px;
}

.location-btn:hover {
  background: linear-gradient(135deg, #FF8FAB 0%, #FFB6C1 100%);
}

/* 分类选择样式 */
.category-select-wrapper {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 10px;
}

.selected-categories {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.selected-category {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: #FFE6E6;
  color: #FF9999;
  border-radius: 15px;
  font-size: 14px;
}

.remove-category {
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
}

.remove-category:hover {
  color: #FF6666;
}

.placeholder {
  color: #999;
  font-size: 14px;
}

.select-category-btn {
  align-self: flex-start;
  background: linear-gradient(135deg, #FF69B4 0%, #FF8FAB 100%);
  border: none;
  border-radius: 8px;
}

.select-category-btn:hover {
  background: linear-gradient(135deg, #FF8FAB 0%, #FFB6C1 100%);
}

/* 分类选择弹窗 */
.category-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.category-modal {
  width: 90%;
  max-width: 600px;
  background: white;
  border-radius: 16px;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #F0F0F0;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.close-btn {
  font-size: 24px;
  color: #999;
  cursor: pointer;
}

.close-btn:hover {
  color: #666;
}

.modal-search {
  padding: 16px 24px;
  border-bottom: 1px solid #F0F0F0;
}

.search-input .el-input__wrapper {
  border-radius: 20px;
  border: 2px solid #F8E1E1;
}

.modal-body {
  padding: 20px 24px;
  max-height: 400px;
  overflow-y: auto;
}

.category-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  background: #F8F8F8;
  color: #333;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.category-item:hover {
  background: #FFE6E6;
  color: #FF9999;
}

.category-item.selected {
  background: #FF9999;
  color: white;
}

.check-icon {
  font-size: 14px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #F0F0F0;
}

.clear-btn {
  color: #FF9999;
  border: 1px solid #FF9999;
  border-radius: 8px;
}

.clear-btn:hover {
  background: #FFF0F0;
}

.confirm-btn {
  background: linear-gradient(135deg, #FF69B4 0%, #FF8FAB 100%);
  border: none;
  border-radius: 8px;
}

.confirm-btn:hover {
  background: linear-gradient(135deg, #FF8FAB 0%, #FFB6C1 100%);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .publish-post-page {
    padding: 20px;
  }
  
  .publish-container {
    padding: 20px;
  }
  
  .form-item {
    padding: 15px;
  }

  .category-modal {
    width: 95%;
  }

  .modal-body {
    max-height: 300px;
  }
}
</style>
