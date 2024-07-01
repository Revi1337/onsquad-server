package revi1337.onsquad.auth.application;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.auth.domain.vo.AccessToken;
import revi1337.onsquad.auth.domain.vo.RefreshToken;
import revi1337.onsquad.auth.dto.response.JsonWebTokenResponse;
import revi1337.onsquad.auth.error.exception.AuthTokenException;
import revi1337.onsquad.member.dto.MemberDto;
import revi1337.onsquad.member.domain.MemberRepository;

import java.util.Collections;
import java.util.HashMap;

import static revi1337.onsquad.auth.error.TokenErrorCode.*;

@RequiredArgsConstructor
@Service
public class JsonWebTokenService {

    private static final String ACCESS_TOKEN_SUBJECT = "access_token";
    private static final String REFRESH_TOKEN_SUBJECT = "refresh_token";

    private final JsonWebTokenProvider jsonWebTokenProvider;
    private final JsonWebTokenEvaluator jsonWebTokenEvaluator;
    private final RefreshTokenManager refreshTokenManager;
    private final MemberRepository memberRepository;

    public JsonWebTokenResponse generateTokenPair(MemberDto dto) {
        return JsonWebTokenResponse.of(
                generateAccessToken(dto),
                generateRefreshToken(dto)
        );
    }

    public void storeTemporaryTokenInMemory(RefreshToken refreshToken, Long memberId) {
        refreshTokenManager.storeTemporaryToken(refreshToken, memberId);
    }

    public JsonWebTokenResponse reissueToken(RefreshToken refreshToken) {
        return refreshTokenManager.findTemporaryToken(refreshToken)
                .filter(tokenOwnerId -> isTokenOwnerValid(tokenOwnerId, refreshToken))
                .map(tokenOwnerId -> restoreTemporaryTokenAndCreateTokenPair(tokenOwnerId, refreshToken))
                .orElseThrow(() -> new AuthTokenException.InvalidRefreshOwner(INVALID_REFRESH_OWNER));
    }

    private AccessToken generateAccessToken(MemberDto dto) {
        return jsonWebTokenProvider.generateAccessToken(
                ACCESS_TOKEN_SUBJECT,
                new HashMap<>() {
                    {
                        put("memberId", dto.getId());
                        put("nickname", dto.getNickname().getValue());
                        put("email", dto.getEmail().getValue());
                        put("userType", dto.getUserType().getText());
                    }
                }
        );
    }

    private RefreshToken generateRefreshToken(MemberDto dto) {
        return jsonWebTokenProvider.generateRefreshToken(
                REFRESH_TOKEN_SUBJECT,
                Collections.singletonMap("memberId", dto.getId())
        );
    }

    private boolean isTokenOwnerValid(Long tokenOwnerId, RefreshToken refreshToken) {
        Claims claims = jsonWebTokenEvaluator.verifyRefreshToken(refreshToken.value());
        Long claimMemberId = claims.get("memberId", Long.class);
        return tokenOwnerId.equals(claimMemberId);
    }

    private JsonWebTokenResponse restoreTemporaryTokenAndCreateTokenPair(Long tokenOwnerId, RefreshToken refreshToken) {
        return memberRepository.findById(tokenOwnerId)
                .map(member -> {
                    JsonWebTokenResponse jsonWebTokenResponse = generateTokenPair(MemberDto.from(member));
                    restoreTemporaryToken(refreshToken, jsonWebTokenResponse.refreshToken(), tokenOwnerId);
                    return jsonWebTokenResponse;
                })
                .orElseThrow(() -> new AuthTokenException.NotFoundRefreshOwner(NOT_FOUND_REFRESH_MEMBER));
    }

    private void restoreTemporaryToken(RefreshToken refreshToken, RefreshToken newRefreshToken, Long tokenOwnerId) {
        removeTemporaryTokenInMemory(refreshToken);
        storeTemporaryTokenInMemory(newRefreshToken, tokenOwnerId);
    }

    private void removeTemporaryTokenInMemory(RefreshToken refreshToken) {
        refreshTokenManager.removeTemporaryToken(refreshToken);
    }
}
