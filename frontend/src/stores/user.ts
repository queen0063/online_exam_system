import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import type { RouteRecordRaw } from 'vue-router'

import { getCurrentUserApi, loginApi, logoutApi } from '@/api/modules/auth'
import { asyncRoutes } from '@/router/routes'
import { clearCachedUser, clearToken, getCachedUser, getToken, setCachedUser, setToken } from '@/utils/auth'
import { filterAsyncRoutes } from '@/utils/route'
import type { AppRouteRecordRaw, LoginPayload, UserInfo } from '@/types'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken() || '')
  const userInfo = ref<UserInfo | null>(getCachedUser())
  const dynamicRoutes = ref<AppRouteRecordRaw[]>([])
  const routesLoaded = ref(false)

  const roleCodes = computed(() => userInfo.value?.roleCodes || [])

  async function loginAction(payload: LoginPayload) {
    const result = await loginApi(payload)
    token.value = result.data.token
    setToken(result.data.token)
    userInfo.value = result.data.userInfo
    setCachedUser(result.data.userInfo)
    routesLoaded.value = false
    return result.data
  }

  async function fetchUserInfo() {
    const result = await getCurrentUserApi()
    userInfo.value = result.data
    setCachedUser(result.data)
    return result.data
  }

  function buildRoutes() {
    const accessedRoutes = filterAsyncRoutes(asyncRoutes, roleCodes.value)
    dynamicRoutes.value = accessedRoutes
    routesLoaded.value = true
    return accessedRoutes as RouteRecordRaw[]
  }

  async function logoutAction() {
    try {
      await logoutApi()
    } finally {
      resetState()
    }
  }

  function resetState() {
    token.value = ''
    userInfo.value = null
    dynamicRoutes.value = []
    routesLoaded.value = false
    clearToken()
    clearCachedUser()
  }

  return {
    token,
    userInfo,
    roleCodes,
    dynamicRoutes,
    routesLoaded,
    loginAction,
    fetchUserInfo,
    buildRoutes,
    logoutAction,
    resetState
  }
})
