<template>
  <div class="personal-container">
    <!-- 顶部导航栏 -->
    <div class="navbar">
      <div class="navbar-left">
        <el-button type="default" class="back-home-btn" @click="handleBackHome">
          <el-icon><House /></el-icon>
          返回首页
        </el-button>
      </div>
      <h1 class="app-title">个人主页</h1>
      <div class="navbar-right">
        <el-button type="danger" @click="handleLogout">退出登录</el-button>
      </div>
    </div>

    <!-- 爱心粒子背景 -->
    <div class="particles-container">
      <div 
        v-for="(particle, index) in particles" 
        :key="index"
        class="particle"
        :style="{
          left: particle.x + 'px',
          top: particle.y + 'px',
          width: particle.size + 'px',
          height: particle.size + 'px',
          backgroundColor: particle.color,
          opacity: particle.opacity
        }"
      ></div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <div class="personal-card" @mouseenter="isCardHovered = true" @mouseleave="isCardHovered = false">
        <!-- 左侧用户信息 -->
        <div class="user-info">
          <!-- 头像 -->
          <div class="avatar-container" @click="handleAvatarUpload">
            <img 
              :src="userInfo.avatar || defaultAvatar" 
              alt="用户头像" 
              class="avatar"
            />
            <div class="avatar-overlay">
              <el-icon><Camera /></el-icon>
              <span>更换头像</span>
            </div>
          </div>
          
          <!-- 用户信息 -->
          <div class="info-details">
            <h2 class="nickname">{{ userInfo.nickname || '未设置昵称' }}</h2>
            <p class="username">用户名：{{ userInfo.username }}</p>
            <p class="phone" v-if="userInfo.phone">手机号：{{ userInfo.phone }}</p>
            <p class="email-info" v-if="userInfo.email">
              <el-icon><Message /></el-icon>
              邮箱：{{ userInfo.email }}
            </p>
            <p class="email-info email-empty" v-else>
              <el-icon><Message /></el-icon>
              未设置邮箱
            </p>
            <p class="intro">欢迎来到个人空间，记录属于你们的美好时光！</p>
          </div>
        </div>

        <!-- 右侧数据统计 -->
        <div class="stats-container">
          <h3 class="stats-title">我的记录</h3>
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon><Calendar /></el-icon>
              </div>
              <div class="stat-info">
                <h4>纪念日</h4>
                <p class="stat-number">{{ stats.anniversaryCount }}</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon><Picture /></el-icon>
              </div>
              <div class="stat-info">
                <h4>相册</h4>
                <p class="stat-number">{{ stats.albumCount }}</p>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <h4>日记</h4>
                <p class="stat-number">{{ stats.diaryCount }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" class="action-btn" @click="handleUpdateNickname">
          <el-icon><Edit /></el-icon>
          修改昵称
        </el-button>
        <el-button type="primary" class="action-btn" @click="handleAvatarUpload">
          <el-icon><Camera /></el-icon>
          上传头像
        </el-button>
        <el-button type="primary" class="action-btn" @click="handleUpdatePassword">
          <el-icon><Lock /></el-icon>
          修改密码
        </el-button>
        <el-button type="primary" class="action-btn" @click="handleEmailSettings">
          <el-icon><Message /></el-icon>
          邮箱设置
        </el-button>
      </div>
    </div>

    <!-- 修改昵称对话框 -->
    <el-dialog
      v-model="nicknameDialogVisible"
      title="修改昵称"
      width="400px"
    >
      <el-form :model="nicknameForm" :rules="nicknameRules" ref="nicknameFormRef">
        <el-form-item label="新昵称" prop="nickname">
          <el-input v-model="nicknameForm.nickname" placeholder="请输入新昵称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="nicknameDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitNickname" :loading="nicknameLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 头像上传对话框 -->
    <el-dialog
      v-model="avatarDialogVisible"
      title="上传头像"
      width="400px"
    >
      <div class="avatar-upload-container">
        <img 
          :src="previewAvatar || userInfo.avatar || defaultAvatar" 
          alt="预览头像" 
          class="preview-avatar"
        />
        <el-upload
          class="avatar-uploader"
          action=""
          :auto-upload="false"
          :on-change="handleFileChange"
          :show-file-list="false"
          accept=".jpg,.png"
        >
          <el-button type="primary">选择图片</el-button>
        </el-upload>
        <p class="upload-tip">仅支持jpg和png格式，大小不超过10MB</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeAvatarDialog">取消</el-button>
          <el-button type="primary" @click="submitAvatar" :loading="avatarLoading">
            确定上传
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="400px"
    >
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="80px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password" 
            placeholder="请输入原密码" 
            show-password
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            placeholder="请输入新密码(6-20位)" 
            show-password
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入新密码" 
            show-password
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPassword" :loading="passwordLoading">
            确认修改
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 邮箱设置对话框 -->
    <el-dialog
      v-model="emailDialogVisible"
      :title="userInfo.email ? '修改邮箱' : '绑定邮箱'"
      width="450px"
      :close-on-click-modal="false"
    >
      <el-form :model="emailForm" :rules="emailRules" ref="emailFormRef" label-width="80px">
        <el-form-item v-if="userInfo.email" label="当前邮箱">
          <span class="current-email">{{ userInfo.email }}</span>
        </el-form-item>
        <el-form-item label="QQ邮箱" prop="email">
          <el-input v-model="emailForm.email" placeholder="请输入QQ邮箱地址" />
        </el-form-item>
        <el-form-item label="验证码">
          <div class="code-input-row">
            <el-input v-model="emailForm.code" placeholder="请输入验证码" style="flex: 1" />
            <el-button
              type="primary"
              :disabled="emailCodeSending || emailCountdown > 0"
              @click="handleSendBindCode"
              :loading="emailCodeSending"
            >
              {{ emailCountdown > 0 ? emailCountdown + 's' : '发送验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="emailDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitBindEmail" :loading="emailSubmitting">
            确认{{ userInfo.email ? '修改' : '绑定' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 隐藏的文件输入 -->
    <input
      type="file"
      ref="fileInput"
      style="display: none"
      accept=".jpg,.png"
      @change="handleFileInputChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Camera, Timer, Calendar, Picture, Document, Edit, House, Lock, Message } from '@element-plus/icons-vue';
import { getUserInfo, updateNickname, updatePassword, uploadAvatar, sendBindCode, bindEmail } from '../api';
import { getCurrentUser, saveUser } from '../composables/useCommon';

// 路由
const router = useRouter();

// 用户信息
const userInfo = ref({
  id: null,
  username: '',
  nickname: '',
  avatar: '',
  phone: ''
});

// 默认头像
const defaultAvatar = 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/default_avatar.jpg';

// 数据统计
const stats = ref({
  anniversaryCount: 0,
  albumCount: 0,
  diaryCount: 0
});

// 对话框状态
const nicknameDialogVisible = ref(false);
const avatarDialogVisible = ref(false);
const passwordDialogVisible = ref(false);

// 加载状态
const nicknameLoading = ref(false);
const avatarLoading = ref(false);
const passwordLoading = ref(false);

// 邮箱设置
const emailDialogVisible = ref(false);
const emailSubmitting = ref(false);
const emailCodeSending = ref(false);
const emailCountdown = ref(0);
let emailTimer = null;
const emailForm = ref({
  email: '',
  code: ''
});
const emailFormRef = ref(null);
const emailRules = {
  email: [
    { required: true, message: '请输入QQ邮箱地址', trigger: 'blur' },
    { pattern: /^[1-9]\d{4,10}@qq\.com$/, message: '请输入正确的QQ邮箱格式', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ]
};

// 表单数据
const nicknameForm = ref({
  nickname: ''
});

// 密码表单数据
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 密码表单验证规则
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入新密码'));
  } else if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的新密码不一致'));
  } else {
    callback();
  }
};

const passwordRules = ref({
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '新密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
});

// 表单引用
const nicknameFormRef = ref(null);
const passwordFormRef = ref(null);
const fileInput = ref(null);

// 预览头像
const previewAvatar = ref(null);
let selectedFile = null;

// 爱心粒子
const particles = ref([]);
let animationId = null;

// 物理参数
const physics = {
  gravity: 0, // 取消重力，实现匀速运动
  friction: 1, // 无摩擦，保持匀速
  bounce: 1 // 完全弹性碰撞
};

// 生成爱心粒子
const generateParticles = () => {
  const newParticles = [];
  for (let i = 0; i < 50; i++) {
    newParticles.push({
      x: Math.random() * window.innerWidth,
      y: -20, // 从顶部开始
      vx: (Math.random() - 0.5) * 1.5, // 水平速度，减慢
      vy: 1.5 + Math.random() * 1, // 垂直速度，匀速下坠，减慢
      size: 10 + Math.random() * 20,
      opacity: 0.3 + Math.random() * 0.7,
      color: `rgba(255, ${150 + Math.random() * 105}, ${180 + Math.random() * 75}, ${0.3 + Math.random() * 0.7})`
    });
  }
  particles.value = newParticles;
};

// 卡片悬停状态
const isCardHovered = ref(false);

// 物理引擎更新
const updatePhysics = () => {
  const width = window.innerWidth;
  const height = window.innerHeight;
  
  // 更新每个粒子的位置和速度
  particles.value.forEach((particle, index) => {
    // 应用重力
    particle.vy += physics.gravity;
    
    // 应用摩擦力
    particle.vx *= physics.friction;
    particle.vy *= physics.friction;
    
    // 鼠标悬停在卡片上时，对应区域的球体加速弹起
    if (isCardHovered.value) {
      // 计算粒子与卡片中心的距离
      const cardCenterX = width / 2;
      const cardCenterY = height / 2;
      const dx = particle.x - cardCenterX;
      const dy = particle.y - cardCenterY;
      const distance = Math.sqrt(dx * dx + dy * dy);
      
      // 如果粒子在卡片附近，给它一个向上的力
      if (distance < 300) {
        const force = (300 - distance) / 300 * 0.5;
        particle.vy -= force;
        particle.vx += (Math.random() - 0.5) * force * 0.5;
      }
    }
    
    // 更新位置
    particle.x += particle.vx;
    particle.y += particle.vy;
    
    // 边界碰撞检测
    if (particle.x - particle.size < 0) {
      particle.x = particle.size;
      particle.vx *= -physics.bounce;
    } else if (particle.x + particle.size > width) {
      particle.x = width - particle.size;
      particle.vx *= -physics.bounce;
    }
    
    if (particle.y - particle.size < 0) {
      particle.y = particle.size;
      particle.vy *= -physics.bounce;
    } else if (particle.y + particle.size > height) {
      particle.y = height - particle.size;
      particle.vy *= -physics.bounce;
    }
    
    // 粒子之间的碰撞检测
    for (let j = index + 1; j < particles.value.length; j++) {
      const other = particles.value[j];
      const dx = particle.x - other.x;
      const dy = particle.y - other.y;
      const distance = Math.sqrt(dx * dx + dy * dy);
      const minDistance = particle.size + other.size;
      
      if (distance < minDistance) {
        // 碰撞响应
        const angle = Math.atan2(dy, dx);
        const overlap = minDistance - distance;
        
        // 分离粒子
        const pushX = Math.cos(angle) * overlap * 0.5;
        const pushY = Math.sin(angle) * overlap * 0.5;
        
        particle.x += pushX;
        particle.y += pushY;
        other.x -= pushX;
        other.y -= pushY;
        
        // 交换速度
        const tempVx = particle.vx;
        const tempVy = particle.vy;
        particle.vx = other.vx * physics.bounce;
        particle.vy = other.vy * physics.bounce;
        other.vx = tempVx * physics.bounce;
        other.vy = tempVy * physics.bounce;
      }
    }
  });
  
  // 继续下一帧
  animationId = requestAnimationFrame(updatePhysics);
};

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const response = await getUserInfo();
    if (response.code === 200) {
      userInfo.value = response.data;
    } else {
      ElMessage.error(response.msg || '获取用户信息失败');
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败');
    console.error('获取用户信息失败:', error);
  }
};

// 修改昵称
const handleUpdateNickname = () => {
  nicknameForm.value.nickname = userInfo.value.nickname || '';
  nicknameDialogVisible.value = true;
};

// 提交昵称修改
const submitNickname = async () => {
  if (!nicknameFormRef.value) return;
  
  await nicknameFormRef.value.validate(async (valid) => {
    if (valid) {
      nicknameLoading.value = true;
      try {
        const response = await updateNickname(nicknameForm.value.nickname);
        if (response.code === 200) {
          ElMessage.success('昵称修改成功');
          userInfo.value.nickname = nicknameForm.value.nickname;
          // 更新localStorage中的用户信息
          const userObj = getCurrentUser();
          if (userObj.id) {
            userObj.nickname = nicknameForm.value.nickname;
            saveUser(userObj);
          }
          nicknameDialogVisible.value = false;
        } else {
          ElMessage.error(response.msg || '昵称修改失败');
        }
      } catch (error) {
        ElMessage.error('昵称修改失败');
        console.error('修改昵称失败:', error);
      } finally {
        nicknameLoading.value = false;
      }
    }
  });
};

// 修改密码
const handleUpdatePassword = () => {
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  };
  passwordDialogVisible.value = true;
};

// 提交密码修改
const submitPassword = async () => {
  if (!passwordFormRef.value) return;
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      passwordLoading.value = true;
      try {
        const response = await updatePassword(passwordForm.value.oldPassword, passwordForm.value.newPassword);
        if (response.code === 200) {
          ElMessage.success('密码修改成功，请重新登录');
          passwordDialogVisible.value = false;
          // Token 是 HttpOnly Cookie，由后端登出接口清除
          fetch('/api/user/logout', { method: 'POST', credentials: 'include' }).catch(() => {})
          localStorage.removeItem('user');
          router.push('/login');
        } else {
          ElMessage.error(response.msg || '密码修改失败');
        }
      } catch (error) {
        ElMessage.error('密码修改失败');
        console.error('修改密码失败:', error);
      } finally {
        passwordLoading.value = false;
      }
    }
  });
};

