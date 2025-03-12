package revi1337.onsquad.auth.application.oauth2;

import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.application.oauth2.model.GoogleUserProfile;
import revi1337.onsquad.auth.application.oauth2.model.PlatformUserProfile;
import revi1337.onsquad.member.domain.vo.UserType;

@Component
public class GoogleUserProfileConverter implements PlatformUserTypeConverter {

    @Override
    public UserType convert(PlatformUserProfile platformUserProfile) {
        return platformUserProfile instanceof GoogleUserProfile ? UserType.GOOGLE : null;
    }
}
