import { request } from './api'

export interface UserProfile {
  id: string
  username: string
  email: string
  phone: string
  avatar: string
  role: string
  firmName: string
  practiceAreas: string[]
  bio: string
  createdAt: string
}

export interface UpdateProfileRequest {
  email?: string
  phone?: string
  avatar?: string
  firmName?: string
  practiceAreas?: string[]
  bio?: string
}

export const userApi = {
  getProfile() {
    return request<UserProfile>({
      url: '/api/v1/users/profile',
      method: 'GET'
    })
  },

  updateProfile(data: UpdateProfileRequest) {
    return request<UserProfile>({
      url: '/api/v1/users/profile',
      method: 'PUT',
      data
    })
  },

  updateAvatar(avatar: string) {
    return request({
      url: '/api/v1/users/profile/avatar',
      method: 'PUT',
      data: { avatar }
    })
  }
}