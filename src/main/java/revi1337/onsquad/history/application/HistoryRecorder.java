package revi1337.onsquad.history.application;

import java.lang.reflect.Method;

public interface HistoryRecorder {

    boolean supports(Class<?> clazz, Method method);

    void record(Object[] args, Object result);

}
