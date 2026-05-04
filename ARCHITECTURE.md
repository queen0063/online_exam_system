# 在线考试系统架构图

## 总体说明

该项目是一个前后端分离的在线考试系统：

- 前端位于 `frontend/`，基于 Vue 3、TypeScript、Pinia、Vue Router、Element Plus、Axios。
- 后端位于 `src/main/java/com/exam/`，基于 Spring Boot、Spring Security、JWT、MyBatis、MySQL。
- 开发环境下，前端通过 Vite 代理将 `/api` 请求转发到 `http://localhost:8080`。
- 后端采用典型单体分层架构：`Controller -> Service -> Mapper -> MySQL`，并通过 JWT、安全过滤器、AOP 日志、全局异常处理做横切治理。

## Mermaid 架构图

```mermaid
flowchart LR
    U[用户<br/>管理员 / 教师 / 学生]

    subgraph FE[前端应用 frontend]
        V[Vue 3 + TypeScript]
        R[Vue Router<br/>静态路由 + 动态权限路由]
        S[Pinia Store<br/>user / app / tabs / answer]
        PAGES[页面与组件<br/>dashboard / system / question / paper / exam / answer / marking / score / notice / profile]
        API[Axios 请求层<br/>utils/request.ts + api/modules/*]

        V --> R
        V --> S
        V --> PAGES
        PAGES --> API
        S --> API
    end

    DEV[Vite Dev Server<br/>/api 代理到 8080]

    subgraph BE[后端单体应用 Spring Boot]
        SEC[安全层<br/>SecurityConfig<br/>JWT Filter<br/>Method Security]
        CTRL[Controller 层<br/>Auth / User / Role / Question / Paper / Exam / Answer / Marking / Score / Notice]
        SRV[Service 层<br/>认证授权 / 用户角色 / 题库 / 试卷 / 考试 / 答题 / 自动判分 / 阅卷 / 成绩 / 公告]
        AOP[AOP 与通用层<br/>OperationLogAspect<br/>GlobalExceptionHandler<br/>Result / PageResult]
        MAP[Mapper 层<br/>MyBatis Mapper + XML]
        CFG[配置层<br/>CorsConfig / MybatisConfig / AsyncConfig]
    end

    subgraph DB[数据存储 MySQL]
        AUTH[(用户与权限表<br/>sys_user / sys_role / sys_user_role)]
        BANK[(题库与试卷表<br/>question / paper / paper_question / subject)]
        EXAM[(考试业务表<br/>exam / exam_student / student_answer / exam_score)]
        AUDIT[(公告与审计表<br/>notice / login_log / operation_log)]
    end

    subgraph CROSS[横切与异步能力]
        JWT[JwtTokenProvider<br/>JwtAuthenticationFilter]
        LOG1[LoginLogService]
        LOG2[OperationLogService]
        ASYNC[auditExecutor 线程池]
    end

    U --> V
    API -->|/api| DEV
    DEV --> SEC
    SEC --> JWT
    SEC --> CTRL
    CTRL --> SRV
    AOP -.拦截控制器与业务调用.-> CTRL
    AOP -.记录操作日志.-> LOG2
    SRV --> MAP
    CFG -.支撑.-> SEC
    CFG -.支撑.-> MAP
    SRV --> LOG1
    LOG1 --> ASYNC
    LOG2 --> ASYNC
    MAP --> AUTH
    MAP --> BANK
    MAP --> EXAM
    MAP --> AUDIT
    ASYNC --> AUDIT
```

## 架构解读

- 前端负责页面渲染、权限路由控制、状态管理和接口调用，登录后通过 JWT 携带身份信息访问后端。
- 后端安全链路由 Spring Security 和 JWT 过滤器负责，请求通过鉴权后进入各业务 Controller。
- 业务核心集中在 Service 层，其中答题、自动判分、人工阅卷、成绩统计是考试主流程的核心。
- 数据访问由 MyBatis `Mapper + XML` 完成，底层统一落到 MySQL。
- 登录日志和操作日志采用异步线程池写库，避免影响主业务请求时延。
