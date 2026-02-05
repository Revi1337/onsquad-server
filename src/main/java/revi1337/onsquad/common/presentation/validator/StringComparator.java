package revi1337.onsquad.common.presentation.validator;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public interface StringComparator {

    Map<String, String> getComparedFields();

    default boolean compareResult() {
        Collection<String> values = getComparedFields().values();
        if (values.isEmpty()) {
            return true;
        }
        String first = values.iterator().next();
        if (first == null) {
            return false;
        }
        return values.stream().allMatch(v -> Objects.equals(first, v));
    }
}
