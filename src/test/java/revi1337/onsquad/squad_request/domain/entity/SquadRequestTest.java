package revi1337.onsquad.squad_request.domain.entity;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad.domain.entity.Squad;

@DisplayName("스쿼드 신청 엔티티 테스트")
class SquadRequestTest {

    @Test
    @DisplayName("정적 팩토리 메서드 of를 통해 스쿼드 신청 객체를 생성한다.")
    void of() {
        Member owner = createMember(1L);
        Crew crew = createCrew(1L, owner);
        Squad squad = createSquad(1L, crew, owner);
        Member requester = createMember(2L);
        LocalDateTime requestAt = LocalDateTime.of(2024, 1, 1, 0, 0);

        SquadRequest squadRequest = SquadRequest.of(squad, requester, requestAt);

        assertSoftly(softly -> {
            softly.assertThat(squadRequest).isNotNull();
            softly.assertThat(squadRequest.getSquad()).isEqualTo(squad);
            softly.assertThat(squadRequest.getMember()).isEqualTo(requester);
            softly.assertThat(squadRequest.getRequestAt()).isEqualTo(requestAt);
        });
    }
}
