package revi1337.onsquad.auth.application.oauth;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import revi1337.onsquad.auth.application.token.JsonWebTokenService;
import revi1337.onsquad.auth.model.oauth.PlatformUserProfile;
import revi1337.onsquad.auth.model.oauth.PlatformUserTypeResolver;
import revi1337.onsquad.auth.model.token.JsonWebToken;
import revi1337.onsquad.member.application.dto.MemberDto;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.UserType;

@RequiredArgsConstructor
@Service
public class OAuth2LoginService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JsonWebTokenService jsonWebTokenService;
    private final PlatformUserTypeResolver platformUserTypeResolver;

    public JsonWebToken loginOAuth2User(PlatformUserProfile platformUserProfile) {
        return memberRepository.findByEmail(new Email(platformUserProfile.getEmail()))
                .map(member -> jsonWebTokenService.generateTokenPair(MemberDto.from(member)))
                .orElseGet(() -> forceParticipant(platformUserProfile));
    }

    private JsonWebToken forceParticipant(PlatformUserProfile platformUserProfile) {
        String encryptedPassword = passwordEncoder.encode(UUID.randomUUID().toString());
        UserType userType = platformUserTypeResolver.resolveUserType(platformUserProfile);
        Member member = Member.createOAuth2User(platformUserProfile.getEmail(), platformUserProfile.getNickname(),
                platformUserProfile.getProfileImage(), encryptedPassword, userType);
        memberRepository.save(member);

        JsonWebToken jsonWebToken = jsonWebTokenService.generateTokenPair(MemberDto.from(member));
        jsonWebTokenService.saveRefreshToken(jsonWebToken.refreshToken(), member.getId());
        return jsonWebToken;
    }
}
