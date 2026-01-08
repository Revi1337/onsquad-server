package revi1337.onsquad.member.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_CODE;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_CODE_TIMEOUT;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_SUCCESS;
import static revi1337.onsquad.common.fixture.MemberValueFixture.EMAIL_VALUE;

import io.lettuce.core.RedisConnectionException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.auth.verification.domain.VerificationCodeRepository;
import revi1337.onsquad.auth.verification.infrastructure.VerificationCodeExpiringMapRepository;
import revi1337.onsquad.auth.verification.infrastructure.VerificationCodeRedisRepository;
import revi1337.onsquad.auth.verification.infrastructure.VerificationCodeRepositoryComposite;

class VerificationCodeRepositoryCompositeTest {

    private static final String REDIS_CONNECTION_ERROR_MSG = "Redis Connection Fail";

    private VerificationCodeRepository firstRepository = mock(VerificationCodeRedisRepository.class);
    private VerificationCodeRepository secondRepository = mock(VerificationCodeRepository.class);
    private VerificationCodeRepository lastRepository = mock(VerificationCodeExpiringMapRepository.class);
    private final VerificationCodeRepositoryComposite repositoryCandidates =
            new VerificationCodeRepositoryComposite(List.of(firstRepository, secondRepository, lastRepository));

    @Nested
    @DisplayName("이메일 인증 코드 저장 위임을 테스트한다.")
    class SaveVerificationCode {

        @Test
        @DisplayName("SaveVerificationCode 시, 첫번째 Repository 에서 예외가 터지면 다음 Repository 로 위임한다.")
        void success1() {
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(firstRepository)
                    .saveVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE, TEST_VERIFICATION_CODE_TIMEOUT);

            repositoryCandidates
                    .saveVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE, TEST_VERIFICATION_CODE_TIMEOUT);

