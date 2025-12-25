package revi1337.onsquad.member.domain.entity.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.member.error.MemberDomainException;

class IntroduceTest {

    @Test
    @DisplayName("자기소개의 길이가 1 보다 짧으면 실패합합니다.")
    void testIntroduce1() {
        String value = "";

        assertThatThrownBy(() -> new Introduce(value))
                .isInstanceOf(MemberDomainException.InvalidIntroduceLength.class);
    }

    @Test
    @DisplayName("자기소개의 길이가 200 보다 길면 실패합니다.")
    void testIntroduce2() {
        String value = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

        assertAll(() -> {
            assertThat(value.length()).isEqualTo(201);
            assertThatThrownBy(() -> new Introduce(value))
                    .isInstanceOf(MemberDomainException.InvalidIntroduceLength.class);
        });
    }

    @Test
    @DisplayName("새로운 introduce 를 만들면 새로운 인스턴스를 반환합니다.")
    void testIntroduce3() {
        Introduce introduce = new Introduce("aaaaaa");

        Introduce newIntroduce = introduce.update("aaaaaa");

        assertNotSame(introduce, newIntroduce);
    }

    @Test
    @DisplayName("자기소개가 null 이면 실패합니다.")
    void testIntroduce4() {
        String value = null;

        assertThatThrownBy(() -> new Introduce(value))
                .isInstanceOf(NullPointerException.class);
    }
}
