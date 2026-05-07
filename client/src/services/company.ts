import { request } from './api'

export interface CompanySearchRequest {
  keyword?: string
  page?: number
  pageSize?: number
}

export interface CompanyItem {
  id: string
  name: string
  creditCode: string
  legalPerson: string
  capital: string
  establishDate: string
  status: string
}

export interface CompanySearchResponse {
  list: CompanyItem[]
  total: number
  page: number
  pageSize: number
}

export interface CompanyDetailResponse {
  id: string
  name: string
  creditCode: string
  legalPerson: string
  capital: string
  establishDate: string
  status: string
  businessScope: string
  shareholders: { name: string; sharePercent: number; capital: string }[]
  risks: { type: string; description: string; date: string }[]
  lawsuits: { caseNumber: string; title: string; date: string }[]
}

export const companyApi = {
  search(params: CompanySearchRequest) {
    return request<CompanySearchResponse>('/api/v1/companies/search', {
      method: 'GET',
      params
    })
  },

  getDetail(id: string) {
    return request<CompanyDetailResponse>(`/api/v1/companies/${id}`, {
      method: 'GET'
    })
  },

  getShareholders(id: string) {
    return request<CompanyDetailResponse>(`/api/v1/companies/${id}/shareholders`, {
      method: 'GET'
    })
  },

  getRiskInfo(id: string) {
    return request<CompanyDetailResponse>(`/api/v1/companies/${id}/risk`, {
      method: 'GET'
    })
  },

  getGraph(id: string) {
    return request<any>(`/api/v1/companies/${id}/graph`, {
      method: 'GET'
    })
  }
}