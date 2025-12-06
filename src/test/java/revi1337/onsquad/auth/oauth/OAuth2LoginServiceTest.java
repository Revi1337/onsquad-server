package revi1337.onsquad.auth.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_PROFILE_IMAGE_LINK;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import revi1337.onsquad.auth.oauth.profile.user.KakaoUserProfile;
import revi1337.onsquad.common.ApplicationLayerWithTestContainerSupport;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.token.domain.model.JsonWebToken;

class OAuth2LoginServiceTest extends ApplicationLayerWithTestContainerSupport {

    @SpyBean
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private OAuth2LoginService oAuth2LoginService;

    @Nested
    @DisplayName("카카오 로그인을 테스트한다.")
    class LoginOAuth2User {

        @Test
        @DisplayName("존재하지 않는 회원이면 강제 회원가입에 성공한다.")
        void success1() {
            KakaoUserProfile kakaoUserProfile = new KakaoUserProfile(
                    REVI_NICKNAME_VALUE,
                    REVI_NICKNAME_VALUE,
                    REVI_EMAIL_VALUE,
                    true,
                    REVI_PROFILE_IMAGE_LINK,
                    REVI_PROFILE_IMAGE_LINK
            );

            JsonWebToken jsonWebToken = oAuth2LoginService.loginOAuth2User(kakaoUserProfile);

            assertThat(jsonWebToken).isNotNull();
            verify(memberJpaRepository, times(1)).save(any(Member.class));
        }

        @Test
        @DisplayName("이미 존재하는 회원이면 기존 회원으로 로그인한다.")
        void success2() {
            memberJpaRepository.save(REVI());
            KakaoUserProfile kakaoUserProfile = new KakaoUserProfile(
                    REVI_NICKNAME_VALUE,
                    REVI_NICKNAME_VALUE,
                    REVI_EMAIL_VALUE,
                    true,
                    REVI_PROFILE_IMAGE_LINK,
                    REVI_PROFILE_IMAGE_LINK
            );
            reset(memberJpaRepository);

            JsonWebToken jsonWebToken = oAuth2LoginService.loginOAuth2User(kakaoUserProfile);

            assertThat(jsonWebToken).isNotNull();
            verify(memberJpaRepository, never()).save(any(Member.class));
        }
    }
}
