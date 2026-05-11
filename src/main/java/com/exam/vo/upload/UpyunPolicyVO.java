package com.exam.vo.upload;

import lombok.Builder;
import lombok.Data;

/**
 * 又拍云直传凭证。
 */
@Data
@Builder
public class UpyunPolicyVO {

    private String uploadUrl;
    private String policy;
    private String authorization;
    private String domain;
    private Long expiresAt;
}
