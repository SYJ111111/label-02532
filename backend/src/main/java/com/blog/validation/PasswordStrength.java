package com.blog.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordStrengthValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordStrength {
    String message() default "密码必须包含大小写字母、数字和特殊字符";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
