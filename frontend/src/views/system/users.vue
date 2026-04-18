<template>
  <page-container title="用户管理" description="管理员可维护系统用户、重置密码并分配角色。">
    <query-bar>
      <el-form :model="queryForm" inline>
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="用户名 / 姓名" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="queryForm.roleId" placeholder="全部角色" clearable style="width: 180px">
            <el-option v-for="item in roleOptions" :key="item.id" :label="item.roleName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #actions>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openDialog()">新增用户</el-button>
      </template>
    </query-bar>

    <div class="app-card table-wrapper">
      <common-table v-loading="loading" :data="tableData">
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="username" label="用户名" min-width="140" />
        <el-table-column prop="realName" label="姓名" min-width="120" />
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column label="角色" min-width="180">
          <template #default="{ row }">
            <el-space wrap>
              <el-tag v-for="item in row.roles" :key="item.id" type="primary" effect="light">{{ item.roleName }}</el-tag>
            </el-space>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <status-tag :value="row.status" :map="{ '1': '启用', '0': '禁用' }" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <div class="actions">
              <el-button link type="primary" @click="showDetail(row)">详情</el-button>
              <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
              <el-button link type="warning" @click="handleResetPassword(row)">重置密码</el-button>
              <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </common-table>
      <common-pagination
        :total="pagination.total"
        :page-num="pagination.pageNum"
        :page-size="pagination.pageSize"
        @change="handlePageChange"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="640px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
        <div class="two-column">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item v-if="!form.id" label="初始密码" prop="password">
            <el-input v-model="form.password" show-password placeholder="默认建议 123456" />
          </el-form-item>
          <el-form-item label="姓名" prop="realName">
            <el-input v-model="form.realName" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="form.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio :label="1">启用</el-radio>
              <el-radio :label="0">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="form.roleIds" multiple placeholder="请选择角色" class="w-full">
            <el-option v-for="item in roleOptions" :key="item.id" :label="item.roleName" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" title="用户详情" size="420px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户名">{{ currentRow?.username }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ currentRow?.realName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentRow?.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentRow?.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          {{ currentRow?.roles?.map((item) => item.roleName).join('、') || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(currentRow?.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </page-container>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'

import { getRoleListApi } from '@/api/modules/role'
import { deleteUserApi, getUserPageApi, resetPasswordApi, saveUserApi } from '@/api/modules/user'
import PageContainer from '@/components/common/PageContainer.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import QueryBar from '@/components/form/QueryBar.vue'
import CommonPagination from '@/components/table/CommonPagination.vue'
import CommonTable from '@/components/table/CommonTable.vue'
import { usePagination } from '@/hooks/usePagination'
import { formatDateTime } from '@/utils/format'
import type { RoleItem, UserRecord } from '@/types'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const formRef = ref<FormInstance>()
const tableData = ref<UserRecord[]>([])
const roleOptions = ref<RoleItem[]>([])
const currentRow = ref<UserRecord>()

const { pagination, updatePagination } = usePagination()

const queryForm = reactive({
  keyword: '',
  roleId: undefined as number | undefined,
  status: '' as number | ''
})

const initialForm = () => ({
  id: undefined as number | undefined,
  username: '',
  password: '123456',
  realName: '',
  phone: '',
  email: '',
  classId: undefined as number | undefined,
  status: 1,
  roleIds: [] as number[]
})

const form = reactive(initialForm())

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入初始密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  roleIds: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

async function loadRoles() {
  const result = await getRoleListApi()
  roleOptions.value = result.data
}

async function loadData() {
  loading.value = true
  try {
    const result = await getUserPageApi({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...queryForm
    })
    tableData.value = result.data.records
    updatePagination(result.data.total, result.data.pageNum, result.data.pageSize)
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryForm.keyword = ''
  queryForm.roleId = undefined
  queryForm.status = ''
  pagination.pageNum = 1
  loadData()
}

function openDialog(row?: UserRecord) {
  Object.assign(
    form,
    initialForm(),
    row
      ? {
          id: row.id,
          username: row.username,
          realName: row.realName,
          phone: row.phone,
          email: row.email,
          classId: row.classId,
          status: row.status,
          roleIds: row.roles?.map((item) => item.id) || []
        }
      : {}
  )
  dialogVisible.value = true
}

function showDetail(row: UserRecord) {
  currentRow.value = row
  detailVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }
  submitLoading.value = true
  try {
    await saveUserApi(form)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row: UserRecord) {
  await ElMessageBox.confirm(`确认删除用户“${row.realName}”吗？`, '删除确认', { type: 'warning' })
  await deleteUserApi(row.id)
  ElMessage.success('删除成功')
  loadData()
}

async function handleResetPassword(row: UserRecord) {
  await ElMessageBox.confirm(`确认将用户“${row.realName}”的密码重置为 123456 吗？`, '重置密码', { type: 'warning' })
  await resetPasswordApi(row.id, { newPassword: '123456' })
  ElMessage.success('密码已重置为 123456')
}

function handlePageChange(pageNum: number, pageSize: number) {
  pagination.pageNum = pageNum
  pagination.pageSize = pageSize
  loadData()
}

onMounted(async () => {
  await loadRoles()
  await loadData()
})
</script>

<style scoped lang="scss">
.table-wrapper {
  padding: 16px;
}
</style>
