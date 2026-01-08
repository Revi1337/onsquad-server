package revi1337.onsquad.squad.domain;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import revi1337.onsquad.crew_member.domain.CrewMemberPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.error.CrewMemberBusinessException;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.error.SquadBusinessException;
import revi1337.onsquad.squad.error.SquadErrorCode;
import revi1337.onsquad.squad_member.domain.SquadMemberPolicy;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

@NoArgsConstructor(access = PRIVATE)
public class SquadPolicy {

    public static boolean isLastMemberRemaining(Squad squad) {
        return squad.getCurrentSize() == 1;
    }

    public static boolean canDelete(CrewMember me) {
        return CrewMemberPolicy.canDeleteCrew(me);
    }

    public static boolean canDelete(SquadMember me, CrewMember meInCrew) {
        return SquadMemberPolicy.canDeleteSquad(me) || CrewMemberPolicy.canDeleteCrew(meInCrew);
    }

    public static void ensureDeletable(SquadMember me, CrewMember meInCrew) {
        if (!canDelete(me, meInCrew)) {
            throw new SquadBusinessException.InsufficientAuthority(SquadErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
    }

    public static void ensureSquadManageListAccessible(CrewMember me) {
        if (CrewMemberPolicy.isNotOwner(me)) {
            throw new CrewMemberBusinessException.NotOwner(CrewMemberErrorCode.NOT_OWNER);
        }
    }
}
