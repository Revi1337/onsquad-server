package revi1337.onsquad.squad_member.application;

import static revi1337.onsquad.squad_member.error.SquadMemberErrorCode.CANNOT_LEAVE_LEADER;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.squad.application.SquadAccessPolicy;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadMemberCommandService {

    private static final int LEADER_LIMIT_THRESHOLD = 2;

    private final SquadAccessPolicy squadAccessPolicy;
    private final SquadMemberAccessPolicy squadMemberAccessPolicy;
    private final SquadMemberRepository squadMemberRepository;

    public void leaveSquad(Long memberId, Long squadId) {  // TODO 여기 고쳐야 함.
        SquadMember squadMember = squadMemberAccessPolicy.ensureMemberInSquadAndGet(memberId, squadId);
        if (squadMember.isLeader() && squadMemberRepository.countBySquadId(squadId) >= LEADER_LIMIT_THRESHOLD) {
            throw new SquadMemberBusinessException.CannotLeaveLeader(CANNOT_LEAVE_LEADER);
        }
        squadMemberRepository.delete(squadMember);
        squadMemberRepository.flush();
        squadMember.leaveSquad();
    }

    public void delegateLeader(Long memberId, Long squadId, Long targetMemberId) {
        Squad squad = squadAccessPolicy.ensureSquadExistsAndGet(squadId);
        SquadMember currentLeader = squadMemberAccessPolicy.ensureMemberInSquadAndGet(memberId, squadId);
        squadMemberAccessPolicy.ensureNotSelfDelegation(memberId, targetMemberId);
        squadMemberAccessPolicy.ensureCanDelegateLeader(currentLeader);
        SquadMember nextLeader = squadMemberAccessPolicy.ensureMemberInSquadAndGet(targetMemberId, squadId);
        squad.delegateLeader(currentLeader, nextLeader);
    }
}
