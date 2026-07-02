INSERT IGNORE INTO sys_role(id, role_code, role_name, status, create_time, update_time, deleted)
VALUES (1, 'ADMIN', '管理员', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (2, 'TEACHER', '教师', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (3, 'STUDENT', '学生', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT IGNORE INTO subject(id, subject_code, subject_name, description, status, create_time, update_time, deleted)
VALUES (1, 'JAVA', 'Java程序设计', 'Java 程序设计课程', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (2, 'SOFTWARE_ARCH', '软件设计和体系结构', '软件设计和体系结构课程', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT IGNORE INTO class_info(id, class_code, class_name, grade_name, teacher_id, status, create_time, update_time, deleted)
VALUES (1, 'CLS001', '软件工程一班', '2026级', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT IGNORE INTO sys_user(id, username, password, student_no, real_name, phone, email, class_id, status, create_time, update_time, deleted)
VALUES (1, 'admin', '$2a$10$1r5RpyTJni/28LM48j2oROfFThp32DPsgte7Mufq.0.7zSNGz37v2', NULL, '系统管理员', '13800000001', 'admin@example.com', NULL, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (2, 'teacher', '$2a$10$1r5RpyTJni/28LM48j2oROfFThp32DPsgte7Mufq.0.7zSNGz37v2', NULL, '默认教师', '13800000002', 'teacher@example.com', NULL, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (3, 'student', '$2a$10$1r5RpyTJni/28LM48j2oROfFThp32DPsgte7Mufq.0.7zSNGz37v2', '20260001', '默认学生', '13800000003', 'student@example.com', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (4, 'student2', '$2a$10$1r5RpyTJni/28LM48j2oROfFThp32DPsgte7Mufq.0.7zSNGz37v2', '20260002', '测试学生2', '13800000004', 'student2@example.com', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (5, 'student3', '$2a$10$1r5RpyTJni/28LM48j2oROfFThp32DPsgte7Mufq.0.7zSNGz37v2', '20260003', '测试学生3', '13800000005', 'student3@example.com', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT IGNORE INTO sys_user_role(id, user_id, role_id, create_time, update_time, deleted)
VALUES (1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (2, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
       (3, 3, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

UPDATE sys_user_role ur
    JOIN sys_user u ON u.id = ur.user_id
    JOIN sys_role r ON r.id = ur.role_id
SET ur.deleted = 0,
    ur.update_time = CURRENT_TIMESTAMP
WHERE u.username IN ('student2', 'student3')
  AND r.role_code = 'STUDENT';

INSERT IGNORE INTO sys_user_role(user_id, role_id, create_time, update_time, deleted)
SELECT u.id, r.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM sys_user u
         JOIN sys_role r ON r.role_code = 'STUDENT' AND r.deleted = 0
WHERE u.username IN ('student2', 'student3')
  AND u.deleted = 0;

UPDATE exam_student es
    JOIN exam e ON e.id = es.exam_id
    JOIN sys_user u ON u.id = es.student_id
SET es.deleted = 0,
    es.answer_status = IFNULL(es.answer_status, 'NOT_STARTED'),
    es.switch_count = IFNULL(es.switch_count, 0),
    es.update_time = CURRENT_TIMESTAMP
WHERE e.id = 1
  AND u.username IN ('student2', 'student3')
  AND es.deleted = 1;

INSERT IGNORE INTO exam_student(exam_id, student_id, answer_status, switch_count, create_time, update_time, deleted)
SELECT e.id, u.id, 'NOT_STARTED', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
FROM exam e
         JOIN sys_user u ON u.username IN ('student2', 'student3') AND u.deleted = 0
WHERE e.id = 1
  AND e.deleted = 0;
