package revi1337.onsquad.auth.oauth.application;

import revi1337.onsquad.auth.oauth.domain.PlatformUserProfile;
import revi1337.onsquad.member.domain.entity.vo.UserType;

public interface PlatformUserTypeConverter {

    UserType convert(PlatformUserProfile platformUserProfile);

}
