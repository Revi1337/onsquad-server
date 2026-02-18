package revi1337.onsquad.crew_request.application.history;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew_request.domain.model.CrewRequestContext.RequestRejectedContext;
import revi1337.onsquad.history.domain.HistoryType;

class RequestRejectHistoryTest {

    @Test
    void constructor() {
        RequestRejectedContext context = new RequestRejectedContext(
                1L,
                "crew-name",
                1L,
                2L,
                "requester-nickname"
        );

        RequestRejectHistory history = new RequestRejectHistory(context);

        assertSoftly(softly -> {
            softly.assertThat(history.getMemberId()).isEqualTo(context.rejecterId());
            softly.assertThat(history.getCrewId()).isEqualTo(context.crewId());
            softly.assertThat(history.getType()).isSameAs(HistoryType.CREW_REJECT);
            softly.assertThat(history.getMessage()).isEqualTo("[crew-name] requester-nickname 님의 크루 합류를 거절헀습니다.");
            softly.assertThat(history.getTimeStamp()).isNotNull();
        });
    }
}
