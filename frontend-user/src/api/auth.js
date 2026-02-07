import request from './request'

export function loginApi(data) {
  return request.post('/auth/login', {
    ...data,
    password: window.btoa(data.password)
  })
}

export function registerApi(data) {
  return request.post('/auth/register', {
    ...data,
    password: window.btoa(data.password)
  })
}

export function getInfoApi() {
  return request.get('/auth/info')
}
