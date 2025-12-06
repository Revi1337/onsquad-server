package revi1337.onsquad.common.fixture;

import java.util.HashMap;
import revi1337.onsquad.token.domain.model.AccessToken;
import revi1337.onsquad.token.domain.model.RefreshToken;

public class TokenFixture {

    public static final String ACCESS_TOKEN_SUBJECT = "access_token";
    public static final String REFRESH_TOKEN_SUBJECT = "refresh_token";

    public static final String TEST_CLAIM_KEY_1 = "test_claim_1";
    public static final String TEST_CLAIM_VALUE_1 = "first-claim-1";
    public static final String TEST_CLAIM_KEY_2 = "test_claim_2";
    public static final String TEST_CLAIM_VALUE_2 = "first-claim-2";

    public static final String ACCESS_TOKEN_VALUE = "access-token";
    public static final String REFRESH_TOKEN_VALUE = "refresh-token";
    public static final AccessToken ACCESS_TOKEN = new AccessToken(ACCESS_TOKEN_VALUE);
    public static final RefreshToken REFRESH_TOKEN = new RefreshToken(REFRESH_TOKEN_VALUE);

    public static final String ACCESS_TOKEN_VALUE_1 = "access-token-1";
    public static final String REFRESH_TOKEN_VALUE_1 = "refresh-token-1";
    public static final AccessToken ACCESS_TOKEN_1 = new AccessToken(ACCESS_TOKEN_VALUE_1);
    public static final RefreshToken REFRESH_TOKEN_1 = new RefreshToken(REFRESH_TOKEN_VALUE_1);

    public static final String ACCESS_TOKEN_VALUE_2 = "access-token-2";
    public static final String REFRESH_TOKEN_VALUE_2 = "refresh-token-2";
    public static final AccessToken ACCESS_TOKEN_2 = new AccessToken(ACCESS_TOKEN_VALUE_2);
    public static final RefreshToken REFRESH_TOKEN_2 = new RefreshToken(REFRESH_TOKEN_VALUE_2);

    public static final HashMap<String, String> CLAIMS = new HashMap<>() {
        {
            put(TEST_CLAIM_KEY_1, TEST_CLAIM_VALUE_1);
            put(TEST_CLAIM_KEY_2, TEST_CLAIM_VALUE_2);
        }
    };
}
