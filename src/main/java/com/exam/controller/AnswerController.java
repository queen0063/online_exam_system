package com.exam.controller;

import com.exam.aspect.OperationLog;
import com.exam.common.enums.OperationTypeEnum;
import com.exam.common.result.PageResult;
import com.exam.common.result.Result;
import com.exam.dto.answer.AnswerSaveDTO;
import com.exam.dto.answer.SwitchCountReportDTO;
import com.exam.dto.exam.ExamQueryDTO;
import com.exam.service.AnswerService;
import com.exam.vo.answer.AnswerVO;
import com.exam.vo.exam.ExamVO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 在线答题接口。
 */
@RestController
@RequestMapping("/answers")
@PreAuthorize("hasRole('STUDENT')")
public class AnswerController {

    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @GetMapping("/exams")
    public Result<PageResult<ExamVO>> myExamPage(ExamQueryDTO queryDTO) {
        return Result.success(answerService.myExamPage(queryDTO));
    }

    @GetMapping("/exams/{examId}")
    public Result<ExamVO> examDetail(@PathVariable Long examId) {
        return Result.success(answerService.examDetail(examId));
    }

    @PostMapping("/exams/{examId}/start")
    @OperationLog(module = "在线答题", description = "开始考试", operationType = OperationTypeEnum.OTHER)
    public Result<Void> startExam(@PathVariable Long examId) {
        answerService.startExam(examId);
        return Result.success("开始成功", null);
    }

    @PutMapping("/exams/{examId}")
    @OperationLog(module = "在线答题", description = "保存答题记录", operationType = OperationTypeEnum.UPDATE)
    public Result<Void> saveAnswers(@PathVariable Long examId, @Valid @RequestBody AnswerSaveDTO answerSaveDTO) {
        answerService.saveAnswers(examId, answerSaveDTO);
        return Result.success("保存成功", null);
    }

    @PostMapping("/exams/{examId}/submit")
    @OperationLog(module = "在线答题", description = "提交试卷", operationType = OperationTypeEnum.SUBMIT)
    public Result<Void> submit(@PathVariable Long examId) {
        answerService.submit(examId);
        return Result.success("提交成功", null);
    }

    @PutMapping("/exams/{examId}/switch-count")
    public Result<Void> reportSwitchCount(@PathVariable Long examId, @Valid @RequestBody SwitchCountReportDTO reportDTO) {
        answerService.reportSwitchCount(examId, reportDTO.getSwitchCount());
        return Result.success("上报成功", null);
    }

    @GetMapping("/exams/{examId}/detail")
    public Result<List<AnswerVO>> answerDetail(@PathVariable Long examId) {
        return Result.success(answerService.answerDetail(examId));
    }
}
