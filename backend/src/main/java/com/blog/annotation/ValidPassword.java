package com.blog.annotation;

import com.blog.validator.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidPassword {
    String message() default "密码必须包含至少一个小写字母、一个大写字母、一个特殊字符，并且长度至少为8位";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
