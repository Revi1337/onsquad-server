package revi1337.onsquad.crew.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.error.CrewBusinessException;
import revi1337.onsquad.crew.domain.error.CrewErrorCode;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;

class CrewPolicyTest {

    @Nested
    @DisplayName("마지막 남은 멤버 여부 확인")
    class isLastMemberRemaining {

        @Test
        @DisplayName("크루에 멤버가 한 명뿐이면 true를 반환한다")
        void isLastMemberRemaining() {
            Crew crew = createCrew(1L, createRevi(1L));

            boolean lastMemberRemaining = CrewPolicy.isLastMemberRemaining(crew);

            assertThat(lastMemberRemaining).isTrue();
        }
    }

    @Nested
    @DisplayName("크루 수정 권한 확인")
    class canModifyCrew {

        @Test
        @DisplayName("방장은 크루를 수정할 수 있다")
        void canModifyCrew() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember owner = crew.getCrewMembers().get(0);

            boolean canModifyCrew = CrewPolicy.canModify(owner);

            assertThat(canModifyCrew).isTrue();
        }

        @Test
        @DisplayName("매니저는 크루를 수정할 수 없다")
        void canModifyCrew2() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember manager = createManagerCrewMember(crew, createAndong(2L));

            boolean canModifyCrew = CrewPolicy.canModify(manager);

