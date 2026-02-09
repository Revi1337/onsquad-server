package revi1337.onsquad.crew_member.application.leaderboard;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.constant.CacheConst.CREW_RANK_MEMBERS;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankedMemberRepository;
import revi1337.onsquad.member.domain.entity.Member;

class CrewRankedMemberCacheServiceTest extends ApplicationLayerTestSupport {

    @SpyBean
    private CrewRankedMemberRepository crewRankedMemberRepository;

    @Autowired
    private CrewRankedMemberCacheService crewRankedMemberCacheService;

    @Test
    void success() {
        Long crewId = 1L;
        List<CrewRankedMember> rankedMembers = List.of(
                createCrewRankedMember(crewId, 1, 2, createRevi(1L), LocalDateTime.now()),
                createCrewRankedMember(crewId, 2, 1, createAndong(2L), LocalDateTime.now())
        );
        crewRankedMemberRepository.insertBatch(rankedMembers);
        crewRankedMemberCacheService.findAllByCrewId(crewId);

        crewRankedMemberCacheService.findAllByCrewId(crewId);

        verify(crewRankedMemberRepository, times(1)).findAllByCrewId(crewId);
    }

    public CrewRankedMember createCrewRankedMember(Long crewId, int rank, long score, Member member, LocalDateTime lastActivityTime) {
        return new CrewRankedMember(
                crewId,
                rank,
                score,
                member.getId(),
                member.getNickname().getValue(),
                member.getMbti().name(),
                lastActivityTime
        );
    }

    @TestConfiguration
    static class CacheTestConfig {

        @Bean("caffeineCacheManager")
        @Primary
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager(CREW_RANK_MEMBERS);
        }
    }
}
