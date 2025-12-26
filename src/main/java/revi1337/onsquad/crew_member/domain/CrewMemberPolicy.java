package revi1337.onsquad.crew_member.domain;

import lombok.RequiredArgsConstructor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.error.CrewMemberBusinessException;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;

@RequiredArgsConstructor
public class CrewMemberPolicy {

    public static boolean cannotReadSquadParticipants(CrewMember crewMember) {
        return crewMember.isNotOwner();
    }

    public static boolean canMangeCrew(CrewMember crewMember) {
        return crewMember.isOwner();
    }

    public static void ensureReadParticipantsAccessible(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_READ_PARTICIPANTS_AUTHORITY);
        }
    }

    public static void ensureReadCrewStatisticAccessible(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_READ_CREW_STATISTIC_AUTHORITY);
        }
    }
}
