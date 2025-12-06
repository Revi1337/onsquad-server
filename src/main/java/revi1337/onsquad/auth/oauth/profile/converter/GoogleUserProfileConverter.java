package revi1337.onsquad.auth.oauth.profile.converter;

import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.oauth.profile.PlatformUserProfile;
import revi1337.onsquad.auth.oauth.profile.PlatformUserTypeConverter;
import revi1337.onsquad.auth.oauth.profile.user.GoogleUserProfile;
import revi1337.onsquad.member.domain.entity.vo.UserType;

@Component
public class GoogleUserProfileConverter implements PlatformUserTypeConverter {

    @Override
    public UserType convert(PlatformUserProfile platformUserProfile) {
        return platformUserProfile instanceof GoogleUserProfile ? UserType.GOOGLE : null;
    }
}
