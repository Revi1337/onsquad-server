package revi1337.onsquad.squad_request.application.history;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;
import revi1337.onsquad.squad_request.domain.model.SquadRequestContext.RequestAcceptedContext;

class RequestAcceptHistoryTest {

    @Test
    void constructor() {
        RequestAcceptedContext context = new RequestAcceptedContext(
                1L,
                "크루A",
                10L,
                "스쿼드B",
                1L,
                2L,
                "태영"
        );

        RequestAcceptHistory history = new RequestAcceptHistory(context);

        assertSoftly(softly -> {
            softly.assertThat(history.getMemberId()).isEqualTo(context.accepterId());
            softly.assertThat(history.getType()).isEqualTo(HistoryType.SQUAD_ACCEPT);
            softly.assertThat(history.getMessage()).isEqualTo("[크루A | 스쿼드B] 태영 님의 스쿼드 합류를 수락했습니다.");

            HistoryEntity entity = history.toEntity();
            softly.assertThat(entity.getMemberId()).isEqualTo(history.getMemberId());
            softly.assertThat(entity.getMessage()).isEqualTo(history.getMessage());
            softly.assertThat(entity.getRecordedAt()).isNotNull();
        });
    }
}
