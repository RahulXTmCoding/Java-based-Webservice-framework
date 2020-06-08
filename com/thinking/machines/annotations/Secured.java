package com.thinking.machines.annotations;
import java.lang.annotation.*;
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Secured
{
	String value() default "";
}