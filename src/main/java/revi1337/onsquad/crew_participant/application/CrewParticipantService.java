package revi1337.onsquad.crew_participant.application;

import static revi1337.onsquad.crew.error.CrewErrorCode.ALREADY_JOIN;
import static revi1337.onsquad.crew.error.CrewErrorCode.OWNER_CANT_PARTICIPANT;
import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_OWNER;
import static revi1337.onsquad.crew_participant.error.CrewParticipantErrorCode.INVALID_REFERENCE;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
import revi1337.onsquad.crew_participant.application.dto.CrewRequestWithCrewDto;
import revi1337.onsquad.crew_participant.application.dto.CrewRequestWithMemberDto;
import revi1337.onsquad.crew_participant.domain.CrewParticipant;
import revi1337.onsquad.crew_participant.domain.CrewParticipantRepository;
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
        Optional<CrewMember> optionalCrewMember = crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId);
        if (optionalCrewMember.isPresent()) {
            CrewMember crewMember = optionalCrewMember.get();
            if (crewMember.isOwner()) {
                throw new CrewBusinessException.OwnerCantParticipant(OWNER_CANT_PARTICIPANT);
            }
            throw new CrewBusinessException.AlreadyJoin(ALREADY_JOIN, crewId);
        }

        Member member = memberRepository.getById(memberId);
        crewParticipantRepository.upsertCrewParticipant(crew, member, LocalDateTime.now());
    }

    @Transactional
    public void acceptCrewRequest(Long memberId, Long crewId, Long requestId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        checkMemberIsCrewOwner(crewMember);

        CrewParticipant crewParticipant = crewParticipantRepository.getById(requestId);
        checkCrewReference(crewId, crewParticipant);

        if (crewMemberRepository.existsByMemberIdAndCrewId(crewParticipant.getRequestMemberId(), crewId)) {
            throw new CrewBusinessException.AlreadyJoin(ALREADY_JOIN, crewId);
        }

        CrewMember newCrewMember = CrewMember.forGeneral(crewParticipant.getCrew(), crewParticipant.getMember());
        crewMemberRepository.save(newCrewMember); // TODO DB 크루 총 인원 수 Field 가 추가된다면, 수정 필요.
        crewParticipantRepository.deleteById(crewParticipant.getId());
    }

    @Transactional
    public void rejectCrewRequest(Long memberId, Long crewId, Long requestId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        checkMemberIsCrewOwner(crewMember);

        CrewParticipant crewParticipant = crewParticipantRepository.getById(requestId);
        checkCrewReference(crewId, crewParticipant);

        crewParticipantRepository.deleteById(requestId);
    }

    @Transactional(readOnly = true)
    public List<CrewRequestWithMemberDto> fetchCrewRequests(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        checkMemberIsCrewOwner(crewMember);

        return crewParticipantRepository.fetchCrewRequests(crewId, pageable).stream()
                .map(CrewRequestWithMemberDto::from)
                .toList();
    }

    @Transactional
    public void cancelCrewRequest(Long memberId, Long crewId) {
        CrewParticipant crewParticipant = crewParticipantRepository.getByCrewIdAndMemberId(crewId, memberId);
        crewParticipantRepository.deleteById(crewParticipant.getId());
    }

    @Transactional(readOnly = true)
    public List<CrewRequestWithCrewDto> fetchAllCrewRequests(Long memberId) {
        return crewParticipantRepository.fetchAllWithSimpleCrewByMemberId(memberId).stream()
                .map(CrewRequestWithCrewDto::from)
                .toList();
    }

    private void checkMemberIsCrewOwner(CrewMember crewMember) {
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
