package com.exam.controller;

import com.exam.common.result.Result;
import com.exam.service.ClassInfoService;
import com.exam.vo.classinfo.ClassInfoVO;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 班级接口。
 */
@RestController
@RequestMapping("/classes")
public class ClassInfoController {

    private final ClassInfoService classInfoService;

    public ClassInfoController(ClassInfoService classInfoService) {
        this.classInfoService = classInfoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<List<ClassInfoVO>> list() {
        return Result.success(classInfoService.listVisible());
    }
}
