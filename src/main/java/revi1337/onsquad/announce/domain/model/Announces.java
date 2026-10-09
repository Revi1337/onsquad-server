package revi1337.onsquad.announce.domain.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import revi1337.onsquad.announce.domain.entity.Announce;

public class Announces {

    private final List<Announce> announces;

    public Announces(List<Announce> announces) {
        this.announces = Collections.unmodifiableList(announces);
    }

    public List<Long> getWriterIds() {
        return announces.stream()
                .map(Announce::getWriterId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    public List<Announce> values() {
        return announces;
    }
}
