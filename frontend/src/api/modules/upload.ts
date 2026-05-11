import axios from 'axios'

import request from '@/utils/request'

export interface UpyunPolicy {
  uploadUrl: string
  policy: string
  authorization: string
  domain: string
  expiresAt: number
}

interface UpyunUploadResult {
  code?: number | string
  message?: string
  url?: string
}

export function getUpyunPolicyApi() {
  return request.get<UpyunPolicy, { data: UpyunPolicy }>('/uploads/upyun/policy')
}

export async function uploadToUpyunApi(file: File) {
  const { data: policy } = await getUpyunPolicyApi()
  const formData = new FormData()
  formData.append('policy', policy.policy)
  formData.append('authorization', policy.authorization)
  formData.append('file', file)
  const response = await axios.post<UpyunUploadResult | string>(policy.uploadUrl, formData, {
    timeout: 60000
  })
  const result = parseUpyunResult(response.data)
  if (String(result.code || '200') !== '200' || !result.url) {
    throw new Error(result.message || '又拍云上传失败')
  }
  return normalizeImageUrl(policy.domain, result.url)
}

function parseUpyunResult(data: UpyunUploadResult | string): UpyunUploadResult {
  if (typeof data !== 'string') {
    return data
  }
  try {
    return JSON.parse(data) as UpyunUploadResult
  } catch {
    const params = new URLSearchParams(data)
    return {
      code: params.get('code') || undefined,
      message: params.get('message') || undefined,
      url: params.get('url') ? decodeURIComponent(params.get('url') as string) : undefined
    }
  }
}

function normalizeImageUrl(domain: string, url: string) {
  const decodedUrl = safeDecode(url)
  if (/^https?:\/\//i.test(decodedUrl)) {
    return decodedUrl
  }
  const normalizedDomain = /^https?:\/\//i.test(domain) ? domain : `https://${domain}`
  return `${normalizedDomain.replace(/\/$/, '')}/${decodedUrl.replace(/^\//, '')}`
}

function safeDecode(value: string) {
  try {
    return decodeURIComponent(value)
  } catch {
    return value
  }
}
