<template>
  <page-container title="公告列表" description="面向管理员、教师、学生展示最新公告和考试通知。">
    <query-bar>
      <el-form :model="queryForm" inline>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="公告标题关键词" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.noticeStatus" clearable placeholder="全部状态" style="width: 160px">
            <el-option v-for="item in NOTICE_STATUS_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #actions>
        <el-button @click="resetQuery">重置</el-button>
        <el-button type="primary" @click="loadData">查询</el-button>
      </template>
    </query-bar>

    <div class="notice-grid">
      <div v-for="item in noticeList" :key="item.id" class="app-card notice-card">
        <div class="notice-card__header">
          <h3>{{ item.title }}</h3>
          <status-tag :value="item.noticeStatus" :map="{ DRAFT: '草稿', PUBLISHED: '已发布' }" />
        </div>
        <p>{{ item.content.slice(0, 120) }}{{ item.content.length > 120 ? '...' : '' }}</p>
        <div class="notice-card__footer">
          <span>{{ formatDateTime(item.createTime) }}</span>
          <el-button type="primary" link @click="goDetail(item.id!)">查看详情</el-button>
        </div>
      </div>
    </div>

    <common-pagination
      :total="pagination.total"
      :page-num="pagination.pageNum"
      :page-size="pagination.pageSize"
      @change="handlePageChange"
    />
  </page-container>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import { getNoticePageApi } from '@/api/modules/notice'
import PageContainer from '@/components/common/PageContainer.vue'
import StatusTag from '@/components/common/StatusTag.vue'
import QueryBar from '@/components/form/QueryBar.vue'
import CommonPagination from '@/components/table/CommonPagination.vue'
import { usePagination } from '@/hooks/usePagination'
import { NOTICE_STATUS_OPTIONS } from '@/utils/dicts'
import { formatDateTime } from '@/utils/format'
import type { NoticeRecord } from '@/types'

const router = useRouter()
const noticeList = ref<NoticeRecord[]>([])
const { pagination, updatePagination } = usePagination()
const queryForm = reactive({
  keyword: '',
  noticeStatus: ''
})

async function loadData() {
  const result = await getNoticePageApi({
    pageNum: pagination.pageNum,
    pageSize: pagination.pageSize,
    ...queryForm
  })
  noticeList.value = result.data.records
  updatePagination(result.data.total, result.data.pageNum, result.data.pageSize)
}

function resetQuery() {
  queryForm.keyword = ''
  queryForm.noticeStatus = ''
  pagination.pageNum = 1
  loadData()
}

function goDetail(id: number) {
  router.push(`/notice/detail/${id}`)
}

function handlePageChange(pageNum: number, pageSize: number) {
  pagination.pageNum = pageNum
  pagination.pageSize = pageSize
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.notice-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 16px;
}

.notice-card {
  padding: 20px;
}

.notice-card__header,
.notice-card__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.notice-card__header h3 {
  margin: 0;
}

.notice-card p {
  color: $app-sub-text-color;
  line-height: 1.75;
  min-height: 76px;
}

.notice-card__footer {
  color: $app-sub-text-color;
}
</style>
