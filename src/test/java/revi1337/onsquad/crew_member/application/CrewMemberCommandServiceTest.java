package revi1337.onsquad.crew_member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.error.CrewBusinessException;
import revi1337.onsquad.crew.domain.error.CrewErrorCode;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.error.CrewMemberBusinessException;
import revi1337.onsquad.crew_member.domain.error.CrewMemberErrorCode;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

class CrewMemberCommandServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private CrewMemberRepository crewMemberRepository;

    @Autowired
    private CrewMemberCommandService crewMemberCommandService;

    @Nested
    @DisplayName("크루 owner 위임 테스트")
    class delegateOwner {

        @Test
        @DisplayName("자신이 크루 owner 이고, 대상이 general 또는 manager 면 성공")
        void test1() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            crewRepository.save(crew);
            clearPersistenceContext();

            crewMemberCommandService.delegateOwner(revi.getId(), crew.getId(), andong.getId());
            clearPersistenceContext();

            assertThat(crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), revi.getId()).get().isOwner()).isFalse();
            assertThat(crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), andong.getId()).get().isOwner()).isTrue();
        }

        @Test
        @DisplayName("자신이 크루 owner 이고, 대상이 general 또는 manager 면 성공")
        void test2() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createManagerCrewMember(crew, andong));
            crewRepository.save(crew);
            clearPersistenceContext();

            crewMemberCommandService.delegateOwner(revi.getId(), crew.getId(), andong.getId());
            clearPersistenceContext();

            assertThat(crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), revi.getId()).get().isOwner()).isFalse();
            assertThat(crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), andong.getId()).get().isOwner()).isTrue();
        }

        @Test
        @DisplayName("자기 자신 위임 불가")
        void test3() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            crewRepository.save(crew);
            clearPersistenceContext();

            assertThatThrownBy(() -> crewMemberCommandService
                    .delegateOwner(revi.getId(), crew.getId(), revi.getId()))
                    .isExactlyInstanceOf(CrewMemberBusinessException.InvalidRequest.class);
        }

        @Test
        @DisplayName("자신이 manager 면 실패")
        void test4() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createManagerCrewMember(crew, andong));
            crewRepository.save(crew);
            clearPersistenceContext();

            assertThatThrownBy(() -> crewMemberCommandService
                    .delegateOwner(andong.getId(), crew.getId(), revi.getId()))
                    .isExactlyInstanceOf(CrewMemberBusinessException.InsufficientAuthority.class);
        }

        @Test
        @DisplayName("자신이 general 이면 실패")
        void test5() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            crewRepository.save(crew);
            clearPersistenceContext();

            assertThatThrownBy(() -> crewMemberCommandService
                    .delegateOwner(andong.getId(), crew.getId(), revi.getId()))
                    .isExactlyInstanceOf(CrewMemberBusinessException.InsufficientAuthority.class);
        }
    }

    @Nested
    @DisplayName("크루 탈퇴 테스트")
    class leaveCrew {

        @Test
        @DisplayName("자신이 크루 manager 또는 general 일때 성공")
        void test1() {
            Member revi = memberRepository.save(createRevi());
            Member kwangwon = memberRepository.save(createKwangwon());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createManagerCrewMember(crew, kwangwon));
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            crewRepository.save(crew);
            clearPersistenceContext();

            crewMemberCommandService.leaveCrew(kwangwon.getId(), crew.getId());
            clearPersistenceContext();

            assertThat(crewRepository.findById(crew.getId()).get().getCurrentSize()).isEqualTo(2);
            assertThat(crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), kwangwon.getId())).isEmpty();
        }

        @Test
        @DisplayName("자신이 크루 owner 일때, 잔류 인원이 없으면 성공")
        void test2() {
            Member revi = memberRepository.save(createRevi());
            Crew crew = crewRepository.save(createCrew(revi));
            clearPersistenceContext();

            crewMemberCommandService.leaveCrew(revi.getId(), crew.getId());
            clearPersistenceContext();

            assertThat(crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), revi.getId())).isEmpty();
            assertThat(crewRepository.findById(crew.getId())).isEmpty();
        }

        @Test
        @DisplayName("자신이 크루 owner 일때, 잔류 인원이 있으면 실패")
        void test3() {
            Member revi = memberRepository.save(createRevi());
            Member kwangwon = memberRepository.save(createKwangwon());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createManagerCrewMember(crew, kwangwon));
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            crewRepository.save(crew);
            clearPersistenceContext();

            assertThatThrownBy(() -> crewMemberCommandService
                    .leaveCrew(revi.getId(), crew.getId()))
                    .isExactlyInstanceOf(CrewBusinessException.InsufficientAuthority.class)
                    .hasMessage(CrewErrorCode.INSUFFICIENT_LEAVE_CREW_AUTHORITY.getDescription());
        }
    }

    @Nested
    @DisplayName("크루원 추방 테스트")
    class kickOutMember {

        @Test
        @DisplayName("자신이 크루 owner 면 성공")
        void test1() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            crewRepository.save(crew);
            clearPersistenceContext();

            crewMemberCommandService.kickOutMember(revi.getId(), crew.getId(), andong.getId());
            clearPersistenceContext();

            assertThat(crewRepository.findById(crew.getId()).get().getCurrentSize()).isEqualTo(1);
            assertThat(crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), andong.getId())).isEmpty();
        }

        @Test
        @DisplayName("자신이 크루 manager 이고, 대상이 general 이면 성공")
        void test2() {
            Member revi = memberRepository.save(createRevi());
            Member kwangwon = memberRepository.save(createKwangwon());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createManagerCrewMember(crew, kwangwon));
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            crewRepository.save(crew);
            clearPersistenceContext();

            crewMemberCommandService.kickOutMember(kwangwon.getId(), crew.getId(), andong.getId());
            clearPersistenceContext();

            assertThat(crewRepository.findById(crew.getId()).get().getCurrentSize()).isEqualTo(2);
            assertThat(crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), andong.getId())).isEmpty();
        }

        @Test
        @DisplayName("자기 자신 추방 불가")
        void test3() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            crewRepository.save(crew);
            clearPersistenceContext();

            assertThatThrownBy(() -> crewMemberCommandService
                    .kickOutMember(revi.getId(), crew.getId(), revi.getId()))
                    .isExactlyInstanceOf(CrewMemberBusinessException.InvalidRequest.class)
                    .hasMessage(CrewMemberErrorCode.CANNOT_TARGET_SELF.getDescription());
        }

        @Test
        @DisplayName("자신이 general 이면 실패")
        void test4() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            crewRepository.save(crew);
            clearPersistenceContext();

            assertThatThrownBy(() -> crewMemberCommandService
                    .kickOutMember(andong.getId(), crew.getId(), revi.getId()))
                    .isExactlyInstanceOf(CrewMemberBusinessException.InsufficientAuthority.class)
                    .hasMessage(CrewMemberErrorCode.INSUFFICIENT_KICK_MEMBER_AUTHORITY.getDescription());
        }

        @Test
        @DisplayName("자신이 manager 이고, 대상이 manager 면 실패")
        void test5() {
            Member revi = memberRepository.save(createRevi());
            Member kwangwon = memberRepository.save(createKwangwon());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createManagerCrewMember(crew, kwangwon));
            crew.addCrewMember(createManagerCrewMember(crew, andong));
            crewRepository.save(crew);
            clearPersistenceContext();

            assertThatThrownBy(() -> crewMemberCommandService
                    .kickOutMember(kwangwon.getId(), crew.getId(), andong.getId()))
                    .isExactlyInstanceOf(CrewMemberBusinessException.InsufficientAuthority.class)
                    .hasMessage(CrewMemberErrorCode.CANNOT_KICK_EQUAL_OR_HIGHER_ROLE_MEMBER.getDescription());
        }

        @Test
        @DisplayName("자신이 manager 이고, 대상이 owner 면 실패")
        void test6() {
            Member revi = memberRepository.save(createRevi());
            Member kwangwon = memberRepository.save(createKwangwon());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createManagerCrewMember(crew, kwangwon));
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            crewRepository.save(crew);
            clearPersistenceContext();

            assertThatThrownBy(() -> crewMemberCommandService
                    .kickOutMember(kwangwon.getId(), crew.getId(), revi.getId()))
                    .isExactlyInstanceOf(CrewMemberBusinessException.InsufficientAuthority.class)
                    .hasMessage(CrewMemberErrorCode.CANNOT_KICK_EQUAL_OR_HIGHER_ROLE_MEMBER.getDescription());
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
