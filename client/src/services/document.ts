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
    return request<DocumentListResponse>('/api/v1/documents', {
      method: 'GET',
      params: { category, page, pageSize }
    })
  },

  getById(id: string) {
    return request<DocumentItem>('/api/v1/documents/${id}'.replace('${id}', id), {
      method: 'GET'
    })
  },

  create(data: CreateDocumentRequest) {
    return request<DocumentItem>('/api/v1/documents', {
      method: 'POST',
      data
    })
  },

  update(id: string, data: UpdateDocumentRequest) {
    return request<DocumentItem>('/api/v1/documents/${id}'.replace('${id}', id), {
      method: 'PUT',
      data
    })
  },

  delete(id: string) {
    return request('/api/v1/documents/${id}'.replace('${id}', id), {
      method: 'DELETE'
    })
  },

  getVersions(id: string) {
    return request<any>('/api/v1/documents/${id}/versions'.replace('${id}', id), {
      method: 'GET'
    })
  },

  restoreVersion(id: string, versionId: string) {
    return request('/api/v1/documents/${id}/versions/${versionId}/restore'
      .replace('${id}', id)
      .replace('${versionId}', versionId), {
      method: 'POST'
    })
  }
}