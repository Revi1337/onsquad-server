package revi1337.onsquad.squad_member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.squad.application.SquadAccessPolicy;
import revi1337.onsquad.squad.application.SquadCommandService;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad.error.SquadBusinessException;
import revi1337.onsquad.squad.error.SquadErrorCode;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadMemberCommandService {

    private final SquadAccessPolicy squadAccessPolicy;
    private final SquadMemberAccessPolicy squadMemberAccessPolicy;
    private final SquadRepository squadRepository;
    private final SquadMemberRepository squadMemberRepository;
    private final SquadCommandService squadCommandService;

    public void delegateLeader(Long memberId, Long squadId, Long targetMemberId) {
        Squad squad = squadAccessPolicy.ensureSquadExistsAndGet(squadId);
        SquadMember currentLeader = squadMemberAccessPolicy.ensureMemberInSquadAndGet(memberId, squadId);
        squadMemberAccessPolicy.ensureCanDelegateLeader(currentLeader);
        squadMemberAccessPolicy.ensureNotSelfDelegation(memberId, targetMemberId);
        SquadMember nextLeader = squadMemberAccessPolicy.ensureMemberInSquadAndGet(targetMemberId, squadId);
        squad.delegateLeader(currentLeader, nextLeader);
    }

    public void leaveSquad(Long memberId, Long squadId) {
        Squad squad = squadRepository.findByIdForUpdate(squadId).orElseThrow(() -> new SquadBusinessException.NotFound(SquadErrorCode.NOT_FOUND));
        SquadMember currentMember = squadMemberAccessPolicy.ensureMemberInSquadAndGet(memberId, squadId);
        if (squadAccessPolicy.isLastMemberRemaining(squad)) {
            squadCommandService.deleteSquad(memberId, squadId);
            return;
        }
        squadMemberAccessPolicy.ensureCanLeaveSquad(currentMember);
        currentMember.leaveSquad();
        squadMemberRepository.delete(currentMember);
    }

    public void kickOutMember(Long memberId, Long squadId, Long targetMemberId) {
        squadRepository.findByIdForUpdate(squadId).orElseThrow(() -> new SquadBusinessException.NotFound(SquadErrorCode.NOT_FOUND));
        SquadMember leader = squadMemberAccessPolicy.ensureMemberInSquadAndGet(memberId, squadId);
        squadMemberAccessPolicy.ensureCanKickOutMember(leader);
        squadMemberAccessPolicy.ensureNotSelfKickOut(memberId, targetMemberId);
        SquadMember targetMember = squadMemberAccessPolicy.ensureMemberInSquadAndGet(targetMemberId, squadId);
        targetMember.leaveSquad();
        squadMemberRepository.delete(targetMember);
    }
}
