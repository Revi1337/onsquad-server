package revi1337.onsquad.inrastructure.mail.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static revi1337.onsquad.common.fixture.MemberValueFixture.INVALID_AUTHENTICATION_CODE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.VALID_AUTHENTICATION_CODE;

import io.lettuce.core.RedisConnectionException;
import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.inrastructure.mail.application.VerificationStatus;
import revi1337.onsquad.inrastructure.mail.support.VerificationState;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        VerificationCodeRedisRepository.class,
        VerificationCodeExpiringMapRepository.class,
        VerificationCodeRepositoryCandidates.class
})
class VerificationCodeRepositoryCandidatesTest {

    private static final String REDIS_CONNECTION_ERROR_MSG = "Redis Connection Fail";

    @MockBean
    private VerificationCodeRedisRepository redisRepository;

    @Autowired
    private VerificationCodeExpiringMapRepository mapRepository;

    @Autowired
    private VerificationCodeRepositoryCandidates repositoryCandidates;

    @BeforeEach
    void tearDown() {
        Map<String, VerificationState> tracker = (Map<String, VerificationState>)
                ReflectionTestUtils.getField(VerificationCodeExpiringMapRepository.class, "VERIFICATION_TRACKER");
        Map<String, String> store = (Map<String, String>)
                ReflectionTestUtils.getField(VerificationCodeExpiringMapRepository.class, "VERIFICATION_STORE");
        tracker.clear();
        store.clear();
    }

    @Nested
    @DisplayName("이메일 인증 코드 저장 위임 테스트")
    class SaveVerificationCode {

        @Test
        @DisplayName("인증 코드를 저장에 성공한다.")
        void saveVerificationCode() {
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(redisRepository)
                    .saveVerificationCode(any(), any(), any());

            repositoryCandidates
                    .saveVerificationCode(REVI_EMAIL_VALUE, VALID_AUTHENTICATION_CODE, Duration.ofMinutes(1));

            boolean valid = mapRepository.isValidVerificationCode(REVI_EMAIL_VALUE, VALID_AUTHENTICATION_CODE);
            assertThat(valid).isTrue();
        }
    }

    @Nested
    @DisplayName("이메일 인증 코드 유효성 위임 테스트")
    class IsValidVerificationCode {

        @Test
        @DisplayName("인증 코드가 유효하면 True 를 반환한다.")
        void isValidVerificationCode1() {
            mapRepository.saveVerificationCode(REVI_EMAIL_VALUE, VALID_AUTHENTICATION_CODE, Duration.ofMinutes(1));
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(redisRepository)
                    .isValidVerificationCode(any(), any());

            boolean valid = mapRepository.isValidVerificationCode(REVI_EMAIL_VALUE, VALID_AUTHENTICATION_CODE);

            assertThat(valid).isTrue();
        }

        @Test
        @DisplayName("인증 코드가 유효하지 않으면 False 를 반환한다.")
        void isValidVerificationCode2() {
            mapRepository.saveVerificationCode(REVI_EMAIL_VALUE, VALID_AUTHENTICATION_CODE, Duration.ofMinutes(1));
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(redisRepository)
                    .isValidVerificationCode(any(), any());

            boolean valid = mapRepository.isValidVerificationCode(REVI_EMAIL_VALUE, INVALID_AUTHENTICATION_CODE);

            assertThat(valid).isFalse();
        }
    }

    @Nested
    @DisplayName("이메일 인증 코드 검증 완료 Mark 위임 테스트")
    class MarkVerificationStatus {

        @Test
        @DisplayName("인증코드를 발급받았다면, Mark 하고 True 를 반환한다.")
        void markVerificationStatus1() {
            mapRepository.saveVerificationCode(REVI_EMAIL_VALUE, VALID_AUTHENTICATION_CODE, Duration.ofMinutes(1));
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(redisRepository)
                    .markVerificationStatus(any(), any(), any());

            boolean marked = mapRepository
                    .markVerificationStatus(REVI_EMAIL_VALUE, VerificationStatus.SUCCESS, Duration.ofMillis(1));

            assertThat(marked).isTrue();
        }

        @Test
        @DisplayName("인증코드를 발급 받지 않았다면, Mark 하지 못하고 False 를 반환한다.")
        void markVerificationStatus2() {
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(redisRepository)
                    .markVerificationStatus(any(), any(), any());

            boolean marked = mapRepository
                    .markVerificationStatus(REVI_EMAIL_VALUE, VerificationStatus.SUCCESS, Duration.ofMillis(1));

            assertThat(marked).isFalse();
        }
    }

    @Nested
    @DisplayName("인증 완료 위임 테스트")
    class IsMarkedVerificationStatusWith {

        @Test
        @DisplayName("인증이 완료되었으면 True 를 반환한다.")
        void isMarkedVerificationStatusWith1() {
            mapRepository.saveVerificationCode(REVI_EMAIL_VALUE, VALID_AUTHENTICATION_CODE, Duration.ofMinutes(1));
            mapRepository.markVerificationStatus(REVI_EMAIL_VALUE, VerificationStatus.SUCCESS, Duration.ofMinutes(1));
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(redisRepository)
                    .isMarkedVerificationStatusWith(any(), any());

            boolean marked = mapRepository.isMarkedVerificationStatusWith(REVI_EMAIL_VALUE, VerificationStatus.SUCCESS);

            assertThat(marked).isTrue();
        }

        @Test
        @DisplayName("인증 코드는 발급받았지만, 인증이 완료되지 않았으면, False 를 반환한다.")
        void isMarkedVerificationStatusWith2() {
            mapRepository.saveVerificationCode(REVI_EMAIL_VALUE, VALID_AUTHENTICATION_CODE, Duration.ofMinutes(1));
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(redisRepository)
                    .isMarkedVerificationStatusWith(any(), any());

            boolean marked = mapRepository.isMarkedVerificationStatusWith(REVI_EMAIL_VALUE, VerificationStatus.SUCCESS);

            assertThat(marked).isFalse();
        }

        @Test
        @DisplayName("인증 코드 자체를 발급 받지 않았으면 False 를 반환한다.")
        void isMarkedVerificationStatusWith3() {
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(redisRepository)
                    .isMarkedVerificationStatusWith(any(), any());

            boolean marked = mapRepository.isMarkedVerificationStatusWith(REVI_EMAIL_VALUE, VerificationStatus.SUCCESS);

            assertThat(marked).isFalse();
        }
    }
}