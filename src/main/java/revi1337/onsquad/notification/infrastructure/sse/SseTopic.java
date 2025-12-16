package revi1337.onsquad.notification.infrastructure.sse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SseTopic {

    USER("user", "user:%s:sse:notification");

    private final String text;
    private final String sseFormat;

    public String toIdentifier(String... args) {
        return String.format(sseFormat, args);
    }
}
