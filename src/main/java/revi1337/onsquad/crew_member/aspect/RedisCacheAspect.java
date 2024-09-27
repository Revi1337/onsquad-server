package revi1337.onsquad.crew_member.aspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_member.aspect.RedisCache.CommunityType;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Aspect
@Component
@RequiredArgsConstructor
public class RedisCacheAspect {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Around("@annotation(redisCache)")
    public Object handleRedisCache(ProceedingJoinPoint joinPoint, RedisCache redisCache) {
        try {
            CommunityType communityType = redisCache.type();
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            String redisKey = getRedisKey(joinPoint, redisCache, methodSignature, communityType);
            Object cachedData = redisTemplate.opsForValue().get(redisKey);

            Method method = methodSignature.getMethod();
            ParameterizedType genericReturnType = resolveGenericReturnType(method);
            if (cachedData != null) {
                if (genericReturnType == null) {
                    return objectMapper.convertValue(cachedData, method.getReturnType());
                }

                Class<?> actualGenericClass = retrieveInternalGenericType(genericReturnType);
                if (genericReturnType.getRawType() instanceof Class<?> genericPrimitiveType) {
                    if (Collection.class.isAssignableFrom(genericPrimitiveType)) {
                        CollectionType collectionType = objectMapper.getTypeFactory()
                                .constructCollectionType((Class<? extends Collection>) genericPrimitiveType, actualGenericClass);
                        return objectMapper.convertValue(cachedData, collectionType);
                    }

                    JavaType javaType = objectMapper.getTypeFactory()
                            .constructParametricType(genericPrimitiveType, actualGenericClass);
                    return objectMapper.convertValue(cachedData, javaType);
                }
            }

            Object result = joinPoint.proceed();
            redisTemplate.opsForValue().set(redisKey, result, getTtlAsDuration(redisCache));

            return result;

        } catch (Throwable e) {
            throw new IllegalArgumentException("cannot invoke method " + joinPoint.getSignature().getName(), e);
        }
    }

    private String getRedisKey(ProceedingJoinPoint joinPoint, RedisCache redisCache, MethodSignature methodSignature, CommunityType communityType) {
        Map<String, Object> parameterMap = mapParametersToValues(methodSignature, joinPoint.getArgs());
        Object communityTypeId = retrieveIdentifier(redisCache.id(), parameterMap);
        String redisKey = String.format(communityType.getFormat(), communityTypeId, redisCache.name());
        return redisKey;
    }

    private Map<String, Object> mapParametersToValues(MethodSignature methodSignature, Object[] argumentValues) {
        String[] parameterNames = methodSignature.getParameterNames();

        return IntStream.range(0, parameterNames.length)
                .boxed()
                .collect(Collectors.toUnmodifiableMap(i -> parameterNames[i], i -> argumentValues[i]));
    }