// 打开邮箱设置弹窗
const handleEmailSettings = () => {
  emailForm.value.email = userInfo.value.email || '';
  emailForm.value.code = '';
  emailDialogVisible.value = true;
};

// 发送绑定邮箱验证码
const handleSendBindCode = async () => {
  const email = emailForm.value.email.trim();
  if (!email) {
    ElMessage.error('请输入QQ邮箱地址');
    return;
  }
  if (!/^[1-9]\d{4,10}@qq\.com$/.test(email)) {
    ElMessage.error('请输入正确的QQ邮箱格式');
    return;
  }
  emailCodeSending.value = true;
  try {
    const res = await sendBindCode(email);
    if (res.code === 200) {
      ElMessage.success('验证码已发送至 ' + email);
      emailCountdown.value = 60;
      if (emailTimer) clearInterval(emailTimer);
      emailTimer = setInterval(() => {
        emailCountdown.value--;
        if (emailCountdown.value <= 0) {
          clearInterval(emailTimer);
          emailTimer = null;
        }
      }, 1000);
    } else {
      ElMessage.error(res.msg || '验证码发送失败');
    }
  } catch (e) {
    ElMessage.error('验证码发送失败');
  } finally {
    emailCodeSending.value = false;
  }
};

// 提交绑定邮箱
const submitBindEmail = async () => {
  if (!emailFormRef.value) return;
  try {
    await emailFormRef.value.validate();
    emailSubmitting.value = true;
    const res = await bindEmail(emailForm.value.email.trim(), emailForm.value.code.trim());
    if (res.code === 200) {
      ElMessage.success(res.msg || '操作成功');
      emailDialogVisible.value = false;
      // 更新本地用户信息
      userInfo.value.email = emailForm.value.email.trim();
      const currentUser = getCurrentUser();
      if (currentUser && currentUser.id) {
        currentUser.email = emailForm.value.email.trim();
        saveUser(currentUser);
      }
    } else {
      ElMessage.error(res.msg || '操作失败');
    }
  } catch (e) {
    ElMessage.error('绑定失败');
  } finally {
    emailSubmitting.value = false;
  }
};

