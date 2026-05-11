<template>
  <page-container title="题库管理" description="支持单选、多选、判断、填空、简答五类题型维护。">
    <query-bar>
      <el-form :model="queryForm" inline>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="题干 / 知识点" clearable />
        </el-form-item>
        <el-form-item label="科目">
          <el-select v-model="queryForm.subjectId" clearable placeholder="全部科目" style="width: 160px">
            <el-option v-for="item in subjectOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="题型">
          <el-select v-model="queryForm.questionType" clearable placeholder="全部题型" style="width: 160px">
            <el-option v-for="item in QUESTION_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="queryForm.difficulty" clearable placeholder="全部难度" style="width: 140px">
            <el-option v-for="item in DIFFICULTY_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #actions>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="openDialog()">新增题目</el-button>
      </template>
    </query-bar>

    <div class="app-card table-wrapper">
      <common-table v-loading="loading" :data="tableData">
        <el-table-column type="index" label="#" width="60" />
        <el-table-column label="题型" width="110">
          <template #default="{ row }">
            {{ questionTypeLabel(row.questionType) }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="题干" min-width="340" show-overflow-tooltip />
        <el-table-column label="图片" width="90">
          <template #default="{ row }">
            <el-image
              v-if="row.imageUrls?.length"
              class="question-thumb"
              :src="row.imageUrls[0]"
              :preview-src-list="row.imageUrls"
              fit="cover"
              preview-teleported
            />
            <span v-else class="empty-text">无</span>
          </template>
        </el-table-column>
        <el-table-column label="知识点" min-width="140" prop="knowledgePoint" />
        <el-table-column label="难度" width="100">
          <template #default="{ row }">
            {{ difficultyLabel(row.difficulty) }}
          </template>
        </el-table-column>
        <el-table-column label="分值" width="80" prop="score" />
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <div class="actions">
              <el-button link type="primary" @click="showDetail(row)">详情</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑题目' : '新增题目'" width="860px" top="4vh">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px">
        <div class="two-column">
          <el-form-item label="科目" prop="subjectId">
            <el-select v-model="form.subjectId" class="w-full">
              <el-option v-for="item in subjectOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="题型" prop="questionType">
            <el-select v-model="form.questionType" class="w-full" @change="handleQuestionTypeChange">
              <el-option v-for="item in QUESTION_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="难度" prop="difficulty">
            <el-select v-model="form.difficulty" class="w-full">
              <el-option v-for="item in DIFFICULTY_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="分值" prop="score">
            <el-input-number v-model="form.score" :min="1" :max="100" class="w-full" />
          </el-form-item>
        </div>
        <el-form-item label="知识点" prop="knowledgePoint">
          <el-input v-model="form.knowledgePoint" placeholder="请输入知识点" />
        </el-form-item>
        <el-form-item label="题干" prop="title">
          <el-input v-model="form.title" type="textarea" :rows="4" placeholder="请输入题干内容" />
        </el-form-item>
        <el-form-item label="题目图片">
          <div class="image-uploader">
            <div v-if="form.imageUrls.length" class="image-uploader__list">
              <div v-for="(url, index) in form.imageUrls" :key="`${index}-${url.slice(0, 32)}`" class="image-uploader__item">
                <el-image :src="url" fit="cover" :preview-src-list="form.imageUrls" :initial-index="index" preview-teleported />
                <el-button type="danger" plain @click="removeImage(index)">删除</el-button>
              </div>
            </div>
            <el-upload
              action="#"
              accept="image/*"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleImageUpload"
            >
              <el-button type="primary" plain :loading="imageUploading">上传图片</el-button>
            </el-upload>
          </div>
        </el-form-item>

        <template v-if="needsOptions">
          <el-form-item label="选项配置">
            <div class="option-editor">
              <div v-for="(item, index) in form.options" :key="index" class="option-editor__row">
                <el-input v-model="form.options[index]" :placeholder="`选项 ${String.fromCharCode(65 + index)}`" />
                <el-button v-if="form.options.length > 2" type="danger" plain @click="removeOption(index)">删除</el-button>
              </div>
              <el-button type="primary" plain @click="addOption">新增选项</el-button>
            </div>
          </el-form-item>
        </template>

        <el-form-item label="正确答案" prop="answers">
          <template v-if="form.questionType === 'SINGLE_CHOICE'">
            <el-radio-group v-model="singleAnswer">
              <el-radio v-for="item in form.options" :key="item" :label="item">{{ item }}</el-radio>
            </el-radio-group>
          </template>
          <template v-else-if="form.questionType === 'MULTIPLE_CHOICE'">
            <el-checkbox-group v-model="form.answers">
              <el-checkbox v-for="item in form.options" :key="item" :label="item">{{ item }}</el-checkbox>
            </el-checkbox-group>
          </template>
          <template v-else-if="form.questionType === 'TRUE_FALSE'">
            <el-radio-group v-model="singleAnswer">
              <el-radio label="true">正确</el-radio>
              <el-radio label="false">错误</el-radio>
            </el-radio-group>
          </template>
          <template v-else>
            <el-input
              :model-value="form.answers.join('；')"
              placeholder="多个答案请使用中文分号分隔"
              @input="handleAnswerInput"
            />
          </template>
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="form.analysis" type="textarea" :rows="4" placeholder="请输入题目解析" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" title="题目详情" size="560px">
      <question-renderer
        v-if="currentRow"
        :question="{
          ...currentRow,
          questionScore: currentRow.score,
          standardAnswers: currentRow.answers
        }"
        :index="0"
        :show-analysis="true"
        :model-value="[]"
        readonly
      />
    </el-drawer>
  </page-container>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, type UploadFile } from 'element-plus'

