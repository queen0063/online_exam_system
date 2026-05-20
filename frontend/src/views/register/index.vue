<template>
  <div class="register-page">
    <div class="register-page__card">
      <div class="register-page__brand">
        <div class="register-page__logo">EX</div>
        <div>
          <h1>账号注册</h1>
          <p>选择身份，填写信息完成注册</p>
        </div>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="handleSubmit">
        <el-form-item label="注册身份" prop="roleCode">
          <el-radio-group v-model="form.roleCode">
            <el-radio-button value="STUDENT">学生</el-radio-button>
            <el-radio-button value="TEACHER">教师</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <div class="two-column">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
          </el-form-item>
          <el-form-item label="姓名" prop="realName">
            <el-input v-model="form.realName" placeholder="请输入真实姓名" size="large" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" show-password placeholder="至少 6 位" size="large" />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="form.confirmPassword" show-password placeholder="请再次输入密码" size="large" />
          </el-form-item>
          <el-form-item v-if="form.roleCode === 'STUDENT'" label="学号" prop="studentNo">
            <el-input v-model="form.studentNo" placeholder="请输入学号" size="large" />
          </el-form-item>
          <el-form-item v-if="form.roleCode === 'STUDENT'" label="班级" prop="classId">
            <el-select v-model="form.classId" class="w-full" placeholder="请选择班级" size="large">
              <el-option v-for="item in classOptions" :key="item.id" :label="classLabel(item)" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" placeholder="请输入手机号" size="large" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="form.email" placeholder="请输入邮箱" size="large" />
          </el-form-item>
        </div>

        <el-alert
          v-if="form.roleCode === 'TEACHER'"
          type="warning"
          :closable="false"
          title="教师账号注册后默认为禁用状态，需要管理员在用户管理中启用后才能登录。"
        />

        <el-button class="w-full register-page__submit" type="primary" size="large" :loading="loading" @click="handleSubmit">
          提交注册
        </el-button>
      </el-form>

      <div class="register-page__footer">
        <span>已有账号？</span>
        <el-button text type="primary" @click="goLogin">返回登录</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

import { getRegisterClassListApi, registerApi } from '@/api/modules/auth'
import type { ClassInfoRecord, RegisterPayload } from '@/types'

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const classOptions = ref<ClassInfoRecord[]>([])

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  roleCode: 'STUDENT' as RegisterPayload['roleCode'],
  studentNo: '',
  classId: undefined as number | undefined,
  phone: '',
  email: ''
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次输入的密码不一致'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请选择注册身份', trigger: 'change' }],
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  classId: [{ required: true, message: '请选择班级', trigger: 'change' }]
}

watch(
  () => form.roleCode,
  (roleCode) => {
    if (roleCode === 'TEACHER') {
      form.studentNo = ''
      form.classId = undefined
    }
  }
)

function classLabel(item: ClassInfoRecord) {
  return item.gradeName ? `${item.gradeName} ${item.className}` : item.className
}

async function loadClasses() {
  const result = await getRegisterClassListApi()
  classOptions.value = result.data
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  const payload: RegisterPayload = {
    username: form.username,
    password: form.password,
    realName: form.realName,
    roleCode: form.roleCode,
    phone: form.phone,
    email: form.email
  }
  if (form.roleCode === 'STUDENT') {
    payload.studentNo = form.studentNo
    payload.classId = form.classId
  }

  loading.value = true
  try {
    await registerApi(payload)
    ElMessage.success(form.roleCode === 'TEACHER' ? '注册成功，请等待管理员启用账号' : '注册成功，请登录')
    await router.replace('/login')
  } finally {
    loading.value = false
  }
}

function goLogin() {
  router.push('/login')
}

onMounted(loadClasses)
</script>

<style scoped lang="scss">
.register-page {
  display: grid;
  min-height: 100vh;
  place-items: center;
  padding: 32px;
  background:
    radial-gradient(circle at 30% 20%, rgba(15, 108, 189, 0.08), transparent 40%),
    radial-gradient(circle at 80% 80%, rgba(139, 92, 246, 0.04), transparent 35%),
    linear-gradient(180deg, #f7f9fc 0%, #f3f6f9 100%);
}

.register-page__card {
  width: min(720px, 100%);
  padding: 40px 36px 32px;
  background: $app-surface-card;
  border: 1px solid $app-border-subtle;
  border-radius: $radius-2xl;
  box-shadow: $shadow-lg;
}

.register-page__brand {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 32px;
}

.register-page__logo {
  display: grid;
  width: 42px;
  height: 42px;
  place-items: center;
  border-radius: $radius-lg;
  background: linear-gradient(135deg, $app-primary, $app-primary-hover);
  color: #fff;
  font-weight: 800;
  font-size: 16px;
  flex-shrink: 0;
}

.register-page__brand h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: $app-text-color;
}

.register-page__brand p {
  margin: 4px 0 0;
  color: $app-text-secondary;
  font-size: 13px;
}

.register-page__submit {
  margin-top: 18px;
}

.register-page__footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin-top: 16px;
  color: $app-text-secondary;
  font-size: 14px;
}

@media (max-width: 720px) {
  .register-page {
    padding: 16px;
  }

  .register-page__card {
    padding: 32px 24px 24px;
  }

  .register-page__brand {
    flex-direction: column;
    text-align: center;
  }
}
</style>
