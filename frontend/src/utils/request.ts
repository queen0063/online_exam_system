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
      ElMessage.error(result.message || '请求失败')
      return Promise.reject(result)
    }
    return result as any
  }) as any,
  async (error) => {
    const status = error?.response?.status
    const message = error?.response?.data?.message || error?.message || '网络异常'

    if (status === 401) {
      const userStore = useUserStore()
      userStore.resetState()
      resetRouter()
      if (router.currentRoute.value.path !== '/login') {
        await router.replace(`/login?redirect=${encodeURIComponent(router.currentRoute.value.fullPath)}`)
      }
      ElMessage.error('登录已失效，请重新登录')
      return Promise.reject(error)
    }

    if (status === 403) {
      router.replace('/403')
    }

    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default request
