package revi1337.onsquad.squad_member.domain;

import static org.assertj.core.api.Assertions.assertThat;
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
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.error.SquadMemberBusinessException;

class SquadMemberPolicyTest {

    @Nested
    @DisplayName("본인 여부 확인 테스트")
    class isMe {

        @Test
        @DisplayName("크루 멤버와 스쿼드 멤버의 식별자가 같으면 본인으로 판정한다.")
        void isMe1() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            CrewMember crewMember = createGeneralCrewMember(crew, createMember(2L));
            SquadMember squadMember = createGeneralSquadMember(squad, createMember(2L));

            boolean isMe = SquadMemberPolicy.isMe(crewMember, squadMember);

            assertThat(isMe).isTrue();
        }

        @Test
        @DisplayName("크루 멤버와 스쿼드 멤버의 식별자가 다르면 본인이 아닌 것으로 판정한다.")
        void isMe2() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            CrewMember crewMember = createGeneralCrewMember(crew, createMember(2L));
            SquadMember squadMember = createGeneralSquadMember(squad, createMember(3L));

            boolean isMe = SquadMemberPolicy.isMe(crewMember, squadMember);

            assertThat(isMe).isFalse();
        }

        @Test
        @DisplayName("두 스쿼드 멤버의 식별자가 같으면 본인으로 판정한다.")
        void isMe3() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            SquadMember squadMember1 = createGeneralSquadMember(squad, createMember(2L));
            SquadMember squadMember2 = createGeneralSquadMember(squad, createMember(2L));

            boolean isMe = SquadMemberPolicy.isMe(squadMember1, squadMember2);

            assertThat(isMe).isTrue();
        }

        @Test
        @DisplayName("두 스쿼드 멤버의 식별자가 다르면 본인이 아닌 것으로 판정한다.")
        void isMe4() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            SquadMember squadMember1 = createGeneralSquadMember(squad, createMember(2L));
            SquadMember squadMember2 = createGeneralSquadMember(squad, createMember(3));

            boolean isMe = SquadMemberPolicy.isMe(squadMember1, squadMember2);

            assertThat(isMe).isFalse();
        }
    }

    @Nested
    @DisplayName("상대방 추방 가능 여부 테스트")
    class canKick {

        @Test
        @DisplayName("자신이 크루 오너일 때, 상대방이 스쿼드 리더가 아니면 추방할 수 있다.")
        void success1() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            CrewMember crewOwner = createOwnerCrewMember(crew, createMember(2L));
            SquadMember squadMember = createGeneralSquadMember(squad, createMember(3L));

            boolean canKick = SquadMemberPolicy.canKick(crewOwner, squadMember);

            assertThat(canKick).isTrue();
        }

        @Test
        @DisplayName("자신이 스쿼드 리더일 때, 상대방이 스쿼드 리더가 아니면 추방할 수 있다.")
        void success2() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            SquadMember squadMember1 = createLeaderSquadMember(squad, createMember(2L));
            SquadMember squadMember2 = createGeneralSquadMember(squad, createMember(3L));

            boolean canKick = SquadMemberPolicy.canKick(squadMember1, squadMember2);

            assertThat(canKick).isTrue();
        }

        @Test
        @DisplayName("자신이 크루 오너가 아니면 상대방을 추방할 수 없다.")
        void fail1() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            CrewMember crewOwner = createManagerCrewMember(crew, createMember(2L));
            SquadMember squadMember = createGeneralSquadMember(squad, createMember(3L));

            boolean canKick = SquadMemberPolicy.canKick(crewOwner, squadMember);

            assertThat(canKick).isFalse();
        }

        @Test
        @DisplayName("자신이 크루 오너일 때, 상대방이 스쿼드 리더면 추방할 수 없다.")
        void fail2() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            CrewMember crewOwner = createOwnerCrewMember(crew, createMember(2L));
            SquadMember squadMember = createLeaderSquadMember(squad, createMember(3L));

            boolean canKick = SquadMemberPolicy.canKick(crewOwner, squadMember);

            assertThat(canKick).isFalse();
        }

        @Test
        @DisplayName("자신이 스쿼드 리더가 아니면 상대방을 추방할 수 없다.")
        void fail3() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            SquadMember squadMember1 = createGeneralSquadMember(squad, createMember(2L));
            SquadMember squadMember2 = createGeneralSquadMember(squad, createMember(3L));

            boolean canKick = SquadMemberPolicy.canKick(squadMember1, squadMember2);

            assertThat(canKick).isFalse();
        }

        @Test
        @DisplayName("자신이 스쿼드 리더일 때, 상대방이 스쿼드 리더면 추방할 수 없다.")
        void fail4() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            SquadMember squadMember1 = createLeaderSquadMember(squad, createMember(2L));
            SquadMember squadMember2 = createLeaderSquadMember(squad, createMember(3L));

            boolean canKick = SquadMemberPolicy.canKick(squadMember1, squadMember2);

            assertThat(canKick).isFalse();
        }
    }

    @Nested
    @DisplayName("리더 위임 가능 여부 테스트")
    class canDelegateLeader {

        @Test
        @DisplayName("자신이 크루 오너일 때, 상대방이 스쿼드 리더가 아니면 리더를 위임할 수 있다.")
        void success1() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            CrewMember crewOwner = createOwnerCrewMember(crew, createMember(2L));
            SquadMember squadMember = createGeneralSquadMember(squad, createMember(3L));

            boolean canDelegateLeader = SquadMemberPolicy.canDelegateLeader(crewOwner, squadMember);

            assertThat(canDelegateLeader).isTrue();
        }

        @Test
        @DisplayName("자신이 스쿼드 리더일 때, 상대방이 스쿼드 리더가 아니면 리더를 위임할 수 있다.")
        void success2() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            SquadMember squadMember1 = createLeaderSquadMember(squad, createMember(2L));
            SquadMember squadMember2 = createGeneralSquadMember(squad, createMember(3L));

            boolean canDelegateLeader = SquadMemberPolicy.canDelegateLeader(squadMember1, squadMember2);

            assertThat(canDelegateLeader).isTrue();
        }

        @Test
        @DisplayName("자신이 크루 오너가 아니면 리더를 위임할 수 없다.")
        void fail1() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            CrewMember crewOwner = createManagerCrewMember(crew, createMember(2L));
            SquadMember squadMember = createGeneralSquadMember(squad, createMember(3L));

            boolean canDelegateLeader = SquadMemberPolicy.canDelegateLeader(crewOwner, squadMember);

            assertThat(canDelegateLeader).isFalse();
        }

        @Test
        @DisplayName("자신이 크루 오너일 때, 상대방이 스쿼드 리더면 리더를 위임할 수 없다.")
        void fail2() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            CrewMember crewOwner = createOwnerCrewMember(crew, createMember(2L));
            SquadMember squadMember = createLeaderSquadMember(squad, createMember(3L));

            boolean canDelegateLeader = SquadMemberPolicy.canDelegateLeader(crewOwner, squadMember);

            assertThat(canDelegateLeader).isFalse();
        }

        @Test
        @DisplayName("자신이 스쿼드 리더가 아니면 리더를 위임할 수 없다.")
        void fail3() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            SquadMember squadMember1 = createGeneralSquadMember(squad, createMember(2L));
            SquadMember squadMember2 = createGeneralSquadMember(squad, createMember(3L));

            boolean canDelegateLeader = SquadMemberPolicy.canDelegateLeader(squadMember1, squadMember2);

            assertThat(canDelegateLeader).isFalse();
        }

        @Test
        @DisplayName("자신이 스쿼드 리더일 때, 상대방이 스쿼드 리더면 리더를 위임할 수 없다.")
        void fail4() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            SquadMember squadMember1 = createLeaderSquadMember(squad, createMember(2L));
            SquadMember squadMember2 = createLeaderSquadMember(squad, createMember(3L));

            boolean canDelegateLeader = SquadMemberPolicy.canDelegateLeader(squadMember1, squadMember2);

            assertThat(canDelegateLeader).isFalse();
        }
    }

    @Nested
    @DisplayName("추방 권한 검증 테스트")
    class ensureKickable {

        @Test
        @DisplayName("스쿼드 리더는 추방 권한 검증을 통과한다.")
        void success() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            SquadMember squadMember = createLeaderSquadMember(squad, createMember(2L));

            assertThatCode(() -> SquadMemberPolicy.ensureKickable(squadMember)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("스쿼드 리더가 아니면 추방 권한 검증 시 예외가 발생한다.")
        void fail() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            SquadMember squadMember = createGeneralSquadMember(squad, createMember(2L));

            assertThatThrownBy(() -> SquadMemberPolicy.ensureKickable(squadMember))
                    .isExactlyInstanceOf(SquadMemberBusinessException.InsufficientAuthority.class);
        }
    }

    @Nested
    @DisplayName("리더 위임 권한 검증 테스트")
    class ensureLeaderDelegatable {

        @Test
        @DisplayName("스쿼드 리더는 리더 위임 권한 검증을 통과한다.")
        void success() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            SquadMember squadMember = createLeaderSquadMember(squad, createMember(2L));

            assertThatCode(() -> SquadMemberPolicy.ensureLeaderDelegatable(squadMember)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("스쿼드 리더가 아니면 리더 위임 권한 검증 시 예외가 발생한다.")
        void fail() {
            Member member1 = createMember(1L);
            Crew crew = createCrew(1L, member1);
            Squad squad = createSquad(1L, crew, member1);
            SquadMember squadMember = createGeneralSquadMember(squad, createMember(2L));

            assertThatThrownBy(() -> SquadMemberPolicy.ensureLeaderDelegatable(squadMember))
                    .isExactlyInstanceOf(SquadMemberBusinessException.InsufficientAuthority.class);
        }
    }

    private CrewMember createOwnerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.owner(crew, member, LocalDateTime.now());
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }
}
