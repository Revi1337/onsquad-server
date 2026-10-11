package revi1337.onsquad.crew_request.application.history;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew_request.domain.model.CrewRequestContext.RequestAddedContext;
import revi1337.onsquad.history.domain.HistoryType;

class RequestAddHistoryTest {

    @Test
    @DisplayName("요청 컨텍스트로 생성하면 요청자 ID, 크루 ID, CREW_REQUEST 타입, 요청 메시지와 기록 시각을 가진 이력이 만들어진다")
    void constructor() {
        RequestAddedContext context = new RequestAddedContext(
                1L,
                "crew-name",
                1L,
                3L,
                2L,
                "requester-nickname"
        );

        RequestAddHistory history = new RequestAddHistory(context);

        assertSoftly(softly -> {
            softly.assertThat(history.getMemberId()).isEqualTo(context.requesterId());
            softly.assertThat(history.getCrewId()).isEqualTo(context.crewId());
            softly.assertThat(history.getType()).isSameAs(HistoryType.CREW_REQUEST);
            softly.assertThat(history.getMessage()).isEqualTo("[crew-name] 크루 합류를 요청했습니다.");
            softly.assertThat(history.getTimeStamp()).isNotNull();
        });
    }
}
