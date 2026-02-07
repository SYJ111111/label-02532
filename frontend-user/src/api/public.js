import request from './request'

export function getArticles(params) {
  return request.get('/public/articles', { params })
}

export function getArticleDetail(id) {
  return request.get(`/public/articles/${id}`)
}

export function getCategories() {
  return request.get('/public/categories')
}
