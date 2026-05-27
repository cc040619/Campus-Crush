<template>
  <div class="login-container" :class="theme">
    <!-- 漂浮爱心粒子背景 -->
    <div class="hearts-background">
      <div v-for="(style, index) in heartStyles" :key="index" class="floating-heart" :style="style"></div>
    </div>

    <!-- 3D翻转容器 -->
    <div class="flip-container" :class="{ 'flipped': isFlipped }">
      <div class="flipper">
        <!-- 正面：登录表单 -->
        <div class="flip-front">
          <div class="login-box">
            <!-- 主题切换按钮 -->
            <div class="theme-toggle" @click="toggleTheme">
              <el-icon v-if="theme === 'light'"><Moon /></el-icon>
              <el-icon v-else><Sunny /></el-icon>
            </div>
            <!-- 注册按钮 -->
            <div class="register-toggle" @click="toggleFlip">
              注册
            </div>
            <h1 class="title">
              <span class="heart-icon">💕</span>
              <span class="title-text">予你平安</span>
            </h1>
            <p class="subtitle">记录我们的美好时光</p>

            <!-- 登录方式切换Tab -->
            <div class="login-tabs">
              <span
                class="login-tab"
                :class="{ active: loginMode === 'password' }"
                @click="loginMode = 'password'"
              >密码登录</span>
              <span
                class="login-tab"
                :class="{ active: loginMode === 'code' }"
                @click="loginMode = 'code'"
              >验证码登录</span>
            </div>

            <!-- 密码登录表单 -->
            <div class="login-form" v-show="loginMode === 'password'">
              <div class="form__group field">
                <input
                  type="text"
                  class="form__field"
                  v-model="loginForm.username"
                  placeholder="用户名"
                  @keyup.enter="handleLogin"
                />
                <label class="form__label">用户名</label>
              </div>
              <div class="form__group field">
                <input
                  type="password"
                  class="form__field"
                  v-model="loginForm.password"
                  placeholder="密码"
                  @keyup.enter="handleLogin"
                />
                <label class="form__label">密码</label>
              </div>
              <div class="form__group">
                <el-button
                  type="primary"
                  @click="handleLogin"
                  size="large"
                  class="login-btn"
                  :loading="loginLoading"
                >
                  登录
                </el-button>
              </div>
            </div>

            <!-- 验证码登录表单 -->
            <div class="login-form" v-show="loginMode === 'code'">
              <div class="form__group field">
                <input
                  type="email"
                  class="form__field"
                  v-model="codeLoginForm.email"
                  placeholder="QQ邮箱"
                />
                <label class="form__label">QQ邮箱</label>
              </div>
              <div class="form__group field code-row">
                <input
                  type="text"
                  class="form__field code-input"
                  v-model="codeLoginForm.code"
                  placeholder="验证码"
                  maxlength="6"
                />
                <label class="form__label">验证码</label>
                <button
                  class="send-code-btn"
                  :disabled="codeCountdown > 0 || codeLoginForm.email === ''"
                  @click="handleSendCode"
                >
                  {{ codeCountdown > 0 ? codeCountdown + 's' : '发送验证码' }}
                </button>
              </div>
              <div class="form__group">
                <el-button
                  type="primary"
                  @click="handleCodeLogin"
                  size="large"
                  class="login-btn"
                  :loading="codeLoginLoading"
                >
                  登录
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 背面：注册表单 -->
        <div class="flip-back">
          <div class="login-box">
            <!-- 主题切换按钮 -->
            <div class="theme-toggle" @click="toggleTheme">
              <el-icon v-if="theme === 'light'"><Moon /></el-icon>
              <el-icon v-else><Sunny /></el-icon>
            </div>
            <!-- 返回登录按钮 -->
            <div class="register-toggle" @click="toggleFlip">
              返回登录
            </div>
            <h1 class="title">
              <span class="heart-icon">💕</span>
              <span class="title-text">加入我们</span>
            </h1>
            <p class="subtitle">开启我们的恋爱记录之旅</p>
            <div class="login-form">
              <div class="form__group field">
                <input
                  type="text"
                  class="form__field"
                  v-model="registerForm.username"
                  placeholder="用户名"
                />
                <label class="form__label">用户名</label>
              </div>
              <div class="form__group field">
                <input
                  type="email"
                  class="form__field"
                  v-model="registerForm.email"
                  placeholder="QQ邮箱（用于登录验证）"
                />
                <label class="form__label">QQ邮箱</label>
              </div>
              <div class="form__group field">
                <input
                  type="password"
                  class="form__field"
                  v-model="registerForm.password"
                  placeholder="密码（8-20位数字+字母）"
                />
                <label class="form__label">密码</label>
              </div>
              <div class="pwd-hint" v-if="registerForm.password && !isPasswordValid">
                <span class="hint-error">{{ passwordErrorMsg }}</span>
              </div>
              <div class="form__group field">
                <input
                  type="password"
                  class="form__field"
                  v-model="registerForm.confirmPassword"
                  placeholder="确认密码"
                />
                <label class="form__label">确认密码</label>
              </div>
              <div class="form__group field">
                <input
                  type="tel"
                  class="form__field"
                  v-model="registerForm.phone"
                  placeholder="手机号"
                />
                <label class="form__label">手机号</label>
              </div>
              <div class="form__group">
                <el-button
                  type="primary"
                  @click="handleRegister"
                  size="large"
                  class="login-btn"
                  :loading="registerLoading"
                >
                  注册
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register, sendLoginCode, loginByCode } from '../api'
import { saveUser, cleanupPasswordFromStorage } from '../composables/useCommon'
import { Sunny, Moon } from '@element-plus/icons-vue'

