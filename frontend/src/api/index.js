export * from './modules/user'
export * from './modules/album'
export * from './modules/diary'
export * from './modules/anniversary'
export * from './modules/statistics'
export * from './modules/checkin'
export * from './modules/whisper'

export * from './community'

import { userApi } from './modules/user'
import { albumApi } from './modules/album'
import { diaryApi } from './modules/diary'
import { anniversaryApi, coupleApi as legacyCoupleApi } from './modules/anniversary'
import { statisticsApi } from './modules/statistics'
import { checkinApi, weekCheckinApi, wishlistApi, weatherApi, coupleApi, statsApi } from './modules/checkin'
import { whisperApi } from './modules/whisper'

export const { login, register, getInfo, getUserInfo = getInfo, updateNickname, updatePassword, uploadAvatar, sendLoginCode, loginByCode, sendBindCode, bindEmail } = userApi
export const { getAlbumList, addAlbum, updateAlbum, deleteAlbum } = albumApi
export const { getDiaryList, addDiary, updateDiary, deleteDiary } = diaryApi
export const { getAnniversaryList, getNextAnniversary, getTypes, addAnniversary, updateAnniversary, deleteAnniversary } = anniversaryApi
export const { getCouple, saveCouple } = legacyCoupleApi
export const { getStatistics } = statisticsApi

// 打卡系统新 API
export const { getList: getCheckinList, create: createCheckin, like: likeCheckin } = checkinApi
export const { getWeek: getWeekCheckin, updateDay: updateWeekDay } = weekCheckinApi
export const { getInfo: getWishlistInfo, update: updateWishlist } = wishlistApi
export const { getToday: getWeatherToday } = weatherApi
export const { getInfo: getCoupleInfo, update: updateCouple, search: searchCoupleUser, sendRequest: sendCoupleRequest, getPendingRequests: getCouplePendingRequests, acceptRequest: acceptCoupleRequest, rejectRequest: rejectCoupleRequest, getNotifications: getCoupleNotifications, getUnreadCount: getCoupleUnreadCount, readAllNotifications: readAllCoupleNotifications, unbind: unbindCouple } = coupleApi
export const { getOverview: getStatsOverview, getChart: getStatsChart } = statsApi

// 悄悄话 API
export const { getContacts: getWhisperContacts, getHistory: getWhisperHistory } = whisperApi