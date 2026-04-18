<template>
  <page-container title="待阅卷列表" description="教师查看待批改答卷，并进入主观题评分页面。">
    <div class="app-card table-wrapper">
      <common-table v-loading="loading" :data="tableData">
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="examName" label="考试名称" min-width="220" />
        <el-table-column prop="studentName" label="学生姓名" min-width="120" />
        <el-table-column prop="objectiveScore" label="客观题得分" width="120" />
        <el-table-column prop="subjectiveScore" label="主观题得分" width="120" />
        <el-table-column prop="totalScore" label="当前总分" width="100" />
        <el-table-column prop="scoreStatus" label="状态" width="120" />
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

import { getPendingMarkingApi } from '@/api/modules/marking'
import PageContainer from '@/components/common/PageContainer.vue'
import CommonPagination from '@/components/table/CommonPagination.vue'
import CommonTable from '@/components/table/CommonTable.vue'
import { usePagination } from '@/hooks/usePagination'
import type { MarkingRecord } from '@/types'

const router = useRouter()
const loading = ref(false)
const tableData = ref<MarkingRecord[]>([])
const { pagination, updatePagination } = usePagination()

async function loadData() {
  loading.value = true
  try {
    const result = await getPendingMarkingApi({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = result.data.records
    updatePagination(result.data.total, result.data.pageNum, result.data.pageSize)
  } finally {
    loading.value = false
  }
}

function goDetail(row: MarkingRecord) {
  router.push(`/marking/detail/${row.examId}/${row.studentId}`)
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
