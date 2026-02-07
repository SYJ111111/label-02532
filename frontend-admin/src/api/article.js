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

export function saveArticle(data) {
  return request.post('/admin/article/save', data)
}

export function deleteArticle(id) {
  return request.delete(`/admin/article/${id}`)
}

export function updateArticleStatus(id, status) {
  return request.put(`/admin/article/${id}/status`, null, { params: { status } })
}
