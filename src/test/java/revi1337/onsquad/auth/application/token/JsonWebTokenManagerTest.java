package revi1337.onsquad.auth.application.token;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_USER_TYPE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.auth.application.token.model.AccessToken;
import revi1337.onsquad.auth.application.token.model.RefreshToken;
import revi1337.onsquad.auth.repository.token.ExpiringMapTokenRepository;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.member.application.dto.MemberSummary;

class JsonWebTokenManagerTest extends ApplicationLayerTestSupport {

    @Autowired
    private ExpiringMapTokenRepository tokenRepository;

    @Autowired
    private JsonWebTokenManager jsonWebTokenManager;

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
    }

    @Test
    @DisplayName("AccessToken 생성에 성공한다.")
    void GenerateAccessToken() {
        MemberSummary SUMMARY = new MemberSummary(1L, REVI_EMAIL_VALUE, null, REVI_USER_TYPE);

        AccessToken ACCESS_TOKEN = jsonWebTokenManager.generateAccessToken(SUMMARY);

        assertThat(ACCESS_TOKEN).isNotNull();
    }

    @Test
    @DisplayName("RefreshToken 생성에 성공한다.")
    void GenerateRefreshToken() {
        Long MEMBER_ID = 1L;

        RefreshToken REFRESH_TOKEN = jsonWebTokenManager.generateRefreshToken(MEMBER_ID);

        assertThat(REFRESH_TOKEN).isNotNull();
    }

    @Test
    @DisplayName("RefreshToken 저장에 성공한다.")
    void StoreRefreshTokenFor() {
        Long MEMBER_ID = 1L;
        RefreshToken REFRESH_TOKEN = jsonWebTokenManager.generateRefreshToken(MEMBER_ID);

        jsonWebTokenManager.storeRefreshTokenFor(MEMBER_ID, REFRESH_TOKEN);

        assertThat(tokenRepository.findBy(MEMBER_ID)).isPresent();
    }
}
