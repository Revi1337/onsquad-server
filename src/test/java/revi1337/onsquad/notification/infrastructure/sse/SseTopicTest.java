package revi1337.onsquad.notification.infrastructure.sse;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SseTopicTest {

    @Test
    @DisplayName("사용자 식별자(ID)를 기반으로 SSE 커넥션을 고유하게 식별할 수 있는 ID를 생성한다.")
    void toIdentifier() {
        String userId = "1";
        SseTopic user = SseTopic.USER;

        String identifier = user.toIdentifier(userId);

        assertThat(identifier).isEqualTo("user:1:sse:notification");
    }
}
