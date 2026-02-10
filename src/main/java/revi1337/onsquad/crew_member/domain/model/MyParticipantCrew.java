package revi1337.onsquad.crew_member.domain.model;

import java.time.LocalDateTime;
import revi1337.onsquad.crew.domain.model.SimpleCrew;

public record MyParticipantCrew(
        Boolean isOwner,
        LocalDateTime participateAt,
        SimpleCrew crew
) {

}
