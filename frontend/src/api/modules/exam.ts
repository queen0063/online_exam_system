import request from '@/utils/request'
import type { ExamMonitorRecord, ExamRecord, PageResult } from '@/types'

export interface ExamQuery {
  pageNum?: number
  pageSize?: number
  examName?: string
  status?: string
}

export function getExamPageApi(params: ExamQuery) {
  return request.get<PageResult<ExamRecord>, { data: PageResult<ExamRecord> }>('/exams', { params })
}

export function getMyExamPageApi(params: ExamQuery) {
  return request.get<PageResult<ExamRecord>, { data: PageResult<ExamRecord> }>('/exams/my', { params })
}

export function getExamDetailApi(id: number) {
  return request.get<ExamRecord, { data: ExamRecord }>(`/exams/${id}`)
}

export function getExamMonitoringApi(id: number) {
  return request.get<ExamMonitorRecord[], { data: ExamMonitorRecord[] }>(`/exams/${id}/monitoring`)
}

export interface ExamSavePayload {
  id?: number
  examName: string
  paperId: number
  subjectId: number
  startTime: string
  endTime: string
  durationMinutes: number
  passScore: number
  maxSwitchCount?: number
  status?: string
  studentIds: number[]
}

export function saveExamApi(data: ExamSavePayload) {
  return request.post('/exams', data)
}

export function publishExamApi(id: number) {
  return request.post(`/exams/${id}/publish`)
}

export function publishExamScoreApi(id: number) {
  return request.post(`/exams/${id}/publish-score`)
}

export function deleteExamApi(id: number) {
  return request.delete(`/exams/${id}`)
}
