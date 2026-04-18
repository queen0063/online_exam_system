<template>
  <page-container title="我的考试" description="查看待参加、进行中、已结束考试，并进入在线答题。">
    <div class="app-card filter-card">
      <el-radio-group v-model="statusFilter" @change="loadData">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="NOT_STARTED">待参加</el-radio-button>
        <el-radio-button label="IN_PROGRESS">进行中</el-radio-button>
        <el-radio-button label="ENDED">已结束</el-radio-button>
      </el-radio-group>
    </div>

    <div class="exam-grid">
      <div v-for="item in examList" :key="item.id" class="app-card exam-card">
        <div class="exam-card__header">
          <div>
            <h3>{{ item.examName }}</h3>
            <p>{{ formatDateTime(item.startTime) }} 至 {{ formatDateTime(item.endTime) }}</p>
          </div>
          <status-tag :value="item.status" :map="statusMap" />
        </div>
        <div class="exam-card__meta">
          <span>时长 {{ item.durationMinutes }} 分钟</span>
          <span>及格线 {{ item.passScore }} 分</span>
        </div>
        <div class="exam-card__footer">
          <el-button @click="showDetail(item)">查看详情</el-button>
          <el-button type="primary" :disabled="!canEnterExam(item)" @click="handleEnterExam(item)">
            {{ getEnterButtonText(item) }}
          </el-button>
        </div>
      </div>
    </div>

    <common-pagination
      :total="pagination.total"
      :page-num="pagination.pageNum"
      :page-size="pagination.pageSize"
      @change="handlePageChange"
    />

    <el-drawer v-model="detailVisible" title="考试说明" size="420px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="考试名称">{{ currentRow?.examName }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ formatDateTime(currentRow?.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ formatDateTime(currentRow?.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="考试时长">{{ currentRow?.durationMinutes }} 分钟</el-descriptions-item>
        <el-descriptions-item label="及格线">{{ currentRow?.passScore }} 分</el-descriptions-item>
      </el-descriptions>
      <el-alert
        class="mt-16"
        type="info"
        :closable="false"
        title="进入考试后禁止切屏、切换标签页或离开当前窗口，系统检测到违规将自动交卷。"
      />
    </el-drawer>
  </page-container>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import { getAnswerExamPageApi, startExamApi } from '@/api/modules/answer'
import PageContainer from '@/components/common/PageContainer.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import CommonPagination from '@/components/table/CommonPagination.vue'
import { usePagination } from '@/hooks/usePagination'
import { EXAM_STATUS_OPTIONS } from '@/utils/dicts'
import { formatDateTime } from '@/utils/format'
import type { ExamRecord } from '@/types'

const router = useRouter()
const examList = ref<ExamRecord[]>([])
const currentRow = ref<ExamRecord>()
const detailVisible = ref(false)
const statusFilter = ref('')
const { pagination, updatePagination } = usePagination()

const statusMap = EXAM_STATUS_OPTIONS.reduce<Record<string, string>>((map, item) => {
  map[item.value] = item.label
  return map
}, {})

async function loadData() {
  const result = await getAnswerExamPageApi({
    pageNum: pagination.pageNum,
    pageSize: pagination.pageSize,
    status: statusFilter.value || undefined
  })
  examList.value = result.data.records
  updatePagination(result.data.total, result.data.pageNum, result.data.pageSize)
}

function showDetail(row: ExamRecord) {
  currentRow.value = row
  detailVisible.value = true
}

async function handleEnterExam(row: ExamRecord) {
  await startExamApi(Number(row.id))
  ElMessage.success('已进入考试页面')
  await router.push(`/answer/exam/${row.id}`)
}

function canEnterExam(row: ExamRecord) {
  return !['SUBMITTED', 'WAIT_MARKING', 'MARKED'].includes(row.answerStatus || '')
}

function getEnterButtonText(row: ExamRecord) {
  return canEnterExam(row) ? '进入考试' : '已提交'
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
.filter-card {
  padding: 16px 18px;
}

.exam-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 16px;
}

.exam-card {
  padding: 20px;
}

.exam-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.exam-card__header h3 {
  margin: 0;
}

.exam-card__header p,
.exam-card__meta {
  color: $app-sub-text-color;
}

.exam-card__meta {
  display: flex;
  gap: 16px;
  margin: 18px 0;
}

.exam-card__footer {
  display: flex;
  gap: 12px;
}
</style>