// 清理邮箱定时器（在 onUnmounted 中调用）
const clearEmailTimer = () => {
  if (emailTimer) {
    clearInterval(emailTimer);
    emailTimer = null;
  }
};

// 处理头像上传
const handleAvatarUpload = () => {
  if (fileInput.value) {
    fileInput.value.click();
  }
};

// 处理文件输入变化
const handleFileInputChange = (e) => {
  const file = e.target.files[0];
  if (file) {
    // 检查文件类型
    if (!file.name.endsWith('.jpg') && !file.name.endsWith('.png')) {
      ElMessage.error('仅支持jpg和png格式的图片');
      return;
    }
    
    // 检查文件大小
    if (file.size > 10 * 1024 * 1024) {
      ElMessage.error('文件大小不能超过10MB');
      return;
    }
    
    // 生成预览
    const reader = new FileReader();
    reader.onload = (e) => {
      previewAvatar.value = e.target.result;
      selectedFile = file;
      avatarDialogVisible.value = true;
    };
    reader.readAsDataURL(file);
  }
};

// 处理上传组件的文件变化
const handleFileChange = (file) => {
  if (file.raw) {
    // 检查文件类型
    if (!file.raw.name.endsWith('.jpg') && !file.raw.name.endsWith('.png')) {
      ElMessage.error('仅支持jpg和png格式的图片');
      return;
    }
    
    // 检查文件大小
    if (file.raw.size > 10 * 1024 * 1024) {
      ElMessage.error('文件大小不能超过10MB');
      return;
    }
    
    // 生成预览
    const reader = new FileReader();
    reader.onload = (e) => {
      previewAvatar.value = e.target.result;
      selectedFile = file.raw;
    };
    reader.readAsDataURL(file.raw);
  }
};

