package com.exam.controller;

import com.exam.aspect.OperationLog;
import com.exam.common.enums.OperationTypeEnum;
import com.exam.common.result.PageResult;
import com.exam.common.result.Result;
import com.exam.dto.notice.NoticeQueryDTO;
import com.exam.dto.notice.NoticeSaveDTO;
import com.exam.service.NoticeService;
import com.exam.vo.notice.NoticeVO;
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
 * 公告接口。
 */
@RestController
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public Result<PageResult<NoticeVO>> page(NoticeQueryDTO queryDTO) {
        return Result.success(noticeService.page(queryDTO));
    }

    @GetMapping("/{id}")
    public Result<NoticeVO> detail(@PathVariable Long id) {
        return Result.success(noticeService.detail(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @OperationLog(module = "公告管理", description = "保存公告", operationType = OperationTypeEnum.CREATE)
    public Result<Void> save(@Valid @RequestBody NoticeSaveDTO noticeSaveDTO) {
        noticeService.save(noticeSaveDTO);
        return Result.success("保存成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @OperationLog(module = "公告管理", description = "删除公告", operationType = OperationTypeEnum.DELETE)
    public Result<Void> remove(@PathVariable Long id) {
        noticeService.remove(id);
        return Result.success("删除成功", null);
    }
}
