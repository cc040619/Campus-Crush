import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  build: {
    chunkSizeWarningLimit: 1000,
    rollupOptions: {
      output: {
        manualChunks: {
          'element-plus': ['element-plus'],
          'gsap': ['gsap']
        }
      }
    }
  },
  server: {
    port: 3000,
    proxy: {
      // 已有 Spring Boot 后端 (:8081)
      '/api': { target: 'http://localhost:8081', changeOrigin: true },
      // WebSocket 代理
      '/ws': { target: 'ws://localhost:8081', ws: true, changeOrigin: true }
    }
  }
})
