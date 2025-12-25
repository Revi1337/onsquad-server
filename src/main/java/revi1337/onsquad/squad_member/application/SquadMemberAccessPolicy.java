package revi1337.onsquad.squad_member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.SquadMemberBusinessException;
import revi1337.onsquad.squad_member.error.SquadMemberErrorCode;

@RequiredArgsConstructor
@Component
public class SquadMemberAccessPolicy {

    private final SquadMemberRepository squadMemberRepository;

    public SquadMember ensureMemberInSquadAndGet(Long memberId, Long squadId) {
        return squadMemberRepository.findBySquadIdAndMemberId(squadId, memberId)
                .orElseThrow(() -> new SquadMemberBusinessException.NotParticipant(SquadMemberErrorCode.NOT_PARTICIPANT));
    }

    public void ensureMemberNotInSquad(Long memberId, Long squadId) {
        if (squadMemberRepository.findBySquadIdAndMemberId(squadId, memberId).isPresent()) {
            throw new SquadMemberBusinessException.AlreadyParticipant(SquadMemberErrorCode.ALREADY_JOIN);
        }
    }

    public void ensureNotSelfDelegation(Long currentMemberId, Long targetMemberId) {
        if (currentMemberId.equals(targetMemberId)) {
            throw new SquadMemberBusinessException.InvalidRequest(SquadMemberErrorCode.CANNOT_TARGET_SELF);
        }
    }

    public void ensureNotSelfKickOut(Long currentMemberId, Long targetMemberId) {
        if (currentMemberId.equals(targetMemberId)) {
            throw new SquadMemberBusinessException.InvalidRequest(SquadMemberErrorCode.CANNOT_TARGET_SELF);
        }
    }

    public void ensureCanDelegateLeader(SquadMember currentMember) {
        if (currentMember.isNotLeader()) {
            throw new SquadMemberBusinessException.InsufficientAuthority(SquadMemberErrorCode.INSUFFICIENT_DELEGATE_LEADER_AUTHORITY);
        }
    }

    public void ensureCanLeaveSquad(SquadMember currentMember) {
        if (currentMember.isLeader()) {
            throw new SquadMemberBusinessException.InsufficientAuthority(SquadMemberErrorCode.INSUFFICIENT_LEAVE_SQUAD_AUTHORITY);
        }
    }

    public void ensureCanKickOutMember(SquadMember currentMember) {
        if (currentMember.isNotLeader()) {
            throw new SquadMemberBusinessException.InsufficientAuthority(SquadMemberErrorCode.INSUFFICIENT_KICK_MEMBER_AUTHORITY);
        }
    }
}
