package top.hrfeat.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @Author: 81247
 * @Description: ${Description}
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HRAutowired {
    String value() default "";

}
