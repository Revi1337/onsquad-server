package revi1337.onsquad.token.infrastructure;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("onsquad.token")
public record TokenProperties(
        @NestedConfigurationProperty AccessTokenAttributes accessToken,
        @NestedConfigurationProperty RefreshTokenAttributes refreshToken
) {

    public TokenProperties(
            @DefaultValue AccessTokenAttributes accessToken,
            @DefaultValue RefreshTokenAttributes refreshToken
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public record AccessTokenAttributes(@NestedConfigurationProperty TokenAttributes attributes) {

        public AccessTokenAttributes(@DefaultValue TokenAttributes attributes) {
            this.attributes = attributes;
        }
    }

    public record RefreshTokenAttributes(@NestedConfigurationProperty TokenAttributes attributes) {

        public RefreshTokenAttributes(@DefaultValue TokenAttributes attributes) {
            this.attributes = attributes;
        }
    }

    public record TokenAttributes(Duration expired, String secretKey) {

        public TokenAttributes(
                @DefaultValue("10s") Duration expired,
                @DefaultValue("secret-key") String secretKey
        ) {
            this.expired = expired;
            this.secretKey = secretKey;
        }
    }
}
