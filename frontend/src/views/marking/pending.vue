<template>
  <page-container title="待阅卷列表" description="教师查看待批改答卷，并进入主观题评分页面。">
    <div class="app-card filter-card">
      <el-select v-model="query.examId" clearable filterable placeholder="按考试筛选" class="exam-filter" @change="handleSearch">
        <el-option v-for="item in examOptions" :key="item.id" :label="item.examName" :value="item.id" />
      </el-select>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <div class="app-card table-wrapper">
      <common-table v-loading="loading" :data="tableData">
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="examName" label="考试名称" min-width="220" />
        <el-table-column prop="studentName" label="学生姓名" min-width="120" />
        <el-table-column prop="objectiveScore" label="客观题得分" width="120" />
        <el-table-column prop="subjectiveScore" label="主观题得分" width="120" />
        <el-table-column prop="totalScore" label="当前总分" width="100" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <status-tag :value="row.scoreStatus" :map="scoreStatusMap" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button type="primary" link @click="goDetail(row)">去阅卷</el-button>
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
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { getExamPageApi } from '@/api/modules/exam'
import { getPendingMarkingApi } from '@/api/modules/marking'
import PageContainer from '@/components/common/PageContainer.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import CommonPagination from '@/components/table/CommonPagination.vue'
import CommonTable from '@/components/table/CommonTable.vue'
import { usePagination } from '@/hooks/usePagination'
import { SCORE_STATUS_OPTIONS } from '@/utils/dicts'
import type { MarkingRecord } from '@/types'

const router = useRouter()
const loading = ref(false)
const tableData = ref<MarkingRecord[]>([])
const examOptions = ref<Array<{ id: number; examName: string }>>([])
const query = ref<{ examId?: number }>({})
const { pagination, updatePagination } = usePagination()
const scoreStatusMap = SCORE_STATUS_OPTIONS.reduce<Record<string, string>>((map, item) => {
  map[item.value] = item.label
  return map
}, {})

async function loadData() {
  loading.value = true
  try {
    const result = await getPendingMarkingApi({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      examId: query.value.examId
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

function goDetail(row: MarkingRecord) {
  router.push(`/marking/detail/${row.examId}/${row.studentId}`)
}

function handleSearch() {
  pagination.pageNum = 1
  loadData()
}

function handleReset() {
  query.value = {}
  handleSearch()
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
.filter-card {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
  padding: 16px;
}

.exam-filter {
  width: 320px;
}

.table-wrapper {
  padding: 16px;
}
</style>
