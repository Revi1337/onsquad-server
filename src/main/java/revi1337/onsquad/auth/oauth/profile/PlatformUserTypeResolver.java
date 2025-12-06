package revi1337.onsquad.auth.oauth.profile;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.member.domain.entity.vo.UserType;

@RequiredArgsConstructor
@Component
public class PlatformUserTypeResolver {

    private final List<PlatformUserTypeConverter> converters;

    public UserType resolveUserType(PlatformUserProfile platformUserProfile) {
        return converters.stream()
                .map(converter -> converter.convert(platformUserProfile))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("cannot convert"));
    }
}
