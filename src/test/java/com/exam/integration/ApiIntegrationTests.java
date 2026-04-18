package com.exam.integration;

import com.exam.OnlineExamApplication;
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
                                  "studentIds": [3]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
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
}
