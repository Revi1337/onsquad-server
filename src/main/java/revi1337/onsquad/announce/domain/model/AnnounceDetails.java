package revi1337.onsquad.announce.domain.model;

import java.util.Collections;
import java.util.List;

public class AnnounceDetails {

    private final List<AnnounceDetail> announces;

    public AnnounceDetails(List<AnnounceDetail> announces) {
        this.announces = Collections.unmodifiableList(announces);
    }

    public List<Long> getWriterIds() {
        return announces.stream()
                .map(detail -> detail.writer().id())
                .distinct()
                .toList();
    }

    public List<AnnounceDetail> values() {
        return announces;
    }
}
