import request from '../../utils/request'

export const statisticsApi = {
  getStatistics: () => request.get('/statistics'),
  getBasicStats: () => request.get('/statistics/basic'),
  getSocialStats: () => request.get('/statistics/social'),
  getProductStats: () => request.get('/statistics/product'),
  getWeeklyActivity: () => request.get('/statistics/chart/weeklyActivity'),
  getMonthlyPost: () => request.get('/statistics/chart/monthlyPost'),
  getCategoryDistribution: () => request.get('/statistics/chart/categoryDistribution'),
  getDailyBrowseStats: (days = 7) => request.get(`/statistics/daily-browse?days=${days}`)
}