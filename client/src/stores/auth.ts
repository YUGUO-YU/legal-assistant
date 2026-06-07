import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService } from '@/services/auth'

interface UserInfo {
  id: string
  phone: string
  email: string
  nickname: string
  avatar: string
  role: string
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>('')
  const refreshToken = ref<string>('')
  const userInfo = ref<UserInfo | null>(null)

  const isLoggedIn = computed(() => !!token.value)

  function setToken(newToken: string) {
    token.value = newToken
    uni.setStorageSync('token', newToken)
  }

  function setRefreshToken(newRefreshToken: string) {
    refreshToken.value = newRefreshToken
    uni.setStorageSync('refreshToken', newRefreshToken)
  }

  function setUserInfo(info: UserInfo) {
    userInfo.value = info
    uni.setStorageSync('userInfo', info)
  }

  async function loginByPhone(phone: string, code: string) {
    const res = await authService.phoneLogin({ phone, code })
    setToken(res.data.token)
    setRefreshToken(res.data.refreshToken)
    setUserInfo(res.data.user)
    return res.data
  }

  async function loginByEmail(email: string, password: string) {
    const res = await authService.emailLogin({ email, password })
    setToken(res.data.token)
    setRefreshToken(res.data.refreshToken)
    setUserInfo(res.data.user)
    return res.data
  }

  async function wechatLogin(params: { code: string; encryptedData?: string; iv?: string }) {
    const res = await authService.wechatLogin(params)
    setToken(res.data.token)
    setRefreshToken(res.data.refreshToken)
    setUserInfo(res.data.user)
    return res.data
  }

  async function sendSms(phone: string, type: string = 'login') {
    return authService.sendSms({ phone, type })
  }

  async function register(params: {
    phone: string
    email: string
    password: string
    code: string
    nickname?: string
  }) {
    const res = await authService.register(params)
    setToken(res.data.token)
    setRefreshToken(res.data.refreshToken)
    setUserInfo(res.data.user)
    return res.data
  }

  async function sendSms(phone: string, type: string = 'login') {
    return authService.sendSms({ phone, type })
  }

  function logout() {
    token.value = ''
    refreshToken.value = ''
    userInfo.value = null
    uni.removeStorageSync('token')
    uni.removeStorageSync('refreshToken')
    uni.removeStorageSync('userInfo')
  }

  function initFromStorage() {
    const storedToken = uni.getStorageSync('token')
    const storedRefreshToken = uni.getStorageSync('refreshToken')
    const storedUserInfo = uni.getStorageSync('userInfo')

    if (storedToken) {
      token.value = storedToken
    }
    if (storedRefreshToken) {
      refreshToken.value = storedRefreshToken
    }
    if (storedUserInfo) {
      userInfo.value = storedUserInfo
    }
  }

  return {
    token,
    refreshToken,
    userInfo,
    isLoggedIn,
    loginByPhone,
    loginByEmail,
    wechatLogin,
    register,
    sendSms,
    logout,
    initFromStorage
  }
})