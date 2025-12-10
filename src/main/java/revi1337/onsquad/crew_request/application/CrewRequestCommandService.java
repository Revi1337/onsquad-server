package revi1337.onsquad.crew_request.application;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.aspect.Throttling;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew.error.CrewErrorCode;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestRepository;
import revi1337.onsquad.crew_request.error.CrewRequestErrorCode;
import revi1337.onsquad.crew_request.error.exception.CrewRequestBusinessException;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class CrewRequestCommandService {

    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewRequestRepository crewRequestRepository;

    @Throttling(name = "throttle-crew-req", key = "'crew:' + #crewId + ':member:' + #memberId", during = 5)
    public void request(Long memberId, Long crewId) {
        validateMemberNotInCrew(memberId, crewId);
        if (crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId).isEmpty()) {
            Crew crewRef = crewRepository.getReferenceById(crewId);
            Member memberRef = memberRepository.getReferenceById(memberId);
            crewRequestRepository.save(CrewRequest.of(crewRef, memberRef, LocalDateTime.now()));
        }
    }

    public void acceptRequest(Long memberId, Long crewId, Long requestId) {
        Optional<Crew> optionalCrew = crewRepository.findByIdForUpdate(crewId);
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId);
        if (optionalCrew.isEmpty()) {
            throw new CrewBusinessException.NotFound(CrewErrorCode.NOT_FOUND);
        }
        if (!(crewMember.isOwner() || crewMember.isManager())) {
            throw new CrewMemberBusinessException.InsufficientAuthority(CrewMemberErrorCode.LESS_THAN_MANAGER);
        }
        CrewRequest request = validateCrewRequestExistsAndGet(requestId);
        if (request.mismatchCrewId(crewId)) {
            throw new CrewRequestBusinessException.InvalidReference(CrewRequestErrorCode.MISMATCH_CREW_REFERENCE);
        }
        Crew crew = optionalCrew.get();
        crew.addCrewMember(CrewMemberFactory.general(crew, request.getMember(), LocalDateTime.now()));
        crewRequestRepository.deleteById(requestId);
    }

    public void rejectRequest(Long memberId, Long crewId, Long requestId) {
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId);
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(CrewMemberErrorCode.NOT_OWNER);
        }
        CrewRequest request = validateCrewRequestExistsAndGet(requestId);
        if (request.mismatchCrewId(crewId)) {
            throw new CrewRequestBusinessException.MismatchReference(CrewRequestErrorCode.MISMATCH_CREW_REFERENCE);
        }
        crewRequestRepository.deleteById(requestId);
    }

    public void cancelMyRequest(Long memberId, Long crewId) {
        crewRequestRepository.deleteByCrewIdAndMemberId(crewId, memberId);
    }

    private void validateMemberNotInCrew(Long memberId, Long crewId) { // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        if (crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId).isPresent()) {
            throw new CrewMemberBusinessException.AlreadyParticipant(CrewMemberErrorCode.ALREADY_JOIN);
        }
    }

    private CrewMember validateMemberInCrewAndGet(Long memberId, Long crewId) {
        return crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT));
    }

    private CrewRequest validateCrewRequestExistsAndGet(Long requestId) {
        return crewRequestRepository.findById(requestId)
                .orElseThrow(() -> new CrewRequestBusinessException.NotFound(CrewRequestErrorCode.NOT_FOUND));
    }
}
