package revi1337.onsquad.squad.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.createGeneralSquadMember;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.error.SquadBusinessException;
import revi1337.onsquad.squad.domain.error.SquadErrorCode;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

class SquadPolicyTest {

    @Nested
    @DisplayName("스쿼드 삭제 권한 검증")
    class canDelete {

        @ParameterizedTest
        @MethodSource("provideDeletePermissionCases")
        @DisplayName("스쿼드 리더이거나 크루 삭제 권한이 있으면 스쿼드를 삭제할 수 있다.")
        void test(SquadMember squadMember, CrewMember crewMember, boolean expected) {
            assertThat(SquadPolicy.canDelete(squadMember, crewMember)).isEqualTo(expected);
        }

        private static Stream<Arguments> provideDeletePermissionCases() {
            Member member = createRevi(1L);
            Crew crew = createCrew(1L, member);
            Squad squad = createSquad(1L, crew, member);

            SquadMember leader = squad.getMembers().get(0);
            CrewMember crewGeneral = createGeneralCrewMember(crew, member);

            SquadMember general = createGeneralSquadMember(squad, member);
            CrewMember crewOwner = crew.getCrewMembers().get(0);

            return Stream.of(
                    Arguments.of(leader, crewGeneral, true),
                    Arguments.of(general, crewOwner, true),
                    Arguments.of(general, crewGeneral, false)
            );
        }
    }

    @Nested
    @DisplayName("스쿼드 관리 권한 검증")
    class ensureManageable {

        @ParameterizedTest
        @MethodSource("provideManageableCases")
        @DisplayName("크루 매니저 이상만 스쿼드 관리(생성/수정) 권한을 가진다.")
        void test(CrewMember me, boolean isSuccess) {
            if (isSuccess) {
                assertDoesNotThrow(() -> SquadPolicy.ensureManageable(me));
            } else {
                assertThatThrownBy(() -> SquadPolicy.ensureManageable(me))
                        .isExactlyInstanceOf(SquadBusinessException.InsufficientAuthority.class)
                        .hasMessage(SquadErrorCode.INSUFFICIENT_MANAGE_SQUAD_AUTHORITY.getDescription());
            }
        }

        private static Stream<Arguments> provideManageableCases() {
            Crew crew = createCrew(1L, createRevi(1L));
            Member member = createAndong(2L);

            return Stream.of(
                    Arguments.of(crew.getCrewMembers().get(0), true),
                    Arguments.of(createManagerCrewMember(crew, member), true),
                    Arguments.of(createGeneralCrewMember(crew, member), false)
            );
        }
    }

    @Nested
    @DisplayName("스쿼드 탈퇴 가능 여부 검증")
    class ensureLeavable {

        @Test
        @DisplayName("스쿼드 리더가 아니고 소속 스쿼드가 일치하면 탈퇴할 수 있다.")
        void successWhenGeneralMember() {
            Squad squad = createSquad(1L, createCrew(1L, createRevi(1L)), createAndong(2L));
            SquadMember me = createGeneralSquadMember(squad, createKwangwon(3L));
            squad.addMembers(me);

            assertDoesNotThrow(() -> SquadPolicy.ensureLeavable(squad, me));
        }

        @Test
        @DisplayName("리더여도 마지막 남은 멤버라면 탈퇴(스쿼드 파기)가 가능하다.")
        void successWhenLastLeader() {
            Squad squad = createSquad(1L, createCrew(1L, createRevi(1L)), createAndong(2L));
            SquadMember leader = squad.getMembers().get(0);

            assertDoesNotThrow(() -> SquadPolicy.ensureLeavable(squad, leader));
        }

        @Test
        @DisplayName("다른 멤버가 남아있는데 리더가 탈퇴하려 하면 예외가 발생한다.")
        void failWhenLeaderNotLast() {
            Squad squad = createSquad(1L, createCrew(1L, createRevi(1L)), createAndong(2L));
            SquadMember leader = squad.getMembers().get(0);
            squad.addMembers(createGeneralSquadMember(squad, createKwangwon(3L)));

            assertThatThrownBy(() -> SquadPolicy.ensureLeavable(squad, leader))
                    .isExactlyInstanceOf(SquadBusinessException.InsufficientAuthority.class)
                    .hasMessage(SquadErrorCode.INSUFFICIENT_LEAVE_SQUAD_AUTHORITY.getDescription());
        }

        @Test
        @DisplayName("탈퇴하려는 스쿼드 정보와 멤버가 소속된 스쿼드 정보가 다르면 예외가 발생한다.")
        void failWhenMismatchSquad() {
            Squad squad1 = createSquad(1L, createCrew(1L, createRevi(1L)), createAndong(2L));
            Squad squad2 = createSquad(2L, createCrew(1L, createRevi(1L)), createAndong(2L));
            SquadMember meFromSquad2 = createGeneralSquadMember(squad2, createKwangwon(3L));

            assertThatThrownBy(() -> SquadPolicy.ensureLeavable(squad1, meFromSquad2))
                    .isExactlyInstanceOf(SquadBusinessException.MismatchReference.class)
                    .hasMessage(SquadErrorCode.MISMATCH_SQUAD_REFERENCE.getDescription());
        }
    }

    @Nested
    @DisplayName("스쿼드 참여자 목록 열람 권한 확인")
    class readParticipants {

        @Test
        @DisplayName("크루 Owner는 스쿼드 참여자 목록을 읽을 수 있다.")
        void canReadWhenOwner() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember owner = crew.getCrewMembers().get(0);

            assertThat(SquadPolicy.canReadParticipants(owner)).isTrue();
        }

        @Test
        @DisplayName("크루 Owner가 아니면 스쿼드 참여자 목록을 읽을 수 없다.")
        void cannotReadWhenNotOwner() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember manager = createManagerCrewMember(crew, createAndong(2L));

            assertThat(SquadPolicy.cannotReadParticipants(manager)).isTrue();
        }
    }

    private static CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }

    private static CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }
}