const router = useRouter()
const loginLoading = ref(false)
const codeLoginLoading = ref(false)
const registerLoading = ref(false)
const theme = ref('light')
const isFlipped = ref(false)
const loginMode = ref('password') // 'password' | 'code'
const codeCountdown = ref(0)
let countdownTimer = null

const heartStyles = ref([])

const generateHeartStyle = (index) => {
  const size = Math.random() * 20 + 10
  const left = Math.random() * 100
  const delay = Math.random() * 10
  const duration = Math.random() * 10 + 10
  const opacity = Math.random() * 0.5 + 0.2
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    animationDelay: `${delay}s`,
    animationDuration: `${duration}s`,
    opacity: opacity
  }
}

const initHeartStyles = () => {
  const styles = []
  for (let i = 0; i < 20; i++) {
    styles.push(generateHeartStyle(i))
  }
  heartStyles.value = styles
}

// 密码登录表单
const loginForm = reactive({
  username: '',
  password: ''
})

// 验证码登录表单
const codeLoginForm = reactive({
  email: '',
  code: ''
})

// 注册表单
const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  phone: ''
})

// 密码强度验证
const isPasswordValid = computed(() => {
  const pwd = registerForm.password
  if (!pwd) return true // 未输入时不提示
  if (pwd.length < 8 || pwd.length > 20) return false
  const hasDigit = /\d/.test(pwd)
  const hasLetter = /[a-zA-Z]/.test(pwd)
  return hasDigit && hasLetter
})

const passwordErrorMsg = computed(() => {
  const pwd = registerForm.password
  if (!pwd) return ''
  if (pwd.length < 8) return '密码长度不能少于8位'
  if (pwd.length > 20) return '密码长度不能超过20位'
  const hasDigit = /\d/.test(pwd)
  const hasLetter = /[a-zA-Z]/.test(pwd)
  if (!hasDigit && hasLetter) return '密码不能为纯字母，必须包含数字'
  if (hasDigit && !hasLetter) return '密码不能为纯数字，必须包含字母'
  if (!hasDigit && !hasLetter) return '密码必须包含数字和字母'
  return ''
})

