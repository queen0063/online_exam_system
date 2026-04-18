export const ROLE_OPTIONS = [
  { label: '管理员', value: 'ADMIN' },
  { label: '教师', value: 'TEACHER' },
  { label: '学生', value: 'STUDENT' }
]

export const SUBJECT_OPTIONS = [
  { label: 'Java程序设计', value: 1 },
  { label: '数据库原理', value: 2 },
  { label: '软件工程', value: 3 }
]

export const QUESTION_TYPE_OPTIONS = [
  { label: '单选题', value: 'SINGLE_CHOICE' },
  { label: '多选题', value: 'MULTIPLE_CHOICE' },
  { label: '判断题', value: 'TRUE_FALSE' },
  { label: '填空题', value: 'FILL_BLANK' },
  { label: '简答题', value: 'SHORT_ANSWER' }
]

export const DIFFICULTY_OPTIONS = [
  { label: '简单', value: 'EASY' },
  { label: '中等', value: 'MEDIUM' },
  { label: '困难', value: 'HARD' }
]

export const PAPER_GENERATE_OPTIONS = [
  { label: '手动组卷', value: 'MANUAL' },
  { label: '随机组卷', value: 'RANDOM' }
]

export const EXAM_STATUS_OPTIONS = [
  { label: '草稿', value: 'DRAFT' },
  { label: '未开始', value: 'NOT_STARTED' },
  { label: '进行中', value: 'IN_PROGRESS' },
  { label: '已结束', value: 'ENDED' },
  { label: '已阅卷', value: 'GRADED' },
  { label: '已发布成绩', value: 'RESULT_PUBLISHED' }
]

export const NOTICE_STATUS_OPTIONS = [
  { label: '草稿', value: 'DRAFT' },
  { label: '已发布', value: 'PUBLISHED' }
]
