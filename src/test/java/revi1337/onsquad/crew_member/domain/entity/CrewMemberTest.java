package revi1337.onsquad.crew_member.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew_member.domain.CrewRole;
import revi1337.onsquad.member.domain.entity.Member;

class CrewMemberTest {

    @Nested
    @DisplayName("CrewMemberFactory 생성 검증")
    class factoryTest {

        @Test
        @DisplayName("GENERAL 팩토리로 생성하면 주어진 멤버와 크루를 가진 GENERAL 역할의 크루 멤버가 만들어진다")
        void createGeneral() {
            Member member = createRevi(1L);
            Crew crew = createCrew(2L, member);

            CrewMember crewMember = CrewMemberFactory.general(crew, member, LocalDateTime.now());

            assertThat(crewMember.getMember()).isEqualTo(member);
            assertThat(crewMember.getCrew()).isEqualTo(crew);
            assertThat(crewMember.getRole()).isSameAs(CrewRole.GENERAL);
        }

        @Test
        @DisplayName("MANAGER 팩토리로 생성하면 주어진 멤버와 크루를 가진 MANAGER 역할의 크루 멤버가 만들어진다")
        void createManager() {
            Member member = createRevi(1L);
            Crew crew = createCrew(2L, member);

            CrewMember crewMember = CrewMemberFactory.manager(crew, member, LocalDateTime.now());

            assertThat(crewMember.getMember()).isEqualTo(member);
            assertThat(crewMember.getCrew()).isEqualTo(crew);
            assertThat(crewMember.getRole()).isSameAs(CrewRole.MANAGER);
        }

        @Test
        @DisplayName("OWNER 팩토리로 생성하면 주어진 멤버와 크루를 가진 OWNER 역할의 크루 멤버가 만들어진다")
        void createOwner() {
            Member member = createRevi(1L);
            Crew crew = createCrew(2L, member);

            CrewMember crewMember = CrewMemberFactory.owner(crew, member, LocalDateTime.now());

            assertThat(crewMember.getMember()).isEqualTo(member);
            assertThat(crewMember.getCrew()).isEqualTo(crew);
            assertThat(crewMember.getRole()).isSameAs(CrewRole.OWNER);
        }
    }

    @Nested
    @DisplayName("CrewMember 도메인 로직 검증")
    class DomainLogicTest {

        @Test
        @DisplayName("크루를 탈퇴하면 멤버와 크루 연관관계가 끊기고 크루의 현재 인원이 1명 줄어든다")
        void leaveCrew() {
            Member member = createRevi(1L);
            Crew crew = createCrew(2L, member);
            CrewMember crewMember = CrewMemberFactory.general(crew, member, LocalDateTime.now());
            crew.addCrewMember(crewMember);

            crewMember.leaveCrew();

            assertThat(crewMember.getMember()).isNull();
            assertThat(crewMember.getCrew()).isNull();
            assertThat(crew.getCurrentSize()).isEqualTo(1);
        }

        @Test
        @DisplayName("크루 연관관계를 해제하면 크루 참조가 null 이 된다")
        void releaseCrew() {
            Member member = createRevi(1L);
            Crew crew = createCrew(member);
            CrewMember crewMember = CrewMemberFactory.general(crew, member, LocalDateTime.now());

            crewMember.releaseCrew();

            assertThat(crewMember.getCrew()).isNull();
        }

        @Test
        @DisplayName("owner 로 승급시키면 역할이 OWNER 로 변경된다")
        void promoteToOwner() {
            Member member = createRevi(1L);
            Crew crew = createCrew(member);
            CrewMember crewMember = CrewMemberFactory.general(crew, member, LocalDateTime.now());

            crewMember.promoteToOwner();

            assertThat(crewMember.getRole()).isSameAs(CrewRole.OWNER);
        }

        @Test
        @DisplayName("owner 를 강등시키면 역할이 GENERAL 로 변경된다")
        void demoteToGeneral() {
            Member member = createRevi(1L);
            Crew crew = createCrew(member);
            CrewMember crewMember = CrewMemberFactory.owner(crew, member, LocalDateTime.now());

            crewMember.demoteToGeneral();

            assertThat(crewMember.getRole()).isSameAs(CrewRole.GENERAL);
        }

