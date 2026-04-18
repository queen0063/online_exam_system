import request from '@/utils/request'
import type { PageResult, PaperRecord, RandomRuleRecord } from '@/types'

export interface PaperQuery {
  pageNum?: number
  pageSize?: number
  paperName?: string
  subjectId?: number | ''
}

export function getPaperPageApi(params: PaperQuery) {
  return request.get<PageResult<PaperRecord>, { data: PageResult<PaperRecord> }>('/papers', { params })
}

export function getPaperDetailApi(id: number) {
  return request.get<PaperRecord, { data: PaperRecord }>(`/papers/${id}`)
}

export function previewPaperApi(id: number) {
  return request.get<PaperRecord, { data: PaperRecord }>(`/papers/${id}/preview`)
}

export interface PaperSavePayload extends PaperRecord {
  randomRules?: RandomRuleRecord[]
}

export function savePaperApi(data: PaperSavePayload) {
  return request.post('/papers', data)
}

export function deletePaperApi(id: number) {
  return request.delete(`/papers/${id}`)
}
