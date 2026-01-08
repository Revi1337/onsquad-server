package revi1337.onsquad.token.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.dockerjava.api.DockerClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.DockerClientFactory;
import revi1337.onsquad.auth.verification.VerificationBackupProcessor;
import revi1337.onsquad.common.TestContainerSupport;
import revi1337.onsquad.common.aspect.RedisCacheAspect;
import revi1337.onsquad.common.aspect.ThrottlingAspect;
import revi1337.onsquad.common.fixture.MemberFixture;
import revi1337.onsquad.infrastructure.aws.cloudfront.CloudFrontCacheInvalidator;
import revi1337.onsquad.infrastructure.recyclebin.RecycleBinLifeCycleManager;
import revi1337.onsquad.infrastructure.redis.RedisCacheCleaner;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.token.domain.model.JsonWebToken;
import revi1337.onsquad.token.domain.model.RefreshToken;
import revi1337.onsquad.token.infrastructure.application.FallbackRefreshTokenManager;
import revi1337.onsquad.token.infrastructure.application.RedisRefreshTokenManager;
import revi1337.onsquad.token.infrastructure.repository.ExpiringMapTokenRepository;

@MockBean({
        RecycleBinLifeCycleManager.class,
        VerificationBackupProcessor.class,
        ThrottlingAspect.class,
        RedisCacheAspect.class,
        CloudFrontCacheInvalidator.class
})
@SpringBootTest
@Disabled
class RefreshTokenReissueConcurrencyTest extends TestContainerSupport {

    @Autowired
    private ExpiringMapTokenRepository expiringMapTokenRepository;

    @Autowired
    private FallbackRefreshTokenManager fallbackRefreshTokenManager;

    @Autowired
    private RedisRefreshTokenManager redisRefreshTokenManager;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private JsonWebTokenManager jsonWebTokenManager;

    @Autowired
    private TokenReissueService tokenReissueService;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @BeforeEach
    void setUp() {
        expiringMapTokenRepository.deleteAll();
        RedisCacheCleaner.cleanAll(stringRedisTemplate);
    }

    @Test
    @DisplayName("redisRefreshTokenManager를 사용했을 때, 여러 사용자가 동시에 토큰 재발급 중에 Redis가 중단되면, 이후 로직은 모두 실패한다.")
    void success1() throws ExecutionException, InterruptedException {
        ReflectionTestUtils.setField(jsonWebTokenManager, "defaultRefreshTokenManager", redisRefreshTokenManager);
        List<Long> userIds = initTestUsers(MemberFixture.REVI(), MemberFixture.ANDONG(), MemberFixture.KWANGWON());
        List<RefreshToken> refreshTokens = initRefreshTokens(userIds);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());

        List<Future<?>> futures = IntStream.range(0, userIds.size())
                .mapToObj(idx -> executor.submit(() -> loopingReissueToken(refreshTokens.get(idx), errors)))
                .collect(Collectors.toList());
        futures.add(executor.submit(this::restartRedisContainer));

        for (Future<?> future : futures) {
            future.get();
        }
        executor.shutdown();

        assertThat(errors).isNotEmpty();
    }

    @Test
    @DisplayName("여러 사용자가 동시에 토큰 재발급 중 Redis가 중단되었다가 재시작되어도 재발급 처리에 실패하지 않는다.")
    void success2() throws ExecutionException, InterruptedException {
        ReflectionTestUtils.setField(jsonWebTokenManager, "defaultRefreshTokenManager", fallbackRefreshTokenManager);
        List<Long> userIds = initTestUsers(MemberFixture.REVI(), MemberFixture.ANDONG(), MemberFixture.KWANGWON());
        List<RefreshToken> refreshTokens = initRefreshTokens(userIds);

        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());

        List<Future<?>> futures = IntStream.range(0, userIds.size())
                .mapToObj(idx -> executor.submit(() -> loopingReissueToken(refreshTokens.get(idx), errors)))
                .collect(Collectors.toList());
        futures.add(executor.submit(this::restartRedisContainer));

        for (Future<?> future : futures) {
            future.get();
        }
        executor.shutdown();

        assertThat(errors).isEmpty();
    }

    private List<Long> initTestUsers(Member... members) {
        return memberJpaRepository.saveAll(List.of(members))
                .stream()
                .map(Member::getId)
                .toList();
    }

    private List<RefreshToken> initRefreshTokens(List<Long> userIds) {
        return userIds.stream()
                .map(userId -> {
                    RefreshToken refreshToken = jsonWebTokenManager.generateRefreshToken(userId);
                    jsonWebTokenManager.storeRefreshTokenFor(userId, refreshToken);
                    return refreshToken;
                }).toList();
    }

    private void loopingReissueToken(RefreshToken token, List<Throwable> errors) {
        RefreshToken current = token;
        for (int i = 0; i < 65; i++) {
            try {
                JsonWebToken jsonWebToken = tokenReissueService.reissue(current);
                current = new RefreshToken(jsonWebToken.refreshToken());
            } catch (Exception e) {
                errors.add(e);
            }
        }
    }

    private void restartRedisContainer() {
        try {
            Thread.sleep(260);
            DockerClient dockerClient = DockerClientFactory.instance().client();
            dockerClient.killContainerCmd(redis.getContainerId()).exec();
            Thread.sleep(650);
            dockerClient.restartContainerCmd(redis.getContainerId()).exec();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
