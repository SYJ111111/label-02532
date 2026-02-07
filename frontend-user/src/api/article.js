import request from './request'

export function getMyArticles(params) {
  return request.get('/user/article/my', { params })
}

export function getArticleDetail(id) {
  return request.get(`/user/article/${id}`)
}

export function saveArticle(data) {
  return request.post('/user/article/save', data)
}

export function submitArticle(id) {
  return request.post(`/user/article/${id}/submit`)
}

export function deleteArticle(id) {
  return request.delete(`/user/article/${id}`)
}
