CREATE TABLE IF NOT EXISTS sys_user
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(64)  NOT NULL,
    password    VARCHAR(255) NOT NULL,
    student_no  VARCHAR(32),
    real_name   VARCHAR(64)  NOT NULL,
    phone       VARCHAR(32),
    email       VARCHAR(128),
    class_id    BIGINT,
    status      INT          NOT NULL DEFAULT 1,
    create_time DATETIME     NOT NULL,
    update_time DATETIME     NOT NULL,
    deleted     INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_sys_user_username (username),
    UNIQUE KEY uk_sys_user_student_no (student_no),
    KEY idx_sys_user_class_id (class_id),
    KEY idx_sys_user_status (status)
);

CREATE TABLE IF NOT EXISTS sys_role
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code   VARCHAR(32) NOT NULL,
    role_name   VARCHAR(64) NOT NULL,
    status      INT         NOT NULL DEFAULT 1,
    create_time DATETIME    NOT NULL,
    update_time DATETIME    NOT NULL,
    deleted     INT         NOT NULL DEFAULT 0,
    UNIQUE KEY uk_sys_role_code (role_code)
);

CREATE TABLE IF NOT EXISTS sys_user_role
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT   NOT NULL,
    role_id     BIGINT   NOT NULL,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    deleted     INT      NOT NULL DEFAULT 0,
    UNIQUE KEY uk_sys_user_role (user_id, role_id),
    KEY idx_sys_user_role_user_id (user_id),
    KEY idx_sys_user_role_role_id (role_id)
);

CREATE TABLE IF NOT EXISTS subject
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    subject_code VARCHAR(32) NOT NULL,
    subject_name VARCHAR(64) NOT NULL,
    description  VARCHAR(255),
    status       INT          NOT NULL DEFAULT 1,
    create_time  DATETIME     NOT NULL,
    update_time  DATETIME     NOT NULL,
    deleted      INT          NOT NULL DEFAULT 0,
    UNIQUE KEY uk_subject_code (subject_code)
);

CREATE TABLE IF NOT EXISTS class_info
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_code  VARCHAR(32) NOT NULL,
    class_name  VARCHAR(64) NOT NULL,
    grade_name  VARCHAR(64),
    teacher_id  BIGINT,
    status      INT         NOT NULL DEFAULT 1,
    create_time DATETIME    NOT NULL,
    update_time DATETIME    NOT NULL,
    deleted     INT         NOT NULL DEFAULT 0,
    UNIQUE KEY uk_class_code (class_code),
    KEY idx_class_teacher_id (teacher_id)
);

CREATE TABLE IF NOT EXISTS question
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    subject_id      BIGINT       NOT NULL,
    question_type   VARCHAR(32)  NOT NULL,
    title           CLOB         NOT NULL,
    image_json      CLOB,
    options_json    TEXT,
    answer_json     TEXT         NOT NULL,
    analysis        TEXT,
    score           INT          NOT NULL,
    difficulty      VARCHAR(32)  NOT NULL,
    knowledge_point VARCHAR(128) NOT NULL,
    creator_id      BIGINT       NOT NULL,
    create_time     DATETIME     NOT NULL,
    update_time     DATETIME     NOT NULL,
    deleted         INT          NOT NULL DEFAULT 0,
    KEY idx_question_subject_type (subject_id, question_type),
    KEY idx_question_creator_id (creator_id),
    KEY idx_question_difficulty (difficulty),
    KEY idx_question_knowledge_point (knowledge_point)
);

CREATE TABLE IF NOT EXISTS paper
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    paper_name       VARCHAR(128) NOT NULL,
    subject_id       BIGINT       NOT NULL,
    generate_type    VARCHAR(32)  NOT NULL,
    total_score      INT          NOT NULL,
    duration_minutes INT          NOT NULL,
    creator_id       BIGINT       NOT NULL,
    description      VARCHAR(255),
    create_time      DATETIME     NOT NULL,
    update_time      DATETIME     NOT NULL,
    deleted          INT          NOT NULL DEFAULT 0,
    KEY idx_paper_subject_id (subject_id),
    KEY idx_paper_creator_id (creator_id)
);

CREATE TABLE IF NOT EXISTS paper_question
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    paper_id       BIGINT   NOT NULL,
    question_id    BIGINT   NOT NULL,
    question_score INT      NOT NULL,
    sort_no        INT      NOT NULL,
    create_time    DATETIME NOT NULL,
    update_time    DATETIME NOT NULL,
    deleted        INT      NOT NULL DEFAULT 0,
    UNIQUE KEY uk_paper_question_unique (paper_id, question_id),
    KEY idx_paper_question_sort_no (paper_id, sort_no)
);

