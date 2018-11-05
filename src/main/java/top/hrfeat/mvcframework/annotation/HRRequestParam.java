package top.hrfeat.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @Author: 81247
 * @Description: ${Description}
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HRRequestParam {
    String value() default "";
}
