package revi1337.onsquad.common.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import revi1337.onsquad.common.error.CommonErrorCode;
import revi1337.onsquad.common.error.exception.CommonBusinessException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Aspect
@Component
public class ThrottlingAspect {

    private final RequestCacheHandlerExecutionChain handlerExecutionChain;

    @Before("@annotation(throttling)")
    public void checkInitialRequest(JoinPoint joinPoint, Throttling throttling) {
        String redisKey = buildRedisKey(joinPoint, throttling);
        boolean firstRequest = handlerExecutionChain.isFirstRequest(redisKey, LocalDateTime.now().toString(), throttling.perCycle(), throttling.unit());
        if (!firstRequest) {
            throw new CommonBusinessException.RequestConflict(
                    CommonErrorCode.REQUEST_CONFLICT, getCycleAsDuration(throttling)
            );
        }
    }

    private String buildRedisKey(JoinPoint joinPoint, Throttling throttling) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Map<String, Object> parameterMap = mapParametersToValues(methodSignature, joinPoint.getArgs());
        String redisKeyFormat = throttling.type().getFormat();

        if (throttling.name().isEmpty()) {
            String kebabMethodName = convertCamelToKebab(methodSignature);
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

    private String convertCamelToKebab(MethodSignature methodSignature) {
        String methodName = methodSignature.getMethod().getName();

        return methodName.chars()
                .mapToObj(c -> (char) c)
                .map(c -> Character.isUpperCase(c) ? "-" + Character.toLowerCase(c) : c.toString())
                .collect(Collectors.joining());
    }

    private String getCycleAsDuration(Throttling throttling) {
        return throttling.perCycle() + convertAsTimeUnitString(throttling.unit());
    }

    private String convertAsTimeUnitString(TimeUnit unit) {
        return switch (unit) {
            case NANOSECONDS -> " nano sec";
            case MICROSECONDS -> " micro sec";
            case MILLISECONDS -> " milli sec";
            case SECONDS -> " sec";
            case MINUTES -> " min";
            case HOURS -> " hour";
            case DAYS -> " day";
        };
    }
}
