<template>
  <page-container title="试卷预览" description="查看试卷结构、题目顺序、总分与时长。">
    <div class="app-card preview-header">
      <div>
        <h2>{{ paperDetail?.paperName || '试卷预览' }}</h2>
        <p>{{ paperDetail?.description || '暂无试卷说明' }}</p>
      </div>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="科目">{{ subjectName }}</el-descriptions-item>
        <el-descriptions-item label="时长">{{ paperDetail?.durationMinutes || 0 }} 分钟</el-descriptions-item>
        <el-descriptions-item label="总分">{{ totalScore }} 分</el-descriptions-item>
      </el-descriptions>
    </div>

    <div class="page-grid">
      <div v-for="(item, index) in paperDetail?.questions || []" :key="item.questionId || index" class="app-card preview-question">
        <question-renderer
          :question="{
            questionType: item.questionType,
            title: item.title,
            imageUrls: item.imageUrls,
            options: item.options,
            answers: item.answers,
            analysis: item.analysis,
            questionScore: item.questionScore
          }"
          :index="index"
          :show-analysis="true"
          :model-value="[]"
          readonly
        />
      </div>
    </div>
  </page-container>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

import { previewPaperApi } from '@/api/modules/paper'
import PageContainer from '@/components/common/PageContainer.vue'
import QuestionRenderer from '@/components/common/QuestionRenderer.vue'
import { useSubjects } from '@/hooks/useSubjects'
import type { PaperRecord } from '@/types'

const route = useRoute()
const paperDetail = ref<PaperRecord>()
const { loadSubjects, subjectLabel } = useSubjects()

const subjectName = computed(() => subjectLabel(paperDetail.value?.subjectId))
const totalScore = computed(() => (paperDetail.value?.questions || []).reduce((sum, item) => sum + (item.questionScore || 0), 0))

async function loadData() {
  const result = await previewPaperApi(Number(route.params.id))
  paperDetail.value = result.data
}

onMounted(() => {
  loadSubjects()
  loadData()
})
</script>

<style scoped lang="scss">
.preview-header {
  padding: 20px;
}

.preview-header h2 {
  margin: 0 0 8px;
}

.preview-header p {
  margin: 0 0 20px;
  color: $app-sub-text-color;
}

.preview-question {
  padding: 16px;
}
</style>
