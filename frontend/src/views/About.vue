<template>
  <div class="about-page">
    <!-- 背景层 -->
    <div class="about-background">
      <div class="bg-image" :style="{ backgroundImage: `url('${bgUrl}')` }"></div>
      <div class="bg-gradient"></div>
    </div>

    <!-- 浮动装饰 -->
    <div class="floating-hearts">
      <span v-for="n in 12" :key="n" class="fh" :style="getHeartStyle(n)">💕</span>
    </div>

    <!-- 返回按钮 -->
    <button class="back-fab" @click="router.push('/')" title="返回首页">
      <el-icon :size="20"><ArrowLeft /></el-icon>
    </button>

    <!-- 主内容 -->
    <div class="about-content">
      <!-- 卡片 1：关于本站 -->
      <div class="about-card card-1">
        <div class="card-icon-circle">
          <svg viewBox="0 0 24 24" width="40" height="40" fill="none">
            <path d="M12 21.35L10.55 20.03C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.08C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.53L12 21.35z" fill="#fff" opacity="0.9"/>
          </svg>
        </div>
        <h2>关于本站</h2>
        <div class="divider"></div>
        <p class="about-text">
          这是一个为<b>情侣们</b>精心打造的小小世界。
        </p>
        <p class="about-text">
          在这里，你们可以<b>记录恋爱中的每一个甜蜜瞬间</b>——
          上传珍贵的合照，写下温暖的日记，打卡每一次约会，纪念每一个重要的日子。
        </p>
        <p class="about-text">
          希望这个小站，能成为你们爱情路上的<b>一本温暖的手账</b>，
          记录欢笑、记录感动、记录那些值得被记住的点点滴滴。
        </p>
        <p class="about-text accent">
          🌸 愿你们的爱情，每一天都如初见般美好。
        </p>
      </div>

      <!-- 卡片 2：关于作者 -->
      <div class="about-card card-2">
        <div class="card-icon-circle">
          <svg viewBox="0 0 24 24" width="40" height="40" fill="none">
            <circle cx="12" cy="8" r="4" fill="#fff" opacity="0.9"/>
            <path d="M4 20c0-4 4-7 8-7s8 3 8 7" fill="#fff" opacity="0.9"/>
          </svg>
        </div>
        <h2>关于作者</h2>
        <div class="divider"></div>
        <p class="about-text">
          我是一个热爱生活的<b>程序员</b>，也是一个相信爱情的人。
        </p>
        <p class="about-text">
          这个网站源于一个简单的想法：<b>用代码为爱情写一本日记</b>。
          在无数个深夜，我敲下一行行代码，想象着每一对情侣在这里留下的故事。
        </p>
        <p class="about-text">
          技术或许冰冷，但<b>爱让一切有了温度</b>。
          希望这个小站能承载你们的甜蜜与幸福，成为你们爱情故事的一部分。
        </p>
        <div class="author-tags">
          <span class="tag">💻 全栈开发</span>
          <span class="tag">💝 浪漫主义</span>
          <span class="tag">🌟 终身学习者</span>
        </div>
      </div>

      <!-- 卡片 3：给所有情侣的祝福 -->
      <div class="about-card card-3 blessing-card">
        <div class="card-icon-circle">
          <svg viewBox="0 0 24 24" width="40" height="40" fill="none">
            <path d="M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z" fill="#fff" opacity="0.9"/>
          </svg>
        </div>
        <h2>写给所有情侣</h2>
        <div class="divider"></div>
        <div class="blessing-text">
          <p>🌙 <b>致正在相爱的你们：</b></p>
          <p>
            爱情从来不是轰轰烈烈的电影，而是<b>每一个平凡日子里的温柔注视</b>。
          </p>
          <p>
            是深夜为你留下的那盏灯，是难过时默默递来的纸巾，
            是争吵后依然紧紧牵起的手，是无数个"你在干嘛"背后的想念。
          </p>
          <p>
            <b>不必追求完美的爱情</b>，因为真正珍贵的，恰恰是那些不完美的瞬间——
            他打呼噜吵得你睡不着，你做饭太咸他却说好吃，
            这些看似琐碎的日常，才是爱情最真实的样子。
          </p>
          <p>
            希望你们不只是彼此的恋人，更是<b>最好的朋友、最坚实的依靠</b>。
            在漫长岁月里，一起看日出日落，一起经历春夏秋冬。
          </p>
          <p class="blessing-highlight">
            💕 愿有岁月可回首，且以深情共白头 💕
          </p>
          <p class="blessing-signature">—— 来自一个默默祝福你们的小站长</p>
        </div>
      </div>
    </div>

    <!-- 底部 -->
    <div class="about-footer">
      <span>Campus Crush © 2025</span>
      <span class="footer-sep">|</span>
      <span>用 ❤️ 构建</span>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const bgUrl = 'https://web-ai-cc.oss-cn-beijing.aliyuncs.com/campus_crush/default/GPT%20Image%202_1779760690230_0.png'

