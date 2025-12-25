package revi1337.onsquad.member.domain.entity.vo;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.member.error.MemberDomainException;

class MbtiTest {

    @Test
    @DisplayName("잘못된 MBTI 값을 파싱하면 실패한다.")
    void fail() {
        String invalidMbtiValue = "xxxx";

        assertThatThrownBy(() -> Mbti.parse(invalidMbtiValue))
                .isExactlyInstanceOf(MemberDomainException.InvalidMbti.class);
    }
}
