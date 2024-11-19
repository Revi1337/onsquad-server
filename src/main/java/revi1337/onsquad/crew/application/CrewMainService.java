package revi1337.onsquad.crew.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import revi1337.onsquad.announce.domain.AnnounceRepository;
import revi1337.onsquad.announce.domain.dto.AnnounceInfoDomainDto;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.crew.application.dto.CrewMainDto;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew.domain.dto.CrewInfoDomainDto;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.domain.dto.Top5CrewMemberDomainDto;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.domain.dto.SquadInfoDomainDto;

@RequiredArgsConstructor
@Service
public class CrewMainService {

    private final CrewMemberRepository crewMemberRepository;
    private final AnnounceRepository announceRepository;
    private final CrewRepository crewRepository;
    private final SquadRepository squadRepository;

    public CrewMainDto fetchMain(Long memberId, Long crewId, CategoryType categoryType, Pageable pageable) {
        crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        CrewInfoDomainDto crewInfo = crewRepository.getCrewById(crewId);
        List<AnnounceInfoDomainDto> announces = announceRepository.findCachedLimitedAnnouncesByCrewId(crewId);
        List<Top5CrewMemberDomainDto> topMembers = crewMemberRepository.findTop5CrewMembers(crewId);
        Page<SquadInfoDomainDto> squads = squadRepository.findSquadsByCrewId(crewId, categoryType, pageable);

        return CrewMainDto.from(crewInfo, announces, topMembers, squads.getContent());
    }
}
