<template>
  <page-container title="角色管理" description="管理员可维护角色名称与启用状态，角色编码由系统自动生成。">
    <query-bar>
      <el-form :model="queryForm" inline>
        <el-form-item label="角色名称">
          <el-input v-model="queryForm.keyword" placeholder="请输入角色名称或编码" clearable />
        </el-form-item>
      </el-form>
      <template #actions>
        <el-button @click="queryForm.keyword = ''">重置</el-button>
        <el-button type="primary" @click="applyFilter">查询</el-button>
        <el-button type="success" @click="openDialog()">新增角色</el-button>
      </template>
    </query-bar>

    <div class="app-card table-wrapper">
      <common-table :data="filteredRoles">
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="roleCode" label="角色编码" min-width="160" />
        <el-table-column prop="roleName" label="角色名称" min-width="160" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <status-tag :value="row.status" :map="{ '1': '启用', '0': '禁用' }" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <div class="actions">
              <el-button link type="primary" @click="showDetail(row)">详情</el-button>
              <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
              <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </common-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑角色' : '新增角色'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item v-if="form.id" label="角色编码">
          <el-input v-model="form.roleCode" disabled />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入中文角色名" />
        </el-form-item>
        <el-form-item label="角色状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" title="角色详情" size="380px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="角色编码">{{ currentRole?.roleCode }}</el-descriptions-item>
        <el-descriptions-item label="角色名称">{{ currentRole?.roleName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          {{ currentRole?.status === 1 ? '启用' : '禁用' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </page-container>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'

import { deleteRoleApi, getRoleListApi, saveRoleApi } from '@/api/modules/role'
import PageContainer from '@/components/common/PageContainer.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import QueryBar from '@/components/form/QueryBar.vue'
import CommonTable from '@/components/table/CommonTable.vue'
import type { RoleItem } from '@/types'

const queryForm = reactive({
  keyword: ''
})

const roles = ref<RoleItem[]>([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const formRef = ref<FormInstance>()
const currentRole = ref<RoleItem>()
const form = reactive({
  id: undefined as number | undefined,
  roleCode: '',
  roleName: '',
  status: 1
})

const rules: FormRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
}

const filteredRoles = computed(() =>
  roles.value.filter(
    (item) =>
      !queryForm.keyword ||
      item.roleCode.toLowerCase().includes(queryForm.keyword.toLowerCase()) ||
      item.roleName.includes(queryForm.keyword)
  )
)

async function loadData() {
  const result = await getRoleListApi()
  roles.value = result.data
}

function applyFilter() {
  roles.value = [...roles.value]
}

function openDialog(row?: RoleItem) {
  form.id = row?.id
  form.roleCode = row?.roleCode || ''
  form.roleName = row?.roleName || ''
  form.status = row?.status ?? 1
  dialogVisible.value = true
}

function showDetail(row: RoleItem) {
  currentRole.value = row
  detailVisible.value = true
}

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }
  await saveRoleApi(form)
  ElMessage.success('角色保存成功')
  dialogVisible.value = false
  await loadData()
}

async function handleDelete(row: RoleItem) {
  await ElMessageBox.confirm(`确认删除角色“${row.roleName}”吗？`, '删除确认', { type: 'warning' })
  await deleteRoleApi(row.id)
  ElMessage.success('角色已删除')
  await loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.table-wrapper {
  padding: 16px;
}
</style>