// 提交头像上传
const submitAvatar = async () => {
  if (!selectedFile) {
    ElMessage.error('请选择要上传的图片');
    return;
  }
  
  avatarLoading.value = true;
  try {
    const response = await uploadAvatar(selectedFile);
    if (response.code === 200) {
      ElMessage.success('头像上传成功');
      userInfo.value.avatar = response.data;
      // 更新localStorage中的用户信息，使其他页面能实时显示新头像
      const userObj = getCurrentUser();
      if (userObj.id) {
        userObj.avatar = response.data;
        saveUser(userObj);
      }
      closeAvatarDialog();
    } else {
      ElMessage.error(response.msg || '头像上传失败');
    }
  } catch (error) {
    ElMessage.error('头像上传失败');
    console.error('上传头像失败:', error);
  } finally {
    avatarLoading.value = false;
  }
};

// 关闭头像上传对话框
const closeAvatarDialog = () => {
  avatarDialogVisible.value = false;
  previewAvatar.value = null;
  selectedFile = null;
  // 重置文件输入
  if (fileInput.value) {
    fileInput.value.value = '';
  }
};

// 返回首页
const handleBackHome = () => {
  router.push('/');
};

const handleLogout = async () => {
  try {
    await fetch('/api/user/logout', { method: 'POST', credentials: 'include' })
  } catch (e) {
    // 即使后端调用失败也清除本地状态
  }
  localStorage.removeItem('user');
  router.push('/login');
};