        @Test
        @DisplayName("GENERAL 은 manager 보다 낮은 권한이다")
        void isLowerThanManager1() {
            Member member = createRevi(1L);
            Crew crew = createCrew(member);
            CrewMember crewMember = CrewMemberFactory.general(crew, member, LocalDateTime.now());

            boolean lowerThanManager = crewMember.isLowerThanManager();

            assertThat(lowerThanManager).isTrue();
        }

        @Test
        @DisplayName("MANAGER 는 manager 보다 낮은 권한이 아니다")
        void isLowerThanManager2() {
            Member member = createRevi(1L);
            Crew crew = createCrew(member);
            CrewMember crewMember = CrewMemberFactory.manager(crew, member, LocalDateTime.now());

            boolean lowerThanManager = crewMember.isLowerThanManager();

            assertThat(lowerThanManager).isFalse();
        }

        @Test
        @DisplayName("OWNER 는 manager 보다 낮은 권한이 아니다")
        void isLowerThanManager3() {
            Member member = createRevi(1L);
            Crew crew = createCrew(member);
            CrewMember crewMember = CrewMemberFactory.owner(crew, member, LocalDateTime.now());

            boolean lowerThanManager = crewMember.isLowerThanManager();

            assertThat(lowerThanManager).isFalse();
        }

        @Test
        @DisplayName("GENERAL 은 manager 이상 권한이 아니다")
        void isManagerOrHigher1() {
            Member member = createRevi(1L);
            Crew crew = createCrew(member);
            CrewMember crewMember = CrewMemberFactory.general(crew, member, LocalDateTime.now());

            boolean lowerThanManager = crewMember.isManagerOrHigher();

            assertThat(lowerThanManager).isFalse();
        }

        @Test
        @DisplayName("MANAGER 는 manager 이상 권한이다")
        void isManagerOrHigher2() {
            Member member = createRevi(1L);
            Crew crew = createCrew(member);
            CrewMember crewMember = CrewMemberFactory.manager(crew, member, LocalDateTime.now());

            boolean lowerThanManager = crewMember.isManagerOrHigher();

            assertThat(lowerThanManager).isTrue();
        }

        @Test
        @DisplayName("OWNER 는 manager 이상 권한이다")
        void isManagerOrHigher3() {
            Member member = createRevi(1L);
            Crew crew = createCrew(member);
            CrewMember crewMember = CrewMemberFactory.owner(crew, member, LocalDateTime.now());

            boolean lowerThanManager = crewMember.isManagerOrHigher();

            assertThat(lowerThanManager).isTrue();
        }

        @Test
        @DisplayName("GENERAL 은 manager 이하 권한이다")
        void isManagerOrLower1() {
            Member member = createRevi(1L);
            Crew crew = createCrew(member);
            CrewMember crewMember = CrewMemberFactory.general(crew, member, LocalDateTime.now());

            boolean managerOrLower = crewMember.isManagerOrLower();

            assertThat(managerOrLower).isTrue();
        }

        @Test
        @DisplayName("MANAGER 는 manager 이하 권한이다")
        void isManagerOrLower2() {
            Member member = createRevi(1L);
            Crew crew = createCrew(member);
            CrewMember crewMember = CrewMemberFactory.manager(crew, member, LocalDateTime.now());

            boolean managerOrLower = crewMember.isManagerOrLower();

            assertThat(managerOrLower).isTrue();
        }

        @Test
        @DisplayName("OWNER 는 manager 이하 권한이 아니다")
        void isManagerOrLower3() {
            Member member = createRevi(1L);
            Crew crew = createCrew(member);
            CrewMember crewMember = CrewMemberFactory.owner(crew, member, LocalDateTime.now());

            boolean managerOrLower = crewMember.isManagerOrLower();

            assertThat(managerOrLower).isFalse();
        }
    }
}
