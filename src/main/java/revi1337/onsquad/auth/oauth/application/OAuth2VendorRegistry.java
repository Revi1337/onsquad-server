package revi1337.onsquad.auth.oauth.application;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.oauth.application.contract.OAuth2VendorProvider;

@Component
public class OAuth2VendorRegistry {

    private final Map<OAuth2Vendor, OAuth2VendorProvider> providers;

    public OAuth2VendorRegistry(List<OAuth2VendorProvider> providers) {
        this.providers = providers.stream()
                .collect(Collectors.toUnmodifiableMap(OAuth2VendorProvider::platform, Function.identity()));
    }

    public OAuth2VendorProvider getOAuth2Provider(String platform) {
        try {
            OAuth2Vendor key = OAuth2Vendor.valueOf(platform.toUpperCase());
            return providers.get(key);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("Unsupported OAuth2 platform: " + platform);
        }
    }
}
