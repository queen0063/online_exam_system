import request from '@/utils/request'
import type { ClassInfoRecord } from '@/types'

export function getClassListApi() {
  return request.get<ClassInfoRecord[], { data: ClassInfoRecord[] }>('/classes')
}

export function getClassManageListApi() {
  return request.get<ClassInfoRecord[], { data: ClassInfoRecord[] }>('/classes/manage')
}

export function saveClassApi(data: ClassInfoRecord) {
  return request.post('/classes', data)
}

export function deleteClassApi(id: number) {
  return request.delete(`/classes/${id}`)
}
