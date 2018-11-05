package top.hrfeat.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @Author: 81247
 * @Description: ${Description}
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HRRequestMapping {
    String value() default "";
}
