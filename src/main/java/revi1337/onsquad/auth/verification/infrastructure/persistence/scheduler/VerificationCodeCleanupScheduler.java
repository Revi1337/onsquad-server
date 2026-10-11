package revi1337.onsquad.auth.verification.infrastructure.persistence.scheduler;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.verification.infrastructure.persistence.RdbEmailVerificationRegistry;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerificationCodeCleanupScheduler {

    private final RdbEmailVerificationRegistry emailVerificationRegistry;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanup() {
        int deletedRows = emailVerificationRegistry.removeExpiredCodes(Instant.now().toEpochMilli());
        log.info("[Verification Code Cleanup] Deleted {} expired records.", deletedRows);
    }
}
