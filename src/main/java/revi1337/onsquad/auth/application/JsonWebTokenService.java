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
import java.util.Optional;

import static revi1337.onsquad.auth.error.TokenErrorCode.*;

@RequiredArgsConstructor
@Service
public class JsonWebTokenService {

    private static final String ACCESS_TOKEN_SUBJECT = "access_token";
    private static final String REFRESH_TOKEN_SUBJECT = "refresh_token";

    private final JsonWebTokenProvider jsonWebTokenProvider;
    private final JsonWebTokenEvaluator jsonWebTokenEvaluator;
    private final RefreshTokenManager redisHashRefreshTokenManager;
    private final MemberRepository memberRepository;

    public JsonWebTokenResponse generateTokenPair(MemberDto dto) {
        return JsonWebTokenResponse.of(
                generateAccessToken(dto),
                generateRefreshToken(dto)
        );
    }

    public void storeTemporaryTokenInMemory(RefreshToken refreshToken, Long memberId) {
        redisHashRefreshTokenManager.storeTemporaryToken(refreshToken, memberId);
    }

    public JsonWebTokenResponse reissueToken(RefreshToken refreshToken) {
        Claims claims = jsonWebTokenEvaluator.verifyRefreshToken(refreshToken.value());
        return Optional.ofNullable(claims.get("memberId", Long.class))
                .flatMap(memberId -> redisHashRefreshTokenManager.findTemporaryToken(memberId)
                        .filter(token -> token.equals(refreshToken))
                        .map(token -> restoreTemporaryTokenAndCreateTokenPair(memberId)))
                .orElseThrow(() -> new AuthTokenException.NotFoundRefresh(NOT_FOUND_REFRESH));
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

    private JsonWebTokenResponse restoreTemporaryTokenAndCreateTokenPair(Long tokenOwnerId) {
        return memberRepository.findById(tokenOwnerId)
                .map(member -> {
                    JsonWebTokenResponse jsonWebTokenResponse = generateTokenPair(MemberDto.from(member));
                    updateTemporaryToken(tokenOwnerId, jsonWebTokenResponse.refreshToken());
                    return jsonWebTokenResponse;
                })
                .orElseThrow(() -> new AuthTokenException.NotFoundRefreshOwner(NOT_FOUND_REFRESH_MEMBER));
    }

    private void updateTemporaryToken(Long tokenOwnerId, RefreshToken newRefreshToken) {
        redisHashRefreshTokenManager.updateTemporaryToken(tokenOwnerId, newRefreshToken);
    }
}
