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
import revi1337.onsquad.common.fixture.MemberFixtures;
import revi1337.onsquad.member.application.dto.MemberInfoDto;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.domain.vo.Mbti;
import revi1337.onsquad.member.domain.vo.UserType;

class MemberQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberQueryService memberQueryService;

    @Test
    @DisplayName("닉네임이 중복되면 true 를 반한한다.")
    void checkDuplicateNickname1() {
        memberRepository.save(MemberFixtures.REVI());

        boolean exists = memberQueryService.checkDuplicateNickname(REVI_NICKNAME_VALUE);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("닉네임이 중복되지 않으면 false 를 반한한다.")
    void checkDuplicateNickname2() {
        memberRepository.save(MemberFixtures.REVI());

        boolean exists = memberQueryService.checkDuplicateNickname(ANDONG_NICKNAME_VALUE);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("이메일이 중복되면 true 를 반한한다.")
    void checkDuplicateEmail1() {
        memberRepository.save(MemberFixtures.REVI());

        boolean exists = memberQueryService.checkDuplicateEmail(REVI_EMAIL_VALUE);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("닉네임이 중복되지 않으면 false 를 반한한다.")
    void checkDuplicateEmail2() {
        memberRepository.save(MemberFixtures.REVI());

        boolean exists = memberQueryService.checkDuplicateEmail(ANDONG_EMAIL_VALUE);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("사용자가 존재하면 dto 를 반환한다.")
    void findMember1() {
        Member revi = memberRepository.save(MemberFixtures.REVI());
        Long reviId = revi.getId();

        MemberInfoDto memberInfoDto = memberQueryService.findMember(reviId);

        assertAll(() -> {
            assertThat(memberInfoDto.id()).isEqualTo(reviId);
            assertThat(memberInfoDto.email()).isEqualTo(REVI_EMAIL_VALUE);
            assertThat(memberInfoDto.nickname()).isEqualTo(REVI_NICKNAME_VALUE);
            assertThat(memberInfoDto.introduce()).isEqualTo(REVI_INTRODUCE_VALUE);
            assertThat(memberInfoDto.kakaoLink()).isEqualTo(REVI_KAKAO_LINK);
            assertThat(memberInfoDto.profileImage()).isEqualTo(REVI_PROFILE_IMAGE_LINK);
            assertThat(memberInfoDto.address()).isEqualTo(REVI_ADDRESS_VALUE);
            assertThat(memberInfoDto.addressDetail()).isEqualTo(REVI_ADDRESS_DETAIL_VALUE);
            assertThat(memberInfoDto.mbti()).isEqualTo(Mbti.ISTP.name());
            assertThat(memberInfoDto.userType()).isEqualTo(UserType.GENERAL.getText());
        });
    }
}