package revi1337.onsquad.history.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
public class HistoryCandidatesInspector {

    private final Map<Class<?>, Set<String>> invokableCache;

    public HistoryCandidatesInspector() {
        Map<Class<?>, Set<String>> cache = new HashMap<>();
        for (HistoricCandidate entry : HistoricCandidate.CANDIDATES) {
            Class<?> clazz = findClass(entry);
            cache.putIfAbsent(clazz, new HashSet<>());
            validateMethodExists(clazz, entry.method(), entry.paramTypes());
            cache.get(clazz).add(entry.method());
        }
        this.invokableCache = Collections.unmodifiableMap(cache);
    }

    public boolean canInspect(Class<?> clazz, String methodName) {
        Set<String> methods = invokableCache.get(clazz);
        return methods != null && methods.contains(methodName);
    }

    private Class<?> findClass(HistoricCandidate candidate) {
        try {
            return Class.forName(candidate.clazz().getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Historic candidate class not found: " + candidate.clazz(), e);
        }
    }

    private void validateMethodExists(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        if (ReflectionUtils.findMethod(clazz, methodName, paramTypes) == null) {
            throw new IllegalArgumentException(
                    "Historic candidate method not found in class " + clazz.getName() + ": " + methodName + " with parameter types " +
                            Arrays.toString(paramTypes)
            );
        }
    }
}
