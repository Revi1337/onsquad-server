package revi1337.onsquad.announce.domain.model;

import java.util.Collections;
import java.util.List;
import revi1337.onsquad.announce.domain.entity.Announce;

public class Announces {

    private final List<Announce> announces;

    public Announces(List<Announce> announces) {
        this.announces = Collections.unmodifiableList(announces);
    }

    public List<Long> getWriterIds() {
        return announces.stream()
                .map(announce -> announce.getMember().getId())
                .distinct()
                .toList();
    }

    public List<Announce> values() {
        return announces;
    }
}
