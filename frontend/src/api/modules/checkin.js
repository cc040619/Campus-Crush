import request from '../../utils/request'

export const checkinApi = {
  /** 获取打卡记录列表 */
  getList: (params = {}) => request.get('/checkin/list', { params }),

  /** 新增打卡 */
  create: (data) => request.post('/checkin/create', data),

  /** 点赞/取消点赞 */
  like: (data) => request.post('/checkin/like', data)
}

export const weekCheckinApi = {
  /** 获取本周打卡数据 */
  getWeek: (params = {}) => request.get('/week/checkin', { params }),

  /** 更新单日打卡状态 */
  updateDay: (data) => request.post('/week/checkin/update', data)
}

export const wishlistApi = {
  /** 获取愿望清单 */
  getInfo: (params = {}) => request.get('/wishlist/info', { params }),

  /** 更新愿望清单（add/toggle/delete） */
  update: (data) => request.post('/wishlist/update', data)
}

export const weatherApi = {
  /** 获取今日天气 */
  getToday: () => request.get('/weather/today')
}

export const coupleApi = {
  /** 获取情侣信息 */
  getInfo: (params = {}) => request.get('/couple/info', { params }),

  /** 更新情侣信息（开始日期等） */
  update: (data) => request.post('/couple/update', data),

  /** 搜索用户（通过手机号/账号） */
  search: (keyword) => request.get('/couple/search', { params: { keyword } }),

  /** 发送情侣申请 */
  sendRequest: (data) => request.post('/couple/request', data),

  /** 获取待处理的情侣申请 */
  getPendingRequests: () => request.get('/couple/request/pending'),

  /** 同意情侣申请 */
  acceptRequest: (requestId) => request.post('/couple/request/accept', { requestId }),

  /** 拒绝情侣申请 */
  rejectRequest: (requestId) => request.post('/couple/request/reject', { requestId }),

  /** 获取通知列表 */
  getNotifications: () => request.get('/couple/notifications'),

  /** 获取未读通知数 */
  getUnreadCount: () => request.get('/couple/notifications/unread-count'),

  /** 标记全部已读 */
  readAllNotifications: () => request.post('/couple/notifications/read-all'),

  /** 解除情侣关系 */
  unbind: () => request.post('/couple/unbind')
}

export const statsApi = {
  /** 获取统计概览 */
  getOverview: (params = {}) => request.get('/stats/overview', { params }),

  /** 获取近7天打卡趋势 */
  getChart: (params = {}) => request.get('/stats/chart', { params })
}
