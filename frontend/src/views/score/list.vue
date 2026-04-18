<template>
  <page-container title="成绩列表" description="查看考试成绩明细、排名，并进入统计页查看图表。">
    <query-bar>
      <el-form :model="queryForm" inline>
        <el-form-item label="考试 ID">
          <el-input-number v-model="queryForm.examId" :min="1" />
        </el-form-item>
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="学生姓名 / 考试名" clearable />
        </el-form-item>
      </el-form>
      <template #actions>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="primary" @click="loadData">查询</el-button>
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
        <el-table-column prop="scoreStatus" label="状态" width="120" />
        <el-table-column label="发布时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.publishTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <div class="actions">
              <el-button link type="primary" @click="viewDetail(row)">详情</el-button>
              <el-button link type="primary" @click="viewStatistics(row)">统计</el-button>
              <el-button link type="success" @click="handleExport(row)">导出成绩</el-button>
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
  </page-container>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import { exportScoreApi, getScorePageApi } from '@/api/modules/score'
import PageContainer from '@/components/common/PageContainer.vue'
import QueryBar from '@/components/form/QueryBar.vue'
import CommonPagination from '@/components/table/CommonPagination.vue'
import CommonTable from '@/components/table/CommonTable.vue'
import { usePagination } from '@/hooks/usePagination'
import { downloadBlobFile, resolveDownloadFileName } from '@/utils/download'
import { formatDateTime } from '@/utils/format'
import type { ScoreRecord } from '@/types'

const router = useRouter()
const loading = ref(false)
const tableData = ref<ScoreRecord[]>([])
const { pagination, updatePagination } = usePagination()

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

function viewStatistics(row: ScoreRecord) {
  router.push(`/score/statistics/${row.examId}`)
}

function viewDetail(row: ScoreRecord) {
  router.push(`/score/detail/${row.examId}?studentId=${row.studentId || ''}`)
}

async function handleExport(row: ScoreRecord) {
  const response = await exportScoreApi(row.examId)
  const fileName = resolveDownloadFileName(response, `exam-score-${row.examId}.csv`)
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
  loadData()
})
</script>

<style scoped lang="scss">
.table-wrapper {
  padding: 16px;
}
</style>
