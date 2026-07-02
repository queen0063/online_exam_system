<template>
  <page-container title="科目管理" description="维护题库、试卷和考试可选择的科目，科目编码由系统自动生成。">
    <div class="actions">
      <el-button type="success" @click="openDialog()">新增科目</el-button>
    </div>

    <div class="app-card table-wrapper">
      <common-table :data="subjectList">
        <el-table-column prop="subjectCode" label="科目编码" width="160" />
        <el-table-column prop="subjectName" label="科目名称" min-width="180" />
        <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑科目' : '新增科目'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
        <el-form-item v-if="form.id" label="科目编码">
          <el-input v-model="form.subjectCode" disabled />
        </el-form-item>
        <el-form-item label="科目名称" prop="subjectName">
          <el-input v-model="form.subjectName" placeholder="如 Java程序设计" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="4" />
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

import { deleteSubjectApi, getSubjectListApi, saveSubjectApi } from '@/api/modules/subject'
import PageContainer from '@/components/common/PageContainer.vue'
import CommonTable from '@/components/table/CommonTable.vue'
import { formatDateTime } from '@/utils/format'
import type { SubjectRecord } from '@/types'

const subjectList = ref<SubjectRecord[]>([])
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<SubjectRecord>({
  id: undefined,
  subjectCode: '',
  subjectName: '',
  description: '',
  status: 1
})

const rules: FormRules = {
  subjectName: [{ required: true, message: '请输入科目名称', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

async function loadData() {
  const result = await getSubjectListApi()
  subjectList.value = result.data
}

function openDialog(row?: SubjectRecord) {
  form.id = row?.id
  form.subjectCode = row?.subjectCode || ''
  form.subjectName = row?.subjectName || ''
  form.description = row?.description || ''
  form.status = row?.status ?? 1
  dialogVisible.value = true
}

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }
  await saveSubjectApi(form)
  ElMessage.success('科目保存成功')
  dialogVisible.value = false
  loadData()
}

async function handleDelete(row: SubjectRecord) {
  await ElMessageBox.confirm(`确认删除科目“${row.subjectName}”吗？`, '删除确认', { type: 'warning' })
  await deleteSubjectApi(Number(row.id))
  ElMessage.success('删除成功')
  loadData()
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
