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
  source?: string
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
    return request<LawSearchResponse>({
      url: '/api/v1/legal/laws/search',
      method: 'GET',
      data: params
    })
  },

  getDetail(id: string) {
    return request<LawDetailResponse>({
      url: `/api/v1/laws/${id}`,
      method: 'GET'
    })
  }
}