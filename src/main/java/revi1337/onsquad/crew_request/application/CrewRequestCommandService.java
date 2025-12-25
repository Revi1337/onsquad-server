package revi1337.onsquad.crew_request.application;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.aspect.Throttling;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew.error.CrewBusinessException;
import revi1337.onsquad.crew.error.CrewErrorCode;
import revi1337.onsquad.crew_member.application.CrewMemberAccessPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.domain.event.RequestAccepted;
import revi1337.onsquad.crew_request.domain.event.RequestAdded;
import revi1337.onsquad.crew_request.domain.event.RequestRejected;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class CrewRequestCommandService {

    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberAccessPolicy crewMemberAccessPolicy;
    private final CrewRequestAccessPolicy crewRequestAccessPolicy;
    private final CrewRequestRepository crewRequestRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Throttling(name = "throttle-crew-req", key = "'crew:' + #crewId + ':member:' + #memberId", during = 5)
    public void request(Long memberId, Long crewId) {
        crewMemberAccessPolicy.ensureMemberNotInCrew(memberId, crewId);
        if (crewRequestAccessPolicy.isRequestAbsent(memberId, crewId)) {
            Crew crewRef = crewRepository.getReferenceById(crewId);
            Member memberRef = memberRepository.getReferenceById(memberId);
            CrewRequest request = crewRequestRepository.save(CrewRequest.of(crewRef, memberRef, LocalDateTime.now()));
            eventPublisher.publishEvent(new RequestAdded(crewId, memberId, request.getId()));
        }
    }

    public void acceptRequest(Long memberId, Long crewId, Long requestId) {
        Crew crew = crewRepository.findByIdForUpdate(crewId).orElseThrow(() -> new CrewBusinessException.NotFound(CrewErrorCode.NOT_FOUND));
        CrewRequest request = crewRequestAccessPolicy.ensureRequestExistsAndGet(requestId);
        crewRequestAccessPolicy.ensureMatchCrew(request, crewId);
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        crewRequestAccessPolicy.ensureAcceptable(crewMember);
        crew.addCrewMember(CrewMemberFactory.general(crew, request.getMember(), LocalDateTime.now()));
        crewRequestRepository.deleteById(requestId);
        eventPublisher.publishEvent(new RequestAccepted(crewId, memberId, request.getRequesterId()));
    }

    public void rejectRequest(Long memberId, Long crewId, Long requestId) {
        CrewRequest request = crewRequestAccessPolicy.ensureRequestExistsAndGet(requestId);
        crewRequestAccessPolicy.ensureMatchCrew(request, crewId);
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        crewRequestAccessPolicy.ensureRejectable(crewMember);
        crewRequestRepository.deleteById(requestId);
        eventPublisher.publishEvent(new RequestRejected(crewId, memberId, request.getRequesterId()));
    }

    public void cancelMyRequest(Long memberId, Long crewId) {
        crewRequestRepository.deleteByCrewIdAndMemberId(crewId, memberId);
    }
}
