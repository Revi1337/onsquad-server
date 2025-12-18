package revi1337.onsquad.history.application;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class HistoryRecorderFactory {

    private final List<HistoryRecordStrategy> strategies;

    public Optional<HistoryRecordStrategy> find(Class<?> clazz, Method method) {
        return strategies.stream()
                .filter(s -> s.supports(clazz, method))
                .findFirst();
    }
}
