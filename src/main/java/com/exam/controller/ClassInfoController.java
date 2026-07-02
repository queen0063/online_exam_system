package com.exam.controller;

import com.exam.aspect.OperationLog;
import com.exam.common.enums.OperationTypeEnum;
import com.exam.common.result.Result;
import com.exam.dto.classinfo.ClassInfoSaveDTO;
import com.exam.service.ClassInfoService;
import com.exam.vo.classinfo.ClassInfoVO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/manage")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<List<ClassInfoVO>> manageList() {
        return Result.success(classInfoService.listAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @OperationLog(module = "班级管理", description = "保存班级", operationType = OperationTypeEnum.CREATE)
    public Result<Void> save(@Valid @RequestBody ClassInfoSaveDTO classInfoSaveDTO) {
        classInfoService.save(classInfoSaveDTO);
        return Result.success("保存成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @OperationLog(module = "班级管理", description = "删除班级", operationType = OperationTypeEnum.DELETE)
    public Result<Void> remove(@PathVariable Long id) {
        classInfoService.remove(id);
        return Result.success("删除成功", null);
    }
}
