import request from '@/utils/request'
import type { MarkingRecord, PageResult } from '@/types'

export function getPendingMarkingApi(params: { pageNum?: number; pageSize?: number; examId?: number }) {
  return request.get<PageResult<MarkingRecord>, { data: PageResult<MarkingRecord> }>('/marking/pending', { params })
}

export function getMarkingDetailApi(examId: number, studentId: number) {
  return request.get<MarkingRecord, { data: MarkingRecord }>(`/marking/exams/${examId}/students/${studentId}`)
}

export function markQuestionApi(answerId: number, data: { actualScore: number; teacherComment?: string }) {
  return request.put(`/marking/answers/${answerId}`, data)
}

export function finishMarkingApi(examId: number, studentId: number) {
  return request.post(`/marking/exams/${examId}/students/${studentId}/finish`)
}
