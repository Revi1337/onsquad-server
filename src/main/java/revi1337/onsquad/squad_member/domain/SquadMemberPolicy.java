package revi1337.onsquad.squad_member.domain;

import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.error.SquadMemberBusinessException;
import revi1337.onsquad.squad_member.error.SquadMemberErrorCode;

public class SquadMemberPolicy {

    public static boolean isMe(SquadMember me, SquadMember participant) {
        return me.getMember().matchId(participant.getMember().getId());
    }

    public static boolean canKick(SquadMember me, SquadMember participant) {
        return !isMe(me, participant) && me.isLeader() && participant.isNotLeader();
    }

    public static boolean canLeaderDelegate(SquadMember me, SquadMember participant) {
        return !isMe(me, participant) && me.isLeader() && participant.isNotLeader();
    }

    public static void ensureNotSelfDelegation(Long currentMemberId, Long targetMemberId) {
        if (currentMemberId.equals(targetMemberId)) {
            throw new SquadMemberBusinessException.InvalidRequest(SquadMemberErrorCode.CANNOT_TARGET_SELF);
        }
    }

    public static void ensureNotSelfKickOut(Long currentMemberId, Long targetMemberId) {
        if (currentMemberId.equals(targetMemberId)) {
            throw new SquadMemberBusinessException.InvalidRequest(SquadMemberErrorCode.CANNOT_TARGET_SELF);
        }
    }

    public static void ensureCanDelegateLeader(SquadMember currentMember) {
        if (currentMember.isNotLeader()) {
            throw new SquadMemberBusinessException.InsufficientAuthority(SquadMemberErrorCode.INSUFFICIENT_DELEGATE_LEADER_AUTHORITY);
        }
    }

    public static void ensureCanLeaveSquad(SquadMember currentMember) {
        if (currentMember.isLeader()) {
            throw new SquadMemberBusinessException.InsufficientAuthority(SquadMemberErrorCode.INSUFFICIENT_LEAVE_SQUAD_AUTHORITY);
        }
    }

    public static void ensureCanKickOutMember(SquadMember currentMember) {
        if (currentMember.isNotLeader()) {
            throw new SquadMemberBusinessException.InsufficientAuthority(SquadMemberErrorCode.INSUFFICIENT_KICK_MEMBER_AUTHORITY);
        }
    }
}
