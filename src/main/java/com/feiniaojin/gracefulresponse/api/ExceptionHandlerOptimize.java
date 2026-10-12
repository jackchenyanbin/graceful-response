package com.feiniaojin.gracefulresponse.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ExceptionHandler增强注解
 *
 * @author qinyujie
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionHandlerOptimize {
    String value() default "";
}
