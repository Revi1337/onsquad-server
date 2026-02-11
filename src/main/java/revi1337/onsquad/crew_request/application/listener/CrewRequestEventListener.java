package revi1337.onsquad.crew_request.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.crew_member.domain.event.ScoreIncreased;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;
import revi1337.onsquad.crew_request.application.history.RequestAcceptHistory;
import revi1337.onsquad.crew_request.application.history.RequestAddHistory;
import revi1337.onsquad.crew_request.application.history.RequestRejectHistory;
import revi1337.onsquad.crew_request.application.notification.RequestAcceptNotification;
import revi1337.onsquad.crew_request.application.notification.RequestAddNotification;
import revi1337.onsquad.crew_request.application.notification.RequestRejectNotification;
import revi1337.onsquad.crew_request.domain.event.RequestAccepted;
import revi1337.onsquad.crew_request.domain.event.RequestAdded;
import revi1337.onsquad.crew_request.domain.event.RequestRejected;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestContextReader;
import revi1337.onsquad.notification.domain.Notification;

@Component
@RequiredArgsConstructor
public class CrewRequestEventListener {

    private final CrewRequestContextReader contextReader;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener
    public void onRequestAdded(RequestAdded added) {
        contextReader.readAddedContext(added.crewId(), added.requesterId(), added.requestId())
                .ifPresent(context -> {
                    eventPublisher.publishEvent(new RequestAddHistory(context));
                    sendNotificationIfPossible(new RequestAddNotification(context));
                });
    }

    @TransactionalEventListener
    public void onRequestAccepted(RequestAccepted accepted) {
        contextReader.readAcceptedContext(accepted.crewId(), accepted.accepterId(), accepted.requesterId())
                .ifPresent(context -> {
                    eventPublisher.publishEvent(new ScoreIncreased(context.crewId(), context.requesterId(), CrewActivity.CREW_PARTICIPANT));
                    eventPublisher.publishEvent(new RequestAcceptHistory(context));
                    sendNotificationIfPossible(new RequestAcceptNotification(context));
                });
    }

    @TransactionalEventListener
    public void onRequestRejected(RequestRejected rejected) {
        contextReader.readRejectedContext(rejected.crewId(), rejected.rejecterId(), rejected.requesterId())
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
