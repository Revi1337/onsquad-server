package revi1337.onsquad.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.ValueFixture.ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.ValueFixture.ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.ValueFixture.ANDONG_ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.ValueFixture.ANDONG_ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.ValueFixture.ANDONG_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.ValueFixture.ANDONG_KAKAO_LINK;
import static revi1337.onsquad.common.fixture.ValueFixture.ANDONG_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.ValueFixture.ANDONG_PROFILE_IMAGE_LINK;
import static revi1337.onsquad.common.fixture.ValueFixture.CHANGED_PROFILE_IMAGE_LINK;
import static revi1337.onsquad.common.fixture.ValueFixture.EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.ValueFixture.INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.ValueFixture.KAKAO_LINK;
import static revi1337.onsquad.common.fixture.ValueFixture.NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.ValueFixture.PROFILE_IMAGE_LINK;
import static revi1337.onsquad.common.fixture.ValueFixture.REVI_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.ValueFixture.REVI_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.ValueFixture.REVI_KAKAO_LINK;
import static revi1337.onsquad.common.fixture.ValueFixture.REVI_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.ValueFixture.REVI_PROFILE_IMAGE_LINK;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.common.fixture.MemberFixtures;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Introduce;
import revi1337.onsquad.member.domain.vo.Mbti;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.domain.vo.UserType;

class MemberTest {

    @Test
    @DisplayName("회원의 기본 프로필 이미지, 카카오 링크, 한줄 소개, 유저 타입, MBTI 를 확인합니다.")
    void testMember1() {
        Member member = MemberFixtures.DEFAULT();

        assertAll(() -> {
            assertThat(member.getEmail()).isEqualTo(new Email(EMAIL_VALUE));
            assertThat(member.getAddress()).isEqualTo(new Address(ADDRESS_VALUE, ADDRESS_DETAIL_VALUE));
            assertThat(member.getNickname()).isEqualTo(new Nickname(NICKNAME_VALUE));
            assertThat(member.getIntroduce()).isEqualTo(new Introduce(INTRODUCE_VALUE));
            assertThat(member.getProfileImage()).isEqualTo(PROFILE_IMAGE_LINK);
            assertThat(member.getKakaoLink()).isEqualTo(KAKAO_LINK);
            assertThat(member.getUserType()).isSameAs(UserType.GENERAL);
            assertThat(member.getMbti()).isSameAs(Mbti.ENTP);
        });
    }

    @Test
    @DisplayName("회원정보 업데이트를 성공합니다.")
    void testMember2() {
        Member member = MemberFixtures.DEFAULT();

        member.updateProfile(MemberFixtures.ANDONG());

        assertAll(() -> {
            assertThat(member.getAddress()).isEqualTo(new Address(ANDONG_ADDRESS_VALUE, ANDONG_ADDRESS_DETAIL_VALUE));
            assertThat(member.getNickname()).isEqualTo(new Nickname(ANDONG_NICKNAME_VALUE));
            assertThat(member.getIntroduce()).isEqualTo(new Introduce(ANDONG_INTRODUCE_VALUE));
            assertThat(member.getProfileImage()).isEqualTo(ANDONG_PROFILE_IMAGE_LINK);
            assertThat(member.getKakaoLink()).isEqualTo(ANDONG_KAKAO_LINK);
            assertThat(member.getUserType()).isSameAs(UserType.GENERAL);
            assertThat(member.getMbti()).isSameAs(Mbti.ISFP);
        });
    }

    @Test
    @DisplayName("회원 프로필 이미지 정보를 업데이트합니다.")
    void testMember3() {
        Member member = MemberFixtures.REVI();

        member.updateProfileImage(CHANGED_PROFILE_IMAGE_LINK);

        assertThat(member.getProfileImage()).isEqualTo(CHANGED_PROFILE_IMAGE_LINK);
    }

    @Test
    @DisplayName("OAuth2 사용자 생성을 테스트합니다.")
    void testMember4() {
        Member member = MemberFixtures.REVI_GOOGLE();

        assertAll(() -> {
            assertThat(member.getEmail()).isEqualTo(new Email(REVI_EMAIL_VALUE));
            assertThat(member.getAddress()).isEqualTo(new Address(ADDRESS_VALUE, ADDRESS_DETAIL_VALUE));
            assertThat(member.getNickname()).isEqualTo(new Nickname(REVI_NICKNAME_VALUE));
            assertThat(member.getIntroduce()).isEqualTo(new Introduce(REVI_INTRODUCE_VALUE));
            assertThat(member.getProfileImage()).isEqualTo(REVI_PROFILE_IMAGE_LINK);
            assertThat(member.getKakaoLink()).isEqualTo(REVI_KAKAO_LINK);
            assertThat(member.getUserType()).isSameAs(UserType.GOOGLE);
            assertThat(member.getMbti()).isSameAs(Mbti.ISTP);
        });
    }
}