<template>
  <page-container title="工作台" description="按角色呈现今日考试工作概览与关键数据。">
    <div class="dashboard page-grid">
      <div class="dashboard__stats">
        <div v-for="card in cards" :key="card.title" class="app-card dashboard__stat-card">
          <div>
            <div class="dashboard__stat-title">{{ card.title }}</div>
            <div class="dashboard__stat-value">{{ card.value }}</div>
            <div class="dashboard__stat-foot">{{ card.foot }}</div>
          </div>
          <div class="dashboard__stat-icon" :style="{ background: card.bg }">
            <el-icon><app-icon :name="card.icon" /></el-icon>
          </div>
        </div>
      </div>

      <div class="two-column">
        <chart-card title="趋势统计" description="近 7 天业务数据趋势" :option="trendOption" />
        <chart-card title="结构占比" description="当前角色关注的业务构成" :option="pieOption" />
      </div>

      <div class="two-column">
        <div class="app-card dashboard__notice">
          <div class="dashboard__section-title">最新公告</div>
          <el-empty v-if="notices.length === 0" description="暂无公告" />
          <el-timeline v-else>
            <el-timeline-item v-for="item in notices" :key="item.id" :timestamp="formatDateTime(item.createTime)">
              <router-link :to="`/notice/detail/${item.id}`">{{ item.title }}</router-link>
            </el-timeline-item>
          </el-timeline>
        </div>
        <div class="app-card dashboard__todo">
          <div class="dashboard__section-title">工作提醒</div>
          <el-empty v-if="todos.length === 0" description="暂无待办" />
          <div v-else class="dashboard__todo-list">
            <div v-for="item in todos" :key="item.title" class="dashboard__todo-item">
              <strong>{{ item.title }}</strong>
              <span>{{ item.description }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </page-container>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
import { computed, onMounted, ref } from 'vue'
import type { EChartsOption } from 'echarts'

import { getAnswerExamPageApi } from '@/api/modules/answer'
import ChartCard from '@/components/chart/ChartCard.vue'
import AppIcon from '@/components/common/AppIcon.vue'
import PageContainer from '@/components/common/PageContainer.vue'
import { getExamPageApi } from '@/api/modules/exam'
import { getPendingMarkingApi } from '@/api/modules/marking'
import { getNoticePageApi } from '@/api/modules/notice'
import { getPaperPageApi } from '@/api/modules/paper'
import { getQuestionPageApi, getWrongQuestionApi } from '@/api/modules/question'
import { getMyScorePageApi, getScorePageApi } from '@/api/modules/score'
import { getUserPageApi } from '@/api/modules/user'
import { useUserStore } from '@/stores/user'
import { formatDateTime } from '@/utils/format'
import type { ExamRecord, NoticeRecord } from '@/types'

const userStore = useUserStore()
const cards = ref<Array<{ title: string; value: string; foot: string; icon: string; bg: string }>>([])
const notices = ref<NoticeRecord[]>([])
const todos = ref<Array<{ title: string; description: string }>>([])
const trendData = ref<number[]>(Array(7).fill(0))
const pieData = ref<Array<{ value: number; name: string }>>([])

const WEEK_LABELS = Array.from({ length: 7 }, (_, index) => dayjs().subtract(6 - index, 'day').format('MM-DD'))
const CARD_BACKGROUNDS = [
  'rgba(37, 99, 235, 0.12)',
  'rgba(22, 163, 74, 0.12)',
  'rgba(217, 119, 6, 0.12)',
  'rgba(139, 92, 246, 0.12)'
]

const trendOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 36, right: 18, top: 36, bottom: 24 },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: WEEK_LABELS
  },
  yAxis: { type: 'value' },
  series: [
    {
      type: 'line',
      smooth: true,
      data: trendData.value,
      areaStyle: {
        color: 'rgba(37, 99, 235, 0.12)'
      },
      lineStyle: {
        color: '#2563eb'
      },
      itemStyle: {
        color: '#2563eb'
      }
    }
  ]
}))

const pieOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'item' },
  legend: { bottom: 0 },
  series: [
    {
      type: 'pie',
      radius: ['45%', '72%'],
      center: ['50%', '46%'],
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 4
      },
      data: pieData.value
    }
  ]
}))

function formatCount(value: number, digits = 0) {
  return digits > 0 ? value.toFixed(digits) : String(value)
}

function isFinishedAnswerStatus(status?: string) {
  return ['SUBMITTED', 'WAIT_MARKING', 'MARKED'].includes(status || '')
}

function buildDailyCounts(values: Array<string | undefined>) {
  return WEEK_LABELS.map((label) => values.filter((value) => value && dayjs(value).format('MM-DD') === label).length)
}

function sortByTimeDesc<T>(records: T[], getter: (record: T) => string | undefined) {
  return [...records].sort((left, right) => dayjs(getter(right)).valueOf() - dayjs(getter(left)).valueOf())
}

function sortByClosestFutureExam(exams: ExamRecord[]) {
  return [...exams].sort((left, right) => dayjs(left.startTime).valueOf() - dayjs(right.startTime).valueOf())
}

async function loadAdminDashboard() {
  const [userResult, examResult, paperResult, questionResult, noticeResult] = await Promise.all([
    getUserPageApi({ pageNum: 1, pageSize: 1000 }),
    getExamPageApi({ pageNum: 1, pageSize: 1000 }),
    getPaperPageApi({ pageNum: 1, pageSize: 1000 }),
    getQuestionPageApi({ pageNum: 1, pageSize: 1000 }),
    getNoticePageApi({ pageNum: 1, pageSize: 1000, noticeStatus: 'PUBLISHED' })
  ])

  const latestNotices = sortByTimeDesc(noticeResult.data.records, (item) => item.createTime)
  notices.value = latestNotices.slice(0, 5)
  cards.value = [
    { title: '用户总数', value: formatCount(userResult.data.total), foot: '数据库实时用户数', icon: 'User', bg: CARD_BACKGROUNDS[0] },
    { title: '考试总数', value: formatCount(examResult.data.total), foot: '当前数据库考试记录', icon: 'Calendar', bg: CARD_BACKGROUNDS[1] },
    { title: '试卷总数', value: formatCount(paperResult.data.total), foot: '数据库可用试卷数', icon: 'Document', bg: CARD_BACKGROUNDS[2] },
    { title: '题目总数', value: formatCount(questionResult.data.total), foot: '数据库题库总量', icon: 'EditPen', bg: CARD_BACKGROUNDS[3] }
  ]

  trendData.value = buildDailyCounts([
    ...userResult.data.records.map((item) => item.createTime),
    ...examResult.data.records.map((item) => item.startTime),
    ...paperResult.data.records.map((item) => item.createTime),
    ...questionResult.data.records.map((item) => item.createTime),
    ...latestNotices.map((item) => item.createTime)
  ])

  pieData.value = [
    { value: questionResult.data.total, name: '题库题目' },
    { value: paperResult.data.total, name: '试卷资源' },
    { value: examResult.data.total, name: '考试记录' },
    { value: noticeResult.data.total, name: '已发布公告' }
  ]

  const upcomingExam = sortByClosestFutureExam(examResult.data.records.filter((item) => dayjs(item.startTime).isAfter(dayjs())))[0]
  todos.value = [
    {
      title: '公告中心',
      description: latestNotices[0]
        ? `最新公告为“${latestNotices[0].title}”，当前共有 ${noticeResult.data.total} 条已发布公告。`
        : '当前数据库中没有已发布公告。'
    },
    {
      title: '考试排期',
      description: upcomingExam
        ? `最近一场考试是“${upcomingExam.examName}”，开始时间 ${formatDateTime(upcomingExam.startTime)}。`
        : '当前数据库中没有待开始考试。'
    }
  ]
}

