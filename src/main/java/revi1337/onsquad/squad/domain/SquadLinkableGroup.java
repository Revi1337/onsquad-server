package revi1337.onsquad.squad.domain;

import java.util.Collections;
import java.util.List;

public record SquadLinkableGroup<T extends SquadLinkable>(List<T> results) {

    public SquadLinkableGroup(List<T> results) {
        this.results = Collections.unmodifiableList(results);
    }

    public List<Long> getSquadIds() {
        return results.stream()
                .map(SquadLinkable::getSquadId)
                .toList();
    }

    public List<T> values() {
        return results;
    }

    public boolean isNotEmpty() {
        return !results.isEmpty();
    }
}
