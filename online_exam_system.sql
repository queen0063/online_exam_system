/*
 Navicat Premium Dump SQL

 Source Server         : Web_MySQL
 Source Server Type    : MySQL
 Source Server Version : 80409 (8.4.9)
 Source Host           : 20.89.74.2:3306
 Source Schema         : online_exam_system

 Target Server Type    : MySQL
 Target Server Version : 80409 (8.4.9)
 File Encoding         : 65001

 Date: 21/05/2026 15:03:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
CREATE DATABASE IF NOT EXISTS `online_exam_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `online_exam_system`;

-- ----------------------------
-- Table structure for class_info
-- ----------------------------
DROP TABLE IF EXISTS `class_info`;
CREATE TABLE `class_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `class_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `class_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `grade_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `teacher_id` bigint NULL DEFAULT NULL,
  `status` int NOT NULL DEFAULT 1,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `deleted` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_class_code`(`class_code` ASC) USING BTREE,
  INDEX `idx_class_teacher_id`(`teacher_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of class_info
-- ----------------------------
INSERT INTO `class_info` VALUES (1, 'CLS001', '软件工程四班', '2023级', 2, 1, '2026-05-11 15:02:02', '2026-05-20 23:15:10', 0);

-- ----------------------------
-- Table structure for exam
-- ----------------------------
DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `paper_id` bigint NOT NULL,
  `subject_id` bigint NOT NULL,
  `creator_id` bigint NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `duration_minutes` int NOT NULL,
  `pass_score` int NOT NULL,
  `max_switch_count` int NULL DEFAULT NULL,
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `result_published` int NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `deleted` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_exam_creator_id`(`creator_id` ASC) USING BTREE,
  INDEX `idx_exam_time`(`start_time` ASC, `end_time` ASC) USING BTREE,
  INDEX `idx_exam_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam
-- ----------------------------
INSERT INTO `exam` VALUES (1, '测试卷1', 1, 1, 2, '2026-05-12 14:16:56', '2026-05-13 00:00:00', 60, 24, NULL, 'ENDED', 0, '2026-05-12 14:17:02', '2026-05-20 16:07:33', 1);
INSERT INTO `exam` VALUES (2, '2021年11月系统架构师真题', 3, 3, 2, '2026-05-15 00:00:00', '2026-05-15 01:00:00', 60, 60, NULL, 'ENDED', 0, '2026-05-15 09:52:01', '2026-05-20 16:07:55', 1);
INSERT INTO `exam` VALUES (3, '2021年11月系统架构师真题', 3, 3, 2, '2026-05-15 09:52:58', '2026-05-16 09:53:00', 60, 60, NULL, 'GRADED', 0, '2026-05-15 09:53:12', '2026-05-20 16:07:54', 1);
INSERT INTO `exam` VALUES (4, 'test2', 5, 3, 2, '2026-05-19 11:09:43', '2026-05-20 00:00:00', 60, 60, NULL, 'IN_PROGRESS', 0, '2026-05-19 11:09:56', '2026-05-19 11:11:22', 1);
INSERT INTO `exam` VALUES (5, 'test2', 5, 3, 2, '2026-05-19 11:11:28', '2026-05-20 00:00:00', 60, 60, NULL, 'IN_PROGRESS', 0, '2026-05-19 11:11:41', '2026-05-19 11:17:36', 1);
INSERT INTO `exam` VALUES (6, 'test2', 5, 1, 2, '2026-05-19 11:17:43', '2026-05-20 00:00:00', 60, 60, NULL, 'ENDED', 0, '2026-05-19 11:17:56', '2026-05-20 16:07:52', 1);
INSERT INTO `exam` VALUES (7, 'test3', 7, 2, 2, '2026-05-19 11:20:05', '2026-05-20 00:00:00', 60, 60, NULL, 'ENDED', 0, '2026-05-19 11:20:18', '2026-05-20 16:07:36', 1);

-- ----------------------------
-- Table structure for exam_score
-- ----------------------------
DROP TABLE IF EXISTS `exam_score`;
CREATE TABLE `exam_score`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `objective_score` int NULL DEFAULT 0,
  `subjective_score` int NULL DEFAULT 0,
  `total_score` int NULL DEFAULT 0,
  `pass_flag` int NULL DEFAULT 0,
  `excellent_flag` int NULL DEFAULT 0,
  `rank_no` int NULL DEFAULT NULL,
  `score_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `submit_time` datetime NULL DEFAULT NULL,
  `publish_time` datetime NULL DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `deleted` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_exam_score_unique`(`exam_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `idx_exam_score_rank`(`exam_id` ASC, `rank_no` ASC) USING BTREE,
  INDEX `idx_exam_score_status`(`score_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_score
-- ----------------------------
INSERT INTO `exam_score` VALUES (1, 1, 3, 0, 0, 0, 0, 0, NULL, 'WAIT_MARKING', '2026-05-12 14:17:48', NULL, '2026-05-12 14:17:48', '2026-05-12 14:17:48', 0);
INSERT INTO `exam_score` VALUES (2, 3, 3, 0, 0, 0, 0, 0, 1, 'MARKED', '2026-05-15 09:53:49', NULL, '2026-05-15 09:53:49', '2026-05-15 09:54:48', 0);
INSERT INTO `exam_score` VALUES (3, 4, 3, 0, 0, 0, 0, 0, 1, 'MARKED', '2026-05-19 11:10:53', NULL, '2026-05-19 11:10:53', '2026-05-19 11:10:52', 0);
INSERT INTO `exam_score` VALUES (4, 5, 3, 0, 0, 0, 0, 0, 1, 'MARKED', '2026-05-19 11:12:01', NULL, '2026-05-19 11:12:01', '2026-05-19 11:12:01', 0);
INSERT INTO `exam_score` VALUES (5, 6, 3, 5, 0, 5, 0, 0, 1, 'MARKED', '2026-05-19 11:20:28', NULL, '2026-05-19 11:20:28', '2026-05-19 11:20:27', 0);
INSERT INTO `exam_score` VALUES (6, 7, 3, 0, 0, 0, 0, 0, NULL, 'WAIT_MARKING', '2026-05-19 11:24:47', NULL, '2026-05-19 11:24:47', '2026-05-19 11:24:47', 0);

-- ----------------------------
-- Table structure for exam_student
-- ----------------------------
DROP TABLE IF EXISTS `exam_student`;
CREATE TABLE `exam_student`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `answer_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `switch_count` int NOT NULL DEFAULT 0,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `deleted` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_exam_student_unique`(`exam_id` ASC, `student_id` ASC) USING BTREE,
  INDEX `idx_exam_student_student_id`(`student_id` ASC) USING BTREE,
  INDEX `idx_exam_student_status`(`answer_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_student
-- ----------------------------

-- ----------------------------
-- Table structure for login_log
-- ----------------------------
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NULL DEFAULT NULL,
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `success_flag` int NOT NULL,
  `ip_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `login_time` datetime NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `deleted` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_login_log_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_login_log_time`(`login_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of login_log
-- ----------------------------
INSERT INTO `login_log` VALUES (1, 3, 'student', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-11 23:02:18', '2026-05-11 23:02:18', '2026-05-11 23:02:18', 0);
INSERT INTO `login_log` VALUES (2, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-11 23:02:22', '2026-05-11 23:02:22', '2026-05-11 23:02:22', 0);
INSERT INTO `login_log` VALUES (3, 3, 'student', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-12 14:10:50', '2026-05-12 14:10:50', '2026-05-12 14:10:50', 0);
INSERT INTO `login_log` VALUES (4, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-12 14:11:09', '2026-05-12 14:11:09', '2026-05-12 14:11:09', 0);
INSERT INTO `login_log` VALUES (5, 3, 'student', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-12 14:16:14', '2026-05-12 14:16:14', '2026-05-12 14:16:14', 0);
INSERT INTO `login_log` VALUES (6, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-12 14:16:20', '2026-05-12 14:16:20', '2026-05-12 14:16:20', 0);
INSERT INTO `login_log` VALUES (7, 3, 'student', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-12 14:17:06', '2026-05-12 14:17:06', '2026-05-12 14:17:06', 0);
INSERT INTO `login_log` VALUES (8, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-12 14:18:23', '2026-05-12 14:18:23', '2026-05-12 14:18:23', 0);
INSERT INTO `login_log` VALUES (9, 3, 'student', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-15 09:26:14', '2026-05-15 09:26:14', '2026-05-15 09:26:14', 0);
INSERT INTO `login_log` VALUES (10, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-15 09:26:33', '2026-05-15 09:26:33', '2026-05-15 09:26:33', 0);
INSERT INTO `login_log` VALUES (11, 1, 'admin', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-15 09:28:39', '2026-05-15 09:28:39', '2026-05-15 09:28:39', 0);
INSERT INTO `login_log` VALUES (12, 1, 'admin', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '登录成功', '2026-05-15 09:41:10', '2026-05-15 09:41:10', '2026-05-15 09:41:10', 0);
INSERT INTO `login_log` VALUES (13, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-15 09:45:29', '2026-05-15 09:45:29', '2026-05-15 09:45:29', 0);
INSERT INTO `login_log` VALUES (14, 1, 'admin', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-15 09:45:37', '2026-05-15 09:45:37', '2026-05-15 09:45:37', 0);
INSERT INTO `login_log` VALUES (15, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-15 09:48:05', '2026-05-15 09:48:05', '2026-05-15 09:48:05', 0);
INSERT INTO `login_log` VALUES (16, 3, 'student', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-15 09:48:56', '2026-05-15 09:48:56', '2026-05-15 09:48:56', 0);
INSERT INTO `login_log` VALUES (17, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-15 09:49:22', '2026-05-15 09:49:22', '2026-05-15 09:49:22', 0);
INSERT INTO `login_log` VALUES (18, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-15 09:49:27', '2026-05-15 09:49:27', '2026-05-15 09:49:27', 0);
INSERT INTO `login_log` VALUES (19, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-15 09:49:54', '2026-05-15 09:49:54', '2026-05-15 09:49:54', 0);
INSERT INTO `login_log` VALUES (20, 3, 'student', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-15 09:52:07', '2026-05-15 09:52:07', '2026-05-15 09:52:07', 0);
INSERT INTO `login_log` VALUES (21, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-15 09:52:23', '2026-05-15 09:52:23', '2026-05-15 09:52:23', 0);
INSERT INTO `login_log` VALUES (22, 3, 'student', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-15 09:53:20', '2026-05-15 09:53:20', '2026-05-15 09:53:20', 0);
INSERT INTO `login_log` VALUES (23, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-15 09:54:12', '2026-05-15 09:54:12', '2026-05-15 09:54:12', 0);
INSERT INTO `login_log` VALUES (24, 1, 'admin', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-15 09:58:11', '2026-05-15 09:58:11', '2026-05-15 09:58:11', 0);
INSERT INTO `login_log` VALUES (25, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-16 11:40:12', '2026-05-16 11:40:12', '2026-05-16 11:40:12', 0);
INSERT INTO `login_log` VALUES (26, 3, 'student', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-16 11:40:19', '2026-05-16 11:40:19', '2026-05-16 11:40:19', 0);
INSERT INTO `login_log` VALUES (27, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-16 11:40:24', '2026-05-16 11:40:24', '2026-05-16 11:40:24', 0);
INSERT INTO `login_log` VALUES (28, 3, 'student', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '登录成功', '2026-05-19 11:07:42', '2026-05-19 11:07:42', '2026-05-19 11:07:42', 0);
INSERT INTO `login_log` VALUES (29, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '登录成功', '2026-05-19 11:08:35', '2026-05-19 11:08:35', '2026-05-19 11:08:35', 0);
INSERT INTO `login_log` VALUES (30, 3, 'student', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '登录成功', '2026-05-19 11:10:09', '2026-05-19 11:10:09', '2026-05-19 11:10:09', 0);
INSERT INTO `login_log` VALUES (31, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '登录成功', '2026-05-19 11:11:10', '2026-05-19 11:11:10', '2026-05-19 11:11:10', 0);
INSERT INTO `login_log` VALUES (32, 3, 'student', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '登录成功', '2026-05-19 11:11:52', '2026-05-19 11:11:52', '2026-05-19 11:11:52', 0);
INSERT INTO `login_log` VALUES (33, 1, 'admin', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 Edg/131.0.0.0', '登录成功', '2026-05-19 11:14:27', '2026-05-19 11:14:27', '2026-05-19 11:14:27', 0);
INSERT INTO `login_log` VALUES (34, 3, 'student', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 Edg/131.0.0.0', '登录成功', '2026-05-19 11:17:05', '2026-05-19 11:17:05', '2026-05-19 11:17:05', 0);
INSERT INTO `login_log` VALUES (35, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '登录成功', '2026-05-19 11:17:24', '2026-05-19 11:17:24', '2026-05-19 11:17:24', 0);
INSERT INTO `login_log` VALUES (36, 3, 'student', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-19 11:35:36', '2026-05-19 11:35:36', '2026-05-19 11:35:36', 0);
INSERT INTO `login_log` VALUES (37, 3, 'student', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-19 11:42:04', '2026-05-19 11:42:04', '2026-05-19 11:42:04', 0);
INSERT INTO `login_log` VALUES (38, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-19 11:42:18', '2026-05-19 11:42:18', '2026-05-19 11:42:18', 0);
INSERT INTO `login_log` VALUES (39, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-20 14:48:09', '2026-05-20 14:48:09', '2026-05-20 14:48:09', 0);
INSERT INTO `login_log` VALUES (40, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '登录成功', '2026-05-20 14:58:50', '2026-05-20 14:58:50', '2026-05-20 14:58:50', 0);
INSERT INTO `login_log` VALUES (41, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '登录成功', '2026-05-20 15:29:43', '2026-05-20 15:29:43', '2026-05-20 15:29:43', 0);
INSERT INTO `login_log` VALUES (42, 1, 'admin', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36', '登录成功', '2026-05-20 16:12:23', '2026-05-20 16:12:23', '2026-05-20 16:12:23', 0);
INSERT INTO `login_log` VALUES (43, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-20 23:08:31', '2026-05-20 23:08:31', '2026-05-20 23:08:31', 0);
INSERT INTO `login_log` VALUES (44, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-20 23:09:54', '2026-05-20 23:09:54', '2026-05-20 23:09:54', 0);
INSERT INTO `login_log` VALUES (45, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-20 23:10:37', '2026-05-20 23:10:37', '2026-05-20 23:10:37', 0);
INSERT INTO `login_log` VALUES (46, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-20 23:11:42', '2026-05-20 23:11:42', '2026-05-20 23:11:42', 0);
INSERT INTO `login_log` VALUES (47, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-20 23:14:00', '2026-05-20 23:14:00', '2026-05-20 23:14:00', 0);
INSERT INTO `login_log` VALUES (48, 1, 'admin', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-20 23:14:47', '2026-05-20 23:14:47', '2026-05-20 23:14:47', 0);
INSERT INTO `login_log` VALUES (49, 2, 'teacher', 1, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36 Edg/148.0.0.0', '登录成功', '2026-05-21 14:58:59', '2026-05-21 14:58:59', '2026-05-21 14:58:59', 0);

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `notice_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `publisher_id` bigint NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `deleted` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_notice_status`(`notice_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice
-- ----------------------------

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `operator_id` bigint NULL DEFAULT NULL,
  `operator_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `operation_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `module_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `operation_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `request_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `request_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `success_flag` int NOT NULL,
  `operation_time` datetime NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `deleted` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_operation_log_operator_id`(`operator_id` ASC) USING BTREE,
  INDEX `idx_operation_log_time`(`operation_time` ASC) USING BTREE,
  INDEX `idx_operation_log_type`(`operation_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------

-- ----------------------------
-- Table structure for paper
-- ----------------------------
DROP TABLE IF EXISTS `paper`;
CREATE TABLE `paper`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `paper_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `subject_id` bigint NOT NULL,
  `generate_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `total_score` int NOT NULL,
  `duration_minutes` int NOT NULL,
  `creator_id` bigint NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `deleted` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_paper_subject_id`(`subject_id` ASC) USING BTREE,
  INDEX `idx_paper_creator_id`(`creator_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of paper
-- ----------------------------
INSERT INTO `paper` VALUES (1, '2026jzz组内测试试题一', 2, 'MANUAL', 40, 60, 2, '', '2026-05-12 14:15:39', '2026-05-12 14:15:39', 0);
INSERT INTO `paper` VALUES (2, '2021年11月系统架构师真题', 2, 'MANUAL', 100, 60, 1, '', '2026-05-15 09:47:32', '2026-05-15 09:47:32', 0);
INSERT INTO `paper` VALUES (3, '2021年11月系统架构师真题', 3, 'MANUAL', 100, 60, 2, '', '2026-05-15 09:51:00', '2026-05-15 10:09:16', 1);
INSERT INTO `paper` VALUES (4, '2021年11月系统架构师真题', 3, 'MANUAL', 50, 60, 2, '', '2026-05-15 10:06:48', '2026-05-15 10:11:43', 1);
INSERT INTO `paper` VALUES (5, '2021年11月系统架构师真题（综合知识+答案解析）', 3, 'MANUAL', 50, 60, 2, '', '2026-05-15 10:12:07', '2026-05-15 10:12:07', 0);
INSERT INTO `paper` VALUES (6, '2026jzz组内测试试题二', 5, 'MANUAL', 65, 60, 1, '', '2026-05-15 10:15:30', '2026-05-15 10:15:30', 0);
INSERT INTO `paper` VALUES (7, '2021年11月系统架构师真题（案例分析）', 2, 'MANUAL', 100, 60, 2, '', '2026-05-15 10:15:36', '2026-05-15 10:15:36', 0);
INSERT INTO `paper` VALUES (8, '中级软件设计师下午试题模拟', 4, 'MANUAL', 100, 60, 2, '', '2026-05-15 10:30:42', '2026-05-15 10:30:42', 0);

-- ----------------------------
-- Table structure for paper_question
-- ----------------------------
DROP TABLE IF EXISTS `paper_question`;
CREATE TABLE `paper_question`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `paper_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `question_score` int NOT NULL,
  `sort_no` int NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `deleted` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_paper_question_unique`(`paper_id` ASC, `question_id` ASC) USING BTREE,
  INDEX `idx_paper_question_sort_no`(`paper_id` ASC, `sort_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 151 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of paper_question
-- ----------------------------
INSERT INTO `paper_question` VALUES (1, 1, 1, 10, 1, '2026-05-12 14:15:39', '2026-05-12 14:15:39', 0);
INSERT INTO `paper_question` VALUES (2, 1, 3, 10, 2, '2026-05-12 14:15:39', '2026-05-12 14:15:39', 0);
INSERT INTO `paper_question` VALUES (3, 1, 5, 10, 3, '2026-05-12 14:15:39', '2026-05-12 14:15:39', 0);
INSERT INTO `paper_question` VALUES (4, 1, 6, 10, 4, '2026-05-12 14:15:39', '2026-05-12 14:15:39', 0);
INSERT INTO `paper_question` VALUES (5, 2, 7, 25, 1, '2026-05-15 09:47:32', '2026-05-15 09:47:32', 0);
INSERT INTO `paper_question` VALUES (6, 2, 8, 25, 2, '2026-05-15 09:47:32', '2026-05-15 09:47:32', 0);
INSERT INTO `paper_question` VALUES (7, 2, 9, 25, 3, '2026-05-15 09:47:32', '2026-05-15 09:47:32', 0);
INSERT INTO `paper_question` VALUES (8, 2, 10, 25, 4, '2026-05-15 09:47:32', '2026-05-15 09:47:32', 0);
INSERT INTO `paper_question` VALUES (9, 3, 7, 25, 1, '2026-05-15 09:51:00', '2026-05-15 10:09:16', 1);
INSERT INTO `paper_question` VALUES (10, 3, 8, 25, 2, '2026-05-15 09:51:00', '2026-05-15 10:09:16', 1);
INSERT INTO `paper_question` VALUES (11, 3, 9, 25, 3, '2026-05-15 09:51:00', '2026-05-15 10:09:16', 1);
INSERT INTO `paper_question` VALUES (12, 3, 10, 25, 4, '2026-05-15 09:51:00', '2026-05-15 10:09:16', 1);
INSERT INTO `paper_question` VALUES (13, 4, 27, 5, 1, '2026-05-15 10:06:48', '2026-05-15 10:11:43', 1);
INSERT INTO `paper_question` VALUES (14, 4, 25, 5, 2, '2026-05-15 10:06:48', '2026-05-15 10:11:43', 1);
INSERT INTO `paper_question` VALUES (15, 4, 23, 5, 3, '2026-05-15 10:06:48', '2026-05-15 10:11:43', 1);
INSERT INTO `paper_question` VALUES (16, 4, 20, 5, 4, '2026-05-15 10:06:48', '2026-05-15 10:11:43', 1);
INSERT INTO `paper_question` VALUES (17, 4, 18, 5, 5, '2026-05-15 10:06:48', '2026-05-15 10:11:43', 1);
INSERT INTO `paper_question` VALUES (18, 4, 16, 5, 6, '2026-05-15 10:06:48', '2026-05-15 10:11:43', 1);
INSERT INTO `paper_question` VALUES (19, 4, 15, 5, 7, '2026-05-15 10:06:48', '2026-05-15 10:11:43', 1);
INSERT INTO `paper_question` VALUES (20, 4, 14, 5, 8, '2026-05-15 10:06:48', '2026-05-15 10:11:43', 1);
INSERT INTO `paper_question` VALUES (21, 4, 12, 5, 9, '2026-05-15 10:06:48', '2026-05-15 10:11:43', 1);
INSERT INTO `paper_question` VALUES (22, 4, 11, 5, 10, '2026-05-15 10:06:48', '2026-05-15 10:11:43', 1);
INSERT INTO `paper_question` VALUES (93, 5, 27, 5, 1, '2026-05-15 10:12:07', '2026-05-15 10:12:07', 0);
INSERT INTO `paper_question` VALUES (94, 5, 25, 5, 2, '2026-05-15 10:12:07', '2026-05-15 10:12:07', 0);
INSERT INTO `paper_question` VALUES (95, 5, 23, 5, 3, '2026-05-15 10:12:07', '2026-05-15 10:12:07', 0);
INSERT INTO `paper_question` VALUES (96, 5, 20, 5, 4, '2026-05-15 10:12:07', '2026-05-15 10:12:07', 0);
INSERT INTO `paper_question` VALUES (97, 5, 18, 5, 5, '2026-05-15 10:12:07', '2026-05-15 10:12:07', 0);
INSERT INTO `paper_question` VALUES (98, 5, 14, 5, 6, '2026-05-15 10:12:07', '2026-05-15 10:12:07', 0);
INSERT INTO `paper_question` VALUES (99, 5, 16, 5, 7, '2026-05-15 10:12:07', '2026-05-15 10:12:07', 0);
INSERT INTO `paper_question` VALUES (100, 5, 15, 5, 8, '2026-05-15 10:12:07', '2026-05-15 10:12:07', 0);
INSERT INTO `paper_question` VALUES (101, 5, 12, 5, 9, '2026-05-15 10:12:07', '2026-05-15 10:12:07', 0);
INSERT INTO `paper_question` VALUES (102, 5, 11, 5, 10, '2026-05-15 10:12:07', '2026-05-15 10:12:07', 0);
INSERT INTO `paper_question` VALUES (133, 6, 36, 5, 1, '2026-05-15 10:15:30', '2026-05-15 10:15:30', 0);
INSERT INTO `paper_question` VALUES (134, 6, 35, 5, 2, '2026-05-15 10:15:30', '2026-05-15 10:15:30', 0);
INSERT INTO `paper_question` VALUES (135, 6, 34, 5, 3, '2026-05-15 10:15:30', '2026-05-15 10:15:30', 0);
INSERT INTO `paper_question` VALUES (136, 6, 33, 5, 4, '2026-05-15 10:15:30', '2026-05-15 10:15:30', 0);
INSERT INTO `paper_question` VALUES (137, 6, 30, 5, 5, '2026-05-15 10:15:30', '2026-05-15 10:15:30', 0);
INSERT INTO `paper_question` VALUES (138, 6, 28, 10, 6, '2026-05-15 10:15:30', '2026-05-15 10:15:30', 0);
INSERT INTO `paper_question` VALUES (139, 6, 24, 10, 7, '2026-05-15 10:15:30', '2026-05-15 10:15:30', 0);
INSERT INTO `paper_question` VALUES (140, 6, 21, 10, 8, '2026-05-15 10:15:30', '2026-05-15 10:15:30', 0);
INSERT INTO `paper_question` VALUES (141, 6, 19, 10, 9, '2026-05-15 10:15:30', '2026-05-15 10:15:30', 0);
INSERT INTO `paper_question` VALUES (142, 7, 7, 25, 1, '2026-05-15 10:15:36', '2026-05-15 10:15:36', 0);
INSERT INTO `paper_question` VALUES (143, 7, 8, 25, 2, '2026-05-15 10:15:36', '2026-05-15 10:15:36', 0);
INSERT INTO `paper_question` VALUES (144, 7, 9, 25, 3, '2026-05-15 10:15:36', '2026-05-15 10:15:36', 0);
INSERT INTO `paper_question` VALUES (145, 7, 10, 25, 4, '2026-05-15 10:15:36', '2026-05-15 10:15:36', 0);
INSERT INTO `paper_question` VALUES (146, 8, 13, 20, 1, '2026-05-15 10:30:42', '2026-05-15 10:30:42', 0);
INSERT INTO `paper_question` VALUES (147, 8, 17, 20, 2, '2026-05-15 10:30:42', '2026-05-15 10:30:42', 0);
INSERT INTO `paper_question` VALUES (148, 8, 26, 20, 3, '2026-05-15 10:30:42', '2026-05-15 10:30:42', 0);
INSERT INTO `paper_question` VALUES (149, 8, 45, 20, 4, '2026-05-15 10:30:42', '2026-05-15 10:30:42', 0);
INSERT INTO `paper_question` VALUES (150, 8, 37, 20, 5, '2026-05-15 10:30:42', '2026-05-15 10:30:42', 0);

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `subject_id` bigint NOT NULL,
  `question_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `image_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `options_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `answer_json` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `score` int NOT NULL,
  `difficulty` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `knowledge_point` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `creator_id` bigint NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `deleted` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_question_subject_type`(`subject_id` ASC, `question_type` ASC) USING BTREE,
  INDEX `idx_question_creator_id`(`creator_id` ASC) USING BTREE,
  INDEX `idx_question_difficulty`(`difficulty` ASC) USING BTREE,
  INDEX `idx_question_knowledge_point`(`knowledge_point` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question
-- ----------------------------
INSERT INTO `question` VALUES (1, 2, 'SHORT_ANSWER', '题目1、阅读下列说明和数据流图，回答问题1至问题4，将解答填入答题纸的对应栏内。\n【说明】\n【问题1】使用说明中的词语，给出图1-1中的实体E1〜E4的名称。\n【问题2】使用说明中的词语，给出图1-2中的数据存储D1〜D5的名称。\n【问题3】根据说明和图中术语，补充图1-2中缺失的数据流及其起点和终点。\n【问题4】根据说明，给出“充电监测与计量数据”数据流的组成。', '[\"https://picup.candlesun.xyz/exam/questions/2026/05/11/sr9ordzw5phdevm8f9up9nlbz6kbbpi7.png\",\"https://picup.candlesun.xyz/exam/questions/2026/05/11/na7q9zvkw4drupeyl9lf3l71sv70e86w.png\",\"https://picup.candlesun.xyz/exam/questions/2026/05/11/vac1nkeotyxaxw1zteaa1uzth79gv8ih.png\"]', '[]', '[\"null\"]', '', 10, 'HARD', '数据流图', 2, '2026-05-11 23:05:18', '2026-05-11 23:27:27', 0);
INSERT INTO `question` VALUES (2, 1, 'SHORT_ANSWER', '1', '[]', '[]', '[\"1\"]', '', 10, 'HARD', 'ER图', 2, '2026-05-11 23:08:42', '2026-05-11 23:08:54', 1);
INSERT INTO `question` VALUES (3, 2, 'SHORT_ANSWER', '题目2、阅读下列说明和E-R图，回答问题1至问题3。\n【说明】\n某学校的教学系统描述如下：\n    学生信息包括：学号(SNo)、姓名(Sname)、性别(Sex)、年龄(Age)、入学年份(Year)、主修专业(Major)，其中学号是入学时唯一编定的。\n    课程信息包括：课程号(CNo)、课程名称(CName)、学时(Period)、学分(Credit)，其中课程号是唯一编定的。\n    一个学生可选多门课，每个学生选每门课有一个成绩。图是经分析得到的E-R图。\n     \n【问题1】\n设基本表：Student(SNo, SName, Sex, Age, Year, Major), Course(CNo, Cname, Period, Credit), Grade(SNo, CNo, Grade)通过如下SQL语句建立，请在SQLN句空缺处填入正确的内容。\n    CREATE TABLE Student (SNo CHAR(6) NOT NULL,\n    SName CHAR(20),\n    Sex CHAR(1),\n    Age INTEGER,\n    Year CHAR(4),\n    Major CHAR(20),\n    ______;\n    CREATE TABLE Course (CNo CHAR(6) NOT NULL,\n    CName CHAR(20),\n    Period INTEGER,\n    Credit INTEGER,\n    ______;\n    CREATE TABLE Grade  (SNo CHAR(6)  NOT NULL,\n    CNo CHAR(6) NOT NULL\n    Grade REAL,\n    ______,\n    ______,\n    ______;\n\n【问题2】\n若另有表Teach(CName, TName)存储教师任课情况，Tname表示教师名。用SQL创建一个含有学号、姓名、课程名、成绩、任课教师名的“主修专业为计算机CS”的学生成绩视图，并要求进行修改、插入操作时保证该视图只有计算机系的学生。请在SQL语句空缺处填入正确的内容。\n    CREATE VIEW SG ______\n    SELECT Student.SNo, SName, Grade, Course.CName, TName\n    FROM Student, Grade, Teach,\n    WHERE ______\n    AND ______\n    AND Major = \'CS\',\n    ______;\n\n【问题3】\n如下的SQL语句是用于查询“每个学生的选修课程数、总成绩、平均成绩”的不完整语句，请在空缺处填入正确的内容。\n    SELECT Student.SNo, ______, SUM(Grade), AVG(Grade)\n    FROM Student, Grade\n    WHERE Student.SNo = Grade.SNo,\n    GROUP BY ______;', '[\"https://picup.candlesun.xyz/exam/questions/2026/05/11/mx69zhplux0qgqg862w6qnlbz9d0cw6b.jpg\"]', '[]', '[\"null\"]', '', 10, 'HARD', 'ER图', 2, '2026-05-11 23:13:21', '2026-05-11 23:48:21', 0);
INSERT INTO `question` VALUES (4, 2, 'SHORT_ANSWER', '1', '[]', '[]', '[\"1\"]', '', 5, 'EASY', '1', 2, '2026-05-11 23:13:38', '2026-05-11 23:13:40', 1);
INSERT INTO `question` VALUES (5, 2, 'SHORT_ANSWER', '题目3、阅读以下说明、C函数和问题，将解答填写完整。\n【说明】\n    二叉查找树又称为二叉排序树，它或者是一棵空树，或者是具有如下性质的二叉树：\n    ● 若它的左子树非空，则其左子树上所有结点的键值均小于根结点的键值；\n    ● 若它的右子树非空，则其右子树上所有结点的键值均大于根结点的键值；\n    ● 左、右子树本身就是二叉查找树。\n    设二叉查找树采用二叉链表存储结构，链表结点类型定义如下：\n    typedef struct BiTnode{\n    int key_value;    //结点的键值为非负整数\n    struct BiTnode*left,*right;    //结点的左、右子树指针\n    }*BSTree;\n    函数find_key(root,key)的功能是用递归方式在给定的二叉查找树(root指向根结点)中查找键值为key的结点并返回结点的指针；若找不到，则返回空指针。\n    [C函数]\n    BSTree find_key(BSTree root, Int key)\n    {\n    if(______)\n    return NULL;\n    else\n    if(key==root-＞key_value)\n    return ______;\n    else if(key＜root-＞key_value)\n    return ______;\n    else\n    return ______;\n    }\n【问题1】请将函数find_key中应填入横线处的字句填写完整。\n【问题2】若某二叉查找树中有n个结点，则查找一个给定关键字时，需要比较的结点个数取决于______。', '[]', '[]', '[\"null\"]', '', 10, 'HARD', '二叉树', 2, '2026-05-12 14:12:34', '2026-05-12 14:12:44', 0);
INSERT INTO `question` VALUES (6, 2, 'SHORT_ANSWER', '题目4：阅读以下说明和C++程序，将应填入______处的字句填写完整。\n【说明】\n    设计希赛IT教育研发中心的工资管理系统，该中心主要有3类人员：经理、销售员和销售经理。要求存储这些人员的编号、姓名和月工资，计算月工资并显示全部信息。月工资计算办法是：经理拿固定月薪8000元；销售员拿固定工资1000元，然后再按当月销售额的4%提成；销售经理既拿固定月工资也领取销售提成，固定月工资为5000元，销售提成为所管辖部门当月销售总额的5‰。\n    按要求设计一个基类employee、销售员类salesman、经理类manager、销售经理类salesmanager。\n    程序4-1是类employee的模块内容，程序4-2是类salesman的模块内容，程序4-3是类manager的模块内容，程序4-4是类salesmanager的模块内容。在主测试程序中，输入张三所管部门月销售量10000后的输出结果如下：\n     \n    [程序4-1]\n    #include＜iostream.h＞\n    #include＜string.h＞\n    class employee\n    {\n    protected:\n    int no;\n    char*name;\n    float salary;\n    public:\n    employee(int num, char*ch)\n    { no=num;\n    name=ch;\n    salary=0;}\n    virtual void pay()=0;\n    virtual void display()\n    { cout＜＜\"编号:\"＜＜no＜＜endl;\n    cout＜＜\"本月工资:\"＜＜salary＜＜endl;}\n    };\n    [程序4-2]\n    class salesman: ______\n    {\n    protected:\n    float commrate, sales;\n    public:\n    salesman(int num, char*ch): employee(num, ch)\n    { commrate=0.04;}\n    void pay()\n    { cout＜＜name＜＜\"本月销售额:\";\n    cin＞＞sales;\n    salary=sales*commrate+1000;}\n    void display()\n    { cout＜＜\"销售员:\"＜＜name＜＜endl;\n    employee::display();}\n    };\n    [程序4-3]\n    class manager: ______\n    {\n    protected:\n    float monthpay;\n    public:\n    manager(int num, char*ch): employee(num, ch)\n    { monthpay=8000;}\n    void pay()\n    { salary=monthpay;}\n    void display()\n    { cout＜＜\"经理:\"＜＜name＜＜endl;\n    emptoyee::display(); }\n    };\n    [程序4-4]\n    class salesmanager: ______\n    {\n    public:\n    salesmanager(int num, char*ch): ______\n    { monthpay=5000;\n    commrate=0.005;}\n    void pay()\n    { cout＜＜name＜＜\"所管部门月销售量:\";\n    cin＞＞sales;\n    ______}\n    void display()\n    { cout＜＜\"销售经理:\"＜＜name＜＜endl;\n    ______}\n    };\n    void main()      //主测试函数\n    { salesmanager p1(1001,\"张三\");\n    p1.pay();\n    p1.display();\n    }\n', '[\"https://picup.candlesun.xyz/exam/questions/2026/05/12/aehzx6q5mwgdk7bewv5oa7jydhztpzvk.jpg\"]', '[]', '[\"null\"]', '', 10, 'HARD', 'C语言程序', 2, '2026-05-12 14:14:35', '2026-05-12 14:14:35', 0);
INSERT INTO `question` VALUES (7, 2, 'SHORT_ANSWER', '阅读以下关于软件架构设计与评估的叙述，在答题纸上回答问题1和问题2。\n[说明]\n某公司拟开发-套机器学习应用开发平台,支持用户使用浏览器在线进行基于机器学习的智能应用开发活动。\n该评台的核心应用场景是用户通过拖拽算法组件灵活定义机器学习流程，采用自助方式进行智能应用设计、实现与部署，并可以开发新算法组件加入平台中。在需求分析与架构设计阶段,公司提出的需求和质量属性描述如下:\n(a)平台用户分为算法工程师、软件工程师和管理员等三种角色，不同角色的功能界面有所不同；\n(b)平台应该具备数据库保护措施，能够预防核心数据库被非授权用户访问；\n(c)平台支持分布式部署,当主站点断电后，应在20秒内将请求重定向到备用站点；\n(d)平台支持初学者和高级用户两种界面操作模式，用户可以根据自己的情况灵活选择合适的模式；\n(e)平台主站点宕机后，需要在15秒内发现错误并启用备用系统；\n(f)在正常负载情况下，机器学习流程从提交到开始执行，时间间隔不大于5秒；\n(g)平台支持硬件扩容与升级，能够在3人天内完成所有部署与测试工作；\n(h)平台需要对用户的所有操作过程进行详细记录，便于审计工作；\n(i)平台部署后，针对界面风格的修改需要在3人天内完成;\n(j)在正常负载情况下，平台应在0.5秒内对用户的界面操作请求进行响应；\n(k)平台应该与目前国内外主流的机器学习应用开发平台的界面风格保持一致；\n(l)平台提供机器学习算法的远程调试功能，支持算法工程师进行远程调试。\n在对平台需求、质量属性描述和架构特性进行分析的基础上，公司的架构师给出了三种候选的架构设计方案，公司目前正在组织相关专家对平台架构进行评估。\n[问题1] (9分)\n在架构评估过程中，,质量属性效用树(utility tree)是对系统质属性进行识别和优先级排序的重要工具。 请将合适的质量属性名称域入图1-1中(1)、(2)空白处,并从题干中的(a)-(i)\n中选择合适的质量属性描述,填入(3)-(6)空白处，完成该平台的效用树。\n \n[问题2] (16分)\n针对该系统的功能，赵工建议采用解释器(interpreter)架构风格，李工建议采用管道过滤器(ppe-and-hlter)的架构风格，王工则建议采用隐式调用(implicit invocation)架构风格。请\n针对平台的核心应用场景，从机器学习流程定义的灵活性和学习算法的可扩展性两个方面对三种架构风格进行对比与分析，并指出该平台更适合采用哪种架构风格。\n', '[\"https://picup.candlesun.xyz/exam/questions/2026/05/15/dyraky67wv736v4bzbim9soa9l5azfe1.png\"]', '[]', '[\"null\"]', '', 25, 'HARD', '软件架构设计与评估', 1, '2026-05-15 09:39:04', '2026-05-15 09:45:54', 0);
INSERT INTO `question` VALUES (8, 2, 'SHORT_ANSWER', '阅读以下关于软件系统设计与建模的叙述，在答题纸上回答问题1至问题3。\n[说明]\n某医院拟委托软件公司开发一套预约挂号管理系统，以便为患者提供更好的就医体验，为医院提供更加科学的预约管理。本系统的主要功能描述如下：(a)注册登录,(b)信息浏览，(c)账号 管理，(d)预约挂号，(e)查询与取消预约，(F)号 源管理，(g)报告 查询，(h)预约管理，(i)报表管理和(j)信用管理等。\n[问题1] (6 分)\n若采用面向对象方法对预约挂号管理系统进行分析,得到如图2-1所示的用例图。请将合适的参与者名称填入图2-1中的(1)和(2)处，使用题干给出的功能描述(a)~(j)，完善用例(3)~(12)的名称，将正确答案填在答题纸上。\n \n[问题2] (10分)\n预约人员(患者)登录系统后发起预约挂号请求，进入预约界面。进行预约挂号时使用数据库访问类获取医生的相关信息，在数据库中调用医生列表，并调取医生出诊时段表，将医生出诊时段反馈到预的界面，并显示给预的人员;预约人员选择医生及就诊时间后确认预的，系统返网预约结果，并向用户显示是否预约成功。\n采用面向对象方法对预约挂号过程进行分析，得到如图2-2所示的顺序图，使用题干中给出的描述，完善图2-2中对象(1),及消息(2)~(4)的名称，将正确答案填在普题纸上请简要说明在描述对象之间的动态交互关系时，协作图与顺序图存在哪些区别。\n \n[问题3] (9分)\n采用面向对象方法开发软件，通常需要建立对象模型、动态模型和功能模型，请分别介绍这3种模型,并详细说明它们之间的关联关系，针对上述模型，说明哪些模型可用于软件的需求分析？\n', '[\"https://picup.candlesun.xyz/exam/questions/2026/05/15/rifswootnyz4an0w47fc03c79nzpr2a5.png\",\"https://picup.candlesun.xyz/exam/questions/2026/05/15/8oouzouuu6pnbj9kv9alx4xvbde0ez6k.png\"]', '[]', '[\"null\"]', '', 25, 'HARD', '软件系统设计与建模', 1, '2026-05-15 09:40:49', '2026-05-15 09:40:49', 0);
INSERT INTO `question` VALUES (9, 2, 'SHORT_ANSWER', '阅读以下关于数据库设计的叙述，在答题纸上回答问题1至问题3。\n[说明]\n某医药销售企业因业务发展，需要建立线上药品销售系统，为用户提供便捷的互联网药品销售服务、该系统除了常规药品展示、订单、用户交流与反馈功能外,还需要提供当前热销产品排名、评价分类管理等功能。\n通过对需求的分析，在数据管理上初步决定采用关系数据库(MySQL)和数据库缓存(Redis) 的混合架构实\n现。\n经过规范化设计之后，该系统的部分数据库表结构如下所示。\n供应商(供应商ID,供应商名称，联系方式，供应商地址) ;\n药品(药品ID，药品名称，药品型号,药品价格,供应商ID) ;\n药品库存(药品ID,当前库存数量) ;\n订单(订单号码，药品ID,供应商ID,药品数量，订单金额) ;\n[问题1] (9分)\n在系统初步运行后，发现系统数据访问性能较差。经过分析，刘工认为原来数据库规范化设计后，关系表过于细分,造成了大量的多表关联查询，影响了性能。例如当用户查询商品信息时，需要同时显示该药品的信息、供应商的信息、当前库存等信息。\n为此，刘工认为可以采用反规范化设计来改造药品关系的结构，以提高查询性能。修改后的药品关系结构为:\n药品(药品ID,药品名称，药品型号，药品价格,供应商ID,供应商名称，当前库存数量) ;\n请用200字以内的文字说明常见的反规范化设计方法，并说明用户查询商品信息应该采用哪种反规范化设计方法。\n[问题2] (9分)\n王工认为，反规范化设计可提高查询的性能，但必然会带来数据的不一致性问题。请用200字以内的文字说明在反规范化设计中，解决数据不一致性问题的三种常见方法，并说明该系统应该采用哪种方法。\n[问题3] (7分)\n该系统采用了Redis来实现某些特定功能(如当前热销药品排名等)，同时将药品关系数据放到内存以提高商品查询的性能，但必然会造成Redis和MySQL的数据实时同步问题。\n(1) Redis的数据类型包括String、 Hash、 List、 Set和ZSet等，请说明实现当前热销药品排名的功能应该选择使用哪种数据类型。\n(2)请用200字以内的文字解释说明解决Redis和MySQL数据实时同步问题的常见方案。\n', '[]', '[]', '[\"null\"]', '', 25, 'HARD', '数据库设计', 1, '2026-05-15 09:42:31', '2026-05-15 09:42:31', 0);
INSERT INTO `question` VALUES (10, 2, 'SHORT_ANSWER', '阅读以下关于Web系统架构设计的教述，在答题纸上回答问题1至问题3。\n[说明]\n某公司拟开发-个智能家居管理系统，该系统的主要功能需求如下：1)用户可使用该系统客户端实现对家居设备的控制，且家居设备可向客户端反馈实时状态；2)支持家居设备数据的实时存储和查询；3)基于用户数据，挖掘用户生活习惯，向用户提供家居设备智能化使用建议。\n基于上述需求，该公司组建了项目组，在项目会议上,张工给出了基于家庭网关的传统智能家居管理系统的设计思路，李工给出了基于云平台的智能家居系统的设计思路。经过深入讨论，公司决定采用李工的设计思路。\n[问题1] (8分)\n请用400字以内的文字简要描述基于家庭网关的传统智能家居管理系统和基于云平台的智能家居管理系统在网关管理、数据处理和系统性能等方面的特点，以说明项目组选择李工设计思路的原因。\n[问题2] (12分)\n请从下面给出的(a) ~ (j) 中进行选择，补充完善图5-1中空(1) ~ (6)处的内容，协助李工完成该系统的架构设计方案。\n \n(a) Wi-FI\n(b) 蓝牙\n(c)驱动程序\n(d)数据库\n(e)家庭网关\n(f)云平台\n(g)微服务\n(h)用户终端\n(i)鸿蒙\n(j)TCP/IP\n[问题3](5分)\n该系统需实现用户终端与服务端的双向可靠通信，请用300字以内的文字从数据传输可靠性的角度对比分析TCP和UDP通信协议的不同，并说明该系统应采用哪种通信协议。\n', '[\"https://picup.candlesun.xyz/exam/questions/2026/05/15/svd6t0tuwx4ozxmhdhdn3xuciium1cu8.png\"]', '[]', '[\"null\"]', '', 25, 'HARD', 'Web系统架构设计', 1, '2026-05-15 09:43:59', '2026-05-15 09:43:59', 0);
INSERT INTO `question` VALUES (11, 3, 'SINGLE_CHOICE', '【2021下架构真题第01题：绿色】\n01.某计算机系统页面大小为4K，进程P1的页面变换表如下图所示，看P1要访问数据的逻辑地址为十六进制1B1AH,那么该逻辑地址经过变换后,其对应的物理地址应为十六进制（ ）\n\nA.1B1AH\nB.3B1AH\nC.6B1AH\nD.8B1AH\n', '[]', '[\"选项A\",\"选项B\",\"选项C\",\"选项D\"]', '[\"选项C\"]', '', 5, 'HARD', '页式存储', 2, '2026-05-15 09:53:58', '2026-05-20 15:49:37', 0);
INSERT INTO `question` VALUES (12, 3, 'SINGLE_CHOICE', '【2021下架构真题第02题：绿色】\n02.嵌入式实时操作系统与一般操作系统相比，具备许多特点。以下不属于嵌入式实时操作系统特点的是（）\nA.可剪裁性\nB.实时性\nC.通用性\nD.可固化性\n', '[]', '[\"选项A\",\"选项B\",\"选项C\",\"选项D\"]', '[\"选项C\"]', '', 5, 'HARD', '嵌入式实时操作系统', 2, '2026-05-15 09:54:38', '2026-05-20 15:49:34', 0);
INSERT INTO `question` VALUES (13, 4, 'SHORT_ANSWER', '试题一\n    【算法说明】\n    下面是一段插入排序的程序，将R[k+1]插入到R[1...k]的适当位置。\n    R[0]=R[k+1]；\n    j=k；\n    while(R[j]＞R[0])\n    {\n    R[j+1]=R[j]；\n    j--；\n    }\n    R[j+1]=R[0]；\n    【流程图】\n     \n    【测试用例设计】\n    (while循环次数为0、1、2次)\n                      测试用例表\n       \n1、    【问题1】\n    指出算法的流程图中(1)～(3)处的内容。\n2、    【问题2】\n    指出测试用例设计中(4)～(9)处的内容。', '[\"https://picup.candlesun.xyz/exam/questions/2026/05/15/0gugkg6fwk8jl6nypqm04vz74pvvh5m1.png\",\"https://picup.candlesun.xyz/exam/questions/2026/05/15/o9qtnbvdkqgf8c9pyjvab5gcgxn71ail.png\"]', '[]', '[\"试题一 1、(1)F  (2)R[j+1]=R[0]  (3)T 　 　  2、(4)①③  (5)①②②③  (6)①②②③  (7)＞＜  (8)1  (9)3\"]', '[解析] 本题考查用路径覆盖方法为算法设计足够的测试用例，属于基本概念的送分题。这类题拿分的关键是考生平时对于理论的理解和临场的细心。', 20, 'HARD', '插入排序的程序', 2, '2026-05-15 09:54:51', '2026-05-15 10:21:42', 0);
INSERT INTO `question` VALUES (14, 3, 'SINGLE_CHOICE', '【2021下架构真题第03题：黄色】\n03.人工智能技术已成为当前国际科技竞争的核心技术之一，AI芯片是占据人工智能市场的法宝。AI 芯片有别于通常处理器芯片，它应具备四种关键特征。（ ）是AI芯片的关键特点。\nA.新型的计算范式、信号处理能力、低精度设计、专用开发工具\nB.新型的计算范式、训练和推断、大数据处理能力、可重构的能力\nC.训练和推断、大数据处理能力、可定制性，专用开发工具\nD.训练和推断、低精度设计、新型的计算范式、图像处理能力\n', '[]', '[\"选项A\",\"选项B\",\"选项C\",\"选项D\"]', '[\"选项C\"]', '', 5, 'HARD', '芯片', 2, '2026-05-15 09:55:08', '2026-05-20 15:49:30', 0);
INSERT INTO `question` VALUES (15, 3, 'SINGLE_CHOICE', '【2021下架构真题第04题：绿色】\n04.前趋图(Precedence Graph)是一个有向无环图，记为：→=(Pi.Pj)Pi must complete before Pj may strat), 假设系统中进程P={P1, P2,P3, P4, P5, P6, P7, P8}， 且进程的前驱图如下:\n\nA\n→={ (P1，P2) , (P3，P1) , (P4，P1), (P5，P2) , (P5，P3) , (P6，P4) , (P7，P5), (P7，P6) , (P5，P6), (P4，P5), (P6，P7) , (P7，P6) }\nB\n→={(P1，P2) , (P1，P3) , (P2，P5) , (P2，P3) , (P3，P4) , (P3，P5) ,(P4，P5) , (P5，P6) , (P5，P7) , (P8，P5), (P6，P7) , (P7，P8) }\nC\n→={(P1，P2) , (P1，P3) , (P2，P3), (P2，P5) , (P3，P4) , (P3，P5),(P4，P6) , (P5，P6) , (P5，P7) ,(P5，P8), (P6，P8) , (P7，P8) }\nD\n→={ (P1，P2) , (P1，P3) , (P2，P3), (P2，P5) , (P3，P6) , (P3，P4) ,(P4，P7) ,(P5，P6) , (P6，P7),(P6，P5),(P7，P5) , (P7，P8) }\n\n', '[]', '[\"选项A\",\"选项B\",\"选项C\",\"选项D\"]', '[\"选项C\"]', '', 5, 'HARD', '前趋图', 2, '2026-05-15 09:57:41', '2026-05-20 15:49:25', 0);
INSERT INTO `question` VALUES (16, 3, 'SINGLE_CHOICE', '\n【2021下架构真题第05题：绿色】\n05.假设系统中互斥资源R的可用数为25。T0时刻进程P1、P2、p3、P4对资源R的最大需求数、已分配资源数和尚需资源数的情况如表a所示，若P1和P3分别申请资源R数为1和2，则系统（ ）。\n表aT0时刻进程对资源的需求情况\n\nA.只能先给P1进行分配，因为分配后系统状态是安全的\nB.只能先给P3进行分配，因为分配后系统状态是安全的\nC.可以时后P1、P3.进行分配，因为分配后系统状态是安全的\nD.不能给P3进行分配，因为分配后系统状态是不安全的\n', '[]', '[\"选项A\",\"选项B\",\"选项C\",\"选项D\"]', '[\"选项B\"]', '', 5, 'HARD', '互斥资源', 2, '2026-05-15 09:58:46', '2026-05-20 15:49:18', 0);
INSERT INTO `question` VALUES (17, 4, 'SHORT_ANSWER', '试题二\n    【说明】\n    本流程图实现从成绩文件生成学生成绩一览表。\n    某中学某年级的学生成绩数据(分数)登录在成绩文件10中，其记录格式见表1：\n　　　　　　　　　　　　　　　表1 \n学号	姓名	课程1成绩	课程2成绩	……	课程6成绩\n 　　由该成绩文件生成见表2的学生成绩一览表。生成的学生成绩一览表按学号升序排列。表中的名次是指该生相应课程在年级中的名次。\n 　　　　　　　　　　　　　　　　　表2  \n学号	姓名	课程1      	课程2      	……      	            课程6\n		成绩	名次	成绩	名次	……	……	成绩	名次\n									\n									\n　　流程图中的顺序文件F0是学生成绩文件，F0文件经处理1处理后产生顺序文件F，然后经过处理2至处理4对文件F进行处理和更新。在处理5中，仅对文件F的纪录进行学生成绩一览表的编排输出，不进行排序和增加名次等处理。\n3、    【问题1】\n    流程图中文件F的纪录格式设定为见表3形式：\n　　　　　　　　　　　　　　　　　　表3\n学号	姓名	课程代码	①	②\n其中的①、②应定义为何种数据项?\n4、【问题2】\n简述处理2、处理3和处理4作何种处理，若有排序处理则需指明排序的键及序(升序或降序)。\n    【流程图】\n    ', '[\"https://picup.candlesun.xyz/exam/questions/2026/05/15/scdiv80zfjlwu46gyoijp2vlvf9uahux.png\",\"https://picup.candlesun.xyz/exam/questions/2026/05/15/3us05dic9y7zhkalm6obe23h9trsxvlv.png\"]', '[]', '[\"试题二 3、①成绩  ②名次 　 　  4、课程代码按升序排列、成绩按降序排列\"]', '[解析] 处理2对每个文件F进行排序。处理3对每个课程代码，确定学生名次，写入文件F的相应字段。处理4按学号(升序)、课程代码(升序)对文件F排序。', 20, 'HARD', '实现从成绩文件生成学生成绩', 2, '2026-05-15 09:58:59', '2026-05-15 10:23:07', 0);
INSERT INTO `question` VALUES (18, 3, 'MULTIPLE_CHOICE', '【2021下架构真题第06题：红色】\n06.某企业开发信息管理系统平台进行E-R图设计，人力部门定义的是员工实体具有属性：员工号、姓名、性别、出生日期、联系方式和部门，培训部门定义的培训师实体具有属性：培训师号，姓名和职称，其中职称={初级培训师，中级培训师，高级培训师}，这种情况属于（ ）。\n在合并E-R图时，解决这一冲突的方法是（ ）。\nA.属性冲突\nB.结构冲突\nC.命名冲突\nD.实体冲突\n》\nA.员工实体和培训师实体均保持不变\nB.保留员工实体、删除培训师实体\nC.员工实体中加入职称属性，剔除培训师实体\nD.将培训师实体所有属性并入员工实体，删除培训师实体\n', '[]', '[\"选项1A\",\"选项1B\",\"选项1C\",\"选项1D\",\"选项2A\",\"选项2B\",\"选项2C\",\"选项2D\"]', '[\"选项1B\"]', '', 5, 'HARD', 'E-R图', 2, '2026-05-15 10:01:29', '2026-05-20 15:48:36', 0);
INSERT INTO `question` VALUES (19, 5, 'SHORT_ANSWER', '题目1、阅读下列说明和图，回答问题1至问题5。\n【说明】\n通常由于机房电磁环境复杂，运维人员很少在现场进行运维工作，在出现安全事件需要紧 急处理时，需要运维人员随时随地运程开展处置工作。\nSSH(安全外壳协议）是一种加密的网络传输协议，提供安全方式访问远程计算机。李工作为公司的安全运维工程师，也经常使用 SSH 远程登录到公司的 Ubuntu18.04 服务器中进行安全维护。\n【问题 1】SSH 协议默认工作的端口号是多少?\n【问题 2】网络设备之间的远程运维可以采用两种安全通信方式:一种是 SSH，还有一种是什么?\n【问题 3】日志包含设备、系统和应用软件的各种运行信息，是安全运维的重点关注对象。李工在定 期巡检服务器的 SSH 日志时，发现了以下可疑记录:\nJul 22 17:17:52 humen systed-logiad [1182] : Waching sytem buttonson/dev/input/eveto (Power Button)\nJul 22 17:17:52 humen systed-logiad [1182] : Waching sytem\nbuttonson/dev/input/evet1(AT Translated Set 2 keyboard)\nJul 23 09:33:41 humen sshd [5423] : pam_unix (sshd:auth) authentication failure, logame= uid=0 euid=0 tty=ssh ruser=rhost=192.168.107.13o user=humen\nJul 23 09:33:43 humen sshd [5423] : Failed password for humen from192.168.107.130\nport 40231 ssh2\nJul 23 09:33:43 humen sshd [5423] : Connection closed by authenticating user humen 192.168.107.130 port 40231[preauth]\nJul 23 09:33:43 humen sshd [5425] : pam_unix (sshd:auth) :authentication failure;\nlogname= uid=0 euid=0 tty=ssh ruser=rhost=192.168.107.130 user=humen\nJul 23 09:33:45 humen sshd [5425] : Failed password for humen from 192.168.107.130\nport 37223 ssh2\nJul 23 09:33:45 humen sshd [5425] : Connection closed by authenticating user humen192.168.107.130 port 37223 [preauth]\nJul 23 09:33:45 humen sshd [5427] : pam_unix (sshd:auth) :authentication\nfailure;logname= uid=0 euid=0 tty=ssh ruser=rhost=192.168.107.130 user=humen\nJul 23 09:33:47 humen sshd [5427] : Failed password for humen from 192.168.107.130\nport 41365 ssh2\nJul 23 09:33:47 humen sshd [5427] : Connection closed by authenticating user humen 192.168.107.130 port 41365 [preauth]\nJul 23 09:33:47 humen sshd [5429] : pam_unix (sshd:auth) :authentication\nfailure;logname= uid=0 euid=0 tty=ssh ruser=rhost=192.168.107.130 user=humen Jul 23 09:33:49 humen sshd [5429] : Failed password for humen from 192.168.107.130\nport 45627 ssh2\nJul 23 09:33:49 humen sshd [5429] : Connection closed by authenticating user humen 192.168.107.130 port 45627 [preauth]\nJul 23 09:33:49 humen sshd [5431] : pam_unix (sshd:auth) :authentication\nfailure;logname= uid=0 euid=0 tty=ssh ruser=rhost=192.168.107.130 user=humen Jul 23 09:33:51 humen sshd [5431] : Failed password for humen from192.168.107.130 port 42271 ssh2\nJul 23 09:33:51 humen sshd [5431] : Connection closed by authenticating user humen\n192.168.107.130 port 42271 [preauth]\nJul 23 09:33:51 humen sshd [5433] : pam_unix (sshd:auth) :authentication failure;logname= uid=0 euid=0 tty=ssh ruser=rhost=192.168.107.130 user=humen Jul 23 09:33:53 humen sshd [5433] : Failed password for humen from 192.168.107.130\nport 45149 ssh2\nJul 23 09:33:53 humen sshd [5433] : Connection closed by authenticating user humen 192.168.107.130 port 45149[preauth]\nJul 23 09:33:54 humen sshd [5435] : Accepted password for humen from 192.168.107.130\nport 45671 ssh2\nJul 23 09:33:54 humen sshd [5435] : pam_unix (sshd:auth) : session opened for user humen by (uid=o)\n(1) 请问李工打开的系统日志文件的路径和名称?\n(2) 李工怀疑有黑客在攻击该系统，请给出判断攻击成功与否的命令以便李工评估攻击的影响。\n\n【问题 4】经过上次 SSH 的攻击事件之后，李工为了加强口令安全，降低远程连接风险，考虑采用免密证书登录。\n(1) Linux 系统默认不允许证书方式登录，李工需要实现免密证书登录的功能,应该修改哪个配置文件?请给出文件名。\n(2) 李工在创建证书后需要拷贝公钥信息到服务器中。他在终端输入了以下拷贝命令，请说 明命令中\">>\"的含义。\nssh xiaoming@server cat/home/xiaoming/.ssh/id_rsa.pub> >authorized_keys\n(3) 服务器中的 authorized_keys 文件详细信息如下，请给出文件权限的数字表示。\n(4）李工完成 SSH 配置修改后需要重启服务，请给出 systemctl 重启 SsH 服务的命令。(5)在上述服务配置过程中，配置命令中可能包含各种敏感信息，因此在配置结束后应及时清除历史命令信息，请给出清除系统历史记录应执行的命令。\n【问题 5】SSH 之所以可以实现安全的远程访问，归根结底还是密码技术的有效使用。对于 SSH 协议， 不管是李工刚开始使用的基于口令的认证还是后来的基于密钥的免密认证，都是密码算法和密码协议在为李工的远程访问保驾护航。请问上述安全能力是基于对称密码体制还是非对称密码体制来实现的?', '[]', '[]', '[\"NULL\"]', '', 10, 'HARD', '计算机网络', 1, '2026-05-15 10:01:57', '2026-05-15 10:01:57', 0);
INSERT INTO `question` VALUES (20, 3, 'SINGLE_CHOICE', '【2021下架构真题第07题：红色】\n07.一般说来，SoC称为系统级芯片，也称片上系统，它是一个有专用且标的集成电路产品：以下关于SoC不正确的说法是（ ）。\nA.SoC是一种技术，是以实际的、确定的系统功能开始，到软/硬件划分，并完成设计的整个过程\nB.SoC是一款具有运算能力的处理器芯片，可面向特定用途进行定制的标准产品\nC.SoC是信息系统核心的芯片集成，是将系统关键部件集成在一块芯片上，完成信息系统的核心功能\nD.SoC是将微处理器、模拟IP核、数字IP核和存储器(或片外存储控制接口)集成在单一芯片上，是面向特定用途的标准产品\n', '[]', '[\"选项A\",\"选项B\",\"选项C\",\"选项D\"]', '[\"选项B\"]', '', 5, 'HARD', '芯片', 2, '2026-05-15 10:02:27', '2026-05-20 15:48:23', 0);
INSERT INTO `question` VALUES (21, 5, 'SHORT_ANSWER', '题目2、微服务提倡将单一应用程序划分成一组小的服务，服务之间互相协调、互相配合，为用户提供最终价值。每个服务运行在其独立的进程中，服务与服务间采用轻量级的通信机制互相沟通。在微服务架构中，每个服务都是一个相对独立的个体，每个服务都可以选择适合于自身的技术来实现。每个服务的部署都是独立的，这样就可以更快地对特定部分的代码进行部署。\n请围绕“论微服务架构及其应用”论题，依次从以下三个方面进行论述。\n1、概要叙述你所参与管理或开发的软件项目，以及你在其中所承担的主要工作。\n2、简要描述微服务优点。\n3、具体阐述如何基于微服务架构进行软件设计实现的。', '[]', '[]', '[\"NULL\"]', '', 10, 'HARD', '微服务架构', 1, '2026-05-15 10:02:53', '2026-05-15 10:02:53', 0);
INSERT INTO `question` VALUES (22, 4, 'SHORT_ANSWER', '下面是一段插入排序的程序，将R[k+1]插入到R[1...k]的适当位置。\n    R[0]=R[k+1]；\n    j=k；\n    while(R[j]＞R[0])\n    {\n    R[j+1]=R[j]；\n    j--；\n    }\n    R[j+1]=R[0]；\n【测试用例设计】\n    (while循环次数为0、1、2次)\n1、    【问题1】\n    指出算法的流程图中(1)～(3)处的内容。\n2、    【问题2】\n    指出测试用例设计中(4)～(9)处的内容。\n', '[\"https://picup.candlesun.xyz/exam/questions/2026/05/15/aqzv2uci1vprihn9o0rovcf2n9d7xaj3.gif\",\"https://picup.candlesun.xyz/exam/questions/2026/05/15/a56vdebfrty4qtucsgx0sv77sh957swq.gif\"]', '[]', '[\"1、(1)F  (2)R[j+1]=R[0]  (3)T\"]', '', 20, 'HARD', '插入排序', 1, '2026-05-15 10:03:17', '2026-05-15 10:12:50', 1);
INSERT INTO `question` VALUES (23, 3, 'SINGLE_CHOICE', '【2021下架构真题第08题：红色】\n08.基于网络的数据库系统(Netware Database System,NDB)是基于4G/5G的移动通信之上，在逻辑上可以把嵌入式设备看作远程服务器的一个客户端。以下有关NDB的叙述中，不正确的是（ ）。\nA.NDB主要由客户端、通信协议和远程服务器等三部分组成\nB.NDB的客户端主要负责提供接口给嵌入式程序，通信协议负责规范客户端与远程服务器之间的通信，远程服务器负责维护服务器上的数据库数据\nC.NDB具有客户端小、无需支持可剪裁性、代码可重用等特点\nD.NDB是以文件方式存储数据库数据。即数据按照一定格式储存在磁盘中，使用时由应用程序通过相应的驱动程序甚至直接对数据文件进行读写\n', '[]', '[\"选项A\",\"选项B\",\"选项C\",\"选项D\"]', '[\"选项C\"]', '', 5, 'HARD', '数据库系统', 2, '2026-05-15 10:03:50', '2026-05-20 15:48:18', 0);
INSERT INTO `question` VALUES (24, 5, 'SHORT_ANSWER', '题目3、阅读以下说明和流程图，填补流程图中的空缺处，将解答填写完整。\n【说明】\n    下面的流程图采用公式ex=1+x+x2/2!+x3/3!+x4/4!+…+xn/n!+…计算ex的近似值。\n    设x位于区间(0，1)，该流程图的算法要点是逐步累积计算每项Xn/n!的值(作为T)，再逐步累加T值得到所需的结果S。当T值小于10-5时，结束计算。\n【流程图】', '[\"https://picup.candlesun.xyz/exam/questions/2026/05/15/cpmmv3kwt1is84dwcjqx5zl4stcizsmp.jpg\"]', '[]', '[\"NULL\"]', '', 10, 'EASY', '流程图', 1, '2026-05-15 10:04:19', '2026-05-15 10:04:19', 0);
INSERT INTO `question` VALUES (25, 3, 'SINGLE_CHOICE', '【2021下架构真题第09题：红色】\n09.以下关于以太网交换机转发表的叙述中，正确的是（ ）。\nA.交换机的初始MAC地址表为空\nB.交换机接收到数据帧后，如果没有相应的表项，则不转发该帧\nC.交换机通过读取输入帧中的目的地址添加相应的MAC地址表项\nD.交换机的MAC地址表项是静态增长的，重启时地址表清空\n', '[]', '[\"选项A\",\"选项B\",\"选项C\",\"选项D\"]', '[\"选项A\"]', '', 5, 'HARD', '交换机', 2, '2026-05-15 10:04:41', '2026-05-20 15:48:12', 0);
INSERT INTO `question` VALUES (26, 4, 'SHORT_ANSWER', '试题三\n5、    有下列关于运动会管理系统的ER图，如图所示。图中矩形表示实体，圆表示属性，双圆表示关键字属性，菱形表示实体之间的关系。假定已通过下列SQL语言建立了基本表。 CREATE TABLE ATHLETE\n    ANAME CHAR(20)，\n    ASEX CHAR(1)，\n    ATEAM CHAR(20))；\n    CREATE TABLE |TEM\n    (INO CHAR(6)NOT NULL，\n    INAME CHAR(20)，\n    ITIME CHAR(12)，\n    IPLACE CHAR(20)；\n    CREATE TABLE GAMES\n    (ANO CHAR(6)NOTNULL，\n    INO CHAR(6)NOT NULL，\n    SCORRE CHAR(10))；\n    为了答题的方便，图中的实体和属性同时给出了中英文两种文字，回答问题时只需写出英文名即可。\n    【E-R图】\n    \n    【问题】\n    填充下列SQL程序1～4中的(1)～(7)，使它们分别完成相应的功能：\n    程序1：统计参加比赛时男运动员人数。\n    SELECT  (1)  \n    FROM ATHLETE\n    WHERE ASEX=\'M\'；\n    程序2：查100872号运动员参加的所有项目及其比赛时间和地点。\n    SELECT ITEM，INO，IN A ME，ITIME，IPLACE\n    FROM GAMES，ITEM\n    WHERE  (2)  ；\n    AND  (3)  ；\n    程序3：查参加100035项目的所有运动员名单。\n    SELECT ANO，ANAME，ATEAM\n    FROM ATHLETE\n    WHERE  (4)  ；\n    (SELECT  (4)    (5)  \n    FROM GAMES\n    WHERE GAMES ANO=ATHLETE.ANO AND INO=\'100035\')；\n    程序4：建立运动员成绩视图。\n      (6)  ATHLETE-SCORE\n    AS SELECT ATHLETE，ANO，ANAME，ATEAM，INAME，SCORE\n    FORM  (7)  WHERE ATHLETE.ANO=GAMES. ANO AND GAMES.INO=ITEM.INO；', '[\"https://picup.candlesun.xyz/exam/questions/2026/05/15/18863fx9y98zqj440jic4fcusfwnquhy.png\",\"https://picup.candlesun.xyz/exam/questions/2026/05/15/o5zzy5mognjdvk7lmsxc0zkax5l1yv3e.png\",\"https://picup.candlesun.xyz/exam/questions/2026/05/15/a7oc27flc1rbdhdhrqubdackg29jur1o.png\"]', '[]', '[\"(1)COUNT(*)(若答COUNT或COUNT，得2分)     (2)GAMES.INO=IFEM.INO     (3)GAMES.ANO=‘100872’(注：(2)、(3)可互换、无前缀得1分)     (4)EXISTS     (5)*或ANO或INO或SCORE或后3个列名的任意组合     (6)CREATEVIEW     (7)ATHLETE，ITEM，GAMES(3项可交换。)     注：(4)、(5)也可为     (4)ANOIN     (5)ANO\"]', '[解析] 本题是关于系数据库标准语言——SQL(Structured Query Language)语言的题目，由题目中给出的ER图可知3个表中，ATH- LETE和ITEM是基本表，表ATHETE的主键是运动员编号ANO，表I-TEM的主键是项目编号INO，表GAMES是一个视图，以ANO、INO为外键。\n    程序1统计参加比赛的男运动员人数，也就是表ATHLETE中， AEX=\'M\'的记录的个数，所以要用到库函数COUNT(*)。这里要注意的是COUNT与COUNT(*)区别，COUNT的功能是对一列中的值计算个数，而COUNT(*)才是计算数据库中记录的个数。所以填空①的答案为“COUNT(*)”。\n    程序的2统计100872号运动员参加的所有项目及比赛时间和地点，所以SELECT后面的内容是项目编号ITEM.INO、项目名称INAME时间ITIME及地点IPLACE。统计涉及比赛表GAMES和项目表ITEM，所以FROM后面的内容为GAMES、ITEM。本题考的是连接查询，所谓连接查询指的是涉及两个以上的表的查询。由于是统计100872号运动员参加的所有项目及比赛时间和地点，所以查询条件中必然有 GAMES.INO=\'100872\'(程序中引用到字段时，若字段名在各个表中是惟一的，则可以把字段名前的表名去掉，否则，应当加上表名作为前缀，以免引起混淆)。由于GAMES表中只有比赛的成绩，那些关于项目的数据必须从项目表ITEM中取得，所以还应该有两个表之间的关联，即 GAMES.INO=TYEM.INO。所以填空②和③可交换，不影响查询结果。\n    程序3要求查参加100035项目的所有运动员名单。分析查询表达式，必首先查询GAMES表，找出参加100035项目的那些运动员的编号ANO，即GAMES.ANO=ATHLETE.ANO AND INO=\'100035\'，然后再根据查询到的运动员号ANO从ATHLETE表中抽取运动员的数据。所以填空④的答案为“EXISTS”或“ANOIN”，填空⑤的答案为“ANO”。\n    程序4要求建立运动员成绩视图。建立视图的命令为CREATE- VIEW，所以填空⑥的答案一定是“CREATEVIEW”。建立的是运动员成绩视图，那么一定涉及运动员情况、运动员参加的项情况和该项目的成绩，所以要用到ATHLETE、ITEM和GAMES这3个表，因此FROM子句后为ATHLETE、GAMES、ITEM，3个表可以是任意次序，不影响结果。', 20, 'HARD', 'SQL语言建立了基本表', 2, '2026-05-15 10:04:59', '2026-05-15 10:19:46', 0);
INSERT INTO `question` VALUES (27, 3, 'SINGLE_CHOICE', '【2021下架构真题第10题：绿色】\n10.lnternet网络核心采取的交换方式为（ ）\nA.分组交换\nB.电路交换\nC.虚电路交换\nD.消息交换\n', '[]', '[\"选项A\",\"选项B\",\"选项C\",\"选项D\"]', '[\"选项A\"]', '', 5, 'HARD', '交换', 2, '2026-05-15 10:05:32', '2026-05-20 15:47:51', 0);
INSERT INTO `question` VALUES (28, 1, 'FILL_BLANK', '题目4、阅读以下说明、Java代码，将应填入______处的字句填写完整。\n【说明】\n    IC卡和200卡都是从电话卡派生的。下面的程序将电话卡定义为抽象类。其中balance为双精度变量，代表电话卡中的余额；cardNumber是长整型变量，代表电话卡的卡号；password是整型变量，代表电话卡的密码；connectNumber是字符串变量，代表电话卡的接入号码；connected是布尔变量，代表电话是否接通。\n    performDial()实现各种电话接通后的扣除费用的操作。其中200卡每次通话扣除0.5元的通话费用和附加费用；IC卡每次通话扣除0.9元的通话费。TimeLeft()方法用于测试电话卡余额及还可以拨打电话的次数。performConnection()用于电话接入操作，如果卡号和密码正确，则接通；否则，接不通。\n【程序】\n    abstmct class PhoneCard\n    {\n    double batace;\n      ①   performDial();\n    double getBalance()\n    {return balance; }\n    double TimeLeft()\n    {\n    double current=balance;\n    int times=0;\n    do\n    {\n      ②  \n    times++;\n    }while(balance＞=0);\n    balance=current;\n    return times-1;\n    }\n    }\n    abstract class Number_PhoneCard extends PhoneCard\n    {\n    long cardNumber;\n    int password;\n    String connectNumber;\n    Boolean connected;\n    Boolean performConnection(tong cn, int pw)\n    {\n    if(cn==cardNumber &&   ③  )\n    {\n    connected=true;\n    return true;\n    }\n    else return false;\n    }\n    }\n    class IC_Card   ④  \n    {\n    boolean performDial()\n    {\n    if(balance＞0.9)\n    {\n    balance-=0.9;\n    return true:\n    }\n    else return false;\n    }\n    }\n    class D200_Card   ④  \n    {\n    static double additoryFee;\n    static{ additoryFee=0.1; }\n    boolean performDial()\n    {\n    if(balance>(0,5+additoryFee))\n    {\n      ⑤  \n    return true;\n    }\n    else  return false;\n    }\n    }', '[]', '[]', '[\"NULL\"]', '', 10, 'HARD', 'Java程序', 1, '2026-05-15 10:05:48', '2026-05-15 10:05:48', 0);
INSERT INTO `question` VALUES (29, 4, 'SHORT_ANSWER', '试题二\n    【说明】\n    本流程图实现从成绩文件生成学生成绩一览表。\n    某中学某年级的学生成绩数据(分数)登录在成绩文件10中，其记录格式见表1：\n表1 \n学号	姓名	课程1成绩	课程2成绩	……	课程6成绩\n\n由该成绩文件生成见表2的学生成绩一览表。生成的学生成绩一览表按学号升序排列。表中的名次是指该生相应课程在年级中的名次。\n表2  \n学号	姓名	课程1      	课程2      	……      	            课程6\n		成绩	名次	成绩	名次	……	……	成绩	名次\n									\n									\n流程图中的顺序文件F0是学生成绩文件，F0文件经处理1处理后产生顺序文件F，然后经过处理2至处理4对文件F进行处理和更新。在处理5中，仅对文件F的纪录进行学生成绩一览表的编排输出，不进行排序和增加名次等处理。\n3、    【问题1】\n    流程图中文件F的纪录格式设定为见表3形式：\n　　　　　　　　　　　　　　　　　　表3\n学号	姓名	课程代码	①	②\n其中的①、②应定义为何种数据项?\n4、【问题2】\n简述处理2、处理3和处理4作何种处理，若有排序处理则需指明排序的键及序(升序或降序)。\n    【流程图】\n', '[\"https://picup.candlesun.xyz/exam/questions/2026/05/15/h7r6yqculccfxqvur7vgg493ct4sdt9a.gif\"]', '[]', '[\"3、①成绩  ②名次 　 　  4、课程代码按升序排列、成绩按降序排列 [解析] 处理2对每个文件F进行排序。处理3对每个课程代码，确定学生名次，写入文件F的相应字段。处理4按学号(升序)、课程代码(升序)对文件F排序。\"]', '', 20, 'HARD', '流程图', 1, '2026-05-15 10:07:33', '2026-05-15 10:12:48', 1);
INSERT INTO `question` VALUES (30, 5, 'SINGLE_CHOICE', 'UML有4种事物，它们是', '[]', '[\"A．结构事物、行为事物、状态事物、说明事物\",\"B．组织事物、分解事物、行为事物、状态事物\",\"C．结构事物、行为事物、分组事物、注释事物\",\"D．组织事物、行为事物、分组事物、说明事物\"]', '[\"C．结构事物、行为事物、分组事物、注释事物\"]', '', 5, 'HARD', 'UML', 1, '2026-05-15 10:09:53', '2026-05-15 10:16:14', 0);
INSERT INTO `question` VALUES (31, 4, 'SHORT_ANSWER', '试题三\n5、    有下列关于运动会管理系统的ER图，如图所示。图中矩形表示实体，圆表示属性，双圆表示关键字属性，菱形表示实体之间的关系。假定已通过下列SQL语言建立了基本表。 CREATE TABLE ATHLETE\n    ANAME CHAR(20)，\n    ASEX CHAR(1)，\n    ATEAM CHAR(20))；\n    CREATE TABLE |TEM\n    (INO CHAR(6)NOT NULL，\n    INAME CHAR(20)，\n    ITIME CHAR(12)，\n    IPLACE CHAR(20)；\n    CREATE TABLE GAMES\n    (ANO CHAR(6)NOTNULL，\n    INO CHAR(6)NOT NULL，\n    SCORRE CHAR(10))；\n    为了答题的方便，图中的实体和属性同时给出了中英文两种文字，回答问题时只需写出英文名即可。\n【问题】\n    填充下列SQL程序1～4中的(1)～(7)，使它们分别完成相应的功能：\n    程序1：统计参加比赛时男运动员人数。\n    SELECT  (1)  \n    FROM ATHLETE\n    WHERE ASEX=\'M\'；\n    程序2：查100872号运动员参加的所有项目及其比赛时间和地点。\n    SELECT ITEM，INO，IN A ME，ITIME，IPLACE\n    FROM GAMES，ITEM\n    WHERE  (2)  ；\n    AND  (3)  ；\n    程序3：查参加100035项目的所有运动员名单。\n    SELECT ANO，ANAME，ATEAM\n    FROM ATHLETE\n    WHERE  (4)  ；\n    (SELECT  (4)    (5)  \n    FROM GAMES\n    WHERE GAMES ANO=ATHLETE.ANO AND INO=\'100035\')；\n    程序4：建立运动员成绩视图。\n      (6)  ATHLETE-SCORE\n    AS SELECT ATHLETE，ANO，ANAME，ATEAM，INAME，SCORE\n    FORM  (7)  WHERE ATHLETE.ANO=GAMES. ANO AND GAMES.INO=ITEM.INO；\n', '[\"https://picup.candlesun.xyz/exam/questions/2026/05/15/3h8yat0arkvrhaotsypa5bwkdlq45o8d.gif\"]', '[]', '[\"试题三  5、    (1)COUNT(*)(若答COUNT或COUNT，得2分)     (2)GAMES.INO=IFEM.INO     (3)GAMES.ANO=‘100872’(注：(2)、(3)可互换、无前缀得1分)     (4)EXISTS     (5)*或ANO或INO或SCORE或后3个列名的任意组合     (6)CREATEVIEW     (7)ATHLETE，ITEM，GAMES(3项可交换。)     注：(4)、(5)也可为     (4)ANOIN     (5)ANO\"]', '[解析] 本题是关于系数据库标准语言——SQL(Structured Query Language)语言的题目，由题目中给出的ER图可知3个表中，ATH- LETE和ITEM是基本表，表ATHETE的主键是运动员编号ANO，表I-TEM的主键是项目编号INO，表GAMES是一个视图，以ANO、INO为外键。\n    程序1统计参加比赛的男运动员人数，也就是表ATHLETE中， AEX=\'M\'的记录的个数，所以要用到库函数COUNT(*)。这里要注意的是COUNT与COUNT(*)区别，COUNT的功能是对一列中的值计算个数，而COUNT(*)才是计算数据库中记录的个数。所以填空①的答案为“COUNT(*)”。\n    程序的2统计100872号运动员参加的所有项目及比赛时间和地点，所以SELECT后面的内容是项目编号ITEM.INO、项目名称INAME时间ITIME及地点IPLACE。统计涉及比赛表GAMES和项目表ITEM，所以FROM后面的内容为GAMES、ITEM。本题考的是连接查询，所谓连接查询指的是涉及两个以上的表的查询。由于是统计100872号运动员参加的所有项目及比赛时间和地点，所以查询条件中必然有 GAMES.INO=\'100872\'(程序中引用到字段时，若字段名在各个表中是惟一的，则可以把字段名前的表名去掉，否则，应当加上表名作为前缀，以免引起混淆)。由于GAMES表中只有比赛的成绩，那些关于项目的数据必须从项目表ITEM中取得，所以还应该有两个表之间的关联，即 GAMES.INO=TYEM.INO。所以填空②和③可交换，不影响查询结果。\n    程序3要求查参加100035项目的所有运动员名单。分析查询表达式，必首先查询GAMES表，找出参加100035项目的那些运动员的编号ANO，即GAMES.ANO=ATHLETE.ANO AND INO=\'100035\'，然后再根据查询到的运动员号ANO从ATHLETE表中抽取运动员的数据。所以填空④的答案为“EXISTS”或“ANOIN”，填空⑤的答案为“ANO”。\n    程序4要求建立运动员成绩视图。建立视图的命令为CREATE- VIEW，所以填空⑥的答案一定是“CREATEVIEW”。建立的是运动员成绩视图，那么一定涉及运动员情况、运动员参加的项情况和该项目的成绩，所以要用到ATHLETE、ITEM和GAMES这3个表，因此FROM子句后为ATHLETE、GAMES、ITEM，3个表可以是任意次序，不影响结果。\n', 5, 'HARD', 'ER图', 1, '2026-05-15 10:10:38', '2026-05-15 10:12:47', 1);
INSERT INTO `question` VALUES (32, 4, 'SHORT_ANSWER', '例如：设散列函数为Hash(Key)=Key mod 7，记录的关键字序列为15,14,21,87,97,293,35,24,149,19,63,16,103,77,5,153,145,356,51,68,705,453，建立的散列文件内容如图所示。', '[\"https://picup.candlesun.xyz/exam/questions/2026/05/15/fcjnza6xhegm6ralee76twkr1v2ukopx.png\",\"https://picup.candlesun.xyz/exam/questions/2026/05/15/th05jclqlm4so2ephucpbh4qkfopmisx.png\",\"https://picup.candlesun.xyz/exam/questions/2026/05/15/hnrv1yrcg8kxb1q5ae3mq5l0wwwd0rt8.png\"]', '[]', '[\"6、(1) Index=NewElemKey % P     (2) i＜ITEMS     (3) front=&Bucket[Index]     (4) k==ITEMS     (5) t==NULL，或!t     (6) front-＞Link=s\"]', '[解析] \n    本题考查元素的散列存储。\n    元素作散列存储时，首先用设定的散列函数计算元素的存储位置。在本题中，将元素存储在预先设定的基桶或根据需要申请的溢出桶中，只要基桶中有空闲单元，就将新元素NewElemKey插入在基桶中，若基桶中无空闲单元，则看是否存在溢出桶，若存在，则在溢出桶中查找空闲单元，若不存在溢出桶或溢出桶中无空闲单元，则申请一个溢出桶并存入新元素。\n    在基桶查找空闲单元时使用的桶号为Index，可知空(1)处应填入“Index= NewElemKey % P”。显然，一旦在基桶中找到空闲单元，即“Bucket[Index].KeyData[i]== NULLKEY”(0≤i＜ITEMS)，则可将元素NewElemKey放入Bucket[Index].KeyData[i],至此元素已经插入散列桶中，函数可返回，因此空(2)处应填入“i＜ITEMS”；反之，若在基桶中没有找到空闲单元，则需查找溢出桶。“t=Bucket[Index].Link”，指针t首先指向桶号Index的第一个溢出桶。下面的代码即为在溢出桶中查找空闲单元。\n     if(t!=NULL)  {/*有溢出桶*/\n           while(t!=NULL){\n             for(k=0; k＜ITEMS;k++)\n                 if(t-＞KeyData[k]==NULLKEY){/*在溢出桶链表中找到空闲单元*/\n                 t-＞KeyData[k]=NewElemKey;   break;\n                }/*if*/\n              front=t;\n               if(  (4)  )t=t-＞Link;\n               else break;\n           }/*while*/\n     }/*if*/\n     由于每个溢出桶都可以存储ITEMS个元素，所以在溢出桶中查找空闲单元与在基桶中的查找过程相同，代码如下。\n    for(k=0;k＜ITEMS;k++)\n             if(t-＞KcyData[k]==NULLKEY){/*在溢出桶链表中找到空闲单元*/\n              t-＞KeyData[k]=NewElemKey;  break;\n             }/*if*/\n    若在指针t指向的溢出桶中找到空闲单元则插入元素，否则，由“t=t-＞Link”得到下一个溢出桶的指针，因此“k＜ITEMS”可作为是否在当前溢出桶中找到空闲单元的判定条件。\n    显然，在桶号Index的基桶和其所有溢出桶都已满的情况下，t的值为空指针。此时才需要申请新的溢出桶并建立链接关系，因此在上面查找溢出桶中空闲单元时，进行指针t的后移“t=t-＞Link”前应先用front记录t的值，以便于后面建立链接关系。所以空(3)处应给front置初值，即“front=&Bucket[Index]”，空(4)填入“k==ITEMS”，空(5)填入“t＝NULL”。空(6)处建立新申请溢出桶的链接关系“front-＞Link=s”。', 20, 'HARD', '哈希表（散列表）的链地址法（拉链法）', 2, '2026-05-15 10:10:59', '2026-05-15 10:12:45', 1);
INSERT INTO `question` VALUES (33, 1, 'SINGLE_CHOICE', '设有数组A[m，n]，数组的每个元素占3个存储单元，m的值为1～8，n的值为 1～10。数组从首地址W开始顺序存放，当以列为主序存放时，元素A[5，8]的存储首地址为', '[]', '[\" A．W+222\",\"B．W+225\",\"C．W+141\",\"D．W+180\"]', '[\"D．W+180\"]', '', 5, 'HARD', '数组', 1, '2026-05-15 10:11:07', '2026-05-15 10:16:06', 0);
INSERT INTO `question` VALUES (34, 5, 'SINGLE_CHOICE', '利用动态规划方法求解每对节点之间的最短路径问题(all pairs shortest path problem)时，设有向图 G=＜V,E＞共有n个节点，节点编号1～n，设C是G的成本邻接矩阵，用Dk(I,j)即为图G中节点i到j并且不经过编号比k还大的节点的最短路径的长度(Dn(i,j)即为图G中节点i到j的最短路径长度)，则求解该问题的递推关系式为', '[]', '[\"A．Dk(I,j)=Dk-1(I,j)+C(I,j)\",\"B．Dk(I,j)=Dk-1(I,k)+Dk-1(k,j)\",\"C．Dk(I,j)=min{Dk-1(I,j),Dk-1(I,j)+C(I,j)}\",\"D．Dk(I,j)=min{Dk-1(I,j),Dk-1(I,K)+Dk-1(k,j)}\"]', '[\"D．Dk(I,j)=min{Dk-1(I,j),Dk-1(I,K)+Dk-1(k,j)}\"]', '', 5, 'HARD', '动态规划', 1, '2026-05-15 10:12:26', '2026-05-15 10:16:00', 0);
INSERT INTO `question` VALUES (35, 1, 'SINGLE_CHOICE', '在有些程序设计语言中，过程调用和响应调用需执行的代码的绑定直到运行时才进行，这种绑定称为', '[]', '[\"A．静态绑定\",\"B．动态绑定\",\"C．过载绑定\",\"D．强制绑定\"]', '[\"B．动态绑定\"]', '', 5, 'EASY', '程序设计', 1, '2026-05-15 10:13:18', '2026-05-15 10:15:56', 0);
INSERT INTO `question` VALUES (36, 1, 'SINGLE_CHOICE', '已知G4=(VT{a，\'，\'，(，)}，VN={S,L，L\'}，S,P)，其中P为，\n    S→(L)|a|ξ\n    L→SL\'\n    L\'→，SL\'|ξ\n    FOLLOW(S)是', '[]', '[\"A．{\'，\'，ξ，}}\",\"B．{\'，\'#，}}\",\"C．{a，\'，\'ξ，}}\",\"D．{a，\'，\'，#}\"]', '[\"B．{\'，\'#，}}\"]', '', 5, 'HARD', '程序', 1, '2026-05-15 10:14:38', '2026-05-15 10:15:51', 0);
INSERT INTO `question` VALUES (37, 4, 'SHORT_ANSWER', '试题五\n7、    【说明】\n    下面的程序先构造Point类，再顺序构造Ball类。由于在类Ball中不能直接存取类Point中的xCoordinate及yCoordinate属性值，Ball中的toString方法调用Point类中的toStrinS方法输出中心点的值。在MovingBsll类的toString方法中，super.toString调用父类Ball的toString方法输出类Ball中声明的属性值。\n    【Java代码】\n    //Point.java文件\n    public class Point{\n          private double xCoordinate;\n          private double yCoordinate；\n          public Point(){}\n          public Point(double x,double y){\n                xCoordinate=x;\n                yCoordinate=y;\n          }\n          public String toStrthg(){\n                return\"(\"+Double.toString(xCoordinate)+\",\"\n                +Double.toString(yCoordinate)+\")\";\n          }\n          //other methods\n    }\n    //Ball.java文件\n    public class Ball{\n          private  (1)  ;//中心点\n          private double radius;//半径\n          private String color;//颜色\n          public Ball(){}\n          public Ball(double xValue, double yValue, double r){\n                //具有中心点及其半径的构造方法\n                center=  (2)  ;//调用类Point中的构造方法\n                radius=r;\n          }\n          public Ball(double xValue, double yValue, double r, String c){\n                //具有中心点、半径和颜色的构造方法\n                  (3)  ;//调用3个参数的构造方法\n                color=c;\n          }\n          public String toString(){\n                return \"A ball with center\"+center.toString()\n                +\",radius \"+Double.toString(radius)+\",color\"+color;\n          }\n          //other methods\n     }\n    class MovingBall  (4)  {\n          private double speed;\n          public MovingBall(){}\n          public MoyingBall(double xValue, double yValue, double r, String c, double s){\n               　  (5)  ;//调用父类Ball中具有4个参数的构造方法\n                 speed=s;\n          }\n          public String toString(){\n                return super.toString()+\",speed\"+Double.toString(speed);\n          }\n          //other methods\n    }\n    public class test{\n          public static void main(String args[]){\n                MovingBall mb=new MovingBall(10,20,40,\"green\",25);\n                System.out.println(mb);\n          }\n    }\n\n\n', '[\"https://picup.candlesun.xyz/exam/questions/2026/05/15/exwb4i0ika3n8j4fe57kbyzmq2u5a1ne.png\",\"https://picup.candlesun.xyz/exam/questions/2026/05/15/73z729cp1tfe7z5tnmipz5rzb6d9g0mi.png\"]', '[]', '[\"7、(1) Point center     (2) new Point(xValue，yValue)     (3) this(xValue，yValue，r)     (4) extends Ball     (5) super(xValue，yValue，r，c)\"]', '[解析] 在类Ball的有参数构造函数中，对成员变量center通过调用Point类的构造方法初始化，而center在类Ball中尚未声明。结合注释可得空(1)是将center变量声明为Point对象引用，故空(1)应填Point。空(2)是调用Point类的构造函数，根据题意，此处应将xValue和yValue作为参数调用类Point的有参数构造函数，故空(2)应填new Point(xValue，yValue)。\n    根据注释，空(3)是调用类Ball的有3个参数的构造方法，而其所在方法本身就是类Ball的一个构造方法，因此可用this来调用自身的构造方法，故空(3)应填this(xValue，yValue，r)。\n    根据题述“在MovingBall类的toString方法中，super.toString调用父类Ball的toString方法输出类Ball中声明的属性值”，可知类MovingBall是类Ball的子类，因此空(4)应填extends Ball。\n    根据注释，空(5)是调用父类Ball中具有4个参数的构造方法，通过super关键字实现，故空(5)应填super(xValue，yValue，r，c)。', 20, 'HARD', '面向对象编程', 2, '2026-05-15 10:15:09', '2026-05-15 10:18:11', 0);
INSERT INTO `question` VALUES (38, 6, 'SINGLE_CHOICE', '1、 UML有4种事物，它们是  ()  。\n ', '[]', '[\"选项A结构事物、行为事物、状态事物、说明事物\",\"选项B组织事物、分解事物、行为事物、状态事物\",\"选项C结构事物、行为事物、分组事物、注释事物\",\"选项D组织事物、行为事物、分组事物、说明事物\"]', '[\"选项C结构事物、行为事物、分组事物、注释事物\"]', '[解析] UML中有4种事物：结构事物、行为事物、分组事物和注释事物。请参阅有关教材理解这4种事物。', 5, 'HARD', 'UML', 1, '2026-05-15 10:15:15', '2026-05-15 10:15:15', 0);
INSERT INTO `question` VALUES (39, 6, 'SINGLE_CHOICE', '2、设有数组A[m，n]，数组的每个元素占3个存储单元，m的值为1～8，n的值为 1～10。数组从首地址W开始顺序存放，当以列为主序存放时，元素A[5，8]的存储首地址为  ()  ', '[]', '[\"选项AW+222 \",\"选项BW+225\",\"选项CW+141\",\"选项DW+180\"]', '[\"选项DW+180\"]', '以列为主序存储就是把二维数组中的数据一行一行地顺次存入存储单元。二维数组A[1..m，1..n)若以列为主序存储，那么A的任意一个元素A[i][j]的存储首地址 Loc(i，j)可由下式确定：\n                 Loc(i，j)=Loc(1，1)+[m×(j-1)+i-1)×b其中，Loc(1，1)是第一个元素A[1][1]的首地址，b是每个元素占用的存储单元个数。此题中代入数值得：Loc(5，8)＝W+[8×(8-1)+5-1)×3=W+180，于是选择D\n', 5, 'EASY', '数组', 1, '2026-05-15 10:16:26', '2026-05-15 10:16:26', 0);
INSERT INTO `question` VALUES (40, 6, 'SINGLE_CHOICE', '利用动态规划方法求解每对节点之间的最短路径问题(all pairs shortest path problem)时，设有向图 G=＜V,E＞共有n个节点，节点编号1～n，设C是G的成本邻接矩阵，用Dk(I,j)即为图G中节点i到j并且不经过编号比k还大的节点的最短路径的长度(Dn(i,j)即为图G中节点i到j的最短路径长度)，则求解该问题的递推关系式为  (62)  。', '[]', '[\"选项A Dk(I,j)=Dk-1(I,j)+C(I,j)\",\"选项B Dk(I,j)=Dk-1(I,k)+Dk-1(k,j)\",\"选项C Dk(I,j)=min{Dk-1(I,j),Dk-1(I,j)+C(I,j)}\",\"选项D Dk(I,j)=min{Dk-1(I,j),Dk-1(I,K)+Dk-1(k,j)}\"]', '[\"选项D Dk(I,j)=min{Dk-1(I,j),Dk-1(I,K)+Dk-1(k,j)}\"]', '[解析] 设Pk(I,j)表示从i到j并且不经过编号比k还大的节点的最短路径，那么Pk(I,j)有以下两种可能：\n    ①Pk(I,j)经过编号为k的节点，此时Pk(I,j)可以分为从i到k和从k到j的两段，易知Pk(I,j)的长度为Dk-1(I,k)+Dk-1(k,j)；\n    ②Pk(I,j)不经过编号为k的节点，此时Pk(I,j)的长度为Dk-1(I,j)。\n    因此，求解该问题的递推关系式为：Dk(I,j)=min{Dk-1(I,j),Dk-1(I,k)+Dk-1(k,j)}。\n', 5, 'EASY', '动态规划方法', 1, '2026-05-15 10:17:57', '2026-05-15 10:17:57', 0);
INSERT INTO `question` VALUES (41, 6, 'SINGLE_CHOICE', '4、在有些程序设计语言中，过程调用和响应调用需执行的代码的绑定直到运行时才进行，这种绑定称为______。', '[]', '[\"选项A．静态绑定\",\"选项B．动态绑定\",\"选项C．过载绑定\",\"选项D．强制绑定\"]', '[\"选项B．动态绑定\"]', '[解析] 动态绑定(Dynamic Binding)是面向对象程序设计语言中的一种机制，绑定是在运行时进行的，即一个给定的过程调用和响应调用需执行的代码的结合直到调用发生时才进行。这种机制实现了方法的定义与具体的对象无关，而对方法的调用则可以关联于具体的对象。', 5, 'EASY', '程序设计语言', 1, '2026-05-15 10:19:06', '2026-05-15 10:19:06', 0);
INSERT INTO `question` VALUES (42, 6, 'SINGLE_CHOICE', '9、 已知G4=(VT{a，\'，\'，(，)}，VN={S,L，L\'}，S,P)，其中P为，\n    S→(L)|a|ξ\n    L→SL\'\n    L\'→，SL\'|ξ\n    FOLLOW(S)是  ()  。\n', '[]', '[\"选项A．{\'，\'，ξ，}}\",\"选项B．{\'，\'#，}}\",\"选项C．{a，\'，\'ξ，}}\",\"选项D．{a，\'，\'，#}\"]', '[\"选项B．{\'，\'#，}}\"]', '解析] 终结符A的FOLLOW集合定义如下：\n    FOLLOW(A)={a|S …Aa…，a∈VT，A∈VN}，若S …A，则规定#∈FOLLOW(A)，约定#为句子结束标记。\n    给定一个文法，求FOLLOW(A)的算法如下：\n    ① 对于文法的开始符号S，置#于FOLLOW(S)中；\n    ② 若A→αBβ∈P，则把FIRST(β)中的所有非∈—元素都加至FOLLOW(B)中；\n    ③ 若A→αB∈P，或A→αBβ∈P而β ξ，则把FOLLOW(A)加至FOLLOW(B)中。\n    重复使用上述3条规则，直到每个FOLLOW集合不再增大为止。\n    非形式地说，一个非终结符的FOLLOW集合，就是从文法开始符号可以推导出的所有含A句型中紧跟在A之后的所有终结符号。\n    首先，因为S是开始符号，所以，置#于FOLLOW(S)中。根据L→SL\'，把FIRST(L\')中的所有非ξ一元素都加至FOLLOW(S)中，即把\',\'加至FOLLOW(S)中。又根据L→SL\'和 L\' ξ，把FOLLOW(L)加至FOLLOW(S)中，即把\')\'加至FOLLOW(S)中。最后， FOLLOW(S)为{#，\',\'，)}。\n', 5, 'EASY', '编译原理', 1, '2026-05-15 10:21:51', '2026-05-15 10:21:51', 0);
INSERT INTO `question` VALUES (43, 1, 'FILL_BLANK', '数据库的数据体系结构分为三个级别，  5  最接近用户，是用户能看到的数据特性。   6  涉及到所有用户的数据定义。最接近物理存储设备的是  7  。这些模型用数据库的  8  描述。\n5、A. 关系级    B. 概念级    C. 内部级    D. 外部级\n6、A. 关系级    B. 概念级    C. 内部级    D. 外部级\n7、A. 关系级    B. 概念级    C. 内部级    D. 外部级\n8、A. SQL    B. DML    C. DDL    D. ER', '[]', '[]', '[\"5、D 　 　 6、B 　 　 7、C 　 　  8、C\"]', '[解析] 数据库的数据体系结构分为三个级别：外部级，概念级和内部级。外部级最接近用户，是用户能看到的数据特性。概念级涉及到所有用户的数据定义。内部级最接近物理存储设备，涉及到实际数据的存储方式。这些模型用数据库的数据定义语言(DDL)描述。', 20, 'EASY', '数据库', 1, '2026-05-15 10:23:03', '2026-05-15 10:23:03', 0);
INSERT INTO `question` VALUES (44, 6, 'SINGLE_CHOICE', '已知完全二叉树有30个节点，则整个二叉树有______个度为1的节点', '[]', '[\"选项A．0  \",\"选项B. 1  \",\"选项C ．2\",\"选项D．不确定\"]', '[\"选项B. 1  \"]', '完全二叉树：除了最外层，其余层上的节点数目都达到最大值，而第h层上的节点集中存放在左侧树中。 \n    n0是度为0的节点总数(即叶子节点数)，n1是度为1的节点总数，n2是度为2的节点总数，由二叉树的性质可知：n0=n2+1，则完全二叉树的节点总数n为：n=n0+n1+n2，由于完全二叉树中度为1的节点数只有两种可能0或1，由此可得n0=(n+1)/2或n0=n/2，合并成一个公式为：n0=(n+1)/2，即可根据完全二叉树的节点总数计算出叶子节点数。 \n    在此，该完全二叉树有30个节点，则n0为15，n2为14，n1即为1，即度为1的节点个数为1。\n', 5, 'EASY', '二叉树', 1, '2026-05-15 10:26:42', '2026-05-15 10:26:42', 0);
INSERT INTO `question` VALUES (45, 4, 'SHORT_ANSWER', '试题四\n6、例如：设散列函数为Hash(Key)=Key mod 7，记录的关键字序列为15,14,21,87,97,293,35,24,149,19,63,16,103,77,5,153,145,356,51,68,705,453，建立的散列文件内容如图所示。\n    \n    为简化起见，散列文件的存储单位以内存单元表示。\n    函数InsertToHashTable(int NewElemKey)的功能是：将元素NewEIemKey插入散列桶中，若插入成功则返回0，否则返回-1。\n    采用的散列函数为Hash(NewElemKey)=NewElemKey % P，其中P为设定的基桶数目。\n    函数中使用的预定义符号如下：\n    #define NULLKEY  -1           /*散列桶的空闲单元标识*/\n    #define P   7                 /*散列文件中基桶的数目*/\n    #define ITEMS  3              /*基桶和溢出桶的容量*/\n    typedef struct BucketNode{    /*基桶和溢出桶的类型定义*/\n            int KcyData[ITEMS];\n            struct BucketNode *Link;\n    }BUCKET;\n    BUCKET Bucket[P];             /*基桶空间定义*/\n[函数]\n    int lnsertToHashTable(int NewElemKey){\n    /*将元素NewElemKey插入散列桶中，若插入成功则返回0，否则返回-1*/\n    /*设插入第一个元素前基桶的所有KeyData[]、Link域已分别初始化为NULLKEY、\n      NULL*/\n        int Index;    /*基桶编号*/\n    int i,k;\n          BUCKET *s,*front,*t;\n           (1)  ;\n         for(i=0; i＜ITEMS;i++)/*在基桶查找空闲单元，若找到则将元素存入*/\n           if(Bucket[Index].KeyData[i]=NULLKEY){\n               Bucket[Index].KeyData[i]=NewElemKey;  break;\n           }\n         if(  (2)  ) return 0;\n    /*若基桶已满，则在溢出桶中查找空闲单元，若找不到则申请新的溢出桶*/\n           (3)  ;        t=Bucket[Index].Link;\n         if(t!=NULL)   {/*有溢出桶*/\n             while (t!=NULL){\n               for(k=0; k＜ITEMS; k++)\n                   if(t-＞KeyData[k]=NULLKEY){/*在溢出桶链表中找到空闲单元*/\n                   t-＞KeyData[k]=NewElemKey;   break;\n                   }/*if*/\n                front=t;\n                if(  (4)  )t=t-＞Link;\n                else break;\n               }/*while*/\n         }/*if*/\n         if(  (5)  ) {/*申请新溢出桶并将元素存入*/\n            s=(BUCKET*)malloe(sizeof(BUCKET));\n             if(!s)  return-1;\n             s-＞Link=NULL;\n             for(k=0; k＜ITEMS; k++)\n                s-＞KeyData[k]=NULLKEY;\n             s-＞KeyData[0]=NewElemKey;\n                (6)  ;\n         }/*if*/\n        return 0;\n    }/*InsertToHashTable*/\n', '[\"https://picup.candlesun.xyz/exam/questions/2026/05/15/ji1xhshjd553iw56y3owqo9gd5sbjbk6.png\",\"https://picup.candlesun.xyz/exam/questions/2026/05/15/lz0jstmnc9gker4p9qrt2btj0ptonkn1.png\"]', '[]', '[\"6、(1) Index=NewElemKey % P     (2) i＜ITEMS     (3) front=&Bucket[Index]     (4) k==ITEMS     (5) t==NULL，或!t     (6) front-＞Link=s\"]', '[解析] \n    本题考查元素的散列存储。\n    元素作散列存储时，首先用设定的散列函数计算元素的存储位置。在本题中，将元素存储在预先设定的基桶或根据需要申请的溢出桶中，只要基桶中有空闲单元，就将新元素NewElemKey插入在基桶中，若基桶中无空闲单元，则看是否存在溢出桶，若存在，则在溢出桶中查找空闲单元，若不存在溢出桶或溢出桶中无空闲单元，则申请一个溢出桶并存入新元素。\n    在基桶查找空闲单元时使用的桶号为Index，可知空(1)处应填入“Index= NewElemKey % P”。显然，一旦在基桶中找到空闲单元，即“Bucket[Index].KeyData[i]== NULLKEY”(0≤i＜ITEMS)，则可将元素NewElemKey放入Bucket[Index].KeyData[i],至此元素已经插入散列桶中，函数可返回，因此空(2)处应填入“i＜ITEMS”；反之，若在基桶中没有找到空闲单元，则需查找溢出桶。“t=Bucket[Index].Link”，指针t首先指向桶号Index的第一个溢出桶。下面的代码即为在溢出桶中查找空闲单元。\n     if(t!=NULL)  {/*有溢出桶*/\n           while(t!=NULL){\n             for(k=0; k＜ITEMS;k++)\n                 if(t-＞KeyData[k]==NULLKEY){/*在溢出桶链表中找到空闲单元*/\n                 t-＞KeyData[k]=NewElemKey;   break;\n                }/*if*/\n              front=t;\n               if(  (4)  )t=t-＞Link;\n               else break;\n           }/*while*/\n     }/*if*/\n     由于每个溢出桶都可以存储ITEMS个元素，所以在溢出桶中查找空闲单元与在基桶中的查找过程相同，代码如下。\n    for(k=0;k＜ITEMS;k++)\n             if(t-＞KcyData[k]==NULLKEY){/*在溢出桶链表中找到空闲单元*/\n              t-＞KeyData[k]=NewElemKey;  break;\n             }/*if*/\n    若在指针t指向的溢出桶中找到空闲单元则插入元素，否则，由“t=t-＞Link”得到下一个溢出桶的指针，因此“k＜ITEMS”可作为是否在当前溢出桶中找到空闲单元的判定条件。\n    显然，在桶号Index的基桶和其所有溢出桶都已满的情况下，t的值为空指针。此时才需要申请新的溢出桶并建立链接关系，因此在上面查找溢出桶中空闲单元时，进行指针t的后移“t=t-＞Link”前应先用front记录t的值，以便于后面建立链接关系。所以空(3)处应给front置初值，即“front=&Bucket[Index]”，空(4)填入“k==ITEMS”，空(5)填入“t＝NULL”。空(6)处建立新申请溢出桶的链接关系“front-＞Link=s”。', 20, 'HARD', '散列函数', 2, '2026-05-15 10:26:59', '2026-05-15 10:26:59', 0);
INSERT INTO `question` VALUES (46, 6, 'FILL_BLANK', '数据库系统由数据库、  12  和硬件等组成，数据库系统是在  13  的基础上发展起来的。\n    数据库系统由于能减少数据冗余，提高数据独立性，并集中检查  14  ，由此获得广泛的应用。数据库提供给用户的接口是  15  ，它具有数据定义、数据操作和数据检查功能，可独立使用，也可以嵌入宿主语言使用。  16  语言已被国际标准化组织采纳为标准的关系数据语言。\n12、A．操作系统    B．文件系统\n    C．编译系统    D．数据库管理系统\n13、A．数据系统    B．文件系统\n    C．解释系统    D．数据库管理系统\n14、A．数据完整性    B．数据层次性\n    C．数据的操作性    D．数据兼容性\n15、A．数据库语言    B．过程化语言\n    C．宿主语言    D．面向对象语言\n16、A．QUEL    B．SEQUEL    C．SQL    D．ALPHA\n', '[]', '[]', '[\"12、D 13、B 　 　 14、A 　 　 15、A 　 　 16、C\"]', '[解析] 本题考查数据库的基础知识。数据库系统由数据库、数据库管理系统和硬件等组成，数据库系统是在文件系统的基础上发展起来的。数据库系统由于能减少数据冗余，提高数据独立性，并集中检查数据完整性，由此获得广泛的应用。数据库提供给用户的接口是数据库语言，它具有数据定义、数据操作和数据检查功能，可独立使用，也可以嵌入宿主语言使用。SQL语言已被国际标准化组织采纳为标准的关系数据语言。', 5, 'EASY', '数据库', 1, '2026-05-15 10:27:37', '2026-05-15 10:27:37', 0);
INSERT INTO `question` VALUES (47, 6, 'SINGLE_CHOICE', '17、已知一个线性表(38，25，74，63，52，48)，假定采用h(k)=k%6计算散列地址进行散列存储，若用线性探测的开放定址法处理冲突，则在该散列表上进行查找的平均查找长度为  ()  。', '[]', '[\"选项A．1.5\",\"选项B．1.7 \",\"选项C．2 \",\"选项D．2.3\"]', '[\"选项A．1.5\"]', '[解析] 用散列函数n(k)=k%6计算得到散列地址见下表。\n                         散列地址\n关键字\n散列地址\n38	25	74	63	52	48\n2	1	2	3	4	0\n 用线性探测的开放定址法处理冲突所构造得到的散列表见下表。\n 　　　　　　　　　　　　　　　散列表\n \n0	1	2	3	4	5\n48	25	38	74	63	52\n1	1	1	2	2	2\n　　该散查找次数列表的平均查找长度为(1×3+2×3)/6=1.5。\n', 5, 'EASY', '线性表', 1, '2026-05-15 10:29:35', '2026-05-15 10:35:16', 0);
INSERT INTO `question` VALUES (48, 6, 'SINGLE_CHOICE', '18、对含有n个互不相同元素的集合，同时找最大元和最小元至少需要  ()  次比较。', '[]', '[\"选项A．2n\",\"选项B．2(n-1) \",\"选项C．n-1 \",\"选项D．n+1\"]', '[\"选项C．n-1 \"]', '[解析] 按照下面的顺序查找算法，如果初始序列递增有序，则只需比较，n-1次；如果初始序列递减有序，则需比较2(n-1)次。因此，对含有n个互不相同元素的集合，同时找最大元和最小元至少需要比较n-1次，最多需要比较2(n-1)次。\n    max=min=r[0].key；\n    for(i=1；i＞n；i++)\n    if(r[i].key＞max)\n    max=r[i].key；    else if(r[i].key＜min)\n    min=r[i].key；\n', 5, 'EASY', '查找', 1, '2026-05-15 10:30:47', '2026-05-15 10:30:47', 0);
INSERT INTO `question` VALUES (49, 6, 'SINGLE_CHOICE', '19、直接选择排序的平均时间复杂度为  ()  ', '[]', '[\"选项A．O(n)\",\"选项B．O(nlogn) \",\"选项C．O(n2) \",\"选项D．O(logn)\"]', '[\"选项C．O(n2) \"]', '[解析] 本题主要考查排序算法的时间复杂度。排序算法的时间复杂度是用元素的平均比较次数和元素的平均移动次数来衡量的，它是评价排序算法的主要标准', 5, 'EASY', '排序', 1, '2026-05-15 10:31:54', '2026-05-15 10:31:54', 0);
INSERT INTO `question` VALUES (50, 6, 'SINGLE_CHOICE', '20、高级语言的语言处理程序分为解释程序和编译程序两种。解释程序处理源程序时，大多数采用______方法。', '[]', '[\"选项A．源程序语句被逐个直接解释执行\",\"选项B．先将源程序转化成某种中间代码，然后对这种代码解释执行\",\"选项C．先将源程序转化成目标代码，再执行\",\"选项D．以上方法都不是\"]', '[\"选项B．先将源程序转化成某种中间代码，然后对这种代码解释执行\"]', '解释程序是一种语言处理程序，在词法、语法和语义分析方面与编译程序的工作原理基本相同，但在运行时直接执行源程序或源程序的内部形式，即解释程序不产生源程序的目标程序，这点是它与编译程序的主要区别。', 5, 'EASY', '语言处理', 1, '2026-05-15 10:33:05', '2026-05-15 10:34:43', 0);
INSERT INTO `question` VALUES (51, 6, 'SINGLE_CHOICE', '21、面向对象设计的任务可以分成  ()  。', '[]', '[\"选项A．软件内部结构设计和外部结构设计\",\"选项B．软件内部交互设计和外部交互设计\",\"选项C．类设计和产品设计\",\"选项D．进程设计和界面设计\"]', '[\"选项C．类设计和产品设计\"]', '[解析] 面向对象设计的任务可以分成类设计和产品设计。', 5, 'EASY', '面向对象设计', 1, '2026-05-15 10:34:18', '2026-05-15 10:34:18', 0);

