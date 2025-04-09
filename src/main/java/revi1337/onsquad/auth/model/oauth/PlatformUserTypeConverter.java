package revi1337.onsquad.auth.model.oauth;

import revi1337.onsquad.member.domain.vo.UserType;

public interface PlatformUserTypeConverter {

    UserType convert(PlatformUserProfile platformUserProfile);

}
