package revi1337.onsquad.squad_member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.SquadMemberErrorCode;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;

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
}