const getHeartStyle = (n) => ({
  left: ((n * 73 + 11) % 94) + '%',
  top: ((n * 47 + 5) % 88) + '%',
  fontSize: (14 + (n % 18)) + 'px',
  animationDelay: (n * 0.35) + 's',
  animationDuration: (3 + n * 0.4) + 's'
})
</script>

<style scoped>
.about-page {
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
  padding: 0 0 60px;
}

/* ========== 背景 ========== */
.about-background {
  position: fixed;
  inset: 0;
  z-index: -1;
}
.bg-image {
  position: absolute; inset: 0;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  filter: brightness(1.05);
}
.bg-gradient {
  position: absolute; inset: 0;
  background: linear-gradient(180deg,
    rgba(255,235,235,0.45) 0%,
    rgba(255,245,245,0.55) 40%,
    rgba(255,250,250,0.65) 100%
  );
}

/* ========== 浮动爱心 ========== */
.floating-hearts {
  position: fixed; inset: 0;
  pointer-events: none;
  z-index: 0;
}
.fh {
  position: absolute;
  animation: softFloat ease-in-out infinite;
  opacity: 0.35;
}
@keyframes softFloat {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-18px) scale(1.15); }
}

/* ========== 返回按钮 ========== */
.back-fab {
  position: fixed; top: 20px; left: 20px; z-index: 10;
  width: 44px; height: 44px;
  border-radius: 50%; border: none;
  background: rgba(255,255,255,0.75);
  backdrop-filter: blur(10px);
  color: #cc8888;
  cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 2px 12px rgba(180,120,120,0.15);
  transition: all 0.3s ease;
}
.back-fab:hover {
  background: rgba(255,255,255,0.9);
  transform: scale(1.08);
  box-shadow: 0 4px 18px rgba(180,120,120,0.25);
}

/* ========== 内容区 ========== */
.about-content {
  position: relative; z-index: 2;
  max-width: 680px;
  margin: 0 auto;
  padding: 80px 20px 40px;
  display: flex; flex-direction: column; gap: 32px;
}

/* 卡片 */
.about-card {
  background: rgba(255,255,255,0.7);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-radius: 22px;
  padding: 36px 32px;
  box-shadow: 0 8px 32px rgba(200,150,150,0.12);
  border: 1px solid rgba(255,200,200,0.3);
  animation: cardIn 0.6s cubic-bezier(0.34, 1.56, 0.64, 1) both;
}
.card-1 { animation-delay: 0.05s; }
.card-2 { animation-delay: 0.2s; }
.card-3 { animation-delay: 0.35s; }
@keyframes cardIn {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

.card-icon-circle {
  width: 64px; height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FFB8B8, #FF9999);
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 16px;
  box-shadow: 0 4px 16px rgba(255,153,153,0.3);
}

.about-card h2 {
  font-size: 22px;
  color: #cc7777;
  margin: 0 0 8px;
  font-weight: 700;
}

.divider {
  width: 50px; height: 3px;
  background: linear-gradient(90deg, #FFC0CB, transparent);
  border-radius: 2px;
  margin-bottom: 20px;
}

.about-text {
  font-size: 15px;
  color: #8a6a6a;
  line-height: 1.9;
  margin: 0 0 14px;
}
.about-text b { color: #cc7777; }
.about-text.accent {
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  color: #cc7777;
  margin-top: 8px;
  padding-top: 14px;
  border-top: 1px dashed rgba(255,180,180,0.3);
}

/* 作者标签 */
.author-tags {
  display: flex; flex-wrap: wrap; gap: 10px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px dashed rgba(255,180,180,0.3);
}
.tag {
  padding: 6px 16px;
  background: linear-gradient(135deg, #FFF0F3, #FFE8EC);
  color: #cc8888;
  border-radius: 18px;
  font-size: 13px;
  font-weight: 500;
}

/* 祝福卡片 */
.blessing-card {
  background: rgba(255,255,255,0.8);
  border-color: rgba(255,180,180,0.45);
}
.blessing-text p {
  font-size: 15px;
  color: #8a6a6a;
  line-height: 1.95;
  margin: 0 0 12px;
}
.blessing-text b { color: #cc7777; }
.blessing-highlight {
  text-align: center;
  font-size: 17px !important;
  font-weight: 700;
  color: #cc7777 !important;
  padding: 18px 0 6px;
  margin-top: 10px;
  border-top: 1px dashed rgba(255,180,180,0.3);
}
.blessing-signature {
  text-align: right;
  font-size: 13px !important;
  color: #d4a8a8 !important;
  font-style: italic;
}

/* 底部 */
.about-footer {
  position: relative; z-index: 2;
  text-align: center;
  padding: 24px;
  color: #d4a8a8;
  font-size: 13px;
}
.footer-sep { margin: 0 10px; }

@media (max-width: 768px) {
  .about-content {
    padding: 70px 14px 30px;
  }
  .about-card {
    padding: 24px 18px;
  }
  .about-card h2 { font-size: 19px; }
  .about-text, .blessing-text p { font-size: 14px; }
}
</style>
