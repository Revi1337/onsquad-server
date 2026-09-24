package revi1337.onsquad.crew_member.presentation;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardUpdateService;

@Profile("local")
@RestController
@RequiredArgsConstructor
public class CrewLeaderboardDebugController {

    private final CrewLeaderboardUpdateService crewLeaderboardUpdateService;

    @PostMapping("/internal/leaderboard/refresh")
    public ResponseEntity<RestResponse<LeaderboardRefreshResponse>> refresh(
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to,
            @RequestParam(defaultValue = "5") Integer rankLimit
    ) {
        long startedAt = System.currentTimeMillis();
        crewLeaderboardUpdateService.refreshLeaderboards(from, to, rankLimit);
        long elapsedMs = System.currentTimeMillis() - startedAt;

        LeaderboardRefreshResponse response = new LeaderboardRefreshResponse(elapsedMs, from, to, rankLimit);

        return ResponseEntity.ok().body(RestResponse.success(response));
    }

    private record LeaderboardRefreshResponse(
            long elapsedMs,
            LocalDateTime from,
            LocalDateTime to,
            Integer rankLimit
    ) {

    }
}
