package revi1337.onsquad.squad_member.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.createGeneralSquadMember;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberJpaRepository;

class SquadMemberCommandServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadMemberJpaRepository squadMemberRepository;

    @Autowired
    private SquadMemberCommandService squadMemberCommandService;

    @Nested
    @DisplayName("스쿼드 리더 위임 테스트")
    class delegateLeader {

        @Test
        @DisplayName("기존 리더가 다른 참여자에게 권한을 위임하면, 리더 자격이 이전되고 스쿼드의 소유자 정보가 변경된다.")
        void success() {
            Member member1 = memberRepository.save(createRevi());
            Member member2 = memberRepository.save(createAndong());
            Member member3 = memberRepository.save(createKwangwon());
            Crew crew = createCrew(member1);
            crew.addCrewMember(createManagerCrewMember(crew, member2));
            crew.addCrewMember(createGeneralCrewMember(crew, member3));
            crewRepository.save(crew);
            Squad squad = createSquad(crew, member1);
            squad.addMembers(createGeneralSquadMember(squad, member2));
            squadRepository.save(squad);
            clearPersistenceContext();

            squadMemberCommandService.delegateLeader(member1.getId(), squad.getId(), member2.getId());

            assertSoftly(softly -> {
                clearPersistenceContext();
                softly.assertThat(squadMemberRepository.findBySquadIdAndMemberId(squad.getId(), member1.getId()).get().isLeader()).isFalse();
                softly.assertThat(squadMemberRepository.findBySquadIdAndMemberId(squad.getId(), member2.getId()).get().isLeader()).isTrue();
                softly.assertThat(squadRepository.findById(squad.getId()).get().getMember().getId()).isEqualTo(member2.getId());
            });
        }
    }

    @Nested
    @DisplayName("스쿼드 탈퇴 테스트")
    class leaveSquad {

        @Test
        @DisplayName("일반 참여자가 스쿼드를 탈퇴하면, 참여 관계가 삭제되고 스쿼드의 현재 인원수가 감소한다.")
        void success() {
            Member member1 = memberRepository.save(createRevi());
            Member member2 = memberRepository.save(createAndong());
            Member member3 = memberRepository.save(createKwangwon());
            Crew crew = createCrew(member1);
            crew.addCrewMember(createManagerCrewMember(crew, member2));
            crew.addCrewMember(createGeneralCrewMember(crew, member3));
            crewRepository.save(crew);
            Squad squad = createSquad(crew, member1, 3);
            squad.addMembers(createGeneralSquadMember(squad, member2));
            squad.addMembers(createGeneralSquadMember(squad, member3));
            squadRepository.save(squad);
            clearPersistenceContext();

            squadMemberCommandService.leaveSquad(member3.getId(), squad.getId());

            assertSoftly(softly -> {
                clearPersistenceContext();
                softly.assertThat(squadMemberRepository.findBySquadIdAndMemberId(squad.getId(), member3.getId())).isEmpty();
                Squad findSquad = squadRepository.findById(squad.getId()).get();
                softly.assertThat(findSquad.getCurrentSize()).isEqualTo(2);
                softly.assertThat(findSquad.getRemain()).isEqualTo(1);
            });
        }

        @Test
        @DisplayName("마지막 남은 멤버(리더)가 탈퇴를 시도하면, 스쿼드 자체가 삭제(Dispose)된다.")
        void success2() {
            Member member1 = memberRepository.save(createRevi());
            Member member2 = memberRepository.save(createAndong());
            Crew crew = createCrew(member1);
            crew.addCrewMember(createManagerCrewMember(crew, member2));
            crewRepository.save(crew);
            Squad squad = createSquad(crew, member1, 2);
            squad.addMembers(createGeneralSquadMember(squad, member2));
            squadRepository.save(squad);
            clearPersistenceContext();
            squadMemberCommandService.leaveSquad(member2.getId(), squad.getId());
            clearPersistenceContext();

            squadMemberCommandService.leaveSquad(member1.getId(), squad.getId());

            assertSoftly(softly -> {
                clearPersistenceContext();
                softly.assertThat(squadMemberRepository.findBySquadIdAndMemberId(squad.getId(), member1.getId())).isEmpty();
                softly.assertThat(squadMemberRepository.findBySquadIdAndMemberId(squad.getId(), member2.getId())).isEmpty();
                softly.assertThat(squadRepository.findById(squad.getId())).isEmpty();
            });
        }
    }

    @Nested
    class kickOutMember {

        @Test
        void success() {
            Member member1 = memberRepository.save(createRevi());
            Member member2 = memberRepository.save(createAndong());
            Member member3 = memberRepository.save(createKwangwon());
            Crew crew = createCrew(member1);
            crew.addCrewMember(createManagerCrewMember(crew, member2));
            crew.addCrewMember(createGeneralCrewMember(crew, member3));
            crewRepository.save(crew);
            Squad squad = createSquad(crew, member1, 3);
            squad.addMembers(createGeneralSquadMember(squad, member2));
            squad.addMembers(createGeneralSquadMember(squad, member3));
            squadRepository.save(squad);
            clearPersistenceContext();

            squadMemberCommandService.kickOutMember(member1.getId(), squad.getId(), member3.getId());

            assertSoftly(softly -> {
                clearPersistenceContext();
                softly.assertThat(squadMemberRepository.findBySquadIdAndMemberId(squad.getId(), member3.getId())).isEmpty();
                softly.assertThat(squadRepository.findById(squad.getId()).get().getCurrentSize()).isEqualTo(2);
            });
        }
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }
}