async function loadTeacherDashboard() {
  const [examResult, paperResult, pendingResult, scoreResult, noticeResult] = await Promise.all([
    getExamPageApi({ pageNum: 1, pageSize: 1000 }),
    getPaperPageApi({ pageNum: 1, pageSize: 1000 }),
    getPendingMarkingApi({ pageNum: 1, pageSize: 1000 }),
    getScorePageApi({ pageNum: 1, pageSize: 1000 }),
    getNoticePageApi({ pageNum: 1, pageSize: 5, noticeStatus: 'PUBLISHED' })
  ])

  const examRecords = examResult.data.records
  const paperRecords = paperResult.data.records
  const scoreRecords = scoreResult.data.records
  const latestNotices = sortByTimeDesc(noticeResult.data.records, (item) => item.createTime)
  const draftCount = examRecords.filter((item) => item.status === 'DRAFT').length
  const averageScore = scoreRecords.length
    ? scoreRecords.reduce((sum, item) => sum + (item.totalScore || 0), 0) / scoreRecords.length
    : 0

  notices.value = latestNotices
  cards.value = [
    { title: '我的考试', value: formatCount(examResult.data.total), foot: `其中 ${draftCount} 场草稿`, icon: 'Notebook', bg: CARD_BACKGROUNDS[0] },
    { title: '我的试卷', value: formatCount(paperResult.data.total), foot: '数据库中归属教师的试卷', icon: 'CollectionTag', bg: CARD_BACKGROUNDS[1] },
    { title: '待阅卷', value: formatCount(pendingResult.data.total), foot: '当前待处理答卷数', icon: 'Finished', bg: CARD_BACKGROUNDS[2] },
    { title: '平均得分', value: formatCount(averageScore, 1), foot: '按现有成绩记录计算', icon: 'TrendCharts', bg: CARD_BACKGROUNDS[3] }
  ]

  trendData.value = buildDailyCounts([
    ...examRecords.map((item) => item.startTime),
    ...paperRecords.map((item) => item.createTime),
    ...scoreRecords.map((item) => item.publishTime)
  ])

  pieData.value = [
    { value: pendingResult.data.total, name: '待阅卷' },
    { value: examResult.data.total, name: '我的考试' },
    { value: paperResult.data.total, name: '我的试卷' },
    { value: scoreResult.data.total, name: '成绩记录' }
  ]

  const nextExam = sortByClosestFutureExam(
    examRecords.filter((item) => item.status !== 'DRAFT' && dayjs(item.startTime).isAfter(dayjs()))
  )[0]
  const firstPending = pendingResult.data.records[0]
  todos.value = [
    {
      title: '阅卷提醒',
      description: firstPending
        ? `“${firstPending.examName}”仍有 ${pendingResult.data.total} 份待阅卷，当前列表首条学生为 ${firstPending.studentName}。`
        : '当前没有待阅卷答卷。'
    },
    {
      title: '考试安排',
      description: nextExam
        ? `最近一场考试是“${nextExam.examName}”，开始时间 ${formatDateTime(nextExam.startTime)}。`
        : '当前没有即将开始的考试安排。'
    }
  ]
}

