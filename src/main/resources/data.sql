INSERT IGNORE INTO sys_role(id, role_code, role_name, status, create_time, update_time, deleted)
VALUES (1, 'ADMIN', '管理员', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (2, 'TEACHER', '教师', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (3, 'STUDENT', '学生', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT IGNORE INTO subject(id, subject_code, subject_name, description, status, create_time, update_time, deleted)
VALUES (1, 'JAVA', 'Java程序设计', 'Java 后端基础课程', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT IGNORE INTO class_info(id, class_code, class_name, grade_name, teacher_id, status, create_time, update_time, deleted)
VALUES (1, 'CLS001', '软件工程一班', '2026级', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT IGNORE INTO sys_user(id, username, password, real_name, phone, email, class_id, status, create_time, update_time, deleted)
VALUES (1, 'admin', '$2a$10$1r5RpyTJni/28LM48j2oROfFThp32DPsgte7Mufq.0.7zSNGz37v2', '系统管理员', '13800000001', 'admin@example.com', NULL, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (2, 'teacher', '$2a$10$1r5RpyTJni/28LM48j2oROfFThp32DPsgte7Mufq.0.7zSNGz37v2', '默认教师', '13800000002', 'teacher@example.com', NULL, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (3, 'student', '$2a$10$1r5RpyTJni/28LM48j2oROfFThp32DPsgte7Mufq.0.7zSNGz37v2', '默认学生', '13800000003', 'student@example.com', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT IGNORE INTO sys_user_role(id, user_id, role_id, create_time, update_time, deleted)
VALUES (1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (2, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (3, 3, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT IGNORE INTO question(id, subject_id, question_type, title, options_json, answer_json, analysis, score, difficulty, knowledge_point, creator_id, create_time, update_time, deleted)
VALUES (1, 1, 'SINGLE_CHOICE', 'Java中用于继承的关键字是？', '["extends","implements","inherit","super"]', '["extends"]', '类继承使用 extends。', 5, 'EASY', 'Java基础', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (2, 1, 'MULTIPLE_CHOICE', '以下哪些属于Spring Boot特性？', '["自动配置","起步依赖","XML强制配置","嵌入式服务器"]', '["自动配置","起步依赖","嵌入式服务器"]', '自动配置、起步依赖、嵌入式服务器都是Spring Boot特性。', 10, 'MEDIUM', 'Spring Boot', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (3, 1, 'TRUE_FALSE', 'HTTP是无状态协议。', '["true","false"]', '["true"]', 'HTTP本身是无状态协议。', 5, 'EASY', '计算机网络', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (4, 1, 'FILL_BLANK', 'Java 8 引入的函数式接口之一是______。', NULL, '["Function"]', 'Function 是常用函数式接口。', 5, 'MEDIUM', 'Java基础', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (5, 1, 'SHORT_ANSWER', '请简述JWT的组成部分。', NULL, '["Header","Payload","Signature"]', 'JWT由Header、Payload、Signature组成。', 15, 'HARD', '安全认证', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT IGNORE INTO paper(id, paper_name, subject_id, generate_type, total_score, duration_minutes, creator_id, description, create_time, update_time, deleted)
VALUES (1, 'Java后端基础试卷', 1, 'MANUAL', 40, 60, 2, '默认初始化试卷', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT IGNORE INTO paper_question(id, paper_id, question_id, question_score, sort_no, create_time, update_time, deleted)
VALUES (1, 1, 1, 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (2, 1, 2, 10, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (3, 1, 3, 5, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (4, 1, 4, 5, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (5, 1, 5, 15, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
