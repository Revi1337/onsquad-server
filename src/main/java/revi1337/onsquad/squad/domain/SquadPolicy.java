package revi1337.onsquad.squad.domain;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import revi1337.onsquad.crew.domain.CrewPolicy;
import revi1337.onsquad.crew_member.domain.CrewMemberPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.error.SquadBusinessException;
import revi1337.onsquad.squad.error.SquadErrorCode;
import revi1337.onsquad.squad_member.domain.SquadMemberPolicy;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

@NoArgsConstructor(access = PRIVATE)
public final class SquadPolicy {

    public static boolean isLastMemberRemaining(Squad squad) {
        return squad.getCurrentSize() == 1;
    }

    public static boolean canDelete(CrewMember me) {
        return CrewPolicy.canDelete(me);
    }

    public static boolean canDelete(SquadMember me, CrewMember meInCrew) {
        return SquadMemberPolicy.isLeader(me) || CrewPolicy.canDelete(meInCrew);
    }

    public static boolean canLeave(SquadMember me, Squad squad) {
        return isLastMemberRemaining(squad) || SquadMemberPolicy.isNotLeader(me);
    }

    public static boolean canReadParticipants(CrewMember me) {
        return CrewMemberPolicy.isOwner(me);
    }

    public static boolean cannotReadParticipants(CrewMember me) {
        return !canReadParticipants(me);
    }

    public static void ensureDeletable(SquadMember me, CrewMember meInCrew) {
        if (!canDelete(me, meInCrew)) {
            throw new SquadBusinessException.InsufficientAuthority(SquadErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
    }

    public static void ensureManageable(CrewMember me) {
        if (CrewMemberPolicy.isNotOwner(me)) {
            throw new SquadBusinessException.InsufficientAuthority(SquadErrorCode.INSUFFICIENT_MANAGE_SQUAD_AUTHORITY);
        }
    }

    public static void ensureLeavable(Squad squad, SquadMember me) {
        if (mismatchSquad(squad, me)) {
            throw new SquadBusinessException.MismatchReference(SquadErrorCode.MISMATCH_SQUAD_REFERENCE);
        }
        if (SquadMemberPolicy.isLeader(me) && !isLastMemberRemaining(squad)) {
            throw new SquadBusinessException.InsufficientAuthority(SquadErrorCode.INSUFFICIENT_LEAVE_SQUAD_AUTHORITY);
        }
    }

    private static boolean mismatchSquad(Squad squad, SquadMember me) {
        return !squad.getId().equals(me.getSquad().getId());
    }
}
