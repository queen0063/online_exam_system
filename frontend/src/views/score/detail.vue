<template>
  <page-container title="成绩详情" description="查看单场考试成绩详情、排名信息和作答表现。">
    <div class="summary-grid">
      <div class="app-card summary-grid__item">
        <span>考试名称</span>
        <strong>{{ detail?.examName || '-' }}</strong>
      </div>
      <div class="app-card summary-grid__item">
        <span>总分</span>
        <strong>{{ detail?.totalScore ?? 0 }}</strong>
      </div>
      <div class="app-card summary-grid__item">
        <span>客观题得分</span>
        <strong>{{ detail?.objectiveScore ?? 0 }}</strong>
      </div>
      <div class="app-card summary-grid__item">
        <span>主观题得分</span>
        <strong>{{ detail?.subjectiveScore ?? 0 }}</strong>
      </div>
      <div class="app-card summary-grid__item">
        <span>排名</span>
        <strong>{{ detail?.rankNo ?? '-' }}</strong>
      </div>
      <div class="app-card summary-grid__item">
        <span>发布状态</span>
        <strong>{{ detail?.scoreStatus || '-' }}</strong>
      </div>
    </div>

    <div class="two-column">
      <chart-card title="成绩构成" description="客观题与主观题占比" :option="scoreOption" />
      <chart-card title="全体排名" description="当前考试排名前十" :option="rankingOption" />
    </div>

    <div v-if="isStudent" class="app-card answer-list">
      <div class="answer-list__header">
        <h3>我的答题详情</h3>
        <el-button type="primary" plain @click="$router.push('/question/wrong')">查看错题本</el-button>
      </div>
      <div v-if="answerList.length === 0" class="page-empty">
        <el-empty description="暂无答题详情" />
      </div>
      <div v-for="(item, index) in answerList" :key="item.id" class="answer-list__item">
        <question-renderer
          :question="{
            ...item,
            answers: item.standardAnswers,
            analysis: item.teacherComment || `得分：${item.actualScore ?? 0}/${item.questionScore}`
          }"
          :index="index"
          :show-analysis="true"
          :model-value="item.studentAnswers || []"
        />
      </div>
    </div>

    <div v-else class="app-card table-wrapper">
      <common-table :data="ranking">
        <el-table-column prop="rankNo" label="排名" width="80" />
        <el-table-column prop="studentName" label="学生" min-width="140" />
        <el-table-column prop="totalScore" label="总分" width="90" />
        <el-table-column prop="objectiveScore" label="客观题" width="90" />
        <el-table-column prop="subjectiveScore" label="主观题" width="90" />
        <el-table-column prop="scoreStatus" label="状态" width="120" />
      </common-table>
    </div>
  </page-container>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import type { EChartsOption } from 'echarts'

import { getAnswerDetailApi } from '@/api/modules/answer'
import { getScoreDetailApi, getScoreRankingApi } from '@/api/modules/score'
import ChartCard from '@/components/chart/ChartCard.vue'
import PageContainer from '@/components/common/PageContainer.vue'
import QuestionRenderer from '@/components/common/QuestionRenderer.vue'
import CommonTable from '@/components/table/CommonTable.vue'
import { useUserStore } from '@/stores/user'
import type { AnswerRecord, ScoreRecord } from '@/types'

const route = useRoute()
const userStore = useUserStore()

const detail = ref<ScoreRecord>()
const ranking = ref<ScoreRecord[]>([])
const answerList = ref<AnswerRecord[]>([])

const examId = computed(() => Number(route.params.examId))
const queryStudentId = computed(() => {
  const value = route.query.studentId
  return value ? Number(value) : undefined
})
const isStudent = computed(() => userStore.roleCodes.includes('STUDENT'))

const scoreOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'item' },
  series: [
    {
      type: 'pie',
      radius: ['42%', '70%'],
      data: [
        { value: detail.value?.objectiveScore || 0, name: '客观题得分' },
        { value: detail.value?.subjectiveScore || 0, name: '主观题得分' }
      ]
    }
  ]
}))

const rankingOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 26, right: 20, top: 30, bottom: 24 },
  xAxis: {
    type: 'category',
    axisLabel: { interval: 0, rotate: 25 },
    data: ranking.value.slice(0, 10).map((item) => item.studentName || `学生${item.studentId}`)
  },
  yAxis: { type: 'value' },
  series: [
    {
      type: 'bar',
      data: ranking.value.slice(0, 10).map((item) => item.totalScore || 0),
      itemStyle: {
        color: '#2563eb',
        borderRadius: [8, 8, 0, 0]
      }
    }
  ]
}))

async function loadData() {
  const [detailResult, rankingResult] = await Promise.all([
    getScoreDetailApi(examId.value, isStudent.value ? undefined : queryStudentId.value),
    getScoreRankingApi(examId.value)
  ])

  detail.value = detailResult.data
  ranking.value = rankingResult.data

  if (isStudent.value) {
    try {
      const answerResult = await getAnswerDetailApi(examId.value)
      answerList.value = answerResult.data
    } catch {
      answerList.value = []
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.summary-grid {
  display: grid;
  grid-template-columns: repeat(6, minmax(0, 1fr));
  gap: 16px;
}

.summary-grid__item {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 20px;
}

.summary-grid__item span {
  color: $app-sub-text-color;
  font-size: 13px;
}

.summary-grid__item strong {
  font-size: 28px;
}

.answer-list,
.table-wrapper {
  padding: 18px;
}

.answer-list__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.answer-list__header h3 {
  margin: 0;
}

.answer-list__item + .answer-list__item {
  margin-top: 16px;
}

@media (max-width: 1280px) {
  .summary-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
