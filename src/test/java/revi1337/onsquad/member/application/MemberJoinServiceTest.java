package revi1337.onsquad.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.willDoNothing;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import revi1337.onsquad.auth.error.exception.AuthJoinException;
import revi1337.onsquad.member.application.dto.MemberDto;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.domain.vo.Password;
import revi1337.onsquad.support.TestContainerSupport;

@DisplayName("회원가입 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MemberJoinServiceTest extends TestContainerSupport {

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "12345!@asa";
    private static final String TEST_NICKNAME = "nickname";
    private static final String TEST_AUTH_CODE = "1111";

    @Mock
    private MemberJpaRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JoinMailService joinMailService;
    @InjectMocks
    private MemberJoinService memberJoinService;

    @DisplayName("이메일 인증코드 전송이 잘 동작하는지 확인한다.")
    @Test
    public void sendAuthCodeToEmail() {
        // given
        willDoNothing().given(joinMailService).sendAuthCodeToEmail(eq(TEST_EMAIL), anyString());

        // when
        memberJoinService.sendAuthCodeToEmail(TEST_EMAIL);

        // then
        then(joinMailService).should(times(1))
                .sendAuthCodeToEmail(eq(TEST_EMAIL), anyString());
    }

    @DisplayName("이메일 인증에 실패하면 true 를 반환한다.")
    @Test
    public void verifyAuthCode() {
        // given
        Duration minutes = Duration.ofMinutes(5);
        given(joinMailService.verifyAuthCode(TEST_EMAIL, TEST_AUTH_CODE, minutes))
                .willReturn(true);

        // when
        boolean success = memberJoinService.verifyAuthCode(TEST_EMAIL, TEST_AUTH_CODE);

        // then
        then(joinMailService).should(times(1))
                .verifyAuthCode(eq(TEST_EMAIL), eq(TEST_AUTH_CODE), eq(minutes));
        assertThat(success).isTrue();
    }

    @DisplayName("이메일 인증에 실패하면 false 를 반환한다.")
    @Test
    public void verifyAuthCode2() {
        // given
        Duration minutes = Duration.ofMinutes(5);
        given(joinMailService.verifyAuthCode(TEST_EMAIL, TEST_AUTH_CODE, minutes))
                .willReturn(false);

        // when
        boolean success = memberJoinService.verifyAuthCode(TEST_EMAIL, TEST_AUTH_CODE);

        // then
        then(joinMailService).should(times(1))
                .verifyAuthCode(eq(TEST_EMAIL), eq(TEST_AUTH_CODE), eq(minutes));
        assertThat(success).isFalse();
    }

    @DisplayName("중복되는 닉네임이 있으면 true 를 반환한다.")
    @Test
    public void checkDuplicateNickname() {
        // given
        Nickname nickname = new Nickname(TEST_NICKNAME);
        given(memberRepository.existsByNickname(nickname)).willReturn(true);

        // when
        boolean exists = memberJoinService.checkDuplicateNickname(TEST_NICKNAME);

        // then
        then(memberRepository).should(times(1))
                .existsByNickname(nickname);
        assertThat(exists).isTrue();
    }

    @DisplayName("중복되는 닉네임이 없으면 false 를 반환한다.")
    @Test
    public void checkDuplicateNickname2() {
        // given
        Nickname nickname = new Nickname(TEST_NICKNAME);
        given(memberRepository.existsByNickname(nickname)).willReturn(false);

        // when
        boolean exists = memberJoinService.checkDuplicateNickname(TEST_NICKNAME);

        // then
        then(memberRepository).should(times(1))
                .existsByNickname(nickname);
        assertThat(exists).isFalse();
    }

    @DisplayName("닉네임이 중복되면 회원가입을 진행할 수 없다.")
    @Test
    public void joinMember1() {
        // given
        MemberDto memberDto = MemberDto.builder().nickname(new Nickname(TEST_NICKNAME)).build();
        given(memberRepository.existsByNickname(memberDto.getNickname())).willReturn(true);

        // when && then
        assertThatThrownBy(() -> memberJoinService.joinMember(memberDto))
                .isExactlyInstanceOf(AuthJoinException.DuplicateNickname.class)
                .hasMessage(String.format("%s 닉네임은 이미 사용중입니다.", memberDto.getNickname().getValue()));
        then(memberRepository).should(times(0)).save(any());
    }

    @DisplayName("메일 인증이 완료되어있지 않으면 회원가입을 진행할 수 없다.")
    @Test
    public void joinMember2() {
        // given
        MemberDto memberDto = MemberDto.builder().email(new Email(TEST_EMAIL)).nickname(new Nickname(TEST_NICKNAME))
                .build();
        given(memberRepository.existsByNickname(memberDto.getNickname())).willReturn(false);
        given(joinMailService.isValidMailStatus(TEST_EMAIL)).willReturn(false);

        // when && then
        assertThatThrownBy(() -> memberJoinService.joinMember(memberDto))
                .isExactlyInstanceOf(AuthJoinException.NonAuthenticateEmail.class)
                .hasMessage("메일 인증이 되어있지 않습니다.");
        then(memberRepository).should(times(0)).save(any());
    }

    @DisplayName("이메일이 중복되면 회원가입을 진행할 수 없다.")
    @Test
    public void joinMember3() {
        // given
        MemberDto memberDto = MemberDto.builder().email(new Email(TEST_EMAIL)).nickname(new Nickname(TEST_NICKNAME))
                .build();
        given(memberRepository.existsByNickname(memberDto.getNickname())).willReturn(false);
        given(joinMailService.isValidMailStatus(TEST_EMAIL)).willReturn(true);
        given(memberRepository.existsByEmail(memberDto.getEmail())).willReturn(true);

        // when && then
        assertThatThrownBy(() -> memberJoinService.joinMember(memberDto))
                .isExactlyInstanceOf(AuthJoinException.DuplicateMember.class)
                .hasMessage("이미 회원가입이 되어있는 사용자입니다.");
        then(memberRepository).should(times(0)).save(any());
    }

    @DisplayName("회원가입에 성공한다.")
    @Test
    public void joinMember4() {
        // given
        MemberDto memberDto = MemberDto.builder().email(new Email(TEST_EMAIL)).password(new Password(TEST_PASSWORD))
                .build();
        given(memberRepository.existsByNickname(memberDto.getNickname())).willReturn(false);
        given(joinMailService.isValidMailStatus(TEST_EMAIL)).willReturn(true);
        given(memberRepository.existsByEmail(memberDto.getEmail())).willReturn(false);
        given(passwordEncoder.encode(memberDto.getPassword().getValue())).willReturn(TEST_PASSWORD);

        // when
        memberJoinService.joinMember(memberDto);

        // then
        then(memberRepository).should(times(1)).save(any());
    }
}
