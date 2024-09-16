package revi1337.onsquad.announce.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.application.dto.AnnounceCreateDto;
import revi1337.onsquad.announce.application.dto.AnnounceInfoDto;
import revi1337.onsquad.announce.domain.Announce;
import revi1337.onsquad.announce.domain.AnnounceRepository;
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

    private static final Long DEFAULT_FETCH_SIZE = 4L;

    // TODO 권한 리팩토링 필요.
    public void createNewAnnounce(Long memberId, AnnounceCreateDto dto) {
        Crew crew = crewRepository.getById(dto.crewId());
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crew.getId(), memberId);
        checkMemberHasAuthority(crewMember.getRole());
        announceRepository.save(dto.toEntity(crew, crewMember));
    }

    @Transactional
    public void fixAnnounce(Long memberId, Long crewId, Long announceId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        checkMemberIsCrewOwner(crewMember);
        Announce announce = announceRepository.getByIdAndCrewId(crewId, announceId);
        announce.updateFixed(true, LocalDateTime.now());
        announceRepository.saveAndFlush(announce);
    }

    public AnnounceInfoDto findAnnounce(Long memberId, Long crewId, Long announceId) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);

        return AnnounceInfoDto.from(announceRepository.getAnnounceByCrewIdAndId(crewId, announceId, memberId));
    }

    public List<AnnounceInfoDto> findAnnounces(Long memberId, Long crewId) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);

        return announceRepository.findAnnouncesByCrewId(crewId, DEFAULT_FETCH_SIZE).stream()
                .map(AnnounceInfoDto::from)
                .toList();
    }

    // TODO 권한 리팩토링 필요.
    public AnnounceInfosWithAuthDto findMoreAnnounces(Long memberId, Long crewId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        boolean hasAuthority = crewMember.getRole() == OWNER || crewMember.getRole() == MANAGER;
        List<AnnounceInfoDto> announceInfos = announceRepository.findAnnouncesByCrewId(crewId, null).stream()
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
            throw new IllegalArgumentException("공지사항은 고정은 크루장만 이용할 수 있습니다.");
        }
    }
}
