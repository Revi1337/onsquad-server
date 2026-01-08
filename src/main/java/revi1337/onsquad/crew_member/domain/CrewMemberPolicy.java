package revi1337.onsquad.crew_member.domain;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.error.CrewMemberBusinessException;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

@NoArgsConstructor(access = PRIVATE)
public final class CrewMemberPolicy {

    public static boolean isOwner(CrewMember me) {
        return me != null && me.isOwner();
    }

    public static boolean isMe(CrewMember me, CrewMember participant) {
        return me.getMember().matchId(participant.getMember().getId());
    }

    public static boolean isMe(CrewMember me, SquadMember participant) {
        return me.getMember().matchId(participant.getMember().getId());
    }

    public static boolean canKick(CrewMember me, SquadMember participant) {
        return !isMe(me, participant) && me.isOwner() && participant.isNotLeader();
    }

    public static boolean canKick(CrewMember me, CrewMember participant) {
        if (isMe(me, participant)) {
            return false;
        }
        if (me.isManager() && participant.isGeneral()) {
            return true;
        }
        if (me.isOwner() && participant.isLowerThanManager()) {
            return true;
        }
        return false;
    }

    public static boolean canDelegateOwner(CrewMember me, CrewMember participant) {
        return !isMe(me, participant) && me.isOwner() && participant.isNotOwner();
    }

    public static boolean canLeaderDelegate(CrewMember me, SquadMember participant) {
        return !isMe(me, participant) && me.isOwner() && participant.isNotLeader();
    }

    public static boolean canModifyCrew(CrewMember me) {
        return me.isOwner();
    }

    public static boolean canDeleteCrew(CrewMember me) {
        return me.isOwner();
    }

    public static boolean canMangeCrew(CrewMember me) {
        return me.isManagerOrHigher();
    }

    public static boolean canReadSquadParticipants(CrewMember crewMember) {
        return crewMember.isOwner();
    }

    public static boolean cannotReadSquadParticipants(CrewMember crewMember) {
        return crewMember.isNotOwner();
    }

    public static void ensureCanManagementCrew(CrewMember me) {
        if (me.isLowerThanManager()) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_MANAGE_CREW_AUTHORITY);
        }
    }

    public static void ensureReadParticipantsAccessible(CrewMember crewMember) {
        if (crewMember.isLowerThanManager()) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_READ_PARTICIPANTS_AUTHORITY);
        }
    }

    public static void ensureCanDelegateOwner(CrewMember me) {
        if (me.isNotOwner()) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_DELEGATE_OWNER_AUTHORITY);
        }
    }

    public static void ensureCanLeaveCrew(CrewMember me) {
        if (me.isOwner()) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_LEAVE_CREW_AUTHORITY);
        }
    }

    public static void ensureCanKickOutMember(CrewMember me, CrewMember targetMember) {
        if (me.isGeneral()) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_KICK_MEMBER_AUTHORITY);
        }
        if (me.isManager() && targetMember.isManagerOrHigher()) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.CANNOT_KICK_HIGHER_ROLE_MEMBER);
        }
    }

    public static void ensureNotSelfTarget(Long currentMemberId, Long targetMemberId) {
        if (currentMemberId.equals(targetMemberId)) {
            throw new CrewMemberBusinessException.InvalidRequest(CrewMemberErrorCode.CANNOT_TARGET_SELF);
        }
    }
}
