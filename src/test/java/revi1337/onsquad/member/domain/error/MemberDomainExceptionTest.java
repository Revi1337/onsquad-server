package revi1337.onsquad.member.domain.error;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MemberDomainExceptionTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("domainExceptions")
    @DisplayName("회원 도메인 규칙 위반 예외는 MemberDomainException 이며 MemberBusinessException 이 아니다.")
    void isDomainException_notBusinessException(String name, RuntimeException exception, MemberErrorCode expected) {
        assertThat(exception)
                .isInstanceOf(MemberDomainException.class)
                .isNotInstanceOf(MemberBusinessException.class);
        MemberDomainException domainException = (MemberDomainException) exception;
        assertThat(domainException.getErrorCode()).isEqualTo(expected);
        assertThat(domainException.getErrorMessage()).isEqualTo(expected.getDescription());
    }

    private static Stream<Arguments> domainExceptions() {
        return Stream.of(
                Arguments.of("InvalidEmailFormat", new MemberDomainException.InvalidEmailFormat(MemberErrorCode.INVALID_EMAIL_FORMAT), MemberErrorCode.INVALID_EMAIL_FORMAT),
                Arguments.of("InvalidPasswordFormat", new MemberDomainException.InvalidPasswordFormat(MemberErrorCode.INVALID_PASSWORD_FORMAT), MemberErrorCode.INVALID_PASSWORD_FORMAT),
                Arguments.of("InvalidNicknameLength", new MemberDomainException.InvalidNicknameLength(MemberErrorCode.INVALID_NICKNAME_LENGTH), MemberErrorCode.INVALID_NICKNAME_LENGTH),
                Arguments.of("InvalidIntroduceLength", new MemberDomainException.InvalidIntroduceLength(MemberErrorCode.INVALID_INTRODUCE_LENGTH), MemberErrorCode.INVALID_INTRODUCE_LENGTH),
                Arguments.of("InvalidMbti", new MemberDomainException.InvalidMbti(MemberErrorCode.INVALID_MBTI), MemberErrorCode.INVALID_MBTI)
        );
    }
}
