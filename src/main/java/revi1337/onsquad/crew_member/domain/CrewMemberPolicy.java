package revi1337.onsquad.crew_member.domain;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.error.CrewMemberBusinessException;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.error.SquadMemberBusinessException;

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

    public static boolean canKick(CrewMember me, CrewMember participant) {
        return !isMe(me, participant) && me.isOwner() && participant.isNotOwner();
    }

    public static boolean canKick(CrewMember me, SquadMember participant) {
        return !isMe(me, participant) && me.isOwner() && participant.isNotLeader();
    }

    public static boolean canOwnerDelegate(CrewMember me, CrewMember participant) {
        return !isMe(me, participant) && me.isOwner() && participant.isNotOwner();
    }

    public static boolean canLeaderDelegate(CrewMember me, SquadMember participant) {
        return !isMe(me, participant) && me.isOwner() && participant.isNotLeader();
    }

    public static boolean canReadSquadParticipants(CrewMember crewMember) {
        return crewMember.isOwner();
    }

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

    public static void ensureNotSelfDelegation(Long currentMemberId, Long targetMemberId) {
        if (currentMemberId.equals(targetMemberId)) {
            throw new CrewMemberBusinessException.InvalidRequest(CrewMemberErrorCode.CANNOT_TARGET_SELF);
        }
    }

    public static void ensureNotSelfKickOut(Long currentMemberId, Long targetMemberId) {
        if (currentMemberId.equals(targetMemberId)) {
            throw new CrewMemberBusinessException.InvalidRequest(CrewMemberErrorCode.CANNOT_TARGET_SELF);
        }
    }

    public static void ensureCanDelegateOwner(CrewMember me) {
        if (me.isNotOwner()) {
            throw new SquadMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.INSUFFICIENT_DELEGATE_OWNER_AUTHORITY);
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
        if (me.isManager() && targetMember.isGreaterThenManager()) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.CANNOT_KICK_HIGHER_ROLE_MEMBER);
        }
    }
}
