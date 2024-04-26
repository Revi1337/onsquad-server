package revi1337.onsquad.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.member.domain.vo.Password;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("영속성객체 Member 테스트")
class MemberTest {

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "12345!@asa";
    private static final String TEST_NICKNAME = "nickname";
    private static final String TEST_AUTH_CODE = "1111";

    @DisplayName("Member 의 비밀번호를 변경한다.")
    @Test
    public void changePassword() {
        // given
        Password prevPassword = new Password(TEST_PASSWORD);
        Member member = MemberFactory.withPassword(prevPassword);
        String rawPassword = TEST_PASSWORD + "TRASH";
        Password encodedPassword = new Password(rawPassword);

        // when
        member.changePassword(encodedPassword);
        
        // then
        assertSoftly(softly -> {
            assertThat(member.getPassword()).isEqualTo(encodedPassword);
            assertThat(member.getPassword()).isNotEqualTo(prevPassword);
        });
    }
}