// 获取数据统计
const getStats = async () => {
  try {
    // 调用与首页相同的统计接口
    const response = await import('../api').then(m => m.getStatistics());
    if (response.code === 200) {
      // 适配统计数据格式
      stats.value = {
        anniversaryCount: response.data.anniversaryCount || 0,
        albumCount: response.data.albumCount || 0,
        diaryCount: response.data.diaryCount || 0
      };
    }
  } catch (error) {
    console.error('获取统计数据失败:', error);
  }
};

// 页面加载时执行
onMounted(() => {
  // 页面加载时滚动到顶部
  window.scrollTo({
    top: 0,
    left: 0,
    behavior: 'instant' // 无动画瞬间定位，避免视觉跳动
  });
  // 兼容页面容器滚动的情况
  const container = document.querySelector('.personal-container');
  if (container) {
    container.scrollTop = 0;
  }
  
  generateParticles();
  fetchUserInfo();
  getStats();
  
  // 启动物理引擎
  updatePhysics();
  
  // 监听窗口大小变化，重新生成粒子
  window.addEventListener('resize', generateParticles);
});

// 组件卸载时清理
onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId);
  }
  clearEmailTimer();
  window.removeEventListener('resize', generateParticles);
});
</script>

<style scoped>
.personal-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #FFF0F5 0%, #FFE4E9 25%, #FFD1DC 50%, #FFC0CB 75%, #FFE6F2 100%);
  background-attachment: fixed;
  background-size: cover;
  position: relative;
  padding: 20px;
  overflow: hidden;
}

