package revi1337.onsquad.auth.oauth.provider.token;

import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import revi1337.onsquad.auth.oauth.config.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.auth.oauth.provider.PlatformOAuth2AccessTokenFetcher;
import revi1337.onsquad.token.domain.model.AccessToken;

public class GoogleOAuth2AccessTokenFetcher implements PlatformOAuth2AccessTokenFetcher {

    @Override
    public AccessToken fetch(String baseUrl, String authorizationCode, OAuth2Properties oAuth2Properties) {
        return fetchToken(baseUrl, authorizationCode, oAuth2Properties);
    }

    private AccessToken fetchToken(String baseUrl, String authorizationCode, OAuth2Properties oAuth2Properties) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            add("client_id", oAuth2Properties.clientId());
            add("client_secret", oAuth2Properties.clientSecret());
            add("redirect_uri", baseUrl + oAuth2Properties.redirectUri());
            add("grant_type", oAuth2Properties.grantType());
            add("code", authorizationCode);
        }};

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<Map<String, String>> tokenAttributeResponse = new RestTemplate().exchange(
                oAuth2Properties.tokenUri(),
                HttpMethod.POST,
                new HttpEntity<>(params, headers),
                new ParameterizedTypeReference<>() {
                }
        );

        return new AccessToken(tokenAttributeResponse.getBody().get("access_token"));
    }
}
