import dayjs from 'dayjs'

export function formatDateTime(value?: string, format = 'YYYY-MM-DD HH:mm:ss') {
  return value ? dayjs(value).format(format) : '-'
}

export function formatPercent(value?: number) {
  if (value === undefined || value === null || Number.isNaN(value)) {
    return '0%'
  }
  return `${Number(value).toFixed(2)}%`
}

export function secondsToClock(seconds: number) {
  const total = Math.max(seconds, 0)
  const hour = String(Math.floor(total / 3600)).padStart(2, '0')
  const minute = String(Math.floor((total % 3600) / 60)).padStart(2, '0')
  const second = String(total % 60).padStart(2, '0')
  return `${hour}:${minute}:${second}`
}
