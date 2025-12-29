package revi1337.onsquad.squad.domain;

import lombok.RequiredArgsConstructor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.error.CrewMemberBusinessException;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.error.SquadBusinessException;
import revi1337.onsquad.squad.error.SquadErrorCode;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

@RequiredArgsConstructor
public class SquadPolicy {

    public static boolean isLeader(SquadMember squadMember) {
        return squadMember != null && squadMember.isLeader();
    }

    public static boolean isLastMemberRemaining(Squad squad) {
        return squad.getCurrentSize() == 1;
    }

    public static void ensureMatchCrew(Squad squad, Long crewId) {
        if (squad.mismatchCrewId(crewId)) {
            throw new SquadBusinessException.MismatchReference(SquadErrorCode.MISMATCH_CREW_REFERENCE);
        }
    }

    public static void ensureDeletable(SquadMember squadMember) {
        if (squadMember.isNotLeader()) {
            throw new SquadBusinessException.InsufficientAuthority(SquadErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
    }

    public static void ensureDeletable(Squad squad, Long memberId) {
        if (squad.mismatchMemberId(memberId)) {
            throw new SquadBusinessException.InsufficientAuthority(SquadErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
    }

    public static void ensureSquadManageListAccessible(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(CrewMemberErrorCode.NOT_OWNER);
        }
    }
}
