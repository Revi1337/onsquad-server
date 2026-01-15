package revi1337.onsquad.crew_member.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew.domain.event.ScoreIncreased;
import revi1337.onsquad.crew_member.application.RetryableCrewRankingService;

@Component
@RequiredArgsConstructor
public class ScoreIncreasedEventListener {

    private final RetryableCrewRankingService retryableCrewRankingService;

    @Async("activityExecutor")
    @EventListener
    public void onIncreased(ScoreIncreased increased) {
        retryableCrewRankingService.applyActivityScore(
                increased.crewId(),
                increased.memberId(),
                increased.applyAt(),
                increased.crewActivity()
        );
    }
}
