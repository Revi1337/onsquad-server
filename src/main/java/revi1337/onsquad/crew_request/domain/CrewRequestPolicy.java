package revi1337.onsquad.crew_request.domain;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import revi1337.onsquad.crew_member.domain.CrewMemberPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.error.CrewRequestBusinessException;
import revi1337.onsquad.crew_request.error.CrewRequestErrorCode;

@NoArgsConstructor(access = PRIVATE)
public final class CrewRequestPolicy {

    public static void ensureMatchCrew(CrewRequest request, Long crewId) {
        if (mismatchCrew(request, crewId)) {
            throw new CrewRequestBusinessException.MismatchReference(CrewRequestErrorCode.MISMATCH_CREW_REFERENCE);
        }
    }

    public static void ensureReadRequests(CrewMember me) {
        if (CrewMemberPolicy.isLowerThanManager(me)) {
            throw new CrewRequestBusinessException.InsufficientAuthority(CrewRequestErrorCode.INSUFFICIENT_READ_LIST_AUTHORITY);
        }
    }

    public static void ensureAcceptable(CrewMember me) {
        if (CrewMemberPolicy.isLowerThanManager(me)) {
            throw new CrewRequestBusinessException.InsufficientAuthority(CrewRequestErrorCode.INSUFFICIENT_ACCEPT_AUTHORITY);
        }
    }

    public static void ensureRejectable(CrewMember me) {
        if (CrewMemberPolicy.isLowerThanManager(me)) {
            throw new CrewRequestBusinessException.InsufficientAuthority(CrewRequestErrorCode.INSUFFICIENT_REJECT_AUTHORITY);
        }
    }

    private static boolean mismatchCrew(CrewRequest request, Long crewId) {
        return !request.getCrew().getId().equals(crewId);
    }
}
