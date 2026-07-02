<template>
  <page-container title="考试管理" description="保存考试草稿、发布考试、发布成绩，并维护参考对象和考试时间。">
    <query-bar>
      <el-form :model="queryForm" inline>
        <el-form-item label="考试名称">
          <el-input v-model="queryForm.examName" placeholder="请输入考试名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" clearable placeholder="全部状态" style="width: 160px">
            <el-option v-for="item in EXAM_STATUS_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #actions>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openDialog()">新增考试</el-button>
      </template>
    </query-bar>

    <div class="app-card table-wrapper">
      <common-table v-loading="loading" :data="tableData">
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="examName" label="考试名称" min-width="220" />
        <el-table-column label="开始时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
        </el-table-column>
        <el-table-column label="结束时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.endTime) }}</template>
        </el-table-column>
        <el-table-column prop="durationMinutes" label="时长" width="90" />
        <el-table-column prop="passScore" label="及格线" width="90" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <status-tag :value="row.status" :map="statusMap" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="380" fixed="right">
          <template #default="{ row }">
            <div class="actions">
              <el-button link type="primary" @click="showDetail(row)">详情</el-button>
              <el-button link type="primary" @click="openMonitor(row)">监测</el-button>
              <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
              <el-button link type="success" @click="handlePublish(row)">发布</el-button>
              <el-button link type="warning" @click="handlePublishScore(row)">发布成绩</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑考试' : '新增考试'" width="720px" @closed="resetFormState">
      <el-form ref="formRef" v-loading="dialogLoading" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="考试名称" prop="examName">
          <el-input v-model="form.examName" placeholder="请输入考试名称" />
        </el-form-item>
        <div class="two-column">
          <el-form-item label="试卷" prop="paperId">
            <el-select v-model="form.paperId" class="w-full">
              <el-option v-for="item in paperOptions" :key="item.id" :label="item.paperName" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="科目" prop="subjectId">
            <el-select v-model="form.subjectId" class="w-full">
              <el-option v-for="item in subjectOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="考试时长" prop="durationMinutes">
            <el-input-number v-model="form.durationMinutes" :min="10" :max="240" class="w-full" />
          </el-form-item>
          <el-form-item label="及格线" prop="passScore">
            <el-input-number v-model="form.passScore" :min="1" :max="100" class="w-full" />
          </el-form-item>
        </div>
        <div class="two-column">
          <el-form-item label="开始时间" prop="startTime">
            <el-date-picker v-model="form.startTime" type="datetime" class="w-full" value-format="YYYY-MM-DD HH:mm:ss" />
          </el-form-item>
          <el-form-item label="结束时间" prop="endTime">
            <el-date-picker v-model="form.endTime" type="datetime" class="w-full" value-format="YYYY-MM-DD HH:mm:ss" />
          </el-form-item>
        </div>
        <el-form-item label="防切屏">
          <div class="anti-switch-setting">
            <el-switch v-model="form.antiSwitchEnabled" active-text="启用" inactive-text="不启用" />
            <el-input-number
              v-if="form.antiSwitchEnabled"
              v-model="form.maxSwitchCount"
              :min="0"
              :max="99"
              controls-position="right"
            />
          </div>
        </el-form-item>
        <el-form-item label="学生班级">
          <el-select v-model="studentQuery.classId" clearable class="w-full" placeholder="全部可选班级" @change="handleStudentClassChange">
            <el-option v-for="item in classOptions" :key="item.id" :label="classLabel(item)" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="参与学生" prop="studentIds">
          <el-select v-model="form.studentIds" multiple class="w-full" placeholder="请选择学生">
            <el-option v-for="item in studentOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" title="考试详情" size="460px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="考试名称">{{ currentRow?.examName }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ formatDateTime(currentRow?.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ formatDateTime(currentRow?.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="考试时长">{{ currentRow?.durationMinutes }} 分钟</el-descriptions-item>
        <el-descriptions-item label="及格线">{{ currentRow?.passScore }} 分</el-descriptions-item>
        <el-descriptions-item label="防切屏">
          {{ currentRow?.maxSwitchCount === undefined || currentRow?.maxSwitchCount === null ? '不启用' : `超过 ${currentRow.maxSwitchCount} 次自动交卷` }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusMap[currentRow?.status || ''] || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </page-container>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'

import { getClassListApi } from '@/api/modules/classInfo'
import { deleteExamApi, getExamDetailApi, getExamPageApi, publishExamApi, publishExamScoreApi, saveExamApi } from '@/api/modules/exam'
import { getPaperPageApi } from '@/api/modules/paper'
import { getStudentListApi } from '@/api/modules/user'
import PageContainer from '@/components/common/PageContainer.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import QueryBar from '@/components/form/QueryBar.vue'
import CommonPagination from '@/components/table/CommonPagination.vue'
import CommonTable from '@/components/table/CommonTable.vue'
import { usePagination } from '@/hooks/usePagination'
import { useSubjects } from '@/hooks/useSubjects'
import { EXAM_STATUS_OPTIONS } from '@/utils/dicts'
import { formatDateTime } from '@/utils/format'
import type { ClassInfoRecord, ExamRecord, PaperRecord, UserRecord } from '@/types'

const statusMap = EXAM_STATUS_OPTIONS.reduce<Record<string, string>>((map, item) => {
  map[item.value] = item.label
  return map
}, {})

const loading = ref(false)
const router = useRouter()
const dialogLoading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const formRef = ref<FormInstance>()
const tableData = ref<ExamRecord[]>([])
const paperOptions = ref<PaperRecord[]>([])
const classOptions = ref<ClassInfoRecord[]>([])
const studentOptions = ref<Array<{ label: string; value: number }>>([])
const currentRow = ref<ExamRecord>()
const { pagination, updatePagination } = usePagination()
const { subjectOptions, loadSubjects } = useSubjects()

const queryForm = reactive({
  examName: '',
  status: ''
})

const studentQuery = reactive({
  classId: '' as number | ''
})

const initialForm = () => ({
  id: undefined as number | undefined,
  examName: '',
  paperId: undefined as number | undefined,
  subjectId: 1,
  startTime: '',
  endTime: '',
  durationMinutes: 60,
  passScore: 60,
  antiSwitchEnabled: false,
  maxSwitchCount: 3 as number | undefined,
  status: 'DRAFT',
  studentIds: [] as number[]
})

const form = reactive(initialForm())

const rules: FormRules = {
  examName: [{ required: true, message: '请输入考试名称', trigger: 'blur' }],
  paperId: [{ required: true, message: '请选择试卷', trigger: 'change' }],
  subjectId: [{ required: true, message: '请选择科目', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  studentIds: [{ required: true, message: '请选择参与学生', trigger: 'change' }]
}

async function loadPaperOptions() {
  const result = await getPaperPageApi({ pageNum: 1, pageSize: 50 })
  paperOptions.value = result.data.records
}

async function loadClassOptions() {
  const result = await getClassListApi()
  classOptions.value = result.data
}

function classLabel(item: ClassInfoRecord) {
  return item.gradeName ? `${item.gradeName} ${item.className}` : item.className
}

function studentLabel(item: UserRecord) {
  const studentNo = item.studentNo ? `${item.studentNo} / ` : ''
  return `${studentNo}${item.realName}`
}

async function loadStudentOptions(preserveSelected = true) {
  const result = await getStudentListApi({ classId: studentQuery.classId })
  studentOptions.value = result.data.map((item) => ({
    label: studentLabel(item),
    value: item.id
  }))
  if (preserveSelected) {
    form.studentIds = form.studentIds.filter((studentId) => studentOptions.value.some((item) => item.value === studentId))
  }
}

async function handleStudentClassChange() {
  await loadStudentOptions(false)
  form.studentIds = studentOptions.value.map((item) => item.value)
}

async function loadData() {
  loading.value = true
  try {
    const result = await getExamPageApi({
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
  queryForm.examName = ''
  queryForm.status = ''
  pagination.pageNum = 1
  loadData()
}

function resetFormState() {
  Object.assign(form, initialForm())
  studentQuery.classId = ''
}

async function openDialog(row?: ExamRecord) {
  studentQuery.classId = ''
  await loadStudentOptions()
  if (!row?.id) {
    resetFormState()
    dialogVisible.value = true
    return
  }

  dialogVisible.value = true
  dialogLoading.value = true
  try {
    const result = await getExamDetailApi(Number(row.id))
    Object.assign(form, initialForm(), result.data, {
      startTime: formatDateTime(result.data.startTime),
      endTime: formatDateTime(result.data.endTime),
      antiSwitchEnabled: result.data.maxSwitchCount !== undefined && result.data.maxSwitchCount !== null,
      maxSwitchCount: result.data.maxSwitchCount ?? 3,
      studentIds: result.data.studentIds || []
    })
  } finally {
    dialogLoading.value = false
  }
}

function showDetail(row: ExamRecord) {
  currentRow.value = row
  detailVisible.value = true
}

function openMonitor(row: ExamRecord) {
  router.push(`/exam/monitor/${row.id}`)
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }
  submitLoading.value = true
  try {
    await saveExamApi({
      id: form.id,
      examName: form.examName,
      paperId: Number(form.paperId),
      subjectId: Number(form.subjectId),
      startTime: form.startTime,
      endTime: form.endTime,
      durationMinutes: Number(form.durationMinutes),
      passScore: Number(form.passScore),
      maxSwitchCount: form.antiSwitchEnabled ? Number(form.maxSwitchCount ?? 0) : undefined,
      status: form.status,
      studentIds: form.studentIds
    })
    ElMessage.success('考试保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

async function handlePublish(row: ExamRecord) {
  await publishExamApi(Number(row.id))
  ElMessage.success('考试已发布')
  loadData()
}

async function handlePublishScore(row: ExamRecord) {
  await publishExamScoreApi(Number(row.id))
  ElMessage.success('成绩已发布')
  loadData()
}

async function handleDelete(row: ExamRecord) {
  await ElMessageBox.confirm(`确认删除考试“${row.examName}”吗？`, '删除确认', { type: 'warning' })
  await deleteExamApi(Number(row.id))
  ElMessage.success('删除成功')
  loadData()
}

function handlePageChange(pageNum: number, pageSize: number) {
  pagination.pageNum = pageNum
  pagination.pageSize = pageSize
  loadData()
}

onMounted(async () => {
  await Promise.all([loadSubjects(), loadClassOptions(), loadPaperOptions(), loadData()])
  await loadStudentOptions()
})
</script>

<style scoped lang="scss">
.table-wrapper {
  padding: 16px;
}

.anti-switch-setting {
  display: flex;
  align-items: center;
  gap: 16px;
}
</style>
