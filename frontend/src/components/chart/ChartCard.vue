<template>
  <div class="app-card chart-card">
    <div class="chart-card__header">
      <div>
        <div class="chart-card__title">{{ title }}</div>
        <div v-if="description" class="chart-card__description">{{ description }}</div>
      </div>
      <slot name="extra" />
    </div>
    <base-chart :option="option" />
  </div>
</template>

<script setup lang="ts">
import type { EChartsOption } from 'echarts'
import BaseChart from './BaseChart.vue'

defineProps<{
  title: string
  description?: string
  option: EChartsOption
}>()
</script>

<style scoped lang="scss">
.chart-card {
  padding: 20px;
}

.chart-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.chart-card__title {
  font-size: 16px;
  font-weight: 700;
}

.chart-card__description {
  margin-top: 6px;
  color: $app-sub-text-color;
  font-size: 13px;
}
</style>
