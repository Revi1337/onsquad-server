package revi1337.onsquad.inrastructure.mail.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_JOINING_TIMEOUT;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_CODE;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_CODE_MILLI_TIMEOUT;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_CODE_TIMEOUT;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_SUCCESS;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.inrastructure.mail.repository.VerificationCodeRepositoryCandidates;
import revi1337.onsquad.inrastructure.mail.support.VerificationCodeGenerator;

@ExtendWith(MockitoExtension.class)
class AuthMailServiceTest {

    @Mock
    private VerificationCodeEmailSender emailSender;

    @Mock
    private VerificationCodeRepositoryCandidates repositoryChain;

    @Mock
    private VerificationCodeGenerator verificationCodeGenerator;

    @InjectMocks
    private AuthMailService authMailService;

    @Nested
    @DisplayName("이메일 인증 코드 발송을 테스트한다.")
    class SendVerificationCode {

        @Test
        @DisplayName("이메일 인증 코드 발송에 성공한다.")
        void success() {
            when(verificationCodeGenerator.generate()).thenReturn(TEST_VERIFICATION_CODE);
            when(repositoryChain.saveVerificationCode(DUMMY_EMAIL_VALUE_1, TEST_VERIFICATION_CODE, TEST_VERIFICATION_CODE_TIMEOUT))
                    .thenReturn(TEST_VERIFICATION_CODE_MILLI_TIMEOUT);
            doNothing().when(emailSender).sendEmail(anyString(), any(VerificationCode.class), eq(DUMMY_EMAIL_VALUE_1));

            authMailService.sendVerificationCode(DUMMY_EMAIL_VALUE_1);

            verify(verificationCodeGenerator).generate();
            verify(emailSender).sendEmail(anyString(), any(VerificationCode.class), eq(DUMMY_EMAIL_VALUE_1));
        }
    }

    @Nested
    @DisplayName("이메일 인증 코드가 유효한지 테스트한다.")
    class IsValidVerificationCode {

        @Test
        @DisplayName("이메일 인증 코드가 유효하면, 인증완료 처리에 성공한다.")
        void success() {
            when(repositoryChain.isValidVerificationCode(DUMMY_EMAIL_VALUE_1, TEST_VERIFICATION_CODE)).thenReturn(true);
            when(repositoryChain.markVerificationStatus(DUMMY_EMAIL_VALUE_1, TEST_VERIFICATION_SUCCESS, TEST_JOINING_TIMEOUT)).thenReturn(true);

            boolean valid = authMailService.validateVerificationCode(DUMMY_EMAIL_VALUE_1, TEST_VERIFICATION_CODE);

            verify(repositoryChain).isValidVerificationCode(DUMMY_EMAIL_VALUE_1, TEST_VERIFICATION_CODE);
            verify(repositoryChain).markVerificationStatus(DUMMY_EMAIL_VALUE_1, TEST_VERIFICATION_SUCCESS, TEST_JOINING_TIMEOUT);
            assertThat(valid).isTrue();
        }

        @Test
        @DisplayName("이메일 인증 코드가 유효하지 않으면, 인증완료 처리에 실패한다.")
        void fail() {
            when(repositoryChain.isValidVerificationCode(DUMMY_EMAIL_VALUE_1, TEST_VERIFICATION_CODE)).thenReturn(false);

            boolean valid = authMailService.validateVerificationCode(DUMMY_EMAIL_VALUE_1, TEST_VERIFICATION_CODE);

            verify(repositoryChain).isValidVerificationCode(DUMMY_EMAIL_VALUE_1, TEST_VERIFICATION_CODE);
            verify(repositoryChain, never()).markVerificationStatus(eq(DUMMY_EMAIL_VALUE_1), any(), any());
            assertThat(valid).isFalse();
        }
    }
}
