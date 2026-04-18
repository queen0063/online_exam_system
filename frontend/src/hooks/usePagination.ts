import { reactive } from 'vue'

export function usePagination() {
  const pagination = reactive({
    pageNum: 1,
    pageSize: 10,
    total: 0
  })

  function updatePagination(total = 0, pageNum = pagination.pageNum, pageSize = pagination.pageSize) {
    pagination.total = total
    pagination.pageNum = pageNum
    pagination.pageSize = pageSize
  }

  function resetPagination() {
    pagination.pageNum = 1
    pagination.pageSize = 10
    pagination.total = 0
  }

  return {
    pagination,
    updatePagination,
    resetPagination
  }
}