-- ----------------------------
-- Table structure for student_answer
-- ----------------------------
DROP TABLE IF EXISTS `student_answer`;
CREATE TABLE `student_answer`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_id` bigint NOT NULL,
  `paper_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `answer_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `question_score` int NOT NULL,
  `actual_score` int NULL DEFAULT 0,
  `objective_flag` int NULL DEFAULT 0,
  `correct_flag` int NULL DEFAULT 0,
  `marking_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `teacher_comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `submit_time` datetime NULL DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `deleted` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_answer_unique`(`exam_id` ASC, `student_id` ASC, `question_id` ASC) USING BTREE,
  INDEX `idx_student_answer_marking`(`marking_status` ASC) USING BTREE,
  INDEX `idx_student_answer_student`(`student_id` ASC) USING BTREE,
  INDEX `idx_student_answer_exam`(`exam_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_answer
-- ----------------------------
INSERT INTO `student_answer` VALUES (1, 1, 1, 1, 3, '[]', 10, 0, 0, 0, 'WAIT_MARKING', NULL, '2026-05-12 14:17:48', '2026-05-12 14:17:48', '2026-05-12 14:17:48', 0);
INSERT INTO `student_answer` VALUES (2, 1, 1, 3, 3, '[]', 10, 0, 0, 0, 'WAIT_MARKING', NULL, '2026-05-12 14:17:48', '2026-05-12 14:17:48', '2026-05-12 14:17:48', 0);
INSERT INTO `student_answer` VALUES (3, 1, 1, 5, 3, '[]', 10, 0, 0, 0, 'WAIT_MARKING', NULL, '2026-05-12 14:17:48', '2026-05-12 14:17:48', '2026-05-12 14:17:48', 0);
INSERT INTO `student_answer` VALUES (4, 1, 1, 6, 3, '[]', 10, 0, 0, 0, 'WAIT_MARKING', NULL, '2026-05-12 14:17:48', '2026-05-12 14:17:48', '2026-05-12 14:17:48', 0);
INSERT INTO `student_answer` VALUES (5, 3, 3, 7, 3, '[]', 25, 0, 0, 0, 'MARKED', '', '2026-05-15 09:53:49', '2026-05-15 09:53:48', '2026-05-15 09:54:41', 0);
INSERT INTO `student_answer` VALUES (6, 3, 3, 8, 3, '[]', 25, 0, 0, 0, 'MARKED', '', '2026-05-15 09:53:49', '2026-05-15 09:53:48', '2026-05-15 09:54:44', 0);
INSERT INTO `student_answer` VALUES (7, 3, 3, 9, 3, '[]', 25, 0, 0, 0, 'MARKED', '', '2026-05-15 09:53:49', '2026-05-15 09:53:48', '2026-05-15 09:54:45', 0);
INSERT INTO `student_answer` VALUES (8, 3, 3, 10, 3, '[]', 25, 0, 0, 0, 'MARKED', '', '2026-05-15 09:53:49', '2026-05-15 09:53:48', '2026-05-15 09:54:47', 0);
INSERT INTO `student_answer` VALUES (9, 4, 5, 27, 3, '[\"选项B\"]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:10:53', '2026-05-19 11:10:52', '2026-05-19 11:10:53', 0);
INSERT INTO `student_answer` VALUES (10, 4, 5, 25, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:10:53', '2026-05-19 11:10:52', '2026-05-19 11:10:53', 0);
INSERT INTO `student_answer` VALUES (11, 4, 5, 23, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:10:53', '2026-05-19 11:10:52', '2026-05-19 11:10:53', 0);
INSERT INTO `student_answer` VALUES (12, 4, 5, 20, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:10:53', '2026-05-19 11:10:52', '2026-05-19 11:10:53', 0);
INSERT INTO `student_answer` VALUES (13, 4, 5, 18, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:10:53', '2026-05-19 11:10:52', '2026-05-19 11:10:53', 0);
INSERT INTO `student_answer` VALUES (14, 4, 5, 14, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:10:53', '2026-05-19 11:10:52', '2026-05-19 11:10:53', 0);
INSERT INTO `student_answer` VALUES (15, 4, 5, 16, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:10:53', '2026-05-19 11:10:52', '2026-05-19 11:10:53', 0);
INSERT INTO `student_answer` VALUES (16, 4, 5, 15, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:10:53', '2026-05-19 11:10:52', '2026-05-19 11:10:53', 0);
INSERT INTO `student_answer` VALUES (17, 4, 5, 12, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:10:53', '2026-05-19 11:10:52', '2026-05-19 11:10:53', 0);
INSERT INTO `student_answer` VALUES (18, 4, 5, 11, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:10:53', '2026-05-19 11:10:52', '2026-05-19 11:10:53', 0);
INSERT INTO `student_answer` VALUES (19, 5, 5, 27, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:12:01', '2026-05-19 11:12:01', '2026-05-19 11:12:01', 0);
INSERT INTO `student_answer` VALUES (20, 5, 5, 25, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:12:01', '2026-05-19 11:12:01', '2026-05-19 11:12:01', 0);
INSERT INTO `student_answer` VALUES (21, 5, 5, 23, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:12:01', '2026-05-19 11:12:01', '2026-05-19 11:12:01', 0);
INSERT INTO `student_answer` VALUES (22, 5, 5, 20, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:12:01', '2026-05-19 11:12:01', '2026-05-19 11:12:01', 0);
INSERT INTO `student_answer` VALUES (23, 5, 5, 18, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:12:01', '2026-05-19 11:12:01', '2026-05-19 11:12:01', 0);
INSERT INTO `student_answer` VALUES (24, 5, 5, 14, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:12:01', '2026-05-19 11:12:01', '2026-05-19 11:12:01', 0);
INSERT INTO `student_answer` VALUES (25, 5, 5, 16, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:12:01', '2026-05-19 11:12:01', '2026-05-19 11:12:01', 0);
INSERT INTO `student_answer` VALUES (26, 5, 5, 15, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:12:01', '2026-05-19 11:12:01', '2026-05-19 11:12:01', 0);
INSERT INTO `student_answer` VALUES (27, 5, 5, 12, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:12:01', '2026-05-19 11:12:01', '2026-05-19 11:12:01', 0);
INSERT INTO `student_answer` VALUES (28, 5, 5, 11, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:12:01', '2026-05-19 11:12:01', '2026-05-19 11:12:01', 0);
INSERT INTO `student_answer` VALUES (29, 6, 5, 27, 3, '[\"选项A\"]', 5, 5, 1, 1, 'MARKED', NULL, '2026-05-19 11:20:28', '2026-05-19 11:20:27', '2026-05-19 11:20:28', 0);
INSERT INTO `student_answer` VALUES (30, 6, 5, 25, 3, '[\"选项D\"]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:20:28', '2026-05-19 11:20:27', '2026-05-19 11:20:28', 0);
INSERT INTO `student_answer` VALUES (31, 6, 5, 23, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:20:28', '2026-05-19 11:20:27', '2026-05-19 11:20:28', 0);
INSERT INTO `student_answer` VALUES (32, 6, 5, 20, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:20:28', '2026-05-19 11:20:27', '2026-05-19 11:20:28', 0);
INSERT INTO `student_answer` VALUES (33, 6, 5, 18, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:20:28', '2026-05-19 11:20:27', '2026-05-19 11:20:28', 0);
INSERT INTO `student_answer` VALUES (34, 6, 5, 14, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:20:28', '2026-05-19 11:20:27', '2026-05-19 11:20:28', 0);
INSERT INTO `student_answer` VALUES (35, 6, 5, 16, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:20:28', '2026-05-19 11:20:27', '2026-05-19 11:20:28', 0);
INSERT INTO `student_answer` VALUES (36, 6, 5, 15, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:20:28', '2026-05-19 11:20:27', '2026-05-19 11:20:28', 0);
INSERT INTO `student_answer` VALUES (37, 6, 5, 12, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:20:28', '2026-05-19 11:20:27', '2026-05-19 11:20:28', 0);
INSERT INTO `student_answer` VALUES (38, 6, 5, 11, 3, '[]', 5, 0, 1, 0, 'MARKED', NULL, '2026-05-19 11:20:28', '2026-05-19 11:20:27', '2026-05-19 11:20:28', 0);
INSERT INTO `student_answer` VALUES (39, 7, 7, 7, 3, '[]', 25, 0, 0, 0, 'WAIT_MARKING', NULL, '2026-05-19 11:24:47', '2026-05-19 11:24:47', '2026-05-19 11:24:47', 0);
INSERT INTO `student_answer` VALUES (40, 7, 7, 8, 3, '[]', 25, 0, 0, 0, 'WAIT_MARKING', NULL, '2026-05-19 11:24:47', '2026-05-19 11:24:47', '2026-05-19 11:24:47', 0);
INSERT INTO `student_answer` VALUES (41, 7, 7, 9, 3, '[]', 25, 0, 0, 0, 'WAIT_MARKING', NULL, '2026-05-19 11:24:47', '2026-05-19 11:24:47', '2026-05-19 11:24:47', 0);
INSERT INTO `student_answer` VALUES (42, 7, 7, 10, 3, '[]', 25, 0, 0, 0, 'WAIT_MARKING', NULL, '2026-05-19 11:24:47', '2026-05-19 11:24:47', '2026-05-19 11:24:47', 0);

-- ----------------------------
-- Table structure for subject
-- ----------------------------
DROP TABLE IF EXISTS `subject`;
CREATE TABLE `subject`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `subject_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `subject_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` int NOT NULL DEFAULT 1,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `deleted` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_subject_code`(`subject_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of subject
-- ----------------------------
INSERT INTO `subject` VALUES (1, 'JAVA', 'Java程序设计', 'Java 程序设计课程', 1, '2026-05-11 15:02:01', '2026-05-11 15:02:01', 0);
INSERT INTO `subject` VALUES (2, 'SOFTWARE_ARCH', '软件设计和体系结构', '软件设计和体系结构课程', 1, '2026-05-11 15:02:01', '2026-05-11 15:02:01', 0);
INSERT INTO `subject` VALUES (3, 'SYSTEM1', '2021年11月系统架构师真题（综合知识+答案解析）', '', 1, '2026-05-15 09:47:31', '2026-05-15 09:47:31', 0);
INSERT INTO `subject` VALUES (4, 'SOFTWARE_1', '01中级软件设计师下午试题模拟+答案详解', '', 1, '2026-05-15 09:48:03', '2026-05-15 09:48:03', 0);
INSERT INTO `subject` VALUES (5, 'SYSTEM2', '2026jzz组内测试试题二', '', 1, '2026-05-15 09:48:37', '2026-05-15 09:48:37', 0);
INSERT INTO `subject` VALUES (6, 'SOFTWARE_2', '01中级软件设计师上午试题模拟+答案详解', '', 1, '2026-05-15 09:49:14', '2026-05-15 09:49:14', 0);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `role_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` int NOT NULL DEFAULT 1,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `deleted` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'ADMIN', '管理员', 1, '2026-05-11 15:02:01', '2026-05-11 15:02:01', 0);
INSERT INTO `sys_role` VALUES (2, 'TEACHER', '教师', 1, '2026-05-11 15:02:01', '2026-05-11 15:02:01', 0);
INSERT INTO `sys_role` VALUES (3, 'STUDENT', '学生', 1, '2026-05-11 15:02:01', '2026-05-11 15:02:01', 0);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `student_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `real_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `class_id` bigint NULL DEFAULT NULL,
  `status` int NOT NULL DEFAULT 1,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `deleted` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_user_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_sys_user_student_no`(`student_no` ASC) USING BTREE,
  INDEX `idx_sys_user_class_id`(`class_id` ASC) USING BTREE,
  INDEX `idx_sys_user_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$1r5RpyTJni/28LM48j2oROfFThp32DPsgte7Mufq.0.7zSNGz37v2', NULL, '系统管理员', '13800000001', 'admin@example.com', NULL, 1, '2026-05-11 15:02:03', '2026-05-11 15:02:03', 0);
INSERT INTO `sys_user` VALUES (2, 'teacher', '$2a$10$1r5RpyTJni/28LM48j2oROfFThp32DPsgte7Mufq.0.7zSNGz37v2', NULL, '默认教师', '13800000002', 'teacher@example.com', NULL, 1, '2026-05-11 15:02:03', '2026-05-11 15:02:03', 0);
INSERT INTO `sys_user` VALUES (3, 'student', '$2a$10$1r5RpyTJni/28LM48j2oROfFThp32DPsgte7Mufq.0.7zSNGz37v2', NULL, '默认学生', '13800000003', 'student@example.com', 1, 1, '2026-05-11 15:02:03', '2026-05-11 15:02:03', 0);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `deleted` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_sys_user_role_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_sys_user_role_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1, '2026-05-11 15:02:04', '2026-05-11 15:02:04', 0);
INSERT INTO `sys_user_role` VALUES (2, 2, 2, '2026-05-11 15:02:04', '2026-05-11 15:02:04', 0);
INSERT INTO `sys_user_role` VALUES (3, 3, 3, '2026-05-11 15:02:04', '2026-05-11 15:02:04', 0);

SET FOREIGN_KEY_CHECKS = 1;
