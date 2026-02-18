package revi1337.onsquad.crew_request.application.history;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew_request.domain.model.CrewRequestContext.RequestAcceptedContext;
import revi1337.onsquad.history.domain.HistoryType;

class RequestAcceptHistoryTest {

    @Test
    void constructor() {
        RequestAcceptedContext context = new RequestAcceptedContext(
                1L,
                "crew-name",
                1L,
                2L,
                "requester-nickname"
        );

        RequestAcceptHistory history = new RequestAcceptHistory(context);

        assertSoftly(softly -> {
            softly.assertThat(history.getMemberId()).isEqualTo(context.accepterId());
            softly.assertThat(history.getCrewId()).isEqualTo(context.crewId());
            softly.assertThat(history.getType()).isSameAs(HistoryType.CREW_ACCEPT);
            softly.assertThat(history.getMessage()).isEqualTo("[crew-name] requester-nickname 님의 크루 합류를 수락했습니다.");
            softly.assertThat(history.getTimeStamp()).isNotNull();
        });
    }
}