// 密码登录
const handleLogin = async () => {
  if (!loginForm.username) {
    ElMessage.error('请输入用户名')
    return
  }
  if (!loginForm.password) {
    ElMessage.error('请输入密码')
    return
  }

  loginLoading.value = true
  try {
    localStorage.removeItem('user')
    const res = await login(loginForm)
    const safeUser = {
      id: res.data.id,
      username: res.data.username,
      nickname: res.data.nickname,
      avatar: res.data.avatar,
      phone: res.data.phone,
      gender: res.data.gender,
      intro: res.data.intro
    }
    saveUser(safeUser)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loginLoading.value = false
  }
}

// 发送验证码
const handleSendCode = async () => {
  const email = codeLoginForm.email.trim()
  if (!email) {
    ElMessage.error('请输入QQ邮箱地址')
    return
  }
  // QQ邮箱格式校验
  if (!/^[1-9]\d{4,10}@qq\.com$/.test(email)) {
    ElMessage.error('请输入正确的QQ邮箱地址（格式：QQ号@qq.com）')
    return
  }

  try {
    await sendLoginCode(email)
    ElMessage.success('验证码已发送，请查收QQ邮箱')
    // 60秒倒计时
    codeCountdown.value = 60
    countdownTimer = setInterval(() => {
      codeCountdown.value--
      if (codeCountdown.value <= 0) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
    }, 1000)
  } catch (error) {
    console.error('发送验证码失败:', error)
  }
}

// 验证码登录
const handleCodeLogin = async () => {
  const email = codeLoginForm.email.trim()
  const code = codeLoginForm.code.trim()
  if (!email) {
    ElMessage.error('请输入QQ邮箱地址')
    return
  }
  if (!/^[1-9]\d{4,10}@qq\.com$/.test(email)) {
    ElMessage.error('请输入正确的QQ邮箱地址')
    return
  }
  if (!code) {
    ElMessage.error('请输入验证码')
    return
  }
  if (!/^\d{6}$/.test(code)) {
    ElMessage.error('验证码为6位数字')
    return
  }

  codeLoginLoading.value = true
  try {
    localStorage.removeItem('user')
    const res = await loginByCode(email, code)
    const safeUser = {
      id: res.data.id,
      username: res.data.username,
      nickname: res.data.nickname,
      avatar: res.data.avatar,
      phone: res.data.phone,
      gender: res.data.gender,
      intro: res.data.intro
    }
    saveUser(safeUser)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error('验证码登录失败:', error)
  } finally {
    codeLoginLoading.value = false
  }
}

// 注册
const handleRegister = async () => {
  if (!registerForm.username) {
    ElMessage.error('请输入用户名')
    return
  }
  if (!registerForm.email || !/^[1-9]\d{4,10}@qq\.com$/.test(registerForm.email.trim())) {
    ElMessage.error('请输入正确的QQ邮箱地址（格式：QQ号@qq.com）')
    return
  }
  if (!registerForm.password) {
    ElMessage.error('请输入密码')
    return
  }
  if (!isPasswordValid.value) {
    ElMessage.error(passwordErrorMsg.value || '密码格式不正确')
    return
  }
  if (registerForm.password !== registerForm.confirmPassword) {
    ElMessage.error('两次输入密码不一致')
    return
  }
  if (!registerForm.phone) {
    ElMessage.error('请输入手机号')
    return
  }
  if (!/^1[3-9]\d{9}$/.test(registerForm.phone)) {
    ElMessage.error('请输入正确的手机号')
    return
  }

  registerLoading.value = true
  try {
    const res = await register({
      username: registerForm.username,
      email: registerForm.email.trim(),
      password: registerForm.password,
      phone: registerForm.phone
    })
    ElMessage.success(res.msg || '注册成功')
    toggleFlip()
    // 清空注册表单
    Object.assign(registerForm, {
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
      phone: ''
    })
  } catch (error) {
    console.error(error)
  } finally {
    registerLoading.value = false
  }
}

