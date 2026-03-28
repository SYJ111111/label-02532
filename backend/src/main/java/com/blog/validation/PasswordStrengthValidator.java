package com.blog.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Base64;

public class PasswordStrengthValidator implements ConstraintValidator<PasswordStrength, String> {

    @Override
    public void initialize(PasswordStrength constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        // 尝试解码Base64密码
        String rawPassword;
        try {
            rawPassword = new String(Base64.getDecoder().decode(password));
        } catch (IllegalArgumentException e) {
            // 如果不是Base64编码，直接使用原始密码
            rawPassword = password;
        }

        boolean hasUppercase = !rawPassword.equals(rawPassword.toLowerCase());
        boolean hasLowercase = !rawPassword.equals(rawPassword.toUpperCase());
        boolean hasDigit = rawPassword.matches(".*\\d.*");
        boolean hasSpecial = rawPassword.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }
}
