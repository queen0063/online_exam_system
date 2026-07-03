package com.exam.controller;

import com.exam.aspect.OperationLog;
import com.exam.common.enums.OperationTypeEnum;
import com.exam.common.result.PageResult;
import com.exam.common.result.Result;
import com.exam.dto.marking.MarkingQueryDTO;
import com.exam.dto.marking.MarkingScoreDTO;
import com.exam.service.MarkingService;
import com.exam.vo.marking.MarkingVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 阅卷接口。
 */
@RestController
@RequestMapping("/marking")
@PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
public class MarkingController {

    private final MarkingService markingService;

    public MarkingController(MarkingService markingService) {
        this.markingService = markingService;
    }

    @GetMapping("/pending")
    public Result<PageResult<MarkingVO>> pending(MarkingQueryDTO queryDTO) {
        return Result.success(markingService.pendingPage(queryDTO));
    }

    @GetMapping("/exams/{examId}/students/{studentId}")
    public Result<MarkingVO> detail(@PathVariable Long examId, @PathVariable Long studentId) {
        return Result.success(markingService.detail(examId, studentId));
    }

    @PutMapping("/answers/{answerId}")
    @OperationLog(module = "阅卷管理", description = "批改单题", operationType = OperationTypeEnum.MARK)
    public Result<Void> markQuestion(@PathVariable Long answerId, @Valid @RequestBody MarkingScoreDTO markingScoreDTO) {
        markingService.markQuestion(answerId, markingScoreDTO);
        return Result.success("批改成功", null);
    }

    @PostMapping("/exams/{examId}/students/{studentId}/finish")
    @OperationLog(module = "阅卷管理", description = "完成阅卷", operationType = OperationTypeEnum.MARK)
    public Result<Void> finish(@PathVariable Long examId, @PathVariable Long studentId) {
        markingService.finishMarking(examId, studentId);
        return Result.success("阅卷完成", null);
    }
}
