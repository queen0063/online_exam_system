<template>
  <div class="pagination-wrapper">
    <el-pagination
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      :current-page="pageNum"
      :page-size="pageSize"
      :page-sizes="[10, 20, 30, 50]"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
    />
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  total: number
  pageNum: number
  pageSize: number
}>()

const emit = defineEmits<{
  change: [pageNum: number, pageSize: number]
}>()

function handleCurrentChange(pageNum: number) {
  emit('change', pageNum, props.pageSize)
}

function handleSizeChange(pageSize: number) {
  emit('change', 1, pageSize)
}
</script>

<style scoped lang="scss">
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
}
</style>
