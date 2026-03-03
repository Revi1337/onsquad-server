package revi1337.onsquad.squad_request.application.history;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;
import revi1337.onsquad.squad_request.domain.model.SquadRequestContext.RequestRejectedContext;

class RequestRejectHistoryTest {

    @Test
    void constructor() {
        RequestRejectedContext context = new RequestRejectedContext(
                1L,
                "크루A",
                10L,
                "스쿼드B",
                1L,
                2L,
                "태영"
        );

        RequestRejectHistory history = new RequestRejectHistory(context);

        assertSoftly(softly -> {
            softly.assertThat(history.getMemberId()).isEqualTo(context.rejecterId());
            softly.assertThat(history.getType()).isEqualTo(HistoryType.SQUAD_REJECT);
            softly.assertThat(history.getMessage()).isEqualTo("[크루A | 스쿼드B] 태영 님의 스쿼드 합류를 거절헀습니다.");

            HistoryEntity entity = history.toEntity();
            softly.assertThat(entity.getType()).isEqualTo(HistoryType.SQUAD_REJECT);
            softly.assertThat(entity.getSquadId()).isEqualTo(context.squadId());
        });
    }
}
