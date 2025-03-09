package revi1337.onsquad.announce.application;

import static revi1337.onsquad.announce.error.AnnounceErrorCode.CANT_FIX;
import static revi1337.onsquad.announce.error.AnnounceErrorCode.CANT_MAKE;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.application.dto.AnnounceCreateDto;
import revi1337.onsquad.announce.application.dto.AnnounceInfoDto;
import revi1337.onsquad.announce.application.event.AnnounceCreateEvent;
import revi1337.onsquad.announce.application.event.AnnounceFixedEvent;
import revi1337.onsquad.announce.domain.Announce;
import revi1337.onsquad.announce.domain.AnnounceRepository;
import revi1337.onsquad.announce.domain.dto.AnnounceInfosWithAuthDto;
import revi1337.onsquad.announce.error.exception.AnnounceBusinessException;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;

@RequiredArgsConstructor
@Service
public class AnnounceService {

    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final AnnounceRepository announceRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void createNewAnnounce(Long memberId, Long crewId, AnnounceCreateDto dto) {
        Crew crew = crewRepository.getById(crewId);
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crew.getId(), memberId);
        checkMemberCanWriteAnnounce(crewMember);
        announceRepository.save(dto.toEntity(crew, crewMember));
        applicationEventPublisher.publishEvent(new AnnounceCreateEvent(crew.getId()));
    }

    @Transactional
    public void fixAnnounce(Long memberId, Long crewId, Long announceId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        checkMemberIsCrewOwner(crewMember);
        Announce announce = announceRepository.getByIdAndCrewId(crewId, announceId);
        announce.updateFixed(true, LocalDateTime.now());
        announceRepository.saveAndFlush(announce);
        applicationEventPublisher.publishEvent(new AnnounceFixedEvent(crewMember.getId()));
    }

    public AnnounceInfoDto findAnnounce(Long memberId, Long crewId, Long announceId) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);

        return AnnounceInfoDto.from(announceRepository.getCachedByCrewIdAndIdAndMemberId(crewId, announceId, memberId));
    }

    public List<AnnounceInfoDto> findAnnounces(Long memberId, Long crewId) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);

        return announceRepository.fetchCachedLimitedAnnouncesByCrewId(crewId).stream()
                .map(AnnounceInfoDto::from)
                .toList();
    }

    public AnnounceInfosWithAuthDto findMoreAnnounces(Long memberId, Long crewId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        List<AnnounceInfoDto> announceInfos = announceRepository.fetchAnnouncesByCrewId(crewId).stream()
                .map(AnnounceInfoDto::from)
                .toList();

        return new AnnounceInfosWithAuthDto(crewMember.isGreaterThenManager(), announceInfos);
    }

    private void checkMemberCanWriteAnnounce(CrewMember crewMember) {
        if (crewMember.isNotGreaterThenManager()) {
            throw new AnnounceBusinessException.CantMake(CANT_MAKE);
        }
    }

    private void checkMemberIsCrewOwner(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new AnnounceBusinessException.CantFix(CANT_FIX);
        }
    }
}
