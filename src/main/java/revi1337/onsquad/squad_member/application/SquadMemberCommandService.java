package revi1337.onsquad.squad_member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.squad.application.SquadAccessor;
import revi1337.onsquad.squad.application.SquadCommandService;
import revi1337.onsquad.squad.domain.SquadPolicy;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_member.domain.SquadMemberPolicy;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadMemberCommandService {

    private final SquadAccessor squadAccessor;
    private final SquadMemberAccessor squadMemberAccessor;
    private final SquadCommandService squadCommandService;
    private final SquadMemberRepository squadMemberRepository;

    public void delegateLeader(Long memberId, Long squadId, Long targetMemberId) {
        Squad squad = squadAccessor.getById(squadId);
        SquadMember currentLeader = squadMemberAccessor.getByMemberIdAndSquadId(memberId, squadId);
        SquadMemberPolicy.ensureCanDelegateLeader(currentLeader);
        SquadMemberPolicy.ensureNotSelfDelegation(memberId, targetMemberId);
        SquadMember nextLeader = squadMemberAccessor.getByMemberIdAndSquadId(targetMemberId, squadId);
        squad.delegateLeader(currentLeader, nextLeader);
    }

    public void leaveSquad(Long memberId, Long squadId) {
        Squad squad = squadAccessor.getByIdForUpdate(squadId);
        SquadMember currentMember = squadMemberAccessor.getByMemberIdAndSquadId(memberId, squadId);
        if (SquadPolicy.isLastMemberRemaining(squad)) {
            squadCommandService.deleteSquad(memberId, squadId);
            return;
        }
        SquadMemberPolicy.ensureCanLeaveSquad(currentMember);
        currentMember.leaveSquad();
        squadMemberRepository.delete(currentMember);
    }

    public void kickOutMember(Long memberId, Long squadId, Long targetMemberId) {
        Squad ignored = squadAccessor.getByIdForUpdate(squadId);
        SquadMember leader = squadMemberAccessor.getByMemberIdAndSquadId(memberId, squadId);
        SquadMemberPolicy.ensureCanKickOutMember(leader);
        SquadMemberPolicy.ensureNotSelfKickOut(memberId, targetMemberId);
        SquadMember targetMember = squadMemberAccessor.getByMemberIdAndSquadId(targetMemberId, squadId);
        targetMember.leaveSquad();
        squadMemberRepository.delete(targetMember);
    }
}
