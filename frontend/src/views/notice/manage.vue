<template>
  <page-container title="公告管理" description="管理员和教师可新建、编辑、删除公告。">
    <div class="actions">
      <el-button type="success" @click="openDialog()">新增公告</el-button>
    </div>

    <div class="app-card table-wrapper">
      <common-table :data="noticeList">
        <el-table-column prop="title" label="公告标题" min-width="220" />
        <el-table-column prop="noticeStatus" label="状态" width="120">
          <template #default="{ row }">
            <status-tag :value="row.noticeStatus" :map="{ DRAFT: '草稿', PUBLISHED: '已发布' }" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <div class="actions">
              <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
              <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </common-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑公告' : '新增公告'" width="720px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="公告状态" prop="noticeStatus">
          <el-radio-group v-model="form.noticeStatus">
            <el-radio label="DRAFT">草稿</el-radio>
            <el-radio label="PUBLISHED">发布</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" />
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

import { deleteNoticeApi, getNoticePageApi, saveNoticeApi } from '@/api/modules/notice'
import PageContainer from '@/components/common/PageContainer.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import CommonTable from '@/components/table/CommonTable.vue'
import { formatDateTime } from '@/utils/format'
import type { NoticeRecord } from '@/types'

const noticeList = ref<NoticeRecord[]>([])
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const form = reactive({
  id: undefined as number | undefined,
  title: '',
  content: '',
  noticeStatus: 'DRAFT'
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
  noticeStatus: [{ required: true, message: '请选择公告状态', trigger: 'change' }]
}

async function loadData() {
  const result = await getNoticePageApi({ pageNum: 1, pageSize: 50 })
  noticeList.value = result.data.records
}

function openDialog(row?: NoticeRecord) {
  form.id = row?.id
  form.title = row?.title || ''
  form.content = row?.content || ''
  form.noticeStatus = row?.noticeStatus || 'DRAFT'
  dialogVisible.value = true
}

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }
  await saveNoticeApi(form)
  ElMessage.success('公告保存成功')
  dialogVisible.value = false
  loadData()
}

async function handleDelete(row: NoticeRecord) {
  await ElMessageBox.confirm(`确认删除公告“${row.title}”吗？`, '删除确认', { type: 'warning' })
  await deleteNoticeApi(Number(row.id))
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
