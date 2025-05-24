package revi1337.onsquad.auth.application.oauth;

import lombok.Getter;
import revi1337.onsquad.auth.application.oauth.provider.endpoint.GoogleOAuth2EndpointBuilder;
import revi1337.onsquad.auth.application.oauth.provider.endpoint.KakaoOAuth2EndpointBuilder;
import revi1337.onsquad.auth.application.oauth.provider.endpoint.PlatformOAuth2EndpointBuilder;
import revi1337.onsquad.auth.application.oauth.provider.token.GoogleOAuth2AccessTokenFetcher;
import revi1337.onsquad.auth.application.oauth.provider.token.KakaoOAuth2AccessTokenFetcher;
import revi1337.onsquad.auth.application.oauth.provider.token.PlatformOAuth2AccessTokenFetcher;
import revi1337.onsquad.auth.application.oauth.provider.user.GoogleOAuth2UserProfileFetcher;
import revi1337.onsquad.auth.application.oauth.provider.user.KakaoOAuth2UserProfileFetcher;
import revi1337.onsquad.auth.application.oauth.provider.user.PlatformOAuth2UserProfileFetcher;

@Getter
public enum OAuth2Platform {

    KAKAO(
            new KakaoOAuth2EndpointBuilder(),
            new KakaoOAuth2AccessTokenFetcher(),
            new KakaoOAuth2UserProfileFetcher()
    ),
    GOOGLE(
            new GoogleOAuth2EndpointBuilder(),
            new GoogleOAuth2AccessTokenFetcher(),
            new GoogleOAuth2UserProfileFetcher()
    );

    private final PlatformOAuth2EndpointBuilder endpointBuilder;
    private final PlatformOAuth2AccessTokenFetcher accessTokenFetcher;
    private final PlatformOAuth2UserProfileFetcher userProfileFetcher;

    OAuth2Platform(PlatformOAuth2EndpointBuilder endpointBuilder,
                   PlatformOAuth2AccessTokenFetcher accessTokenFetcher,
                   PlatformOAuth2UserProfileFetcher userProfileFetcher) {
        this.endpointBuilder = endpointBuilder;
        this.accessTokenFetcher = accessTokenFetcher;
        this.userProfileFetcher = userProfileFetcher;
    }
}