.personal-container::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    radial-gradient(circle at 30% 20%, rgba(255, 180, 180, 0.35) 0%, transparent 50%),
    radial-gradient(circle at 70% 30%, rgba(255, 160, 190, 0.4) 0%, transparent 50%),
    radial-gradient(circle at 50% 70%, rgba(255, 190, 200, 0.3) 0%, transparent 40%),
    radial-gradient(circle at 15% 85%, rgba(255, 170, 185, 0.35) 0%, transparent 45%);
  pointer-events: none;
  animation: float 25s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  50% {
    transform: translate(-2%, 2%) rotate(-1deg);
  }
}

/* 爱心粒子背景 */
.particles-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.particle {
  position: absolute;
  border-radius: 50%;
  box-shadow: 0 0 20px rgba(255, 192, 203, 0.8), 0 0 40px rgba(255, 192, 203, 0.4), inset 0 0 10px rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
}

.particle:hover {
  transform: scale(1.2);
  box-shadow: 0 0 30px rgba(255, 192, 203, 1), 0 0 60px rgba(255, 192, 203, 0.6), inset 0 0 15px rgba(255, 255, 255, 0.8);
}

/* 顶部导航栏 */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 192, 203, 0.8);
  backdrop-filter: blur(3px);
  -webkit-backdrop-filter: blur(3px);
  border-radius: 10px;
  padding: 15px 30px;
  margin-bottom: 30px;
  box-shadow: 0 4px 15px rgba(255, 192, 203, 0.3);
  position: relative;
  z-index: 10;
}

.navbar-left {
  flex: 1;
  display: flex;
  justify-content: flex-start;
}

.navbar-right {
  flex: 1;
  display: flex;
  justify-content: flex-end;
}

.app-title {
  flex: 1;
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  color: #fff;
  margin: 0;
}

.back-home-btn {
  background: rgba(255, 255, 255, 0.9) !important;
  border-color: #FF9999 !important;
  color: #FF9999 !important;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.back-home-btn:hover {
  background: #FF9999 !important;
  color: #fff !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 153, 153, 0.4);
}

