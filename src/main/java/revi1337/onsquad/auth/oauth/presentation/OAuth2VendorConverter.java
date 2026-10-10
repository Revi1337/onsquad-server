package revi1337.onsquad.auth.oauth.presentation;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.oauth.application.OAuth2Vendor;

@Component
public class OAuth2VendorConverter implements Converter<String, OAuth2Vendor> {

    @Override
    public OAuth2Vendor convert(String source) {
        return OAuth2Vendor.valueOf(source.trim().toUpperCase());
    }
}
