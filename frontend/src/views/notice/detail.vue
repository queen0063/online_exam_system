<template>
  <page-container title="公告详情" description="查看公告正文与发布时间。">
    <div class="app-card detail-card">
      <div class="detail-card__header">
        <h1>{{ detail?.title }}</h1>
        <status-tag :value="detail?.noticeStatus" :map="{ DRAFT: '草稿', PUBLISHED: '已发布' }" />
      </div>
      <div class="detail-card__meta">发布时间：{{ formatDateTime(detail?.createTime) }}</div>
      <div class="detail-card__content">{{ detail?.content }}</div>
    </div>
  </page-container>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

import { getNoticeDetailApi } from '@/api/modules/notice'
import PageContainer from '@/components/common/PageContainer.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import { formatDateTime } from '@/utils/format'
import type { NoticeRecord } from '@/types'

const route = useRoute()
const detail = ref<NoticeRecord>()

async function loadData() {
  const result = await getNoticeDetailApi(Number(route.params.id))
  detail.value = result.data
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.detail-card {
  padding: 28px;
}

.detail-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.detail-card__header h1 {
  margin: 0;
}

.detail-card__meta {
  margin-top: 10px;
  color: $app-sub-text-color;
}

.detail-card__content {
  margin-top: 24px;
  line-height: 1.9;
  white-space: pre-wrap;
}
</style>
