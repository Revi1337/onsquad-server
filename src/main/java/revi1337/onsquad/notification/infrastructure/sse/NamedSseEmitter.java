package revi1337.onsquad.notification.infrastructure.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class NamedSseEmitter extends SseEmitter {

    private final String identifier;
    private final SseTopic topic;

    public NamedSseEmitter(String identifier, SseTopic topic, long timeout) {
        super(timeout);
        this.identifier = identifier;
        this.topic = topic;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getTopic() {
        return topic.getText();
    }
}
