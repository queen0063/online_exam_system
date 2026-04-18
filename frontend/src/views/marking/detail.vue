<template>
  <page-container title="阅卷详情" description="支持查看学生答案、参考答案、解析并录入主观题得分。">
    <div class="app-card detail-header">
      <el-descriptions :column="3" border>
        <el-descriptions-item label="考试名称">{{ detail?.examName }}</el-descriptions-item>
        <el-descriptions-item label="学生">{{ detail?.studentName }}</el-descriptions-item>
        <el-descriptions-item label="当前总分">{{ detail?.totalScore ?? 0 }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <div class="page-grid">
      <div v-for="(item, index) in detail?.answers || []" :key="item.id" class="app-card answer-card">
        <question-renderer
          :question="{ ...item, answers: item.standardAnswers }"
          :index="index"
          :show-analysis="true"
          :model-value="item.studentAnswers || []"
          readonly
        />
        <div class="answer-card__footer">
          <div class="answer-card__score">
            <span>当前得分</span>
            <el-input-number v-model="scoreMap[item.id]" :min="0" :max="item.questionScore" :disabled="!canMark(item)" />
          </div>
          <el-input v-model="commentMap[item.id]" type="textarea" :rows="2" placeholder="请输入教师评语" :disabled="!canMark(item)" />
          <div class="answer-card__actions">
            <el-tag v-if="!canMark(item)" type="info">客观题已自动判分</el-tag>
            <el-button v-else type="primary" @click="handleMark(item.id)">保存评分</el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="app-card footer-bar">
      <el-button @click="$router.back()">返回列表</el-button>
      <el-button type="primary" @click="handleFinish">完成阅卷</el-button>
    </div>
  </page-container>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

import { finishMarkingApi, getMarkingDetailApi, markQuestionApi } from '@/api/modules/marking'
import PageContainer from '@/components/common/PageContainer.vue'
import QuestionRenderer from '@/components/common/QuestionRenderer.vue'
import type { AnswerRecord, MarkingRecord } from '@/types'

const route = useRoute()
const router = useRouter()
const detail = ref<MarkingRecord>()
const scoreMap = reactive<Record<number, number>>({})
const commentMap = reactive<Record<number, string>>({})

async function loadData() {
  const result = await getMarkingDetailApi(Number(route.params.examId), Number(route.params.studentId))
  detail.value = result.data
  ;(detail.value.answers || []).forEach((item) => {
    scoreMap[item.id] = item.actualScore || 0
    commentMap[item.id] = item.teacherComment || ''
  })
}

async function handleMark(answerId: number) {
  await markQuestionApi(answerId, {
    actualScore: scoreMap[answerId] || 0,
    teacherComment: commentMap[answerId]
  })
  ElMessage.success('单题评分已保存')
}

function canMark(item: AnswerRecord) {
  return item.questionType === 'SHORT_ANSWER'
}

async function handleFinish() {
  await finishMarkingApi(Number(route.params.examId), Number(route.params.studentId))
  ElMessage.success('阅卷已完成')
  await router.replace('/marking/pending')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.detail-header {
  padding: 18px;
}

.answer-card {
  padding: 16px;
}

.answer-card__footer {
  display: grid;
  gap: 12px;
  margin-top: 16px;
}

.answer-card__score {
  display: flex;
  align-items: center;
  gap: 12px;
}

.answer-card__actions {
  display: flex;
  justify-content: flex-end;
}

.footer-bar {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px;
}
</style>
