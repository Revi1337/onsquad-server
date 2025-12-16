package revi1337.onsquad.notification.infrastructure.sse;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class SseEmitterRepository { // TODO 다중 디바이스 허용 가능한지? 이건 상의해봐야함. 현재 정확히는 마지막으로 연결한 디바이스만 허용

    private final Map<String, NamedSseEmitter> emitters = new ConcurrentHashMap<>();

    public NamedSseEmitter save(String identifier, NamedSseEmitter emitter) {
        emitters.put(identifier, emitter);
        return emitter;
    }

    public Optional<NamedSseEmitter> findById(String identifier) {
        return Optional.ofNullable(emitters.get(identifier));
    }

    public List<NamedSseEmitter> findAll() {
        return emitters.values().stream().toList();
    }

    public NamedSseEmitter deleteById(String identifier) {
        return emitters.remove(identifier);
    }
}
