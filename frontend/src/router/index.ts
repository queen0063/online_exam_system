import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

import { useTabsStore } from '@/stores/tabs'
import { useUserStore } from '@/stores/user'
import { staticRoutes, notFoundRoute } from './routes'

const DEFAULT_AUTHED_PATH = '/dashboard'
const PUBLIC_PATHS = ['/', '/login', '/register']

const router = createRouter({
  history: createWebHistory(),
  routes: staticRoutes as RouteRecordRaw[],
  scrollBehavior: () => ({ top: 0 })
})

let routesInjected = false
let removeInjectedRoutes: Array<() => void> = []

router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore()
  const hasToken = !!userStore.token

  document.title = `${to.meta.title || '在线考试系统'} - 在线考试系统`

  if (hasToken && PUBLIC_PATHS.includes(to.path)) {
    next({ path: DEFAULT_AUTHED_PATH, replace: true })
    return
  }

  if (!hasToken && !PUBLIC_PATHS.includes(to.path)) {
    next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
    return
  }

  if (hasToken && !userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
    } catch {
      next('/login')
      return
    }
  }

  if (hasToken && !routesInjected) {
    resetRouter()
    const routes = userStore.buildRoutes()
    routes.forEach((route) => {
      removeInjectedRoutes.push(router.addRoute(route))
    })
    removeInjectedRoutes.push(router.addRoute(notFoundRoute as RouteRecordRaw))
    routesInjected = true
    next(to.path === '/' ? { path: DEFAULT_AUTHED_PATH, replace: true } : { ...to, replace: true })
    return
  }

  if (to.meta?.roles) {
    const hasPermission = (to.meta.roles as string[]).some((role) => userStore.roleCodes.includes(role))
    if (!hasPermission) {
      next('/403')
      return
    }
  }

  const tabsStore = useTabsStore()
  if (!PUBLIC_PATHS.includes(to.path)) {
    tabsStore.addTab(to)
  }

  next()
})

export function resetRouter() {
  removeInjectedRoutes.reverse().forEach((removeRoute) => removeRoute())
  removeInjectedRoutes = []
  routesInjected = false
}

export default router
