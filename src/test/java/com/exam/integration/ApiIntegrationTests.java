package com.exam.integration;

import com.exam.OnlineExamApplication;
import com.exam.entity.Exam;
import com.exam.mapper.ExamMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 接口级集成测试。
 */
@ActiveProfiles("test")
@SpringBootTest(classes = OnlineExamApplication.class)
class ApiIntegrationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ExamMapper examMapper;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void loginShouldReturnJwtToken() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "Admin@123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.userInfo.username").value("admin"));
    }

    @Test
    void loginWithWrongAccountShouldReturnPasswordErrorMessage() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "not-exists",
                                  "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    @Test
    void registeredTeacherShouldNotLoginBeforeEnabled() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "pending_teacher",
                                  "password": "Admin@123",
                                  "realName": "待审核教师",
                                  "roleCode": "TEACHER",
                                  "phone": "13900000001",
                                  "email": "pending-teacher@example.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "pending_teacher",
                                  "password": "Admin@123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("账号未启用，请联系管理员"));
    }

    @Test
    void requestWithoutTokenShouldBeUnauthorized() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void studentShouldQueryAssignedExamList() throws Exception {
        String token = loginAndGetToken("student", "Admin@123");
        mockMvc.perform(get("/answers/exams")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records.length()").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void teacherShouldQueryScoreStatistics() throws Exception {
        String token = loginAndGetToken("teacher", "Admin@123");
        mockMvc.perform(get("/scores/exams/1/statistics")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.examId").value(1))
                .andExpect(jsonPath("$.data.scoreSegments.length()").value(5));
    }

    @Test
    void teacherShouldExportScoreCsv() throws Exception {
        String token = loginAndGetToken("teacher", "Admin@123");
        mockMvc.perform(post("/scores/exams/1/export")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("exam-score-1.csv")))
                .andExpect(content().string(containsString("考试ID,考试名称")));
    }

    @Test
    void adminShouldSaveRole() throws Exception {
        String token = loginAndGetToken("admin", "Admin@123");
        mockMvc.perform(post("/roles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "roleCode": "ASSISTANT_IT",
                                  "roleName": "助教",
                                  "status": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/roles")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(greaterThanOrEqualTo(4)));
    }

    @Test
    void studentShouldQueryExamDetailWithQuestions() throws Exception {
        String token = loginAndGetToken("student", "Admin@123");
        mockMvc.perform(get("/answers/exams/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.questions.length()").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.questions[0].title").isNotEmpty());
    }

    @Test
    void teacherShouldSaveExamWithSpaceSeparatedDateTime() throws Exception {
        String token = loginAndGetToken("teacher", "Admin@123");
        mockMvc.perform(post("/exams")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "examName": "考试时间格式回归验证",
                                  "paperId": 1,
                                  "subjectId": 1,
                                  "startTime": "2026-04-18 23:16:08",
                                  "endTime": "2026-04-19 00:16:08",
                                  "durationMinutes": 60,
                                  "passScore": 60,
                                  "maxSwitchCount": 2,
                                  "studentIds": [3]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Long examId = findTeacherExamIdByName(token, "考试时间格式回归验证");
        mockMvc.perform(get("/exams/" + examId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.maxSwitchCount").value(2));
    }

    @Test
    void teacherShouldUpdateExistingExamWithoutExamStudentConstraintFailure() throws Exception {
        String token = loginAndGetToken("teacher", "Admin@123");
        mockMvc.perform(post("/exams")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 1,
                                  "examName": "Java后端期中考试-调整时间",
                                  "paperId": 1,
                                  "subjectId": 1,
                                  "startTime": "2026-04-18 23:16:08",
                                  "endTime": "2026-04-19 00:16:08",
                                  "durationMinutes": 60,
                                  "passScore": 24,
                                  "studentIds": [3]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void studentExamListShouldRefreshPublishedExamStatusWhenStartTimeArrives() throws Exception {
        String teacherToken = loginAndGetToken("teacher", "Admin@123");
        String examName = "考试状态自动流转回归验证";
        mockMvc.perform(post("/exams")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "examName": "%s",
                                  "paperId": 1,
                                  "subjectId": 1,
                                  "startTime": "2026-04-19 14:00:00",
                                  "endTime": "2026-04-19 15:00:00",
                                  "durationMinutes": 60,
                                  "passScore": 24,
                                  "studentIds": [3]
                                }
                                """.formatted(examName)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Long examId = findTeacherExamIdByName(teacherToken, examName);
        publishExam(teacherToken, examId);

        Exam exam = examMapper.selectById(examId);
        exam.setStartTime(LocalDateTime.now().minusMinutes(5));
        exam.setEndTime(LocalDateTime.now().plusMinutes(55));
        exam.setStatus("NOT_STARTED");
        exam.setUpdateTime(LocalDateTime.now());
        examMapper.updateById(exam);

        String studentToken = loginAndGetToken("student", "Admin@123");
        mockMvc.perform(get("/answers/exams")
                        .header("Authorization", "Bearer " + studentToken)
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].id").value(examId))
                .andExpect(jsonPath("$.data.records[0].status").value("IN_PROGRESS"));
    }

    @Test
    void studentExamDetailShouldReturnPersonalCountdownDeadlineAfterStart() throws Exception {
        String token = loginAndGetToken("student", "Admin@123");
        mockMvc.perform(post("/answers/exams/1/start")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/answers/exams/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.countdownEndTime").isNotEmpty());
    }

    @Test
    void studentShouldNotReenterExamAfterSubmit() throws Exception {
        String token = loginAndGetToken("student", "Admin@123");
        mockMvc.perform(post("/answers/exams/1/start")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/answers/exams/1/submit")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/answers/exams/1/start")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("试卷已提交，不能重新进入考试"));

        mockMvc.perform(get("/answers/exams/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("试卷已提交，不能再次参加考试"));
    }

    @Test
    void teacherShouldSeeSubmittedPaperInPendingMarkingList() throws Exception {
        String teacherToken = loginAndGetToken("teacher", "Admin@123");
        String examName = "待阅卷列表回归验证";
        mockMvc.perform(post("/exams")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activeExamPayload(examName)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Long publishedExamId = findTeacherExamIdByName(teacherToken, examName);
        publishExam(teacherToken, publishedExamId);

        String studentToken = loginAndGetToken("student", "Admin@123");
        Long examId = findStudentExamIdByName(studentToken, examName);

        mockMvc.perform(post("/answers/exams/" + examId + "/start")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/answers/exams/" + examId + "/submit")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        MvcResult pendingResult = mockMvc.perform(get("/marking/pending")
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records.length()").value(greaterThanOrEqualTo(1)))
                .andReturn();

        JsonNode root = objectMapper.readTree(pendingResult.getResponse().getContentAsString());
        boolean matched = false;
        for (JsonNode item : root.path("data").path("records")) {
            if (examId.equals(item.path("examId").asLong())
                    && item.path("studentId").asLong() == 3L
                    && "WAIT_MARKING".equals(item.path("scoreStatus").asText())) {
                matched = true;
                break;
            }
        }
        if (!matched) {
            throw new AssertionError("待阅卷列表中未找到学生提交的试卷");
        }
    }

    @Test
    void teacherShouldSaveSubjectiveScoreMoreThanOnce() throws Exception {
        String teacherToken = loginAndGetToken("teacher", "Admin@123");
        String examName = "阅卷保存评分回归验证";
        mockMvc.perform(post("/exams")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activeExamPayload(examName)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Long publishedExamId = findTeacherExamIdByName(teacherToken, examName);
        publishExam(teacherToken, publishedExamId);

        String studentToken = loginAndGetToken("student", "Admin@123");
        Long examId = findStudentExamIdByName(studentToken, examName);
        mockMvc.perform(post("/answers/exams/" + examId + "/start")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        mockMvc.perform(post("/answers/exams/" + examId + "/submit")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        MvcResult detailResult = mockMvc.perform(get("/marking/exams/" + examId + "/students/3")
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        JsonNode detailRoot = objectMapper.readTree(detailResult.getResponse().getContentAsString());
        Long answerId = null;
        for (JsonNode item : detailRoot.path("data").path("answers")) {
            if ("SHORT_ANSWER".equals(item.path("questionType").asText())) {
                answerId = item.path("id").asLong();
                break;
            }
        }
        if (answerId == null) {
            throw new AssertionError("未找到主观题答题记录");
        }

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/marking/answers/" + answerId)
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "actualScore": 10,
                                  "teacherComment": "第一次评分"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/marking/answers/" + answerId)
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "actualScore": 12,
                                  "teacherComment": "调整评分"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void studentShouldViewOwnScoreDetailAndAnswerReviewAfterPublishScore() throws Exception {
        String teacherToken = loginAndGetToken("teacher", "Admin@123");
        String examName = "学生成绩详情回归验证";
        mockMvc.perform(post("/exams")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activeExamPayload(examName)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Long publishedExamId = findTeacherExamIdByName(teacherToken, examName);
        publishExam(teacherToken, publishedExamId);

        String studentToken = loginAndGetToken("student", "Admin@123");
        Long examId = findStudentExamIdByName(studentToken, examName);

        mockMvc.perform(post("/answers/exams/" + examId + "/start")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/answers/exams/" + examId + "/submit")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/scores/my")
                        .header("Authorization", "Bearer " + studentToken)
                        .param("examId", String.valueOf(examId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0));

        mockMvc.perform(get("/scores/exams/" + examId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("成绩尚未发布"));

        mockMvc.perform(get("/answers/exams/" + examId + "/detail")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("成绩尚未发布"));

        mockMvc.perform(post("/exams/" + examId + "/publish-score")
                        .header("Authorization", "Bearer " + teacherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("仍有未完成阅卷的成绩，不能发布成绩"));

        completeSubjectiveMarking(teacherToken, examId, 3L, 12);
        publishExamScore(teacherToken, examId);

        mockMvc.perform(get("/scores/exams/" + examId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.examId").value(examId))
                .andExpect(jsonPath("$.data.studentId").value(3))
                .andExpect(jsonPath("$.data.publishTime").isNotEmpty());

        mockMvc.perform(get("/answers/exams/" + examId + "/detail")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void studentWrongBookShouldContainIncorrectObjectiveAnswersAfterPublishScore() throws Exception {
        String teacherToken = loginAndGetToken("teacher", "Admin@123");
        String examName = "错题本回归验证";
        mockMvc.perform(post("/exams")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activeExamPayload(examName)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Long publishedExamId = findTeacherExamIdByName(teacherToken, examName);
        publishExam(teacherToken, publishedExamId);

        String studentToken = loginAndGetToken("student", "Admin@123");
        Long examId = findStudentExamIdByName(studentToken, examName);

        mockMvc.perform(post("/answers/exams/" + examId + "/start")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(put("/answers/exams/" + examId)
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "answers": [
                                    {
                                      "questionId": 1,
                                      "answers": ["implements"]
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/answers/exams/" + examId + "/submit")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/questions/wrong")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(0));

        completeSubjectiveMarking(teacherToken, examId, 3L, 12);
        publishExamScore(teacherToken, examId);

        MvcResult wrongResult = mockMvc.perform(get("/questions/wrong")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        JsonNode wrongRoot = objectMapper.readTree(wrongResult.getResponse().getContentAsString());
        boolean matched = false;
        for (JsonNode item : wrongRoot.path("data")) {
            if (item.path("id").asLong() == 1L) {
                if (!"implements".equals(item.path("studentAnswers").path(0).asText())) {
                    throw new AssertionError("错题本未返回学生原始作答记录");
                }
                if (item.path("actualScore").asInt(-1) != 0) {
                    throw new AssertionError("错题本未返回错误题目的实际得分");
                }
                matched = true;
                break;
            }
        }
        if (!matched) {
            throw new AssertionError("错题本中未找到错误作答的客观题");
        }
    }

    @Test
    void draftExamShouldBeHiddenFromStudentUntilPublished() throws Exception {
        String teacherToken = loginAndGetToken("teacher", "Admin@123");
        String examName = "草稿考试发布回归验证";
        mockMvc.perform(post("/exams")
                        .header("Authorization", "Bearer " + teacherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "examName": "%s",
                                  "paperId": 1,
                                  "subjectId": 1,
                                  "startTime": "2026-04-18 14:00:00",
                                  "endTime": "2026-04-19 14:00:00",
                                  "durationMinutes": 60,
                                  "passScore": 24,
                                  "studentIds": [3]
                                }
                                """.formatted(examName)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        String studentToken = loginAndGetToken("student", "Admin@123");
        if (studentExamExistsByName(studentToken, examName)) {
            throw new AssertionError("草稿考试不应出现在学生考试列表");
        }

        Long examId = findTeacherExamIdByName(teacherToken, examName);
        publishExam(teacherToken, examId);

        if (!studentExamExistsByName(studentToken, examName)) {
            throw new AssertionError("发布后学生考试列表中应可见该考试");
        }
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        return root.path("data").path("token").asText();
    }

    private Long findStudentExamIdByName(String token, String examName) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/answers/exams")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        for (JsonNode item : root.path("data").path("records")) {
            if (examName.equals(item.path("examName").asText())) {
                return item.path("id").asLong();
            }
        }
        throw new AssertionError("未找到考试: " + examName);
    }

    private Long findTeacherExamIdByName(String token, String examName) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/exams")
                        .header("Authorization", "Bearer " + token)
                        .param("examName", examName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        for (JsonNode item : root.path("data").path("records")) {
            if (examName.equals(item.path("examName").asText())) {
                return item.path("id").asLong();
            }
        }
        throw new AssertionError("教师列表中未找到考试: " + examName);
    }

    private boolean studentExamExistsByName(String token, String examName) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/answers/exams")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        for (JsonNode item : root.path("data").path("records")) {
            if (examName.equals(item.path("examName").asText())) {
                return true;
            }
        }
        return false;
    }

    private String activeExamPayload(String examName) {
        LocalDateTime now = LocalDateTime.now();
        return """
                {
                  "examName": "%s",
                  "paperId": 1,
                  "subjectId": 1,
                  "startTime": "%s",
                  "endTime": "%s",
                  "durationMinutes": 60,
                  "passScore": 24,
                  "studentIds": [3]
                }
                """.formatted(
                examName,
                formatTestDateTime(now.minusMinutes(5)),
                formatTestDateTime(now.plusHours(1)));
    }

    private String formatTestDateTime(LocalDateTime dateTime) {
        return dateTime.withNano(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private void completeSubjectiveMarking(String token, Long examId, Long studentId, int actualScore) throws Exception {
        MvcResult detailResult = mockMvc.perform(get("/marking/exams/" + examId + "/students/" + studentId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        JsonNode detailRoot = objectMapper.readTree(detailResult.getResponse().getContentAsString());
        Long answerId = null;
        for (JsonNode item : detailRoot.path("data").path("answers")) {
            if ("SHORT_ANSWER".equals(item.path("questionType").asText())) {
                answerId = item.path("id").asLong();
                break;
            }
        }
        if (answerId == null) {
            throw new AssertionError("未找到主观题答题记录");
        }

        mockMvc.perform(put("/marking/answers/" + answerId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "actualScore": %d,
                                  "teacherComment": "发布前完成评分"
                                }
                                """.formatted(actualScore)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/marking/exams/" + examId + "/students/" + studentId + "/finish")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private void publishExam(String token, Long examId) throws Exception {
        mockMvc.perform(post("/exams/" + examId + "/publish")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private void publishExamScore(String token, Long examId) throws Exception {
        mockMvc.perform(post("/exams/" + examId + "/publish-score")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
