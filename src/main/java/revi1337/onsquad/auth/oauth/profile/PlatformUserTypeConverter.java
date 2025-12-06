package revi1337.onsquad.auth.oauth.profile;

import revi1337.onsquad.member.domain.entity.vo.UserType;

public interface PlatformUserTypeConverter {

    UserType convert(PlatformUserProfile platformUserProfile);

}
