import { request } from './api'

export interface DocumentItem {
  id: string
  title: string
  content: string
  docType: string
  tags: string[]
  status: string
  wordCount: number
  createdAt: string
  updatedAt: string
}

export interface DocumentListResponse {
  list: DocumentItem[]
  total: number
  page: number
  pageSize: number
}

export interface CreateDocumentRequest {
  title: string
  content?: string
  docType?: string
  tags?: string[]
}

export interface UpdateDocumentRequest {
  title?: string
  content?: string
  docType?: string
  tags?: string[]
  status?: string
}

export const documentApi = {
  getList(category?: string, page = 1, pageSize = 20) {
    return request<DocumentListResponse>({
      url: '/api/v1/documents',
      method: 'GET',
      data: { category, page, pageSize }
    })
  },

  getById(id: string) {
    return request<DocumentItem>({
      url: `/api/v1/documents/${id}`,
      method: 'GET'
    })
  },

  create(data: CreateDocumentRequest) {
    return request<DocumentItem>({
      url: '/api/v1/documents',
      method: 'POST',
      data
    })
  },

  update(id: string, data: UpdateDocumentRequest) {
    return request<DocumentItem>({
      url: `/api/v1/documents/${id}`,
      method: 'PUT',
      data
    })
  },

  delete(id: string) {
    return request({
      url: `/api/v1/documents/${id}`,
      method: 'DELETE'
    })
  },

  getVersions(id: string) {
    return request<DocumentItem[]>({
      url: `/api/v1/documents/${id}/versions`,
      method: 'GET'
    })
  },

  restoreVersion(id: string, versionId: string) {
    return request({
      url: `/api/v1/documents/${id}/versions/${versionId}/restore`,
      method: 'POST'
    })
  }
}