package revi1337.onsquad.common.fixture;

import static revi1337.onsquad.common.config.FixedTime.CLOCK;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad_participant.domain.SquadParticipant;

public class SquadParticipantFixture {

    public static SquadParticipant SQUAD_PARTICIPANT(Squad squad, CrewMember crewMember) {
        return SquadParticipant.of(squad, crewMember, LocalDateTime.now(CLOCK));
    }

    public static SquadParticipant SQUAD_PARTICIPANT(Squad squad, CrewMember crewMember, LocalDateTime requestAt) {
        return SquadParticipant.of(squad, crewMember, requestAt);
    }
}
