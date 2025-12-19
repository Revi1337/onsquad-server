package revi1337.onsquad.squad.application;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CategoryFixture.ALL_WITH_SOME_CATEGORIES;
import static revi1337.onsquad.common.fixture.CategoryFixture.CATEGORIES_1;
import static revi1337.onsquad.common.fixture.CategoryFixture.CATEGORIES_2;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_1;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW_2;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD_1;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD_2;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD_3;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.GENERAL_SQUAD_MEMBER;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.category.domain.entity.Category;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.category.presentation.request.CategoryCondition;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.application.dto.response.SquadResponse;
import revi1337.onsquad.squad.application.dto.response.SquadWithLeaderStateResponse;
import revi1337.onsquad.squad.application.dto.response.SquadWithParticipantAndLeaderAndViewStateResponse;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryJdbcRepository;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberJpaRepository;

class SquadQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberRepository;

    @Autowired
    private SquadMemberJpaRepository squadMemberRepository;

    @Autowired
    private SquadRepository squadRepository;

    @Autowired
    private SquadCategoryJdbcRepository squadCategoryRepository;

    @Autowired
    private SquadQueryService squadQueryService;

    @Nested
    @DisplayName("스쿼드 단일 조회를 테스트한다.")
    class FetchSquad {

        @Test
        @DisplayName("현재 스쿼드에 참여하고 있다면 (참여상태 & 리더인지 & 멤버를 볼수있는지)와 함께 스쿼드 정보를 반환한다.")
        void success1() {
            // given
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            CrewMember CREW_OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadRepository.save(SQUAD(CREW_OWNER, CREW));
            clearPersistenceContext();

            // when
            SquadWithParticipantAndLeaderAndViewStateResponse DTO = squadQueryService
                    .fetchSquad(REVI.getId(), CREW.getId(), SQUAD.getId());

            // then
            assertThat(DTO.alreadyParticipant()).isTrue();
            assertThat(DTO.isLeader()).isTrue();
            assertThat(DTO.canSeeMembers()).isTrue();
            assertThat(DTO.squad().id()).isEqualTo(SQUAD.getId());
            assertThat(DTO.squad().title()).isEqualTo(SQUAD.getTitle().getValue());
            assertThat(DTO.squad().content()).isEqualTo(SQUAD.getContent().getValue());
            assertThat(DTO.squad().capacity()).isEqualTo(SQUAD.getCapacity());
            assertThat(DTO.squad().remain()).isEqualTo(SQUAD.getRemain());
            assertThat(DTO.squad().address()).isEqualTo(SQUAD.getAddress().getValue());
            assertThat(DTO.squad().addressDetail()).isEqualTo(SQUAD.getAddress().getDetail());
            assertThat(DTO.squad().kakaoLink()).isEqualTo(SQUAD.getKakaoLink());
            assertThat(DTO.squad().discordLink()).isEqualTo(SQUAD.getDiscordLink());
            assertThat(DTO.squad().categories()).hasSize(0);
            assertThat(DTO.squad().leader().id()).isEqualTo(REVI.getId());
            assertThat(DTO.squad().leader().email()).isNull();
            assertThat(DTO.squad().leader().nickname()).isEqualTo(REVI.getNickname().getValue());
            assertThat(DTO.squad().leader().mbti()).isEqualTo(REVI.getMbti().name());
        }

        @Test
        @DisplayName("Squad 에 속해있지 않다면, 참여상태 false, 리더인지 false, & 멤버를 볼수있는지 false 를 반환한다.")
        void success2() {
            // given
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            CrewMember CREW_OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadRepository.save(SQUAD(CREW_OWNER, CREW));

            Member ANDONG = memberRepository.save(ANDONG());
            crewMemberRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            clearPersistenceContext();

            // when
            SquadWithParticipantAndLeaderAndViewStateResponse DTO = squadQueryService
                    .fetchSquad(ANDONG.getId(), CREW.getId(), SQUAD.getId());

            // then
            assertThat(DTO.alreadyParticipant()).isFalse();
            assertThat(DTO.isLeader()).isFalse();
            assertThat(DTO.canSeeMembers()).isFalse();
        }

        @Test
        @DisplayName("Squad 에 속해있지만 Leader 가 아니라면, 참여상태 true, 리더인지 false, & 멤버를 볼수있는지 true 를 반환한다.")
        void success3() {
            // given
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            CrewMember CREW_OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
            Squad SQUAD = squadRepository.save(SQUAD(CREW_OWNER, CREW));

            Member ANDONG = memberRepository.save(ANDONG());
            CrewMember ANDONG_MEMBER = crewMemberRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            squadMemberRepository.save(GENERAL_SQUAD_MEMBER(SQUAD, ANDONG_MEMBER));
            clearPersistenceContext();

            // when
            SquadWithParticipantAndLeaderAndViewStateResponse DTO = squadQueryService
                    .fetchSquad(ANDONG.getId(), CREW.getId(), SQUAD.getId());

            // then
            assertThat(DTO.alreadyParticipant()).isTrue();
            assertThat(DTO.isLeader()).isFalse();
            assertThat(DTO.canSeeMembers()).isTrue();
        }

        @Test
        @DisplayName("Squad 에 속해있지 않지만 Crew Owner 라면, 참여상태 false, 리더인지 false, & 멤버를 볼수있는지 true 를 반환한다.")
        void success4() {
            // given
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));

            Member ANDONG = memberRepository.save(ANDONG());
            CrewMember ANDONG_MEMBER = crewMemberRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            Squad SQUAD = squadRepository.save(SQUAD(ANDONG_MEMBER, CREW));
            clearPersistenceContext();

            // when
            SquadWithParticipantAndLeaderAndViewStateResponse DTO = squadQueryService
                    .fetchSquad(REVI.getId(), CREW.getId(), SQUAD.getId());

            // then
            assertThat(DTO.alreadyParticipant()).isFalse();
            assertThat(DTO.isLeader()).isFalse();
            assertThat(DTO.canSeeMembers()).isTrue();
        }
    }

    @Nested
    @DisplayName("스쿼드들 조회를 테스트한다.")
    class FetchSquads {

        @Test
        @DisplayName("스쿼드들 조회에 성공한다.")
        void success() {
            // given
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            Member ANDONG = memberRepository.save(ANDONG());
            Member KWANGWON = memberRepository.save(KWANGWON());
            CrewMember GENERAL_CREW_MEMBER_1 = crewMemberRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            CrewMember GENERAL_CREW_MEMBER_2 = crewMemberRepository.save(GENERAL_CREW_MEMBER(CREW, KWANGWON));
            Squad SQUAD_1 = squadRepository.save(SQUAD_1(GENERAL_CREW_MEMBER_1, CREW));
            squadCategoryRepository.insertBatch(SQUAD_1.getId(), ALL_WITH_SOME_CATEGORIES());
            Squad SQUAD_2 = squadRepository.save(SQUAD_2(GENERAL_CREW_MEMBER_1, CREW));
            squadCategoryRepository.insertBatch(SQUAD_2.getId(), CATEGORIES_1());
            Squad SQUAD_3 = squadRepository.save(SQUAD_3(GENERAL_CREW_MEMBER_2, CREW));
            squadCategoryRepository.insertBatch(SQUAD_3.getId(), CATEGORIES_2());
            CategoryCondition CONDITION = new CategoryCondition(CategoryType.BADMINTON);
            PageRequest PAGE_REQUEST = PageRequest.of(0, 2);
            clearPersistenceContext();

            // when
            List<SquadResponse> SQUADS = squadQueryService
                    .fetchSquads(ANDONG.getId(), CREW.getId(), CONDITION, PAGE_REQUEST);

            // then
            assertThat(SQUADS.size()).isEqualTo(2);

            assertThat(SQUADS.get(0).id()).isEqualTo(SQUAD_3.getId());
            assertThat(SQUADS.get(0).title()).isEqualTo(SQUAD_3.getTitle().getValue());
            assertThat(SQUADS.get(0).content()).isEqualTo(SQUAD_3.getContent().getValue());
            assertThat(SQUADS.get(0).address()).isEqualTo(SQUAD_3.getAddress().getValue());
            assertThat(SQUADS.get(0).addressDetail()).isEqualTo(SQUAD_3.getAddress().getDetail());
            assertThat(SQUADS.get(0).kakaoLink()).isEqualTo(SQUAD_3.getKakaoLink());
            assertThat(SQUADS.get(0).discordLink()).isEqualTo(SQUAD_3.getDiscordLink());
            assertThat(SQUADS.get(0).categories()).hasSize(3);
            assertThat(SQUADS.get(0).categories()).containsExactly(CATEGORIES_2().stream()
                    .map(Category::getCategoryType)
                    .map(CategoryType::getText)
                    .toArray(String[]::new));
            assertThat(SQUADS.get(0).leader().id()).isEqualTo(KWANGWON.getId());
            assertThat(SQUADS.get(0).leader().nickname()).isEqualTo(KWANGWON.getNickname().getValue());
            assertThat(SQUADS.get(0).leader().mbti()).isEqualTo(KWANGWON.getMbti().name());

            assertThat(SQUADS.get(1).id()).isEqualTo(SQUAD_2.getId());
            assertThat(SQUADS.get(1).title()).isEqualTo(SQUAD_2.getTitle().getValue());
            assertThat(SQUADS.get(1).content()).isEqualTo(SQUAD_2.getContent().getValue());
            assertThat(SQUADS.get(1).address()).isEqualTo(SQUAD_2.getAddress().getValue());
            assertThat(SQUADS.get(1).addressDetail()).isEqualTo(SQUAD_2.getAddress().getDetail());
            assertThat(SQUADS.get(1).kakaoLink()).isEqualTo(SQUAD_2.getKakaoLink());
            assertThat(SQUADS.get(1).discordLink()).isEqualTo(SQUAD_2.getDiscordLink());
            assertThat(SQUADS.get(1).categories()).hasSize(3);
            assertThat(SQUADS.get(1).categories()).containsExactly(CATEGORIES_1().stream()
                    .map(Category::getCategoryType)
                    .map(CategoryType::getText)
                    .toArray(String[]::new));
            assertThat(SQUADS.get(1).leader().id()).isEqualTo(ANDONG.getId());
            assertThat(SQUADS.get(1).leader().nickname()).isEqualTo(ANDONG.getNickname().getValue());
            assertThat(SQUADS.get(1).leader().mbti()).isEqualTo(ANDONG.getMbti().name());
        }
    }

    @Nested
    @DisplayName("크루에 속한 스쿼드 관리 목록 조회를 테스트한다.")
    class FetchSquadsWithOwnerState {

        @Test
        @DisplayName("크루에 속한 스쿼드 관리 목록 조회에 성공한다.")
        void success() {
            // given
            Member REVI = memberRepository.save(REVI());
            Member ANDONG = memberRepository.save(ANDONG());
            Crew CREW1 = crewRepository.save(CREW_1(REVI));
            Crew CREW2 = crewRepository.save(CREW_2(ANDONG));
            CrewMember CREW1_OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW1.getId(), REVI.getId()).get();
            CrewMember CREW1_GENERAL = crewMemberRepository.save(GENERAL_CREW_MEMBER(CREW1, ANDONG));
            CrewMember CREW2_OWNER = crewMemberRepository.findByCrewIdAndMemberId(CREW2.getId(), ANDONG.getId()).get();
            CrewMember CREW2_GENERAL = crewMemberRepository.save(GENERAL_CREW_MEMBER(CREW2, REVI));
            squadRepository.save(SQUAD_1(CREW1_OWNER, CREW1));
            squadRepository.save(SQUAD_2(CREW1_GENERAL, CREW1));
            Squad CREW2_SQUAD1 = squadRepository.save(SQUAD_1(CREW2_OWNER, CREW2));
            Squad CREW2_SQUAD2 = squadRepository.save(SQUAD_2(CREW2_GENERAL, CREW2));
            squadCategoryRepository.insertBatch(CREW2_SQUAD2.getId(), CATEGORIES_1());
            PageRequest PAGE_REQUEST = PageRequest.of(0, 5);
            clearPersistenceContext();

            // when
            List<SquadWithLeaderStateResponse> RESULTS = squadQueryService
                    .fetchSquadsWithOwnerState(ANDONG.getId(), CREW2.getId(), PAGE_REQUEST);

            // then
            assertThat(RESULTS.size()).isEqualTo(2);
            assertThat(RESULTS.get(0).isLeader()).isFalse();
            assertThat(RESULTS.get(0).squad().id()).isEqualTo(CREW2_SQUAD2.getId());
            assertThat(RESULTS.get(0).squad().title()).isEqualTo(CREW2_SQUAD2.getTitle().getValue());
            assertThat(RESULTS.get(0).squad().categories()).hasSize(CATEGORIES_1().size());
            assertThat(RESULTS.get(1).isLeader()).isTrue();
            assertThat(RESULTS.get(1).squad().id()).isEqualTo(CREW2_SQUAD1.getId());
            assertThat(RESULTS.get(1).squad().title()).isEqualTo(CREW2_SQUAD1.getTitle().getValue());
            assertThat(RESULTS.get(1).squad().categories()).hasSize(0);
        }
    }
}
