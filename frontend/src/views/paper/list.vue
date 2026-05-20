<template>
  <page-container title="试卷管理" description="支持手动组卷、随机组卷、试卷预览与题目顺序管理。">
    <query-bar>
      <el-form :model="queryForm" inline>
        <el-form-item label="试卷名称">
          <el-input v-model="queryForm.paperName" placeholder="请输入试卷名称" clearable />
        </el-form-item>
        <el-form-item label="科目">
          <el-select v-model="queryForm.subjectId" clearable placeholder="全部科目" style="width: 160px">
            <el-option v-for="item in subjectOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #actions>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openDialog()">新增试卷</el-button>
      </template>
    </query-bar>

    <div class="app-card table-wrapper">
      <common-table v-loading="loading" :data="tableData">
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="paperName" label="试卷名称" min-width="220" />
        <el-table-column label="组卷方式" width="120">
          <template #default="{ row }">
            {{ row.generateType === 'MANUAL' ? '手动组卷' : '随机组卷' }}
          </template>
        </el-table-column>
        <el-table-column label="总分" width="90" prop="totalScore" />
        <el-table-column label="时长(分钟)" width="110" prop="durationMinutes" />
        <el-table-column label="题目数" width="90">
          <template #default="{ row }">
            {{ row.questions?.length || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <div class="actions">
              <el-button link type="primary" @click="goPreview(row)">预览</el-button>
              <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑试卷' : '新增试卷'" width="980px" top="3vh">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <div class="two-column">
          <el-form-item label="试卷名称" prop="paperName">
            <el-input v-model="form.paperName" placeholder="请输入试卷名称" />
          </el-form-item>
          <el-form-item label="所属科目" prop="subjectId">
            <el-select v-model="form.subjectId" class="w-full">
              <el-option v-for="item in subjectOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="组卷方式" prop="generateType">
            <el-radio-group v-model="form.generateType">
              <el-radio label="MANUAL">手动组卷</el-radio>
              <el-radio label="RANDOM">随机组卷</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="考试时长" prop="durationMinutes">
            <el-input-number v-model="form.durationMinutes" :min="10" :max="180" class="w-full" />
          </el-form-item>
        </div>
        <el-form-item label="试卷说明">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入试卷说明" />
        </el-form-item>

        <template v-if="form.generateType === 'MANUAL'">
          <el-form-item label="选择题目">
            <div class="question-select">
              <div class="question-select-toolbar">
                <span class="question-select-label">题目科目</span>
                <el-select
                  v-model="manualQuestionQuery.subjectId"
                  clearable
                  placeholder="全部科目"
                  style="width: 180px"
                  @change="loadQuestionOptions"
                >
                  <el-option v-for="item in subjectOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </div>
              <common-table :data="questionOptions">
                <el-table-column width="60">
                  <template #default="{ row }">
                    <el-checkbox :model-value="selectedQuestionIds.includes(Number(row.id))" @change="toggleQuestion(row)" />
                  </template>
                </el-table-column>
                <el-table-column prop="title" label="题干" min-width="260" show-overflow-tooltip />
                <el-table-column prop="questionType" label="题型" width="120">
                  <template #default="{ row }">
                    {{ questionTypeLabel(row.questionType) }}
                  </template>
                </el-table-column>
                <el-table-column prop="score" label="原始分值" width="90" />
                <el-table-column label="试卷分值" width="150">
                  <template #default="{ row }">
                    <el-input-number
                      v-if="selectedQuestionIds.includes(Number(row.id))"
                      v-model="questionScoreMap[Number(row.id)]"
                      :min="1"
                      :max="100"
                    />
                    <span v-else class="text-secondary">未加入</span>
                  </template>
                </el-table-column>
              </common-table>
            </div>
          </el-form-item>
        </template>

        <template v-else>
          <el-form-item label="随机规则">
            <div class="random-rule-panel">
              <el-alert
                type="info"
                :closable="false"
                title="按题型、难度、知识点和数量生成试卷，保存时将直接调用后端随机抽题接口。"
              />
              <div class="random-rule-toolbar">
                <el-button type="primary" link @click="addRandomRule">新增规则</el-button>
              </div>
              <common-table :data="form.randomRules || []">
                <el-table-column label="科目" min-width="140">
                  <template #default="{ row }">
                    <el-select v-model="row.subjectId" class="w-full">
                      <el-option v-for="item in subjectOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="题型" min-width="140">
                  <template #default="{ row }">
                    <el-select v-model="row.questionType" class="w-full">
                      <el-option v-for="item in QUESTION_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="难度" min-width="120">
                  <template #default="{ row }">
                    <el-select v-model="row.difficulty" class="w-full">
                      <el-option v-for="item in DIFFICULTY_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="知识点" min-width="180">
                  <template #default="{ row }">
                    <el-input v-model="row.knowledgePoint" placeholder="可选，不填则按条件全量抽题" />
                  </template>
                </el-table-column>
                <el-table-column label="题目数量" width="120">
                  <template #default="{ row }">
                    <el-input-number v-model="row.questionCount" :min="1" :max="50" class="w-full" />
                  </template>
                </el-table-column>
                <el-table-column label="单题分值" width="120">
                  <template #default="{ row }">
                    <el-input-number v-model="row.questionScore" :min="1" :max="100" class="w-full" />
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100" fixed="right">
                  <template #default="{ $index }">
                    <el-button link type="danger" @click="removeRandomRule($index)">删除</el-button>
                  </template>
                </el-table-column>
              </common-table>
            </div>
          </el-form-item>
        </template>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存试卷</el-button>
      </template>
    </el-dialog>
  </page-container>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'

import { getPaperPageApi, savePaperApi, deletePaperApi } from '@/api/modules/paper'
import { getQuestionPageApi } from '@/api/modules/question'
import PageContainer from '@/components/common/PageContainer.vue'
import QueryBar from '@/components/form/QueryBar.vue'
import CommonPagination from '@/components/table/CommonPagination.vue'
import CommonTable from '@/components/table/CommonTable.vue'
import { usePagination } from '@/hooks/usePagination'
import { useSubjects } from '@/hooks/useSubjects'
import { DIFFICULTY_OPTIONS, QUESTION_TYPE_OPTIONS } from '@/utils/dicts'
import { formatDateTime } from '@/utils/format'
import type { PaperRecord, QuestionRecord, RandomRuleRecord } from '@/types'

const router = useRouter()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const tableData = ref<PaperRecord[]>([])
const questionOptions = ref<QuestionRecord[]>([])
const selectedQuestionIds = ref<number[]>([])
const questionScoreMap = reactive<Record<number, number>>({})
const { pagination, updatePagination } = usePagination()
const { subjectOptions, loadSubjects } = useSubjects()

const queryForm = reactive({
  paperName: '',
  subjectId: '' as number | ''
})

const manualQuestionQuery = reactive({
  subjectId: '' as number | ''
})

function createRandomRule(subjectId = 1): RandomRuleRecord {
  return {
    subjectId,
    questionType: 'SINGLE_CHOICE',
    difficulty: 'EASY',
    knowledgePoint: '',
    questionCount: 5,
    questionScore: 2
  }
}

const initialForm = () => ({
  id: undefined as number | undefined,
  paperName: '',
  subjectId: 1,
  generateType: 'MANUAL',
  durationMinutes: 60,
  description: '',
  questions: [] as PaperRecord['questions'],
  randomRules: [createRandomRule()] as RandomRuleRecord[]
})

const form = reactive(initialForm())

const rules: FormRules = {
  paperName: [{ required: true, message: '请输入试卷名称', trigger: 'blur' }],
  subjectId: [{ required: true, message: '请选择科目', trigger: 'change' }],
  generateType: [{ required: true, message: '请选择组卷方式', trigger: 'change' }],
  durationMinutes: [{ required: true, message: '请输入时长', trigger: 'change' }]
}

watch(
  () => form.subjectId,
  (subjectId) => {
    if (!dialogVisible.value || form.generateType !== 'MANUAL') {
      return
    }
    manualQuestionQuery.subjectId = Number(subjectId) || ''
    loadQuestionOptions()
  }
)

watch(
  () => form.generateType,
  (generateType) => {
    if (!dialogVisible.value || generateType !== 'MANUAL') {
      return
    }
    manualQuestionQuery.subjectId = Number(form.subjectId) || ''
    loadQuestionOptions()
  }
)

function questionTypeLabel(value: string) {
  return QUESTION_TYPE_OPTIONS.find((item) => item.value === value)?.label || value
}

async function loadQuestionOptions() {
  const result = await getQuestionPageApi({
    pageNum: 1,
    pageSize: 50,
    subjectId: manualQuestionQuery.subjectId
  })
  questionOptions.value = result.data.records
}

async function loadData() {
  loading.value = true
  try {
    const result = await getPaperPageApi({
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
  queryForm.paperName = ''
  queryForm.subjectId = ''
  pagination.pageNum = 1
  loadData()
}

function openDialog(row?: PaperRecord) {
  Object.assign(form, initialForm(), row || {})
  manualQuestionQuery.subjectId = Number(form.subjectId) || ''
  if (form.generateType === 'RANDOM') {
    form.randomRules = row?.randomRules?.length
      ? row.randomRules.map((item) => ({ ...item }))
      : [createRandomRule(Number(form.subjectId) || 1)]
  }
  selectedQuestionIds.value = (row?.questions || []).map((item) => Number(item.questionId))
  Object.keys(questionScoreMap).forEach((key) => delete questionScoreMap[Number(key)])
  ;(row?.questions || []).forEach((item) => {
    questionScoreMap[Number(item.questionId)] = item.questionScore
  })
  dialogVisible.value = true
  loadQuestionOptions()
}

function addRandomRule() {
  form.randomRules = [...(form.randomRules || []), createRandomRule(Number(form.subjectId) || 1)]
}

function removeRandomRule(index: number) {
  if ((form.randomRules || []).length === 1) {
    ElMessage.warning('至少保留一条随机组卷规则')
    return
  }
  form.randomRules.splice(index, 1)
}

function toggleQuestion(question: QuestionRecord) {
  const id = Number(question.id)
  if (selectedQuestionIds.value.includes(id)) {
    selectedQuestionIds.value = selectedQuestionIds.value.filter((item) => item !== id)
    delete questionScoreMap[id]
    return
  }
  selectedQuestionIds.value.push(id)
  questionScoreMap[id] = question.score
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  if (form.generateType === 'MANUAL' && selectedQuestionIds.value.length === 0) {
    ElMessage.warning('请至少选择一道题目')
    return
  }
  if (form.generateType === 'RANDOM' && !(form.randomRules || []).length) {
    ElMessage.warning('请至少配置一条随机组卷规则')
    return
  }

  const questions = selectedQuestionIds.value.map((questionId, index) => ({
    questionId,
    questionScore: questionScoreMap[questionId] || 1,
    sortNo: index + 1
  }))
  const randomRules = (form.randomRules || []).map((item) => ({
    ...item,
    subjectId: Number(form.subjectId) || item.subjectId
  }))

  submitLoading.value = true
  try {
    await savePaperApi({
      ...form,
      questions,
      randomRules: form.generateType === 'RANDOM' ? randomRules : []
    })
    ElMessage.success('试卷保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row: PaperRecord) {
  await ElMessageBox.confirm(`确认删除试卷“${row.paperName}”吗？`, '删除确认', { type: 'warning' })
  await deletePaperApi(Number(row.id))
  ElMessage.success('删除成功')
  loadData()
}

function goPreview(row: PaperRecord) {
  router.push(`/paper/preview/${row.id}`)
}

function handlePageChange(pageNum: number, pageSize: number) {
  pagination.pageNum = pageNum
  pagination.pageSize = pageSize
  loadData()
}

onMounted(async () => {
  await Promise.all([loadSubjects(), loadData(), loadQuestionOptions()])
})
</script>

<style scoped lang="scss">
.table-wrapper {
  padding: 16px;
}

.question-select {
  display: grid;
  gap: 12px;
  width: 100%;
}

.question-select-toolbar {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: flex-end;
}

.question-select-label {
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.random-rule-panel {
  display: grid;
  gap: 12px;
  width: 100%;
}

.random-rule-toolbar {
  display: flex;
  justify-content: flex-end;
}
</style>
