import request from '../../utils/request'

export const anniversaryApi = {
  getAnniversaryList: () => request.get('/anniversary'),

  getNextAnniversary: () => request.get('/anniversary/next'),

  getTypes: () => request.get('/anniversary/types'),

  addAnniversary: (data) => request.post('/anniversary', data),

  updateAnniversary: (id, data) => request.put(`/anniversary/${id}`, data),

  deleteAnniversary: (id) => request.delete(`/anniversary/${id}`)
}

export const coupleApi = {
  getCouple: () => request.get('/couple'),

  saveCouple: (data) => request.post('/couple', data)
}