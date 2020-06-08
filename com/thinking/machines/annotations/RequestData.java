package com.thinking.machines.annotations;
import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RequestData{
	String variableName() default "";
}