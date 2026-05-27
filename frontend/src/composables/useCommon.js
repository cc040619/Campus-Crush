import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

export function useGoHome() {
  const router = useRouter()
  return () => router.push('/')
}

export function useGoTo(path) {
  const router = useRouter()
  return () => router.push(path)
}

export function useFormatTime() {
  return (timeString) => {
    if (!timeString) return ''
    
    const date = new Date(timeString)
    const now = new Date()
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
    const yesterday = new Date(today)
    yesterday.setDate(yesterday.getDate() - 1)
    const messageDate = new Date(date.getFullYear(), date.getMonth(), date.getDate())
    
    const hours = date.getHours().toString().padStart(2, '0')
    const minutes = date.getMinutes().toString().padStart(2, '0')
    const month = (date.getMonth() + 1).toString().padStart(2, '0')
    const day = date.getDate().toString().padStart(2, '0')
    const year = date.getFullYear()
    
    if (messageDate.getTime() === today.getTime()) {
      return `${hours}:${minutes}`
    } else if (messageDate.getTime() === yesterday.getTime()) {
      return `昨天 ${hours}:${minutes}`
    } else if (date.getFullYear() === now.getFullYear()) {
      return `${month}-${day}`
    } else {
      return `${year}-${month}-${day}`
    }
  }
}

export function useConfirmDelete() {
  return async (deleteFn, message = '确定要删除吗？') => {
    try {
      await ElMessageBox.confirm(message, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await deleteFn()
      ElMessage.success('删除成功')
      return true
    } catch (error) {
      if (error !== 'cancel') {
        ElMessage.error('删除失败')
      }
      return false
    }
  }
}

/**
 * 安全写入用户信息到 localStorage，自动移除 password 字段
 */
export function saveUser(user) {
  const safeUser = { ...user }
  delete safeUser.password
  localStorage.setItem('user', JSON.stringify(safeUser))
}

/**
 * 安全读取用户信息，自动移除 password 字段（纵深防御）
 */
export function getCurrentUser() {
  const userStr = localStorage.getItem('user')
  if (!userStr) return {}
  const user = JSON.parse(userStr)
  if (user.password) {
    delete user.password
    localStorage.setItem('user', JSON.stringify(user))
  }
  return user
}

/**
 * 清除 localStorage 中已存在的 password 字段（存量数据修复）
 */
export function cleanupPasswordFromStorage() {
  try {
    const userStr = localStorage.getItem('user')
    if (userStr) {
      const user = JSON.parse(userStr)
      if (user.password) {
        delete user.password
        localStorage.setItem('user', JSON.stringify(user))
        return true
      }
    }
  } catch (e) {
    // ignore parse errors
  }
  return false
}

export function useLogout() {
  const router = useRouter()
  return async () => {
    try {
      // 调用后端登出接口清除 HttpOnly Cookie
      await fetch('/api/user/logout', { method: 'POST', credentials: 'include' })
    } catch (e) {
      // 即使后端调用失败也要清除本地状态
    }
    localStorage.removeItem('user')
    router.push('/login')
  }
}

export function useGetCurrentUser() {
  return () => getCurrentUser()
}