package revi1337.onsquad.member.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import revi1337.onsquad.common.mail.EmailSender;

import java.time.Duration;

import static org.mockito.BDDMockito.*;

@DisplayName("회원가입 메일서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class JoinMailServiceTest {

    @Mock private EmailSender emailSender;
    @Mock private StringRedisTemplate stringRedisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;
    @InjectMocks private JoinMailService joinMailService;

    @DisplayName("이메일 인증코드가 정상적으로 전송되는지 확인한다.")
    @Test
    public void sendAuthCodeToEmail() {
        // given
        String testEmail = "test@email.com";
        String testAuthCode = "11111";
        willDoNothing().given(emailSender).sendEmail(anyString(), eq(testAuthCode), eq(testEmail));
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        willDoNothing().given(valueOperations).set(anyString(), anyString(), any(Duration.class));

        // when
        joinMailService.sendAuthCodeToEmail(testEmail, testAuthCode);

        // then
        verify(emailSender, times(1))
                .sendEmail(anyString(), eq(testAuthCode), eq(testEmail));
        verify(valueOperations, times(1)).set(anyString(), anyString(), any(Duration.class));
    }
}
