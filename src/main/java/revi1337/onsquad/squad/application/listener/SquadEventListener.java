package revi1337.onsquad.squad.application.listener;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.crew.application.CrewRankingManager;
import revi1337.onsquad.crew_member.domain.CrewActivity;
import revi1337.onsquad.squad.domain.event.SquadCreated;

@RequiredArgsConstructor
@Component
public class SquadEventListener {

    private final CrewRankingManager crewRankingManager;

    @TransactionalEventListener
    public void onSquadCreated(SquadCreated created) {
        crewRankingManager.applyActivityScore(created.crewId(), created.creatorId(), Instant.now(), CrewActivity.SQUAD_CREATE);
    }
}
