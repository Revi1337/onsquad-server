package revi1337.onsquad.crew_member.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew.domain.event.ScoreIncreased;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardService;

@Component
@RequiredArgsConstructor
public class CrewLeaderboardUpdateListener {

    private final CrewLeaderboardService crewLeaderboardService;

    @Async("activityExecutor")
    @EventListener
    public void onIncreased(ScoreIncreased increased) {
        crewLeaderboardService.applyActivity(
                increased.crewId(),
                increased.memberId(),
                increased.applyAt(),
                increased.crewActivity()
        );
    }
}
