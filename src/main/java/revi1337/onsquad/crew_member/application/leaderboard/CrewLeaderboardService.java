package revi1337.onsquad.crew_member.application.leaderboard;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;
import revi1337.onsquad.crew_member.infrastructure.discord.ApplyScoreFailNotificationProvider;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrewLeaderboardService {

    public static final int MAX_RETRY_COUNT = 3;

    private final CrewLeaderboardManager delegate;
    private final ApplyScoreFailNotificationProvider notificationProvider;

    @Retryable(maxAttempts = MAX_RETRY_COUNT, backoff = @Backoff(delay = 1000, multiplier = 1.5, maxDelay = 5000))
    public void applyActivity(Long crewId, Long memberId, Instant applyAt, CrewActivity crewActivity) {
        delegate.applyActivity(crewId, memberId, applyAt, crewActivity);
    }

    @Recover
    public void recover(Throwable throwable, Long crewId, Long memberId, Instant applyAt, CrewActivity crewActivity) {
        log.error("Final failure updating ranking score...");
        notificationProvider.sendExceedRetryAlert(crewId, memberId, crewActivity);
    }
}
