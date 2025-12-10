package revi1337.onsquad.token.application;

import static revi1337.onsquad.token.error.TokenErrorCode.NOT_FOUND_REFRESH;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import revi1337.onsquad.member.application.dto.MemberSummary;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;
import revi1337.onsquad.member.error.MemberErrorCode;
import revi1337.onsquad.member.error.exception.MemberBusinessException;
import revi1337.onsquad.token.domain.model.AccessToken;
import revi1337.onsquad.token.domain.model.JsonWebToken;
import revi1337.onsquad.token.domain.model.RefreshToken;
import revi1337.onsquad.token.error.TokenException;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenReissueService {

    private static final String REISSUE_LOG_FORMAT = "[사용자 인증 토큰 재발급 완료] - id : {}, email : {}";

    private final JsonWebTokenEvaluator jsonWebTokenEvaluator;
    private final JsonWebTokenManager jsonWebTokenManager;
    private final MemberRepository memberRepository;

    public JsonWebToken reissue(RefreshToken refreshToken) {
        ClaimsParser claimsParser = jsonWebTokenEvaluator.verifyRefreshToken(refreshToken.value());
        Long memberId = claimsParser.parseIdentity();
        Optional<RefreshToken> optionalRefreshToken = jsonWebTokenManager.findRefreshTokenBy(memberId);
        if (optionalRefreshToken.isEmpty() || !optionalRefreshToken.get().equals(refreshToken)) {
            throw new TokenException.NotFoundRefresh(NOT_FOUND_REFRESH);
        }

        return generateAndStoreNewTokenPair(memberId);
    }

    private JsonWebToken generateAndStoreNewTokenPair(Long memberId) {
        Member member = validateMemberExistsAndGet(memberId);
        AccessToken accessToken = jsonWebTokenManager.generateAccessToken(MemberSummary.from(member));
        RefreshToken refreshToken = jsonWebTokenManager.generateRefreshToken(memberId);
        jsonWebTokenManager.storeRefreshTokenFor(memberId, refreshToken);
        log.info(REISSUE_LOG_FORMAT, member.getId(), member.getEmail().getValue());

        return JsonWebToken.create(accessToken, refreshToken);
    }

    private Member validateMemberExistsAndGet(Long memberId) { // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberBusinessException.NotFound(MemberErrorCode.NOT_FOUND));
    }
}
