import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

import { ANSWER_CACHE_KEY, storage } from '@/utils/storage'

type AnswerMap = Record<number, string[]>

export const useAnswerStore = defineStore('answer', () => {
  const currentExamId = ref<number | null>(null)
  const currentUserId = ref<number | null>(null)
  const answers = ref<Record<string, AnswerMap>>(storage.get<Record<string, AnswerMap>>(ANSWER_CACHE_KEY, {}) || {})

  function buildCacheKey(userId: number | null, examId: number | null) {
    if (!userId || !examId) {
      return ''
    }
    return `${userId}:${examId}`
  }

  const currentAnswers = computed(() => {
    const cacheKey = buildCacheKey(currentUserId.value, currentExamId.value)
    if (!cacheKey) {
      return {}
    }
    return answers.value[cacheKey] || {}
  })

  function setCurrentExam(examId: number, userId: number) {
    currentExamId.value = examId
    currentUserId.value = userId
    const cacheKey = buildCacheKey(userId, examId)
    if (!answers.value[cacheKey]) {
      answers.value[cacheKey] = {}
    }
  }

  function setAnswer(questionId: number, value: string[]) {
    const cacheKey = buildCacheKey(currentUserId.value, currentExamId.value)
    if (!cacheKey) {
      return
    }
    answers.value[cacheKey] = {
      ...answers.value[cacheKey],
      [questionId]: value
    }
    storage.set(ANSWER_CACHE_KEY, answers.value)
  }

  function clearExamAnswers(examId: number, userId: number) {
    delete answers.value[buildCacheKey(userId, examId)]
    storage.set(ANSWER_CACHE_KEY, answers.value)
  }

  return {
    currentExamId,
    currentUserId,
    currentAnswers,
    setCurrentExam,
    setAnswer,
    clearExamAnswers
  }
})
