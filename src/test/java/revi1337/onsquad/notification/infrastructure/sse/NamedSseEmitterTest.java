package revi1337.onsquad.notification.infrastructure.sse;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NamedSseEmitterTest {

    @Test
    @DisplayName("식별자와 토픽, 타임아웃 정보를 전달하면 고유 정보가 포함된 SSE 에미터 객체가 생성된다.")
    void constructor() {
        String identifier = UUID.randomUUID().toString();
        SseTopic topic = SseTopic.USER;
        long timeout = 60 * 60 * 1000L;

        NamedSseEmitter emitter = new NamedSseEmitter(identifier, topic, timeout);

        assertSoftly(softly -> {
            softly.assertThat(emitter.getIdentifier()).isEqualTo(identifier);
            softly.assertThat(emitter.getTopic()).isSameAs(SseTopic.USER.getText());
            softly.assertThat(emitter.getTimeout()).isEqualTo(timeout);
        });
    }
}
