package revi1337.onsquad.squad_member.domain;

import lombok.RequiredArgsConstructor;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.error.SquadMemberBusinessException;
import revi1337.onsquad.squad_member.error.SquadMemberErrorCode;

@RequiredArgsConstructor
public class SquadMemberPolicy {

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
