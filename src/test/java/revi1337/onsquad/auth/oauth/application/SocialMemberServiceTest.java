package revi1337.onsquad.auth.oauth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.auth.oauth.infrastructure.google.GoogleOAuth2UserProfile;
import revi1337.onsquad.auth.oauth.infrastructure.kakao.KakaoOAuth2UserProfile;
import revi1337.onsquad.auth.token.domain.model.JsonWebToken;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.member.domain.vo.Address;
import revi1337.onsquad.member.domain.vo.UserType;

class SocialMemberServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private SocialMemberService socialMemberService;

    @Test
    @DisplayName("소셜 로그인으로 처음 로그인하면 기본 주소와 랜덤 닉네임으로 회원이 가입되고 토큰이 발급된다.")
    void joinsMemberWithDefaultAddressAndRandomNickname() {
        KakaoOAuth2UserProfile profile = new KakaoOAuth2UserProfile("홍길동", "홍길동", "social.user@gmail.com", true, "image", "thumbnail");

        JsonWebToken token = socialMemberService.authenticate(profile);

        List<Member> members = memberRepository.findAll();
        assertSoftly(softly -> {
            softly.assertThat(token).isNotNull();
            softly.assertThat(members).hasSize(1);
            Member member = members.get(0);
            softly.assertThat(member.getEmail().getValue()).isEqualTo("social.user@gmail.com");
            softly.assertThat(member.getUserType()).isSameAs(UserType.KAKAO);
            softly.assertThat(member.getProfileImage()).isEqualTo("image");
            softly.assertThat(member.getAddress()).isEqualTo(Address.defaultValue());
            softly.assertThat(member.getNickname().getValue()).matches("[a-z0-9]{8}");
        });
    }

    @Test
    @DisplayName("벤더 닉네임이 8자를 초과해도 가입에 실패하지 않는다.")
    void joins_whenVendorNicknameIsTooLong() {
        GoogleOAuth2UserProfile profile = new GoogleOAuth2UserProfile(
                "Christopher Lee", "Christopher Lee", "long.name@gmail.com", true, "image", "thumbnail"
        );

        JsonWebToken token = socialMemberService.authenticate(profile);

        assertThat(token).isNotNull();
        assertThat(memberRepository.findAll()).singleElement()
                .satisfies(member -> assertThat(member.getNickname().getValue()).hasSize(8));
    }

    @Test
    @DisplayName("벤더 닉네임이 2자 미만이어도 가입에 실패하지 않는다.")
    void joins_whenVendorNicknameIsTooShort() {
        KakaoOAuth2UserProfile profile = new KakaoOAuth2UserProfile("김", "김", "short.name@gmail.com", true, "image", "thumbnail");

        JsonWebToken token = socialMemberService.authenticate(profile);

        assertThat(token).isNotNull();
        assertThat(memberRepository.findAll()).singleElement()
                .satisfies(member -> assertThat(member.getNickname().getValue()).hasSize(8));
    }

    @Test
    @DisplayName("이미 가입된 이메일로 소셜 로그인하면 새로 가입하지 않고 기존 회원으로 토큰이 발급된다.")
    void logsInExistingMember() {
        Member existing = memberRepository.save(createRevi());
        KakaoOAuth2UserProfile profile = new KakaoOAuth2UserProfile("홍길동", "홍길동", existing.getEmail().getValue(), true, "image", "thumbnail");

        JsonWebToken token = socialMemberService.authenticate(profile);

        assertThat(token).isNotNull();
        assertThat(memberRepository.findAll()).hasSize(1);
        assertThat(memberRepository.findAll().get(0).getNickname()).isEqualTo(existing.getNickname());
    }
}
