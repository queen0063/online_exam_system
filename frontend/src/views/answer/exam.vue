<template>
  <page-container :title="examDetail?.examName || '在线答题'" description="请在考试结束前完成作答并主动提交试卷。">
    <div class="answer-layout">
      <div class="answer-layout__main">
        <div class="app-card exam-banner">
          <div>
            <div class="exam-banner__title">{{ examDetail?.examName }}</div>
            <div class="exam-banner__meta">
              <span>考试时长 {{ examDetail?.durationMinutes || 0 }} 分钟</span>
              <span>结束时间 {{ formatDateTime(examDetail?.endTime) }}</span>
            </div>
          </div>
          <div class="exam-banner__countdown">
            <span>剩余时间</span>
            <strong>{{ secondsToClock(remainSeconds) }}</strong>
          </div>
        </div>

        <question-renderer
          v-if="questions.length"
          :question="currentQuestion"
          :index="currentIndex"
          v-model="currentAnswer"
          :show-analysis="false"
        />

        <div class="app-card answer-actions">
          <el-button @click="goPrev" :disabled="currentIndex === 0">上一题</el-button>
          <el-button @click="goNext" :disabled="currentIndex === questions.length - 1">下一题</el-button>
          <el-button @click="handleSave">保存答题</el-button>
          <el-button type="danger" @click="handleSubmit">提交试卷</el-button>
        </div>
      </div>

      <div class="answer-layout__aside">
        <answer-sheet
          :current-index="currentIndex"
          :questions="questions"
          :answer-map="answerStore.currentAnswers"
          @change="handleQuestionChange"
        />
      </div>
    </div>
  </page-container>
</template>

<script setup lang="ts">
import dayjs from 'dayjs'
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

import { getAnswerExamDetailApi, saveAnswerApi, submitAnswerApi } from '@/api/modules/answer'
import AnswerSheet from '@/components/common/AnswerSheet.vue'
import PageContainer from '@/components/common/PageContainer.vue'
import QuestionRenderer from '@/components/common/QuestionRenderer.vue'
import { useAnswerStore } from '@/stores/answer'
import { formatDateTime, secondsToClock } from '@/utils/format'
import type { AnswerRecord, ExamRecord } from '@/types'

const route = useRoute()
const router = useRouter()
const answerStore = useAnswerStore()

const examId = Number(route.params.examId)
const examDetail = ref<ExamRecord>()
const questions = ref<Array<AnswerRecord & { score?: number }>>([])
const currentIndex = ref(0)
const remainSeconds = ref(0)
let timer: number | undefined

const currentQuestion = computed(() => questions.value[currentIndex.value])
const currentAnswer = computed({
  get: () => answerStore.currentAnswers[currentQuestion.value?.questionId || 0] || currentQuestion.value?.studentAnswers || [],
  set: (value: string[]) => {
    if (!currentQuestion.value) {
      return
    }
    answerStore.setAnswer(currentQuestion.value.questionId, value)
  }
})

function syncCountdown() {
  if (!examDetail.value?.endTime) {
    remainSeconds.value = 0
    return
  }
  remainSeconds.value = Math.max(dayjs(examDetail.value.endTime).diff(dayjs(), 'second'), 0)
  if (remainSeconds.value === 0) {
    window.clearInterval(timer)
    ElMessage.warning('考试时间已到，已触发自动交卷预留逻辑')
  }
}

async function loadData() {
  answerStore.setCurrentExam(examId)
  const examResult = await getAnswerExamDetailApi(examId)
  examDetail.value = examResult.data
  questions.value = examResult.data.questions || []

  syncCountdown()
  timer = window.setInterval(syncCountdown, 1000)
}

function goPrev() {
  currentIndex.value = Math.max(currentIndex.value - 1, 0)
}

function goNext() {
  currentIndex.value = Math.min(currentIndex.value + 1, questions.value.length - 1)
}

function handleQuestionChange(index: number) {
  currentIndex.value = index
}

async function handleSave() {
  const payload = questions.value.map((item) => ({
    questionId: item.questionId,
    answers: answerStore.currentAnswers[item.questionId] || []
  }))
  await saveAnswerApi(examId, { answers: payload })
  ElMessage.success('答题记录已保存')
}

async function handleSubmit() {
  await ElMessageBox.confirm('确认提交试卷吗？提交后将不能继续修改。', '交卷确认', { type: 'warning' })
  await handleSave()
  await submitAnswerApi(examId)
  answerStore.clearExamAnswers(examId)
  ElMessage.success('试卷提交成功')
  await router.replace('/student/my-exams')
}

function handleBeforeUnload(event: BeforeUnloadEvent) {
  event.preventDefault()
  event.returnValue = ''
}

watch(
  () => currentIndex.value,
  () => {
    // 题目切换扩展点，后续可在此接自动保存逻辑。
  }
)

onMounted(() => {
  loadData()
  window.addEventListener('beforeunload', handleBeforeUnload)
})

onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', handleBeforeUnload)
  if (timer) {
    window.clearInterval(timer)
  }
})
</script>

<style scoped lang="scss">
.answer-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 16px;
}

.answer-layout__main {
  display: grid;
  gap: 16px;
}

.exam-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
}

.exam-banner__title {
  font-size: 24px;
  font-weight: 700;
}

.exam-banner__meta {
  display: flex;
  gap: 18px;
  margin-top: 10px;
  color: $app-sub-text-color;
}

.exam-banner__countdown {
  text-align: right;
}

.exam-banner__countdown span {
  display: block;
  color: $app-sub-text-color;
}

.exam-banner__countdown strong {
  display: block;
  margin-top: 6px;
  color: $app-danger;
  font-size: 32px;
}

.answer-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px;
}

@media (max-width: 1080px) {
  .answer-layout {
    grid-template-columns: 1fr;
  }
}
</style>
