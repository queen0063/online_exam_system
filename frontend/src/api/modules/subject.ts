import request from '@/utils/request'
import type { SubjectRecord } from '@/types'

export function getSubjectListApi() {
  return request.get<SubjectRecord[], { data: SubjectRecord[] }>('/subjects')
}

export function saveSubjectApi(data: SubjectRecord) {
  return request.post('/subjects', data)
}

export function deleteSubjectApi(id: number) {
  return request.delete(`/subjects/${id}`)
}
