package revi1337.onsquad.auth.verification.infrastructure.persistence.initializer;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import net.jodah.expiringmap.ExpiringMap;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.auth.verification.domain.VerificationCode;
import revi1337.onsquad.auth.verification.domain.VerificationCodes;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;
import revi1337.onsquad.auth.verification.infrastructure.persistence.ExpiringMapVerificationCodeStorage;
import revi1337.onsquad.common.config.web.ObjectMapperConfig;

@ActiveProfiles("local")
@TestPropertySource(properties = "spring.mail.verification-code-backup-path=dummy-path")
@ContextConfiguration(classes = {VerificationCodeReloader.class, ExpiringMapVerificationCodeStorage.class, ObjectMapperConfig.class})
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
class VerificationCodeReloaderTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExpiringMapVerificationCodeStorage expiringMapVerificationCodeStorage;

    @Autowired
    private VerificationCodeReloader verificationCodeReloader;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        ExpiringMap<String, VerificationCode> verificationStore =
                (ExpiringMap<String, VerificationCode>) ReflectionTestUtils.getField(expiringMapVerificationCodeStorage, "verificationStore");
        verificationStore.clear();
    }

    @Test
    @DisplayName("백업 JSON 파일을 복원하여 인증 상태를 되살리는 데 성공한다.")
    void restore(@TempDir Path tempDir) throws IOException {
        Path backupFile = init(tempDir);
        expiringMapVerificationCodeStorage.saveVerificationCode("test@email.com1", "1234", VerificationStatus.PENDING, Duration.ofMinutes(4));
        expiringMapVerificationCodeStorage.saveVerificationCode("test@email.com2", "1234", VerificationStatus.SUCCESS, Duration.ofMinutes(4));
        expiringMapVerificationCodeStorage.saveVerificationCode("test@email.com3", "1234", VerificationStatus.PENDING, Duration.ofMinutes(4));
        VerificationCodes verificationCodes = expiringMapVerificationCodeStorage.getVerificationCodes();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(backupFile.toFile(), verificationCodes);

        verificationCodeReloader.restore();

        VerificationCodes restoredVerificationCodes = expiringMapVerificationCodeStorage.getVerificationCodes();
        assertSoftly(softly -> {
            softly.assertThat(restoredVerificationCodes.verificationCodes()).hasSize(3);
            softly.assertThat(verificationCodes.verificationCodes()).extracting(VerificationCode::getStatus)
                    .containsExactlyInAnyOrder(VerificationStatus.PENDING, VerificationStatus.SUCCESS, VerificationStatus.PENDING);
        });
    }

    @Test
    @DisplayName("저장된 인증 상태를 JSON 파일로 백업하는 데 성공한다.")
    void backup(@TempDir Path tempDir) throws IOException {
        Path backupFile = init(tempDir);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(backupFile.toFile(), "");
        expiringMapVerificationCodeStorage.saveVerificationCode("test@email.com1", "1234", VerificationStatus.PENDING, Duration.ofMinutes(4));
        expiringMapVerificationCodeStorage.saveVerificationCode("test@email.com2", "1234", VerificationStatus.SUCCESS, Duration.ofMinutes(4));
        expiringMapVerificationCodeStorage.saveVerificationCode("test@email.com3", "1234", VerificationStatus.PENDING, Duration.ofMinutes(4));
        expiringMapVerificationCodeStorage.saveVerificationCode("test@email.com4", "1234", VerificationStatus.PENDING, Duration.ofMinutes(4));

        verificationCodeReloader.backup(new ContextClosedEvent(applicationContext));

        VerificationCodes verificationCodes = objectMapper.readValue(backupFile.toFile(), VerificationCodes.class);
        assertSoftly(softly -> {
            softly.assertThat(verificationCodes.verificationCodes()).hasSize(4);
            softly.assertThat(verificationCodes.verificationCodes()).extracting(VerificationCode::getStatus)
                    .containsExactlyInAnyOrder(VerificationStatus.PENDING, VerificationStatus.SUCCESS, VerificationStatus.PENDING, VerificationStatus.PENDING);
        });
    }

    private Path init(Path tempDir) {
        Path backupFile = tempDir.resolve("test-verification_backup.json");
        ReflectionTestUtils.setField(verificationCodeReloader, "backupPath", backupFile.toString());
        return backupFile;
    }
}
