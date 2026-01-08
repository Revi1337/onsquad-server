package revi1337.onsquad.crew_member.domain;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.error.CrewMemberBusinessException;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.squad_member.domain.SquadMemberPolicy;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

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

    public static boolean isNotOwner(CrewMember me) {
        return !isOwner(me);
    }

    public static boolean isMe(CrewMember me, CrewMember participant) {
        return me.getMember().matchId(participant.getMember().getId());
    }

    public static boolean isMe(CrewMember me, SquadMember participant) {
        return me.getMember().matchId(participant.getMember().getId());
    }

    public static boolean canKick(CrewMember me, SquadMember participant) {
        return !isMe(me, participant) && isOwner(me) && SquadMemberPolicy.isNotLeader(participant);
    }

    public static boolean canKick(CrewMember me, CrewMember participant) {
        if (isMe(me, participant)) {
            return false;
        }
        if (isManager(me) && isGeneral(participant)) {
            return true;
        }
        if (isOwner(me) && isLowerThanManager(participant)) {
            return true;
        }
        return false;
    }

    public static boolean canDelegateOwner(CrewMember me, CrewMember participant) {
        return !isMe(me, participant) && isOwner(me) && isNotOwner(participant);
    }

    public static boolean canLeaderDelegate(CrewMember me, SquadMember participant) {
        return !isMe(me, participant) && isOwner(me) && SquadMemberPolicy.isNotLeader(participant);
    }

    public static boolean canModifyCrew(CrewMember me) {
        return isOwner(me);
    }

    public static boolean canDeleteCrew(CrewMember me) {
        return isOwner(me);
    }

    public static boolean canMangeCrew(CrewMember me) {
        return isManagerOrHigher(me);
    }

    public static boolean canReadSquadParticipants(CrewMember me) {
        return isOwner(me);
    }

    public static boolean cannotReadSquadParticipants(CrewMember me) {
        return isNotOwner(me);
    }

    public static void ensureCanManagementCrew(CrewMember me) {
        if (isLowerThanManager(me)) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_MANAGE_CREW_AUTHORITY);
        }
    }

    public static void ensureReadParticipantsAccessible(CrewMember me) {
        if (isLowerThanManager(me)) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_READ_PARTICIPANTS_AUTHORITY);
        }
    }

    public static void ensureCanDelegateOwner(CrewMember me) {
        if (isNotOwner(me)) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_DELEGATE_OWNER_AUTHORITY);
        }
    }

    public static void ensureCanLeaveCrew(CrewMember me) {
        if (isOwner(me)) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_LEAVE_CREW_AUTHORITY);
        }
    }

    public static void ensureCanKickOutMember(CrewMember me, CrewMember targetMember) {
        if (isGeneral(me)) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_KICK_MEMBER_AUTHORITY);
        }
        if (isManager(me) && isManagerOrHigher(targetMember)) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.CANNOT_KICK_HIGHER_ROLE_MEMBER);
        }
    }

    public static void ensureNotSelfTarget(Long currentMemberId, Long targetMemberId) {
        if (currentMemberId.equals(targetMemberId)) {
            throw new CrewMemberBusinessException.InvalidRequest(CrewMemberErrorCode.CANNOT_TARGET_SELF);
        }
    }
}
