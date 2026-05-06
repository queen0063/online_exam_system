package com.exam;

import java.util.TimeZone;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAsync
@MapperScan("com.exam.mapper")
@EnableTransactionManagement
@SpringBootApplication
public class OnlineExamApplication {

    private static final String APP_TIME_ZONE = "Asia/Shanghai";

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(APP_TIME_ZONE));
        SpringApplication.run(OnlineExamApplication.class, args);
    }
}
