package revi1337.onsquad.crew_request.application;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.aspect.Throttling;
import revi1337.onsquad.crew.application.CrewAccessor;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_request.domain.CrewRequestPolicy;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.domain.event.RequestAccepted;
import revi1337.onsquad.crew_request.domain.event.RequestAdded;
import revi1337.onsquad.crew_request.domain.event.RequestRejected;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestRepository;
import revi1337.onsquad.member.application.MemberAccessor;
import revi1337.onsquad.member.domain.entity.Member;

@Service
@Transactional
@RequiredArgsConstructor
public class CrewRequestCommandService {

    private final MemberAccessor memberAccessor;
    private final CrewAccessor crewAccessor;
    private final CrewMemberAccessor crewMemberAccessor;
    private final CrewRequestAccessor crewRequestAccessor;
    private final CrewRequestRepository crewRequestRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Throttling(name = "throttle-crew-req", key = "'crew:' + #crewId + ':member:' + #memberId", during = 5)
    public void request(Long memberId, Long crewId) {
        crewMemberAccessor.validateMemberNotInCrew(memberId, crewId);
        if (crewRequestAccessor.isRequestAbsent(memberId, crewId)) {
            Crew crewRef = crewAccessor.getReferenceById(crewId);
            Member memberRef = memberAccessor.getReferenceById(memberId);
            CrewRequest request = crewRequestRepository.save(CrewRequest.of(crewRef, memberRef, LocalDateTime.now()));
            eventPublisher.publishEvent(new RequestAdded(crewId, memberId, request.getId()));
        }
    }

    public void acceptRequest(Long memberId, Long crewId, Long requestId) { // TODO 굳이 지금처럼 비관적 락을 걸 필요가 있을까? (Optimistic VS PESSIMISTIC VS Atomic Update Query)
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        CrewRequestPolicy.ensureAcceptable(me);
        CrewRequest request = crewRequestAccessor.getById(requestId);
        Crew crew = request.getCrew();
        CrewRequestPolicy.ensureMatchCrew(request, crewId);
        crew.addCrewMember(CrewMemberFactory.general(crew, request.getMember(), LocalDateTime.now()));
        crewRequestRepository.deleteById(requestId);
        eventPublisher.publishEvent(new RequestAccepted(crewId, memberId, request.getRequesterId()));
    }

    public void rejectRequest(Long memberId, Long crewId, Long requestId) {
        CrewRequest request = crewRequestAccessor.getById(requestId);
        CrewRequestPolicy.ensureMatchCrew(request, crewId);
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        CrewRequestPolicy.ensureRejectable(me);
        crewRequestRepository.deleteById(requestId);
        eventPublisher.publishEvent(new RequestRejected(crewId, memberId, request.getRequesterId()));
    }

    public void cancelMyRequest(Long memberId, Long crewId) {
        crewRequestRepository.deleteByCrewIdAndMemberId(crewId, memberId);
    }
}
