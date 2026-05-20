import { request } from './api'

export interface CreateLeadRequest {
  title: string
  description?: string
  source?: string
  tags?: string[]
}

export interface UpdateLeadRequest {
  title?: string
  description?: string
  source?: string
  tags?: string[]
  status?: string
}

export interface LeadItem {
  id: string
  title: string
  description: string
  source: string
  tags: string[]
  status: string
  createdAt: string
  updatedAt: string
}

export interface LeadListResponse {
  list: LeadItem[]
  total: number
  page: number
  pageSize: number
}

export const leadApi = {
  getList(status?: string, page = 1, pageSize = 20) {
    return request<LeadListResponse>({
      url: '/api/v1/leads',
      method: 'GET',
      data: { status, page, pageSize }
    })
  },

  getById(id: string) {
    return request<LeadItem>({
      url: `/api/v1/leads/${id}`,
      method: 'GET'
    })
  },

  create(data: CreateLeadRequest) {
    return request<LeadItem>({
      url: '/api/v1/leads',
      method: 'POST',
      data
    })
  },

  update(id: string, data: UpdateLeadRequest) {
    return request<LeadItem>({
      url: `/api/v1/leads/${id}`,
      method: 'PUT',
      data
    })
  },

  updateStatus(id: string, status: string) {
    return request({
      url: `/api/v1/leads/${id}/status`,
      method: 'PUT',
      data: { status }
    })
  },

  delete(id: string) {
    return request({
      url: `/api/v1/leads/${id}`,
      method: 'DELETE'
    })
  }
}