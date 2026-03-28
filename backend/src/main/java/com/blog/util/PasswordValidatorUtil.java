package com.blog.util;

import com.blog.validator.PasswordValidator;

public class PasswordValidatorUtil {
    
    private static final PasswordValidator validator = new PasswordValidator();
    
    public static boolean isValid(String password) {
        return validator.isValid(password, null);
    }
    
    public static String getPasswordRequirement() {
        return "密码必须包含至少一个小写字母、一个大写字母、一个特殊字符，并且长度至少为8位";
    }
}
