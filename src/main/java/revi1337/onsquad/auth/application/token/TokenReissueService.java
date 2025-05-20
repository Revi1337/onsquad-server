package revi1337.onsquad.auth.application.token;

import static revi1337.onsquad.auth.error.TokenErrorCode.NOT_FOUND_REFRESH;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import revi1337.onsquad.auth.error.exception.AuthTokenException;
import revi1337.onsquad.auth.model.token.AccessToken;
import revi1337.onsquad.auth.model.token.JsonWebToken;
import revi1337.onsquad.auth.model.token.RefreshToken;
import revi1337.onsquad.member.application.dto.MemberSummary;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenReissueService {

    private static final String REISSUE_LOG_FORMAT = "[사용자 인증 토큰 재발급 완료] - id : {}, email : {}";

    private final JsonWebTokenEvaluator jsonWebTokenEvaluator;
    private final JsonWebTokenManager jsonWebTokenManager;
    private final RefreshTokenManager expiringMapRefreshTokenManager;
    private final MemberRepository memberRepository;

    public JsonWebToken reissue(RefreshToken refreshToken) {
        ClaimsParser claimsParser = jsonWebTokenEvaluator.verifyRefreshToken(refreshToken.value());
        Long memberId = claimsParser.parseIdentity();
        Optional<RefreshToken> optionalRefreshToken = expiringMapRefreshTokenManager.findTokenBy(memberId);
        if (optionalRefreshToken.isEmpty() || !optionalRefreshToken.get().equals(refreshToken)) {
            throw new AuthTokenException.NotFoundRefresh(NOT_FOUND_REFRESH);
        }

        return generateAndStoreNewTokenPair(memberId);
    }

    private JsonWebToken generateAndStoreNewTokenPair(Long memberId) {
        Member member = memberRepository.getById(memberId);
        AccessToken accessToken = jsonWebTokenManager.generateAccessToken(MemberSummary.from(member));
        RefreshToken refreshToken = jsonWebTokenManager.generateRefreshToken(memberId);
        expiringMapRefreshTokenManager.saveTokenFor(memberId, refreshToken);
        log.info(REISSUE_LOG_FORMAT, member.getId(), member.getEmail().getValue());

        return JsonWebToken.create(accessToken, refreshToken);
    }
}
