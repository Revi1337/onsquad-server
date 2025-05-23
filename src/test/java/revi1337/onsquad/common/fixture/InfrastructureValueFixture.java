package revi1337.onsquad.common.fixture;

import java.time.Duration;
import revi1337.onsquad.inrastructure.mail.application.VerificationStatus;

public abstract class InfrastructureValueFixture {

    public static final String TEST_EMAIL_SUBJECT = "test-email-subject";
    public static final String TEST_EMAIL_BODY = "test-email-body";
    public static final String TEST_VERIFICATION_CODE = "test-verification-code";
    public static final VerificationStatus TEST_VERIFICATION_SUCCESS = VerificationStatus.SUCCESS;
    public static final Duration TEST_VERIFICATION_CODE_TIMEOUT = Duration.ofMinutes(3);
    public static final Duration TEST_JOINING_TIMEOUT = Duration.ofMinutes(5);

}
