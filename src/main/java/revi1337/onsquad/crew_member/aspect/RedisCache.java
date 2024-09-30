package revi1337.onsquad.crew_member.aspect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RedisCache {

    CommunityType type() default CommunityType.CREW;

    String id();

    String name();

    long ttl() default 1;

    TimeUnit unit() default TimeUnit.MINUTES;

    boolean cacheEmptyCollection() default false;

    @Getter
    @RequiredArgsConstructor
    enum CommunityType {

        CREW("crew", "onsquad:crew:%s:%s"),
        SQUAD("squad", "onsquad:squad:%s:%s")
        ;

        private final String text;
        private final String format;

    }
}
