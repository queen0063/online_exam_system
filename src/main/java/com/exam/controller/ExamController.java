package com.exam.controller;

import com.exam.aspect.OperationLog;
import com.exam.common.enums.OperationTypeEnum;
import com.exam.common.result.PageResult;
import com.exam.common.result.Result;
import com.exam.dto.exam.ExamQueryDTO;
import com.exam.dto.exam.ExamSaveDTO;
import com.exam.service.ExamService;
import com.exam.vo.exam.ExamMonitorVO;
import com.exam.vo.exam.ExamVO;
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
 * 考试接口。
 */
@RestController
@RequestMapping("/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<PageResult<ExamVO>> page(ExamQueryDTO queryDTO) {
        return Result.success(examService.page(queryDTO));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<PageResult<ExamVO>> myPage(ExamQueryDTO queryDTO) {
        return Result.success(examService.myPage(queryDTO));
    }

    @GetMapping("/{id}")
    public Result<ExamVO> detail(@PathVariable Long id) {
        return Result.success(examService.detail(id));
    }

    @GetMapping("/{id}/monitoring")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<List<ExamMonitorVO>> monitoring(@PathVariable Long id) {
        return Result.success(examService.monitoring(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @OperationLog(module = "考试管理", description = "保存考试", operationType = OperationTypeEnum.CREATE)
    public Result<Void> save(@Valid @RequestBody ExamSaveDTO examSaveDTO) {
        examService.save(examSaveDTO);
        return Result.success("保存成功", null);
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @OperationLog(module = "考试管理", description = "发布考试", operationType = OperationTypeEnum.UPDATE)
    public Result<Void> publish(@PathVariable Long id) {
        examService.publish(id);
        return Result.success("发布成功", null);
    }

    @PostMapping("/{id}/publish-score")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @OperationLog(module = "考试管理", description = "发布成绩", operationType = OperationTypeEnum.UPDATE)
    public Result<Void> publishScore(@PathVariable Long id) {
        examService.publishScore(id);
        return Result.success("发布成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @OperationLog(module = "考试管理", description = "删除考试", operationType = OperationTypeEnum.DELETE)
    public Result<Void> remove(@PathVariable Long id) {
        examService.remove(id);
        return Result.success("删除成功", null);
    }
}
