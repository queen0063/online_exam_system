<template>
  <page-container title="考试监测" description="查看已进入考试学生的作答状态与切屏次数。">
    <query-bar>
      <el-form inline>
        <el-form-item label="考试ID">
          <el-input :model-value="examId" disabled />
        </el-form-item>
      </el-form>
      <template #actions>
        <el-button @click="router.back()">返回</el-button>
        <el-button type="primary" :loading="loading" @click="loadData">刷新</el-button>
      </template>
    </query-bar>

    <div class="app-card monitor-summary">
      <div>
        <span>已进入考试</span>
        <strong>{{ tableData.length }}</strong>
      </div>
      <div>
        <span>考试中</span>
        <strong>{{ answeringCount }}</strong>
      </div>
      <div>
        <span>已提交</span>
        <strong>{{ submittedCount }}</strong>
      </div>
    </div>

    <div class="app-card table-wrapper">
      <common-table v-loading="loading" :data="tableData">
        <el-table-column type="index" label="#" width="60" />
        <el-table-column prop="studentName" label="学生姓名" min-width="140" />
        <el-table-column prop="studentNo" label="学号" min-width="140">
          <template #default="{ row }">{{ row.studentNo || '-' }}</template>
        </el-table-column>
        <el-table-column label="班级" min-width="180">
          <template #default="{ row }">{{ classLabel(row) }}</template>
        </el-table-column>
        <el-table-column prop="switchCount" label="切屏次数" width="120" sortable />
        <el-table-column label="开始作答时间" min-width="180">
          <template #default="{ row }">{{ formatDateTime(row.startTime) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <status-tag :value="row.answerStatus" :map="answerStatusMap" />
          </template>
        </el-table-column>
      </common-table>
    </div>
  </page-container>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { getExamMonitoringApi } from '@/api/modules/exam'
import PageContainer from '@/components/common/PageContainer.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import QueryBar from '@/components/form/QueryBar.vue'
import CommonTable from '@/components/table/CommonTable.vue'
import { formatDateTime } from '@/utils/format'
import type { ExamMonitorRecord } from '@/types'

const route = useRoute()
const router = useRouter()
const examId = Number(route.params.examId)
const loading = ref(false)
const tableData = ref<ExamMonitorRecord[]>([])
let refreshTimer: number | undefined

const answerStatusMap: Record<string, string> = {
  ANSWERING: '考试中',
  SUBMITTED: '已提交',
  WAIT_MARKING: '已提交',
  MARKED: '已提交'
}

const answeringCount = computed(() => tableData.value.filter((item) => item.answerStatus === 'ANSWERING').length)
const submittedCount = computed(() => tableData.value.filter((item) => item.answerStatus !== 'ANSWERING').length)

function classLabel(row: ExamMonitorRecord) {
  if (!row.className) {
    return '-'
  }
  return row.gradeName ? `${row.gradeName} ${row.className}` : row.className
}

async function loadData() {
  loading.value = true
  try {
    const result = await getExamMonitoringApi(examId)
    tableData.value = result.data
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  void loadData()
  refreshTimer = window.setInterval(() => {
    void loadData()
  }, 10000)
})

onBeforeUnmount(() => {
  if (refreshTimer !== undefined) {
    window.clearInterval(refreshTimer)
  }
})
</script>

<style scoped lang="scss">
.monitor-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  padding: 18px;
}

.monitor-summary div {
  display: grid;
  gap: 8px;
  padding: 14px;
  border-radius: $radius-md;
  background: $app-surface-subtle;
}

.monitor-summary span {
  color: $app-text-secondary;
  font-size: 13px;
}

.monitor-summary strong {
  font-size: 28px;
}

.table-wrapper {
  padding: 16px;
}

@media (max-width: 720px) {
  .monitor-summary {
    grid-template-columns: 1fr;
  }
}
</style>
