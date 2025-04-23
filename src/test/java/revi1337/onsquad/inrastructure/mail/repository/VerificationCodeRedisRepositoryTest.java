package revi1337.onsquad.inrastructure.mail.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.ValueFixture.INVALID_AUTHENTICATION_CODE;
import static revi1337.onsquad.common.fixture.ValueFixture.REVI_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.ValueFixture.VALID_AUTHENTICATION_CODE;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.common.TestContainerSupport;
import revi1337.onsquad.inrastructure.mail.application.VerificationStatus;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        RedisAutoConfiguration.class,
        VerificationCodeRedisRepository.class,
})
class VerificationCodeRedisRepositoryTest extends TestContainerSupport {

    @Autowired
    private VerificationCodeRedisRepository repository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void init() {
        redisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushAll();
    }

    @Nested
    @DisplayName("이메일 인증 코드 저장 테스트")
    class SaveVerificationCode {

        @Test
        @DisplayName("인증 코드를 저장에 성공한다.")
        void saveVerificationCode() {
            Duration expiringTime = Duration.ofMinutes(1);

            repository.saveVerificationCode(REVI_EMAIL_VALUE, VALID_AUTHENTICATION_CODE, expiringTime);

            boolean valid = repository.isValidVerificationCode(REVI_EMAIL_VALUE, VALID_AUTHENTICATION_CODE);
            assertThat(valid).isTrue();
        }
    }

    @Nested
    @DisplayName("이메일 인증 코드 유효성 테스트")
    class IsValidVerificationCode {

        @Test
        @DisplayName("인증 코드가 유효하면 True 를 반환한다.")
        void isValidVerificationCode1() {
            Duration expiringTime = Duration.ofMinutes(1);
            repository.saveVerificationCode(REVI_EMAIL_VALUE, VALID_AUTHENTICATION_CODE, expiringTime);

            boolean valid = repository.isValidVerificationCode(REVI_EMAIL_VALUE, VALID_AUTHENTICATION_CODE);

            assertThat(valid).isTrue();
        }

        @Test
        @DisplayName("인증 코드가 유효하지 않으면 False 를 반환한다.")
        void isValidVerificationCode2() {
            repository.saveVerificationCode(REVI_EMAIL_VALUE, VALID_AUTHENTICATION_CODE, Duration.ofMinutes(1));

            boolean valid = repository.isValidVerificationCode(REVI_EMAIL_VALUE, INVALID_AUTHENTICATION_CODE);

            assertThat(valid).isFalse();
        }
    }

    @Nested
    @DisplayName("이메일 인증 코드 검증 완료 Mark 테스트")
    class MarkVerificationStatus {

        @Test
        @DisplayName("인증코드를 발급받았다면, Mark 하고 True 를 반환한다.")
        void markVerificationStatus1() {
            repository.saveVerificationCode(REVI_EMAIL_VALUE, VALID_AUTHENTICATION_CODE, Duration.ofMinutes(1));
            Duration expiringTime = Duration.ofMillis(1);

            boolean marked = repository
                    .markVerificationStatus(REVI_EMAIL_VALUE, VerificationStatus.SUCCESS, expiringTime);

            assertThat(marked).isTrue();
        }

        @Test
        @DisplayName("인증코드를 발급 받지 않았다면, Mark 하지 못하고 False 를 반환한다.")
        void markVerificationStatus2() {
            Duration expiringTime = Duration.ofMillis(1);

            boolean marked = repository
                    .markVerificationStatus(REVI_EMAIL_VALUE, VerificationStatus.SUCCESS, expiringTime);

            assertThat(marked).isFalse();
        }
    }

    @Nested
    @DisplayName("인증이 완료되었는지 테스트")
    class IsMarkedVerificationStatusWith {

        @Test
        @DisplayName("인증이 완료되었으면 True 를 반환한다.")
        void isMarkedVerificationStatusWith1() {
            repository.saveVerificationCode(REVI_EMAIL_VALUE, VALID_AUTHENTICATION_CODE, Duration.ofMinutes(1));
            repository.markVerificationStatus(REVI_EMAIL_VALUE, VerificationStatus.SUCCESS, Duration.ofMinutes(1));

            boolean marked = repository.isMarkedVerificationStatusWith(REVI_EMAIL_VALUE, VerificationStatus.SUCCESS);

            assertThat(marked).isTrue();
        }

        @Test
        @DisplayName("인증 코드는 발급받았지만, 인증이 완료되지 않았으면, False 를 반환한다.")
        void isMarkedVerificationStatusWith2() {
            repository.saveVerificationCode(REVI_EMAIL_VALUE, VALID_AUTHENTICATION_CODE, Duration.ofMinutes(1));

            boolean marked = repository.isMarkedVerificationStatusWith(REVI_EMAIL_VALUE, VerificationStatus.SUCCESS);

            assertThat(marked).isFalse();
        }

        @Test
        @DisplayName("인증 코드 자체를 발급 받지 않았으면 False 를 반환한다.")
        void isMarkedVerificationStatusWith3() {
            boolean marked = repository.isMarkedVerificationStatusWith(REVI_EMAIL_VALUE, VerificationStatus.SUCCESS);

            assertThat(marked).isFalse();
        }
    }
}