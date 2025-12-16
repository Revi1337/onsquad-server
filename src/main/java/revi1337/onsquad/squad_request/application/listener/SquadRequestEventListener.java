package revi1337.onsquad.squad_request.application.listener;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import revi1337.onsquad.notification.domain.Notification;
import revi1337.onsquad.squad_request.application.notification.RequestAcceptNotification;
import revi1337.onsquad.squad_request.application.notification.RequestAddedNotification;
import revi1337.onsquad.squad_request.application.notification.RequestNotificationFetcher;
import revi1337.onsquad.squad_request.application.notification.RequestRejectNotification;
import revi1337.onsquad.squad_request.domain.event.RequestAccepted;
import revi1337.onsquad.squad_request.domain.event.RequestAdded;
import revi1337.onsquad.squad_request.domain.event.RequestRejected;

@Component
public class SquadRequestEventListener {

    private final RequestNotificationFetcher requestNotificationFetcher;
    private final ApplicationEventPublisher eventPublisher;

    public SquadRequestEventListener(
            @Qualifier("squadRequestNotificationFetcher") RequestNotificationFetcher requestNotificationFetcher,
            ApplicationEventPublisher eventPublisher
    ) {
        this.requestNotificationFetcher = requestNotificationFetcher;
        this.eventPublisher = eventPublisher;
    }

    @TransactionalEventListener
    public void onRequestAdded(RequestAdded added) {
        requestNotificationFetcher.fetchAddedInformation(added.squadId(), added.requesterId(), added.requestId())
                .ifPresent(notificationResult -> {
                    RequestAddedNotification notification = new RequestAddedNotification(notificationResult);
                    sendIfPossible(notification);
                });
    }

    @TransactionalEventListener
    public void onRequestAccepted(RequestAccepted accepted) {
        requestNotificationFetcher.fetchAcceptedInformation(accepted.squadId(), accepted.accepterId(), accepted.requesterId())
                .ifPresent(notificationResult -> {
                    RequestAcceptNotification notification = new RequestAcceptNotification(notificationResult);
                    sendIfPossible(notification);
                });
    }

    @TransactionalEventListener
    public void onRequestRejected(RequestRejected rejected) {
        requestNotificationFetcher.fetchRejectedInformation(rejected.squadId(), rejected.rejecterId(), rejected.requesterId())
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
