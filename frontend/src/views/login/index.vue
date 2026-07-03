<template>
  <div class="login-page">
    <div class="login-page__card">
      <div class="login-page__brand">
        <img class="login-page__logo" src="/logo.png" alt="Logo" />
        <h1>在线考试系统</h1>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="handleLogin">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" show-password placeholder="请输入密码" size="large" />
        </el-form-item>
        <el-button class="w-full" type="primary" size="large" :loading="loading" @click="handleLogin">
          登录
        </el-button>
      </el-form>
      <div class="login-page__footer">
        <span>还没有账号？</span>
        <el-button text type="primary" @click="goRegister">注册</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

import { useUserStore } from '@/stores/user'

const DEFAULT_AUTHED_PATH = '/dashboard'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  loading.value = true
  try {
    await userStore.loginAction(form)
    ElMessage.success('登录成功')
    const redirect = (route.query.redirect as string) || '/'
    await router.replace(redirect === '/' ? DEFAULT_AUTHED_PATH : redirect)
  } catch {
    // 请求层已统一弹出后端返回的真实错误信息，这里不再覆盖提示。
  } finally {
    loading.value = false
  }
}

function goRegister() {
  router.push('/register')
}
</script>

<style scoped lang="scss">
.login-page {
  display: grid;
  min-height: 100vh;
  place-items: center;
  padding: 32px;
  background:
    radial-gradient(circle at 30% 20%, rgba(15, 108, 189, 0.08), transparent 40%),
    radial-gradient(circle at 80% 80%, rgba(139, 92, 246, 0.04), transparent 35%),
    linear-gradient(180deg, #f7f9fc 0%, #f3f6f9 100%);
}

.login-page__card {
  width: min(420px, 100%);
  padding: 40px 36px 32px;
  background: $app-surface-card;
  border: 1px solid $app-border-subtle;
  border-radius: $radius-2xl;
  box-shadow: $shadow-lg;
}

.login-page__brand {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 32px;
}

.login-page__logo {
  width: 42px;
  height: 42px;
  border-radius: $radius-lg;
  object-fit: contain;
  flex-shrink: 0;
}

.login-page__brand h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: $app-text-color;
}

.login-page__footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin-top: 16px;
  color: $app-text-secondary;
  font-size: 14px;
}

@media (max-width: 480px) {
  .login-page {
    padding: 16px;
  }

  .login-page__card {
    padding: 32px 24px 24px;
  }
}
</style>