.nav-actions {
  display: flex;
  gap: 10px;
}

/* 主体内容 */
.main-content {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
}

.personal-card {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 20px;
  border: 2px solid rgba(255, 192, 203, 0.5);
  padding: 40px;
  margin-bottom: 30px;
  box-shadow: 0 8px 32px rgba(255, 192, 203, 0.2);
  display: flex;
  flex-wrap: wrap;
  gap: 40px;
  transition: all 0.3s ease;
}

.personal-card:hover {
  box-shadow: 0 12px 40px rgba(255, 192, 203, 0.3);
  transform: translateY(-5px);
}

/* 左侧用户信息 */
.user-info {
  flex: 1;
  min-width: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

/* 头像 */
.avatar-container {
  position: relative;
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.avatar-container:hover {
  transform: scale(1.05);
  box-shadow: 0 0 20px rgba(255, 192, 203, 0.8);
}

.avatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #FFC0CB;
  transition: all 0.3s ease;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  color: white;
}

.avatar-container:hover .avatar-overlay {
  opacity: 1;
}

/* 用户信息详情 */
.info-details {
  width: 100%;
}

.nickname {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin: 0 0 10px 0;
}

.username {
  font-size: 16px;
  color: #666;
  margin: 0 0 5px 0;
}

.phone {
  font-size: 14px;
  color: #999;
  margin: 0 0 5px 0;
}

.email-info {
  font-size: 14px;
  color: #666;
  margin: 0 0 5px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.email-info.email-empty {
  color: #ccc;
}

.current-email {
  color: #999;
  font-size: 14px;
}

.code-input-row {
  display: flex;
  gap: 10px;
  align-items: center;
}

.code-input-row .el-button {
  flex-shrink: 0;
}

.intro {
  font-size: 14px;
  color: #666;
  margin: 0;
  line-height: 1.5;
}

/* 右侧数据统计 */
.stats-container {
  flex: 2;
  min-width: 500px;
}

.stats-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin: 0 0 20px 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  border-radius: 15px;
  border: 1px solid rgba(255, 192, 203, 0.3);
  padding: 20px;
  text-align: center;
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(255, 192, 203, 0.1);
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(255, 192, 203, 0.2);
  border-color: #FFC0CB;
}

.stat-icon {
  font-size: 32px;
  color: #FF9999;
  margin-bottom: 10px;
}

.stat-info h4 {
  font-size: 16px;
  color: #333;
  margin: 0 0 10px 0;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #FF9999;
  margin: 0;
  animation: countUp 2s ease-out;
}

@keyframes countUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  position: relative;
  z-index: 1;
}

.action-btn {
  background: #FF9999;
  border-color: #FF9999;
  color: white;
  border-radius: 20px;
  padding: 10px 30px;
  font-size: 16px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(255, 153, 153, 0.3);
}

.action-btn:hover {
  background: #FF6666;
  border-color: #FF6666;
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(255, 153, 153, 0.4);
}

.action-btn.default {
  background: #fff;
  border-color: #FFC0CB;
  color: #FF9999;
}

.action-btn.default:hover {
  background: #FFE6E6;
  border-color: #FF9999;
  color: #FF6666;
}

/* 头像上传容器 */
.avatar-upload-container {
  text-align: center;
}

.preview-avatar {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #FFC0CB;
  margin-bottom: 20px;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .personal-container {
    padding: 10px;
  }
  
  .navbar {
    padding: 10px 20px;
  }
  
  .app-title {
    font-size: 20px;
  }
  
  .personal-card {
    flex-direction: column;
    padding: 20px;
    gap: 30px;
  }
  
  .user-info,
  .stats-container {
    min-width: 100%;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .action-buttons {
    flex-wrap: wrap;
  }
  
  .action-btn {
    flex: 1;
    min-width: 120px;
  }
}
</style>