const toggleFlip = () => {
  isFlipped.value = !isFlipped.value
}

const toggleTheme = () => {
  theme.value = theme.value === 'light' ? 'dark' : 'light'
  localStorage.setItem('theme', theme.value)
}

onMounted(() => {
  cleanupPasswordFromStorage()
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme) theme.value = savedTheme
  initHeartStyles()
})
</script>

<style scoped>
/* ========== CSS 变量定义 ========== */
.login-container {
  --primary-color: #FF9AA2;
  --secondary-color: #FFB7B2;
  --background-color: rgba(255, 255, 255, 0.88);
  --text-color: #D66078;
  --card-border: rgba(255, 255, 255, 0.3);
  --shadow-color: rgba(255, 154, 162, 0.15);
  --input-bg: #FFFFFF;
  --input-border: #FFE6E9;

  --primary-color-dark: #FF6B8B;
  --secondary-color-dark: #FF8E9E;
  --background-color-dark: rgba(45, 27, 46, 0.88);
  --text-color-dark: #FFFFFF;
  --card-border-dark: rgba(255, 255, 255, 0.1);
  --shadow-color-dark: rgba(255, 107, 139, 0.2);
  --input-bg-dark: rgba(255, 255, 255, 0.08);
  --input-border-dark: rgba(255, 255, 255, 0.2);
}

.login-container.light {
  background-image: url('https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/EAACF3E030D6FFF787EF12E966B2C0EC.jpg');
  background-size: cover;
  background-position: center center;
  background-repeat: no-repeat;
  background-attachment: fixed;
}

.login-container.light::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(255, 230, 230, 0.3) 0%,
    rgba(255, 240, 245, 0.5) 50%,
    rgba(255, 220, 230, 0.4) 100%
  );
  pointer-events: none;
  z-index: 0;
}

.login-container.dark {
  background-image: url('https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/EAACF3E030D6FFF787EF12E966B2C0EC.jpg');
  background-size: cover;
  background-position: center center;
  background-repeat: no-repeat;
  background-attachment: fixed;

  --primary-color: var(--primary-color-dark);
  --secondary-color: var(--secondary-color-dark);
  --background-color: var(--background-color-dark);
  --text-color: var(--text-color-dark);
  --card-border: var(--card-border-dark);
  --shadow-color: var(--shadow-color-dark);
  --input-bg: var(--input-bg-dark);
  --input-border: var(--input-border-dark);
}

.login-container.dark::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(45, 27, 46, 0.6) 0%,
    rgba(58, 42, 59, 0.7) 50%,
    rgba(35, 20, 35, 0.65) 100%
  );
  pointer-events: none;
  z-index: 0;
}

