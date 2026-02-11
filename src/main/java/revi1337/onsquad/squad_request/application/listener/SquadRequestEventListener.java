package revi1337.onsquad.squad_request.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.crew_member.domain.event.ScoreIncreased;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;
import revi1337.onsquad.notification.domain.Notification;
import revi1337.onsquad.squad_request.application.history.RequestAcceptHistory;
import revi1337.onsquad.squad_request.application.history.RequestAddHistory;
import revi1337.onsquad.squad_request.application.history.RequestRejectHistory;
import revi1337.onsquad.squad_request.application.notification.RequestAcceptNotification;
import revi1337.onsquad.squad_request.application.notification.RequestAddNotification;
import revi1337.onsquad.squad_request.application.notification.RequestRejectNotification;
import revi1337.onsquad.squad_request.domain.event.RequestAccepted;
import revi1337.onsquad.squad_request.domain.event.RequestAdded;
import revi1337.onsquad.squad_request.domain.event.RequestRejected;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestContextReader;

@Component
@RequiredArgsConstructor
public class SquadRequestEventListener {

    private final SquadRequestContextReader contextReader;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener
    public void onRequestAdded(RequestAdded added) {
        contextReader.readAddedContext(added.squadId(), added.requesterId(), added.requestId())
                .ifPresent(context -> {
                    eventPublisher.publishEvent(new RequestAddHistory(context));
                    sendNotificationIfPossible(new RequestAddNotification(context));
                });
    }

    @TransactionalEventListener
    public void onRequestAccepted(RequestAccepted accepted) {
        contextReader.readAcceptedContext(accepted.squadId(), accepted.accepterId(), accepted.requesterId())
                .ifPresent(context -> {
                    eventPublisher.publishEvent(new ScoreIncreased(context.crewId(), context.requesterId(), CrewActivity.SQUAD_PARTICIPANT));
                    eventPublisher.publishEvent(new RequestAcceptHistory(context));
                    sendNotificationIfPossible(new RequestAcceptNotification(context));
                });
    }

    @TransactionalEventListener
    public void onRequestRejected(RequestRejected rejected) {
        contextReader.readRejectedContext(rejected.squadId(), rejected.rejecterId(), rejected.requesterId())
                .ifPresent(context -> {
                    eventPublisher.publishEvent(new RequestRejectHistory(context));
                    sendNotificationIfPossible(new RequestRejectNotification(context));
                });
    }

    private void sendNotificationIfPossible(Notification notification) {
        if (notification.shouldSend()) {
            eventPublisher.publishEvent(notification);
        }
    }
}
