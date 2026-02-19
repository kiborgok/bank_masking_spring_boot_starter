package com.p11.masking.annotation;

import com.p11.masking.core.MaskStyle;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mask {

    MaskStyle style() default MaskStyle.PARTIAL;

    char maskChar() default '\0';
}
