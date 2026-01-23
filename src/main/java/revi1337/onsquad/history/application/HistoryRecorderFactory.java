package revi1337.onsquad.history.application;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HistoryRecorderFactory {

    private final List<HistoryRecorder> strategies;

    public Optional<HistoryRecorder> find(Class<?> clazz, Method method) {
        return strategies.stream()
                .filter(s -> s.supports(clazz, method))
                .findFirst();
    }
}
