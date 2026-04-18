<template>
  <div ref="chartRef" class="base-chart"></div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import type { EChartsOption } from 'echarts'
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import {
  DatasetComponent,
  GridComponent,
  LegendComponent,
  TitleComponent,
  ToolboxComponent,
  TooltipComponent
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { use } from 'echarts/core'
import { init } from 'echarts/core'
import type { EChartsType } from 'echarts/core'

use([
  BarChart,
  LineChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  DatasetComponent,
  ToolboxComponent,
  CanvasRenderer
])

const props = defineProps<{
  option: EChartsOption
}>()

const chartRef = ref<HTMLElement>()
let chartInstance: EChartsType | null = null

function renderChart() {
  if (!chartRef.value) {
    return
  }
  if (!chartInstance) {
    chartInstance = init(chartRef.value)
  }
  chartInstance?.setOption(props.option)
}

function resizeChart() {
  chartInstance?.resize()
}

onMounted(() => {
  renderChart()
  window.addEventListener('resize', resizeChart)
})

watch(
  () => props.option,
  () => renderChart(),
  { deep: true }
)

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeChart)
  chartInstance?.dispose()
})
</script>

<style scoped lang="scss">
.base-chart {
  width: 100%;
  height: 320px;
}
</style>
