package revi1337.onsquad.common.fixture;

import java.time.LocalDateTime;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.entity.SquadMemberFactory;

public class SquadMemberFixture {

    public static SquadMember createLeaderSquadMember(Squad squad, Member member) {
        return SquadMemberFactory.leader(squad, member, LocalDateTime.now());
    }

    public static SquadMember createGeneralSquadMember(Squad squad, Member member) {
        return SquadMemberFactory.general(squad, member, LocalDateTime.now());
    }

    public static SquadMember createGeneralSquadMember(Squad squad, Member member, LocalDateTime leaderParticipateAt) {
        return SquadMemberFactory.general(squad, member, leaderParticipateAt);
    }
}
