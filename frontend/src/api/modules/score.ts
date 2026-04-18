import type { AxiosResponse } from 'axios'

import request from '@/utils/request'
import type { PageResult, ScoreRecord, ScoreStatistics } from '@/types'

export function getScorePageApi(params: { pageNum?: number; pageSize?: number; examId?: number | ''; keyword?: string }) {
  return request.get<PageResult<ScoreRecord>, { data: PageResult<ScoreRecord> }>('/scores', { params })
}

export function getMyScorePageApi(params: { pageNum?: number; pageSize?: number; examId?: number | ''; keyword?: string }) {
  return request.get<PageResult<ScoreRecord>, { data: PageResult<ScoreRecord> }>('/scores/my', { params })
}

export function getScoreDetailApi(examId: number, studentId?: number) {
  return request.get<ScoreRecord, { data: ScoreRecord }>('/scores/exams/' + examId, { params: { studentId } })
}

export function getScoreStatisticsApi(examId: number) {
  return request.get<ScoreStatistics, { data: ScoreStatistics }>(`/scores/exams/${examId}/statistics`)
}

export function getScoreRankingApi(examId: number) {
  return request.get<ScoreRecord[], { data: ScoreRecord[] }>(`/scores/exams/${examId}/ranking`)
}

export function exportScoreApi(examId: number) {
  return request.request<Blob, AxiosResponse<Blob>>({
    url: `/scores/exams/${examId}/export`,
    method: 'post',
    responseType: 'blob'
  })
}
