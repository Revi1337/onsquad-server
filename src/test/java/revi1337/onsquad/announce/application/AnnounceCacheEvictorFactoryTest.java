package revi1337.onsquad.announce.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import revi1337.onsquad.announce.infrastructure.CaffeineAnnounceCacheEvictor;
import revi1337.onsquad.announce.infrastructure.RedisAnnounceCacheEvictor;
import revi1337.onsquad.common.ApplicationLayerTestSupport;

class AnnounceCacheEvictorFactoryTest extends ApplicationLayerTestSupport {

    @Autowired
    private CaffeineCacheManager caffeineCacheManager;

    @Autowired
    private RedisCacheManager redisCacheManager;

    @Autowired
    private AnnounceCacheEvictorFactory announceCacheEvictorFactory;

    @Test
    @DisplayName("CaffeineCacheManager가 제공되면 CaffeineAnnounceCacheEvictor를 반환해야 한다.")
    void findAvailableEvictor1() {
        AnnounceCacheEvictor availableEvictor = announceCacheEvictorFactory.findAvailableEvictor(caffeineCacheManager);

        assertThat(availableEvictor).isExactlyInstanceOf(CaffeineAnnounceCacheEvictor.class);
    }

    @Test
    @DisplayName("RedisCacheManager가 제공되면 RedisAnnounceCacheEvictor를 반환해야 한다.")
    void findAvailableEvictor2() {
        AnnounceCacheEvictor availableEvictor = announceCacheEvictorFactory.findAvailableEvictor(redisCacheManager);

        assertThat(availableEvictor).isExactlyInstanceOf(RedisAnnounceCacheEvictor.class);
    }

    @Test
    @DisplayName("지원하지 않는 CacheManager가 제공되면 UnsupportedOperationException이 발생한다.")
    void findAvailableEvictor3() {
        assertThatThrownBy(() -> announceCacheEvictorFactory
                .findAvailableEvictor(new SimpleCacheManager()))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
    }
}
