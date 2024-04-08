package revi1337.onsquad.member.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.*;

@DisplayName("회원가입 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MemberJoinServiceTest {

    @Mock private JoinMailService joinMailService;
    @InjectMocks private MemberJoinService memberJoinService;

    @DisplayName("이메일 인증코드 전송이 잘 동작하는지 확인한다.")
    @Test
    public void sendAuthCodeToEmail() {
        // given
        String email = "david122123@gmail.com";

        // when
        memberJoinService.sendAuthCodeToEmail(email);

        // then
        verify(joinMailService, times(1))
                .sendAuthCodeToEmail(eq(email), anyString());
    }
}