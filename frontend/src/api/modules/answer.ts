import request from '@/utils/request'
import type { AnswerRecord, ExamRecord, PageResult } from '@/types'

export function getAnswerExamPageApi(params: { pageNum?: number; pageSize?: number; status?: string }) {
  return request.get<PageResult<ExamRecord>, { data: PageResult<ExamRecord> }>('/answers/exams', { params })
}

export function getAnswerExamDetailApi(examId: number) {
  return request.get<ExamRecord & { questions?: AnswerRecord[] }, { data: ExamRecord & { questions?: AnswerRecord[] } }>(
    `/answers/exams/${examId}`
  )
}

export function startExamApi(examId: number) {
  return request.post(`/answers/exams/${examId}/start`)
}

export function saveAnswerApi(examId: number, data: { answers: Array<{ questionId: number; answers: string[] }> }) {
  return request.put(`/answers/exams/${examId}`, data)
}

export function submitAnswerApi(examId: number) {
  return request.post(`/answers/exams/${examId}/submit`)
}

export function getAnswerDetailApi(examId: number) {
  return request.get<AnswerRecord[], { data: AnswerRecord[] }>(`/answers/exams/${examId}/detail`)
}
