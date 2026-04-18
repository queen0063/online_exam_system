import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { RouteLocationNormalizedLoaded } from 'vue-router'

export interface TabItem {
  fullPath: string
  path: string
  title: string
}

export const useTabsStore = defineStore('tabs', () => {
  const visitedTabs = ref<TabItem[]>([])

  function addTab(route: RouteLocationNormalizedLoaded) {
    if (!route.meta?.title || route.meta.hidden) {
      return
    }
    const exists = visitedTabs.value.find((item) => item.fullPath === route.fullPath)
    if (exists) {
      return
    }
    visitedTabs.value.push({
      fullPath: route.fullPath,
      path: route.path,
      title: String(route.meta.title)
    })
  }

  function removeTab(fullPath: string) {
    visitedTabs.value = visitedTabs.value.filter((item) => item.fullPath !== fullPath)
  }

  function resetTabs() {
    visitedTabs.value = []
  }

  return {
    visitedTabs,
    addTab,
    removeTab,
    resetTabs
  }
})
