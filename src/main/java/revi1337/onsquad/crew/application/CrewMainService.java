package revi1337.onsquad.crew.application;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_OWNER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import revi1337.onsquad.announce.domain.dto.AnnounceDomainDto;
import revi1337.onsquad.announce.domain.repository.AnnounceCacheRepository;
import revi1337.onsquad.backup.crew.domain.entity.CrewTopMember;
import revi1337.onsquad.backup.crew.domain.repository.CrewTopMemberCacheRepository;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.crew.application.dto.CrewMainDto;
import revi1337.onsquad.crew.application.dto.CrewStatisticDto;
import revi1337.onsquad.crew.domain.dto.CrewDomainDto;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew.domain.repository.CrewStatisticCacheRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.squad.domain.dto.SquadDomainDto;
import revi1337.onsquad.squad.domain.repository.SquadRepository;

@RequiredArgsConstructor
@Service
public class CrewMainService {

    private final CrewMemberRepository crewMemberRepository;
    private final CrewTopMemberCacheRepository crewTopMemberCacheRepository;
    private final AnnounceCacheRepository announceCacheRepository;
    private final CrewRepository crewRepository;
    private final SquadRepository squadRepository;
    private final CrewStatisticCacheRepository crewStatisticRedisRepository;

    public CrewMainDto fetchMain(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        CrewDomainDto crewInfo = crewRepository.getCrewById(crewId);
        List<AnnounceDomainDto> announces = announceCacheRepository.fetchAllCacheInDefaultByCrewId(crewId);
        List<CrewTopMember> topMembers = crewTopMemberCacheRepository.findAllByCrewId(crewId);
        Page<SquadDomainDto> squads = squadRepository.fetchAllByCrewId(crewId, CategoryType.ALL, pageable);

        return CrewMainDto.from(crewMember.isOwner(), crewInfo, announces, topMembers, squads.getContent());
    }

    // TODO 캐시 정합성을 조금 더 올릴 방법을 생각해봐야 한다. 캐싱 된 이후에 추가된 인원 수, 추가된 신청 수, 추가된 스쿼드 수 를 파악(Redis)해 추가해주는 방향을 생각해야한다.
    public CrewStatisticDto calculateStatistic(Long memberId, Long crewId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(NOT_OWNER);
        }

        return CrewStatisticDto.from(crewStatisticRedisRepository.getStatisticById(crewId));
    }
}
