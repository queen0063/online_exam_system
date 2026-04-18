import request from '@/utils/request'
import type { LoginPayload, LoginResult, UserInfo } from '@/types'

export function loginApi(data: LoginPayload) {
  return request.post<LoginResult, { data: LoginResult }>('/auth/login', data)
}

export function logoutApi() {
  return request.post('/auth/logout')
}

export function getCurrentUserApi() {
  return request.get<UserInfo, { data: UserInfo }>('/auth/me')
}
