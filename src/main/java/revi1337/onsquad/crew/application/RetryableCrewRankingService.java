package revi1337.onsquad.crew.application;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew.infrastructure.discord.ApplyScoreFailNotificationProvider;
import revi1337.onsquad.crew_member.domain.CrewActivity;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryableCrewRankingService {

    public static final int MAX_RETRY_COUNT = 3;

    private final CrewRankingService delegate;
    private final ApplyScoreFailNotificationProvider notificationProvider;

    @Retryable(maxAttempts = MAX_RETRY_COUNT, backoff = @Backoff(delay = 1000, multiplier = 1.5, maxDelay = 5000))
    public void applyActivityScore(Long crewId, Long memberId, Instant applyAt, CrewActivity crewActivity) {
        delegate.applyActivityScore(crewId, memberId, applyAt, crewActivity);
    }

    @Recover
    public void recover(Throwable throwable, Long crewId, Long memberId, Instant applyAt, CrewActivity crewActivity) {
        log.error("Final failure updating ranking score...");
        notificationProvider.sendExceedRetryAlert(crewId, memberId, crewActivity);
    }
}
