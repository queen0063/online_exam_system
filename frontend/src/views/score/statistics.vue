<template>
  <page-container title="成绩统计" description="展示分数段、及格率、优秀率和排名情况。">
    <div class="two-column">
      <chart-card title="分数段统计" :option="segmentOption" />
      <chart-card title="通过率概览" :option="rateOption" />
    </div>

    <div class="app-card summary-card">
      <el-descriptions :column="4" border>
        <el-descriptions-item label="考试 ID">{{ statistics?.examId }}</el-descriptions-item>
        <el-descriptions-item label="参考人数">{{ statistics?.totalCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="及格人数">{{ statistics?.passCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="优秀人数">{{ statistics?.excellentCount || 0 }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <div class="app-card table-wrapper">
      <common-table :data="ranking">
        <el-table-column prop="rankNo" label="排名" width="80" />
        <el-table-column prop="studentName" label="学生姓名" min-width="140" />
        <el-table-column prop="totalScore" label="总分" width="100" />
        <el-table-column prop="objectiveScore" label="客观题" width="100" />
        <el-table-column prop="subjectiveScore" label="主观题" width="100" />
      </common-table>
    </div>
  </page-container>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import type { EChartsOption } from 'echarts'

import { getScoreRankingApi, getScoreStatisticsApi } from '@/api/modules/score'
import ChartCard from '@/components/chart/ChartCard.vue'
import PageContainer from '@/components/common/PageContainer.vue'
import CommonTable from '@/components/table/CommonTable.vue'
import { formatPercent } from '@/utils/format'
import type { ScoreRecord, ScoreStatistics } from '@/types'

const route = useRoute()
const statistics = ref<ScoreStatistics>()
const ranking = ref<ScoreRecord[]>([])

const segmentOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis' },
  xAxis: {
    type: 'category',
    data: statistics.value?.scoreSegments?.map((item) => item.segmentName) || []
  },
  yAxis: { type: 'value' },
  series: [
    {
      type: 'bar',
      data: statistics.value?.scoreSegments?.map((item) => item.count) || [],
      itemStyle: { color: '#2563eb', borderRadius: [8, 8, 0, 0] }
    }
  ]
}))

const rateOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'item' },
  series: [
    {
      type: 'pie',
      radius: ['50%', '72%'],
      data: [
        { value: Number(statistics.value?.passRate || 0), name: `及格率 ${formatPercent(statistics.value?.passRate)}` },
        { value: Number(statistics.value?.excellentRate || 0), name: `优秀率 ${formatPercent(statistics.value?.excellentRate)}` }
      ]
    }
  ]
}))

async function loadData() {
  const examId = Number(route.params.examId)
  const [statisticsResult, rankingResult] = await Promise.all([getScoreStatisticsApi(examId), getScoreRankingApi(examId)])
  statistics.value = statisticsResult.data
  ranking.value = rankingResult.data
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.summary-card,
.table-wrapper {
  padding: 16px;
}
</style>
