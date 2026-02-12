package revi1337.onsquad.auth.oauth.infrastructure.kakao;

import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.oauth.application.PlatformUserTypeConverter;
import revi1337.onsquad.auth.oauth.domain.PlatformUserProfile;
import revi1337.onsquad.member.domain.entity.vo.UserType;

@Component
public class KakaoUserProfileConverter implements PlatformUserTypeConverter {

    @Override
    public UserType convert(PlatformUserProfile platformUserProfile) {
        return platformUserProfile instanceof KakaoUserProfile ? UserType.KAKAO : null;
    }
}
