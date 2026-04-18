<template>
  <page-container title="错题本" description="学生端查看历史错题，支持按题型与知识点复盘。">
    <div class="page-grid">
      <div v-if="loading" class="app-card page-block">
        <el-skeleton :rows="5" animated />
      </div>
      <template v-else>
        <div v-for="(item, index) in questionList" :key="item.id" class="app-card page-block">
          <question-renderer
            :question="{ ...item, questionScore: item.questionScore || item.score, standardAnswers: item.answers }"
            :index="index"
            :show-analysis="true"
            :model-value="item.studentAnswers || []"
            readonly
          />
        </div>
        <div v-if="questionList.length === 0" class="app-card page-block">
          <el-empty description="当前没有错题记录" />
        </div>
      </template>
    </div>
  </page-container>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { getWrongQuestionApi } from '@/api/modules/question'
import PageContainer from '@/components/common/PageContainer.vue'
import QuestionRenderer from '@/components/common/QuestionRenderer.vue'
import type { QuestionRecord } from '@/types'

const loading = ref(false)
const questionList = ref<QuestionRecord[]>([])

async function loadData() {
  loading.value = true
  try {
    const result = await getWrongQuestionApi()
    questionList.value = result.data
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.page-block {
  padding: 16px;
}
</style>
