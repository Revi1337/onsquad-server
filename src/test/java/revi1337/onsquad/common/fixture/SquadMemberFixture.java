package revi1337.onsquad.common.fixture;

import static revi1337.onsquad.common.config.FixedTime.CLOCK;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.squad.domain.Squad;
import revi1337.onsquad.squad_member.domain.SquadMember;

public class SquadMemberFixture {

    public static SquadMember GENERAL_SQUAD_MEMBER(Squad squad, CrewMember crewMember) {
        return SquadMember.forGeneral(squad, crewMember, LocalDateTime.now(CLOCK));
    }

    public static SquadMember GENERAL_SQUAD_MEMBER(CrewMember crewMember) {
        return SquadMember.forGeneral(crewMember, LocalDateTime.now(CLOCK));
    }

    public static SquadMember LEADER_SQUAD_MEMBER(Squad squad, CrewMember crewMember) {
        return SquadMember.forLeader(squad, crewMember, LocalDateTime.now(CLOCK));
    }

    public static SquadMember LEADER_SQUAD_MEMBER(CrewMember crewMember) {
        return SquadMember.forLeader(crewMember, LocalDateTime.now(CLOCK));
    }
}
