package revi1337.onsquad.announce.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.application.dto.AnnounceCreateDto;
import revi1337.onsquad.announce.application.dto.AnnounceInfoDto;
import revi1337.onsquad.announce.application.event.AnnounceCacheEvent;
import revi1337.onsquad.announce.application.event.AnnounceCreateEvent;
import revi1337.onsquad.announce.application.event.AnnounceFixedEvent;
import revi1337.onsquad.announce.domain.*;
import revi1337.onsquad.announce.domain.dto.AnnounceInfosWithAuthDto;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.domain.vo.CrewRole;

import java.time.LocalDateTime;
import java.util.List;

import static revi1337.onsquad.crew_member.domain.vo.CrewRole.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AnnounceService {

    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final AnnounceRepository announceRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CacheManager announceCacheManager;

    // TODO 권한 리팩토링 필요.
    public void createNewAnnounce(Long memberId, AnnounceCreateDto dto) {
        Crew crew = crewRepository.getById(dto.crewId());
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crew.getId(), memberId);
        checkMemberHasAuthority(crewMember.getRole());
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

        return AnnounceInfoDto.from(announceRepository.getAnnounceByCrewIdAndId(crewId, announceId, memberId));
    }

    // TODO 캐시를 AOP 로 빼던가 리팩토링 필요.
    public List<AnnounceInfoDto> findAnnounces(Long memberId, Long crewId) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        List<AnnounceInfoDto> cachedCrewAnnounceInfos = announceCacheManager.getCachedCrewAnnounceInfosById(crewId);
        if (!cachedCrewAnnounceInfos.isEmpty()) {
            return cachedCrewAnnounceInfos;
        }

        List<AnnounceInfoDto> announceInfos = getLimitedAnnouncesByCrewId(crewId);
        applicationEventPublisher.publishEvent(new AnnounceCacheEvent(crewId, announceInfos));

        return announceInfos;
    }

    // TODO 권한 리팩토링 필요.
    public AnnounceInfosWithAuthDto findMoreAnnounces(Long memberId, Long crewId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        boolean hasAuthority = crewMember.getRole() == OWNER || crewMember.getRole() == MANAGER;
        List<AnnounceInfoDto> announceInfos = announceRepository.findAnnouncesByCrewId(crewId).stream()
                .map(AnnounceInfoDto::from)
                .toList();

        return new AnnounceInfosWithAuthDto(hasAuthority, announceInfos);
    }

    // TODO 권한 Auth 패키지에서 예외처리 필요.
    private void checkMemberHasAuthority(CrewRole role) {
        if (role == GENERAL) {
            throw new IllegalArgumentException("공지사항은 크루 작성자나 매니저만 작성할 수 있습니다");
        }
    }

    // TODO 권한 Auth 패키지에서 예외처리 필요.
    private void checkMemberIsCrewOwner(CrewMember crewMember) {
        if (crewMember.getRole() != OWNER) {
            throw new IllegalArgumentException("공지사항 고정은 크루장만 이용할 수 있습니다.");
        }
    }

    private List<AnnounceInfoDto> getLimitedAnnouncesByCrewId(Long crewId) {
        return announceRepository.findLimitedAnnouncesByCrewId(crewId).stream()
                .map(AnnounceInfoDto::from)
                .toList();
    }
}
