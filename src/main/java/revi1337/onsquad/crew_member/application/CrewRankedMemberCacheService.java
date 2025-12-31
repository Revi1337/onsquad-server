package revi1337.onsquad.crew_member.application;

import static revi1337.onsquad.common.constant.CacheConst.CREW_RANK_MEMBERS;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;
import revi1337.onsquad.crew_member.domain.repository.top.CrewRankedMemberRepository;

@RequiredArgsConstructor
@Service
public class CrewRankedMemberCacheService {

    private final CrewRankedMemberRepository crewRankedMemberRepository;

    @Cacheable(cacheNames = CREW_RANK_MEMBERS, key = "'crew:' + #crewId")
    public List<CrewRankedMember> findAllByCrewId(Long crewId) {
        return crewRankedMemberRepository.findAllByCrewId(crewId);
    }
}
