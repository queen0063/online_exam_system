<template>
  <el-tag :type="tagType" effect="light" round>{{ label }}</el-tag>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  value?: string | number
  map?: Record<string, string>
}>()

const label = computed(() => props.map?.[String(props.value)] || String(props.value ?? '-'))
const tagType = computed(() => {
  const value = String(props.value)
  if (['1', 'PUBLISHED', 'IN_PROGRESS', 'GRADED', 'RESULT_PUBLISHED'].includes(value)) {
    return 'success'
  }
  if (['0', 'DRAFT', 'NOT_STARTED'].includes(value)) {
    return 'info'
  }
  if (['ENDED', 'PENDING_MARK'].includes(value)) {
    return 'warning'
  }
  if (['DISABLED'].includes(value)) {
    return 'danger'
  }
  return 'primary'
})
</script>
