import { request } from './api'

export interface LawSearchRequest {
  keyword?: string
  lawType?: string
  page?: number
  pageSize?: number
}

export interface LawItem {
  id: string
  title: string
  lawNumber: string
  lawType: string
  effectiveDate: string
  status: string
}

export interface LawSearchResponse {
  list: LawItem[]
  total: number
  page: number
  pageSize: number
}

export interface LawDetailResponse {
  id: string
  title: string
  lawNumber: string
  lawType: string
  effectiveDate: string
  status: string
  content: string
  chapters: { title: string; articles: string[] }[]
}

export const lawApi = {
  search(params: LawSearchRequest) {
    return request<LawSearchResponse>('/api/v1/laws/search', {
      method: 'GET',
      params
    })
  },

  getDetail(id: string) {
    return request<LawDetailResponse>(`/api/v1/laws/${id}`, {
      method: 'GET'
    })
  }
}