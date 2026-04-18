<template>
  <div class="layout-shell">
    <aside class="layout-shell__sidebar" :class="{ collapsed: appStore.sidebarCollapsed }">
      <app-sidebar />
    </aside>
    <div class="layout-shell__main">
      <app-header />
      <app-tags-view />
      <main class="layout-shell__content">
        <router-view v-slot="{ Component }">
          <transition name="fade-slide" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted } from 'vue'

import AppHeader from '@/components/layout/AppHeader.vue'
import AppSidebar from '@/components/layout/AppSidebar.vue'
import AppTagsView from '@/components/layout/AppTagsView.vue'
import { useAppStore } from '@/stores/app'

const appStore = useAppStore()

function handleResize() {
  appStore.setMobile(window.innerWidth < 960)
}

onMounted(() => {
  handleResize()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped lang="scss">
.layout-shell {
  display: flex;
  min-height: 100vh;
}

.layout-shell__sidebar {
  position: sticky;
  top: 0;
  width: $sidebar-width;
  min-height: 100vh;
  transition: width 0.2s ease;
}

.layout-shell__sidebar.collapsed {
  width: $sidebar-collapse-width;
}

.layout-shell__main {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
}

.layout-shell__content {
  flex: 1;
  padding: 20px;
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.2s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(6px);
}

@media (max-width: 960px) {
  .layout-shell__sidebar {
    position: fixed;
    z-index: 20;
    box-shadow: $shadow-card;
  }

  .layout-shell__sidebar.collapsed {
    width: 0;
    overflow: hidden;
  }
}
</style>
