<template>
  <div class="tags-view">
    <el-scrollbar>
      <div class="tags-view__list">
        <router-link
          v-for="item in tabsStore.visitedTabs"
          :key="item.fullPath"
          :to="item.fullPath"
          class="tags-view__item"
          :class="{ active: item.fullPath === route.fullPath }"
        >
          <span>{{ item.title }}</span>
          <el-icon v-if="item.path !== '/dashboard'" @click.prevent.stop="closeTag(item.fullPath)">
            <Close />
          </el-icon>
        </router-link>
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'

import { Close } from '@element-plus/icons-vue'
import { useTabsStore } from '@/stores/tabs'

const route = useRoute()
const router = useRouter()
const tabsStore = useTabsStore()

async function closeTag(fullPath: string) {
  tabsStore.removeTab(fullPath)
  if (route.fullPath === fullPath) {
    const fallback = tabsStore.visitedTabs.at(-1)
    await router.push(fallback?.fullPath || '/dashboard')
  }
}
</script>

<style scoped lang="scss">
.tags-view {
  padding: 10px 20px 0;
}

.tags-view__list {
  display: flex;
  gap: 10px;
  padding-bottom: 6px;
}

.tags-view__item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 14px;
  border-radius: $radius-pill;
  background: $app-surface-overlay;
  border: 1px solid $app-border-subtle;
  color: $app-text-secondary;
  white-space: nowrap;
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  transition:
    background-color $duration-fast $ease-fluent,
    border-color $duration-fast $ease-fluent,
    color $duration-fast $ease-fluent;
}

.tags-view__item:hover {
  background: rgba(255, 255, 255, 0.92);
  border-color: $app-border-color;
}

.tags-view__item.active {
  color: #fff;
  background: linear-gradient(135deg, $app-primary, $app-primary-hover);
  border-color: transparent;
  box-shadow: 0 2px 8px rgba(15, 108, 189, 0.2);
}
</style>
