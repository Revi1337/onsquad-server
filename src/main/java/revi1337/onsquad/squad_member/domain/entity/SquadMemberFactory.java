package revi1337.onsquad.squad_member.domain.entity;

import static revi1337.onsquad.squad_member.domain.entity.vo.JoinStatus.ACCEPT;
import static revi1337.onsquad.squad_member.domain.entity.vo.JoinStatus.PENDING;
import static revi1337.onsquad.squad_member.domain.entity.vo.SquadRole.GENERAL;
import static revi1337.onsquad.squad_member.domain.entity.vo.SquadRole.LEADER;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.domain.entity.Squad;

public abstract class SquadMemberFactory {

    public static SquadMember general(Squad squad, CrewMember crewMember, LocalDateTime participantAt) {
        SquadMember squadMember = general(crewMember, participantAt);
        squadMember.addSquad(squad);
        return squadMember;
    }

    public static SquadMember general(CrewMember crewMember, LocalDateTime participantAt) {
        SquadMember squadMember = new SquadMember(GENERAL, PENDING, participantAt);
        squadMember.addOwner(crewMember);
        return squadMember;
    }

    public static SquadMember leader(Squad squad, CrewMember crewMember, LocalDateTime participantAt) {
        SquadMember squadMember = leader(crewMember, participantAt);
        squadMember.addSquad(squad);
        return squadMember;
    }

    public static SquadMember leader(CrewMember crewMember, LocalDateTime participantAt) {
        SquadMember squadMember = new SquadMember(LEADER, ACCEPT, participantAt);
        squadMember.addOwner(crewMember);
        return squadMember;
    }
}
