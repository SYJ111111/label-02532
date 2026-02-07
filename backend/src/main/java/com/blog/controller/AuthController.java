package com.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.annotation.Log;
import com.blog.common.Constants;
import com.blog.common.Result;
import com.blog.dto.LoginDTO;
import com.blog.dto.RegisterDTO;
import com.blog.entity.User;
import com.blog.exception.BusinessException;
import com.blog.service.UserService;
import com.blog.util.JwtUtil;
import com.blog.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Log("用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        String rawPassword = new String(Base64.getDecoder().decode(dto.getPassword()));
        User user = userService.getOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (Constants.STATUS_DISABLED.equals(user.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", vo);
        return Result.success(data);
    }

    @Log("用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        long usernameCount = userService.count(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (usernameCount > 0) {
            throw new BusinessException("用户名已存在");
        }
        long nicknameCount = userService.count(
                new LambdaQueryWrapper<User>().eq(User::getNickname, dto.getNickname())
        );
        if (nicknameCount > 0) {
            throw new BusinessException("昵称已存在");
        }
        String rawPassword = new String(Base64.getDecoder().decode(dto.getPassword()));
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setRole(Constants.ROLE_USER);
        user.setStatus(Constants.STATUS_ENABLED);
        userService.save(user);
        return Result.success();
    }

    @GetMapping("/info")
    public Result<UserVO> info(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return Result.success(vo);
    }

    @Log("用户退出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }
}
