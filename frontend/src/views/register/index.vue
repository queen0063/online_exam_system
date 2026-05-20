<template>
  <div class="register-page">
    <div class="app-card register-card">
      <div class="register-card__header">
        <div>
          <h1>账号注册</h1>
          <p>教师账号提交后需管理员启用，学生账号注册后可直接登录。</p>
        </div>
        <el-button text @click="goLogin">返回登录</el-button>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="handleSubmit">
        <el-form-item label="注册身份" prop="roleCode">
          <el-radio-group v-model="form.roleCode">
            <el-radio-button label="STUDENT">学生</el-radio-button>
            <el-radio-button label="TEACHER">教师</el-radio-button>
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

        <el-button class="w-full submit-button" type="primary" size="large" :loading="loading" @click="handleSubmit">
          提交注册
        </el-button>
      </el-form>
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
}

.register-card {
  width: min(760px, 100%);
  padding: 32px;
}

.register-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
}

.register-card__header h1 {
  margin: 0;
  font-size: 28px;
}

.register-card__header p {
  margin: 8px 0 0;
  color: $app-sub-text-color;
}

.submit-button {
  margin-top: 18px;
}

@media (max-width: 720px) {
  .register-page {
    padding: 16px;
  }

  .register-card {
    padding: 20px;
  }

  .register-card__header {
    display: grid;
  }
}
</style>