CREATE TABLE IF NOT EXISTS exam
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    exam_name        VARCHAR(128) NOT NULL,
    paper_id         BIGINT       NOT NULL,
    subject_id       BIGINT       NOT NULL,
    creator_id       BIGINT       NOT NULL,
    start_time       DATETIME     NOT NULL,
    end_time         DATETIME     NOT NULL,
    duration_minutes INT          NOT NULL,
    pass_score       INT          NOT NULL,
    status           VARCHAR(32)  NOT NULL,
    result_published INT          NOT NULL DEFAULT 0,
    create_time      DATETIME     NOT NULL,
    update_time      DATETIME     NOT NULL,
    deleted          INT          NOT NULL DEFAULT 0,
    KEY idx_exam_creator_id (creator_id),
    KEY idx_exam_time (start_time, end_time),
    KEY idx_exam_status (status)
);

CREATE TABLE IF NOT EXISTS exam_student
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    exam_id       BIGINT      NOT NULL,
    student_id    BIGINT      NOT NULL,
    answer_status VARCHAR(32) NOT NULL,
    create_time   DATETIME    NOT NULL,
    update_time   DATETIME    NOT NULL,
    deleted       INT         NOT NULL DEFAULT 0,
    UNIQUE KEY uk_exam_student_unique (exam_id, student_id),
    KEY idx_exam_student_student_id (student_id),
    KEY idx_exam_student_status (answer_status)
);

CREATE TABLE IF NOT EXISTS student_answer
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    exam_id         BIGINT      NOT NULL,
    paper_id        BIGINT      NOT NULL,
    question_id     BIGINT      NOT NULL,
    student_id      BIGINT      NOT NULL,
    answer_content  TEXT,
    question_score  INT         NOT NULL,
    actual_score    INT DEFAULT 0,
    objective_flag  INT DEFAULT 0,
    correct_flag    INT DEFAULT 0,
    marking_status  VARCHAR(32) NOT NULL,
    teacher_comment VARCHAR(255),
    submit_time     DATETIME,
    create_time     DATETIME    NOT NULL,
    update_time     DATETIME    NOT NULL,
    deleted         INT         NOT NULL DEFAULT 0,
    UNIQUE KEY uk_student_answer_unique (exam_id, student_id, question_id),
    KEY idx_student_answer_marking (marking_status),
    KEY idx_student_answer_student (student_id),
    KEY idx_student_answer_exam (exam_id)
);

CREATE TABLE IF NOT EXISTS exam_score
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    exam_id          BIGINT      NOT NULL,
    student_id       BIGINT      NOT NULL,
    objective_score  INT DEFAULT 0,
    subjective_score INT DEFAULT 0,
    total_score      INT DEFAULT 0,
    pass_flag        INT DEFAULT 0,
    excellent_flag   INT DEFAULT 0,
    rank_no          INT,
    score_status     VARCHAR(32) NOT NULL,
    submit_time      DATETIME,
    publish_time     DATETIME,
    create_time      DATETIME    NOT NULL,
    update_time      DATETIME    NOT NULL,
    deleted          INT         NOT NULL DEFAULT 0,
    UNIQUE KEY uk_exam_score_unique (exam_id, student_id),
    KEY idx_exam_score_rank (exam_id, rank_no),
    KEY idx_exam_score_status (score_status)
);

CREATE TABLE IF NOT EXISTS notice
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    title         VARCHAR(128) NOT NULL,
    content       TEXT         NOT NULL,
    notice_status VARCHAR(32)  NOT NULL,
    publisher_id  BIGINT       NOT NULL,
    create_time   DATETIME     NOT NULL,
    update_time   DATETIME     NOT NULL,
    deleted       INT          NOT NULL DEFAULT 0,
    KEY idx_notice_status (notice_status)
);

CREATE TABLE IF NOT EXISTS login_log
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      BIGINT,
    username     VARCHAR(64),
    success_flag INT          NOT NULL,
    ip_address   VARCHAR(64),
    user_agent   VARCHAR(255),
    message      VARCHAR(255),
    login_time   DATETIME     NOT NULL,
    create_time  DATETIME     NOT NULL,
    update_time  DATETIME     NOT NULL,
    deleted      INT          NOT NULL DEFAULT 0,
    KEY idx_login_log_user_id (user_id),
    KEY idx_login_log_time (login_time)
);

CREATE TABLE IF NOT EXISTS operation_log
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    operator_id    BIGINT,
    operator_name  VARCHAR(64),
    operation_type VARCHAR(32)  NOT NULL,
    module_name    VARCHAR(64)  NOT NULL,
    operation_desc VARCHAR(255) NOT NULL,
    request_uri    VARCHAR(255),
    request_method VARCHAR(16),
    request_param  TEXT,
    success_flag   INT          NOT NULL,
    operation_time DATETIME     NOT NULL,
    create_time    DATETIME     NOT NULL,
    update_time    DATETIME     NOT NULL,
    deleted        INT          NOT NULL DEFAULT 0,
    KEY idx_operation_log_operator_id (operator_id),
    KEY idx_operation_log_time (operation_time),
    KEY idx_operation_log_type (operation_type)
);
