package revi1337.onsquad.crew_member.domain.event;

import java.time.Instant;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;

public record ScoreIncreased(
        Long crewId,
        Long memberId,
        Instant applyAt,
        CrewActivity crewActivity
) {

    public ScoreIncreased(Long crewId, Long memberId, CrewActivity crewActivity) {
        this(crewId, memberId, Instant.now(), crewActivity);
    }
}
