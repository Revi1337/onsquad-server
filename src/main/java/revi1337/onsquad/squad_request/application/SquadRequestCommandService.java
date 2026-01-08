package revi1337.onsquad.squad_request.application;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.member.application.MemberAccessor;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad.application.SquadAccessor;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_member.application.SquadMemberAccessor;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.entity.SquadMemberFactory;
import revi1337.onsquad.squad_request.domain.SquadRequestPolicy;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.domain.event.RequestAccepted;
import revi1337.onsquad.squad_request.domain.event.RequestAdded;
import revi1337.onsquad.squad_request.domain.event.RequestRejected;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class SquadRequestCommandService {

    private final MemberAccessor memberAccessor;
    private final SquadAccessor squadAccessor;
    private final SquadMemberAccessor squadMemberAccessor;
    private final SquadRequestAccessor squadRequestAccessor;
    private final SquadRequestRepository squadRequestRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void request(Long memberId, Long squadId) {
        squadMemberAccessor.validateMemberNotInSquad(memberId, squadId);
        if (squadRequestAccessor.isRequestAbsent(memberId, squadId)) {
            Squad squadRef = squadAccessor.getReferenceById(squadId);
            Member memberRef = memberAccessor.getReferenceById(memberId);
            SquadRequest request = squadRequestRepository.save(SquadRequest.of(squadRef, memberRef, LocalDateTime.now()));
            eventPublisher.publishEvent(new RequestAdded(squadId, memberId, request.getId()));
        }
    }

    public void acceptRequest(Long memberId, Long squadId, Long requestId) { // TODO 동시성 이슈 해결 필요.
        SquadRequest request = squadRequestAccessor.getById(requestId);
        SquadRequestPolicy.ensureMatchSquad(request, squadId);
        SquadMember me = squadMemberAccessor.getByMemberIdAndSquadId(memberId, squadId);
        SquadRequestPolicy.ensureAcceptable(me);
        Squad squad = request.getSquad();
        squad.addMembers(SquadMemberFactory.general(request.getMember(), LocalDateTime.now()));
        squadRequestRepository.deleteById(requestId);
        eventPublisher.publishEvent(new RequestAccepted(squadId, request.getRequesterId(), memberId));
    }

    public void rejectRequest(Long memberId, Long squadId, Long requestId) {
        SquadRequest request = squadRequestAccessor.getById(requestId);
        SquadRequestPolicy.ensureMatchSquad(request, squadId);
        SquadMember me = squadMemberAccessor.getByMemberIdAndSquadId(memberId, squadId);
        SquadRequestPolicy.ensureRejectable(me);
        squadRequestRepository.deleteById(requestId);
        eventPublisher.publishEvent(new RequestRejected(squadId, request.getRequesterId(), memberId));
    }

    public void cancelMyRequest(Long memberId, Long squadId) {
        squadRequestRepository.deleteBySquadIdMemberId(squadId, memberId);
    }
}
