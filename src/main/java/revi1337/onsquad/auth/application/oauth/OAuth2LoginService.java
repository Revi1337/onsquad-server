package revi1337.onsquad.auth.application.oauth;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import revi1337.onsquad.auth.application.token.JsonWebTokenManager;
import revi1337.onsquad.auth.application.oauth.model.PlatformUserProfile;
import revi1337.onsquad.auth.application.oauth.model.PlatformUserTypeResolver;
import revi1337.onsquad.auth.application.token.model.AccessToken;
import revi1337.onsquad.auth.application.token.model.JsonWebToken;
import revi1337.onsquad.auth.application.token.model.RefreshToken;
import revi1337.onsquad.member.application.dto.MemberSummary;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.member.domain.vo.Email;

@RequiredArgsConstructor
@Service
public class OAuth2LoginService {

    private final MemberJpaRepository memberJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JsonWebTokenManager jsonWebTokenManager;
    private final PlatformUserTypeResolver platformUserTypeResolver;

    public JsonWebToken loginOAuth2User(PlatformUserProfile platformUserProfile) {
        Optional<Member> optionalMember = memberJpaRepository.findByEmail(new Email(platformUserProfile.getEmail()));
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            return generateTokenPair(member);
        }

        return forceParticipant(platformUserProfile);
    }

    private JsonWebToken forceParticipant(PlatformUserProfile platformUserProfile) {
        Member member = memberJpaRepository.save(createOAuthMember(platformUserProfile));
        return generateTokenPair(member);
    }

    private Member createOAuthMember(PlatformUserProfile platformUserProfile) {
        String encryptedPassword = passwordEncoder.encode(UUID.randomUUID().toString());
        return Member.oauth2(
                platformUserProfile.getEmail(),
                encryptedPassword,
                platformUserProfile.getNickname(),
                platformUserProfile.getProfileImage(),
                platformUserTypeResolver.resolveUserType(platformUserProfile)
        );
    }

    private JsonWebToken generateTokenPair(Member member) {
        AccessToken accessToken = jsonWebTokenManager.generateAccessToken(MemberSummary.from(member));
        RefreshToken refreshToken = jsonWebTokenManager.generateRefreshToken(member.getId());
        jsonWebTokenManager.storeRefreshTokenFor(member.getId(), refreshToken);

        return JsonWebToken.create(accessToken, refreshToken);
    }
}
