package com.exam.controller;

import com.exam.aspect.OperationLog;
import com.exam.common.enums.OperationTypeEnum;
import com.exam.common.result.PageResult;
import com.exam.common.result.Result;
import com.exam.dto.question.QuestionQueryDTO;
import com.exam.dto.question.QuestionSaveDTO;
import com.exam.service.QuestionService;
import com.exam.vo.question.QuestionVO;
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
 * 题库接口。
 */
@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<PageResult<QuestionVO>> page(QuestionQueryDTO queryDTO) {
        return Result.success(questionService.page(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<QuestionVO> detail(@PathVariable Long id) {
        return Result.success(questionService.detail(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @OperationLog(module = "题库管理", description = "保存题目", operationType = OperationTypeEnum.CREATE)
    public Result<Void> save(@Valid @RequestBody QuestionSaveDTO questionSaveDTO) {
        questionService.save(questionSaveDTO);
        return Result.success("保存成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @OperationLog(module = "题库管理", description = "删除题目", operationType = OperationTypeEnum.DELETE)
    public Result<Void> remove(@PathVariable Long id) {
        questionService.remove(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/wrong")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<List<QuestionVO>> wrongQuestions() {
        return Result.success(questionService.wrongQuestions(com.exam.security.context.SecurityContextUtils.getUserId()));
    }
}
