package com.exam.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 又拍云 FORM API 配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.upyun")
public class UpyunProperties {

    private String bucket;
    private String operator;
    private String password;
    private String domain;
    private String uploadHost = "https://v0.api.upyun.com";
    private String saveKey = "/exam/questions/{year}/{mon}/{day}/{random32}{.suffix}";
    private Integer expirationMinutes = 30;
    private Integer maxFileSize = 2 * 1024 * 1024;
    private String allowFileType = "jpg,jpeg,png,gif,webp";
}
