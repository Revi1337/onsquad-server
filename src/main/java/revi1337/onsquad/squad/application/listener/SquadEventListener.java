package revi1337.onsquad.squad.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.crew_member.domain.event.ScoreIncreased;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;
import revi1337.onsquad.squad.domain.event.SquadCreated;

@Component
@RequiredArgsConstructor
public class SquadEventListener {

    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener
    public void onSquadCreated(SquadCreated created) {
        eventPublisher.publishEvent(new ScoreIncreased(created.crewId(), created.creatorId(), CrewActivity.SQUAD_CREATE));
    }
}
