package revi1337.onsquad.crew.domain;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.error.CrewBusinessException;
import revi1337.onsquad.crew.error.CrewErrorCode;
import revi1337.onsquad.crew_member.domain.CrewMemberPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;

@NoArgsConstructor(access = PRIVATE)
public final class CrewPolicy {

    public static boolean isLastMemberRemaining(Crew crew) {
        return crew.getCurrentSize() == 1;
    }

    public static boolean canModify(CrewMember me) {
        return CrewMemberPolicy.isOwner(me);
    }

    public static boolean canDelete(CrewMember me) {
        return CrewMemberPolicy.isOwner(me);
    }

    public static boolean canManage(CrewMember me) {
        return CrewMemberPolicy.isManagerOrHigher(me);
    }

    public static void ensureModifiable(Crew crew, Long memberId) {
        if (mismatchMember(crew, memberId)) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_UPDATE_AUTHORITY);
        }
    }

    public static void ensureDeletable(Crew crew, Long memberId) {
        if (mismatchMember(crew, memberId)) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_DELETE_AUTHORITY);
        }
    }

    public static void ensureImageModifiable(Crew crew, Long memberId) {
        if (mismatchMember(crew, memberId)) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_IMAGE_UPDATE_AUTHORITY);
        }
    }

    public static void ensureImageDeletable(Crew crew, Long memberId) {
        if (mismatchMember(crew, memberId)) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_IMAGE_DELETE_AUTHORITY);
        }
    }

    public static void ensureParticipantsReadable(CrewMember me) {
        if (CrewMemberPolicy.isLowerThanManager(me)) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_READ_PARTICIPANTS_AUTHORITY);
        }
    }

    public static void ensureManageable(CrewMember me) {
        if (CrewMemberPolicy.isLowerThanManager(me)) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_MANAGE_CREW_AUTHORITY);
        }
    }

    public static void ensureLeavable(Crew crew, CrewMember me) {
        if (mismatchCrew(crew, me)) {
            throw new CrewBusinessException.MismatchReference(CrewErrorCode.MISMATCH_CREW_REFERENCE);
        }
        if (CrewMemberPolicy.isOwner(me) && !isLastMemberRemaining(crew)) {
            throw new CrewBusinessException.InsufficientAuthority(CrewErrorCode.INSUFFICIENT_LEAVE_CREW_AUTHORITY);
        }
    }

    private static boolean mismatchCrew(Crew crew, CrewMember me) {
        return !crew.getId().equals(me.getCrew().getId());
    }

    private static boolean mismatchMember(Crew crew, Long memberId) {
        return !crew.getMember().getId().equals(memberId);
    }
}