.login-container.dark .login-box {
  background: rgba(45, 27, 46, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow:
    0 8px 32px rgba(255, 107, 139, 0.25),
    0 2px 8px rgba(0, 0, 0, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

/* ========== 3D翻转容器 ========== */
.flip-container {
  perspective: 1000px;
  z-index: 2;
  position: relative;
  width: 420px;
}

.flipper {
  position: relative;
  width: 100%;
  min-height: 560px;
  transition: transform 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  transform-style: preserve-3d;
}

.flip-container.flipped .flipper {
  transform: rotateY(180deg);
}

.flip-front,
.flip-back {
  backface-visibility: hidden;
  -webkit-backface-visibility: hidden;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
}

.flip-front {
  z-index: 2;
}

.flip-back {
  transform: rotateY(180deg);
  z-index: 1;
}

/* ========== 注册/切换按钮 ========== */
.register-toggle {
  position: absolute;
  top: 20px;
  left: 20px;
  padding: 8px 20px;
  background: linear-gradient(135deg, #FF9999 0%, #FFB7B2 100%);
  color: white;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  z-index: 10;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(255, 153, 153, 0.3);
}

.register-toggle:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 153, 153, 0.4);
  filter: brightness(1.1);
}

.register-toggle:active {
  transform: translateY(0);
}

/* ========== 基础布局 ========== */
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  transition: background 0.5s ease;
  cursor: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24'><path fill='%23FF9AA2' d='M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z'/></svg>") 12 12, auto;
}

/* ========== 漂浮爱心粒子背景 ========== */
.hearts-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.floating-heart {
  position: absolute;
  bottom: -50px;
  background: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%23FF9AA2'><path d='M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z'/></svg>") no-repeat center;
  background-size: contain;
  animation: floatUp linear infinite;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

@keyframes floatUp {
  0% { transform: translateY(0) rotate(0deg) scale(1); opacity: 0; }
  10% { opacity: 0.7; }
  90% { opacity: 0.3; }
  100% { transform: translateY(-100vh) rotate(360deg) scale(0.5); opacity: 0; }
}

/* ========== 登录卡片 ========== */
.login-box {
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(1px);
  -webkit-backdrop-filter: blur(1px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 24px;
  padding: 40px 40px;
  width: 100%;
  text-align: center;
  position: relative;
  z-index: 2;
  animation: cardEntrance 0.8s cubic-bezier(0.34, 1.56, 0.64, 1) both;
  box-shadow:
    0 8px 32px rgba(255, 154, 162, 0.2),
    0 2px 8px rgba(0, 0, 0, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.6);
  transition: all 0.3s ease;
}

@keyframes cardEntrance {
  0% { opacity: 0; transform: translateY(30px) scale(0.95); }
  100% { opacity: 1; transform: translateY(0) scale(1); }
}

/* ========== 主题切换按钮 ========== */
.theme-toggle {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border: 1px solid var(--card-border);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
  transition: all 0.3s ease;
  color: var(--primary-color);
}

.theme-toggle:hover {
  transform: scale(1.1);
  background: rgba(255, 255, 255, 0.3);
  box-shadow: 0 4px 12px var(--shadow-color);
}

.theme-toggle .el-icon {
  font-size: 20px;
}

/* ========== 登录方式切换Tab ========== */
.login-tabs {
  display: flex;
  justify-content: center;
  gap: 0;
  margin-bottom: 24px;
  background: rgba(255, 154, 162, 0.1);
  border-radius: 22px;
  padding: 3px;
  width: fit-content;
  margin-left: auto;
  margin-right: auto;
}

.login-tab {
  padding: 8px 28px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-color);
  cursor: pointer;
  transition: all 0.3s ease;
  opacity: 0.6;
}

.login-tab.active {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  color: #fff;
  opacity: 1;
  box-shadow: 0 2px 8px rgba(255, 154, 162, 0.3);
}

.login-tab:hover:not(.active) {
  opacity: 0.85;
}

/* ========== 标题样式 ========== */
.title {
  color: var(--primary-color);
  font-size: 32px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  animation: fadeInDown 0.8s ease-out 0.2s both;
  text-shadow: 0 2px 8px rgba(255, 154, 162, 0.2);
}

@keyframes fadeInDown {
  from { opacity: 0; transform: translateY(-20px); }
  to { opacity: 1; transform: translateY(0); }
}

.heart-icon {
  display: inline-block;
  animation: heartBeat 1.5s ease-in-out infinite;
}

@keyframes heartBeat {
  0%, 100% { transform: scale(1); }
  25% { transform: scale(1.2); }
  50% { transform: scale(1); }
  75% { transform: scale(1.1); }
}

.title-text {
  animation: fadeIn 1s ease-out 0.4s both;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.subtitle {
  color: var(--text-color);
  opacity: 0.7;
  font-size: 14px;
  margin-bottom: 20px;
  animation: fadeIn 1s ease-out 0.6s both;
}

/* ========== 表单样式 ========== */
.login-form {
  margin-top: 15px;
  width: 100%;
}

.form__group {
  position: relative;
  padding: 20px 0 0;
  width: 100%;
  max-width: 100%;
  margin-bottom: 20px;
}

.form__field {
  font-family: inherit;
  width: 100%;
  border: none;
  border-bottom: 2px solid var(--input-border);
  outline: 0;
  font-size: 17px;
  color: var(--text-color);
  padding: 7px 0;
  background: transparent;
  transition: border-color 0.2s;
}

.form__field::placeholder {
  color: transparent;
}

.form__field:placeholder-shown ~ .form__label {
  font-size: 17px;
  cursor: text;
  top: 20px;
}

.form__label {
  position: absolute;
  top: 0;
  display: block;
  transition: 0.2s;
  font-size: 17px;
  color: rgb(255, 154, 162);
  pointer-events: none;
}

.form__field:focus {
  padding-bottom: 6px;
  font-weight: 700;
  border-width: 3px;
  border-image: linear-gradient(to right, var(--primary-color), var(--secondary-color));
  border-image-slice: 1;
}

.form__field:focus ~ .form__label {
  position: absolute;
  top: 0;
  display: block;
  transition: 0.2s;
  font-size: 14px;
  color: rgb(255, 154, 162);
  font-weight: 700;
}

.form__field:required, .form__field:invalid {
  box-shadow: none;
}

/* ========== 验证码行 ========== */
.code-row {
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.code-row .code-input {
  flex: 1;
}

.send-code-btn {
  flex-shrink: 0;
  padding: 7px 14px;
  border: 1px solid var(--primary-color);
  border-radius: 18px;
  background: transparent;
  color: var(--primary-color);
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  margin-bottom: 2px;
}

.send-code-btn:hover:not(:disabled) {
  background: var(--primary-color);
  color: #fff;
}

.send-code-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
  border-color: #ccc;
  color: #999;
}

/* ========== 密码提示 ========== */
.pwd-hint {
  text-align: left;
  padding: 0 4px;
  margin-top: -14px;
  margin-bottom: 4px;
}

.hint-error {
  color: #FF6B6B;
  font-size: 12px;
  animation: fadeIn 0.3s ease;
}

/* ========== 登录按钮 ========== */
.login-btn {
  width: 100%;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  border: none;
  font-size: 16px;
  font-weight: 600;
  border-radius: 14px;
  padding: 15px 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  color: white;
  margin-top: 10px;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 12px 30px rgba(255, 154, 162, 0.4);
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
  filter: brightness(1.1);
}

.login-btn:active:not(:disabled) {
  transform: translateY(-1px) scale(0.98);
  box-shadow: 0 6px 20px rgba(255, 154, 162, 0.3);
}

.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

.login-container.dark .register-toggle {
  background: linear-gradient(135deg, #FF6B8B 0%, #FF8E9E 100%);
  box-shadow: 0 4px 12px rgba(255, 107, 139, 0.4);
}

.login-container.dark .register-toggle:hover {
  box-shadow: 0 6px 16px rgba(255, 107, 139, 0.5);
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .login-box {
    width: 90%;
    padding: 40px 25px;
    backdrop-filter: blur(8px);
  }

  .title {
    font-size: 26px;
  }

  .floating-heart {
    display: none;
  }

  .theme-toggle {
    top: 15px;
    right: 15px;
    width: 36px;
    height: 36px;
  }
}

@media (max-width: 480px) {
  .login-box {
    width: 95%;
    padding: 30px 20px;
    border-radius: 20px;
  }

  .title {
    font-size: 22px;
  }

  .subtitle {
    font-size: 13px;
    margin-bottom: 25px;
  }

  .login-btn {
    border-radius: 12px;
    padding: 14px 20px;
  }
}

@supports not (backdrop-filter: blur(10px)) {
  .login-box {
    background: rgba(255, 255, 255, 0.7);
  }

  .login-container.dark .login-box {
    background: rgba(45, 27, 46, 0.7);
  }

  .theme-toggle {
    background: rgba(255, 255, 255, 0.9);
  }

  .login-container.dark .theme-toggle {
    background: rgba(45, 27, 46, 0.9);
  }
}
</style>
