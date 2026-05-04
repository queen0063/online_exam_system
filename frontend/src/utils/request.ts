import axios from 'axios'
import { ElMessage } from 'element-plus'

import router from '@/router'
import { resetRouter } from '@/router'
import { useUserStore } from '@/stores/user'
import type { ApiResult } from '@/types'
import { getToken } from './auth'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

let isHandlingUnauthorized = false

function isLoginRequest(url?: string) {
  return url === '/auth/login' || url?.endsWith('/auth/login')
}

function getAuthorizationToken(config: any) {
  const headers = config?.headers
  const authorization =
    headers?.Authorization ||
    headers?.authorization ||
    headers?.get?.('Authorization') ||
    headers?.get?.('authorization')

  if (typeof authorization === 'string' && authorization.startsWith('Bearer ')) {
    return authorization.slice(7)
  }

  return ''
}

async function handleUnauthorized(error: unknown, config?: any) {
  const requestToken = getAuthorizationToken(config)
  const currentToken = getToken()

  if (requestToken && currentToken && requestToken !== currentToken) {
    return Promise.reject(error)
  }

  if (isHandlingUnauthorized) {
    return Promise.reject(error)
  }

  isHandlingUnauthorized = true

  const userStore = useUserStore()
  const currentRoute = router.currentRoute.value

  userStore.resetState()
  resetRouter()
  ElMessage.error('登录已失效，请重新登录')

  if (currentRoute.path !== '/login') {
    await router.replace({
      path: '/login',
      query: { redirect: currentRoute.fullPath }
    })
  }

  return Promise.reject(error)
}

request.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  ((response: any) => {
    if (response.config?.responseType === 'blob') {
      return response
    }
    const result = response.data as ApiResult
    if (result.code !== 200) {
      if (result.code === 401 && !isLoginRequest(response.config?.url)) {
        return handleUnauthorized(result, response.config)
      }
      ElMessage.error(result.message || '请求失败')
      return Promise.reject(result)
    }
    if (isLoginRequest(response.config?.url)) {
      isHandlingUnauthorized = false
    }
    return result as any
  }) as any,
  async (error) => {
    const status = error?.response?.status
    const message = error?.response?.data?.message || error?.message || '网络异常'

    if (status === 401) {
      return handleUnauthorized(error, error?.config)
    }

    if (status === 403) {
      router.replace('/403')
    }

    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
