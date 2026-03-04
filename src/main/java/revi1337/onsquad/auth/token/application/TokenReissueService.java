package revi1337.onsquad.auth.token.application;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.auth.token.domain.model.ClaimsParser;
import revi1337.onsquad.auth.token.domain.model.JsonWebToken;
import revi1337.onsquad.member.application.MemberAccessor;
import revi1337.onsquad.member.application.dto.MemberSummary;
import revi1337.onsquad.member.domain.entity.Member;

@Service
@RequiredArgsConstructor
public class TokenReissueService {

    private final MemberAccessor memberAccessor;
    private final JsonWebTokenManager jsonWebTokenManager;

    public JsonWebToken reissue(String refreshToken) {
        ClaimsParser claimsParser = jsonWebTokenManager.verifyRefreshToken(refreshToken);
        Long memberId = claimsParser.parseIdentity();

        return generateAndStoreNewTokenPair(memberId);
    }

    private JsonWebToken generateAndStoreNewTokenPair(Long memberId) {
        Member member = memberAccessor.getById(memberId);
        Instant now = Instant.now();

        return jsonWebTokenManager.issueJsonWebToken(MemberSummary.from(member), now);
    }
}
