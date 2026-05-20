<template>
  <div class="login-page">
    <div class="login-page__panel">
      <div class="login-page__intro">
        <span class="login-page__badge">ONLINE EXAM SYSTEM</span>
        <h1>在线考试系统前端控制台</h1>
        <p>支持管理员、教师、学生三类角色，覆盖题库、试卷、考试、答题、阅卷与成绩全过程。</p>
        <div class="login-page__tips">
          <el-alert title="当前后端初始化账号：admin / Admin@123" type="info" :closable="false" />
          <el-alert title="当前后端初始化账号：teacher / Admin@123" type="success" :closable="false" />
          <el-alert title="当前后端初始化账号：student / Admin@123" type="warning" :closable="false" />
        </div>
      </div>

      <div class="app-card login-page__card">
        <div class="login-page__card-header">
          <h2>账号登录</h2>
          <span>请输入用户名和密码</span>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="handleLogin">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" show-password placeholder="请输入密码" size="large" />
          </el-form-item>
          <div class="login-page__accounts">
            <el-button text @click="fillAccount('admin')">管理员</el-button>
            <el-button text @click="fillAccount('teacher')">教师</el-button>
            <el-button text @click="fillAccount('student')">学生</el-button>
          </div>
          <el-button class="w-full" type="primary" size="large" :loading="loading" @click="handleLogin">
            登录系统
          </el-button>
          <div class="login-page__register">
            <span>还没有账号？</span>
            <el-button text @click="goRegister">注册账号</el-button>
          </div>
        </el-form>
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
  username: 'admin',
  password: 'Admin@123'
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

function fillAccount(username: string) {
  form.username = username
  form.password = 'Admin@123'
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
}

.login-page__panel {
  display: grid;
  width: min(1120px, 100%);
  grid-template-columns: 1.1fr 0.9fr;
  gap: 24px;
  align-items: stretch;
}

.login-page__intro {
  padding: 42px;
  border-radius: 28px;
  color: #fff;
  background:
    linear-gradient(135deg, rgba(15, 23, 42, 0.92), rgba(37, 99, 235, 0.88)),
    radial-gradient(circle at top right, rgba(96, 165, 250, 0.35), transparent 28%);
  box-shadow: 0 24px 50px rgba(15, 23, 42, 0.22);
}

.login-page__badge {
  display: inline-flex;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  font-size: 12px;
  letter-spacing: 0.12em;
}

.login-page__intro h1 {
  margin: 20px 0 14px;
  font-size: 42px;
  line-height: 1.15;
}

.login-page__intro p {
  margin: 0;
  max-width: 520px;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.8);
}

.login-page__tips {
  display: grid;
  gap: 12px;
  margin-top: 32px;
}

.login-page__card {
  padding: 32px;
}

.login-page__card-header {
  margin-bottom: 20px;
}

.login-page__card-header h2 {
  margin: 0;
  font-size: 28px;
}

.login-page__card-header span {
  display: block;
  margin-top: 8px;
  color: $app-sub-text-color;
}

.login-page__accounts {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-bottom: 12px;
}

.login-page__register {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin-top: 14px;
  color: $app-sub-text-color;
}

@media (max-width: 960px) {
  .login-page__panel {
    grid-template-columns: 1fr;
  }

  .login-page__intro {
    order: 2;
  }
}
</style>
