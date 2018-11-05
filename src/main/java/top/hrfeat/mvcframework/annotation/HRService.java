package top.hrfeat.mvcframework.annotation;

import java.lang.annotation.*;

/**
 * @Author: 81247
 * @Description: ${Description}
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HRService {
    String value() default "";

}
