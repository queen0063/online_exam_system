<template>
  <page-container title="成绩列表" description="查看考试成绩明细、排名，并进入统计页查看图表。">
    <query-bar>
      <el-form :model="queryForm" inline>
        <el-form-item label="考试名称">
          <el-select v-model="queryForm.examId" clearable filterable placeholder="请选择考试" class="exam-select">
            <el-option v-for="item in examOptions" :key="item.id" :label="item.examName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="学生姓名" clearable />
        </el-form-item>
      </el-form>
      <template #actions>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button :disabled="!queryForm.examId" @click="viewSelectedStatistics">统计</el-button>
        <el-button type="success" :disabled="!queryForm.examId" @click="handleSelectedExport">导出成绩</el-button>
      </template>
    </query-bar>

    <div class="app-card table-wrapper">
      <common-table v-loading="loading" :data="tableData">
        <el-table-column prop="examName" label="考试名称" min-width="200" />
        <el-table-column prop="studentName" label="学生姓名" min-width="120" />
        <el-table-column prop="objectiveScore" label="客观题" width="90" />
        <el-table-column prop="subjectiveScore" label="主观题" width="90" />
        <el-table-column prop="totalScore" label="总分" width="90" />
        <el-table-column prop="rankNo" label="排名" width="80" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <status-tag :value="row.scoreStatus" :map="scoreStatusMap" />
          </template>
        </el-table-column>
        <el-table-column label="发布时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.publishTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
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
  </page-container>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import { getExamPageApi } from '@/api/modules/exam'
import { exportScoreApi, getScorePageApi } from '@/api/modules/score'
import PageContainer from '@/components/common/PageContainer.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import QueryBar from '@/components/form/QueryBar.vue'
import CommonPagination from '@/components/table/CommonPagination.vue'
import CommonTable from '@/components/table/CommonTable.vue'
import { usePagination } from '@/hooks/usePagination'
import { SCORE_STATUS_OPTIONS } from '@/utils/dicts'
import { downloadBlobFile, resolveDownloadFileName } from '@/utils/download'
import { formatDateTime } from '@/utils/format'
import type { ScoreRecord } from '@/types'

const router = useRouter()
const loading = ref(false)
const tableData = ref<ScoreRecord[]>([])
const examOptions = ref<Array<{ id: number; examName: string }>>([])
const { pagination, updatePagination } = usePagination()
const scoreStatusMap = SCORE_STATUS_OPTIONS.reduce<Record<string, string>>((map, item) => {
  map[item.value] = item.label
  return map
}, {})

const queryForm = reactive({
  examId: undefined as number | undefined,
  keyword: ''
})

async function loadData() {
  loading.value = true
  try {
    const result = await getScorePageApi({
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

async function loadExamOptions() {
  const result = await getExamPageApi({
    pageNum: 1,
    pageSize: 1000
  })
  examOptions.value = result.data.records
    .filter((item) => item.id)
    .map((item) => ({
      id: Number(item.id),
      examName: item.examName
    }))
}

function viewSelectedStatistics() {
  if (!queryForm.examId) {
    return
  }
  router.push(`/score/statistics/${queryForm.examId}`)
}

function viewDetail(row: ScoreRecord) {
  router.push(`/score/detail/${row.examId}?studentId=${row.studentId || ''}`)
}

async function handleSelectedExport() {
  if (!queryForm.examId) {
    return
  }
  const response = await exportScoreApi(queryForm.examId)
  const fileName = resolveDownloadFileName(response, `exam-score-${queryForm.examId}.csv`)
  downloadBlobFile(response.data, fileName)
  ElMessage.success('成绩导出成功')
}

function resetQuery() {
  queryForm.examId = undefined
  queryForm.keyword = ''
  pagination.pageNum = 1
  loadData()
}

function handlePageChange(pageNum: number, pageSize: number) {
  pagination.pageNum = pageNum
  pagination.pageSize = pageSize
  loadData()
}

onMounted(() => {
  loadExamOptions()
  loadData()
})
</script>

<style scoped lang="scss">
.exam-select {
  width: 280px;
}

.table-wrapper {
  padding: 16px;
}
</style>
