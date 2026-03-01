package revi1337.onsquad.squad.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.createGeneralSquadMember;

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
import revi1337.onsquad.squad.application.response.SquadWithLeaderStateResponse;
import revi1337.onsquad.squad.application.response.SquadWithStatesResponse;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;

class SquadQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadQueryService squadQueryService;

    @Nested
    @DisplayName("스쿼드 상세 조회")
    class fetchSquad {

        @Test
        @DisplayName("사용자가 스쿼드 참여자인 경우, 참여자 전용 상태값(리더여부와 탈퇴가능여부)이 포함되어 반환된다.")
        void success1() {
            Member member1 = memberRepository.save(createMember(1));
            Member member2 = memberRepository.save(createMember(2));
            Member member3 = memberRepository.save(createMember(3));
            Crew crew = createCrew(member1);
            crew.addCrewMember(createManagerCrewMember(crew, member2));
            crewRepository.save(crew);
            Squad squad = createSquad(1, crew, member1);
            squad.addMembers(createGeneralSquadMember(squad, member2));
            squadRepository.save(squad);
            clearPersistenceContext();

            SquadWithStatesResponse response = squadQueryService.fetchSquad(member2.getId(), squad.getId());

            assertSoftly(softly -> {
                softly.assertThat(response.states().alreadyRequest()).isNull();
                softly.assertThat(response.states().alreadyParticipant()).isTrue();
                softly.assertThat(response.states().isLeader()).isNotNull();
                softly.assertThat(response.states().canSeeParticipants()).isTrue();
                softly.assertThat(response.states().canLeave()).isNotNull();
                softly.assertThat(response.states().canDelete()).isFalse();
            });
        }

        @Test
        @DisplayName("사용자가 스쿼드 미참여자인 경우, 참여자 전용 상태값(리더여부와 탈퇴가능여부)가 포함되지 않고 반환된다.")
        void success2() {
            Member member1 = memberRepository.save(createMember(1));
            Member member2 = memberRepository.save(createMember(2));
            Member member3 = memberRepository.save(createMember(3));
            Crew crew = createCrew(member1);
            crew.addCrewMember(createManagerCrewMember(crew, member2));
            crewRepository.save(crew);
            Squad squad1 = squadRepository.save(createSquad(1, crew, member1));
            clearPersistenceContext();

            SquadWithStatesResponse response = squadQueryService.fetchSquad(member2.getId(), squad1.getId());

            assertSoftly(softly -> {
                softly.assertThat(response.states().alreadyRequest()).isFalse();
                softly.assertThat(response.states().alreadyParticipant()).isFalse();
                softly.assertThat(response.states().isLeader()).isNull();
                softly.assertThat(response.states().canSeeParticipants()).isFalse();
                softly.assertThat(response.states().canLeave()).isNull();
                softly.assertThat(response.states().canDelete()).isFalse();
            });
        }
    }

    @Nested
    @DisplayName("스쿼드 관리 목록 조회")
    class fetchManageList {

        @Test
        @DisplayName("크루 오너가 조회하면 본인이 리더인 스쿼드는 구분되지만, 모든 스쿼드에 대해 폭파 권한(canDestroy)이 true로 반환된다.")
        void success1() {
            Member member1 = memberRepository.save(createMember(1));
            Member member2 = memberRepository.save(createMember(2));
            Member member3 = memberRepository.save(createMember(3));
            Crew crew = createCrew(member1);
            crew.addCrewMember(createManagerCrewMember(crew, member2));
            crew.addCrewMember(createGeneralCrewMember(crew, member3));
            crewRepository.save(crew);
            Squad squad1 = squadRepository.save(createSquad(1, crew, member1));
            Squad squad2 = squadRepository.save(createSquad(2, crew, member2));
            Squad squad3 = squadRepository.save(createSquad(3, crew, member3));
            PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("createdAt").descending());

            PageResponse<SquadWithLeaderStateResponse> response = squadQueryService.fetchManageList(member1.getId(), crew.getId(), pageRequest);

            assertSoftly(softly -> {
                List<SquadWithLeaderStateResponse> results = response.results();
                softly.assertThat(results).hasSize(3);
                softly.assertThat(results.get(0).states().isLeader()).isFalse();
                softly.assertThat(results.get(0).states().canDestroy()).isTrue();
                softly.assertThat(results.get(1).states().isLeader()).isFalse();
                softly.assertThat(results.get(1).states().canDestroy()).isTrue();
                softly.assertThat(results.get(2).states().isLeader()).isTrue();
                softly.assertThat(results.get(2).states().canDestroy()).isTrue();
            });
        }

        @Test
        @DisplayName("크루 매니저가 조회하면 본인이 리더인 스쿼드는 구분되지만, 크루 차원의 삭제 권한은 false로 반환된다.")
        void success2() {
            Member member1 = memberRepository.save(createMember(1));
            Member member2 = memberRepository.save(createMember(2));
            Member member3 = memberRepository.save(createMember(3));
            Crew crew = createCrew(member1);
            crew.addCrewMember(createManagerCrewMember(crew, member2));
            crew.addCrewMember(createGeneralCrewMember(crew, member3));
            crewRepository.save(crew);
            Squad squad1 = squadRepository.save(createSquad(1, crew, member1));
            Squad squad2 = squadRepository.save(createSquad(2, crew, member2));
            Squad squad3 = squadRepository.save(createSquad(3, crew, member3));
            PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("createdAt").descending());

            PageResponse<SquadWithLeaderStateResponse> response = squadQueryService.fetchManageList(member2.getId(), crew.getId(), pageRequest);

            assertSoftly(softly -> {
                List<SquadWithLeaderStateResponse> results = response.results();
                softly.assertThat(results).hasSize(3);
                softly.assertThat(results.get(0).states().isLeader()).isFalse();
                softly.assertThat(results.get(0).states().canDestroy()).isFalse();
                softly.assertThat(results.get(1).states().isLeader()).isTrue();
                softly.assertThat(results.get(1).states().canDestroy()).isFalse();
                softly.assertThat(results.get(2).states().isLeader()).isFalse();
                softly.assertThat(results.get(2).states().canDestroy()).isFalse();
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
