package revi1337.onsquad.squad.domain.event;

import java.util.List;

public record SquadContextDisposed(List<Long> squadIds) {

    public SquadContextDisposed(Long squadId) {
        this(List.of(squadId));
    }

    public SquadContextDisposed(List<Long> squadIds) {
        this.squadIds = squadIds;
    }
}
