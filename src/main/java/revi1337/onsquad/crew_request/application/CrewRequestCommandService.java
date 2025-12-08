package revi1337.onsquad.crew_request.application;

import static revi1337.onsquad.crew.error.CrewErrorCode.ALREADY_JOIN;
import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.LESS_THEN_MANAGER;
import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_OWNER;
import static revi1337.onsquad.crew_request.error.CrewRequestErrorCode.INVALID_REFERENCE;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.aspect.Throttling;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestRepository;
import revi1337.onsquad.crew_request.error.exception.CrewRequestBusinessException;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class CrewRequestCommandService {

    private final CrewRequestRepository crewRequestRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;

    @Throttling(name = "throttle-crew-req", key = "'crew:' + #crewId + ':member:' + #memberId", during = 5)
    public void request(Long memberId, Long crewId) {
        Crew crew = crewRepository.getById(crewId);
        checkAlreadyJoin(memberId, crewId);

        Optional<CrewRequest> crewParticipant = crewRequestRepository.findByCrewIdAndMemberId(crewId, memberId);
        if (crewParticipant.isEmpty()) {
            Member member = memberRepository.getReferenceById(memberId);
            crewRequestRepository.save(new CrewRequest(crew, member, LocalDateTime.now()));
        }
    }

    public void acceptRequest(Long memberId, Long crewId, Long requestId) {
        Crew crew = crewRepository.getByIdForUpdate(crewId);
        checkMemberIsGreaterOrEqualThanManager(crewId, memberId);
        CrewRequest crewRequest = crewRequestRepository.getById(requestId);
        checkAlreadyJoin(crewRequest.getRequestMemberId(), crewId);
        checkCrewReference(crewId, crewRequest);

        crew.addCrewMember(CrewMemberFactory.general(crew, crewRequest.getMember()));
        crewRequestRepository.deleteById(crewRequest.getId());
    }

    public void rejectRequest(Long memberId, Long crewId, Long requestId) {
        checkMemberIsCrewOwner(crewId, memberId);
        checkCrewReference(crewId, crewRequestRepository.getById(requestId));

        crewRequestRepository.deleteById(requestId);
    }

    public void cancelMyRequest(Long memberId, Long crewId) {
        CrewRequest crewRequest = crewRequestRepository.getByCrewIdAndMemberId(crewId, memberId);
        crewRequestRepository.deleteById(crewRequest.getId());
    }

    private void checkAlreadyJoin(Long memberId, Long crewId) {
        if (crewMemberRepository.existsByMemberIdAndCrewId(memberId, crewId)) {
            throw new CrewBusinessException.AlreadyJoin(ALREADY_JOIN, crewId);
        }
    }

    private void checkMemberIsGreaterOrEqualThanManager(Long crewId, Long memberId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        if (!(crewMember.isOwner() || crewMember.isManager())) {
            throw new CrewMemberBusinessException.LessThenManager(LESS_THEN_MANAGER);
        }
    }

    private void checkMemberIsCrewOwner(Long crewId, Long memberId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(NOT_OWNER);
        }
    }

    private void checkCrewReference(Long crewId, CrewRequest crewRequest) {
        if (crewRequest.isNotFrom(crewId)) {
            throw new CrewRequestBusinessException.InvalidReference(INVALID_REFERENCE);
        }
    }
}
