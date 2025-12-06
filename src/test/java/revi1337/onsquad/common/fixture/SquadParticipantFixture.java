package revi1337.onsquad.common.fixture;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_participant.domain.entity.SquadParticipant;

public class SquadParticipantFixture {

    public static SquadParticipant SQUAD_PARTICIPANT(Squad squad, CrewMember crewMember) {
        return SquadParticipant.of(squad, crewMember, LocalDateTime.now());
    }

    public static SquadParticipant SQUAD_PARTICIPANT(Squad squad, CrewMember crewMember, LocalDateTime requestAt) {
        return SquadParticipant.of(squad, crewMember, requestAt);
    }
}
