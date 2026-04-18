import request from '@/utils/request'
import type { RoleItem } from '@/types'

export function getRoleListApi() {
  return request.get<RoleItem[], { data: RoleItem[] }>('/roles')
}

export function getRoleDetailApi(id: number) {
  return request.get<RoleItem, { data: RoleItem }>(`/roles/${id}`)
}

export function saveRoleApi(payload: Partial<RoleItem>) {
  return request.post('/roles', payload)
}

export function deleteRoleApi(id: number) {
  return request.delete(`/roles/${id}`)
}
