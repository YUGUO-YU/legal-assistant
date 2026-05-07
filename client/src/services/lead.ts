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
    return request<LeadListResponse>('/api/v1/leads', {
      method: 'GET',
      params: { status, page, pageSize }
    })
  },

  getById(id: string) {
    return request<LeadItem>('/api/v1/leads/${id}'.replace('${id}', id), {
      method: 'GET'
    })
  },

  create(data: CreateLeadRequest) {
    return request<LeadItem>('/api/v1/leads', {
      method: 'POST',
      data
    })
  },

  update(id: string, data: UpdateLeadRequest) {
    return request<LeadItem>('/api/v1/leads/${id}'.replace('${id}', id), {
      method: 'PUT',
      data
    })
  },

  updateStatus(id: string, status: string) {
    return request('/api/v1/leads/${id}/status'.replace('${id}', id), {
      method: 'PUT',
      data: { status }
    })
  },

  delete(id: string) {
    return request('/api/v1/leads/${id}'.replace('${id}', id), {
      method: 'DELETE'
    })
  }
}