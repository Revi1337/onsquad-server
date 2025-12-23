package revi1337.onsquad.crew.domain;

import java.util.Collections;
import java.util.List;
import revi1337.onsquad.crew.domain.result.CrewResult;

public final class CrewResults {

    private final List<CrewResult> results;

    public CrewResults(List<CrewResult> results) {
        this.results = Collections.unmodifiableList(results);
    }

    public List<Long> getIds() {
        return results.stream()
                .map(CrewResult::getId)
                .toList();
    }

    public List<CrewResult> values() {
        return results;
    }

    public boolean isNotEmpty() {
        return !results.isEmpty();
    }
}
