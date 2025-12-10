package revi1337.onsquad.squad_member.domain.entity;

import static revi1337.onsquad.squad_member.domain.entity.vo.JoinStatus.ACCEPT;
import static revi1337.onsquad.squad_member.domain.entity.vo.JoinStatus.PENDING;
import static revi1337.onsquad.squad_member.domain.entity.vo.SquadRole.GENERAL;
import static revi1337.onsquad.squad_member.domain.entity.vo.SquadRole.LEADER;

import java.time.LocalDateTime;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad.domain.entity.Squad;

public abstract class SquadMemberFactory {

    public static SquadMember general(Squad squad, Member member, LocalDateTime participantAt) {
        SquadMember squadMember = general(member, participantAt);
        squadMember.addSquad(squad);
        return squadMember;
    }

    public static SquadMember general(Member member, LocalDateTime participantAt) {
        SquadMember squadMember = new SquadMember(GENERAL, PENDING, participantAt);
        squadMember.addOwner(member);
        return squadMember;
    }

    public static SquadMember leader(Squad squad, Member member, LocalDateTime participantAt) {
        SquadMember squadMember = leader(member, participantAt);
        squadMember.addSquad(squad);
        return squadMember;
    }

    public static SquadMember leader(Member member, LocalDateTime participantAt) {
        SquadMember squadMember = new SquadMember(LEADER, ACCEPT, participantAt);
        squadMember.addOwner(member);
        return squadMember;
    }
}
