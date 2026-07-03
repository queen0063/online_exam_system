<template>
  <div class="layout-shell">
    <aside class="layout-shell__sidebar" :class="{ collapsed: appStore.sidebarCollapsed }">
      <app-sidebar />
    </aside>
    <div class="layout-shell__mask" :class="{ visible: appStore.isMobile && !appStore.sidebarCollapsed }" @click="appStore.toggleSidebar" />
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
  width: $sidebar-width;
  min-height: 100vh;
  background:
    linear-gradient(180deg, rgba(15, 23, 42, 0.96), rgba(30, 41, 59, 0.96)),
    radial-gradient(circle at top 20% left 30%, rgba(15, 108, 189, 0.12), transparent 35%);
  transition: width $duration-base $ease-fluent;
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
  transition: all $duration-slow $ease-fluent;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(8px);
}

@media (max-width: 960px) {
  .layout-shell__sidebar {
    position: fixed;
    top: 0;
    bottom: 0;
    z-index: 100;
    box-shadow: $shadow-xl;
    transition: transform $duration-base $ease-fluent;
  }

  .layout-shell__sidebar.collapsed {
    transform: translateX(-100%);
    width: $sidebar-width;
  }

  .layout-shell__mask {
    position: fixed;
    inset: 0;
    z-index: 99;
    background: rgba(0, 0, 0, 0.45);
    opacity: 0;
    pointer-events: none;
    transition: opacity $duration-base $ease-fluent;
  }

  .layout-shell__mask.visible {
    opacity: 1;
    pointer-events: auto;
  }
}
</style>
