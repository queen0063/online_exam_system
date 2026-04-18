import type { AppRouteRecordRaw } from '@/types'

export function hasPermission(route: AppRouteRecordRaw, roles: string[]) {
  if (!route.meta?.roles || route.meta.roles.length === 0) {
    return true
  }
  return route.meta.roles.some((role) => roles.includes(role))
}

export function filterAsyncRoutes(routes: AppRouteRecordRaw[], roles: string[]): AppRouteRecordRaw[] {
  return routes
    .filter((route) => hasPermission(route, roles))
    .map((route) => {
      const currentRoute: AppRouteRecordRaw = { ...route }
      if (currentRoute.children) {
        currentRoute.children = filterAsyncRoutes(currentRoute.children, roles)
      }
      return currentRoute
    })
}
