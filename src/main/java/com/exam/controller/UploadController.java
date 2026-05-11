package com.exam.controller;

import com.exam.common.result.Result;
import com.exam.service.UpyunUploadService;
import com.exam.vo.upload.UpyunPolicyVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件上传辅助接口。
 */
@RestController
@RequestMapping("/uploads")
public class UploadController {

    private final UpyunUploadService upyunUploadService;

    public UploadController(UpyunUploadService upyunUploadService) {
        this.upyunUploadService = upyunUploadService;
    }

    @GetMapping("/upyun/policy")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<UpyunPolicyVO> upyunPolicy() {
        return Result.success(upyunUploadService.createPolicy());
    }
}
