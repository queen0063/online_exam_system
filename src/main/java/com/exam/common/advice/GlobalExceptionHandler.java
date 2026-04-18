package com.exam.common.advice;

import com.exam.common.api.ResultCode;
import com.exam.common.exception.BusinessException;
import com.exam.common.result.Result;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException exception) {
        return Result.failed(exception.getResultCode(), exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("；"));
        return Result.failed(ResultCode.BAD_REQUEST, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException exception) {
        return Result.failed(ResultCode.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(AccessDeniedException exception) {
        return Result.failed(ResultCode.FORBIDDEN, "没有权限执行该操作");
    }

    @ExceptionHandler(DataAccessException.class)
    public Result<Void> handleDataAccessException(DataAccessException exception) {
        return Result.failed(ResultCode.BUSINESS_ERROR, "数据库访问失败，请确认已执行 schema.sql 和 data.sql，且数据库连接配置正确");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception exception) {
        return Result.failed(ResultCode.BUSINESS_ERROR, exception.getMessage());
    }
}
