package revi1337.onsquad.common.fixture;

import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_1;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_2;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_3;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_4;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_5;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_6;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_7;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import revi1337.onsquad.auth.verification.domain.VerificationCode;
import revi1337.onsquad.auth.verification.domain.VerificationSnapshot;
import revi1337.onsquad.auth.verification.domain.VerificationSnapshots;
import revi1337.onsquad.auth.verification.domain.VerificationState;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;

public abstract class InfrastructureValueFixture {

    public static final String TEST_EMAIL_SUBJECT = "test-email-subject";

    public static final String TEST_EMAIL_BODY = "test-email-body";

    public static final String TEST_VERIFICATION_CODE = "test-verification-code";
    public static final String TEST_VERIFICATION_SUCCESS_CODE = VerificationStatus.SUCCESS.name();
    public static final String TEST_VERIFICATION_FAIL_CODE = VerificationStatus.FAIL.name();

    public static final VerificationStatus TEST_VERIFICATION_SUCCESS = VerificationStatus.SUCCESS;
    public static final VerificationStatus TEST_VERIFICATION_FAIL = VerificationStatus.FAIL;

    public static final Duration TEST_VERIFICATION_CODE_TIMEOUT = Duration.ofMinutes(3);
    public static final Duration TEST_JOINING_TIMEOUT = Duration.ofMinutes(5);
    public static final Duration TEST_VERIFICATION_CODE_HOUR_TIMEOUT = Duration.ofHours(1);
    public static final long TEST_VERIFICATION_CODE_MILLI_TIMEOUT = 100000;

    public static final VerificationCode TEST_VERIFICATION_CODE_1 = new VerificationCode(TEST_EMAIL_BODY, TEST_VERIFICATION_CODE_MILLI_TIMEOUT);

    private static final Instant NOW = Instant.now();

    public static final VerificationSnapshot SNAPSHOT_1 = new VerificationSnapshot(
            toKey(DUMMY_EMAIL_VALUE_1),
            new VerificationState(TEST_VERIFICATION_SUCCESS_CODE, DUMMY_EMAIL_VALUE_1, NOW.plusSeconds(7200).toEpochMilli())
    );

    public static final VerificationSnapshot SNAPSHOT_2 = new VerificationSnapshot(
            toKey(DUMMY_EMAIL_VALUE_2),
            new VerificationState(TEST_VERIFICATION_SUCCESS_CODE, DUMMY_EMAIL_VALUE_2, NOW.plusSeconds(7200).toEpochMilli())
    );

    public static final VerificationSnapshot SNAPSHOT_3 = new VerificationSnapshot(
            toKey(DUMMY_EMAIL_VALUE_3),
            new VerificationState(TEST_VERIFICATION_FAIL_CODE, DUMMY_EMAIL_VALUE_3, NOW.plusSeconds(7200).toEpochMilli())
    );

    public static final VerificationSnapshot SNAPSHOT_4 = new VerificationSnapshot(
            toKey(DUMMY_EMAIL_VALUE_4),
            new VerificationState(TEST_VERIFICATION_CODE, DUMMY_EMAIL_VALUE_4, NOW.plusSeconds(7200).toEpochMilli())
    );

    public static final VerificationSnapshot SNAPSHOT_5 = new VerificationSnapshot(
            toKey(DUMMY_EMAIL_VALUE_5),
            new VerificationState(TEST_VERIFICATION_CODE, DUMMY_EMAIL_VALUE_5, NOW.minusSeconds(10800).toEpochMilli())
    );

    public static final VerificationSnapshot SNAPSHOT_6 = new VerificationSnapshot(
            toKey(DUMMY_EMAIL_VALUE_6),
            new VerificationState(TEST_VERIFICATION_FAIL_CODE, DUMMY_EMAIL_VALUE_6, NOW.minusSeconds(10800).toEpochMilli())
    );

    public static final VerificationSnapshot SNAPSHOT_7 = new VerificationSnapshot(
            toKey(DUMMY_EMAIL_VALUE_7),
            new VerificationState(TEST_VERIFICATION_SUCCESS_CODE, DUMMY_EMAIL_VALUE_7, NOW.minusSeconds(10800).toEpochMilli())
    );

    public static final VerificationSnapshots SNAPSHOTS = new VerificationSnapshots(
            List.of(SNAPSHOT_1, SNAPSHOT_2, SNAPSHOT_3, SNAPSHOT_4, SNAPSHOT_5, SNAPSHOT_6, SNAPSHOT_7)
    );

    private static String toKey(String email) {
        return String.format(CacheFormat.COMPLEX, CacheConst.VERIFICATION_CODE, email);
    }
}
