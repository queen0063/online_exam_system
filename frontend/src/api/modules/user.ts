import request from '@/utils/request'
import type { PageResult, UserRecord } from '@/types'

export interface UserQuery {
  pageNum?: number
  pageSize?: number
  keyword?: string
  roleId?: number
  status?: number | ''
}

export interface UserSavePayload {
  id?: number
  username: string
  password?: string
  realName: string
  phone?: string
  email?: string
  classId?: number
  status: number
  roleIds: number[]
}

export function getUserPageApi(params: UserQuery) {
  return request.get<PageResult<UserRecord>, { data: PageResult<UserRecord> }>('/users', { params })
}

export function getUserDetailApi(id: number) {
  return request.get<UserRecord, { data: UserRecord }>(`/users/${id}`)
}

export function saveUserApi(data: UserSavePayload) {
  return request.post('/users', data)
}

export function deleteUserApi(id: number) {
  return request.delete(`/users/${id}`)
}

export function resetPasswordApi(id: number, data: { newPassword: string }) {
  return request.put(`/users/${id}/reset-password`, data)
}

export function changePasswordApi(data: { oldPassword: string; newPassword: string }) {
  return request.put('/users/change-password', data)
}
