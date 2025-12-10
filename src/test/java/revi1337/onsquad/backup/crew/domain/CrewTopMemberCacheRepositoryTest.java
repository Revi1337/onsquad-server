package revi1337.onsquad.backup.crew.domain;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.constant.CacheConst.CREW_TOP_USERS;
import static revi1337.onsquad.common.fixture.CrewTopMemberFixture.CREW1_ANDONG_RANK2;
import static revi1337.onsquad.common.fixture.CrewTopMemberFixture.CREW1_REVI_RANK1;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.common.config.PersistenceLayerConfiguration;
import revi1337.onsquad.crew.domain.repository.top.CrewTopMemberCacheRepository;
import revi1337.onsquad.crew.domain.repository.top.CrewTopMemberJpaRepository;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({PersistenceLayerConfiguration.class, CrewTopMemberCacheRepository.class})
@ImportAutoConfiguration(CacheAutoConfiguration.class) // TODO 캐싱테스트가 더 생기면, 클래스로 분리하는것이 좋을듯?
class CrewTopMemberCacheRepositoryTest {

    @SpyBean
    private CrewTopMemberJpaRepository crewTopMemberJpaRepository;

    @Autowired
    private CrewTopMemberCacheRepository crewTopMemberCacheRepository;

    @Test
    @DisplayName("한번 캐싱된 CrewTopMember 는, 두번다시 호출되지 않는다.")
    void success() {
        Long DUMMY_CREW_ID = 1L;
        crewTopMemberJpaRepository.saveAll(List.of(CREW1_REVI_RANK1, CREW1_ANDONG_RANK2));
        crewTopMemberCacheRepository.findAllByCrewId(DUMMY_CREW_ID);
        reset(crewTopMemberJpaRepository);

        crewTopMemberCacheRepository.findAllByCrewId(DUMMY_CREW_ID);

        verify(crewTopMemberJpaRepository, times(0)).findAllByCrewId(DUMMY_CREW_ID);
    }

    @TestConfiguration
    @EnableCaching
    static class TestCacheConfig {

        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager(CREW_TOP_USERS);
        }
    }
}
