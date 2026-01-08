package revi1337.onsquad.member.application.initializer;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.SNAPSHOTS;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_CODE;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_CODE_HOUR_TIMEOUT;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_FAIL;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_FAIL_CODE;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_SUCCESS;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_SUCCESS_CODE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_1;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_2;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_3;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_4;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_5;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_6;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_EMAIL_VALUE_7;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.auth.verification.VerificationBackupProcessor;
import revi1337.onsquad.auth.verification.domain.VerificationSnapshots;
import revi1337.onsquad.auth.verification.domain.VerificationState;
import revi1337.onsquad.auth.verification.infrastructure.VerificationCodeExpiringMapRepository;

@ContextConfiguration(classes = {VerificationBackupProcessor.class, VerificationCodeExpiringMapRepository.class})
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
class VerificationBackupProcessorMethodTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VerificationCodeExpiringMapRepository verificationCodeExpiringMapRepository;

    @Autowired
    private VerificationBackupProcessor lifeCycleManager;

    @Autowired
    private ApplicationContext applicationContext;

    private Map<String, String> store = (Map<String, String>) ReflectionTestUtils
            .getField(VerificationCodeExpiringMapRepository.class, "VERIFICATION_STORE");
    private Map<String, VerificationState> tracker = (Map<String, VerificationState>) ReflectionTestUtils
            .getField(VerificationCodeExpiringMapRepository.class, "VERIFICATION_TRACKER");

    @BeforeEach
    void setUp() {
        store.clear();
        tracker.clear();
    }

    @Test
    @DisplayName("백업 JSON 파일을 복원하여 인증 상태를 되살리는 데 성공한다.")
    void success1(@TempDir Path tempDir) throws IOException {
        Path backupFile = init(tempDir);
        initializeVerificationSnapshotsInFile(backupFile);

        lifeCycleManager.restore();

        assertThat(store).hasSize(4);
        assertThat(tracker).hasSize(4);
    }

    @Test
    @DisplayName("저장된 인증 상태를 JSON 파일로 백업하는 데 성공한다.")
    void success2(@TempDir Path tempDir) throws IOException {
        Path backupFile = init(tempDir);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(backupFile.toFile(), "");
        initializeVerificationSnapshotsInRepository();

        lifeCycleManager.backup(new ContextClosedEvent(applicationContext));

        VerificationSnapshots verificationSnapshots = objectMapper.readValue(backupFile.toFile(), VerificationSnapshots.class);
        assertThat(verificationSnapshots.size()).isEqualTo(7);
    }

    private Path init(Path tempDir) {
        Path backupFile = tempDir.resolve("test-verification_backup.json");
        ReflectionTestUtils.setField(lifeCycleManager, "backupPath", backupFile.toString());
        return backupFile;
    }

    private void initializeVerificationSnapshotsInFile(Path backupFile) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(backupFile.toFile(), SNAPSHOTS);
    }

    private void initializeVerificationSnapshotsInRepository() {
        verificationCodeExpiringMapRepository.saveVerificationCode(DUMMY_EMAIL_VALUE_1, TEST_VERIFICATION_SUCCESS_CODE, TEST_VERIFICATION_CODE_HOUR_TIMEOUT);

        verificationCodeExpiringMapRepository.markVerificationStatus(DUMMY_EMAIL_VALUE_1, TEST_VERIFICATION_SUCCESS, TEST_VERIFICATION_CODE_HOUR_TIMEOUT);

        verificationCodeExpiringMapRepository.saveVerificationCode(DUMMY_EMAIL_VALUE_2, TEST_VERIFICATION_SUCCESS_CODE, TEST_VERIFICATION_CODE_HOUR_TIMEOUT);
        verificationCodeExpiringMapRepository.markVerificationStatus(DUMMY_EMAIL_VALUE_2, TEST_VERIFICATION_SUCCESS, TEST_VERIFICATION_CODE_HOUR_TIMEOUT);

        verificationCodeExpiringMapRepository.saveVerificationCode(DUMMY_EMAIL_VALUE_3, TEST_VERIFICATION_FAIL_CODE, TEST_VERIFICATION_CODE_HOUR_TIMEOUT);
        verificationCodeExpiringMapRepository.markVerificationStatus(DUMMY_EMAIL_VALUE_3, TEST_VERIFICATION_FAIL, TEST_VERIFICATION_CODE_HOUR_TIMEOUT);

        verificationCodeExpiringMapRepository.saveVerificationCode(DUMMY_EMAIL_VALUE_4, TEST_VERIFICATION_CODE, TEST_VERIFICATION_CODE_HOUR_TIMEOUT);

        verificationCodeExpiringMapRepository.saveVerificationCode(DUMMY_EMAIL_VALUE_5, TEST_VERIFICATION_CODE, TEST_VERIFICATION_CODE_HOUR_TIMEOUT);

        verificationCodeExpiringMapRepository.saveVerificationCode(DUMMY_EMAIL_VALUE_6, TEST_VERIFICATION_FAIL_CODE, TEST_VERIFICATION_CODE_HOUR_TIMEOUT);
        verificationCodeExpiringMapRepository.markVerificationStatus(DUMMY_EMAIL_VALUE_6, TEST_VERIFICATION_FAIL, TEST_VERIFICATION_CODE_HOUR_TIMEOUT);

        verificationCodeExpiringMapRepository.saveVerificationCode(DUMMY_EMAIL_VALUE_7, TEST_VERIFICATION_SUCCESS_CODE, TEST_VERIFICATION_CODE_HOUR_TIMEOUT);
        verificationCodeExpiringMapRepository.markVerificationStatus(DUMMY_EMAIL_VALUE_7, TEST_VERIFICATION_SUCCESS, TEST_VERIFICATION_CODE_HOUR_TIMEOUT);
    }
}
