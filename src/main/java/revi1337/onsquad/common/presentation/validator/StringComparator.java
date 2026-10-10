package revi1337.onsquad.common.presentation.validator;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public interface StringComparator {

    Map<String, String> getComparedFields();

    static Map<String, String> fields(String firstName, String firstValue, String secondName, String secondValue) {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put(firstName, firstValue);
        fields.put(secondName, secondValue);
        return fields;
    }

    default boolean compareResult() {
        Collection<String> values = getComparedFields().values();
        if (values.isEmpty() || values.stream().anyMatch(Objects::isNull)) {
            return true;
        }
        String first = values.iterator().next();
        return values.stream().allMatch(v -> Objects.equals(first, v));
    }
}
