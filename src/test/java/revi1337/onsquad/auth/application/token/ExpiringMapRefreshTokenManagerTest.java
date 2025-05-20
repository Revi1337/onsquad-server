package revi1337.onsquad.auth.application.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static revi1337.onsquad.common.fixture.TokenFixture.REFRESH_TOKEN;

import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.auth.config.properties.TokenProperties;
import revi1337.onsquad.auth.config.properties.TokenProperties.RefreshTokenAttributes;
import revi1337.onsquad.auth.config.properties.TokenProperties.TokenAttributes;
import revi1337.onsquad.auth.application.token.model.RefreshToken;
import revi1337.onsquad.auth.repository.token.ExpiringMapTokenRepository;

@ExtendWith(MockitoExtension.class)
class ExpiringMapRefreshTokenManagerTest {

    @Mock
    private TokenProperties tokenProperties;

    @Mock
    private RefreshTokenAttributes refreshTokenAttributes;

    @Mock
    private TokenAttributes tokenAttributes;

    @Mock
    private ExpiringMapTokenRepository repository;

    @InjectMocks
    private ExpiringMapRefreshTokenManager tokenManager;

    @Test
    @DisplayName("RefreshToken 저장에 성공한다.")
    void saveTokenFor() {
        Long MEMBER_ID = 1L;
        Duration DURATION = Duration.ofSeconds(300);
        when(tokenProperties.refreshTokenAttributes()).thenReturn(refreshTokenAttributes);
        when(refreshTokenAttributes.tokenAttributes()).thenReturn(tokenAttributes);
        when(tokenAttributes.expired()).thenReturn(DURATION);

        tokenManager.saveTokenFor(MEMBER_ID, REFRESH_TOKEN);

        verify(repository).save(REFRESH_TOKEN, MEMBER_ID, DURATION);
    }

    @Test
    @DisplayName("RefreshToken 조회에 성공한다.")
    void findTokenBy() {
        Long MEMBER_ID = 1L;
        when(repository.findBy(MEMBER_ID)).thenReturn(Optional.of(REFRESH_TOKEN));

        Optional<RefreshToken> RESULT = tokenManager.findTokenBy(MEMBER_ID);

        assertThat(RESULT).isPresent().contains(REFRESH_TOKEN);
    }

    @Test
    @DisplayName("RefreshToken 삭제에 성공한다.")
    void deleteTokenBy() {
        Long MEMBER_ID = 1L;

        tokenManager.deleteTokenBy(MEMBER_ID);

        verify(repository).deleteBy(MEMBER_ID);
    }
}
