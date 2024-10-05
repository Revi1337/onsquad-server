package revi1337.onsquad.common.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class ThrottlingAspect {

    private final StringRedisTemplate stringRedisTemplate;

    @Before("@annotation(throttling)")
    public void checkInitialRequest(JoinPoint joinPoint, Throttling throttling) {
        String redisKey = buildRedisKey(joinPoint, throttling);
        var valueOperations = stringRedisTemplate.opsForValue();
        Boolean firstRequest = valueOperations.setIfAbsent(redisKey, LocalDateTime.now().toString(), throttling.perCycle(), throttling.unit());
        if (!firstRequest) {
            log.info("Duplicate Request");
            throw new IllegalArgumentException("Duplicate Request");
        }
    }

    private String buildRedisKey(JoinPoint joinPoint, Throttling throttling) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Map<String, Object> parameterMap = mapParametersToValues(methodSignature, joinPoint.getArgs());
        String redisKeyFormat = throttling.type().getFormat();

        if (throttling.name().isEmpty()) {
            String methodName = methodSignature.getMethod().getName();
            String kebabMethodName = convertCamelToKebab(methodName);
            return String.format(redisKeyFormat, parameterMap.get(throttling.id()), kebabMethodName);
        }

        return String.format(redisKeyFormat, parameterMap.get(throttling.id()), throttling.name());
    }

    private Map<String, Object> mapParametersToValues(MethodSignature methodSignature, Object[] argumentValues) {
        String[] parameterNames = methodSignature.getParameterNames();

        return IntStream.range(0, parameterNames.length)
                .boxed()
                .collect(Collectors.toUnmodifiableMap(i -> parameterNames[i], i -> argumentValues[i]));
    }

    private String convertCamelToKebab(String methodName) {
        return methodName.chars()
                .mapToObj(c -> (char) c)
                .map(c -> Character.isUpperCase(c) ? "-" + Character.toLowerCase(c) : c.toString())
                .collect(Collectors.joining());
    }
}
