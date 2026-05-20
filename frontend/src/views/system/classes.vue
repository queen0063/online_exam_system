<template>
  <page-container title="班级管理" description="维护学生注册、用户分班和考试分配使用的班级。">
    <div class="actions">
      <el-button type="success" @click="openDialog()">新增班级</el-button>
    </div>

    <div class="app-card table-wrapper">
      <common-table :data="classList">
        <el-table-column prop="classCode" label="班级编码" width="150" />
        <el-table-column prop="className" label="班级名称" min-width="160" />
        <el-table-column prop="gradeName" label="年级" min-width="140">
          <template #default="{ row }">{{ row.gradeName || '-' }}</template>
        </el-table-column>
        <el-table-column label="负责教师" min-width="150">
          <template #default="{ row }">{{ teacherName(row.teacherId) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <div class="actions">
              <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
              <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </common-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑班级' : '新增班级'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
        <el-form-item label="班级编码" prop="classCode">
          <el-input v-model="form.classCode" placeholder="如 CS-2026-01" />
        </el-form-item>
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="form.className" placeholder="如 计算机一班" />
        </el-form-item>
        <el-form-item label="年级">
          <el-input v-model="form.gradeName" placeholder="如 2026级" />
        </el-form-item>
        <el-form-item label="负责教师">
          <el-select v-model="form.teacherId" clearable filterable class="w-full" placeholder="请选择负责教师">
            <el-option v-for="item in teacherOptions" :key="item.id" :label="item.realName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </page-container>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'

import { deleteClassApi, getClassManageListApi, saveClassApi } from '@/api/modules/classInfo'
import { getUserPageApi } from '@/api/modules/user'
import PageContainer from '@/components/common/PageContainer.vue'
import CommonTable from '@/components/table/CommonTable.vue'
import { formatDateTime } from '@/utils/format'
import type { ClassInfoRecord, UserRecord } from '@/types'

type ClassForm = Omit<ClassInfoRecord, 'teacherId'> & {
  teacherId?: number | ''
}

const classList = ref<ClassInfoRecord[]>([])
const teacherOptions = ref<UserRecord[]>([])
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<ClassForm>({
  id: undefined,
  classCode: '',
  className: '',
  gradeName: '',
  teacherId: '',
  status: 1
})

const rules: FormRules = {
  classCode: [{ required: true, message: '请输入班级编码', trigger: 'blur' }],
  className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

async function loadData() {
  const result = await getClassManageListApi()
  classList.value = result.data
}

async function loadTeachers() {
  const result = await getUserPageApi({ pageNum: 1, pageSize: 1000, status: 1 })
  teacherOptions.value = result.data.records.filter((item) =>
    item.roles?.some((role) => role.roleCode === 'TEACHER')
  )
}

function teacherName(teacherId?: number) {
  if (!teacherId) {
    return '-'
  }
  return teacherOptions.value.find((item) => item.id === teacherId)?.realName || '-'
}

function openDialog(row?: ClassInfoRecord) {
  form.id = row?.id
  form.classCode = row?.classCode || ''
  form.className = row?.className || ''
  form.gradeName = row?.gradeName || ''
  form.teacherId = row?.teacherId || ''
  form.status = row?.status ?? 1
  dialogVisible.value = true
}

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }
  await saveClassApi({
    ...form,
    teacherId: form.teacherId || undefined
  })
  ElMessage.success('班级保存成功')
  dialogVisible.value = false
  loadData()
}

async function handleDelete(row: ClassInfoRecord) {
  await ElMessageBox.confirm(`确认删除班级“${row.className}”吗？`, '删除确认', { type: 'warning' })
  await deleteClassApi(Number(row.id))
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  Promise.all([loadData(), loadTeachers()])
})
</script>

<style scoped lang="scss">
.table-wrapper {
  padding: 16px;
}
</style>
