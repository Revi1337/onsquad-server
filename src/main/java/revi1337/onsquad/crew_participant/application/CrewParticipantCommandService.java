package revi1337.onsquad.crew_participant.application;

import static revi1337.onsquad.crew.error.CrewErrorCode.ALREADY_JOIN;
import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.LESS_THEN_MANAGER;
import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_OWNER;
import static revi1337.onsquad.crew_participant.error.CrewParticipantErrorCode.INVALID_REFERENCE;

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
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.crew_participant.domain.entity.CrewParticipant;
import revi1337.onsquad.crew_participant.domain.repository.CrewParticipantRepository;
import revi1337.onsquad.crew_participant.error.exception.CrewParticipantBusinessException;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

@RequiredArgsConstructor
@Transactional
@Service
public class CrewParticipantCommandService {

    private final CrewParticipantRepository crewParticipantRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;

    @Throttling(name = "throttle-crew-join", key = "'crew:' + #crewId + ':member:' + #memberId", during = 5)
    public void request(Long memberId, Long crewId) {
        Crew crew = crewRepository.getById(crewId);
        checkAlreadyJoin(memberId, crewId);

        Optional<CrewParticipant> crewParticipant = crewParticipantRepository.findByCrewIdAndMemberId(crewId, memberId);
        if (crewParticipant.isEmpty()) {
            Member member = memberRepository.getReferenceById(memberId);
            crewParticipantRepository.save(new CrewParticipant(crew, member, LocalDateTime.now()));
        }
    }

    public void acceptRequest(Long memberId, Long crewId, Long requestId) {
        Crew crew = crewRepository.getByIdForUpdate(crewId);
        checkMemberIsGreaterOrEqualThanManager(crewId, memberId);
        CrewParticipant crewParticipant = crewParticipantRepository.getById(requestId);
        checkAlreadyJoin(crewParticipant.getRequestMemberId(), crewId);
        checkCrewReference(crewId, crewParticipant);

        crew.addCrewMember(CrewMember.forGeneral(crew, crewParticipant.getMember()));
        crewParticipantRepository.deleteById(crewParticipant.getId());
    }

    public void rejectRequest(Long memberId, Long crewId, Long requestId) {
        checkMemberIsCrewOwner(crewId, memberId);
        checkCrewReference(crewId, crewParticipantRepository.getById(requestId));

        crewParticipantRepository.deleteById(requestId);
    }

    public void cancelMyRequest(Long memberId, Long crewId) {
        CrewParticipant crewParticipant = crewParticipantRepository.getByCrewIdAndMemberId(crewId, memberId);
        crewParticipantRepository.deleteById(crewParticipant.getId());
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

    private void checkCrewReference(Long crewId, CrewParticipant crewParticipant) {
        if (crewParticipant.isNotFrom(crewId)) {
            throw new CrewParticipantBusinessException.InvalidReference(INVALID_REFERENCE);
        }
    }
}
