package com.exam.controller;

import com.exam.aspect.OperationLog;
import com.exam.common.enums.OperationTypeEnum;
import com.exam.common.result.PageResult;
import com.exam.common.result.Result;
import com.exam.dto.paper.PaperQueryDTO;
import com.exam.dto.paper.PaperSaveDTO;
import com.exam.service.PaperService;
import com.exam.vo.paper.PaperVO;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 试卷接口。
 */
@RestController
@RequestMapping("/papers")
public class PaperController {

    private final PaperService paperService;

    public PaperController(PaperService paperService) {
        this.paperService = paperService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<PageResult<PaperVO>> page(PaperQueryDTO queryDTO) {
        return Result.success(paperService.page(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<PaperVO> detail(@PathVariable Long id) {
        return Result.success(paperService.detail(id));
    }

    @GetMapping("/{id}/preview")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<PaperVO> preview(@PathVariable Long id) {
        return Result.success(paperService.preview(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @OperationLog(module = "试卷管理", description = "保存试卷", operationType = OperationTypeEnum.CREATE)
    public Result<Void> save(@Valid @RequestBody PaperSaveDTO paperSaveDTO) {
        paperService.save(paperSaveDTO);
        return Result.success("保存成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @OperationLog(module = "试卷管理", description = "删除试卷", operationType = OperationTypeEnum.DELETE)
    public Result<Void> remove(@PathVariable Long id) {
        paperService.remove(id);
        return Result.success("删除成功", null);
    }
}
