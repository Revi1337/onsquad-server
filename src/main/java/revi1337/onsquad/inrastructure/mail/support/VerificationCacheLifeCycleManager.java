package revi1337.onsquad.inrastructure.mail.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import revi1337.onsquad.inrastructure.mail.application.VerificationStatus;
import revi1337.onsquad.inrastructure.mail.repository.VerificationCodeExpiringMapRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class VerificationCacheLifeCycleManager {

    private static final String VERIFICATION_BACKUP_PATH = "backup/verification_backup.json";
    private static final String RESTORE_ERROR_LOG = "Error Occur While Restoring Verification Code";
    private static final String RESTORE_LOG_FORMAT = "Restored Verification Code - path : {}";
    private static final String WRITING_ERROR_LOG = "Error Occur While Writing Verification Snapshots";
    private static final String WRITING_LOG = "Writing Verification Code Snapshots";

    private final VerificationCodeExpiringMapRepository repository;
    private final ObjectMapper objectMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeVerificationCacheInRepository() {
        File backupFile = Paths.get(VERIFICATION_BACKUP_PATH).toFile();
        long currentEpochMilli = Instant.now()
                .atZone(TimeZone.getDefault().toZoneId())
                .toInstant()
                .toEpochMilli();
        try {
            VerificationSnapshots snapshots = objectMapper.readValue(backupFile, VerificationSnapshots.class);
            List<VerificationSnapshot> availableSnapshots = snapshots.extractAvailableBefore(currentEpochMilli);

            log.info(RESTORE_LOG_FORMAT, VERIFICATION_BACKUP_PATH);
            restoreVerificationsUsingSnapshots(availableSnapshots, currentEpochMilli);
        } catch (IOException exception) {
            log.error(RESTORE_ERROR_LOG, exception);
        }

    }

    @EventListener(ContextClosedEvent.class)
    public void storeVerificationCacheInFile() {
        VerificationSnapshots verificationSnapshots = repository.collectAvailableSnapshots();
        File backupFile = Paths.get(VERIFICATION_BACKUP_PATH).toFile();
        try {
            log.info(WRITING_LOG);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(backupFile, verificationSnapshots);
        } catch (IOException e) {
            log.error(WRITING_ERROR_LOG, e);
        }
    }

    private void restoreVerificationsUsingSnapshots(List<VerificationSnapshot> snapshots, long currentEpochMilli) {
        for (VerificationSnapshot availableSnapshot : snapshots) {
            final VerificationState state = availableSnapshot.state();
            final String verificationCode = state.code();
            final String target = state.target();
            final long expiredTime = state.predictedExpiredTime();

            final Duration duration = Duration.ofMillis(expiredTime - currentEpochMilli);
            if (VerificationStatus.supports(verificationCode)) {
                repository.markVerificationStatus(target, VerificationStatus.valueOf(verificationCode), duration);
            } else {
                repository.saveVerificationCode(target, verificationCode, duration);
            }
        }
    }
}
