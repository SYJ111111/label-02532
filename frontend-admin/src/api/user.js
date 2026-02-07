import request from './request'

export function getUserList(params) {
  return request.get('/admin/user/list', { params })
}

export function updateUserStatus(id, status) {
  return request.put(`/admin/user/${id}/status`, null, { params: { status } })
}

export function getProfile() {
  return request.get('/admin/user/profile')
}

export function updateProfile(data) {
  return request.put('/admin/user/profile', data)
}
