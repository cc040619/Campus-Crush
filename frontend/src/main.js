import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import lazyLoad from './directives/lazyLoad'
import { cleanupPasswordFromStorage } from './composables/useCommon'

// 存量数据修复：清除可能已泄露到 localStorage 的密码哈希
cleanupPasswordFromStorage()

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.directive('lazy', lazyLoad)
app.use(ElementPlus)
app.use(router)
app.mount('#app')
