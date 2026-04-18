<template>
  <div class="app-card answer-sheet">
    <div class="answer-sheet__header">
      <h4>答题卡</h4>
      <span class="text-secondary">已答 {{ answeredCount }}/{{ questions.length }}</span>
    </div>
    <div class="answer-sheet__grid">
      <button
        v-for="(item, index) in questions"
        :key="item.questionId || item.id || index"
        class="answer-sheet__item"
        :class="{
          'is-active': index === currentIndex,
          'is-answered': isAnswered(item.questionId || item.id)
        }"
        @click="$emit('change', index)"
      >
        {{ index + 1 }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  currentIndex: number
  questions: Array<{ id?: number; questionId?: number }>
  answerMap: Record<number, string[]>
}>()

defineEmits<{
  change: [index: number]
}>()

const answeredCount = computed(() =>
  props.questions.filter((item) => {
    const key = item.questionId || item.id
    return key ? isAnswered(key) : false
  }).length
)

function isAnswered(questionId?: number) {
  if (!questionId) {
    return false
  }
  return !!props.answerMap[questionId]?.find((item) => item && item.trim())
}
</script>

<style scoped lang="scss">
.answer-sheet {
  padding: 18px;
}

.answer-sheet__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.answer-sheet__header h4 {
  margin: 0;
}

.answer-sheet__grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
}

.answer-sheet__item {
  height: 40px;
  border: 1px solid $app-border-color;
  border-radius: 12px;
  background: #fff;
  cursor: pointer;
  transition: 0.2s ease;
}

.answer-sheet__item.is-active {
  border-color: $app-primary;
  background: rgba(37, 99, 235, 0.08);
}

.answer-sheet__item.is-answered {
  background: rgba(22, 163, 74, 0.12);
  color: $app-success;
  border-color: rgba(22, 163, 74, 0.2);
}
</style>
