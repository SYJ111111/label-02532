import request from './request'

export function getCategoryList() {
  return request.get('/admin/category/list')
}

export function saveCategory(data) {
  return request.post('/admin/category/save', data)
}

export function deleteCategory(id) {
  return request.delete(`/admin/category/${id}`)
}
