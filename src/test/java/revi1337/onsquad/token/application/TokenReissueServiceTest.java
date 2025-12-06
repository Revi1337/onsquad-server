package revi1337.onsquad.token.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerWithTestContainerSupport;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.token.domain.model.JsonWebToken;
import revi1337.onsquad.token.domain.model.RefreshToken;
import revi1337.onsquad.token.error.TokenException;

class TokenReissueServiceTest extends ApplicationLayerWithTestContainerSupport {

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
                .isExactlyInstanceOf(TokenException.NotFoundRefresh.class);
    }

    @Test
    @DisplayName("사용자가 존재하지만 기존 RefreshToken 이 다르면 재발급에 실패한다.")
    void reissueFail2() {
        Member revi = memberJpaRepository.save(REVI());
        RefreshToken refreshToken1 = jsonWebTokenManager.generateRefreshToken(revi.getId());
        jsonWebTokenManager.storeRefreshTokenFor(revi.getId(), refreshToken1);
        RefreshToken refreshToken2 = jsonWebTokenManager.generateRefreshToken(revi.getId());

        assertThatThrownBy(() -> tokenReissueService.reissue(refreshToken2))
                .isExactlyInstanceOf(TokenException.NotFoundRefresh.class);
    }
}
