import request from '../../utils/request'

export const diaryApi = {
  getDiaryList: () => request.get('/diary'),
  
  addDiary: (data) => request.post('/diary', data),
  
  updateDiary: (id, data) => request.put(`/diary/${id}`, data),
  
  deleteDiary: (id) => request.delete(`/diary/${id}`)
}