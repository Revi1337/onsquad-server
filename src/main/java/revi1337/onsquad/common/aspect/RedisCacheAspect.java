package revi1337.onsquad.common.aspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@RequiredArgsConstructor
@Component
public class RedisCacheAspect {

    private static final String DELIMITER = ":";
    private static final String CACHE_NAME_PREFIX = "onsquad" + DELIMITER;
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Around("@annotation(redisCache)")
    public Object handleRedisCache(ProceedingJoinPoint joinPoint, RedisCache redisCache) {
        String redisKey = generateRedisKey(joinPoint, redisCache);
        Object cachedData = redisTemplate.opsForValue().get(redisKey);
        if (cachedData != null) {
            return deserializeCachedData(joinPoint, cachedData);
        }
        try {
            Object result = joinPoint.proceed();
            if (shouldCache(redisCache, result)) {
                redisTemplate.opsForValue().set(redisKey, result, calculateTtl(redisCache));
            }
            return result;
        } catch (Throwable e) {
            throw new IllegalArgumentException("Error executing method " + joinPoint.getSignature().getName());
        }
    }

    private String generateRedisKey(ProceedingJoinPoint joinPoint, RedisCache redisCache) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
        String[] paramNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < paramNames.length; i++) {
            standardEvaluationContext.setVariable(paramNames[i], args[i]);
        }
        Expression expression = spelExpressionParser.parseExpression(redisCache.key());
        String expressionValue = expression.getValue(standardEvaluationContext, String.class);
        return CACHE_NAME_PREFIX + redisCache.name() + DELIMITER + expressionValue;
    }

    private Object deserializeCachedData(ProceedingJoinPoint joinPoint, Object cachedData) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        ParameterizedType genericReturnType = resolveGenericReturnType(methodSignature);
        if (genericReturnType == null) {
            return objectMapper.convertValue(cachedData, methodSignature.getReturnType());
        }

        Type genericOuterClass = genericReturnType.getRawType();
        Class<?> genericInternalClass = guessGenericInternalClass(genericReturnType);
        JavaType javaType = determineJavaTypeForGeneric(genericOuterClass, genericInternalClass);
        return objectMapper.convertValue(cachedData, javaType);
    }

    private ParameterizedType resolveGenericReturnType(MethodSignature methodSignature) {
        Method method = methodSignature.getMethod();
        if (method.getGenericReturnType() instanceof ParameterizedType parameterizedType) {
            return parameterizedType;
        }
        return null;
    }

    private Class<?> guessGenericInternalClass(ParameterizedType genericReturnType) {
        for (Type typeArgument : genericReturnType.getActualTypeArguments()) {
            if (typeArgument instanceof Class<?> actualType) {
                return actualType;
            }
        }
        return null;
    }

    private JavaType determineJavaTypeForGeneric(Type genericOuterClass, Class<?> genericInternalClass) {
        if (genericOuterClass instanceof Class<?> genericPrimitiveType) {
            if (Collection.class.isAssignableFrom(genericPrimitiveType)) {
                return objectMapper.getTypeFactory().constructCollectionType(
                        (Class<? extends Collection>) genericPrimitiveType, genericInternalClass
                );
            }
            if (Map.class.isAssignableFrom(genericPrimitiveType)) {
                return objectMapper.getTypeFactory().constructMapType(
                        (Class<? extends Map>) genericPrimitiveType, String.class, genericInternalClass
                );
            }
            return objectMapper.getTypeFactory().constructParametricType(
                    genericPrimitiveType, genericInternalClass
            );
        }
        return null;
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
