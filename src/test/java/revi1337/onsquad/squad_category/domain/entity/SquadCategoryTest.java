package revi1337.onsquad.squad_category.domain.entity;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.category.domain.entity.Category;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.entity.Squad.SquadMetadata;

class SquadCategoryTest {

    @Test
    void success() {
        Member member = createMember(1L);
        Crew crew = createCrew(1L, member);
        Squad squad = createSquad(1L, crew, member);
        Category category = Category.fromCategoryTypes(List.of(CategoryType.ACTIVITY)).get(0);

        SquadCategory squadCategory = new SquadCategory(squad, category);

        assertSoftly(softly -> {
            softly.assertThat(squadCategory.getSquad()).isNotNull();
            softly.assertThat(squadCategory.getCategory()).isNotNull();
        });

    }

    private static Squad createSquad(Long id, Crew crew, Member member) {
        Squad squad = Squad.create(
                new SquadMetadata(
                        "title" + id,
                        "content" + id,
                        10,
                        "add" + id,
                        "add-detail" + id,
                        "kakao" + id,
                        "discord" + id
                ),
                member,
                crew
        );
        ReflectionTestUtils.setField(squad, "id", id);
        return squad;
    }
}
