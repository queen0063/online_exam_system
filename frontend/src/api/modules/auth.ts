import request from '@/utils/request'
import type { ClassInfoRecord, LoginPayload, LoginResult, RegisterPayload, UserInfo } from '@/types'

export function loginApi(data: LoginPayload) {
  return request.post<LoginResult, { data: LoginResult }>('/auth/login', data)
}

export function registerApi(data: RegisterPayload) {
  return request.post('/auth/register', data)
}

export function getRegisterClassListApi() {
  return request.get<ClassInfoRecord[], { data: ClassInfoRecord[] }>('/auth/register/classes')
}

export function logoutApi() {
  return request.post('/auth/logout')
}

export function getCurrentUserApi() {
  return request.get<UserInfo, { data: UserInfo }>('/auth/me')
}