import { deleteQuestionApi, getQuestionPageApi, saveQuestionApi } from '@/api/modules/question'
import { uploadToUpyunApi } from '@/api/modules/upload'
import PageContainer from '@/components/common/PageContainer.vue'
import QuestionRenderer from '@/components/common/QuestionRenderer.vue'
import QueryBar from '@/components/form/QueryBar.vue'
import CommonPagination from '@/components/table/CommonPagination.vue'
import CommonTable from '@/components/table/CommonTable.vue'
import { usePagination } from '@/hooks/usePagination'
import { useSubjects } from '@/hooks/useSubjects'
import { DIFFICULTY_OPTIONS, QUESTION_TYPE_OPTIONS } from '@/utils/dicts'
import { formatDateTime } from '@/utils/format'
import type { QuestionRecord } from '@/types'

const loading = ref(false)
const submitLoading = ref(false)
const imageUploading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const formRef = ref<FormInstance>()
const tableData = ref<QuestionRecord[]>([])
const currentRow = ref<QuestionRecord>()
const { pagination, updatePagination } = usePagination()
const { subjectOptions, loadSubjects } = useSubjects()

const queryForm = reactive({
  keyword: '',
  subjectId: '' as number | '',
  questionType: '',
  difficulty: ''
})

const initialForm = () => ({
  id: undefined as number | undefined,
  subjectId: 1,
  questionType: 'SINGLE_CHOICE',
  title: '',
  imageUrls: [] as string[],
  options: ['选项A', '选项B'],
  answers: [] as string[],
  analysis: '',
  score: 5,
  difficulty: 'EASY',
  knowledgePoint: ''
})

const form = reactive(initialForm())
const singleAnswer = ref('')

watch(singleAnswer, (value) => {
  if (value) {
    form.answers = [value]
  }
})

const needsOptions = computed(() => ['SINGLE_CHOICE', 'MULTIPLE_CHOICE'].includes(form.questionType))

const rules: FormRules = {
  subjectId: [{ required: true, message: '请选择科目', trigger: 'change' }],
  questionType: [{ required: true, message: '请选择题型', trigger: 'change' }],
  title: [{ required: true, message: '请输入题干', trigger: 'blur' }],
  answers: [{ required: true, message: '请填写答案', trigger: 'change' }],
  score: [{ required: true, message: '请输入分值', trigger: 'blur' }],
  difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }],
  knowledgePoint: [{ required: true, message: '请输入知识点', trigger: 'blur' }]
}