async function loadStudentDashboard() {
  const [examResult, scoreResult, wrongResult, noticeResult] = await Promise.all([
    getAnswerExamPageApi({ pageNum: 1, pageSize: 1000 }),
    getMyScorePageApi({ pageNum: 1, pageSize: 1000 }),
    getWrongQuestionApi(),
    getNoticePageApi({ pageNum: 1, pageSize: 1000, noticeStatus: 'PUBLISHED' })
  ])

  const examRecords = examResult.data.records
  const scoreRecords = scoreResult.data.records
  const wrongRecords = wrongResult.data
  const latestNotices = sortByTimeDesc(noticeResult.data.records, (item) => item.createTime)
  const latestScores = sortByTimeDesc(scoreRecords, (item) => item.publishTime)
  const now = dayjs()
  const pendingExamCount = examRecords.filter((item) => dayjs(item.startTime).isAfter(now) && !isFinishedAnswerStatus(item.answerStatus)).length
  const ongoingExamCount = examRecords.filter((item) => {
    const inTimeRange = (dayjs(item.startTime).isBefore(now) || dayjs(item.startTime).isSame(now))
      && dayjs(item.endTime).isAfter(now)
    return inTimeRange && !isFinishedAnswerStatus(item.answerStatus)
  }).length
  const latestScore = latestScores[0]

  notices.value = latestNotices.slice(0, 5)
  cards.value = [
    { title: '待参加考试', value: formatCount(pendingExamCount), foot: '数据库内待开始且未提交的考试', icon: 'AlarmClock', bg: CARD_BACKGROUNDS[0] },
    { title: '进行中考试', value: formatCount(ongoingExamCount), foot: '当前时间窗口内可作答的考试', icon: 'Timer', bg: CARD_BACKGROUNDS[1] },
    { title: '最近成绩', value: latestScore ? formatCount(latestScore.totalScore || 0) : '-', foot: latestScore ? `最近一次考试：${latestScore.examName}` : '当前还没有成绩记录', icon: 'Medal', bg: CARD_BACKGROUNDS[2] },
    { title: '公告提醒', value: formatCount(noticeResult.data.total), foot: '数据库中已发布公告数量', icon: 'Bell', bg: CARD_BACKGROUNDS[3] }
  ]

  trendData.value = buildDailyCounts([
    ...examRecords.map((item) => item.startTime),
    ...latestScores.map((item) => item.publishTime),
    ...wrongRecords.map((item) => item.submitTime)
  ])

  pieData.value = [
    { value: pendingExamCount, name: '待考试' },
    { value: ongoingExamCount, name: '进行中' },
    { value: scoreResult.data.total, name: '成绩记录' },
    { value: wrongRecords.length, name: '错题数量' }
  ]

  const nearestExam = sortByClosestFutureExam(
    examRecords.filter((item) => !isFinishedAnswerStatus(item.answerStatus) && dayjs(item.endTime).isAfter(now))
  )[0]
  todos.value = [
    {
      title: '考试提醒',
      description: nearestExam
        ? `最近需要关注的考试是“${nearestExam.examName}”，开始时间 ${formatDateTime(nearestExam.startTime)}。`
        : '当前没有待参加或进行中的考试。'
    },
    {
      title: '复习提醒',
      description: wrongRecords.length > 0
        ? `错题本当前共有 ${wrongRecords.length} 道错题，最近成绩 ${latestScore?.totalScore ?? 0} 分。`
        : latestScore
          ? `当前没有错题记录，最近一次考试“${latestScore.examName}”成绩为 ${latestScore.totalScore ?? 0} 分。`
          : '当前还没有错题和成绩记录。'
    }
  ]
}

async function loadDashboard() {
  try {
    if (userStore.roleCodes.includes('ADMIN')) {
      await loadAdminDashboard()
      return
    }
    if (userStore.roleCodes.includes('TEACHER')) {
      await loadTeacherDashboard()
      return
    }
    await loadStudentDashboard()
  } catch {
    cards.value = []
    notices.value = []
    todos.value = []
    trendData.value = Array(7).fill(0)
    pieData.value = []
  }
}

onMounted(() => {
  loadDashboard()
})
</script>

<style scoped lang="scss">
.dashboard__stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.dashboard__stat-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 22px;
}

.dashboard__stat-title {
  color: $app-sub-text-color;
  font-size: 14px;
}

.dashboard__stat-value {
  margin-top: 10px;
  font-size: 30px;
  font-weight: 800;
}

.dashboard__stat-foot {
  margin-top: 8px;
  color: $app-sub-text-color;
  font-size: 13px;
}

.dashboard__stat-icon {
  display: grid;
  width: 56px;
  height: 56px;
  place-items: center;
  border-radius: 18px;
  font-size: 24px;
}

.dashboard__notice,
.dashboard__todo {
  padding: 20px;
}

.dashboard__section-title {
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: 700;
}

.dashboard__todo-list {
  display: grid;
  gap: 12px;
}

.dashboard__todo-item {
  padding: 16px;
  border-radius: 16px;
  background: #f8fafc;
}

.dashboard__todo-item span {
  display: block;
  margin-top: 8px;
  color: $app-sub-text-color;
}

@media (max-width: 1200px) {
  .dashboard__stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .dashboard__stats {
    grid-template-columns: 1fr;
  }
}
</style>
