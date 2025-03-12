package revi1337.onsquad.auth.application.oauth2;

import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.application.oauth2.model.KakaoUserProfile;
import revi1337.onsquad.auth.application.oauth2.model.PlatformUserProfile;
import revi1337.onsquad.member.domain.vo.UserType;

@Component
public class KakaoUserProfileConverter implements PlatformUserTypeConverter {

    @Override
    public UserType convert(PlatformUserProfile platformUserProfile) {
        return platformUserProfile instanceof KakaoUserProfile ? UserType.KAKAO : null;
    }
}
