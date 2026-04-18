import request from '@/utils/request'
import type { NoticeRecord, PageResult } from '@/types'

export function getNoticePageApi(params: { pageNum?: number; pageSize?: number; keyword?: string; noticeStatus?: string }) {
  return request.get<PageResult<NoticeRecord>, { data: PageResult<NoticeRecord> }>('/notices', { params })
}

export function getNoticeDetailApi(id: number) {
  return request.get<NoticeRecord, { data: NoticeRecord }>(`/notices/${id}`)
}

export function saveNoticeApi(data: NoticeRecord) {
  return request.post('/notices', data)
}

export function deleteNoticeApi(id: number) {
  return request.delete(`/notices/${id}`)
}
