import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)
  const isMobile = ref(false)

  const sidebarWidth = computed(() => (sidebarCollapsed.value ? '72px' : '228px'))

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  function setMobile(value: boolean) {
    isMobile.value = value
    if (value) {
      sidebarCollapsed.value = true
    }
  }

  return {
    sidebarCollapsed,
    sidebarWidth,
    isMobile,
    toggleSidebar,
    setMobile
  }
})
