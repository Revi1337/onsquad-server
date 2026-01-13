package revi1337.onsquad.infrastructure.aws.s3.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import revi1337.onsquad.infrastructure.aws.s3.core.S3ImageCleanupProcessor.CleanupResult;
import revi1337.onsquad.infrastructure.aws.s3.model.FilePaths;
import revi1337.onsquad.infrastructure.aws.s3.notification.S3FailNotificationProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3BatchDeletionScheduler {

    private final S3ImageCleanupProcessor s3ImageCleanupProcessor;
    private final S3FailNotificationProvider notificationProvider;

    @Scheduled(cron = "${onsquad.aws.s3.delete-batch-cron}")
    public void deleteInBatch() {
        FilePaths targets = s3ImageCleanupProcessor.fetchCleanupTargets();
        if (targets.isEmpty()) {
            return;
        }
        log.debug("Starting S3 Cleanup - Total: {}", targets.size());
        CleanupResult result = s3ImageCleanupProcessor.processCleanup(targets);
        if (result.success().isNotEmpty()) {
            log.debug("Success Cleanup S3 Objects - Total: {}", result.success().size());
            s3ImageCleanupProcessor.clearBin(result.success());
        }
        if (result.failure().isEmpty()) {
            return;
        }
        log.debug("Fail to Cleanup S3 Objects - Total: {}", result.failure().size());
        FilePaths exceedPaths = s3ImageCleanupProcessor.handleFailedResults(result.failure());
        if (exceedPaths.isEmpty()) {
            return;
        }
        log.warn("S3 Objects Exceeded Max Retries - Notifying Total: {}", exceedPaths.size());
        notificationProvider.sendExceedRetryAlert(exceedPaths.pathValues());
        s3ImageCleanupProcessor.clearBin(exceedPaths);
    }
}
