package revi1337.onsquad.auth.token.infrastructure.persistence.scheduler;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.token.infrastructure.persistence.RdbRefreshTokenStorage;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {

    private final RdbRefreshTokenStorage refreshTokenStorage;

    @Scheduled(cron = "0 0 * * * *")
    public void cleanup() {
        int deletedRows = refreshTokenStorage.removeExpiredTokens(Instant.now().toEpochMilli());
        log.info("[Refresh Token Cleanup] Deleted {} expired records.", deletedRows);
    }
}
