<template>
  <el-table v-bind="$attrs" :data="data" class="common-table">
    <slot />
    <template #empty>
      <el-empty description="暂无数据" />
    </template>
  </el-table>
</template>

<script setup lang="ts" generic="T">
defineProps<{
  data: T[]
}>()
</script>

<style scoped lang="scss">
.common-table {
  width: 100%;
}
</style>
