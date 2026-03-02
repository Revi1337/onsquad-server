package revi1337.onsquad.squad_member.application;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.error.SquadMemberBusinessException;
import revi1337.onsquad.squad_member.domain.error.SquadMemberErrorCode;
import revi1337.onsquad.squad_member.domain.model.MyParticipantSquad;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;

@Component
@RequiredArgsConstructor
public class SquadMemberAccessor {

    private final SquadMemberRepository squadMemberRepository;

    public Optional<SquadMember> findByMemberIdAndSquadId(Long memberId, Long squadId) {
        return squadMemberRepository.findBySquadIdAndMemberId(squadId, memberId);
    }

    public SquadMember getByMemberIdAndSquadId(Long memberId, Long squadId) {
        return squadMemberRepository.findBySquadIdAndMemberId(squadId, memberId)
                .orElseThrow(() -> new SquadMemberBusinessException.NotParticipant(SquadMemberErrorCode.NOT_PARTICIPANT));
    }

    public List<MyParticipantSquad> fetchParticipantSquads(Long memberId) {
        return squadMemberRepository.fetchParticipantSquads(memberId);
    }

    public Page<SquadMember> fetchParticipantsBySquadId(Long squadId, Pageable pageable) {
        return squadMemberRepository.fetchParticipantsBySquadId(squadId, pageable);
    }

    public void validateMemberNotInSquad(Long memberId, Long squadId) {
        if (squadMemberRepository.findBySquadIdAndMemberId(squadId, memberId).isPresent()) {
            throw new SquadMemberBusinessException.AlreadyParticipant(SquadMemberErrorCode.ALREADY_JOIN);
        }
    }
}
