package revi1337.onsquad.auth.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.auth.dto.response.AccessToken;
import revi1337.onsquad.auth.dto.response.JsonWebTokenResponse;
import revi1337.onsquad.auth.dto.response.RefreshToken;
import revi1337.onsquad.member.dto.MemberDto;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@DisplayName("JsonWebTokenService 테스트")
@ExtendWith(MockitoExtension.class)
class JsonWebTokenServiceTest {

    @Mock JsonWebTokenProvider jsonWebTokenProvider;
    @Mock RefreshTokenStoreService refreshTokenStoreService;
    @InjectMocks JsonWebTokenService jsonWebTokenService;

    @DisplayName("AccessToken 과 RefreshToken 을 포함한 dto 가 만들어지는지 확인한다.")
    @Test
    public void generateTokenPairResponse() {
        // given
        AccessToken accessToken = AccessToken.of("ACCESS-TOKEN");
        RefreshToken refreshToken = RefreshToken.of("REFRESH-TOKEN");
        given(jsonWebTokenProvider.generateAccessToken(anyString(), anyMap())).willReturn(accessToken);
        given(jsonWebTokenProvider.generateRefreshToken(anyString(), anyMap())).willReturn(refreshToken);

        // when
        JsonWebTokenResponse jsonWebTokenResponse = jsonWebTokenService.generateTokenPairResponse(Collections.singletonMap("identifier", "1"));

        // then
        assertThat(jsonWebTokenResponse.refreshToken()).isEqualTo(refreshToken);
        assertThat(jsonWebTokenResponse.accessToken()).isEqualTo(accessToken);
    }

    @DisplayName("RefreshToken 의 저장은 refreshTokenStoreService 에게 위임한다.")
    @Test
    public void storeTemporaryTokenInMemory() {
        // given
        RefreshToken refreshToken = RefreshToken.of("REFRESH-TOKEN");
        MemberDto memberDto = MemberDto.builder().id(10L).build();

        // when
        jsonWebTokenService.storeTemporaryTokenInMemory(refreshToken, memberDto);

        // then
        then(refreshTokenStoreService).should(times(1))
                .storeTemporaryToken(refreshToken, memberDto.getId());
    }
}