package com.exam.controller;

import com.exam.aspect.OperationLog;
import com.exam.common.enums.OperationTypeEnum;
import com.exam.common.result.PageResult;
import com.exam.common.result.Result;
import com.exam.dto.score.ScoreQueryDTO;
import com.exam.service.ScoreService;
import com.exam.vo.score.ScoreStatisticsVO;
import com.exam.vo.score.ScoreVO;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 成绩接口。
 */
@RestController
@RequestMapping("/scores")
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<PageResult<ScoreVO>> page(@Valid ScoreQueryDTO queryDTO) {
        return Result.success(scoreService.page(queryDTO));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<PageResult<ScoreVO>> myPage(@Valid ScoreQueryDTO queryDTO) {
        return Result.success(scoreService.myScores(queryDTO));
    }

    @GetMapping("/exams/{examId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    public Result<ScoreVO> detail(@PathVariable Long examId, Long studentId) {
        return Result.success(scoreService.detail(examId, studentId));
    }

    @GetMapping("/exams/{examId}/statistics")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<ScoreStatisticsVO> statistics(@PathVariable Long examId) {
        return Result.success(scoreService.statistics(examId));
    }

    @GetMapping("/exams/{examId}/ranking")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<List<ScoreVO>> ranking(@PathVariable Long examId) {
        return Result.success(scoreService.ranking(examId));
    }

    @PostMapping("/exams/{examId}/export")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @OperationLog(module = "成绩管理", description = "导出成绩", operationType = OperationTypeEnum.EXPORT)
    public ResponseEntity<byte[]> export(@PathVariable Long examId) {
        byte[] content = scoreService.export(examId);
        String fileName = "exam-score-" + examId + ".csv";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv", StandardCharsets.UTF_8));
        headers.setContentDisposition(ContentDisposition.attachment().filename(fileName, StandardCharsets.UTF_8).build());
        return ResponseEntity.ok()
                .headers(headers)
                .body(content);
    }
}
