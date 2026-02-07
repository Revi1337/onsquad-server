package revi1337.onsquad.notification.infrastructure.sse;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;
import revi1337.onsquad.notification.domain.model.NotificationMessage;
import revi1337.onsquad.notification.domain.model.NotificationMessages;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseEmitterManager {

    private static final long TIME_OUT = 60 * 60 * 1000L;

    private final SseEmitterRepository sseEmitterRepository;

    public NamedSseEmitter createEmitter(Long id, SseTopic topic, Runnable onCompletion) {
        String identifier = topic.toIdentifier(id.toString());
        NamedSseEmitter emitter = sseEmitterRepository.save(identifier, new NamedSseEmitter(identifier, topic, TIME_OUT));
        emitter.onTimeout(emitter::complete);
        emitter.onError(throwable -> emitter.complete());
        emitter.onCompletion(() -> {
            sseEmitterRepository.deleteById(identifier);
            onCompletion.run();
        });
        return emitter;
    }

    public void broadcast(NotificationMessage message) {
        sseEmitterRepository.findAll()
                .forEach(emitter -> send(emitter, message));
    }

    public void send(String identifier, NotificationMessage message) {
        sseEmitterRepository.findById(identifier)
                .ifPresent(emitter -> send(emitter, message));
    }

    public void sends(NamedSseEmitter emitter, NotificationMessages message) {
        message.getMessages()
                .forEach(notification -> send(emitter, notification));
    }

    public void send(NamedSseEmitter emitter, NotificationMessage message) {
        try {
            SseEventBuilder sseEventBuilder = SseEmitter.event()
                    .name(emitter.getTopic())
                    .data(message, APPLICATION_JSON);

            Long notificationId = message.id();
            if (notificationId != null) {
                sseEventBuilder.id(notificationId.toString());
            }
            emitter.send(sseEventBuilder);
        } catch (IOException | IllegalStateException e) {
            sseEmitterRepository.deleteById(emitter.getIdentifier());
            emitter.completeWithError(e);
        }
    }
}
