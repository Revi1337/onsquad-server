package revi1337.onsquad.common.aspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Aspect
@RequiredArgsConstructor
@Component
public class RedisCacheAspect {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Around("@annotation(redisCache)")
    public Object handleRedisCache(ProceedingJoinPoint joinPoint, RedisCache redisCache) {
        try {
            OnSquadType onsquadType = redisCache.type();
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            String redisKey = getRedisKey(joinPoint, redisCache, methodSignature, onsquadType);
            Object cachedData = redisTemplate.opsForValue().get(redisKey);

            Method method = methodSignature.getMethod();
            ParameterizedType genericReturnType = resolveGenericReturnType(method);
            if (cachedData != null && genericReturnType == null) {
                return objectMapper.convertValue(cachedData, method.getReturnType());
            }

            if (cachedData != null) {
                Class<?> actualGenericClass = retrieveInternalGenericType(genericReturnType);
                JavaType genericInternalType = null;
                if (genericReturnType.getRawType() instanceof Class<?> genericPrimitiveType) {
                    if (Collection.class.isAssignableFrom(genericPrimitiveType)) {
                        genericInternalType = objectMapper.getTypeFactory()
                                .constructCollectionType((Class<? extends Collection>) genericPrimitiveType, actualGenericClass);
                    } else {
                        genericInternalType = objectMapper.getTypeFactory()
                                .constructParametricType(genericPrimitiveType, actualGenericClass);
                    }
                }

                return objectMapper.convertValue(cachedData, genericInternalType);
            }

            Object result = joinPoint.proceed();                        // Invoke Method

            if (result != null) {
                boolean shouldCache;
                if (result instanceof Collection<?> collection) {
                    shouldCache = !collection.isEmpty() || redisCache.cacheEmptyCollection();
                } else {
                    shouldCache = true;
                }
                if (shouldCache) {
                    redisTemplate.opsForValue().set(redisKey, result, getTtlAsDuration(redisCache));
                }
            }

            return result;
        } catch (Throwable e) {
            throw new IllegalArgumentException("cannot invoke method " + joinPoint.getSignature().getName(), e);
        }
    }

    private String getRedisKey(ProceedingJoinPoint joinPoint, RedisCache redisCache, MethodSignature methodSignature, OnSquadType onsquadType) {
        Map<String, Object> parameterMap = mapParametersToValues(methodSignature, joinPoint.getArgs());
        Object communityTypeId = retrieveIdentifier(redisCache.id(), parameterMap);
        return String.format(onsquadType.getFormat(), communityTypeId, redisCache.name());
    }

    private Map<String, Object> mapParametersToValues(MethodSignature methodSignature, Object[] argumentValues) {
        String[] parameterNames = methodSignature.getParameterNames();

        return IntStream.range(0, parameterNames.length)
                .boxed()
                .collect(Collectors.toUnmodifiableMap(i -> parameterNames[i], i -> argumentValues[i]));
    }

    private Object retrieveIdentifier(String parameter, Map<String, Object> parameterMap) {
        Object communityTypeId = parameterMap.get(parameter);
        if (parameterMap.get(parameter) == null) {
            throw new IllegalArgumentException();
        }

        return communityTypeId;
    }

    private ParameterizedType resolveGenericReturnType(Method method) {
        if (method.getGenericReturnType() instanceof ParameterizedType parameterizedType) {
            return parameterizedType;
        }

        return null;
    }

    private Class<?> retrieveInternalGenericType(ParameterizedType genericReturnType) {
        for (Type typeArgument : genericReturnType.getActualTypeArguments()) {
            if (typeArgument instanceof Class<?> actualType) {
                return actualType;
            }
        }

        return null;
    }

    private Duration getTtlAsDuration(RedisCache redisCache) {
        long ttl = redisCache.ttl();
        TimeUnit unit = redisCache.unit();

        return switch (unit) {
            case NANOSECONDS -> Duration.ofNanos(ttl);
            case MICROSECONDS -> Duration.ofMillis(TimeUnit.MICROSECONDS.toMillis(ttl));
            case MILLISECONDS -> Duration.ofMillis(ttl);
            case SECONDS -> Duration.ofSeconds(ttl);
            case MINUTES -> Duration.ofMinutes(ttl);
            case HOURS -> Duration.ofHours(ttl);
            case DAYS -> Duration.ofDays(ttl);
        };
    }
}
