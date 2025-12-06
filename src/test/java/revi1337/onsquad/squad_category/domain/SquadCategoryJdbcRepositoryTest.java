package revi1337.onsquad.squad_category.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CategoryFixture.ALL_WITH_SOME_CATEGORIES;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;
import static revi1337.onsquad.common.fixture.SquadFixture.SQUAD_1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryJdbcRepository;

@Import(SquadCategoryJdbcRepository.class)
class SquadCategoryJdbcRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private SquadJpaRepository squadJpaRepository;

    @Autowired
    private SquadCategoryJdbcRepository squadCategoryJdbcRepository;

    @Test
    @DisplayName("CategoryType ALL 을 제외한 Category Batch Insert 에 성공한다.")
    void success() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        Member ANDONG = memberJpaRepository.save(ANDONG());
        CrewMember GENERAL_CREW_MEMBER = crewMemberJpaRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
        Squad SQUAD = squadJpaRepository.save(SQUAD_1(GENERAL_CREW_MEMBER, CREW));

        int influenced = squadCategoryJdbcRepository.batchInsert(SQUAD.getId(), ALL_WITH_SOME_CATEGORIES());

        assertThat(influenced).isEqualTo(2);
    }
}
