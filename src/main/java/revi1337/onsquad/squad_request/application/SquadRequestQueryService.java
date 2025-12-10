package revi1337.onsquad.squad_request.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.SquadMemberErrorCode;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;
import revi1337.onsquad.squad_request.application.dto.SquadRequestDto;
import revi1337.onsquad.squad_request.application.dto.SquadRequestWithSquadAndCrewDto;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadRequestQueryService { // TODO ApplicationService 와 DomainService 로 나눌 수 있을듯? 일단 그건 나중에

    private final SquadRequestRepository squadRequestRepository;
    private final SquadMemberRepository squadMemberRepository;

    public List<SquadRequestDto> fetchAllRequests(Long memberId, Long squadId, Pageable pageable) {
        SquadMember squadMember = validateMemberInSquadAndGet(memberId, squadId);
        if (squadMember.isNotLeader()) {
            throw new SquadMemberBusinessException.NotLeader(SquadMemberErrorCode.NOT_LEADER);
        }
        return squadRequestRepository.fetchAllBySquadId(squadId, pageable).stream()
                .map(SquadRequestDto::from)
                .toList();
    }

    public List<SquadRequestWithSquadAndCrewDto> fetchAllMyRequests(Long memberId) {
        return squadRequestRepository.findSquadParticipantRequestsByMemberId(memberId).stream()
                .map(SquadRequestWithSquadAndCrewDto::from)
                .toList();
    }

    private SquadMember validateMemberInSquadAndGet(Long memberId, Long squadId) { // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        return squadMemberRepository.findBySquadIdAndMemberId(squadId, memberId)
                .orElseThrow(() -> new SquadMemberBusinessException.NotParticipant(SquadMemberErrorCode.NOT_PARTICIPANT));
    }
}
