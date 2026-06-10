const BASE_URL = process.env.NODE_ENV === 'development' 
  ? 'http://localhost:8080' 
  : 'http://localhost:8080'

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: Record<string, string>
}

interface ResponseData<T = any> {
  code: number
  message: string
  data: T
}

class Request {
  private token: string = ''

  constructor() {
    const savedBaseUrl = uni.getStorageSync('baseUrl')
    if (savedBaseUrl) {
      (this as any).baseUrl = savedBaseUrl
    }
  }

  get baseUrl(): string {
    return (this as any).baseUrl || BASE_URL
  }

  set baseUrl(url: string) {
    (this as any).baseUrl = url
    uni.setStorageSync('baseUrl', url)
  }

  setToken(token: string) {
    this.token = token
  }

  getToken(): string {
    return this.token
  }

  clearToken() {
    this.token = ''
  }

  async request<T = any>(options: RequestOptions): Promise<ResponseData<T>> {
    const { url, method = 'GET', data, header = {} } = options

    if (this.token) {
      header['Authorization'] = `Bearer ${this.token}`
    }

    header['Content-Type'] = 'application/json'

    try {
      const response = await uni.request({
        url: this.baseUrl + url,
        method,
        data,
        header
      })

      const result = response.data as ResponseData<T>

      if (result.code !== 0) {
        if (result.code === 2001 || result.code === 2002) {
          this.clearToken()
          uni.navigateTo({ url: '/pages/auth/login' })
        }
        throw new Error(result.message)
      }

      return result
    } catch (error: any) {
      uni.showToast({
        title: error.message || '请求失败',
        icon: 'none'
      })
      throw error
    }
  }

  get<T = any>(url: string, data?: any) {
    return this.request<T>({ url, method: 'GET', data })
  }

  post<T = any>(url: string, data?: any) {
    return this.request<T>({ url, method: 'POST', data })
  }

  put<T = any>(url: string, data?: any) {
    return this.request<T>({ url, method: 'PUT', data })
  }

  delete<T = any>(url: string, data?: any) {
    return this.request<T>({ url, method: 'DELETE', data })
  }
}

export const api = new Request()

export const request = api.request.bind(api)

export default api