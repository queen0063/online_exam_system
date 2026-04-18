<template>
  <header class="app-header">
    <div class="app-header__left">
      <el-button circle plain @click="appStore.toggleSidebar">
        <el-icon><app-icon name="Fold" /></el-icon>
      </el-button>
      <app-breadcrumb />
    </div>
    <div class="app-header__right">
      <el-tag type="primary" round>{{ currentRoleLabel }}</el-tag>
      <el-dropdown @command="handleCommand">
        <div class="app-header__user">
          <el-avatar :size="36">{{ userStore.userInfo?.realName?.slice(0, 1) || 'U' }}</el-avatar>
          <div class="app-header__meta">
            <strong>{{ userStore.userInfo?.realName }}</strong>
            <span>{{ userStore.userInfo?.username }}</span>
          </div>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'

import AppIcon from '@/components/common/AppIcon.vue'
import { resetRouter } from '@/router'
import AppBreadcrumb from './AppBreadcrumb.vue'
import { useAppStore } from '@/stores/app'
import { useTabsStore } from '@/stores/tabs'
import { useUserStore } from '@/stores/user'
import { ROLE_OPTIONS } from '@/utils/dicts'

const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()
const tabsStore = useTabsStore()

const currentRoleLabel = computed(() => {
  const currentRole = userStore.roleCodes[0]
  return ROLE_OPTIONS.find((item) => item.value === currentRole)?.label || '访客'
})

async function handleCommand(command: string) {
  if (command === 'profile') {
    await router.push('/profile/index')
    return
  }
  if (command === 'logout') {
    await userStore.logoutAction()
    resetRouter()
    tabsStore.resetTabs()
    await router.replace('/login')
  }
}
</script>

<style scoped lang="scss">
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: $header-height;
  padding: 0 20px;
  background: rgba(255, 255, 255, 0.86);
  border-bottom: 1px solid rgba(230, 235, 242, 0.9);
  backdrop-filter: blur(10px);
}

.app-header__left,
.app-header__right {
  display: flex;
  align-items: center;
  gap: 14px;
}

.app-header__user {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.app-header__meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.app-header__meta strong {
  font-size: 14px;
}

.app-header__meta span {
  color: $app-sub-text-color;
  font-size: 12px;
}
</style>
