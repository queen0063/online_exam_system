package com.exam.aspect;

import com.exam.security.context.SecurityContextUtils;
import com.exam.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 操作日志切面。
 */
@Aspect
@Component
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final HttpServletRequest request;

    public OperationLogAspect(OperationLogService operationLogService, HttpServletRequest request) {
        this.operationLogService = operationLogService;
        this.request = request;
    }

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, com.exam.aspect.OperationLog operationLogAnnotation) throws Throwable {
        boolean success = true;
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            success = false;
            throw throwable;
        } finally {
            OperationLogEntityBuilder.record(operationLogService, operationLogAnnotation, request, success);
        }
    }

    private final class OperationLogEntityBuilder {

        private OperationLogEntityBuilder() {
        }

        private static void record(
                OperationLogService operationLogService,
                com.exam.aspect.OperationLog operationLogAnnotation,
                HttpServletRequest request,
                boolean success) {
            com.exam.entity.OperationLog entity = new com.exam.entity.OperationLog();
            entity.setOperatorId(resolveUserId());
            entity.setOperatorName(resolveUsername());
            entity.setOperationType(operationLogAnnotation.operationType().name());
            entity.setModuleName(operationLogAnnotation.module());
            entity.setOperationDesc(operationLogAnnotation.description());
            entity.setRequestUri(request.getRequestURI());
            entity.setRequestMethod(request.getMethod());
            entity.setRequestParam(request.getQueryString());
            entity.setSuccessFlag(success ? 1 : 0);
            entity.setOperationTime(LocalDateTime.now());
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            entity.setDeleted(0);
            operationLogService.record(entity);
        }

        private static Long resolveUserId() {
            try {
                return SecurityContextUtils.getUserId();
            } catch (Exception exception) {
                return null;
            }
        }

        private static String resolveUsername() {
            try {
                return SecurityContextUtils.getUsername();
            } catch (Exception exception) {
                return "anonymous";
            }
        }
    }
}