    private Object retrieveIdentifier(String parameter, Map<String, Object> parameterMap) {
        Object communityTypeId = parameterMap.get(parameter);
        if (communityTypeId == null) {
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





//package revi1337.onsquad.crew_member.application.aspect;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.type.CollectionType;
//import lombok.RequiredArgsConstructor;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//import revi1337.onsquad.crew_member.application.aspect.RedisCache.CommunityType;
//
//import java.lang.reflect.Method;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//@Aspect
//@Component
//@RequiredArgsConstructor
//public class RedisCacheAspect {
//
//    private final RedisTemplate<String, Object> redisTemplate;
//    private final ObjectMapper objectMapper;
//
//    @Around("@annotation(redisCache)")
//    public Object handleRedisCache(ProceedingJoinPoint joinPoint, RedisCache redisCache) {
//        try {
//            CommunityType communityType = redisCache.type();
//            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//            Method method = methodSignature.getMethod();
//            Class<?> genericClass = guessMethodReturnTypeIsGeneric(method);
//
//            Map<String, Object> parameterMap = mapParametersToValues(methodSignature, joinPoint.getArgs());
//            Object communityTypeId = retrieveIdentifier(redisCache.parameter(), parameterMap);
//            String redisKey = String.format(communityType.getFormat(), communityTypeId, redisCache.key());
//            Object cachedData = redisTemplate.opsForValue().get(redisKey);
//
//            if (cachedData != null) {
//                if (genericClass == null) {
//                    return objectMapper.convertValue(cachedData, method.getReturnType());
//                }
//
//                ParameterizedType genericParameterizedType = (ParameterizedType) method.getGenericReturnType();
//                if (genericParameterizedType.getRawType() instanceof Class<?> genericPrimitiveType) {
//                    if (Collection.class.isAssignableFrom(genericPrimitiveType)) {
//                        CollectionType collectionType = objectMapper.getTypeFactory()
//                                .constructCollectionType((Class<? extends Collection>) genericPrimitiveType, genericClass);
//
//                        return objectMapper.convertValue(cachedData, collectionType);
//                    }
//                }
//
//                return objectMapper.convertValue(cachedData, method.getReturnType());
//            }
//
//            Object result = joinPoint.proceed();
//            redisTemplate.opsForValue().set(redisKey, result);
//
//            return result;
//
//        } catch (Throwable e) {
//            throw new IllegalArgumentException("cannot invoke method " + joinPoint.getSignature().getName(), e);
//        }
//    }
//
//    private Class<?> guessMethodReturnTypeIsGeneric(Method method) {
//        Class<?> genericClass = null;
//        if (method.getGenericReturnType() instanceof ParameterizedType parameterizedType) {
//            for (Type typeArgument : parameterizedType.getActualTypeArguments()) {
//                if (typeArgument instanceof Class<?> actualType) {
//                    genericClass = actualType;
//                    break;
//                }
//            }
//        }
//
//        return genericClass;
//    }
//
//    private Map<String, Object> mapParametersToValues(MethodSignature methodSignature, Object[] argumentValues) {
//        String[] parameterNames = methodSignature.getParameterNames();
//
//        return IntStream.range(0, parameterNames.length)
//                .boxed()
//                .collect(Collectors.toUnmodifiableMap(i -> parameterNames[i], i -> argumentValues[i]));
//    }
//
//    private Object retrieveIdentifier(String parameter, Map<String, Object> parameterMap) {
//        Object communityTypeId = parameterMap.get(parameter);
//        if (communityTypeId == null) {
//            throw new IllegalArgumentException();
//        }
//
//        return communityTypeId;
//    }
//}





//package revi1337.onsquad.crew_member.application.aspect;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.type.CollectionType;
//import lombok.RequiredArgsConstructor;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//import revi1337.onsquad.crew_member.application.aspect.RedisCache.CommunityType;
//
//import java.lang.reflect.Method;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//@Aspect
//@Component
//@RequiredArgsConstructor
//public class RedisCacheAspect {
//
//    private final RedisTemplate<String, Object> redisTemplate;
//    private final ObjectMapper objectMapper;
//
//    @Around("@annotation(redisCache)")
//    public Object handleRedisCache(ProceedingJoinPoint joinPoint, RedisCache redisCache) {
//        try {
//            CommunityType communityType = redisCache.type();
//            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//            Method method = methodSignature.getMethod();
//            Class<?> genericClass = guessMethodReturnTypeIsGeneric(method);
//
//            Map<String, Object> parameterMap = mapParametersToValues(methodSignature, joinPoint.getArgs());
//            Object communityTypeId = retrieveIdentifier(redisCache.parameter(), parameterMap);
//            String redisKey = String.format(communityType.getFormat(), communityTypeId, redisCache.key());
//            Object cachedData = redisTemplate.opsForValue().get(redisKey);
//
//            if (cachedData != null) {
//                if (genericClass != null) {
//                    CollectionType collectionType = objectMapper.getTypeFactory()
//                            .constructCollectionType(Collection.class, genericClass);
//                    return objectMapper.convertValue(cachedData, collectionType);
//                }
//
//                return objectMapper.convertValue(cachedData, method.getReturnType());
//            }
//
//            Object result = joinPoint.proceed();
//            redisTemplate.opsForValue().set(redisKey, result);
//
//            return result;
//
//        } catch (Throwable e) {
//            throw new IllegalArgumentException("cannot invoke method " + joinPoint.getSignature().getName(), e);
//        }
//    }
//
//    private Class<?> guessMethodReturnTypeIsGeneric(Method method) {
//        Class<?> genericClass = null;
//        if (method.getGenericReturnType() instanceof ParameterizedType parameterizedType) {
//            for (Type typeArgument : parameterizedType.getActualTypeArguments()) {
//                if (typeArgument instanceof Class<?> actualType) {
//                    genericClass = actualType;
//                    break;
//                }
//            }
//        }
//
//        return genericClass;
//    }
//
//    private Map<String, Object> mapParametersToValues(MethodSignature methodSignature, Object[] argumentValues) {
//        String[] parameterNames = methodSignature.getParameterNames();
//
//        return IntStream.range(0, parameterNames.length)
//                .boxed()
//                .collect(Collectors.toUnmodifiableMap(i -> parameterNames[i], i -> argumentValues[i]));
//    }
//
//    private Object retrieveIdentifier(String parameter, Map<String, Object> parameterMap) {
//        Object communityTypeId = parameterMap.get(parameter);
//        if (communityTypeId == null) {
//            throw new IllegalArgumentException();
//        }
//
//        return communityTypeId;
//    }
//}
