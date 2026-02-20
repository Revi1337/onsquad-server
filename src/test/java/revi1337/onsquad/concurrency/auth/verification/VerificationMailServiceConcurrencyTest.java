package revi1337.onsquad.concurrency.auth.verification;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import net.jodah.expiringmap.ExpiringMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.auth.verification.application.VerificationCodeGenerator;
import revi1337.onsquad.auth.verification.application.VerificationCodeStorage;
import revi1337.onsquad.auth.verification.application.VerificationMailService;
import revi1337.onsquad.auth.verification.domain.VerificationCode;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;
import revi1337.onsquad.auth.verification.infrastructure.persistence.ExpiringMapVerificationCodeStorage;
import revi1337.onsquad.auth.verification.infrastructure.persistence.RdbVerificationCodeStorage;
import revi1337.onsquad.auth.verification.infrastructure.persistence.RedisVerificationCodeStorage;
import revi1337.onsquad.common.application.mail.EmailSender;
import revi1337.onsquad.common.config.ApplicationLayerConfiguration;
import revi1337.onsquad.common.container.MySqlTestContainerInitializer;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;

@Disabled("동시성 테스트는 스레드 간 격리 문제로 인해 수동 검증 시에만 단독 실행한다. (CI/CD 에서 문제 발생 가능)")
@Import({ApplicationLayerConfiguration.class})
@ContextConfiguration(initializers = {MySqlTestContainerInitializer.class, RedisTestContainerInitializer.class})
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class VerificationMailServiceConcurrencyTest {

    @Autowired
    @Qualifier("verificationCodeEmailSender")
    private EmailSender emailSender;

    @Autowired
    private VerificationCodeGenerator verificationCodeGenerator;

    private VerificationCodeStorage verificationCodeStorage;

    private VerificationMailService verificationMailService;

    private final String email = "user@test.com";

    @Nested
    @DisplayName("VerificationMailService 가 Rdb 를 사용할 경우를 테스트한다.")
    class whenRdb {

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @Autowired
        private RdbVerificationCodeStorage rdbVerificationCodeStorage;

        @BeforeEach
        void setUp() {
            jdbcTemplate.execute("DROP TABLE IF EXISTS verification_code");
            jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS verification_code(
                        email      VARCHAR(255) NOT NULL,
                        code       VARCHAR(50)  NOT NULL,
                        status     VARCHAR(20)  NOT NULL,
                        expired_at BIGINT       NOT NULL,
                        PRIMARY KEY (email)
                    );
                    """);
            verificationCodeStorage = rdbVerificationCodeStorage;
            verificationMailService = new VerificationMailService(emailSender, rdbVerificationCodeStorage, verificationCodeGenerator);
        }

        @Test
        @DisplayName("동시에 10개의 인증 요청이 올 때, 단 하나만 성공(true)을 반환해야 한다.")
        void success() {
            invokeConcurrencyTest();
        }
    }

    @Nested
    @DisplayName("VerificationMailService 가 Redis 를 사용할 경우를 테스트한다.")
    class whenRedis {

        @Autowired
        private StringRedisTemplate stringRedisTemplate;

        @Autowired
        private RedisVerificationCodeStorage redisVerificationCodeStorage;

        @BeforeEach
        void setUp() {
            stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
                connection.serverCommands().flushAll();
                return null;
            });
            verificationCodeStorage = redisVerificationCodeStorage;
            verificationMailService = new VerificationMailService(emailSender, redisVerificationCodeStorage, verificationCodeGenerator);
        }

        @Test
        @DisplayName("동시에 10개의 인증 요청이 올 때, 단 하나만 성공(true)을 반환해야 한다.")
        void success() {
            invokeConcurrencyTest();
        }
    }

    @Nested
    @DisplayName("VerificationMailService 가 ExpiringMap 을 사용할 경우를 테스트한다.")
    class whenExpiringMap {

        @Autowired
        private ExpiringMapVerificationCodeStorage expiringMapVerificationCodeStorage;

        @BeforeEach
        void setUp() {
            ((ExpiringMap<String, VerificationCode>) ReflectionTestUtils.getField(expiringMapVerificationCodeStorage, "verificationStore")).clear();
            verificationCodeStorage = expiringMapVerificationCodeStorage;
            verificationMailService = new VerificationMailService(emailSender, expiringMapVerificationCodeStorage, verificationCodeGenerator);
        }

        @Test
        @DisplayName("동시에 10개의 인증 요청이 올 때, 단 하나만 성공(true)을 반환해야 한다.")
        void success() {
            invokeConcurrencyTest();
        }
    }

    private void invokeConcurrencyTest() {
        // given
        String authCode = "123456";
        verificationCodeStorage.saveVerificationCode(email, authCode, VerificationStatus.PENDING, Duration.ofMinutes(3));

        // when
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            futures.add(CompletableFuture.runAsync(() -> {
                waitToStart(startLatch);
                boolean result = verificationMailService.validateVerificationCode(email, authCode);
                if (result) {
                    successCount.incrementAndGet();
                } else {
                    failCount.incrementAndGet();
                }
            }, executor));
        }
        startLatch.countDown();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        executor.shutdown();

        // then
        assertSoftly(softly -> {
            softly.assertThat(successCount.get())
                    .as("인증 성공은 반드시 1번만 성공한다.")
                    .isEqualTo(1);
            softly.assertThat(failCount.get())
                    .as("나머지는 모두 실패해야 한다")
                    .isEqualTo(threadCount - 1);
        });
    }

    private void waitToStart(CountDownLatch start) {
        try {
            start.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
