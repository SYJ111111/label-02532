import request from './request'

export function getStats() {
  return request.get('/admin/article/stats')
}

export function getArticleList(params) {
  return request.get('/admin/article/list', { params })
}

export function getArticleDetail(id) {
  return request.get(`/admin/article/${id}`)
}

export function approveArticle(id) {
  return request.put(`/admin/article/${id}/approve`)
}

export function rejectArticle(id) {
  return request.put(`/admin/article/${id}/reject`)
}

export function offlineArticle(id) {
  return request.put(`/admin/article/${id}/offline`)
}

export function deleteArticle(id) {
  return request.delete(`/admin/article/${id}`)
}
