import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

import { ANSWER_CACHE_KEY, storage } from '@/utils/storage'

type AnswerMap = Record<number, string[]>

export const useAnswerStore = defineStore('answer', () => {
  const currentExamId = ref<number | null>(null)
  const answers = ref<Record<number, AnswerMap>>(storage.get<Record<number, AnswerMap>>(ANSWER_CACHE_KEY, {}) || {})

  const currentAnswers = computed(() => {
    if (!currentExamId.value) {
      return {}
    }
    return answers.value[currentExamId.value] || {}
  })

  function setCurrentExam(examId: number) {
    currentExamId.value = examId
    if (!answers.value[examId]) {
      answers.value[examId] = {}
    }
  }

  function setAnswer(questionId: number, value: string[]) {
    if (!currentExamId.value) {
      return
    }
    answers.value[currentExamId.value] = {
      ...answers.value[currentExamId.value],
      [questionId]: value
    }
    storage.set(ANSWER_CACHE_KEY, answers.value)
  }

  function clearExamAnswers(examId: number) {
    delete answers.value[examId]
    storage.set(ANSWER_CACHE_KEY, answers.value)
  }

  return {
    currentExamId,
    currentAnswers,
    setCurrentExam,
    setAnswer,
    clearExamAnswers
  }
})
