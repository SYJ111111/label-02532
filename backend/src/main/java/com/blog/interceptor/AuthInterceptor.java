package com.blog.interceptor;

import com.blog.common.Constants;
import com.blog.exception.BusinessException;
import com.blog.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String header = request.getHeader(Constants.TOKEN_HEADER);
        if (header == null || !header.startsWith(Constants.TOKEN_PREFIX)) {
            throw new BusinessException(401, "未登录或token已过期");
        }
        String token = header.substring(Constants.TOKEN_PREFIX.length());
        try {
            Claims claims = jwtUtil.parseToken(token);
            request.setAttribute("userId", Long.parseLong(claims.getSubject()));
            request.setAttribute("username", claims.get("username", String.class));
            request.setAttribute("role", claims.get("role", String.class));
            return true;
        } catch (Exception e) {
            log.warn("Token解析失败: {}", e.getMessage());
            throw new BusinessException(401, "未登录或token已过期");
        }
    }
}
