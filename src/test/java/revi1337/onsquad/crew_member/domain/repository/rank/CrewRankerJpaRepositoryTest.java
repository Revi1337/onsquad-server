package revi1337.onsquad.crew_member.domain.repository.rank;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

class CrewRankerJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewRankerJpaRepository crewRankerJpaRepository;

    @Test
    @DisplayName("특정 크루에서 지정된 순위 이하인 멤버들을 순위 오름차순으로 조회한다")
    void findAllByCrewIdAndRankLessThanEqualOrderByRankAsc() {
        Member revi = memberJpaRepository.save(createRevi());
        Member andong = memberJpaRepository.save(createAndong());
        CrewRanker ranked2 = createCrewRanker(1L, 2, 5, andong);
        CrewRanker ranked1 = createCrewRanker(1L, 1, 10, revi);
        crewRankerJpaRepository.saveAll(List.of(ranked1, ranked2));

        List<CrewRanker> rankedMembers = crewRankerJpaRepository.findAllByCrewIdAndRankLessThanEqualOrderByRankAsc(1L, 1);

        assertSoftly(softly -> {
            softly.assertThat(rankedMembers).hasSize(1);
            softly.assertThat(rankedMembers.get(0).getMemberId()).isEqualTo(revi.getId());
        });
    }

    private static CrewRanker createCrewRanker(Long crewId, int rank, long score, Member member) {
        return new CrewRanker(
                crewId,
                rank,
                score,
                member.getId(),
                member.getNickname().getValue(),
                member.getMbti().name(),
                LocalDateTime.now()
        );
    }
}
