package revi1337.onsquad.notification.infrastructure.sse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ContextConfiguration(classes = SseEmitterRepository.class)
@ExtendWith(SpringExtension.class)
class SseEmitterRepositoryTest {

    @Autowired
    private SseEmitterRepository sseEmitterRepository;

    @BeforeEach
    void setUp() {
        Map<String, NamedSseEmitter> emitters = (Map<String, NamedSseEmitter>) ReflectionTestUtils.getField(sseEmitterRepository, "emitters");
        emitters.clear();
    }

    @Test
    @DisplayName("생성된 SSE 커넥션 객체를 고유 식별자와 함께 메모리에 저장한다.")
    void save() {
        String identifier = UUID.randomUUID().toString();
        NamedSseEmitter namedSseEmitter = createNamedSseEmitter(identifier);

        NamedSseEmitter savedEmitter = sseEmitterRepository.save(identifier, namedSseEmitter);

        assertThat(savedEmitter).isEqualTo(namedSseEmitter);
    }

    @Test
    @DisplayName("고유 식별자를 이용해 메모리에 보관 중인 특정 SSE 커넥션을 조회한다.")
    void findById() {
        String identifier = UUID.randomUUID().toString();
        sseEmitterRepository.save(identifier, createNamedSseEmitter(identifier));

        Optional<NamedSseEmitter> emitterOpt = sseEmitterRepository.findById(identifier);

        assertSoftly(softly -> {
            softly.assertThat(emitterOpt).isPresent();
            softly.assertThat(emitterOpt.get().getIdentifier()).isEqualTo(identifier);
        });
    }

    @Test
    @DisplayName("현재 서버 메모리에 활성화되어 있는 모든 SSE 커넥션 목록을 조회한다.")
    void findAll() {
        String identifier1 = UUID.randomUUID().toString();
        String identifier2 = UUID.randomUUID().toString();
        sseEmitterRepository.save(identifier1, createNamedSseEmitter(identifier1));
        sseEmitterRepository.save(identifier2, createNamedSseEmitter(identifier2));

        List<NamedSseEmitter> emitters = sseEmitterRepository.findAll();

        assertSoftly(softly -> {
            softly.assertThat(emitters).hasSize(2);
        });
    }

    @Test
    @DisplayName("연결이 종료되거나 유효하지 않은 SSE 커넥션을 고유 식별자로 삭제한다.")
    void deleteById() {
        String identifier1 = UUID.randomUUID().toString();
        String identifier2 = UUID.randomUUID().toString();
        sseEmitterRepository.save(identifier1, createNamedSseEmitter(identifier1));
        sseEmitterRepository.save(identifier2, createNamedSseEmitter(identifier2));

        NamedSseEmitter deleted = sseEmitterRepository.deleteById(identifier1);

        assertSoftly(softly -> {
            softly.assertThat(deleted.getIdentifier()).isEqualTo(identifier1);
            softly.assertThat(sseEmitterRepository.findAll()).hasSize(1);
        });
    }

    private NamedSseEmitter createNamedSseEmitter(String identifier) {
        SseTopic topic = SseTopic.USER;
        long timeout = 60 * 60 * 1000L;
        return new NamedSseEmitter(identifier, topic, timeout);
    }
}
