<template>
  <div class="sidebar">
    <div class="sidebar__brand">
      <div class="sidebar__logo">EX</div>
      <div v-if="!appStore.sidebarCollapsed" class="sidebar__title">
        <strong>在线考试系统</strong>
        <span>Online Exam Admin</span>
      </div>
    </div>

    <el-scrollbar class="sidebar__menu">
      <el-menu
        :default-active="activeMenu"
        :collapse="appStore.sidebarCollapsed"
        unique-opened
        router
        background-color="transparent"
        text-color="#cbd5e1"
        active-text-color="#ffffff"
      >
        <template v-for="route in visibleRoutes" :key="route.path">
          <el-sub-menu v-if="route.children && route.children.length > 0 && !route.meta?.hidden" :index="route.path">
            <template #title>
              <el-icon>
                <app-icon :name="route.meta?.icon || 'Menu'" />
              </el-icon>
              <span>{{ route.meta?.title }}</span>
            </template>
            <el-menu-item
              v-for="child in route.children.filter((item) => !item.meta?.hidden)"
              :key="resolvePath(route.path, child.path)"
              :index="resolvePath(route.path, child.path)"
            >
              <el-icon>
                <app-icon :name="child.meta?.icon || 'Document'" />
              </el-icon>
              <span>{{ child.meta?.title }}</span>
            </el-menu-item>
          </el-sub-menu>

          <el-menu-item v-else-if="!route.meta?.hidden" :index="route.path">
            <el-icon>
              <app-icon :name="route.meta?.icon || 'Document'" />
            </el-icon>
            <span>{{ route.meta?.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

import AppIcon from '@/components/common/AppIcon.vue'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const appStore = useAppStore()
const userStore = useUserStore()

const activeMenu = computed(() => String(route.meta.activeMenu || route.path))
const visibleRoutes = computed(() => userStore.dynamicRoutes)

function resolvePath(parentPath: string, childPath: string) {
  return `${parentPath}/${childPath}`.replace(/\/+/g, '/')
}
</script>

<style scoped lang="scss">
.sidebar {
  position: sticky;
  top: 0;
  display: flex;
  flex-direction: column;
  height: 100vh;
  color: #fff;
}

.sidebar__brand {
  display: flex;
  align-items: center;
  gap: 12px;
  height: $header-height + 12px;
  padding: 0 18px;
}

.sidebar__logo {
  display: grid;
  width: 38px;
  height: 38px;
  place-items: center;
  border-radius: $radius-lg;
  background: linear-gradient(135deg, $app-primary, $app-primary-hover);
  font-weight: 800;
}

.sidebar__title {
  display: flex;
  flex-direction: column;
  gap: 4px;
  overflow: hidden;
}

.sidebar__title strong {
  font-size: 15px;
}

.sidebar__title span {
  color: rgba(226, 232, 240, 0.7);
  font-size: 12px;
}

.sidebar__menu {
  flex: 1;
  padding: 8px 12px 16px;
}

:deep(.el-menu) {
  border-right: none;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  margin-bottom: 6px;
  border-radius: $radius-lg;
  transition: background-color $duration-fast $ease-fluent;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: rgba(255, 255, 255, 0.08) !important;
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(15, 108, 189, 0.85), rgba(17, 94, 155, 0.95));
  box-shadow: 0 2px 8px rgba(15, 108, 189, 0.25);
}
</style>
