package revi1337.onsquad.crew_member.domain.repository.rank;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.config.PersistenceLayerConfiguration;
import revi1337.onsquad.common.container.MySqlTestContainerInitializer;
import revi1337.onsquad.common.fixture.CrewFixture;
import revi1337.onsquad.common.fixture.SquadFixture;
import revi1337.onsquad.common.fixture.SquadMemberFixture;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadJpaRepository;
import revi1337.onsquad.squad_comment.domain.entity.SquadComment;
import revi1337.onsquad.squad_comment.domain.repository.SquadCommentJpaRepository;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberJpaRepository;

@Sql({"/mysql-truncate.sql"})
@Import({PersistenceLayerConfiguration.class, CrewRankerJdbcRepository.class})
@ContextConfiguration(initializers = MySqlTestContainerInitializer.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest(showSql = false)
class CrewRankerJdbcRepositoryTest {

    private static final LocalDateTime WINDOW_FROM = LocalDateTime.of(2026, 1, 5, 0, 0);
    private static final LocalDateTime WINDOW_TO = LocalDateTime.of(2026, 1, 12, 0, 0);

    @Autowired
    private CrewRankerJpaRepository jpaRepository;

    @Autowired
    private CrewRankerJdbcRepository jdbcRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private SquadJpaRepository squadJpaRepository;

    @Autowired
    private SquadMemberJpaRepository squadMemberJpaRepository;

    @Autowired
    private SquadCommentJpaRepository squadCommentJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("JDBC 배치 삽입 시, 지정된 순위(Rank)와 크루 ID 조건에 맞는 데이터만 정렬되어 조회된다")
    void insertBatch() {
        CrewRankerCandidate ranked1 = createCrewRankerCandidate(1L, 3, 5, createRevi(1L));
        CrewRankerCandidate ranked2 = createCrewRankerCandidate(1L, 2, 10, createAndong(2L));
        CrewRankerCandidate ranked3 = createCrewRankerCandidate(1L, 1, 15, createKwangwon(3L));

        jdbcRepository.insertBatch(List.of(ranked1, ranked2, ranked3));

        assertSoftly(softly -> {
            List<CrewRanker> rankers = jpaRepository.findAllByCrewIdAndRankLessThanEqualOrderByRankAsc(1L, 2);
            softly.assertThat(rankers).hasSize(2);
            softly.assertThat(rankers.get(0).getMemberId()).isEqualTo(3L);
            softly.assertThat(rankers.get(1).getMemberId()).isEqualTo(2L);
        });
    }

    @Test
    @DisplayName("가입/스쿼드생성/스쿼드참여/댓글 활동을 가중치(5/10/3/1)로 합산하여 멤버별 점수를 집계한다")
    void aggregateRankedMembersGivenActivityWeight() {
        // given
        Member owner = memberJpaRepository.save(createRevi());
        Member joiner = memberJpaRepository.save(createAndong());
        Member creator = memberJpaRepository.save(createKwangwon());
        Member participant = memberJpaRepository.save(createMember(4));
        Member commenter = memberJpaRepository.save(createMember(5));
        Member outsider = memberJpaRepository.save(createMember(6));

        Crew crew = crewJpaRepository.save(CrewFixture.createCrew(owner, WINDOW_FROM.minusDays(30)));

        // 가입(+5): 윈도우 내 활동이므로 집계 대상
        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, joiner, WINDOW_FROM.plusDays(1)));
        // 가입(+5): 윈도우 밖 활동이므로 집계에서 제외
        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, outsider, WINDOW_FROM.minusDays(1)));

        // 스쿼드생성(+10) + 리더로서 스쿼드참여(+3) = 13점. 스쿼드 생성 시각은 감사(auditing)로 채워지므로 이후 직접 덮어쓴다.
        Squad createdSquad = squadJpaRepository.save(SquadFixture.createSquad(crew, creator, WINDOW_FROM.plusDays(2)));
        overwriteSquadCreatedAt(createdSquad.getId(), WINDOW_FROM.plusDays(2));

        // 스쿼드참여(+3): 리더가 아닌 일반 참여자
        squadMemberJpaRepository.save(SquadMemberFixture.createGeneralSquadMember(createdSquad, participant, WINDOW_FROM.plusDays(3)));

        // 댓글(+1)
        Squad hostSquad = squadJpaRepository.save(SquadFixture.createSquad(crew, owner, WINDOW_FROM.minusDays(40)));
        overwriteSquadCreatedAt(hostSquad.getId(), WINDOW_FROM.minusDays(40));
        SquadComment comment = squadCommentJpaRepository.save(SquadComment.create("좋은 스쿼드네요", hostSquad, commenter));
        overwriteSquadCommentCreatedAt(comment.getId(), WINDOW_FROM.plusDays(4));

        clearPersistenceContext();

        // when
        List<CrewRankerCandidate> candidates = jdbcRepository.aggregateRankedMembersGivenActivityWeight(WINDOW_FROM, WINDOW_TO, 10);

        // then
        assertSoftly(softly -> {
            softly.assertThat(candidates).hasSize(4);
            softly.assertThat(candidates).extracting(CrewRankerCandidate::memberId)
                    .doesNotContain(outsider.getId());

            CrewRankerCandidate first = candidates.get(0);
            softly.assertThat(first.memberId()).isEqualTo(creator.getId());
            softly.assertThat(first.score()).isEqualTo(13L);
            softly.assertThat(first.rank()).isEqualTo(1);

            CrewRankerCandidate second = candidates.get(1);
            softly.assertThat(second.memberId()).isEqualTo(joiner.getId());
            softly.assertThat(second.score()).isEqualTo(5L);
            softly.assertThat(second.rank()).isEqualTo(2);

            CrewRankerCandidate third = candidates.get(2);
            softly.assertThat(third.memberId()).isEqualTo(participant.getId());
            softly.assertThat(third.score()).isEqualTo(3L);
            softly.assertThat(third.rank()).isEqualTo(3);

            CrewRankerCandidate fourth = candidates.get(3);
            softly.assertThat(fourth.memberId()).isEqualTo(commenter.getId());
            softly.assertThat(fourth.score()).isEqualTo(1L);
            softly.assertThat(fourth.rank()).isEqualTo(4);
        });

        // when rankLimit 을 2로 좁히면 상위 2명만 반환된다
        List<CrewRankerCandidate> topTwo = jdbcRepository.aggregateRankedMembersGivenActivityWeight(WINDOW_FROM, WINDOW_TO, 2);

        // then
        assertSoftly(softly -> {
            softly.assertThat(topTwo).hasSize(2);
            softly.assertThat(topTwo).extracting(CrewRankerCandidate::memberId)
                    .containsExactly(creator.getId(), joiner.getId());
        });
    }

    @Test
    @DisplayName("활동 시각이 [from, to] 구간을 벗어나면 집계 대상에서 제외된다")
    void aggregateRankedMembersGivenActivityWeight_excludesActivitiesOutsideWindow() {
        // given
        Member owner = memberJpaRepository.save(createRevi());
        Member beforeWindow = memberJpaRepository.save(createAndong());
        Member afterWindow = memberJpaRepository.save(createKwangwon());
        Crew crew = crewJpaRepository.save(CrewFixture.createCrew(owner, WINDOW_FROM.minusDays(30)));

        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, beforeWindow, WINDOW_FROM.minusSeconds(1)));
        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, afterWindow, WINDOW_TO.plusSeconds(1)));

        clearPersistenceContext();

        // when
        List<CrewRankerCandidate> candidates = jdbcRepository.aggregateRankedMembersGivenActivityWeight(WINDOW_FROM, WINDOW_TO, 5);

        // then
        assertSoftly(softly -> {
            softly.assertThat(candidates).isEmpty();
        });
    }

    private void overwriteSquadCreatedAt(Long squadId, LocalDateTime createdAt) {
        entityManager.flush();
        jdbcTemplate.update("UPDATE squad SET created_at = ? WHERE id = ?", createdAt, squadId);
    }

    private void overwriteSquadCommentCreatedAt(Long commentId, LocalDateTime createdAt) {
        entityManager.flush();
        jdbcTemplate.update("UPDATE squad_comment SET created_at = ? WHERE id = ?", createdAt, commentId);
    }

    private static CrewRankerCandidate createCrewRankerCandidate(Long crewId, int rank, long score, Member member) {
        return new CrewRankerCandidate(
                crewId,
                rank,
                score,
                member.getId(),
                member.getNickname().getValue(),
                member.getMbti().name(),
                LocalDateTime.now()
        );
    }

    private void clearPersistenceContext() {
        entityManager.flush();
        entityManager.clear();
    }
}
