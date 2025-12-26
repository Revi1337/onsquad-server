package revi1337.onsquad.squad_member.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_member.domain.result.MyParticipantSquadResult;
import revi1337.onsquad.squad_member.domain.result.SquadMemberResult;
import revi1337.onsquad.squad_member.error.SquadMemberBusinessException;
import revi1337.onsquad.squad_member.error.SquadMemberErrorCode;

@RequiredArgsConstructor
@Component
public class SquadMemberAccessor {

    private final SquadMemberRepository squadMemberRepository;

    public SquadMember getByMemberIdAndSquadId(Long memberId, Long squadId) {
        return squadMemberRepository.findBySquadIdAndMemberId(squadId, memberId)
                .orElseThrow(() -> new SquadMemberBusinessException.NotParticipant(SquadMemberErrorCode.NOT_PARTICIPANT));
    }

    public List<MyParticipantSquadResult> fetchParticipantSquads(Long memberId) {
        return squadMemberRepository.fetchParticipantSquads(memberId);
    }

    public List<SquadMemberResult> fetchParticipantsBySquadId(Long squadId) {
        return squadMemberRepository.fetchParticipantsBySquadId(squadId);
    }

    public boolean alreadyParticipant(Long memberId, Long squadId) {
        return squadMemberRepository.findBySquadIdAndMemberId(squadId, memberId).isPresent();
    }

    public void validateMemberNotInSquad(Long memberId, Long squadId) {
        if (squadMemberRepository.findBySquadIdAndMemberId(squadId, memberId).isPresent()) {
            throw new SquadMemberBusinessException.AlreadyParticipant(SquadMemberErrorCode.ALREADY_JOIN);
        }
    }
}
