package revi1337.onsquad.member.domain;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.member.domain.vo.Password;

@DisplayName("영속성객체 Member 테스트")
class MemberTest {

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "12345!@asa";
    private static final String TEST_NICKNAME = "nickname";
    private static final String TEST_AUTH_CODE = "1111";

    @DisplayName("Member 의 비밀번호를 변경한다.")
    @Test
    public void updatePassword() {
        // given
        Password prevPassword = new Password(TEST_PASSWORD);
        Member member = MemberFactory.withPassword(prevPassword);
        String encodedPassword = TEST_PASSWORD + "TRASH";

        // when
        member.updatePassword(encodedPassword);

        // then
        assertSoftly(softly -> {
            softly.assertThat(member.getPassword().getValue()).isNotEqualTo(TEST_PASSWORD);
            softly.assertThat(member.getPassword().getValue()).isEqualTo(encodedPassword);
        });
    }
}