package revi1337.onsquad.crew_member.domain;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.error.CrewMemberBusinessException;
import revi1337.onsquad.crew_member.domain.error.CrewMemberErrorCode;

@NoArgsConstructor(access = PRIVATE)
public final class CrewMemberPolicy {

    public static boolean isOwner(CrewMember me) {
        return me.isOwner();
    }

    public static boolean isManager(CrewMember me) {
        return me.isManager();
    }

    public static boolean isGeneral(CrewMember me) {
        return me.isGeneral();
    }

    public static boolean isManagerOrHigher(CrewMember me) {
        return me.isManagerOrHigher();
    }

    public static boolean isLowerThanManager(CrewMember me) {
        return me.isLowerThanManager();
    }

    public static boolean isManagerOrLower(CrewMember me) {
        return me.isManagerOrLower();
    }

    public static boolean isNotOwner(CrewMember me) {
        return !isOwner(me);
    }

    public static boolean isMe(CrewMember me, CrewMember participant) {
        return me.getMember().getId().equals(participant.getMember().getId());
    }

    public static boolean canKick(CrewMember me, CrewMember participant) {
        if (isMe(me, participant)) {
            return false;
        }
        if (isManager(me) && isGeneral(participant)) {
            return true;
        }
        if (isOwner(me) && isManagerOrLower(participant)) {
            return true;
        }
        return false;
    }

    public static boolean canDelegateOwner(CrewMember me, CrewMember participant) {
        return !isMe(me, participant) && isOwner(me) && isNotOwner(participant);
    }

    public static void ensureKickable(CrewMember me, CrewMember participant) {
        if (mismatchCrew(me, participant)) {
            throw new CrewMemberBusinessException.MismatchReference(CrewMemberErrorCode.MISMATCH_CREW_REFERENCE);
        }
        if (isMe(me, participant)) {
            throw new CrewMemberBusinessException.InvalidRequest(CrewMemberErrorCode.CANNOT_TARGET_SELF);
        }
        if (isGeneral(me)) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_KICK_MEMBER_AUTHORITY);
        }
        if (isManager(me) && isManagerOrHigher(participant)) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.CANNOT_KICK_EQUAL_OR_HIGHER_ROLE_MEMBER);
        }
    }

    public static void ensureOwnerDelegatable(CrewMember me) {
        if (isNotOwner(me)) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_DELEGATE_OWNER_AUTHORITY);
        }
    }

    public static void ensureNotSelfTargeting(Long currentMemberId, Long targetMemberId) {
        if (currentMemberId.equals(targetMemberId)) {
            throw new CrewMemberBusinessException.InvalidRequest(CrewMemberErrorCode.CANNOT_TARGET_SELF);
        }
    }

    private static boolean mismatchCrew(CrewMember me, CrewMember participant) {
        return !me.getCrew().getId().equals(participant.getCrew().getId());
    }
}
