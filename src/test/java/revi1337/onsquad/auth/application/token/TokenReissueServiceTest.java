package revi1337.onsquad.auth.application.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.auth.error.exception.AuthTokenException;
import revi1337.onsquad.auth.application.token.model.JsonWebToken;
import revi1337.onsquad.auth.application.token.model.RefreshToken;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;

class TokenReissueServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private JsonWebTokenManager jsonWebTokenManager;

    @Autowired
    private TokenReissueService tokenReissueService;

    @Test
    @DisplayName("사용자가 존재하고 기존 RefreshToken 이 같으면, 재발급에 성공한다.")
    void reissueSuccess() {
        Member revi = memberJpaRepository.save(REVI());
        RefreshToken refreshToken = jsonWebTokenManager.generateRefreshToken(revi.getId());
        jsonWebTokenManager.storeRefreshTokenFor(revi.getId(), refreshToken);

        JsonWebToken reissueToken = tokenReissueService.reissue(refreshToken);

        assertThat(reissueToken.refreshToken()).isNotEqualTo(refreshToken.value());
    }

    @Test
    @DisplayName("사용자가 존재하지 않으면 재발급에 실패한다.")
    void reissueFail1() {
        Long memberId = 1L;
        RefreshToken refreshToken = jsonWebTokenManager.generateRefreshToken(memberId);

        assertThatThrownBy(() -> tokenReissueService.reissue(refreshToken))
                .isExactlyInstanceOf(AuthTokenException.NotFoundRefresh.class);
    }

    @Test
    @DisplayName("사용자가 존재하지만 기존 RefreshToken 이 다르면 재발급에 실패한다.")
    void reissueFail2() {
        Member revi = memberJpaRepository.save(REVI());
        RefreshToken refreshToken1 = jsonWebTokenManager.generateRefreshToken(revi.getId());
        jsonWebTokenManager.storeRefreshTokenFor(revi.getId(), refreshToken1);
        RefreshToken refreshToken2 = jsonWebTokenManager.generateRefreshToken(revi.getId());

        assertThatThrownBy(() -> tokenReissueService.reissue(refreshToken2))
                .isExactlyInstanceOf(AuthTokenException.NotFoundRefresh.class);
    }
}