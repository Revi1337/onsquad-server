package revi1337.onsquad.crew_participant.application;

import static revi1337.onsquad.crew.error.CrewErrorCode.ALREADY_JOIN;
import static revi1337.onsquad.crew.error.CrewErrorCode.INVALID_PUBLISHER;
import static revi1337.onsquad.crew.error.CrewErrorCode.OWNER_CANT_PARTICIPANT;
import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_OWNER;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.aspect.Throttling;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.crew_participant.application.dto.CrewParticipantRequestDto;
import revi1337.onsquad.crew_participant.application.dto.SimpleCrewParticipantRequestDto;
import revi1337.onsquad.crew_participant.domain.CrewParticipant;
import revi1337.onsquad.crew_participant.domain.CrewParticipantRepository;
import revi1337.onsquad.crew_participant.error.CrewParticipantErrorCode;
import revi1337.onsquad.crew_participant.error.exception.CrewParticipantBusinessException;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

@RequiredArgsConstructor
@Service
public class CrewParticipantService {

    private final CrewParticipantRepository crewParticipantRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final CrewRepository crewRepository;
    private final MemberRepository memberRepository;

    @Throttling(name = "throttle-crew-join", key = "'crew:' + #crewId + ':member:' + #memberId", during = 5)
    @Transactional
    public void requestInCrew(Long memberId, Long crewId) {
        Crew crew = crewRepository.getById(crewId);
        validateMemberIsNotCrewCreator(crew, memberId);
        crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId).ifPresentOrElse(
                crewMember -> {
                    throw new CrewBusinessException.AlreadyJoin(ALREADY_JOIN, crewId);
                },
                () -> {
                    Member referenceMember = memberRepository.getReferenceById(memberId);
                    crewParticipantRepository.upsertCrewParticipant(crew, referenceMember, LocalDateTime.now());
                }
        );
    }

    @Transactional
    public void acceptCrewRequest(Long memberId, Long crewId, Long reqMemberId) {
        Crew crew = crewRepository.getById(crewId);
        validateCrewCreator(memberId, crew);
        crewMemberRepository.findByCrewIdAndMemberId(crewId, reqMemberId).ifPresentOrElse(
                crewMember -> {
                    throw new CrewBusinessException.AlreadyJoin(ALREADY_JOIN, crewId);
                },
                () -> {
                    CrewParticipant cp = crewParticipantRepository.getByCrewIdAndMemberId(crew.getId(), reqMemberId);
                    CrewMember cm = CrewMember.forGeneral(crew, cp.getMember(), LocalDateTime.now());
                    crewMemberRepository.save(cm);
                    crewParticipantRepository.deleteById(cp.getId());
                }
        );
    }

    @Transactional
    public void rejectCrewRequest(Long memberId, Long crewId, Long requestId) {
        Crew crew = crewRepository.getById(crewId);
        validateCrewCreator(memberId, crew);
        CrewParticipant crewParticipant = crewParticipantRepository.getById(requestId);
        if (crewParticipant.isNotFrom(crewId)) {
            throw new CrewParticipantBusinessException.InvalidRequest(CrewParticipantErrorCode.INVALID_REQUEST);
        }

        crewParticipantRepository.deleteById(requestId);
    }

    @Transactional(readOnly = true)
    public List<SimpleCrewParticipantRequestDto> fetchCrewRequests(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(NOT_OWNER);
        }

        return crewParticipantRepository.fetchCrewRequests(crewId, pageable).stream()
                .map(SimpleCrewParticipantRequestDto::from)
                .toList();
    }

    @Transactional
    public void cancelCrewRequest(Long memberId, Long crewId) {
        CrewParticipant crewParticipant = crewParticipantRepository.getByCrewIdAndMemberId(crewId, memberId);
        crewParticipantRepository.deleteById(crewParticipant.getId());
    }

    @Transactional(readOnly = true)
    public List<CrewParticipantRequestDto> fetchAllCrewRequests(Long memberId) {
        return crewParticipantRepository.fetchAllCrewRequestsByMemberId(memberId).stream()
                .map(CrewParticipantRequestDto::from)
                .toList();
    }

    private void validateMemberIsNotCrewCreator(Crew crew, Long memberId) {
        if (crew.isCreatedBy(memberId)) {
            throw new CrewBusinessException.OwnerCantParticipant(OWNER_CANT_PARTICIPANT);
        }
    }

    private void validateCrewCreator(Long memberId, Crew crew) {
        if (!crew.isCreatedBy(memberId)) {
            throw new CrewBusinessException.InvalidPublisher(INVALID_PUBLISHER, crew.getId());
        }
    }
}
