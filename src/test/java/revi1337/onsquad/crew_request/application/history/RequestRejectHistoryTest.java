package revi1337.onsquad.crew_request.application.history;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew_request.domain.model.CrewRequestContext.RequestRejectedContext;
import revi1337.onsquad.history.domain.HistoryType;

class RequestRejectHistoryTest {

    @Test
    @DisplayName("참가신청 거절 컨텍스트로 생성하면 거절자 ID, 크루 ID, CREW_REJECT 타입, 거절 메시지가 설정된다.")
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
            softly.assertThat(history.getMessage()).isEqualTo("[crew-name] requester-nickname 님의 크루 합류를 거절했습니다.");
            softly.assertThat(history.getTimeStamp()).isNotNull();
        });
    }
}
