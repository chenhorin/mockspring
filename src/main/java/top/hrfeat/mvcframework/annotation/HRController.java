package top.hrfeat.mvcframework.annotation;

import java.lang.annotation.*;
import java.lang.ref.SoftReference;

/**
 * @Author: 81247
 * @Description: ${Description}
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HRController {
    String value() default "";
}
