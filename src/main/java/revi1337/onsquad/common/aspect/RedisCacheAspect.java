package revi1337.onsquad.common.aspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Aspect
@RequiredArgsConstructor
@Component
public class RedisCacheAspect {

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
        Map<String, Object> params = getMethodParams(methodSignature, joinPoint.getArgs());
        String cacheKeyParam = getCacheKeyParam(redisCache.id(), params);
        return String.format(redisCache.type().getFormat(), cacheKeyParam, redisCache.name());
    }

    private Map<String, Object> getMethodParams(MethodSignature methodSignature, Object[] args) {
        String[] paramNames = methodSignature.getParameterNames();
        return IntStream.range(0, paramNames.length)
                .boxed()
                .collect(Collectors.toMap(i -> paramNames[i], i -> args[i]));
    }

    private String getCacheKeyParam(String paramName, Map<String, Object> params) {
        Object paramValue = params.get(paramName);
        if (paramValue == null) {
            throw new IllegalArgumentException("Missing parameter: " + paramName);
        }
        return paramValue.toString();
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
