package revi1337.onsquad.auth.application.oauth2;

import revi1337.onsquad.auth.application.oauth2.model.PlatformUserProfile;
import revi1337.onsquad.member.domain.vo.UserType;

public interface PlatformUserTypeConverter {

    UserType convert(PlatformUserProfile platformUserProfile);

}
