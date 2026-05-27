import request from '../../utils/request'

export const albumApi = {
  getAlbumList: () => request.get('/album'),
  
  addAlbum: (data, userId) => request.post('/album', data, {
    headers: {
      'Content-Type': 'multipart/form-data',
      'X-User-Id': userId
    }
  }),
  
  updateAlbum: (id, data, userId) => request.put(`/album/${id}`, data, {
    headers: {
      'Content-Type': 'multipart/form-data',
      'X-User-Id': userId
    }
  }),
  
  deleteAlbum: (id) => request.delete(`/album/${id}`)
}