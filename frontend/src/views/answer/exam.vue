<template>
  <page-container :title="examDetail?.examName || '在线答题'" description="考试过程中禁止切屏、切换标签页或离开当前窗口，违规将自动交卷。">
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

        <el-alert
          type="error"
          :closable="false"
          title="已开启考试防切屏监测。考试过程中切换标签页、切换窗口或离开当前页面，将被判定为违规并自动交卷。"
        />

        <question-renderer
          v-if="questions.length"
          :question="currentQuestion"
          :index="currentIndex"
          v-model="currentAnswer"
          :show-analysis="false"
        />

        <div class="app-card answer-actions">
          <el-button @click="goPrev" :disabled="currentIndex === 0 || isSubmitting">上一题</el-button>
          <el-button @click="goNext" :disabled="currentIndex === questions.length - 1 || isSubmitting">下一题</el-button>
          <el-button @click="handleSave" :disabled="isSubmitting">保存答题</el-button>
          <el-button type="danger" @click="handleSubmit" :disabled="isSubmitting">提交试卷</el-button>
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
import { onBeforeRouteLeave, useRoute, useRouter } from 'vue-router'
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
const isSubmitting = ref(false)
const examFinished = ref(false)
const forcedSubmitTriggered = ref(false)
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

function clearTimer() {
  if (timer !== undefined) {
    window.clearInterval(timer)
    timer = undefined
  }
}

function detachExamGuard() {
  window.removeEventListener('beforeunload', handleBeforeUnload)
  window.removeEventListener('blur', handleWindowBlur)
  document.removeEventListener('visibilitychange', handleVisibilityChange)
}

function buildAnswerPayload() {
  return questions.value.map((item) => ({
    questionId: item.questionId,
    answers: answerStore.currentAnswers[item.questionId] || []
  }))
}

async function saveCurrentAnswers(showSuccess = true) {
  await saveAnswerApi(examId, { answers: buildAnswerPayload() })
  if (showSuccess) {
    ElMessage.success('答题记录已保存')
  }
}

async function finishExam(successMessage: string, failureMessage: string) {
  if (isSubmitting.value || examFinished.value) {
    return
  }

  isSubmitting.value = true
  try {
    await saveCurrentAnswers(false)
    await submitAnswerApi(examId)
    examFinished.value = true
    clearTimer()
    detachExamGuard()
    answerStore.clearExamAnswers(examId)
    ElMessage.success(successMessage)
    await router.replace('/student/my-exams')
  } catch (error) {
    ElMessage.error(failureMessage)
    isSubmitting.value = false
    throw error
  }
}

async function triggerForcedSubmit(reason: string) {
  if (forcedSubmitTriggered.value || examFinished.value || isSubmitting.value) {
    return
  }
  forcedSubmitTriggered.value = true
  try {
    await finishExam(reason, '自动交卷失败，请勿离开当前页面并联系管理员处理')
  } catch (error) {
    forcedSubmitTriggered.value = false
  }
}

function syncCountdown() {
  const countdownEndTime = examDetail.value?.countdownEndTime || examDetail.value?.endTime
  if (!countdownEndTime) {
    remainSeconds.value = 0
    return
  }
  remainSeconds.value = Math.max(dayjs(countdownEndTime).diff(dayjs(), 'second'), 0)
  if (remainSeconds.value === 0 && !examFinished.value) {
    clearTimer()
    void triggerForcedSubmit('考试时间已到，系统已自动交卷')
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
  await saveCurrentAnswers()
}

async function handleSubmit() {
  await ElMessageBox.confirm('确认提交试卷吗？提交后将不能继续修改。', '交卷确认', { type: 'warning' })
  await finishExam('试卷提交成功', '试卷提交失败，请稍后重试')
}

function handleBeforeUnload(event: BeforeUnloadEvent) {
  if (examFinished.value || isSubmitting.value) {
    return
  }
  event.preventDefault()
  event.returnValue = ''
}

function handleVisibilityChange() {
  if (document.visibilityState === 'hidden') {
    void triggerForcedSubmit('检测到切换标签页，系统已自动交卷')
  }
}

function handleWindowBlur() {
  if (!document.hidden) {
    void triggerForcedSubmit('检测到切换窗口，系统已自动交卷')
  }
}

watch(
  () => currentIndex.value,
  () => {
    // 题目切换扩展点，后续可在此接自动保存逻辑。
  }
)

onMounted(() => {
  void loadData()
  window.addEventListener('beforeunload', handleBeforeUnload)
  window.addEventListener('blur', handleWindowBlur)
  document.addEventListener('visibilitychange', handleVisibilityChange)
})

onBeforeRouteLeave(() => {
  if (examFinished.value || isSubmitting.value) {
    return true
  }
  void triggerForcedSubmit('检测到离开考试页面，系统已自动交卷')
  return false
})

onBeforeUnmount(() => {
  detachExamGuard()
  clearTimer()
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
