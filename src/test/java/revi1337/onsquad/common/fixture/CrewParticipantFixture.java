package revi1337.onsquad.common.fixture;

import static revi1337.onsquad.common.config.FixedTime.CLOCK;

import java.time.LocalDateTime;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_participant.domain.CrewParticipant;
import revi1337.onsquad.member.domain.Member;

public class CrewParticipantFixture {

    public static CrewParticipant CREW_PARTICIPANT(Crew crew, Member member, LocalDateTime requestAt) {
        return new CrewParticipant(crew, member, requestAt);
    }

    public static CrewParticipant CREW_PARTICIPANT(Crew crew, Member member) {
        return new CrewParticipant(crew, member, LocalDateTime.now(CLOCK));
    }
}