            assertThat(canModifyCrew).isFalse();
        }

        @Test
        @DisplayName("일반 멤버는 크루를 수정할 수 없다")
        void canModifyCrew3() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember general = createGeneralCrewMember(crew, createAndong(2L));
            crew.addCrewMember(general);

            boolean canModifyCrew = CrewPolicy.canModify(general);

            assertThat(canModifyCrew).isFalse();
        }
    }

    @Nested
    @DisplayName("크루 삭제 권한 확인")
    class canDeleteCrew {

        @Test
        @DisplayName("방장은 크루를 삭제할 수 있다")
        void canDeleteCrew() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember owner = crew.getCrewMembers().get(0);

            boolean canDeleteCrew = CrewPolicy.canDelete(owner);

            assertThat(canDeleteCrew).isTrue();
        }

        @Test
        @DisplayName("매니저는 크루를 삭제할 수 없다")
        void canDeleteCrew2() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember manager = createManagerCrewMember(crew, createAndong(2L));
            crew.addCrewMember(manager);

            boolean canDeleteCrew = CrewPolicy.canDelete(manager);

            assertThat(canDeleteCrew).isFalse();
        }

        @Test
        @DisplayName("일반 멤버는 크루를 삭제할 수 없다")
        void canDeleteCrew3() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember general = createGeneralCrewMember(crew, createAndong(2L));
            crew.addCrewMember(general);

            boolean canDeleteCrew = CrewPolicy.canDelete(general);

            assertThat(canDeleteCrew).isFalse();
        }
    }

    @Nested
    @DisplayName("크루 관리 권한 확인")
    class canManageCrew {

        @Test
        @DisplayName("방장은 크루 관리 권한이 있다")
        void canManageCrew() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember owner = crew.getCrewMembers().get(0);

            boolean canManageCrew = CrewPolicy.canManage(owner);

            assertThat(canManageCrew).isTrue();
        }

        @Test
        @DisplayName("매니저는 크루 관리 권한이 있다")
        void canManageCrew2() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember manager = createManagerCrewMember(crew, createAndong(2L));
            crew.addCrewMember(manager);

            boolean canManageCrew = CrewPolicy.canManage(manager);

            assertThat(canManageCrew).isTrue();
        }

        @Test
        @DisplayName("일반 멤버는 크루 관리 권한이 없다")
        void canManageCrew3() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember general = createGeneralCrewMember(crew, createAndong(2L));
            crew.addCrewMember(general);

            boolean canManageCrew = CrewPolicy.canManage(general);

            assertThat(canManageCrew).isFalse();
        }
    }

    @Nested
    @DisplayName("크루 수정 가능 여부 검증")
    class ensureCrewUpdatable {

        @Test
        @DisplayName("수정 권한이 없는 경우 예외가 발생한다")
        void ensureCrewUpdatable() {
            Crew crew = createCrew(1L, createRevi(1L));
            Long memberId = 2L;

            assertThatThrownBy(() -> CrewPolicy.ensureModifiable(crew, memberId))
                    .isExactlyInstanceOf(CrewBusinessException.InsufficientAuthority.class)
                    .hasMessage(CrewErrorCode.INSUFFICIENT_UPDATE_AUTHORITY.getDescription());
        }
    }

    @Nested
    @DisplayName("크루 삭제 가능 여부 검증")
    class ensureCrewDeletable {

        @Test
        @DisplayName("삭제 권한이 없는 경우 예외가 발생한다")
        void ensureCrewDeletable() {
            Crew crew = createCrew(1L, createRevi(1L));
            Long memberId = 2L;

            assertThatThrownBy(() -> CrewPolicy.ensureDeletable(crew, memberId))
                    .isExactlyInstanceOf(CrewBusinessException.InsufficientAuthority.class)
                    .hasMessage(CrewErrorCode.INSUFFICIENT_DELETE_AUTHORITY.getDescription());
        }
    }

    @Nested
    @DisplayName("크루 이미지 수정 가능 여부 검증")
    class ensureCrewImageUpdatable {

        @Test
        @DisplayName("이미지 수정 권한이 없는 경우 예외가 발생한다")
        void ensureCrewImageUpdatable() {
            Crew crew = createCrew(1L, createRevi(1L));
            Long memberId = 2L;

            assertThatThrownBy(() -> CrewPolicy.ensureImageModifiable(crew, memberId))
                    .isExactlyInstanceOf(CrewBusinessException.InsufficientAuthority.class)
                    .hasMessage(CrewErrorCode.INSUFFICIENT_IMAGE_UPDATE_AUTHORITY.getDescription());
        }
    }

    @Nested
    @DisplayName("크루 이미지 삭제 가능 여부 검증")
    class ensureCrewImageDeletable {

        @Test
        @DisplayName("이미지 삭제 권한이 없는 경우 예외가 발생한다")
        void ensureCrewImageDeletable() {
            Crew crew = createCrew(1L, createRevi(1L));
            Long memberId = 2L;

            assertThatThrownBy(() -> CrewPolicy.ensureImageDeletable(crew, memberId))
                    .isExactlyInstanceOf(CrewBusinessException.InsufficientAuthority.class)
                    .hasMessage(CrewErrorCode.INSUFFICIENT_IMAGE_DELETE_AUTHORITY.getDescription());
        }
    }

    @Nested
    @DisplayName("참여자 목록 접근 권한 검증")
    class ensureReadParticipantsAccessible {

        @Test
        @DisplayName("방장은 참여자 목록에 접근 가능하다")
        void ensureReadParticipantsAccessible2() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember owner = crew.getCrewMembers().get(0);

            assertDoesNotThrow(() -> CrewPolicy.ensureParticipantsReadable(owner));
        }

        @Test
        @DisplayName("매니저는 참여자 목록에 접근 가능하다")
        void ensureReadParticipantsAccessible1() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember manager = createManagerCrewMember(crew, createAndong(2L));
            crew.addCrewMember(manager);

            assertDoesNotThrow(() -> CrewPolicy.ensureParticipantsReadable(manager));
        }

        @Test
        @DisplayName("일반 멤버는 참여자 목록에 접근할 수 없어 예외가 발생한다")
        void ensureReadParticipantsAccessible3() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember general = createGeneralCrewMember(crew, createAndong(2L));
            crew.addCrewMember(general);

            assertThatThrownBy(() -> CrewPolicy.ensureParticipantsReadable(general))
                    .isExactlyInstanceOf(CrewBusinessException.InsufficientAuthority.class)
                    .hasMessage(CrewErrorCode.INSUFFICIENT_READ_PARTICIPANTS_AUTHORITY.getDescription());
        }
    }

    @Nested
    @DisplayName("크루 관리 페이지 접근 권한 검증")
    class ensureCanManagementCrew {

        @Test
        @DisplayName("매니저는 관리 페이지에 접근 가능하다")
        void ensureCanManagementCrew1() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember manager = createManagerCrewMember(crew, createAndong(2L));
            crew.addCrewMember(manager);

            assertDoesNotThrow(() -> CrewPolicy.ensureManageable(manager));
        }

        @Test
        @DisplayName("방장은 관리 페이지에 접근 가능하다")
        void ensureCanManagementCrew2() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember owner = crew.getCrewMembers().get(0);

            assertDoesNotThrow(() -> CrewPolicy.ensureManageable(owner));
        }

        @Test
        @DisplayName("일반 멤버는 관리 페이지에 접근할 수 없어 예외가 발생한다")
        void ensureCanManagementCrew3() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember general = createGeneralCrewMember(crew, createAndong(2L));
            crew.addCrewMember(general);

            assertThatThrownBy(() -> CrewPolicy.ensureParticipantsReadable(general))
                    .isExactlyInstanceOf(CrewBusinessException.InsufficientAuthority.class)
                    .hasMessage(CrewErrorCode.INSUFFICIENT_READ_PARTICIPANTS_AUTHORITY.getDescription());
        }
    }

    @Nested
    @DisplayName("크루 탈퇴 가능 여부 검증")
    class ensureCanLeaveCrew {

        @Test
        @DisplayName("일반 멤버는 크루를 탈퇴할 수 있다")
        void ensureCanLeaveCrew1() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember general = createGeneralCrewMember(crew, createAndong(2L));
            crew.addCrewMember(general);

            assertDoesNotThrow(() -> CrewPolicy.ensureLeavable(crew, general));
        }

        @Test
        @DisplayName("매니저는 크루를 탈퇴할 수 있다")
        void ensureCanLeaveCrew2() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember manager = createManagerCrewMember(crew, createAndong(2L));
            crew.addCrewMember(manager);

            assertDoesNotThrow(() -> CrewPolicy.ensureLeavable(crew, manager));
        }

        @Test
        @DisplayName("다른 멤버가 남아있는 경우 방장은 탈퇴할 수 없다")
        void ensureCanLeaveCrew3() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember owner = crew.getCrewMembers().get(0);
            CrewMember manager = createManagerCrewMember(crew, createAndong(2L));
            crew.addCrewMember(manager);

            assertThatThrownBy(() -> CrewPolicy.ensureLeavable(crew, owner))
                    .isExactlyInstanceOf(CrewBusinessException.InsufficientAuthority.class)
                    .hasMessage(CrewErrorCode.INSUFFICIENT_LEAVE_CREW_AUTHORITY.getDescription());
        }

        @Test
        @DisplayName("마지막 남은 멤버가 방장인 경우 탈퇴(파기)할 수 있다.")
        void ensureCanLeaveCrew4() {
            Crew crew = createCrew(1L, createRevi(1L));
            CrewMember owner = crew.getCrewMembers().get(0);

            assertDoesNotThrow(() -> CrewPolicy.ensureLeavable(crew, owner));
        }
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }
}
