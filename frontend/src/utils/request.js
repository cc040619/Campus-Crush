import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getCurrentUser } from '../composables/useCommon'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true
})

request.interceptors.request.use(
  config => {
    const user = getCurrentUser()
    if (user.id) {
      config.headers['X-User-Id'] = user.id
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

const clearAuthData = () => {
  localStorage.removeItem('user')
}

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      if (res.msg?.includes('登录信息已过期') || res.msg?.includes('Token无效') || res.msg?.includes('Token解析失败')) {
        clearAuthData()
        window.location.href = '/login'
        ElMessage.error({
          message: '登录信息已过期，请重新登录',
          duration: 3000,
          showClose: true
        })
      } else if (!res.msg?.includes('请先在设置中绑定情侣关系')) {
        ElMessage.error({
          message: res.msg || '请求失败',
          duration: 3000,
          showClose: true
        })
      }
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        clearAuthData()
        window.location.href = '/login'
        ElMessage.error({
          message: '登录信息已过期，请重新登录',
          duration: 3000,
          showClose: true
        })
      } else {
        ElMessage.error({
          message: error.response.data?.msg || `请求失败(${status})`,
          duration: 3000,
          showClose: true
        })
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error({
        message: '请求超时，请稍后重试',
        duration: 3000,
        showClose: true
      })
    } else {
      ElMessage.error({
        message: error.message || '网络错误',
        duration: 3000,
        showClose: true
      })
    }
    return Promise.reject(error)
  }
)

export { request as default }