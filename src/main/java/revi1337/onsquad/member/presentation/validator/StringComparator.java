package revi1337.onsquad.member.presentation.validator;

import java.util.Map;

public interface StringComparator {

    Map<String, String> inspectStrings();

    default boolean getCompareResult() {
        return inspectStrings().keySet().size() == 1;
    }
}