            verify(secondRepository)
                    .saveVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE, TEST_VERIFICATION_CODE_TIMEOUT);
        }

        @Test
        @DisplayName("SaveVerificationCode 시, 모든 Repository 에서 예외를 던져도 마지막 Repository 에서는 예외가 나지 않는다.")
        void success2() {
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(firstRepository)
                    .saveVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE, TEST_VERIFICATION_CODE_TIMEOUT);
            doThrow(new IllegalArgumentException())
                    .when(secondRepository)
                    .saveVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE, TEST_VERIFICATION_CODE_TIMEOUT);

            repositoryCandidates
                    .saveVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE, TEST_VERIFICATION_CODE_TIMEOUT);

            verify(lastRepository)
                    .saveVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE, TEST_VERIFICATION_CODE_TIMEOUT);
        }

        @Test
        @DisplayName("SaveVerificationCode 시, 모든 Repository 에서 예외를 던지면 실패한다.")
        void fail() {
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(firstRepository)
                    .saveVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE, TEST_VERIFICATION_CODE_TIMEOUT);
            doThrow(new IllegalArgumentException())
                    .when(secondRepository)
                    .saveVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE, TEST_VERIFICATION_CODE_TIMEOUT);
            doThrow(new IllegalArgumentException())
                    .when(lastRepository)
                    .saveVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE, TEST_VERIFICATION_CODE_TIMEOUT);

            assertThatThrownBy(() -> repositoryCandidates
                    .saveVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE, TEST_VERIFICATION_CODE_TIMEOUT))
                    .isExactlyInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("이메일 인증 코드 유효성 위임을 테스트한다.")
    class IsValidVerificationCode {

        @Test
        @DisplayName("isValidVerificationCode 시, 첫번째 Repository 에서 예외가 터지면 다음 Repository 로 위임한다.")
        void success1() {
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(firstRepository)
                    .isValidVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE);

            boolean ignored = repositoryCandidates.isValidVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE);

            verify(secondRepository).isValidVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE);
        }

        @Test
        @DisplayName("isValidVerificationCode 시, 모든 Repository 에서 예외를 던져도 마지막 Repository 에서는 예외가 나지 않는다.")
        void success2() {
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(firstRepository)
                    .isValidVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE);
            doThrow(new IllegalArgumentException())
                    .when(secondRepository)
                    .isValidVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE);

            boolean ignored = repositoryCandidates.isValidVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE);

            verify(lastRepository).isValidVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE);
        }

        @Test
        @DisplayName("isValidVerificationCode 시, 모든 Repository 에서 예외를 던지면 실패한다.")
        void fail() {
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(firstRepository)
                    .isValidVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE);
            doThrow(new IllegalArgumentException())
                    .when(secondRepository)
                    .isValidVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE);
            doThrow(new IllegalArgumentException())
                    .when(lastRepository)
                    .isValidVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE);

            assertThatThrownBy(() -> repositoryCandidates
                    .isValidVerificationCode(EMAIL_VALUE, TEST_VERIFICATION_CODE))
                    .isExactlyInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("이메일 인증 코드 검증 완료 Mark 위임을 테스트한다.")
    class MarkVerificationStatus {

        @Test
        @DisplayName("markVerificationStatus 시, 첫번째 Repository 에서 예외가 터지면 다음 Repository 로 위임한다.")
        void success1() {
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(firstRepository)
                    .markVerificationStatus(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS, TEST_VERIFICATION_CODE_TIMEOUT);

            boolean ignored = repositoryCandidates
                    .markVerificationStatus(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS, TEST_VERIFICATION_CODE_TIMEOUT);

            verify(secondRepository).markVerificationStatus(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS,
                    TEST_VERIFICATION_CODE_TIMEOUT);
        }

        @Test
        @DisplayName("markVerificationStatus 시, 모든 Repository 에서 예외를 던져도 마지막 Repository 에서는 예외가 나지 않는다.")
        void success2() {
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(firstRepository)
                    .markVerificationStatus(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS, TEST_VERIFICATION_CODE_TIMEOUT);
            doThrow(new IllegalArgumentException())
                    .when(secondRepository)
                    .markVerificationStatus(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS, TEST_VERIFICATION_CODE_TIMEOUT);

            boolean ignored = repositoryCandidates
                    .markVerificationStatus(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS, TEST_VERIFICATION_CODE_TIMEOUT);

            verify(lastRepository).markVerificationStatus(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS,
                    TEST_VERIFICATION_CODE_TIMEOUT);
        }

        @Test
        @DisplayName("markVerificationStatus 시, 모든 Repository 에서 예외를 던지면 실패한다.")
        void fail() {
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(firstRepository)
                    .markVerificationStatus(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS, TEST_VERIFICATION_CODE_TIMEOUT);
            doThrow(new IllegalArgumentException())
                    .when(secondRepository)
                    .markVerificationStatus(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS, TEST_VERIFICATION_CODE_TIMEOUT);
            doThrow(new IllegalArgumentException())
                    .when(lastRepository)
                    .markVerificationStatus(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS, TEST_VERIFICATION_CODE_TIMEOUT);

            assertThatThrownBy(() -> repositoryCandidates
                    .markVerificationStatus(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS, TEST_VERIFICATION_CODE_TIMEOUT))
                    .isExactlyInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("인증이 완료 여부 확인 위임을 테스트한다.")
    class IsMarkedVerificationStatusWith {

        @Test
        @DisplayName("isMarkedVerificationStatusWith 시, 첫번째 Repository 에서 예외가 터지면 다음 Repository 로 위임한다.")
        void success1() {
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(firstRepository)
                    .isMarkedVerificationStatusWith(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS);

            boolean ignored = repositoryCandidates
                    .isMarkedVerificationStatusWith(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS);

            verify(secondRepository).isMarkedVerificationStatusWith(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS);
        }

        @Test
        @DisplayName("isMarkedVerificationStatusWith 시, 첫번째 Repository 에서 예외가 터지면 다음 Repository 로 위임한다.")
        void success2() {
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(firstRepository)
                    .isMarkedVerificationStatusWith(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS);
            doThrow(new IllegalArgumentException())
                    .when(secondRepository)
                    .isMarkedVerificationStatusWith(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS);

            boolean ignored = repositoryCandidates
                    .isMarkedVerificationStatusWith(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS);

            verify(lastRepository).isMarkedVerificationStatusWith(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS);
        }

        @Test
        @DisplayName("isMarkedVerificationStatusWith 시, 모든 Repository 에서 예외를 던지면 실패한다.")
        void fail() {
            doThrow(new RedisConnectionException(REDIS_CONNECTION_ERROR_MSG))
                    .when(firstRepository)
                    .isMarkedVerificationStatusWith(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS);
            doThrow(new IllegalArgumentException())
                    .when(secondRepository)
                    .isMarkedVerificationStatusWith(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS);
            doThrow(new IllegalArgumentException())
                    .when(lastRepository)
                    .isMarkedVerificationStatusWith(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS);

            assertThatThrownBy(() -> repositoryCandidates
                    .isMarkedVerificationStatusWith(EMAIL_VALUE, TEST_VERIFICATION_SUCCESS))
                    .isExactlyInstanceOf(UnsupportedOperationException.class);
        }
    }
}
