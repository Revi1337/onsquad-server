package revi1337.onsquad.common.fixture;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;

public class SquadParticipantFixture {

    public static SquadRequest SQUAD_PARTICIPANT(Squad squad, CrewMember crewMember) {
        return SquadRequest.of(squad, crewMember, LocalDateTime.now());
    }

    public static SquadRequest SQUAD_PARTICIPANT(Squad squad, CrewMember crewMember, LocalDateTime requestAt) {
        return SquadRequest.of(squad, crewMember, requestAt);
    }
}
