<template>
  <page-container title="个人中心" description="查看当前登录用户信息并修改登录密码。">
    <div class="two-column">
      <div class="app-card profile-card">
        <div class="profile-card__avatar">
          <el-avatar :size="88">{{ userStore.userInfo?.realName?.slice(0, 1) || 'U' }}</el-avatar>
          <div>
            <h3>{{ userStore.userInfo?.realName }}</h3>
            <p>{{ userStore.userInfo?.username }}</p>
          </div>
        </div>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="用户 ID">{{ userStore.userInfo?.userId }}</el-descriptions-item>
          <el-descriptions-item label="角色">{{ userStore.roleCodes.join('、') }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ userStore.userInfo?.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ userStore.userInfo?.email || '-' }}</el-descriptions-item>

          <!-- 学生专属信息 -->
          <template v-if="userStore.roleCodes.includes('STUDENT')">
            <el-descriptions-item label="学号">{{ userStore.userInfo?.studentNo || '-' }}</el-descriptions-item>
            <el-descriptions-item label="年级">{{ userStore.userInfo?.gradeName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="班级">{{ userStore.userInfo?.className || '-' }}</el-descriptions-item>
          </template>

          <!-- 教师专属信息 -->
          <template v-if="userStore.roleCodes.includes('TEACHER')">
            <el-descriptions-item label="负责班级">
              <span v-if="userStore.userInfo?.teacherClasses?.length">
                <el-tag
                  v-for="cls in userStore.userInfo.teacherClasses"
                  :key="cls.id"
                  class="profile-card__class-tag"
                  effect="light"
                >
                  {{ cls.gradeName ? `${cls.gradeName} ` : '' }}{{ cls.className }}
                </el-tag>
              </span>
              <span v-else>-</span>
            </el-descriptions-item>
          </template>
        </el-descriptions>
      </div>

      <div class="app-card profile-card">
        <h3>修改密码</h3>
        <el-form ref="formRef" :model="passwordForm" :rules="rules" label-width="90px">
          <el-form-item label="旧密码" prop="oldPassword">
            <el-input v-model="passwordForm.oldPassword" show-password />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="passwordForm.newPassword" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="passwordForm.confirmPassword" show-password />
          </el-form-item>
          <el-button type="primary" @click="handleChangePassword">更新密码</el-button>
        </el-form>
      </div>
    </div>
  </page-container>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

import { changePasswordApi } from '@/api/modules/user'
import PageContainer from '@/components/common/PageContainer.vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const formRef = ref<FormInstance>()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules: FormRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [
    {
      validator: (_rule, value, callback) => {
        if (!value) {
          callback(new Error('请再次输入新密码'))
          return
        }
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的新密码不一致'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
}

async function handleChangePassword() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }
  await changePasswordApi({
    oldPassword: passwordForm.oldPassword,
    newPassword: passwordForm.newPassword
  })
  ElMessage.success('密码修改成功')
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}
</script>

<style scoped lang="scss">
.profile-card {
  padding: 24px;
}

.profile-card__avatar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.profile-card__avatar h3 {
  margin: 0;
}

.profile-card__avatar p {
  margin: 8px 0 0;
  color: $app-sub-text-color;
}

.profile-card__class-tag {
  margin-right: 6px;
  margin-bottom: 4px;
}
</style>
