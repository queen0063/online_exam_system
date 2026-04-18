import request from '@/utils/request'
import type { PageResult, QuestionRecord } from '@/types'

export interface QuestionQuery {
  pageNum?: number
  pageSize?: number
  subjectId?: number | ''
  questionType?: string
  difficulty?: string
  knowledgePoint?: string
  keyword?: string
}

export function getQuestionPageApi(params: QuestionQuery) {
  return request.get<PageResult<QuestionRecord>, { data: PageResult<QuestionRecord> }>('/questions', { params })
}

export function getQuestionDetailApi(id: number) {
  return request.get<QuestionRecord, { data: QuestionRecord }>(`/questions/${id}`)
}

export function saveQuestionApi(data: QuestionRecord) {
  return request.post('/questions', data)
}

export function deleteQuestionApi(id: number) {
  return request.delete(`/questions/${id}`)
}

export function getWrongQuestionApi() {
  return request.get<QuestionRecord[], { data: QuestionRecord[] }>('/questions/wrong')
}
