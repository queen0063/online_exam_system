<template>
  <page-container title="我的成绩" description="学生查看个人成绩、排名、是否及格等结果。">
    <div class="app-card table-wrapper">
      <common-table v-loading="loading" :data="tableData">
        <el-table-column prop="examName" label="考试名称" min-width="220" />
        <el-table-column prop="objectiveScore" label="客观题" width="100" />
        <el-table-column prop="subjectiveScore" label="主观题" width="100" />
        <el-table-column prop="totalScore" label="总分" width="90" />
        <el-table-column prop="rankNo" label="排名" width="90" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <status-tag :value="row.scoreStatus" :map="scoreStatusMap" />
          </template>
        </el-table-column>
        <el-table-column label="发布时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.publishTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120">
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
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { getMyScorePageApi } from '@/api/modules/score'
import PageContainer from '@/components/common/PageContainer.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import CommonPagination from '@/components/table/CommonPagination.vue'
import CommonTable from '@/components/table/CommonTable.vue'
import { usePagination } from '@/hooks/usePagination'
import { SCORE_STATUS_OPTIONS } from '@/utils/dicts'
import { formatDateTime } from '@/utils/format'
import type { ScoreRecord } from '@/types'

const router = useRouter()
const loading = ref(false)
const tableData = ref<ScoreRecord[]>([])
const { pagination, updatePagination } = usePagination()
const scoreStatusMap = SCORE_STATUS_OPTIONS.reduce<Record<string, string>>((map, item) => {
  map[item.value] = item.label
  return map
}, {})

async function loadData() {
  loading.value = true
  try {
    const result = await getMyScorePageApi({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = result.data.records
    updatePagination(result.data.total, result.data.pageNum, result.data.pageSize)
  } finally {
    loading.value = false
  }
}

function handlePageChange(pageNum: number, pageSize: number) {
  pagination.pageNum = pageNum
  pagination.pageSize = pageSize
  loadData()
}

function viewDetail(row: ScoreRecord) {
  router.push(`/score/detail/${row.examId}`)
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
