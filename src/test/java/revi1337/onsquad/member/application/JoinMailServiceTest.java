package revi1337.onsquad.member.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.common.mail.EmailSender;
import revi1337.onsquad.common.mail.MailStatus;
import revi1337.onsquad.member.domain.redis.RedisMailRepository;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@DisplayName("회원가입 메일서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class JoinMailServiceTest {

    @Mock private EmailSender emailSender;
    @Mock private RedisMailRepository redisMailRepository;
    @InjectMocks private JoinMailService joinMailService;

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_AUTH_CODE = "1111";

    @DisplayName("이메일 인증코드가 정상적으로 전송되는지 확인한다.")
    @Test
    public void sendAuthCodeToEmail() {
        // given
        String testEmail = "test@email.com";
        String testAuthCode = "11111";
        willDoNothing().given(emailSender)
                .sendEmail(anyString(), eq(testAuthCode), eq(testEmail));

        // when
        joinMailService.sendAuthCodeToEmail(testEmail, testAuthCode);

        // then
        then(emailSender).should(times(1))
                .sendEmail(anyString(), eq(testAuthCode), eq(testEmail));
        then(redisMailRepository).should(times(1))
                .saveAuthCode(eq(testEmail), eq(testAuthCode), any(Duration.class));
    }

    @DisplayName("이메일 인증코드 검증에 성공하면 true 를 반환한다.")
    @Test
    public void verifyAuthCode() {
        // given
        Duration minutes = Duration.ofMinutes(5);
        given(redisMailRepository.isValidMailAuthCode(TEST_EMAIL, TEST_AUTH_CODE))
                .willReturn(true);
        given(redisMailRepository.overwriteAuthCodeToStatus(TEST_EMAIL, MailStatus.SUCCESS, minutes))
                .willReturn(true);

        // when
        boolean valid = joinMailService.verifyAuthCode(TEST_EMAIL, TEST_AUTH_CODE, minutes);

        // then
        then(redisMailRepository).should(times(1))
                .isValidMailAuthCode(TEST_EMAIL, TEST_AUTH_CODE);
        then(redisMailRepository).should(times(1))
                .overwriteAuthCodeToStatus(TEST_EMAIL, MailStatus.SUCCESS, minutes);
        assertThat(valid).isTrue();
    }

    @DisplayName("이메일 인증코드 검증에 실패하면 false 를 반환한다.")
    @Test
    public void verifyAuthCode2() {
        // given
        Duration minutes = Duration.ofMinutes(5);
        given(redisMailRepository.isValidMailAuthCode(any(), any()))
                .willReturn(true);
        given(redisMailRepository.overwriteAuthCodeToStatus(any(), any(MailStatus.class), any(Duration.class)))
                .willReturn(false);

        // when
        boolean valid = joinMailService.verifyAuthCode(TEST_EMAIL, TEST_AUTH_CODE, minutes);

        // then
        then(redisMailRepository).should(times(1))
                .isValidMailAuthCode(TEST_EMAIL, TEST_AUTH_CODE);
        then(redisMailRepository).should(times(1))
                .overwriteAuthCodeToStatus(TEST_EMAIL, MailStatus.SUCCESS, minutes);
        assertThat(valid).isFalse();
    }

    @DisplayName("회원가입 메일 인증이 완료되어있는 상태면 true 를 반환한다.")
    @Test
    public void isValidMailStatus() {
        // given
        given(redisMailRepository.isValidMailStatus(TEST_EMAIL, MailStatus.SUCCESS))
                .willReturn(true);

        // when
        boolean isValidMailStatus = joinMailService.isValidMailStatus(TEST_EMAIL);

        // then
        then(redisMailRepository).should(times(1))
                .isValidMailStatus(TEST_EMAIL, MailStatus.SUCCESS);
        assertThat(isValidMailStatus).isTrue();
    }

    @DisplayName("회원가입 메일 인증이 완료되어있지 않은 상태면 false 를 반환한다.")
    @Test
    public void isValidMailStatus2() {
        // given
        given(redisMailRepository.isValidMailStatus(any(), any()))
                .willReturn(false);

        // when
        boolean isValidMailStatus = joinMailService.isValidMailStatus(TEST_EMAIL);

        // then
        then(redisMailRepository).should(times(1))
                .isValidMailStatus(TEST_EMAIL, MailStatus.SUCCESS);
        assertThat(isValidMailStatus).isFalse();
    }
}
