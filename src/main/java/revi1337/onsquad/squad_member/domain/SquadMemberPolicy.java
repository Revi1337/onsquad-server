package revi1337.onsquad.squad_member.domain;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import revi1337.onsquad.squad.domain.SquadPolicy;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.error.SquadMemberBusinessException;
import revi1337.onsquad.squad_member.error.SquadMemberErrorCode;

@NoArgsConstructor(access = PRIVATE)
public class SquadMemberPolicy {

    public static boolean isLeader(SquadMember me) {
        return me.isLeader();
    }

    public static boolean isNotLeader(SquadMember me) {
        return !isLeader(me);
    }

    public static boolean isMe(SquadMember me, SquadMember participant) {
        return me.getMember().matchId(participant.getMember().getId());
    }

    public static boolean canKick(SquadMember me, SquadMember participant) {
        return !isMe(me, participant) && isLeader(me) && isNotLeader(participant);
    }

    public static boolean DelegateLeader(SquadMember me, SquadMember participant) {
        return !isMe(me, participant) && isLeader(me) && isNotLeader(participant);
    }

    public static boolean canLeaveSquad(SquadMember me, Squad squad) {
        return SquadMemberPolicy.isNotLeader(me) || SquadPolicy.isLastMemberRemaining(squad);
    }

    public static boolean canDeleteSquad(SquadMember me) {
        return isLeader(me);
    }

    public static void ensureCanDelegateLeader(SquadMember me) {
        if (isNotLeader(me)) {
            throw new SquadMemberBusinessException.InsufficientAuthority(SquadMemberErrorCode.INSUFFICIENT_DELEGATE_LEADER_AUTHORITY);
        }
    }

    public static void ensureLeaderCannotLeaveWhileMembersRemain(SquadMember me) {
        if (isLeader(me)) {
            throw new SquadMemberBusinessException.InsufficientAuthority(SquadMemberErrorCode.INSUFFICIENT_LEAVE_SQUAD_AUTHORITY);
        }
    }

    public static void ensureCanKickOutMember(SquadMember me) {
        if (isNotLeader(me)) {
            throw new SquadMemberBusinessException.InsufficientAuthority(SquadMemberErrorCode.INSUFFICIENT_KICK_MEMBER_AUTHORITY);
        }
    }

    public static void ensureNotSelfTarget(Long currentMemberId, Long targetMemberId) {
        if (currentMemberId.equals(targetMemberId)) {
            throw new SquadMemberBusinessException.InvalidRequest(SquadMemberErrorCode.CANNOT_TARGET_SELF);
        }
    }
}
