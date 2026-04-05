package com.exam;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = OnlineExamApplication.class)
class OnlineExamApplicationTests {

    @Test
    void contextLoads() {
    }
}
