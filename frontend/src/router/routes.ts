import type { AppRouteRecordRaw } from '@/types'
import Layout from '@/layout/index.vue'

export const staticRoutes: AppRouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录',
      hidden: true
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: {
      title: '注册',
      hidden: true
    }
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
    meta: {
      title: '403',
      hidden: true
    }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '404',
      hidden: true
    }
  },
  {
    path: '/',
    redirect: '/login'
  }
]

export const asyncRoutes: AppRouteRecordRaw[] = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: { title: '首页', icon: 'House', affix: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '工作台', icon: 'DataBoard', affix: true }
      }
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/users',
    meta: { title: '系统管理', icon: 'Setting', roles: ['ADMIN'] },
    children: [
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/system/users.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'roles',
        name: 'RoleManagement',
        component: () => import('@/views/system/roles.vue'),
        meta: { title: '角色管理', icon: 'Avatar' }
      },
      {
        path: 'subjects',
        name: 'SubjectManagement',
        component: () => import('@/views/system/subjects.vue'),
        meta: { title: '科目管理', icon: 'Reading' }
      },
      {
        path: 'classes',
        name: 'ClassManagement',
        component: () => import('@/views/system/classes.vue'),
        meta: { title: '班级管理', icon: 'School' }
      }
    ]
  },
  {
    path: '/question',
    component: Layout,
    redirect: '/question/list',
    meta: { title: '题库管理', icon: 'EditPen', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
    children: [
      {
        path: 'list',
        name: 'QuestionList',
        component: () => import('@/views/question/list.vue'),
        meta: { title: '题目列表', icon: 'Tickets', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: 'wrong',
        name: 'WrongQuestions',
        component: () => import('@/views/question/wrong.vue'),
        meta: { title: '错题本', icon: 'Warning', roles: ['STUDENT'] }
      }
    ]
  },
  {
    path: '/paper',
    component: Layout,
    redirect: '/paper/list',
    meta: { title: '试卷管理', icon: 'Document', roles: ['ADMIN', 'TEACHER'] },
    children: [
      {
        path: 'list',
        name: 'PaperList',
        component: () => import('@/views/paper/list.vue'),
        meta: { title: '试卷列表', icon: 'CollectionTag' }
      },
      {
        path: 'preview/:id',
        name: 'PaperPreview',
        component: () => import('@/views/paper/preview.vue'),
        meta: { title: '试卷预览', hidden: true, activeMenu: '/paper/list' }
      }
    ]
  },
  {
    path: '/exam',
    component: Layout,
    redirect: '/exam/list',
    meta: { title: '考试管理', icon: 'Calendar', roles: ['ADMIN', 'TEACHER'] },
    children: [
      {
        path: 'list',
        name: 'ExamList',
        component: () => import('@/views/exam/list.vue'),
        meta: { title: '考试列表', icon: 'Memo' }
      }
    ]
  },
  {
    path: '/student',
    component: Layout,
    redirect: '/student/my-exams',
    meta: { title: '我的考试', icon: 'Reading', roles: ['STUDENT'] },
    children: [
      {
        path: 'my-exams',
        name: 'StudentExamList',
        component: () => import('@/views/student/my-exams.vue'),
        meta: { title: '考试列表', icon: 'Notebook' }
      }
    ]
  },
  {
    path: '/answer',
    component: Layout,
    redirect: '/student/my-exams',
    meta: { title: '在线答题', icon: 'Edit', roles: ['STUDENT'], hidden: true },
    children: [
      {
        path: 'exam/:examId',
        name: 'AnswerExam',
        component: () => import('@/views/answer/exam.vue'),
        meta: { title: '在线答题', hidden: true, activeMenu: '/student/my-exams' }
      }
    ]
  },
  {
    path: '/marking',
    component: Layout,
    redirect: '/marking/pending',
    meta: { title: '阅卷管理', icon: 'Checked', roles: ['ADMIN', 'TEACHER'] },
    children: [
      {
        path: 'pending',
        name: 'MarkingPending',
        component: () => import('@/views/marking/pending.vue'),
        meta: { title: '待阅卷列表', icon: 'Finished' }
      },
      {
        path: 'detail/:examId/:studentId',
        name: 'MarkingDetail',
        component: () => import('@/views/marking/detail.vue'),
        meta: { title: '阅卷详情', hidden: true, activeMenu: '/marking/pending' }
      }
    ]
  },
  {
    path: '/score',
    component: Layout,
    redirect: '/score/list',
    meta: { title: '成绩中心', icon: 'TrendCharts', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
    children: [
      {
        path: 'list',
        name: 'ScoreList',
        component: () => import('@/views/score/list.vue'),
        meta: { title: '成绩列表', icon: 'Histogram', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: 'my',
        name: 'MyScoreList',
        component: () => import('@/views/score/my.vue'),
        meta: { title: '我的成绩', icon: 'Medal', roles: ['STUDENT'] }
      },
      {
        path: 'statistics/:examId',
        name: 'ScoreStatistics',
        component: () => import('@/views/score/statistics.vue'),
        meta: { title: '成绩统计', hidden: true, activeMenu: '/score/list', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: 'detail/:examId',
        name: 'ScoreDetail',
        component: () => import('@/views/score/detail.vue'),
        meta: { title: '成绩详情', hidden: true, activeMenu: '/score/list', roles: ['ADMIN', 'TEACHER', 'STUDENT'] }
      }
    ]
  },
  {
    path: '/notice',
    component: Layout,
    redirect: '/notice/list',
    meta: { title: '公告中心', icon: 'Bell', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
    children: [
      {
        path: 'list',
        name: 'NoticeList',
        component: () => import('@/views/notice/list.vue'),
        meta: { title: '公告列表', icon: 'Notification' }
      },
      {
        path: 'manage',
        name: 'NoticeManage',
        component: () => import('@/views/notice/manage.vue'),
        meta: { title: '公告管理', icon: 'Management', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: 'detail/:id',
        name: 'NoticeDetail',
        component: () => import('@/views/notice/detail.vue'),
        meta: { title: '公告详情', hidden: true, activeMenu: '/notice/list' }
      }
    ]
  },
  {
    path: '/profile',
    component: Layout,
    redirect: '/profile/index',
    meta: { title: '个人中心', icon: 'UserFilled', roles: ['ADMIN', 'TEACHER', 'STUDENT'] },
    children: [
      {
        path: 'index',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', icon: 'UserFilled' }
      }
    ]
  }
]

export const notFoundRoute: AppRouteRecordRaw = {
  path: '/:pathMatch(.*)*',
  redirect: '/404',
  meta: {
    hidden: true,
    title: '404'
  }
}
