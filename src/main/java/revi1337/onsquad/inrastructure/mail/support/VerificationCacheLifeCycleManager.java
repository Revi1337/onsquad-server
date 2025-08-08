package revi1337.onsquad.inrastructure.mail.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import revi1337.onsquad.inrastructure.mail.application.VerificationStatus;
import revi1337.onsquad.inrastructure.mail.repository.VerificationCodeExpiringMapRepository;

@Slf4j
@Component
public class VerificationCacheLifeCycleManager {

    private static final String RESTORE_ERROR_LOG = "Error Occur While Restoring Verification Code";
    private static final String RESTORE_LOG_FORMAT = "Restored Verification Code - path : {}";
    private static final String WRITING_ERROR_LOG = "Error Occur While Writing Verification Snapshots";
    private static final String WRITING_LOG = "Backup Verification Code Snapshots";

    private final String backupPath;
    private final VerificationCodeExpiringMapRepository repository;
    private final ObjectMapper objectMapper;

    public VerificationCacheLifeCycleManager(
            @Value("${spring.mail.verification-code-backup-path}") String backupPath,
            VerificationCodeExpiringMapRepository repository,
            ObjectMapper objectMapper
    ) {
        this.backupPath = backupPath;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void restore() {
        Optional<File> optionalFile = getBackupFileIfExists();
        long currentEpochMilli = Instant.now()
                .atZone(TimeZone.getDefault().toZoneId())
                .toInstant()
                .toEpochMilli();

        optionalFile.ifPresent(backupFile -> restoreVerificationCodeSnapshots(backupFile, currentEpochMilli));
    }

    @EventListener(ContextClosedEvent.class)
    public void backup() {
        Optional<File> optionalFile = getBackupFileIfExists();
        VerificationSnapshots verificationSnapshots = repository.collectAvailableSnapshots();

        optionalFile.ifPresent(backupFile -> backupVerificationCodeSnapshots(backupFile, verificationSnapshots));
    }

    private Optional<File> getBackupFileIfExists() {
        File file = Paths.get(backupPath).toFile();
        if (file.exists()) {
            return Optional.of(file);
        }

        return Optional.empty();
    }

    private void restoreVerificationCodeSnapshots(File backupFile, long epochMilli) {
        try {
            VerificationSnapshots snapshots = objectMapper.readValue(backupFile, VerificationSnapshots.class);
            List<VerificationSnapshot> availableSnapshots = snapshots.extractAvailableBefore(epochMilli);

            restoreSnapshots(availableSnapshots, epochMilli);
        } catch (IOException exception) {
            log.error(RESTORE_ERROR_LOG, exception);
        }
    }

    private void restoreSnapshots(List<VerificationSnapshot> snapshots, long epochMilli) {
        for (VerificationSnapshot snapshot : snapshots) {
            final String email = snapshot.getTarget();
            final long expiredTime = snapshot.getExpireTime();
            final Duration duration = Duration.ofMillis(expiredTime - epochMilli);

            repository.saveVerificationCode(email, snapshot.getCode(), duration);
            if (snapshot.authenticated()) {
                repository.markVerificationStatus(email, VerificationStatus.valueOf(snapshot.getCode()), duration);
            }
        }
        log.info(RESTORE_LOG_FORMAT, backupPath);
    }

    private void backupVerificationCodeSnapshots(File backupFile, VerificationSnapshots snapshots) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(backupFile, snapshots);
            log.info(WRITING_LOG);
        } catch (IOException e) {
            log.error(WRITING_ERROR_LOG, e);
        }
    }
}
