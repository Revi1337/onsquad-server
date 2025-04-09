package revi1337.onsquad.auth.model.oauth;

import org.springframework.stereotype.Component;
import revi1337.onsquad.member.domain.vo.UserType;

@Component
public class KakaoUserProfileConverter implements PlatformUserTypeConverter {

    @Override
    public UserType convert(PlatformUserProfile platformUserProfile) {
        return platformUserProfile instanceof KakaoUserProfile ? UserType.KAKAO : null;
    }
}
