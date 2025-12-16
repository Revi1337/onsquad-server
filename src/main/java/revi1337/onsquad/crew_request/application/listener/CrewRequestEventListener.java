package revi1337.onsquad.crew_request.application.listener;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.crew_request.application.notification.RequestAcceptNotification;
import revi1337.onsquad.crew_request.application.notification.RequestAddedNotification;
import revi1337.onsquad.crew_request.application.notification.RequestNotificationFetcher;
import revi1337.onsquad.crew_request.application.notification.RequestRejectNotification;
import revi1337.onsquad.crew_request.domain.event.RequestAccepted;
import revi1337.onsquad.crew_request.domain.event.RequestAdded;
import revi1337.onsquad.crew_request.domain.event.RequestRejected;
import revi1337.onsquad.notification.domain.Notification;

@Component
public class CrewRequestEventListener {

    private final RequestNotificationFetcher requestNotificationFetcher;
    private final ApplicationEventPublisher eventPublisher;

    public CrewRequestEventListener(
            @Qualifier("crewRequestNotificationFetcher") RequestNotificationFetcher requestNotificationFetcher,
            ApplicationEventPublisher eventPublisher
    ) {
        this.requestNotificationFetcher = requestNotificationFetcher;
        this.eventPublisher = eventPublisher;
    }

    @TransactionalEventListener
    public void onRequestAdded(RequestAdded added) {
        requestNotificationFetcher.fetchAddedInformation(added.crewId(), added.requesterId(), added.requestId())
                .ifPresent(notificationResult -> {
                    RequestAddedNotification notification = new RequestAddedNotification(notificationResult);
                    sendIfPossible(notification);
                });
    }

    @TransactionalEventListener
    public void onRequestAccepted(RequestAccepted accepted) {
        requestNotificationFetcher.fetchAcceptedInformation(accepted.crewId(), accepted.accepterId(), accepted.requesterId())
                .ifPresent(notificationResult -> {
                    RequestAcceptNotification notification = new RequestAcceptNotification(notificationResult);
                    sendIfPossible(notification);
                });
    }

    @TransactionalEventListener
    public void onRequestRejected(RequestRejected rejected) {
        requestNotificationFetcher.fetchRejectedInformation(rejected.crewId(), rejected.rejecterId(), rejected.requesterId())
                .ifPresent(notificationResult -> {
                    RequestRejectNotification notification = new RequestRejectNotification(notificationResult);
                    sendIfPossible(notification);
                });
    }

    private void sendIfPossible(Notification notification) {
        if (notification.shouldSend()) {
            eventPublisher.publishEvent(notification);
        }
    }
}
