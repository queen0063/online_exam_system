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
          <el-timeline>
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
import { computed, onMounted, ref } from 'vue'
import type { EChartsOption } from 'echarts'

import ChartCard from '@/components/chart/ChartCard.vue'
import AppIcon from '@/components/common/AppIcon.vue'
import PageContainer from '@/components/common/PageContainer.vue'
import { getNoticePageApi } from '@/api/modules/notice'
import { useUserStore } from '@/stores/user'
import { formatDateTime } from '@/utils/format'
import type { NoticeRecord } from '@/types'

const userStore = useUserStore()
const notices = ref<NoticeRecord[]>([])

const cards = computed(() => {
  if (userStore.roleCodes.includes('ADMIN')) {
    return [
      { title: '用户总数', value: '128', foot: '管理员视角总览', icon: 'User', bg: 'rgba(37, 99, 235, 0.12)' },
      { title: '考试总数', value: '24', foot: '本学期累计', icon: 'Calendar', bg: 'rgba(22, 163, 74, 0.12)' },
      { title: '试卷总数', value: '18', foot: '可用题库资源', icon: 'Document', bg: 'rgba(217, 119, 6, 0.12)' },
      { title: '题目总数', value: '368', foot: '覆盖多题型', icon: 'EditPen', bg: 'rgba(139, 92, 246, 0.12)' }
    ]
  }
  if (userStore.roleCodes.includes('TEACHER')) {
    return [
      { title: '我的考试', value: '9', foot: '本周 3 场待发布', icon: 'Notebook', bg: 'rgba(37, 99, 235, 0.12)' },
      { title: '我的试卷', value: '12', foot: '含 4 份随机卷', icon: 'CollectionTag', bg: 'rgba(22, 163, 74, 0.12)' },
      { title: '待阅卷', value: '6', foot: '主观题待批改', icon: 'Finished', bg: 'rgba(217, 119, 6, 0.12)' },
      { title: '平均得分', value: '82.5', foot: '近 5 场考试', icon: 'TrendCharts', bg: 'rgba(139, 92, 246, 0.12)' }
    ]
  }
  return [
    { title: '待参加考试', value: '2', foot: '请关注开始时间', icon: 'AlarmClock', bg: 'rgba(37, 99, 235, 0.12)' },
    { title: '进行中考试', value: '1', foot: '已进入答题阶段', icon: 'Timer', bg: 'rgba(22, 163, 74, 0.12)' },
    { title: '最近成绩', value: '88', foot: '最新一次考试成绩', icon: 'Medal', bg: 'rgba(217, 119, 6, 0.12)' },
    { title: '公告提醒', value: '3', foot: '含 1 条考试通知', icon: 'Bell', bg: 'rgba(139, 92, 246, 0.12)' }
  ]
})

const trendOption = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 36, right: 18, top: 36, bottom: 24 },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  },
  yAxis: { type: 'value' },
  series: [
    {
      type: 'line',
      smooth: true,
      data: userStore.roleCodes.includes('STUDENT') ? [1, 2, 0, 3, 2, 1, 0] : [5, 7, 6, 8, 10, 6, 9],
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
      data: userStore.roleCodes.includes('ADMIN')
        ? [
            { value: 36, name: '题库建设' },
            { value: 24, name: '考试发布' },
            { value: 18, name: '用户管理' },
            { value: 22, name: '公告通知' }
          ]
        : userStore.roleCodes.includes('TEACHER')
          ? [
              { value: 42, name: '待阅卷' },
              { value: 28, name: '试卷维护' },
              { value: 18, name: '考试管理' },
              { value: 12, name: '成绩分析' }
            ]
          : [
              { value: 40, name: '待考试' },
              { value: 22, name: '作答中' },
              { value: 28, name: '成绩已发布' },
              { value: 10, name: '错题复盘' }
            ]
    }
  ]
}))

const todos = computed(() => {
  if (userStore.roleCodes.includes('ADMIN')) {
    return [
      { title: '审核新建题库资源', description: '有 5 道教师提交的新题待确认。' },
      { title: '检查本周公告排期', description: '请确认期中考试通知是否已发布。' }
    ]
  }
  if (userStore.roleCodes.includes('TEACHER')) {
    return [
      { title: '完成主观题批改', description: 'Java 后端期中考试还有 6 份待阅卷。' },
      { title: '补充填空题资源', description: '本周题库填空题占比偏低。' }
    ]
  }
  return [
    { title: '准备参加考试', description: '今天 14:30 有一场“Java 后端期中考试”。' },
    { title: '复盘错题本', description: '最近新增 4 道错题建议复习。' }
  ]
})

async function loadNotices() {
  try {
    const result = await getNoticePageApi({ pageNum: 1, pageSize: 5, noticeStatus: 'PUBLISHED' })
    notices.value = result.data.records
  } catch {
    notices.value = []
  }
}

onMounted(() => {
  loadNotices()
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
