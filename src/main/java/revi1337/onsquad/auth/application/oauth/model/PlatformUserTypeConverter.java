package revi1337.onsquad.auth.application.oauth.model;

import revi1337.onsquad.member.domain.vo.UserType;

public interface PlatformUserTypeConverter {

    UserType convert(PlatformUserProfile platformUserProfile);

}
