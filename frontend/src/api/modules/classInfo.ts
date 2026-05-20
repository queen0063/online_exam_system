import request from '@/utils/request'
import type { ClassInfoRecord } from '@/types'

export function getClassListApi() {
  return request.get<ClassInfoRecord[], { data: ClassInfoRecord[] }>('/classes')
}
