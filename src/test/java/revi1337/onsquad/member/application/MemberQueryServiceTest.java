package revi1337.onsquad.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_KAKAO_LINK;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_PROFILE_IMAGE_LINK;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.fixture.MemberFixture;
import revi1337.onsquad.member.application.dto.response.MemberResponse;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.entity.vo.Mbti;
import revi1337.onsquad.member.domain.entity.vo.UserType;
import revi1337.onsquad.member.domain.repository.MemberRepository;

class MemberQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberQueryService memberQueryService;

    @Test
    @DisplayName("닉네임이 중복되면 true 를 반한한다.")
    void checkDuplicateNickname1() {
        memberRepository.save(MemberFixture.REVI());

        boolean exists = memberQueryService.checkDuplicateNickname(REVI_NICKNAME_VALUE);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("닉네임이 중복되지 않으면 false 를 반한한다.")
    void checkDuplicateNickname2() {
        memberRepository.save(MemberFixture.REVI());

        boolean exists = memberQueryService.checkDuplicateNickname(ANDONG_NICKNAME_VALUE);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("이메일이 중복되면 true 를 반한한다.")
    void checkDuplicateEmail1() {
        memberRepository.save(MemberFixture.REVI());

        boolean exists = memberQueryService.checkDuplicateEmail(REVI_EMAIL_VALUE);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("닉네임이 중복되지 않으면 false 를 반한한다.")
    void checkDuplicateEmail2() {
        memberRepository.save(MemberFixture.REVI());

        boolean exists = memberQueryService.checkDuplicateEmail(ANDONG_EMAIL_VALUE);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("사용자가 존재하면 dto 를 반환한다.")
    void findMember1() {
        Member revi = memberRepository.save(MemberFixture.REVI());
        Long reviId = revi.getId();

        MemberResponse memberResponse = memberQueryService.findMember(reviId);

        assertAll(() -> {
            assertThat(memberResponse.id()).isEqualTo(reviId);
            assertThat(memberResponse.email()).isEqualTo(REVI_EMAIL_VALUE);
            assertThat(memberResponse.nickname()).isEqualTo(REVI_NICKNAME_VALUE);
            assertThat(memberResponse.introduce()).isEqualTo(REVI_INTRODUCE_VALUE);
            assertThat(memberResponse.kakaoLink()).isEqualTo(REVI_KAKAO_LINK);
            assertThat(memberResponse.profileImage()).isEqualTo(REVI_PROFILE_IMAGE_LINK);
            assertThat(memberResponse.address()).isEqualTo(REVI_ADDRESS_VALUE);
            assertThat(memberResponse.addressDetail()).isEqualTo(REVI_ADDRESS_DETAIL_VALUE);
            assertThat(memberResponse.mbti()).isEqualTo(Mbti.ISTP.name());
            assertThat(memberResponse.userType()).isEqualTo(UserType.GENERAL.getText());
        });
    }
}
