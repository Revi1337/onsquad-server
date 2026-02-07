package revi1337.onsquad.notification.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.common.config.web.ObjectMapperConfig;

@JsonTest
@Import({ObjectMapperConfig.class, NotificationPayloadDeserializer.class})
class NotificationPayloadDeserializerTest {

    @Autowired
    private NotificationPayloadDeserializer deserializer;

    @Test
    @DisplayName("jsonString 이 json 포맷이라면 성공한다.")
    void deserialize() {
        String json = "{\"message\": \"hello\", \"count\": 1}";

        JsonNode jsonNode = deserializer.deserialize(json);

        assertSoftly(softly -> {
            softly.assertThat(jsonNode.isObject()).isTrue();
            softly.assertThat(jsonNode.get("message").asText()).isEqualTo("hello");
            softly.assertThat(jsonNode.get("count").asInt()).isEqualTo(1);
        });
    }

    @Test
    @DisplayName("jsonString 이 배열형태여도 파싱할 수 있다.")
    void deserializeFail1() {
        String json = "[\"활발한\",\"트랜디한\"]";

        JsonNode jsonNode = deserializer.deserialize(json);

        assertSoftly(softly -> {
            softly.assertThat(jsonNode.isArray()).isTrue();
        });
    }

    @Test
    @DisplayName("유효하지 않은 jsonString 이면 빈 json 을 반환한다.")
    void deserializeFail2() {
        String json = "{invalid-json}";

        JsonNode jsonNode = deserializer.deserialize(json);

        assertSoftly(softly -> {
            softly.assertThat(jsonNode.isObject()).isTrue();
            softly.assertThat(jsonNode).isEmpty();
        });
    }
}
