package revi1337.onsquad.squad_member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.squad.application.SquadAccessor;
import revi1337.onsquad.squad.application.SquadContextHandler;
import revi1337.onsquad.squad.domain.SquadPolicy;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_member.domain.SquadMemberPolicy;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class SquadMemberCommandService {

    private final SquadAccessor squadAccessor;
    private final SquadMemberAccessor squadMemberAccessor;
    private final SquadContextHandler squadContextHandler;
    private final SquadMemberRepository squadMemberRepository;

    public void delegateLeader(Long memberId, Long squadId, Long targetMemberId) { // TODO 동시성 이슈 해결 필요.
        Squad squad = squadAccessor.getById(squadId);
        SquadMember me = squadMemberAccessor.getByMemberIdAndSquadId(memberId, squadId);
        SquadMemberPolicy.ensureNotSelfTargeting(memberId, targetMemberId);
        SquadMemberPolicy.ensureLeaderDelegatable(me);
        SquadMember nextLeader = squadMemberAccessor.getByMemberIdAndSquadId(targetMemberId, squadId);
        squad.delegateLeader(me, nextLeader);
    }

    public void leaveSquad(Long memberId, Long squadId) { // TODO 굳이 비관적 락을?
        Squad squad = squadAccessor.getByIdForUpdate(squadId);
        SquadMember me = squadMemberAccessor.getByMemberIdAndSquadId(memberId, squadId);
        if (SquadPolicy.isLastMemberRemaining(squad)) {
            squadContextHandler.disposeContext(squadId);
            return;
        }
        SquadPolicy.ensureLeavable(squad, me);
        me.leaveSquad();
        squadMemberRepository.delete(me);
    }

    public void kickOutMember(Long memberId, Long squadId, Long targetMemberId) { // TODO 굳이 비관적 락을?
        Squad ignored = squadAccessor.getByIdForUpdate(squadId);
        SquadMember me = squadMemberAccessor.getByMemberIdAndSquadId(memberId, squadId);
        SquadMemberPolicy.ensureNotSelfTargeting(memberId, targetMemberId);
        SquadMemberPolicy.ensureKickable(me);
        SquadMember targetMember = squadMemberAccessor.getByMemberIdAndSquadId(targetMemberId, squadId);
        targetMember.leaveSquad();
        squadMemberRepository.delete(targetMember);
    }
}
