package revi1337.onsquad.auth.oauth.application;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import revi1337.onsquad.auth.oauth.application.contract.OAuth2VendorUserProfile;
import revi1337.onsquad.member.application.dto.MemberSummary;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.entity.vo.Email;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.token.application.JsonWebTokenManager;
import revi1337.onsquad.token.domain.model.AccessToken;
import revi1337.onsquad.token.domain.model.JsonWebToken;
import revi1337.onsquad.token.domain.model.RefreshToken;

@Service
@RequiredArgsConstructor
public class SocialMemberService {

    private final MemberJpaRepository memberJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JsonWebTokenManager jsonWebTokenManager;

    public JsonWebToken authenticate(OAuth2VendorUserProfile vendorUserProfile) {
        Optional<Member> optionalMember = memberJpaRepository.findByEmail(new Email(vendorUserProfile.getEmail()));
        if (optionalMember.isPresent()) {
            return issueTokens(optionalMember.get());
        }

        return join(vendorUserProfile);
    }

    private JsonWebToken join(OAuth2VendorUserProfile vendorUserProfile) {
        Member member = memberJpaRepository.save(createOAuthMember(vendorUserProfile));
        return issueTokens(member);
    }

    private Member createOAuthMember(OAuth2VendorUserProfile vendorUserProfile) {
        String encryptedPassword = passwordEncoder.encode(UUID.randomUUID().toString());
        return Member.oauth2(
                vendorUserProfile.getEmail(),
                encryptedPassword,
                vendorUserProfile.getNickname(),
                vendorUserProfile.getProfileImage(),
                vendorUserProfile.getUserType()
        );
    }

    private JsonWebToken issueTokens(Member member) {
        AccessToken accessToken = jsonWebTokenManager.generateAccessToken(MemberSummary.from(member));
        RefreshToken refreshToken = jsonWebTokenManager.generateRefreshToken(member.getId());
        jsonWebTokenManager.storeRefreshTokenFor(member.getId(), refreshToken);

        return JsonWebToken.create(accessToken, refreshToken);
    }
}
