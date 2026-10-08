package revi1337.onsquad.auth.verification.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreaker.State;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import java.time.Instant;
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
import revi1337.onsquad.auth.verification.domain.VerificationStatus;
import revi1337.onsquad.common.container.MySqlTestContainerInitializer;
import revi1337.onsquad.common.container.UnreachableRedis;

@Sql("classpath:db/mysql/verification_code_ddl.sql")
@Import(RdbVerificationCodeStorage.class)
@ContextConfiguration(initializers = MySqlTestContainerInitializer.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@JdbcTest
class ResilientVerificationCodeStorageIntegrationTest {

    private static final int MINIMUM_NUMBER_OF_CALLS = 4;

    @Autowired
    private RdbVerificationCodeStorage rdbVerificationCodeStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String email = "user@test.com";
    private final String code = "123456";

    private CircuitBreaker circuitBreaker;
    private ResilientVerificationCodeStorage storage;

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
        RedisVerificationCodeStorage unreachableRedisStorage = new RedisVerificationCodeStorage(UnreachableRedis.stringRedisTemplate());
        storage = new ResilientVerificationCodeStorage(unreachableRedisStorage, rdbVerificationCodeStorage, registry);
    }

    @Test
    @DisplayName("Redis 가 불가능해도 발송, 검증, 가입 확인 흐름이 RDB 로 끝까지 동작한다")
    void fullFlowWorksOnRdbWhenRedisIsDown() {
        long expiredAt = storage.saveVerificationCode(email, code, VerificationStatus.PENDING, Duration.ofMinutes(3));
        boolean codeValid = storage.isValidVerificationCode(email, code);
        boolean wrongCodeValid = storage.isValidVerificationCode(email, "000000");
        boolean marked = storage.markVerificationStatusAsSuccess(email, code, Duration.ofMinutes(5));
        boolean markedAgain = storage.markVerificationStatusAsSuccess(email, code, Duration.ofMinutes(5));

        assertThat(expiredAt).isGreaterThan(Instant.now().toEpochMilli());
        assertThat(codeValid).isTrue();
        assertThat(wrongCodeValid).isFalse();
        assertThat(marked).isTrue();
        assertThat(markedAgain).isFalse();
        assertThat(storage.isMarkedVerificationStatusWith(email, VerificationStatus.SUCCESS)).isTrue();
        assertThat(jdbcTemplate.queryForObject("SELECT status FROM verification_code WHERE email = ?", String.class, email))
                .isEqualTo(VerificationStatus.SUCCESS.name());
    }

    @Test
    @DisplayName("Redis 실패가 누적되어 서킷이 열린 뒤에도 흐름이 RDB 로 동작한다")
    void flowWorksAfterCircuitOpens() {
        for (int i = 0; i < MINIMUM_NUMBER_OF_CALLS; i++) {
            storage.isMarkedVerificationStatusWith(email, VerificationStatus.SUCCESS);
        }
        assertThat(circuitBreaker.getState()).isEqualTo(State.OPEN);

        storage.saveVerificationCode(email, code, VerificationStatus.PENDING, Duration.ofMinutes(3));
        boolean marked = storage.markVerificationStatusAsSuccess(email, code, Duration.ofMinutes(5));

        assertThat(marked).isTrue();
        assertThat(storage.isMarkedVerificationStatusWith(email, VerificationStatus.SUCCESS)).isTrue();
    }
}
