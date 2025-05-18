package revi1337.onsquad.auth.application.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI_WITH_ID;
import static revi1337.onsquad.common.fixture.TokenFixture.ACCESS_TOKEN_SUBJECT;
import static revi1337.onsquad.common.fixture.TokenFixture.REFRESH_TOKEN_SUBJECT;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import revi1337.onsquad.auth.error.exception.AuthTokenException;
import revi1337.onsquad.auth.model.token.AccessToken;
import revi1337.onsquad.auth.model.token.JsonWebToken;
import revi1337.onsquad.auth.model.token.RefreshToken;
import revi1337.onsquad.auth.repository.token.ExpiringMapTokenRepository;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.member.application.dto.MemberDto;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

class JsonWebTokenServiceTest extends ApplicationLayerTestSupport {

    @SpyBean
    private MemberRepository memberRepository;

    @SpyBean
    private JsonWebTokenProvider jsonWebTokenProvider;

    @SpyBean
    private ExpiringMapRefreshTokenManager tokenManager;

    @Autowired
    private ExpiringMapTokenRepository tokenRepository;

    @Autowired
    private JsonWebTokenService jsonWebTokenService;

    @BeforeEach
    void setUp() {
        tokenRepository.deleteAll();
    }

    @Nested
    @DisplayName("AccessToken & RefreshToken 생성을 테스트한다.")
    class GenerateTokenPair {

        @Test
        @DisplayName("AccessToken & RefreshToken 생성에 성공한다.")
        void success() {
            MemberDto DTO = MemberDto.from(REVI_WITH_ID(1L));
            AccessToken ACCESS_TOKEN = jsonWebTokenProvider.generateAccessToken(ACCESS_TOKEN_SUBJECT, new HashMap<>() {{
                put("memberId", DTO.getId());
                put("email", DTO.getEmail().getValue());
                put("userType", DTO.getUserType().getText());
            }});
            RefreshToken REFRESH_TOKEN = jsonWebTokenProvider.generateRefreshToken(
                    REFRESH_TOKEN_SUBJECT, Collections.singletonMap("memberId", DTO.getId())
            );
            when(jsonWebTokenProvider.generateAccessToken(eq(ACCESS_TOKEN_SUBJECT), anyMap())).thenReturn(ACCESS_TOKEN);
            when(jsonWebTokenProvider.generateRefreshToken(eq(REFRESH_TOKEN_SUBJECT), anyMap())).thenReturn(
                    REFRESH_TOKEN);

            JsonWebToken TOKEN = jsonWebTokenService.generateTokenPair(DTO);

            assertAll(() -> {
                assertThat(TOKEN.refreshToken()).isEqualTo(REFRESH_TOKEN);
                assertThat(TOKEN.accessToken()).isEqualTo(ACCESS_TOKEN);
            });
        }
    }

    @Nested
    @DisplayName("RefreshToken 저장을 테스트한다.")
    class SaveRefreshToken {

        @Test
        @DisplayName("RefreshToken 저장에 성공한다.")
        void success() {
            Member REVI = REVI_WITH_ID(1L);
            MemberDto DTO = MemberDto.from(REVI);
            RefreshToken REFRESH_TOKEN = jsonWebTokenProvider.generateRefreshToken(
                    REFRESH_TOKEN_SUBJECT, Collections.singletonMap("memberId", DTO.getId())
            );

            jsonWebTokenService.saveRefreshToken(REFRESH_TOKEN, REVI.getId());

            Optional<RefreshToken> FIND_TOKEN = tokenManager.findTokenBy(REVI.getId());
            assertThat(FIND_TOKEN).isPresent();
            assertThat(FIND_TOKEN.get()).isEqualTo(REFRESH_TOKEN);
            verify(tokenManager).saveToken(eq(REFRESH_TOKEN), eq(REVI.getId()));
        }
    }

    @Nested
    @DisplayName("AccessToken & RefreshToken 재발급을 테스트한다.")
    class ReIssueRefreshToken {

        @Test
        @DisplayName("AccessToken & RefreshToken 재발급에 성공한다.")
        void success() {
            Member REVI = memberRepository.save(REVI());
            MemberDto DTO = MemberDto.from(REVI);
            AccessToken ACCESS_TOKEN = jsonWebTokenProvider.generateAccessToken(ACCESS_TOKEN_SUBJECT, new HashMap<>() {{
                put("memberId", DTO.getId());
                put("email", DTO.getEmail().getValue());
                put("userType", DTO.getUserType().getText());
            }});
            RefreshToken REFRESH_TOKEN = jsonWebTokenProvider.generateRefreshToken(
                    REFRESH_TOKEN_SUBJECT, Collections.singletonMap("memberId", DTO.getId())
            );
            tokenManager.saveToken(REFRESH_TOKEN, DTO.getId());

            // when
            JsonWebToken REISSUE_TOKEN = jsonWebTokenService.reissueToken(REFRESH_TOKEN);

            // then
            assertThat(REISSUE_TOKEN.accessToken().value()).isNotEqualTo(ACCESS_TOKEN.value());
            assertThat(REISSUE_TOKEN.refreshToken().value()).isNotEqualTo(REFRESH_TOKEN.value());
        }

        @Test
        @DisplayName("RefreshToken 가 저장되어 있지 않으면, AccessToken & RefreshToken 재발급에 실패한다.")
        void fail() {
            Member REVI = memberRepository.save(REVI());
            MemberDto DTO = MemberDto.from(REVI);
            RefreshToken REFRESH_TOKEN = jsonWebTokenProvider.generateRefreshToken(
                    REFRESH_TOKEN_SUBJECT, Collections.singletonMap("memberId", DTO.getId())
            );

            assertThatThrownBy(() -> jsonWebTokenService.reissueToken(REFRESH_TOKEN))
                    .isExactlyInstanceOf(AuthTokenException.NotFoundRefresh.class);
        }
    }
}