function questionTypeLabel(value: string) {
  return QUESTION_TYPE_OPTIONS.find((item) => item.value === value)?.label || value
}

function difficultyLabel(value: string) {
  return DIFFICULTY_OPTIONS.find((item) => item.value === value)?.label || value
}

async function loadData() {
  loading.value = true
  try {
    const result = await getQuestionPageApi({
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
  Object.assign(queryForm, {
    keyword: '',
    subjectId: '',
    questionType: '',
    difficulty: ''
  })
  pagination.pageNum = 1
  loadData()
}

function handleQuestionTypeChange(value: string) {
  if (value === 'TRUE_FALSE') {
    form.options = ['true', 'false']
  } else if (['SINGLE_CHOICE', 'MULTIPLE_CHOICE'].includes(value)) {
    form.options = form.options?.length ? form.options : ['选项A', '选项B']
  } else {
    form.options = []
  }
  form.answers = []
  singleAnswer.value = ''
}

function addOption() {
  form.options.push(`选项${String.fromCharCode(65 + form.options.length)}`)
}

function removeOption(index: number) {
  form.options.splice(index, 1)
}

function handleAnswerInput(value: string) {
  form.answers = value
    .split('；')
    .map((item) => item.trim())
    .filter(Boolean)
}

async function handleImageUpload(file: UploadFile) {
  const rawFile = file.raw
  if (!rawFile) {
    return
  }
  if (!rawFile.type.startsWith('image/')) {
    ElMessage.warning('只能上传图片文件')
    return
  }
  if (rawFile.size > 2 * 1024 * 1024) {
    ElMessage.warning('单张图片不能超过 2MB')
    return
  }
  imageUploading.value = true
  try {
    const imageUrl = await uploadToUpyunApi(rawFile)
    form.imageUrls.push(imageUrl)
    ElMessage.success('图片上传成功')
  } catch (error: any) {
    ElMessage.error(error?.message || '图片上传失败')
  } finally {
    imageUploading.value = false
  }
}

function removeImage(index: number) {
  form.imageUrls.splice(index, 1)
}

function openDialog(row?: QuestionRecord) {
  Object.assign(form, initialForm(), row || {})
  form.imageUrls = [...(row?.imageUrls || [])]
  singleAnswer.value = row?.answers?.[0] || ''
  dialogVisible.value = true
}

function showDetail(row: QuestionRecord) {
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
    await saveQuestionApi(form)
    ElMessage.success('题目保存成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(row: QuestionRecord) {
  await ElMessageBox.confirm('确认删除该题目吗？', '删除确认', { type: 'warning' })
  await deleteQuestionApi(Number(row.id))
  ElMessage.success('删除成功')
  loadData()
}

function handlePageChange(pageNum: number, pageSize: number) {
  pagination.pageNum = pageNum
  pagination.pageSize = pageSize
  loadData()
}

onMounted(() => {
  loadSubjects()
  loadData()
})
</script>

<style scoped lang="scss">
.table-wrapper {
  padding: 16px;
}

.option-editor {
  display: grid;
  width: 100%;
  gap: 12px;
}

.option-editor__row {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
}

.question-thumb {
  width: 46px;
  height: 46px;
  border: 1px solid $app-border-color;
  border-radius: 6px;
}

.empty-text {
  color: $app-sub-text-color;
}

.image-uploader {
  display: grid;
  width: 100%;
  gap: 12px;
}

.image-uploader__list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
}

.image-uploader__item {
  display: grid;
  gap: 8px;
}

.image-uploader__item :deep(.el-image) {
  width: 100%;
  height: 110px;
  border: 1px solid $app-border-color;
  border-radius: 8px;
  background: #f8fafc;
}
</style>
