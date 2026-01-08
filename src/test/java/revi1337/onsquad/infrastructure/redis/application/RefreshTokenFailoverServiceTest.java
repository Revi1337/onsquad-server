package revi1337.onsquad.infrastructure.redis.application;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.TokenFixture.REFRESH_TOKEN_1;
import static revi1337.onsquad.common.fixture.TokenFixture.REFRESH_TOKEN_2;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import revi1337.onsquad.auth.verification.VerificationBackupProcessor;
import revi1337.onsquad.common.TestContainerSupport;
import revi1337.onsquad.common.aspect.RedisCacheAspect;
import revi1337.onsquad.common.aspect.ThrottlingAspect;
import revi1337.onsquad.infrastructure.aws.cloudfront.CloudFrontCacheInvalidator;
import revi1337.onsquad.infrastructure.recyclebin.RecycleBinLifeCycleManager;
import revi1337.onsquad.infrastructure.redis.RedisCacheCleaner;
import revi1337.onsquad.infrastructure.redis.RefreshTokenFailoverService;
import revi1337.onsquad.token.infrastructure.repository.ExpiringMapTokenRepository;

@MockBean({
        RecycleBinLifeCycleManager.class,
        VerificationBackupProcessor.class,
        ThrottlingAspect.class,
        RedisCacheAspect.class,
        CloudFrontCacheInvalidator.class
})
@SpringBootTest
class RefreshTokenFailoverServiceTest extends TestContainerSupport {

    @Autowired
    private ExpiringMapTokenRepository expiringMapTokenRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RefreshTokenFailoverService failoverService;

    @BeforeEach
    void setUp() {
        expiringMapTokenRepository.deleteAll();
        RedisCacheCleaner.cleanAll(stringRedisTemplate);
    }

    @Test
    @DisplayName("ExpiringMap에 저장된 Refresh토큰들을 Redis에 마이그레이션하는데 성공한다.")
    void migrateTokensToRedis() {
        expiringMapTokenRepository.save(REFRESH_TOKEN_1, 1L, Duration.ofSeconds(30));
        expiringMapTokenRepository.save(REFRESH_TOKEN_2, 2L, Duration.ofSeconds(40));
        String key1 = "onsquad:refresh:user:1";
        String key2 = "onsquad:refresh:user:2";

        failoverService.migrateTokensToRedis();

        assertThat(expiringMapTokenRepository.findAllState()).isEmpty();
        assertThat(stringRedisTemplate.opsForHash().get(key1, "value")).isEqualTo(REFRESH_TOKEN_1.value());
        assertThat(stringRedisTemplate.opsForHash().get(key2, "value")).isEqualTo(REFRESH_TOKEN_2.value());
        assertThat(stringRedisTemplate.getExpire(key1)).isLessThanOrEqualTo(30);
        assertThat(stringRedisTemplate.getExpire(key2)).isLessThanOrEqualTo(40);
    }
}
