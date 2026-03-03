package revi1337.onsquad.squad_member.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.createGeneralSquadMember;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_member.application.response.MyParticipantSquadResponse;
import revi1337.onsquad.squad_member.application.response.SquadMemberResponse;

class SquadMemberQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadMemberQueryService squadMemberQueryService;

    @Nested
    @DisplayName("스쿼드 참여자 목록 조회")
    class fetchParticipants {

        @Test
        @DisplayName("스쿼드 미참여자(크루장)가 스쿼드 참여자 조회 시, 참여자 목록은 나오지만 본인 여부 및 관리 권한(강퇴, 위임)은 null로 반환된다.")
        void success1() {
            Member member1 = memberRepository.save(createMember(1));
            Member member2 = memberRepository.save(createMember(2));
            Member member3 = memberRepository.save(createMember(3));
            Member member4 = memberRepository.save(createMember(4));
            Crew crew = createCrew(member1);
            crew.addCrewMember(createManagerCrewMember(crew, member2));
            crew.addCrewMember(createGeneralCrewMember(crew, member3));
            crew.addCrewMember(createGeneralCrewMember(crew, member4));
            crewRepository.save(crew);
            LocalDateTime baseTime = LocalDate.of(2026, 1, 4).atStartOfDay();
            Squad squad = createSquad(crew, member2, baseTime);
            squad.addMembers(createGeneralSquadMember(squad, member3, baseTime.plusHours(1)));
            squad.addMembers(createGeneralSquadMember(squad, member4, baseTime.plusHours(2)));
            squadRepository.save(squad);
            clearPersistenceContext();
            PageRequest pageRequest = PageRequest.of(0, 4, Sort.by("participateAt").descending());

            PageResponse<SquadMemberResponse> response = squadMemberQueryService.fetchParticipants(member1.getId(), squad.getId(), pageRequest);

            assertSoftly(softly -> {
                List<SquadMemberResponse> results = response.results();
                softly.assertThat(results).hasSize(3);
                softly.assertThat(results.get(0).states().isMe()).isNull();
                softly.assertThat(results.get(0).states().canKick()).isNull();
                softly.assertThat(results.get(0).states().canDelegateLeader()).isNull();

                softly.assertThat(results.get(1).states().isMe()).isNull();
                softly.assertThat(results.get(1).states().canKick()).isNull();
                softly.assertThat(results.get(1).states().canDelegateLeader()).isNull();

                softly.assertThat(results.get(2).states().isMe()).isNull();
                softly.assertThat(results.get(2).states().canKick()).isNull();
                softly.assertThat(results.get(2).states().canDelegateLeader()).isNull();
            });
        }

        @Test
        @DisplayName("스쿼드 참여자가 조회 시, 목록 내 각 멤버에 대해 본인 여부 및 리더 권한 기반의 관리 가능 여부가 계산되어 반환된다.")
        void success2() {
            Member member1 = memberRepository.save(createMember(1));
            Member member2 = memberRepository.save(createMember(2));
            Member member3 = memberRepository.save(createMember(3));
            Member member4 = memberRepository.save(createMember(4));
            Crew crew = createCrew(member1);
            crew.addCrewMember(createManagerCrewMember(crew, member2));
            crew.addCrewMember(createGeneralCrewMember(crew, member3));
            crew.addCrewMember(createGeneralCrewMember(crew, member4));
            crewRepository.save(crew);
            LocalDateTime baseTime = LocalDate.of(2026, 1, 4).atStartOfDay();
            Squad squad = createSquad(crew, member1, baseTime);
            squad.addMembers(createGeneralSquadMember(squad, member2, baseTime.plusHours(1)));
            squad.addMembers(createGeneralSquadMember(squad, member3, baseTime.plusHours(2)));
            squad.addMembers(createGeneralSquadMember(squad, member4, baseTime.plusHours(3)));
            squadRepository.save(squad);
            clearPersistenceContext();
            PageRequest pageRequest = PageRequest.of(0, 4, Sort.by("participateAt").descending());

            PageResponse<SquadMemberResponse> response = squadMemberQueryService.fetchParticipants(member2.getId(), squad.getId(), pageRequest);

            assertSoftly(softly -> {
                List<SquadMemberResponse> results = response.results();
                softly.assertThat(results).hasSize(4);

                softly.assertThat(results.get(0).states().isMe()).isFalse();
                softly.assertThat(results.get(0).states().canKick()).isFalse();
                softly.assertThat(results.get(0).states().canDelegateLeader()).isFalse();

                softly.assertThat(results.get(1).states().isMe()).isFalse();
                softly.assertThat(results.get(1).states().canKick()).isFalse();
                softly.assertThat(results.get(1).states().canDelegateLeader()).isFalse();

                softly.assertThat(results.get(2).states().isMe()).isTrue();
                softly.assertThat(results.get(2).states().canKick()).isFalse();
                softly.assertThat(results.get(2).states().canDelegateLeader()).isFalse();

                softly.assertThat(results.get(3).states().isMe()).isFalse();
                softly.assertThat(results.get(3).states().canKick()).isFalse();
                softly.assertThat(results.get(3).states().canDelegateLeader()).isFalse();
            });
        }
    }

    @Nested
    @DisplayName("나의 참여 스쿼드 목록 조회 (크루별 그룹화)")
    class fetchMyParticipatingSquads {

        @Test
        @DisplayName("회원이 참여 중인 모든 스쿼드를 조회하며, 소속된 크루를 기준으로 그룹화하고 크루장 여부를 함께 반환한다.")
        void success() {
            Member member1 = memberRepository.save(createMember(1));
            Member member2 = memberRepository.save(createMember(2));
            Member member3 = memberRepository.save(createMember(3));
            Member member4 = memberRepository.save(createMember(4));
            LocalDateTime baseTime = LocalDate.of(2026, 1, 4).atStartOfDay();
            Crew crew1 = createCrew(member1, baseTime.plusHours(1));
            crew1.addCrewMember(createManagerCrewMember(crew1, member2));
            crew1.addCrewMember(createGeneralCrewMember(crew1, member3));
            crew1.addCrewMember(createGeneralCrewMember(crew1, member4));
            crewRepository.save(crew1);
            Crew crew2 = createCrew(member2, baseTime.plusHours(2));
            crew2.addCrewMember(createManagerCrewMember(crew2, member1));
            crew2.addCrewMember(createGeneralCrewMember(crew2, member3));
            crew2.addCrewMember(createGeneralCrewMember(crew2, member4));
            crewRepository.save(crew2);
            Crew crew3 = createCrew(member3, baseTime.plusHours(3));
            crew3.addCrewMember(createManagerCrewMember(crew3, member1));
            crew3.addCrewMember(createGeneralCrewMember(crew3, member2));
            crew3.addCrewMember(createGeneralCrewMember(crew3, member4));
            crewRepository.save(crew3);
            Squad squad1 = createSquad(crew1, member1, baseTime.plusHours(4));
            squad1.addMembers(createGeneralSquadMember(squad1, member2, baseTime.plusHours(7)));
            squad1.addMembers(createGeneralSquadMember(squad1, member3, baseTime.plusHours(6)));
            squad1.addMembers(createGeneralSquadMember(squad1, member4, baseTime.plusHours(5)));
            squadRepository.save(squad1);
            Squad squad2 = createSquad(crew2, member2, baseTime.plusHours(4));
            squad2.addMembers(createGeneralSquadMember(squad2, member1, baseTime.plusHours(7)));
            squad2.addMembers(createGeneralSquadMember(squad2, member3, baseTime.plusHours(6)));
            squad2.addMembers(createGeneralSquadMember(squad2, member4, baseTime.plusHours(5)));
            squadRepository.save(squad2);
            Squad squad3 = createSquad(crew3, member3, baseTime.plusHours(4));
            squad3.addMembers(createGeneralSquadMember(squad3, member1, baseTime.plusHours(7)));
            squad3.addMembers(createGeneralSquadMember(squad3, member2, baseTime.plusHours(6)));
            squad3.addMembers(createGeneralSquadMember(squad3, member4, baseTime.plusHours(5)));
            squadRepository.save(squad3);
            clearPersistenceContext();

            List<MyParticipantSquadResponse> response = squadMemberQueryService.fetchMyParticipatingSquads(member1.getId());

            assertSoftly(softly -> {
                softly.assertThat(response).hasSize(3);

                softly.assertThat(response.get(0).states().isOwner()).isTrue();
                softly.assertThat(response.get(0).crew().id()).isEqualTo(crew1.getId());

                softly.assertThat(response.get(1).states().isOwner()).isFalse();
                softly.assertThat(response.get(1).crew().id()).isEqualTo(crew3.getId());

                softly.assertThat(response.get(2).states().isOwner()).isFalse();
                softly.assertThat(response.get(2).crew().id()).isEqualTo(crew2.getId());
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
