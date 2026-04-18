<template>
  <div class="question-renderer">
    <div class="question-renderer__header">
      <span class="question-renderer__type">{{ typeLabel }}</span>
      <h3>{{ index + 1 }}. {{ question.title }}</h3>
      <span class="question-renderer__score">{{ question.questionScore || question.score }} 分</span>
    </div>

    <div v-if="question.questionType === 'SINGLE_CHOICE'" class="question-renderer__body">
      <el-radio-group :model-value="singleValue" :disabled="readonly" @update:model-value="onSingleChange">
        <el-radio v-for="option in question.options || []" :key="option" :label="option">{{ option }}</el-radio>
      </el-radio-group>
    </div>

    <div v-else-if="question.questionType === 'MULTIPLE_CHOICE'" class="question-renderer__body">
      <el-checkbox-group :model-value="modelValue" :disabled="readonly" @change="onMultipleChange">
        <el-checkbox v-for="option in question.options || []" :key="option" :label="option">{{ option }}</el-checkbox>
      </el-checkbox-group>
    </div>

    <div v-else-if="question.questionType === 'TRUE_FALSE'" class="question-renderer__body">
      <el-radio-group :model-value="singleValue" :disabled="readonly" @update:model-value="onSingleChange">
        <el-radio label="true">正确</el-radio>
        <el-radio label="false">错误</el-radio>
      </el-radio-group>
    </div>

    <div v-else-if="question.questionType === 'FILL_BLANK'" class="question-renderer__body">
      <el-input :model-value="singleValue" :readonly="readonly" placeholder="请输入答案" @input="onInputChange" />
    </div>

    <div v-else class="question-renderer__body">
      <el-input
        :model-value="singleValue"
        type="textarea"
        :rows="6"
        :readonly="readonly"
        placeholder="请输入作答内容"
        @input="onInputChange"
      />
    </div>

    <div v-if="showAnalysis" class="question-renderer__analysis">
      <p v-if="showStudentAnswer"><strong>我的作答：</strong>{{ displayStudentAnswers }}</p>
      <p v-if="showScore"><strong>作答得分：</strong>{{ question.actualScore ?? 0 }}/{{ question.questionScore || question.score || 0 }}</p>
      <p><strong>参考答案：</strong>{{ (question.standardAnswers || question.answers || []).join('；') || '-' }}</p>
      <p><strong>题目解析：</strong>{{ question.analysis || '-' }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

import { QUESTION_TYPE_OPTIONS } from '@/utils/dicts'
import type { AnswerRecord, QuestionRecord } from '@/types'

const props = defineProps<{
  question: Partial<QuestionRecord & AnswerRecord> & { questionScore?: number }
  modelValue?: string[]
  index: number
  showAnalysis?: boolean
  readonly?: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: string[]]
}>()

const typeLabel = computed(
  () => QUESTION_TYPE_OPTIONS.find((item) => item.value === props.question.questionType)?.label || props.question.questionType
)

const singleValue = computed(() => props.modelValue?.[0] || '')
const displayStudentAnswers = computed(() => (props.modelValue || []).join('；') || '未作答')
const showStudentAnswer = computed(
  () =>
    props.question.studentAnswers !== undefined ||
    props.question.actualScore !== undefined ||
    (props.modelValue?.length ?? 0) > 0
)
const showScore = computed(() => props.question.actualScore !== undefined && props.question.actualScore !== null)

function onSingleChange(value: string | number | boolean) {
  emit('update:modelValue', [String(value)])
}

function onMultipleChange(value: Array<string | number>) {
  emit(
    'update:modelValue',
    value.map((item) => String(item))
  )
}

function onInputChange(value: string) {
  emit('update:modelValue', [value])
}
</script>

<style scoped lang="scss">
.question-renderer {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 24px;
  border-radius: 18px;
  background: #fff;
  border: 1px solid $app-border-color;
}

.question-renderer__header {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.question-renderer__header h3 {
  margin: 0;
  flex: 1;
  font-size: 18px;
}

.question-renderer__type {
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.12);
  color: $app-primary;
  font-size: 12px;
}

.question-renderer__score {
  color: $app-warning;
  font-weight: 700;
}

.question-renderer__analysis {
  padding: 16px;
  border-radius: 14px;
  background: #f8fafc;
}

.question-renderer__analysis p {
  margin: 0 0 8px;
}

.question-renderer__analysis p:last-child {
  margin-bottom: 0;
}
</style>
