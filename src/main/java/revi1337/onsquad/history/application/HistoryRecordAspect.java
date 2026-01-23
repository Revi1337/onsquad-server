package revi1337.onsquad.history.application;

import java.lang.reflect.Method;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import revi1337.onsquad.history.config.HistoryCandidatesInspector;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class HistoryRecordAspect {

    private final HistoryCandidatesInspector historyCandidatesInspector;
    private final HistoryRecorderFactory historyRecorderFactory;

    @Around("execution(* revi1337.onsquad..*CommandService.*(..)) || execution(* revi1337.onsquad..*QueryService.*(..))")
    public Object recordHistory(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        Class<?> clazz = method.getDeclaringClass();
        if (!historyCandidatesInspector.canInspect(clazz, method.getName())) {
            return proceedingJoinPoint.proceed();
        }
        Object result = proceedingJoinPoint.proceed();
        recordHistory(clazz, method, proceedingJoinPoint.getArgs(), result);
        return result;
    }

    private void recordHistory(Class<?> clazz, Method method, Object[] args, Object result) {
        Optional<HistoryRecordStrategy> recordStrategy = historyRecorderFactory.find(clazz, method);
        if (recordStrategy.isEmpty()) {
            return;
        }
        HistoryRecordStrategy historyRecordStrategy = recordStrategy.get();
        try {
            historyRecordStrategy.record(args, result);
        } catch (Throwable throwable) {
            // This is a non-critical logging process any exceptions are logged and intentionally ignored.
            log.error("error while record history. cause: {}", throwable.getMessage());
        }
    }
}
