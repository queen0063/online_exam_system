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
  padding: 8px 12px;
  border-radius: 999px;
  background: #fff;
  border: 1px solid $app-border-color;
  color: $app-sub-text-color;
  white-space: nowrap;
}

.tags-view__item.active {
  color: #fff;
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  border-color: transparent;
}
</style>
