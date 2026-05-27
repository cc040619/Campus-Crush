import request from '../../utils/request'

export const whisperApi = {
  /** 获取悄悄话联系人（伴侣） */
  getContacts: () => request.get('/whisper/contacts'),

  /** 获取聊天历史 */
  getHistory: () => request.get('/whisper/history')
}
