package revi1337.onsquad.auth.oauth.config;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import revi1337.onsquad.auth.oauth.OAuth2Platform;

@ConfigurationProperties("onsquad.oauth2")
public record OAuth2ClientProperties(
        Map<SupportOAuth2Platform, OAuth2Properties> clients
) {

    public record OAuth2Properties(
            String clientName,
            String clientId,
            String clientSecret,
            String redirectUri,
            String issuerUri,
            String authorizationUri,
            @DefaultValue("code") String responseType,
            String tokenUri,
            @DefaultValue("authorization_code") String grantType,
            String userInfoUri,
            String accountUri,
            Map<String, String> scope
    ) {

    }

    public OAuth2Properties getPropertyFrom(OAuth2Platform platform) {
        return clients().get(SupportOAuth2Platform.from(platform.name()));
    }
}
