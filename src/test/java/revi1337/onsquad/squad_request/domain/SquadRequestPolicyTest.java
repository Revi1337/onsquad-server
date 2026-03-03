package revi1337.onsquad.squad_request.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.createGeneralSquadMember;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.createLeaderSquadMember;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_request.domain.entity.SquadRequest;
import revi1337.onsquad.squad_request.domain.error.SquadRequestBusinessException;
import revi1337.onsquad.squad_request.domain.error.SquadRequestErrorCode;

class SquadRequestPolicyTest {

    @Nested
    @DisplayName("스쿼드 신청 참조 정합성 검증")
    class ensureMatchSquad {

        @Test
        @DisplayName("신청 내역의 스쿼드 식별자와 요청 식별자가 일치하면 검증을 통과한다.")
        void success() {
            Member member = createMember(1L);
            Crew crew = createCrew(1L, member);
            Squad squad = createSquad(1L, crew, member);
            SquadRequest request = createSquadRequest(squad, createMember(2L));

            assertThatCode(() -> SquadRequestPolicy.ensureMatchSquad(request, 1L))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("신청 내역의 스쿼드 식별자와 요청 식별자가 다르면 예외가 발생한다.")
        void fail() {
            Member member = createMember(1L);
            Crew crew = createCrew(1L, member);
            Squad squad = createSquad(1L, crew, member);
            SquadRequest request = createSquadRequest(squad, createMember(2L));

            assertThatThrownBy(() -> SquadRequestPolicy.ensureMatchSquad(request, 2L))
                    .isExactlyInstanceOf(SquadRequestBusinessException.MismatchReference.class)
                    .hasMessage(SquadRequestErrorCode.MISMATCH_SQUAD_REFERENCE.getDescription());
        }
    }

    @Nested
    @DisplayName("신청 목록 조회 권한 검증")
    class ensureReadRequests {

        @Test
        @DisplayName("스쿼드 리더는 신청 목록을 조회할 수 있다.")
        void success() {
            SquadMember leader = createLeaderSquadMember(null, createMember(1L));

            assertThatCode(() -> SquadRequestPolicy.ensureReadRequests(leader))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("스쿼드 리더가 아니면 신청 목록 조회 시 예외가 발생한다.")
        void fail() {
            SquadMember general = createGeneralSquadMember(null, createMember(1L));

            assertThatThrownBy(() -> SquadRequestPolicy.ensureReadRequests(general))
                    .isExactlyInstanceOf(SquadRequestBusinessException.InsufficientAuthority.class)
                    .hasMessage(SquadRequestErrorCode.INSUFFICIENT_READ_LIST_AUTHORITY.getDescription());
        }
    }

    @Nested
    @DisplayName("신청 승인 권한 검증")
    class ensureAcceptable {

        @Test
        @DisplayName("스쿼드 리더는 신청을 승인할 수 있다.")
        void success() {
            SquadMember leader = createLeaderSquadMember(null, createMember(1L));

            assertThatCode(() -> SquadRequestPolicy.ensureAcceptable(leader))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("스쿼드 리더가 아니면 신청 승인 시 예외가 발생한다.")
        void fail() {
            SquadMember general = createGeneralSquadMember(null, createMember(1L));

            assertThatThrownBy(() -> SquadRequestPolicy.ensureAcceptable(general))
                    .isExactlyInstanceOf(SquadRequestBusinessException.InsufficientAuthority.class)
                    .hasMessage(SquadRequestErrorCode.INSUFFICIENT_ACCEPT_AUTHORITY.getDescription());
        }
    }

    @Nested
    @DisplayName("신청 거절 권한 검증")
    class ensureRejectable {

        @Test
        @DisplayName("스쿼드 리더는 신청을 거절할 수 있다.")
        void success() {
            SquadMember leader = createLeaderSquadMember(null, createMember(1L));

            assertThatCode(() -> SquadRequestPolicy.ensureRejectable(leader))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("스쿼드 리더가 아니면 신청 거절 시 예외가 발생한다.")
        void fail() {
            SquadMember general = createGeneralSquadMember(null, createMember(1L));

            assertThatThrownBy(() -> SquadRequestPolicy.ensureRejectable(general))
                    .isExactlyInstanceOf(SquadRequestBusinessException.InsufficientAuthority.class)
                    .hasMessage(SquadRequestErrorCode.INSUFFICIENT_REJECT_AUTHORITY.getDescription());
        }
    }

    private static SquadRequest createSquadRequest(Squad squad, Member member) {
        return createSquadRequest(squad, member, LocalDateTime.now());
    }

    private static SquadRequest createSquadRequest(Squad squad, Member member, LocalDateTime requestAt) {
        return SquadRequest.of(squad, member, requestAt);
    }
}
