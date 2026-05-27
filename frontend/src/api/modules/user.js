import request from '../../utils/request'

export const userApi = {
  login: (data) => request.post('/user/login', data),

  register: (data) => request.post('/user/register', data),

  /** 发送登录验证码到QQ邮箱 */
  sendLoginCode: (email) => request.post('/user/send-login-code', { email }),

  /** QQ邮箱验证码登录 */
  loginByCode: (email, code) => request.post('/user/login-by-code', { email, code }),

  getInfo: () => request.get('/user/info'),

  updateNickname: (nickname) => request.put('/user/nickname', { nickname }),

  updatePassword: (oldPassword, newPassword) =>
    request.put('/user/password', { oldPassword, newPassword }),

  uploadAvatar: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/user/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /** 发送绑定邮箱验证码 */
  sendBindCode: (email) => request.post('/user/send-bind-code', { email }),

  /** 绑定/修改邮箱 */
  bindEmail: (email, code) => request.post('/user/bind-email', { email, code })
}