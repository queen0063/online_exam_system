# online-exam-system-frontend

基于 `Vue 3 + Vite + TypeScript + Pinia + Vue Router + Element Plus + Axios + ECharts` 构建的在线考试系统前端项目。

## 功能概览

- 登录认证与 Token 持久化
- 基于角色的动态路由与菜单权限控制
- 管理端布局：顶部导航、侧边菜单、面包屑、标签页
- 首页仪表盘
- 用户管理、角色管理
- 题库管理、试卷管理、考试管理
- 学生考试列表、在线答题页
- 阅卷管理、成绩统计
- 公告中心、个人中心

## 目录说明

```text
frontend
├── package.json
├── vite.config.ts
├── tsconfig.json
├── src
│   ├── api
│   │   └── modules
│   ├── assets
│   │   └── styles
│   ├── components
│   │   ├── chart
│   │   ├── common
│   │   ├── form
│   │   ├── layout
│   │   └── table
│   ├── hooks
│   ├── layout
│   ├── router
│   ├── stores
│   ├── types
│   ├── utils
│   └── views
└── README.md
```

## 启动方式

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发环境

```bash
npm run dev
```

默认访问地址：

```text
http://localhost:5173
```

### 3. 生产构建

```bash
npm run build
```

## 后端联调说明

当前 Vite 代理配置位于 [vite.config.ts](D:/Project/JavaProjects/online_exam_system/frontend/vite.config.ts)：

- 前端开发端口：`5173`
- 接口代理前缀：`/api`
- 默认代理目标：`http://localhost:8080`

如果后端地址变化，请修改：

```ts
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080'
    }
  }
}
```

## 默认账号

当前与后端初始化数据一致的登录账号如下：

- `admin / Admin@123`
- `teacher / Admin@123`
- `student / Admin@123`

如果你后续修改了后端初始化数据，请同步调整 [login/index.vue](D:/Project/JavaProjects/online_exam_system/frontend/src/views/login/index.vue) 中的默认密码与快捷填充逻辑。

## 已对接接口

- `/api/auth/**`
- `/api/users/**`
- `/api/roles/**`
- `/api/questions/**`
- `/api/papers/**`
- `/api/exams/**`
- `/api/answers/**`
- `/api/marking/**`
- `/api/scores/**`
- `/api/notices/**`

## 当前预留说明

- 角色新增、编辑、删除：由于后端当前仅开放角色查询接口，前端页面以本地演示数据形式预留。
- 学生考试题目详情：后端当前未提供学生可直接获取完整试卷题目内容的公开接口，在线答题页已保留真实答题保存与提交接口，同时用演示题目做前端闭环展示。
- 头像上传、消息通知、自动交卷与自动保存增强逻辑：已预留扩展位，可在后续版本继续接入。
