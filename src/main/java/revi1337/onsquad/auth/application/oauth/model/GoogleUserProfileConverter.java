package revi1337.onsquad.auth.application.oauth.model;

import org.springframework.stereotype.Component;
import revi1337.onsquad.member.domain.vo.UserType;

@Component
public class GoogleUserProfileConverter implements PlatformUserTypeConverter {

    @Override
    public UserType convert(PlatformUserProfile platformUserProfile) {
        return platformUserProfile instanceof GoogleUserProfile ? UserType.GOOGLE : null;
    }
}
