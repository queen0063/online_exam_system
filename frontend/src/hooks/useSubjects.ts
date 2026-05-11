import { computed, ref } from 'vue'

import { getSubjectListApi } from '@/api/modules/subject'
import type { SubjectRecord } from '@/types'

export function useSubjects() {
  const subjects = ref<SubjectRecord[]>([])
  const subjectOptions = computed(() =>
    subjects.value
      .filter((item) => item.status === 1)
      .map((item) => ({
        label: item.subjectName,
        value: Number(item.id)
      }))
  )

  async function loadSubjects() {
    const result = await getSubjectListApi()
    subjects.value = result.data
  }

  function subjectLabel(subjectId?: number) {
    return subjects.value.find((item) => item.id === subjectId)?.subjectName || '-'
  }

  return {
    subjects,
    subjectOptions,
    loadSubjects,
    subjectLabel
  }
}
