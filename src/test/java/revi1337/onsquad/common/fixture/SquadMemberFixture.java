package revi1337.onsquad.common.fixture;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.entity.SquadMemberFactory;

public class SquadMemberFixture {

    public static SquadMember GENERAL_SQUAD_MEMBER(Squad squad, CrewMember crewMember) {
        return SquadMemberFactory.general(squad, crewMember, LocalDateTime.now());
    }

    public static SquadMember GENERAL_SQUAD_MEMBER(CrewMember crewMember) {
        return SquadMemberFactory.general(crewMember, LocalDateTime.now());
    }

    public static SquadMember LEADER_SQUAD_MEMBER(Squad squad, CrewMember crewMember) {
        return SquadMemberFactory.leader(squad, crewMember, LocalDateTime.now());
    }

    public static SquadMember LEADER_SQUAD_MEMBER(CrewMember crewMember) {
        return SquadMemberFactory.leader(crewMember, LocalDateTime.now());
    }
}
