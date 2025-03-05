package revi1337.onsquad.auth.application.oauth2;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import revi1337.onsquad.auth.application.JsonWebTokenService;
import revi1337.onsquad.auth.application.dto.JsonWebToken;
import revi1337.onsquad.auth.application.oauth2.model.GoogleUserProfile;
import revi1337.onsquad.auth.application.oauth2.model.KakaoUserProfile;
import revi1337.onsquad.auth.application.oauth2.model.PlatformUserProfile;
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

    public JsonWebToken loginOAuth2User(PlatformUserProfile platformUserProfile) {
        return memberRepository.findByEmail(new Email(platformUserProfile.getEmail()))
                .map(member -> jsonWebTokenService.generateTokenPair(MemberDto.from(member)))
                .orElseGet(() -> forceParticipant(platformUserProfile));
    }

    private JsonWebToken forceParticipant(PlatformUserProfile platformUserProfile) {
        String encryptedPassword = passwordEncoder.encode(UUID.randomUUID().toString());
        UserType userType = guessUserType(platformUserProfile);
        Member member = Member.createOAuth2User(platformUserProfile.getEmail(), platformUserProfile.getNickname(),
                platformUserProfile.getProfileImage(), encryptedPassword, userType);
        memberRepository.save(member);

        JsonWebToken jsonWebToken = jsonWebTokenService.generateTokenPair(MemberDto.from(member));
        jsonWebTokenService.storeTemporaryTokenInMemory(jsonWebToken.refreshToken(), member.getId());
        return jsonWebToken;
    }

    private UserType guessUserType(PlatformUserProfile platformUserProfile) {
        UserType userType = UserType.GENERAL;
        if (platformUserProfile instanceof GoogleUserProfile) {
            userType = UserType.GOOGLE;
        } else if (platformUserProfile instanceof KakaoUserProfile) {
            userType = UserType.KAKAO;
        }
        return userType;
    }
}
