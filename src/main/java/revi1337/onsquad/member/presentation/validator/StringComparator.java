package revi1337.onsquad.member.presentation.validator;

import java.util.HashSet;
import java.util.Map;

public interface StringComparator {

    Map<String, String> inspectStrings();

    default boolean compareResult() {
        return new HashSet<>(inspectStrings().values()).size() == 1;
    }
}
