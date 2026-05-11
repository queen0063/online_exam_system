INSERT IGNORE INTO sys_role(id, role_code, role_name, status, create_time, update_time, deleted)
VALUES (1, 'ADMIN', '管理员', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (2, 'TEACHER', '教师', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (3, 'STUDENT', '学生', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT IGNORE INTO subject(id, subject_code, subject_name, description, status, create_time, update_time, deleted)
VALUES (1, 'JAVA', 'Java程序设计', 'Java 程序设计课程', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (2, 'SOFTWARE_ARCH', '软件设计和体系结构', '软件设计和体系结构课程', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

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
