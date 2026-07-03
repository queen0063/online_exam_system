package com.exam.service.impl;

import com.exam.common.api.ResultCode;
import com.exam.common.exception.BusinessException;
import com.exam.config.UpyunProperties;
import com.exam.service.UpyunUploadService;
import com.exam.vo.upload.UpyunPolicyVO;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

/**
 * 又拍云 FORM API 上传签名实现。
 */
@Service
public class UpyunUploadServiceImpl implements UpyunUploadService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final UpyunProperties properties;

    public UpyunUploadServiceImpl(UpyunProperties properties) {
        this.properties = properties;
    }

    @Override
    public UpyunPolicyVO createPolicy() {
        validateConfig();
        long expiresAt = Instant.now().plusSeconds(properties.getExpirationMinutes() * 60L).getEpochSecond();
        String date = DateTimeFormatter.RFC_1123_DATE_TIME.format(Instant.now().atZone(ZoneOffset.UTC));
        Map<String, Object> policyMap = new LinkedHashMap<>();
        policyMap.put("bucket", properties.getBucket());
        policyMap.put("save-key", properties.getSaveKey());
        policyMap.put("expiration", expiresAt);
        policyMap.put("date", date);
        policyMap.put("allow-file-type", properties.getAllowFileType());
        policyMap.put("content-length-range", "1," + properties.getMaxFileSize());
        String policy = base64Json(policyMap);
        String signature = hmacSha1Base64(md5Hex(properties.getPassword()), "POST&/" + properties.getBucket() + "&" + date + "&" + policy);
        return UpyunPolicyVO.builder()
                .uploadUrl(trimRight(properties.getUploadHost()) + "/" + properties.getBucket())
                .policy(policy)
                .authorization("UPYUN " + properties.getOperator() + ":" + signature)
                .domain(trimRight(properties.getDomain()))
                .expiresAt(expiresAt)
                .build();
    }

    private void validateConfig() {
        if (isBlank(properties.getBucket())
                || isBlank(properties.getOperator())
                || isBlank(properties.getPassword())
                || isBlank(properties.getDomain())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "又拍云配置不完整，请配置 bucket、operator、password、domain");
        }
    }

    private String base64Json(Map<String, Object> value) {
        try {
            String json = OBJECT_MAPPER.writeValueAsString(value);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new BusinessException("又拍云 policy 生成失败");
        }
    }

    private String hmacSha1Base64(String key, String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            return Base64.getEncoder().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new BusinessException("又拍云上传签名生成失败");
        }
    }

    private String md5Hex(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(bytes.length * 2);
            for (byte item : bytes) {
                builder.append(String.format("%02x", item));
            }
            return builder.toString();
        } catch (Exception exception) {
            throw new BusinessException("又拍云密码摘要生成失败");
        }
    }

    private String trimRight(String value) {
        if (value == null) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
