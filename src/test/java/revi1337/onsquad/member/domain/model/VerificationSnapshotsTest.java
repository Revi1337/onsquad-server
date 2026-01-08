package revi1337.onsquad.member.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_CODE;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_FAIL_CODE;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_SUCCESS_CODE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_4;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_5;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_6;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_7;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.auth.verification.domain.VerificationSnapshot;
import revi1337.onsquad.auth.verification.domain.VerificationSnapshots;
import revi1337.onsquad.auth.verification.domain.VerificationState;

class VerificationSnapshotsTest {

    @Test
    @DisplayName("만료되지 않은 인증 상태만 추출에 성공한다.")
    void success1() {
        Instant now = Instant.now();
        VerificationSnapshots snapshots = new VerificationSnapshots(List.of(
                new VerificationSnapshot(DUMMY_EMAIL_VALUE_4,
                        new VerificationState(TEST_VERIFICATION_CODE, DUMMY_EMAIL_VALUE_4, now.plusSeconds(7200).toEpochMilli())),
                new VerificationSnapshot(DUMMY_EMAIL_VALUE_5,
                        new VerificationState(TEST_VERIFICATION_CODE, DUMMY_EMAIL_VALUE_5, now.minusSeconds(10800).toEpochMilli())),
                new VerificationSnapshot(DUMMY_EMAIL_VALUE_6,
                        new VerificationState(TEST_VERIFICATION_FAIL_CODE, DUMMY_EMAIL_VALUE_6, now.minusSeconds(10800).toEpochMilli())),
                new VerificationSnapshot(DUMMY_EMAIL_VALUE_7,
                        new VerificationState(TEST_VERIFICATION_SUCCESS_CODE, DUMMY_EMAIL_VALUE_7, now.minusSeconds(10800).toEpochMilli()))
        ));

        List<VerificationSnapshot> verificationSnapshots = snapshots.extractAvailableBefore(now.toEpochMilli());

        assertThat(verificationSnapshots.size()).isEqualTo(1);
    }
}
