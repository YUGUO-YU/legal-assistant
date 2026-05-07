import api from './api'

interface LoginResponse {
  token: string
  refreshToken: string
  expiresIn: number
  user: {
    id: string
    phone: string
    email: string
    nickname: string
    avatar: string
    role: string
  }
}

interface PhoneLoginParams {
  phone: string
  code: string
}

interface EmailLoginParams {
  email: string
  password: string
}

interface RegisterParams {
  phone: string
  email: string
  password: string
  code: string
  nickname?: string
}

interface SendSmsParams {
  phone: string
  type?: string
}

export const authService = {
  phoneLogin(data: PhoneLoginParams) {
    return api.post<LoginResponse>('/api/v1/auth/phone/login', data)
  },

  emailLogin(data: EmailLoginParams) {
    return api.post<LoginResponse>('/api/v1/auth/email/login', data)
  },

  register(data: RegisterParams) {
    return api.post<LoginResponse>('/api/v1/auth/register', data)
  },

  sendSms(data: SendSmsParams) {
    return api.post('/api/v1/auth/sms/send', data)
  },

  refreshToken(refreshToken: string) {
    return api.post<LoginResponse>('/api/v1/auth/refresh', { refreshToken })
  },

  logout() {
    return api.post('/api/v1/auth/logout')
  }
}