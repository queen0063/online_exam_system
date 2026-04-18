import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

import { useTabsStore } from '@/stores/tabs'
import { useUserStore } from '@/stores/user'
import { staticRoutes, notFoundRoute } from './routes'

const router = createRouter({
  history: createWebHistory(),
  routes: staticRoutes as RouteRecordRaw[],
  scrollBehavior: () => ({ top: 0 })
})

let routesInjected = false
const injectedRouteNames = new Set<string>()

function collectRouteNames(routes: RouteRecordRaw[]) {
  routes.forEach((route) => {
    if (route.name) {
      injectedRouteNames.add(String(route.name))
    }
    if (route.children?.length) {
      collectRouteNames(route.children)
    }
  })
}

router.beforeEach(async (to, _from, next) => {
  const userStore = useUserStore()
  const hasToken = !!userStore.token

  document.title = `${to.meta.title || '在线考试系统'} - 在线考试系统`

  if (hasToken && to.path === '/login') {
    next({ path: '/' })
    return
  }

  if (!hasToken && to.path !== '/login') {
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
    const routes = userStore.buildRoutes()
    routes.forEach((route) => router.addRoute(route))
    collectRouteNames(routes)
    router.addRoute(notFoundRoute as RouteRecordRaw)
    injectedRouteNames.add('404-fallback')
    routesInjected = true
    next({ ...to, replace: true })
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
  if (to.path !== '/login') {
    tabsStore.addTab(to)
  }

  next()
})

export function resetRouter() {
  injectedRouteNames.forEach((name) => {
    if (name !== '404-fallback' && router.hasRoute(name)) {
      router.removeRoute(name)
    }
  })
  injectedRouteNames.clear()
  routesInjected = false
}

export default router
