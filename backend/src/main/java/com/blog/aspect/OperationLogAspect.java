package com.blog.aspect;

import com.blog.annotation.Log;
import com.blog.entity.OperationLog;
import com.blog.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    @AfterReturning("@annotation(logAnnotation)")
    public void afterReturning(JoinPoint joinPoint, Log logAnnotation) {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) return;
            HttpServletRequest request = attributes.getRequest();

            OperationLog opLog = new OperationLog();
            opLog.setUserId((Long) request.getAttribute("userId"));
            opLog.setUsername((String) request.getAttribute("username"));
            opLog.setOperation(logAnnotation.value());

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            opLog.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());
            String params = Arrays.toString(joinPoint.getArgs());
            opLog.setParams(params.length() > 2000 ? params.substring(0, 2000) : params);
            opLog.setIp(getClientIp(request));

            operationLogService.save(opLog);
            log.info("操作日志: {} - {}", opLog.getUsername(), opLog.getOperation());
        } catch (Exception e) {
            log.error("记录操作日志失败: ", e);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
