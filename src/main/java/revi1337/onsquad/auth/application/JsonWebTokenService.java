package revi1337.onsquad.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.auth.dto.response.JsonWebTokenResponse;
import revi1337.onsquad.auth.dto.response.RefreshToken;
import revi1337.onsquad.member.dto.MemberDto;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class JsonWebTokenService {

    private final JsonWebTokenProvider jsonWebTokenProvider;
    private final RefreshTokenStoreService refreshTokenStoreService;

    private static final String ACCESS_TOKEN_SUBJECT = "access_token";
    private static final String REFRESH_TOKEN_SUBJECT = "refresh_token";

    public JsonWebTokenResponse generateTokenPairResponse(Map<String, ?> extraClaims) {
        return JsonWebTokenResponse.of(
                jsonWebTokenProvider.generateAccessToken(ACCESS_TOKEN_SUBJECT, extraClaims),
                jsonWebTokenProvider.generateRefreshToken(REFRESH_TOKEN_SUBJECT, Collections.emptyMap())
        );
    }

    public void storeTemporaryTokenInMemory(RefreshToken refreshToken, MemberDto memberDto) {
        refreshTokenStoreService.storeTemporaryToken(refreshToken, memberDto.getId());
    }
}
