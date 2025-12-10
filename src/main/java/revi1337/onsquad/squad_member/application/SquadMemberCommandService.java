package revi1337.onsquad.squad_member.application;

import static revi1337.onsquad.squad_member.error.SquadMemberErrorCode.CANNOT_LEAVE_LEADER;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.SquadMemberErrorCode;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;

@Transactional
@RequiredArgsConstructor
@Service
public class SquadMemberCommandService { // TODO ApplicationService 와 DomainService 로 나눌 수 있을듯? 일단 그건 나중에

    private static final int LEADER_LIMIT_THRESHOLD = 2;

    private final SquadMemberRepository squadMemberRepository;

    public void leaveSquad(Long memberId, Long squadId) {
        SquadMember squadMember = validateMemberInSquadAndGet(memberId, squadId);
        if (squadMember.isLeader() && squadMemberRepository.countBySquadId(squadId) >= LEADER_LIMIT_THRESHOLD) {
            throw new SquadMemberBusinessException.CannotLeaveLeader(CANNOT_LEAVE_LEADER);
        }
        squadMemberRepository.delete(squadMember);
        squadMemberRepository.flush();
        squadMember.leaveSquad();
    }

    private SquadMember validateMemberInSquadAndGet(Long memberId, Long squadId) { // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        return squadMemberRepository.findBySquadIdAndMemberId(squadId, memberId)
                .orElseThrow(() -> new SquadMemberBusinessException.NotParticipant(SquadMemberErrorCode.NOT_PARTICIPANT));
    }
}
