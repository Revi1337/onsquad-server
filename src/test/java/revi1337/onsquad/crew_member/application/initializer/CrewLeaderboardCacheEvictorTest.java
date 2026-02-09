package revi1337.onsquad.crew_member.application.initializer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheEvictor;

@Import(CrewLeaderboardCacheEvictor.class)
@ExtendWith(SpringExtension.class)
class CrewLeaderboardCacheEvictorTest {

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @SpyBean
    private CrewLeaderboardCacheEvictor crewLeaderboardCacheEvictor;

    @Test
    @DisplayName("애플리케이션 구동 완료 시 리더보드 관련 모든 캐시를 삭제한다")
    void onApplicationEvent() {
        try (MockedStatic<RedisCacheEvictor> evictorMock = mockStatic(RedisCacheEvictor.class)) {
            ApplicationReadyEvent event = new ApplicationReadyEvent(
                    new SpringApplication(), new String[]{}, applicationContext, null
            );

            applicationContext.publishEvent(event);

            verify(crewLeaderboardCacheEvictor).onApplicationEvent();
            evictorMock.verify(() -> RedisCacheEvictor.scanKeysAndUnlink(any(), anyList()), times(1));
        }
    }
}
