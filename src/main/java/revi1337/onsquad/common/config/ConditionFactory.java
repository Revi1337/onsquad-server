package revi1337.onsquad.common.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ConditionFactory {

    public static class UseCustomRedisAspectOrUseRedisCacheManager implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String useCustomRedisAspect = context.getEnvironment().getProperty("onsquad.use-custom-redis-aspect");
            String useRedisCacheManager = context.getEnvironment().getProperty("onsquad.use-redis-cache-manager");

            return Boolean.parseBoolean(useCustomRedisAspect) || Boolean.parseBoolean(useRedisCacheManager);
        }
    }
}
