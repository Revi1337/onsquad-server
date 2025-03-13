package revi1337.onsquad.common.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@RequiredArgsConstructor
@Component
public class RedisCacheAspect {

    private static final String DELIMITER = ":";
    private static final String CACHE_NAME_PREFIX = "onsquad" + DELIMITER;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    @Around("@annotation(redisCache)")
    public Object handleRedisCache(ProceedingJoinPoint joinPoint, RedisCache redisCache) {
        String redisKey = generateRedisKey(joinPoint, redisCache);
        String cachedData = redisTemplate.opsForValue().get(redisKey);
        if (cachedData != null) {
            return deserializeCachedData(cachedData, joinPoint);
        }
        try {
            Object result = joinPoint.proceed();
            if (shouldCache(redisCache, result)) {
                redisTemplate.opsForValue().set(redisKey, serializeData(result), calculateTtl(redisCache));
            }
            return result;
        } catch (Throwable e) {
            throw new RuntimeException("Error executing method " + joinPoint.getSignature().getName(), e);
        }
    }

    private String generateRedisKey(ProceedingJoinPoint joinPoint, RedisCache redisCache) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        StandardEvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        String expressionValue = spelExpressionParser.parseExpression(redisCache.key()).getValue(context, String.class);
        return CACHE_NAME_PREFIX + redisCache.name() + DELIMITER + expressionValue;
    }

    private String serializeData(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization error", e);
        }
    }

    private Object deserializeCachedData(String cachedData, ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        JavaType javaType = objectMapper.getTypeFactory()
                .constructType(methodSignature.getMethod().getGenericReturnType());
        try {
            return objectMapper.readValue(cachedData, javaType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Deserialization error", e);
        }
    }

    private boolean shouldCache(RedisCache redisCache, Object result) {
        if (result == null) {
            return false;
        }
        if (result instanceof Collection<?> collection) {
            return !collection.isEmpty() || redisCache.cacheEmptyCollection();
        }
        return true;
    }

    private Duration calculateTtl(RedisCache redisCache) {
        return Duration.of(redisCache.ttl(), redisCache.unit().toChronoUnit());
    }
}
