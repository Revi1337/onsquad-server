package revi1337.onsquad.squad_request.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.squad_member.application.SquadMemberAccessPolicy;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_request.application.response.SquadRequestResponse;
import revi1337.onsquad.squad_request.application.response.SquadRequestWithSquadAndCrewResponse;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadRequestQueryService {

    private final SquadMemberAccessPolicy squadMemberAccessPolicy;
    private final SquadRequestAccessPolicy squadRequestAccessPolicy;
    private final SquadRequestRepository squadRequestRepository;

    public List<SquadRequestResponse> fetchAllRequests(Long memberId, Long squadId, Pageable pageable) {
        SquadMember squadMember = squadMemberAccessPolicy.ensureMemberInSquadAndGet(memberId, squadId);
        squadRequestAccessPolicy.ensureRequestListAccessible(squadMember);

        return squadRequestRepository.fetchAllBySquadId(squadId, pageable).stream()
                .map(SquadRequestResponse::from)
                .toList();
    }

    public List<SquadRequestWithSquadAndCrewResponse> fetchAllMyRequests(Long memberId) {
        return squadRequestRepository.findSquadParticipantRequestsByMemberId(memberId).stream()
                .map(SquadRequestWithSquadAndCrewResponse::from)
                .toList();
    }
}
