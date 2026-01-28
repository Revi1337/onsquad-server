package revi1337.onsquad.squad_member.domain;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import revi1337.onsquad.crew_member.domain.CrewMemberPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.error.SquadMemberBusinessException;
import revi1337.onsquad.squad_member.error.SquadMemberErrorCode;

@NoArgsConstructor(access = PRIVATE)
public final class SquadMemberPolicy {

    public static boolean isLeader(SquadMember me) {
        return me.isLeader();
    }

    public static boolean isNotLeader(SquadMember me) {
        return !isLeader(me);
    }

    public static boolean isMe(CrewMember me, SquadMember participant) {
        return me.getMember().getId().equals(participant.getMember().getId());
    }

    public static boolean isMe(SquadMember me, SquadMember participant) {
        return me.getMember().getId().equals(participant.getMember().getId());
    }

    public static boolean canKick(CrewMember me, SquadMember participant) {
        return !isMe(me, participant) && CrewMemberPolicy.isOwner(me) && isNotLeader(participant);
    }

    public static boolean canKick(SquadMember me, SquadMember participant) {
        return !isMe(me, participant) && isLeader(me) && isNotLeader(participant);
    }

    public static boolean canDelegateLeader(CrewMember me, SquadMember participant) {
        return !isMe(me, participant) && CrewMemberPolicy.isOwner(me) && isNotLeader(participant);
    }

    public static boolean canDelegateLeader(SquadMember me, SquadMember participant) {
        return !isMe(me, participant) && isLeader(me) && isNotLeader(participant);
    }

    public static void ensureKickable(SquadMember me) {
        if (isNotLeader(me)) {
            throw new SquadMemberBusinessException.InsufficientAuthority(SquadMemberErrorCode.INSUFFICIENT_KICK_MEMBER_AUTHORITY);
        }
    }

    public static void ensureLeaderDelegatable(SquadMember me) {
        if (isNotLeader(me)) {
            throw new SquadMemberBusinessException.InsufficientAuthority(SquadMemberErrorCode.INSUFFICIENT_DELEGATE_LEADER_AUTHORITY);
        }
    }

    public static void ensureNotSelfTargeting(Long currentMemberId, Long targetMemberId) {
        if (currentMemberId.equals(targetMemberId)) {
            throw new SquadMemberBusinessException.InvalidRequest(SquadMemberErrorCode.CANNOT_TARGET_SELF);
        }
    }
}
