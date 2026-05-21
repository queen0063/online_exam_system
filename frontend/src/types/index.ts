import type { RouteRecordRaw } from 'vue-router'

export interface ApiResult<T = unknown> {
  code: number
  message: string
  data: T
  timestamp: string
}

export interface PageResult<T> {
  records: T[]
  total: number
  pageNum: number
  pageSize: number
}

export interface LoginPayload {
  username: string
  password: string
}

export interface RegisterPayload {
  username: string
  password: string
  realName: string
  roleCode: 'TEACHER' | 'STUDENT'
  studentNo?: string
  classId?: number
  phone?: string
  email?: string
}

export interface RoleItem {
  id: number
  roleCode: string
  roleName: string
  status?: number
}

export interface UserInfo {
  userId: number
  username: string
  realName: string
  phone?: string
  email?: string
  roleCodes: string[]
  studentNo?: string
  classId?: number
  className?: string
  gradeName?: string
  teacherClasses?: ClassInfoRecord[]
}

export interface LoginResult {
  token: string
  userInfo: UserInfo
}

export interface UserRecord {
  id: number
  username: string
  studentNo?: string
  realName: string
  phone?: string
  email?: string
  classId?: number
  status: number
  roles: RoleItem[]
  createTime?: string
}

export interface ClassInfoRecord {
  id?: number
  classCode: string
  className: string
  gradeName?: string
  teacherId?: number
  status: number
  createTime?: string
}

export interface SubjectRecord {
  id?: number
  subjectCode: string
  subjectName: string
  description?: string
  status: number
  createTime?: string
}

export interface QuestionRecord {
  id?: number
  subjectId: number
  questionType: string
  title: string
  imageUrls?: string[]
  options?: string[]
  answers: string[]
  studentAnswers?: string[]
  questionScore?: number
  actualScore?: number
  analysis?: string
  score: number
  difficulty: string
  knowledgePoint: string
  creatorId?: number
  createTime?: string
  submitTime?: string
}

export interface PaperQuestionRecord {
  id?: number
  questionId: number
  questionScore: number
  sortNo: number
  title?: string
  questionType?: string
  imageUrls?: string[]
  options?: string[]
  answers?: string[]
  analysis?: string
}

export interface RandomRuleRecord {
  subjectId: number
  questionType: string
  difficulty: string
  knowledgePoint?: string
  questionCount: number
  questionScore: number
}

export interface PaperRecord {
  id?: number
  paperName: string
  subjectId: number
  generateType: string
  totalScore?: number
  durationMinutes: number
  creatorId?: number
  description?: string
  questions?: PaperQuestionRecord[]
  randomRules?: RandomRuleRecord[]
  createTime?: string
}

export interface ExamRecord {
  id?: number
  examName: string
  paperId: number
  subjectId: number
  creatorId?: number
  startTime: string
  endTime: string
  countdownEndTime?: string
  durationMinutes: number
  passScore: number
  maxSwitchCount?: number
  status: string
  resultPublished?: number
  answerStatus?: string
  studentIds?: number[]
}

export interface AnswerRecord {
  id: number
  questionId: number
  questionType: string
  title: string
  imageUrls?: string[]
  options?: string[]
  standardAnswers?: string[]
  studentAnswers?: string[]
  questionScore: number
  actualScore?: number
  teacherComment?: string
  markingStatus?: string
  submitTime?: string
}

export interface MarkingRecord {
  examId: number
  examName: string
  studentId: number
  studentName: string
  objectiveScore?: number
  subjectiveScore?: number
  totalScore?: number
  scoreStatus?: string
  answers: AnswerRecord[]
}

export interface ScoreRecord {
  id?: number
  examId: number
  examName: string
  studentId?: number
  studentName?: string
  objectiveScore?: number
  subjectiveScore?: number
  totalScore?: number
  passFlag?: number
  excellentFlag?: number
  rankNo?: number
  scoreStatus?: string
  publishTime?: string
}

export interface ScoreStatistics {
  examId: number
  totalCount: number
  passCount: number
  excellentCount: number
  passRate: number
  excellentRate: number
  scoreSegments: Array<{
    segmentName: string
    count: number
  }>
}

export interface NoticeRecord {
  id?: number
  title: string
  content: string
  noticeStatus: string
  publisherId?: number
  createTime?: string
}

export interface RouteMetaExtended {
  title: string
  icon?: string
  roles?: string[]
  hidden?: boolean
  activeMenu?: string
  affix?: boolean
  keepAlive?: boolean
}

export type AppRouteRecordRaw = Omit<RouteRecordRaw, 'meta' | 'children'> & {
  meta?: RouteMetaExtended
  children?: AppRouteRecordRaw[]
}
