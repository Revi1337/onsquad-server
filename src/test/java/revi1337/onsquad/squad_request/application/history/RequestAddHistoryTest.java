package revi1337.onsquad.squad_request.application.history;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import revi1337.onsquad.history.domain.HistoryType;
import revi1337.onsquad.history.domain.entity.HistoryEntity;
import revi1337.onsquad.squad_request.domain.model.SquadRequestContext.RequestAddedContext;

class RequestAddHistoryTest {

    @Test
    void constructor() {
        RequestAddedContext context = new RequestAddedContext(
                1L,
                "크루A",
                10L,
                "스쿼드B",
                50L,
                "리더",
                100L,
                2L,
                "태영"
        );

        RequestAddHistory history = new RequestAddHistory(context);

        assertSoftly(softly -> {
            softly.assertThat(history.getMemberId()).isEqualTo(context.requesterId());
            softly.assertThat(history.getType()).isEqualTo(HistoryType.SQUAD_REQUEST);
            softly.assertThat(history.getMessage()).isEqualTo("[크루A | 스쿼드B] 스쿼드 합류를 요청했습니다.");

            HistoryEntity entity = history.toEntity();
            softly.assertThat(entity.getMemberId()).isEqualTo(history.getMemberId());
            softly.assertThat(entity.getCrewId()).isEqualTo(history.getCrewId());
            softly.assertThat(entity.getSquadId()).isEqualTo(history.getSquadId());
            softly.assertThat(entity.getType()).isEqualTo(HistoryType.SQUAD_REQUEST);
        });
    }
}
