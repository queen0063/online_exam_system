# Online Exam System Backend

基于 `Spring Boot 4 + Spring Security + JWT + MyBatis + MySQL` 的在线考试系统后端，覆盖认证授权、用户角色、题库、试卷、考试、答题、阅卷、成绩、公告、日志审计等模块。

## 技术栈

- Java 21
- Spring Boot 4.0.5
- Spring Security
- JWT
- MyBatis XML
- MySQL 8.x
- H2（测试环境）

## 目录结构

```text
src/main/java/com/exam
├── OnlineExamApplication
├── common
├── config
├── security
├── controller
├── service
│   └── impl
├── mapper
├── entity
├── dto
├── vo
├── convert
└── aspect
```

## 数据库初始化

1. 创建数据库：

```sql
create database online_exam_system default character set utf8mb4 collate utf8mb4_general_ci;
```

2. 执行脚本：

```bash
mysql -uroot -p online_exam_system < src/main/resources/schema.sql
mysql -uroot -p online_exam_system < src/main/resources/data.sql
```

3. 修改配置文件：

文件位置：`src/main/resources/application.yml`

修改以下参数：

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`

## 启动项目

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## 默认账号

- 管理员：`admin / Admin@123`
- 教师：`teacher / Admin@123`
- 学生：`student / Admin@123`

说明：`data.sql` 中已写入 BCrypt 密码密文，对应明文统一为 `Admin@123`。

## 登录接口测试

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "Admin@123"
}
```

登录成功后，将返回 `token`，后续请求在 Header 中携带：

```http
Authorization: Bearer {token}
```

## 关键接口说明

- `/api/auth/**`：认证接口
- `/api/users/**`：用户管理
- `/api/roles/**`：角色查询
- `/api/questions/**`：题库管理
- `/api/papers/**`：试卷管理
- `/api/exams/**`：考试管理
- `/api/answers/**`：在线答题
- `/api/marking/**`：阅卷管理
- `/api/scores/**`：成绩管理
- `/api/notices/**`：公告管理

## 开发建议

- 导出能力可接入 EasyExcel。
- 可增加 Redis 存储 JWT 黑名单与登录态。
- 可为操作日志增加 TraceId、耗时、异常堆栈。
- 可接入 OpenAPI/Swagger 生成在线接口文档。
