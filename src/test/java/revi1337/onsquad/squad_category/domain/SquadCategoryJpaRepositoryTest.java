package revi1337.onsquad.squad_category.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.category.domain.entity.vo.CategoryType.BADMINTON;
import static revi1337.onsquad.category.domain.entity.vo.CategoryType.GAME;
import static revi1337.onsquad.category.domain.entity.vo.CategoryType.TENNIS;
import static revi1337.onsquad.common.fixture.CategoryFixture.BADMINTON_CATEGORY;
import static revi1337.onsquad.common.fixture.CategoryFixture.CATEGORIES_1;
import static revi1337.onsquad.common.fixture.CategoryFixture.CATEGORIES_2;
import static revi1337.onsquad.common.fixture.CategoryFixture.GAME_CATEGORY;
import static revi1337.onsquad.common.fixture.CategoryFixture.TENNIS_CATEGORY;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD_1;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD_2;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_category.domain.entity.SquadCategory;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryJdbcRepository;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryJpaRepository;

@Import({SquadCategoryJdbcRepository.class})
class SquadCategoryJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private SquadJpaRepository squadJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private SquadCategoryJpaRepository squadCategoryJpaRepository;

    @Autowired
    private SquadCategoryJdbcRepository squadCategoryJdbcRepository;

    @Test
    @DisplayName("Squad 에 속한 모든 SquadCategory 조회에 성공한다.")
    void success() {
        // given
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        Member ANDONG = memberJpaRepository.save(ANDONG());
        CrewMember GENERAL_CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
        Squad SQUAD_1 = squadJpaRepository.save(SQUAD_1(GENERAL_CREW_MEMBER, CREW));
        Squad SQUAD_2 = squadJpaRepository.save(SQUAD_2(GENERAL_CREW_MEMBER, CREW));
        squadCategoryJdbcRepository.insertBatch(SQUAD_1.getId(), CATEGORIES_1());
        squadCategoryJdbcRepository.insertBatch(SQUAD_2.getId(), CATEGORIES_2());

        // when
        List<SquadCategory> CATEGORIES = squadCategoryJpaRepository.findAllBySquadId(SQUAD_1.getId());

        // then
        assertThat(CATEGORIES).hasSize(3);
        assertThat(CATEGORIES.get(0).getCategory()).isEqualTo(GAME_CATEGORY);
        assertThat(CATEGORIES.get(1).getCategory()).isEqualTo(BADMINTON_CATEGORY);
        assertThat(CATEGORIES.get(2).getCategory()).isEqualTo(TENNIS_CATEGORY);
    }

    @Test
    @DisplayName("Squad 에 속한 모든 CategoryType 조회에 성공한다.")
    void success2() {
        // given
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        Member ANDONG = memberJpaRepository.save(ANDONG());
        CrewMember GENERAL_CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
        Squad SQUAD_1 = squadJpaRepository.save(SQUAD_1(GENERAL_CREW_MEMBER, CREW));
        Squad SQUAD_2 = squadJpaRepository.save(SQUAD_2(GENERAL_CREW_MEMBER, CREW));
        squadCategoryJdbcRepository.insertBatch(SQUAD_1.getId(), CATEGORIES_1());
        squadCategoryJdbcRepository.insertBatch(SQUAD_2.getId(), CATEGORIES_2());

        // when
        List<CategoryType> CATEGORY_TYPES = squadCategoryJpaRepository.fetchAllBySquadId(SQUAD_1.getId());

        // then
        assertThat(CATEGORY_TYPES).hasSize(3);
        assertThat(CATEGORY_TYPES.get(0)).isSameAs(GAME);
        assertThat(CATEGORY_TYPES.get(1)).isSameAs(BADMINTON);
        assertThat(CATEGORY_TYPES.get(2)).isSameAs(TENNIS);
    }
}
