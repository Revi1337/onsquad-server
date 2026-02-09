package revi1337.onsquad.crew_member.domain.repository.rank;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;
import revi1337.onsquad.member.domain.entity.Member;

@Import(CrewRankedMemberJdbcRepository.class)
class CrewRankedMemberJdbcRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private CrewRankedMemberJpaRepository jpaRepository;

    @Autowired
    private CrewRankedMemberJdbcRepository jdbcRepository;

    @Test
    @DisplayName("JDBC 배치 삽입 시, 지정된 순위(Rank)와 크루 ID 조건에 맞는 데이터만 정렬되어 조회된다")
    void insertBatch() {
        CrewRankedMember ranked1 = createCrewRankedMember(1L, 3, 5, createRevi(1L));
        CrewRankedMember ranked2 = createCrewRankedMember(1L, 2, 10, createAndong(2L));
        CrewRankedMember ranked3 = createCrewRankedMember(1L, 1, 15, createKwangwon(3L));

        jdbcRepository.insertBatch(List.of(ranked1, ranked2, ranked3));

        assertSoftly(softly -> {
            List<CrewRankedMember> rankedMembers = jpaRepository.findAllByCrewIdAndRankLessThanEqualOrderByRankAsc(1L, 2);
            softly.assertThat(rankedMembers).hasSize(2);
            softly.assertThat(rankedMembers.get(0).getMemberId()).isEqualTo(3L);
            softly.assertThat(rankedMembers.get(1).getMemberId()).isEqualTo(2L);
        });
    }

    @Test
    @DisplayName("테이블 초기화(Truncate) 실행 시 기존의 모든 랭킹 데이터는 삭제되고, Sequence index 는 초기화된다.")
    void truncate() {
        CrewRankedMember ranked1 = createCrewRankedMember(1L, 3, 5, createRevi(1L));
        CrewRankedMember ranked2 = createCrewRankedMember(1L, 2, 10, createAndong(2L));
        CrewRankedMember ranked3 = createCrewRankedMember(1L, 1, 15, createKwangwon(3L));
        jdbcRepository.insertBatch(List.of(ranked1, ranked2, ranked3));

        jdbcRepository.truncate();

        assertSoftly(softly -> {
            List<CrewRankedMember> rankedMembers = jpaRepository.findAllByCrewIdAndRankLessThanEqualOrderByRankAsc(1L, 3);
            softly.assertThat(rankedMembers).isEmpty();
            clearPersistenceContext();

            CrewRankedMember newRankedMember = jpaRepository.save(ranked1);
            softly.assertThat(newRankedMember.getId()).isEqualTo(1L);
        });
    }

    @Test
    void aggregateRankedMembersByActivityOccurrence() {
        // TODO 다른 도메인 테스트하고 작성
    }

    @Test
    void aggregateRankedMembersGivenActivityWeight() {
        // TODO 다른 도메인 테스트하고 작성
    }

    private static CrewRankedMember createCrewRankedMember(Long crewId, int rank, long score, Member member) {
        return new CrewRankedMember(
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
