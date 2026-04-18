import type { AxiosResponse } from 'axios'

export function resolveDownloadFileName(response: AxiosResponse<Blob>, fallbackName: string) {
  const contentDisposition = response.headers['content-disposition'] || response.headers['Content-Disposition']
  if (!contentDisposition) {
    return fallbackName
  }

  const utf8Match = contentDisposition.match(/filename\*=UTF-8''([^;]+)/i)
  if (utf8Match?.[1]) {
    return decodeURIComponent(utf8Match[1])
  }

  const plainMatch = contentDisposition.match(/filename="?([^";]+)"?/i)
  if (plainMatch?.[1]) {
    return plainMatch[1]
  }

  return fallbackName
}

export function downloadBlobFile(blob: Blob, fileName: string) {
  const link = document.createElement('a')
  const url = window.URL.createObjectURL(blob)
  link.href = url
  link.download = fileName
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}
