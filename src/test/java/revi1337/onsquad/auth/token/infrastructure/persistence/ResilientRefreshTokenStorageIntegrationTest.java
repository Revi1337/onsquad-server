package revi1337.onsquad.auth.token.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreaker.State;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.auth.token.domain.model.RefreshToken;
import revi1337.onsquad.common.container.MySqlTestContainerInitializer;
import revi1337.onsquad.common.container.UnreachableRedis;

@Sql("classpath:db/mysql/refresh_token_ddl.sql")
@Import(RdbRefreshTokenStorage.class)
@ContextConfiguration(initializers = MySqlTestContainerInitializer.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@JdbcTest
class ResilientRefreshTokenStorageIntegrationTest {

    private static final int MINIMUM_NUMBER_OF_CALLS = 4;
    private static final Duration DURATION = Duration.ofDays(7);

    @Autowired
    private RdbRefreshTokenStorage rdbRefreshTokenStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Long memberId = 1L;

    private CircuitBreaker circuitBreaker;
    private ResilientRefreshTokenStorage storage;

    @BeforeEach
    void setUp() {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(CircuitBreakerConfig.custom()
                .slidingWindowSize(MINIMUM_NUMBER_OF_CALLS)
                .minimumNumberOfCalls(MINIMUM_NUMBER_OF_CALLS)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMinutes(5))
                .recordExceptions(DataAccessException.class)
                .build());
        circuitBreaker = registry.circuitBreaker("redisAuthCircuitBreaker");
        RedisRefreshTokenStorage unreachableRedisStorage = new RedisRefreshTokenStorage(UnreachableRedis.stringRedisTemplate());
        storage = new ResilientRefreshTokenStorage(unreachableRedisStorage, rdbRefreshTokenStorage, registry);
    }

    private RefreshToken newToken(String value) {
        return new RefreshToken(memberId, value, new Date(System.currentTimeMillis() + DURATION.toMillis()));
    }

    @Test
    @DisplayName("Redis 가 불가능해도 저장, 조회, 삭제 흐름이 RDB 로 끝까지 동작한다")
    void fullFlowWorksOnRdbWhenRedisIsDown() {
        RefreshToken token = newToken("first-token");

        long expiredAt = storage.saveToken(memberId, token, DURATION);
        Optional<RefreshToken> found = storage.findTokenBy(memberId);

        assertThat(expiredAt).isEqualTo(token.expiredAt().getTime());
        assertThat(found).isPresent();
        assertThat(found.get().value()).isEqualTo("first-token");
        assertThat(jdbcTemplate.queryForObject("SELECT token_value FROM refresh_token WHERE member_id = ?", String.class, memberId))
                .isEqualTo("first-token");

        storage.deleteTokenBy(memberId);

        assertThat(storage.findTokenBy(memberId)).isEmpty();
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM refresh_token", Integer.class)).isZero();
    }

    @Test
    @DisplayName("Redis 실패가 누적되어 서킷이 열린 뒤에도 저장, 조회, 삭제 흐름이 RDB 로 동작한다")
    void flowWorksAfterCircuitOpens() {
        for (int i = 0; i < MINIMUM_NUMBER_OF_CALLS; i++) {
            storage.findTokenBy(memberId);
        }
        assertThat(circuitBreaker.getState()).isEqualTo(State.OPEN);

        storage.saveToken(memberId, newToken("opened-token"), DURATION);
        Optional<RefreshToken> found = storage.findTokenBy(memberId);

        assertThat(found).isPresent();
        assertThat(found.get().value()).isEqualTo("opened-token");

        storage.saveToken(memberId, newToken("reissued-token"), DURATION);
        assertThat(storage.findTokenBy(memberId).map(RefreshToken::value)).contains("reissued-token");

        storage.deleteTokenBy(memberId);

        assertThat(storage.findTokenBy(memberId)).isEmpty();
        assertThat(circuitBreaker.getState()).isEqualTo(State.OPEN);
    }

    @Test
    @DisplayName("Redis 가 불가능한 상태에서 deleteAll 은 RDB 의 모든 토큰을 삭제한다")
    void deleteAllClearsRdbWhenRedisIsDown() {
        storage.saveToken(1L, newToken("token-1"), DURATION);
        storage.saveToken(2L, newToken("token-2"), DURATION);

        storage.deleteAll();

        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM refresh_token", Integer.class)).isZero();
    }
}
