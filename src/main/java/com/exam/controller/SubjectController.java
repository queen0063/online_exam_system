package com.exam.controller;

import com.exam.aspect.OperationLog;
import com.exam.common.enums.OperationTypeEnum;
import com.exam.common.result.Result;
import com.exam.dto.subject.SubjectSaveDTO;
import com.exam.service.SubjectService;
import com.exam.vo.subject.SubjectVO;
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
 * 科目接口。
 */
@RestController
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public Result<List<SubjectVO>> list() {
        return Result.success(subjectService.list());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @OperationLog(module = "科目管理", description = "保存科目", operationType = OperationTypeEnum.CREATE)
    public Result<Void> save(@Valid @RequestBody SubjectSaveDTO subjectSaveDTO) {
        subjectService.save(subjectSaveDTO);
        return Result.success("保存成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @OperationLog(module = "科目管理", description = "删除科目", operationType = OperationTypeEnum.DELETE)
    public Result<Void> remove(@PathVariable Long id) {
        subjectService.remove(id);
        return Result.success("删除成功", null);
    }
}
