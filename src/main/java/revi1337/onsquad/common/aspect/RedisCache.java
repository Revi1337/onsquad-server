package revi1337.onsquad.common.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RedisCache {

    OnSquadType type() default OnSquadType.CREW;

    String id();

    String name();

    long ttl() default 1;

    TimeUnit unit() default TimeUnit.MINUTES;

    boolean cacheEmptyCollection() default false;

}
