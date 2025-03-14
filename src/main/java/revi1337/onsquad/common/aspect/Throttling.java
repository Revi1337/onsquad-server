package revi1337.onsquad.common.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Throttling {

    String name() default "";

    String key();

    int during() default 10;

    TimeUnit unit() default TimeUnit.SECONDS;

}
