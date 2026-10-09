package revi1337.onsquad.squad.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;
import static revi1337.onsquad.common.fixture.SquadCategoryFixture.createSquadCategories;
import static revi1337.onsquad.common.fixture.SquadFixture.createSquad;
import static revi1337.onsquad.common.fixture.SquadMemberFixture.createGeneralSquadMember;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.category.domain.vo.CategoryType;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.error.SquadBusinessException;
import revi1337.onsquad.squad.domain.model.SquadCreateSpec;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryJpaRepository;
import revi1337.onsquad.squad_member.domain.error.SquadMemberBusinessException;

@Sql({"/h2-truncate.sql", "/h2-category.sql"})
class SquadCommandServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private SquadJpaRepository squadRepository;

    @Autowired
    private SquadCategoryJpaRepository squadCategoryRepository;

    @Autowired
    private SquadCommandService squadCommandService;

    @Test
    @DisplayName("새로운 스쿼드를 생성하면 스쿼드 정보와 카테고리가 함께 저장된다.")
    void savesSquadWithCategories_whenCreated() {
        Member member = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(member));
        SquadCreateSpec spec = new SquadCreateSpec(
                "title",
                "content",
                10,
                "add",
                "add-detail",
                List.of(CategoryType.GAME, CategoryType.MANGACAFE),
                "kakao",
                "discord"
        );

        Long squadId = squadCommandService.newSquad(member.getId(), crew.getId(), spec);

        assertSoftly(softly -> {
            clearPersistenceContext();
            softly.assertThat(squadRepository.findById(squadId)).isPresent();
            softly.assertThat(squadCategoryRepository.findAll()).hasSize(2);
        });
    }

    @Test
    @DisplayName("스쿼드를 삭제하면 해당 스쿼드와 관련된 카테고리 정보가 모두 제거된다.")
    void deletesSquadWithCategories_whenCreatorDeletes() {
        Member member = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(member));
        Squad squad = squadRepository.save(createSquad(crew, member));
        squadCategoryRepository.saveAll(createSquadCategories(squad, CategoryType.GAME, CategoryType.MANGACAFE));
        clearPersistenceContext();

        squadCommandService.deleteSquad(member.getId(), squad.getId());

        assertSoftly(softly -> {
            clearPersistenceContext();
            softly.assertThat(squadRepository.findById(squad.getId())).isEmpty();
            softly.assertThat(squadCategoryRepository.findAll().size()).isEqualTo(0);
        });
    }

    @Test
    @DisplayName("크루장은 본인이 참여하지 않은 스쿼드도 삭제할 수 있다.")
    void deletesSquad_whenCrewOwnerIsNotSquadMember() {
        Member owner = memberRepository.save(createMember(1));
        Member leader = memberRepository.save(createMember(2));
        Crew crew = createCrew(owner);
        crew.addCrewMember(CrewMemberFactory.general(crew, leader, LocalDateTime.now()));
        crewRepository.save(crew);
        Squad squad = squadRepository.save(createSquad(crew, leader));
        squadCategoryRepository.saveAll(createSquadCategories(squad, CategoryType.GAME));
        clearPersistenceContext();

        squadCommandService.deleteSquad(owner.getId(), squad.getId());

        assertSoftly(softly -> {
            clearPersistenceContext();
            softly.assertThat(squadRepository.findById(squad.getId())).isEmpty();
            softly.assertThat(squadCategoryRepository.findAll()).isEmpty();
        });
    }

    @Test
    @DisplayName("크루 일반 멤버라도 스쿼드 리더면 스쿼드를 삭제할 수 있다.")
    void deletesSquad_whenCrewGeneralIsSquadLeader() {
        Member owner = memberRepository.save(createMember(1));
        Member leader = memberRepository.save(createMember(2));
        Crew crew = createCrew(owner);
        crew.addCrewMember(CrewMemberFactory.general(crew, leader, LocalDateTime.now()));
        crewRepository.save(crew);
        Squad squad = squadRepository.save(createSquad(crew, leader));
        clearPersistenceContext();

        squadCommandService.deleteSquad(leader.getId(), squad.getId());

        clearPersistenceContext();
        assertSoftly(softly -> softly.assertThat(squadRepository.findById(squad.getId())).isEmpty());
    }

    @Test
    @DisplayName("스쿼드 리더가 아닌 일반 멤버는 스쿼드를 삭제할 수 없다.")
    void throwsInsufficientAuthority_whenSquadGeneralMemberDeletes() {
        Member owner = memberRepository.save(createMember(1));
        Member leader = memberRepository.save(createMember(2));
        Member general = memberRepository.save(createMember(3));
        Crew crew = createCrew(owner);
        crew.addCrewMember(CrewMemberFactory.general(crew, leader, LocalDateTime.now()));
        crew.addCrewMember(CrewMemberFactory.general(crew, general, LocalDateTime.now()));
        crewRepository.save(crew);
        Squad squad = createSquad(crew, leader);
        squad.addMembers(createGeneralSquadMember(squad, general));
        squadRepository.save(squad);
        clearPersistenceContext();

        assertThatThrownBy(() -> squadCommandService.deleteSquad(general.getId(), squad.getId()))
                .isInstanceOf(SquadBusinessException.InsufficientAuthority.class);
    }

    @Test
    @DisplayName("스쿼드에 참여하지 않은 크루 매니저는 스쿼드를 삭제할 수 없다.")
    void throwsNotParticipant_whenCrewManagerNotInSquadDeletes() {
        Member owner = memberRepository.save(createMember(1));
        Member leader = memberRepository.save(createMember(2));
        Member manager = memberRepository.save(createMember(3));
        Crew crew = createCrew(owner);
        crew.addCrewMember(CrewMemberFactory.general(crew, leader, LocalDateTime.now()));
        crew.addCrewMember(CrewMemberFactory.manager(crew, manager, LocalDateTime.now()));
        crewRepository.save(crew);
        Squad squad = squadRepository.save(createSquad(crew, leader));
        clearPersistenceContext();

        assertThatThrownBy(() -> squadCommandService.deleteSquad(manager.getId(), squad.getId()))
                .isInstanceOf(SquadMemberBusinessException.NotParticipant.class);
    }
}
