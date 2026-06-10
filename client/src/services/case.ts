import { request } from './api'

export interface CaseSearchRequest {
  keyword?: string
  caseType?: string
  court?: string
  date?: string
  page?: number
  pageSize?: number
}

export interface CaseItem {
  id: string
  title: string
  caseNumber: string
  caseType: string
  court: string
  date: string
  parties: string
  status: string
  source?: string
}

export interface CaseSearchResponse {
  list: CaseItem[]
  total: number
  page: number
  pageSize: number
}

export interface CaseDetailResponse {
  id: string
  title: string
  caseNumber: string
  caseType: string
  court: string
  date: string
  parties: string
  status: string
  content: string
  judgment: string
}

export const caseApi = {
  search(params: CaseSearchRequest) {
    return request<CaseSearchResponse>({
      url: '/api/v1/legal/cases/search',
      method: 'GET',
      data: params
    })
  },

  getDetail(id: string) {
    return request<CaseDetailResponse>({
      url: `/api/v1/cases/${id}`,
      method: 'GET'
    })
  },

  getBookmarks() {
    return request<CaseItem[]>({
      url: '/api/v1/cases/bookmarks',
      method: 'GET'
    })
  },

  addBookmark(id: string) {
    return request({
      url: '/api/v1/cases/bookmarks',
      method: 'POST',
      data: { caseId: id }
    })
  },

  removeBookmark(id: string) {
    return request({
      url: `/api/v1/cases/bookmarks/${id}`,
      method: 'DELETE'
    })
  }
}