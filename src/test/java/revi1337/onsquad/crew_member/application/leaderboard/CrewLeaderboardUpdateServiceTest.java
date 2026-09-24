package revi1337.onsquad.crew_member.application.leaderboard;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.container.MySqlTestContainerInitializer;
import revi1337.onsquad.common.fixture.CrewFixture;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

@Sql({"/mysql-truncate.sql"})
@ContextConfiguration(initializers = MySqlTestContainerInitializer.class)
class CrewLeaderboardUpdateServiceTest extends ApplicationLayerTestSupport {

    private static final LocalDateTime WINDOW_FROM = LocalDateTime.of(2026, 1, 5, 0, 0);
    private static final LocalDateTime WINDOW_TO = LocalDateTime.of(2026, 1, 12, 0, 0);

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private CrewRankerRepository crewRankerRepository;

    @Autowired
    private CrewLeaderboardUpdateService leaderboardUpdateService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("기존 랭킹 데이터를 모두 삭제하고, 집계 기간 내 활동을 기반으로 새로운 순위를 반영한다")
    void refreshLeaderboards() {
        // given
        Member owner = memberJpaRepository.save(createRevi());
        Member joiner = memberJpaRepository.save(createMember(1));
        Crew crew = crewJpaRepository.save(CrewFixture.createCrew(owner, WINDOW_FROM.minusDays(30)));
        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, joiner, WINDOW_FROM.plusDays(1)));
        saveActivityLog(crew.getId(), joiner.getId(), CrewActivity.CREW_PARTICIPANT, WINDOW_FROM.plusDays(1));

        // 스케줄러 실행 전 남아있던 지난 주차 랭킹 데이터
        crewRankerRepository.insertBatch(List.of(staleCandidate(crew.getId(), owner)));
        clearPersistenceContext();

        // when
        leaderboardUpdateService.refreshLeaderboards(WINDOW_FROM, WINDOW_TO, 5);

        // then
        assertSoftly(softly -> {
            List<CrewRanker> rankers = crewRankerRepository.findAllByCrewId(crew.getId());
            softly.assertThat(rankers).hasSize(1);
            softly.assertThat(rankers.get(0).getMemberId()).isEqualTo(joiner.getId());
            softly.assertThat(rankers.get(0).getScore()).isEqualTo(5L);
            softly.assertThat(rankers.get(0).getRank()).isEqualTo(1);
        });
    }

    @Test
    @DisplayName("rankLimit 을 초과하는 순위의 활동 데이터는 반영되지 않는다")
    void refreshLeaderboards_appliesRankLimit() {
        // given
        Member owner = memberJpaRepository.save(createRevi());
        Member first = memberJpaRepository.save(createMember(1));
        Member second = memberJpaRepository.save(createMember(2));
        Member third = memberJpaRepository.save(createMember(3));
        Crew crew = crewJpaRepository.save(CrewFixture.createCrew(owner, WINDOW_FROM.minusDays(30)));

        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, first, WINDOW_FROM.plusDays(1)));
        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, second, WINDOW_FROM.plusDays(2)));
        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, third, WINDOW_FROM.plusDays(3)));
        saveActivityLog(crew.getId(), first.getId(), CrewActivity.CREW_PARTICIPANT, WINDOW_FROM.plusDays(1));
        saveActivityLog(crew.getId(), second.getId(), CrewActivity.CREW_PARTICIPANT, WINDOW_FROM.plusDays(2));
        saveActivityLog(crew.getId(), third.getId(), CrewActivity.CREW_PARTICIPANT, WINDOW_FROM.plusDays(3));
        clearPersistenceContext();

        // when
        leaderboardUpdateService.refreshLeaderboards(WINDOW_FROM, WINDOW_TO, 2);

        // then
        assertSoftly(softly -> {
            List<CrewRanker> rankers = crewRankerRepository.findAllByCrewId(crew.getId());
            softly.assertThat(rankers).hasSize(2);
            softly.assertThat(rankers).extracting(CrewRanker::getMemberId)
                    .containsExactly(third.getId(), second.getId());
        });
    }

    @Test
    @DisplayName("over-fetch 된 후보 중 탈퇴하여 crew_member 에 남아있지 않은 멤버는 최종 결과에서 제외된다")
    void refreshLeaderboards_excludesWithdrawnMembersFromOverFetchedCandidates() {
        // given
        Member owner = memberJpaRepository.save(createRevi());
        Member activeMember = memberJpaRepository.save(createMember(1));
        Member withdrawnMember = memberJpaRepository.save(createMember(2));
        Crew crew = crewJpaRepository.save(CrewFixture.createCrew(owner, WINDOW_FROM.minusDays(30)));

        // activeMember 는 crew_member 에 남아있는 유효 멤버
        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, activeMember, WINDOW_FROM.plusDays(1)));
        saveActivityLog(crew.getId(), activeMember.getId(), CrewActivity.CREW_PARTICIPANT, WINDOW_FROM.plusDays(1));

        // withdrawnMember 는 활동 로그(append-only)는 남아있지만, 이후 탈퇴하여 crew_member 에는 존재하지 않는다
        saveActivityLog(crew.getId(), withdrawnMember.getId(), CrewActivity.SQUAD_CREATE, WINDOW_FROM.plusDays(2));
        clearPersistenceContext();

        // when
        leaderboardUpdateService.refreshLeaderboards(WINDOW_FROM, WINDOW_TO, 5);

        // then
        assertSoftly(softly -> {
            List<CrewRanker> rankers = crewRankerRepository.findAllByCrewId(crew.getId());
            softly.assertThat(rankers).hasSize(1);
            softly.assertThat(rankers.get(0).getMemberId()).isEqualTo(activeMember.getId());
            softly.assertThat(rankers).extracting(CrewRanker::getMemberId)
                    .doesNotContain(withdrawnMember.getId());
        });
    }

    @Test
    @DisplayName("탈퇴한 상위 후보가 필터링되면, 남은 후보들의 순위는 공백 없이 1위부터 다시 매겨진다")
    void refreshLeaderboards_reassignsRanksSequentiallyAfterFiltering() {
        // given
        Member owner = memberJpaRepository.save(createRevi());
        Member topWithdrawn = memberJpaRepository.save(createMember(1));
        Member mid = memberJpaRepository.save(createMember(2));
        Member low = memberJpaRepository.save(createMember(3));
        Crew crew = crewJpaRepository.save(CrewFixture.createCrew(owner, WINDOW_FROM.minusDays(30)));

        // topWithdrawn 은 가장 높은 점수(스쿼드생성 10점)를 갖지만 crew_member 에는 없다 (탈퇴)
        saveActivityLog(crew.getId(), topWithdrawn.getId(), CrewActivity.SQUAD_CREATE, WINDOW_FROM.plusDays(1));

        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, mid, WINDOW_FROM.plusDays(2)));
        saveActivityLog(crew.getId(), mid.getId(), CrewActivity.SQUAD_PARTICIPANT, WINDOW_FROM.plusDays(2));

        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, low, WINDOW_FROM.plusDays(3)));
        saveActivityLog(crew.getId(), low.getId(), CrewActivity.SQUAD_COMMENT, WINDOW_FROM.plusDays(3));
        clearPersistenceContext();

        // when
        leaderboardUpdateService.refreshLeaderboards(WINDOW_FROM, WINDOW_TO, 5);

        // then
        assertSoftly(softly -> {
            List<CrewRanker> rankers = crewRankerRepository.findAllByCrewId(crew.getId());
            softly.assertThat(rankers).hasSize(2);
            softly.assertThat(rankers.get(0).getMemberId()).isEqualTo(mid.getId());
            softly.assertThat(rankers.get(0).getRank())
                    .as("탈퇴한 1위가 빠졌으므로 mid 는 2위가 아닌 1위여야 한다")
                    .isEqualTo(1);
            softly.assertThat(rankers.get(1).getMemberId()).isEqualTo(low.getId());
            softly.assertThat(rankers.get(1).getRank())
                    .as("탈퇴한 1위가 빠졌으므로 low 는 3위가 아닌 2위여야 한다")
                    .isEqualTo(2);
        });
    }

    @Test
    @DisplayName("크루의 유효 후보 수가 rankLimit 보다 적으면, 있는 만큼만 순위가 매겨진다")
    void refreshLeaderboards_handlesFewerValidCandidatesThanRankLimit() {
        // given
        Member owner = memberJpaRepository.save(createRevi());
        Member onlyMember = memberJpaRepository.save(createMember(1));
        Crew crew = crewJpaRepository.save(CrewFixture.createCrew(owner, WINDOW_FROM.minusDays(30)));

        crewMemberJpaRepository.save(CrewMemberFactory.general(crew, onlyMember, WINDOW_FROM.plusDays(1)));
        saveActivityLog(crew.getId(), onlyMember.getId(), CrewActivity.CREW_PARTICIPANT, WINDOW_FROM.plusDays(1));
        clearPersistenceContext();

        // when: rankLimit(5) 보다 유효 후보 수(1명)가 적은 상황
        leaderboardUpdateService.refreshLeaderboards(WINDOW_FROM, WINDOW_TO, 5);

        // then
        assertSoftly(softly -> {
            List<CrewRanker> rankers = crewRankerRepository.findAllByCrewId(crew.getId());
            softly.assertThat(rankers).hasSize(1);
            softly.assertThat(rankers.get(0).getMemberId()).isEqualTo(onlyMember.getId());
            softly.assertThat(rankers.get(0).getRank()).isEqualTo(1);
        });
    }

    @Test
    @DisplayName("집계 기간 내 활동이 전혀 없는 크루는 예외 없이 랭커가 비어있는 상태로 처리된다")
    void refreshLeaderboards_handlesCrewWithNoOverFetchedCandidates() {
        // given
        Member owner1 = memberJpaRepository.save(createRevi());
        Member activeMember = memberJpaRepository.save(createMember(1));
        Crew activeCrew = crewJpaRepository.save(CrewFixture.createCrew(owner1, WINDOW_FROM.minusDays(30)));
        crewMemberJpaRepository.save(CrewMemberFactory.general(activeCrew, activeMember, WINDOW_FROM.plusDays(1)));
        saveActivityLog(activeCrew.getId(), activeMember.getId(), CrewActivity.CREW_PARTICIPANT, WINDOW_FROM.plusDays(1));

        // idleCrew 는 집계 기간 내 활동 로그가 전혀 없다
        Member owner2 = memberJpaRepository.save(createMember(2));
        Crew idleCrew = crewJpaRepository.save(CrewFixture.createCrew(owner2, WINDOW_FROM.minusDays(30)));
        clearPersistenceContext();

        // when
        leaderboardUpdateService.refreshLeaderboards(WINDOW_FROM, WINDOW_TO, 5);

        // then
        assertSoftly(softly -> {
            List<CrewRanker> activeCrewRankers = crewRankerRepository.findAllByCrewId(activeCrew.getId());
            softly.assertThat(activeCrewRankers).hasSize(1);
            softly.assertThat(activeCrewRankers.get(0).getMemberId()).isEqualTo(activeMember.getId());

            List<CrewRanker> idleCrewRankers = crewRankerRepository.findAllByCrewId(idleCrew.getId());
            softly.assertThat(idleCrewRankers).isEmpty();
        });
    }

    private void saveActivityLog(Long crewId, Long memberId, CrewActivity activityType, LocalDateTime lastActivityAt) {
        jdbcTemplate.update(
                "INSERT INTO crew_activity_score (crew_id, member_id, weight, last_activity_at) VALUES (?, ?, ?, ?)",
                crewId, memberId, activityType.getWeight(), lastActivityAt
        );
    }

    private CrewRankerCandidate staleCandidate(Long crewId, Member member) {
        return new CrewRankerCandidate(
                crewId,
                1,
                999L,
                member.getId(),
                member.getNickname().getValue(),
                member.getMbti().name(),
                LocalDateTime.now()
        );
    }
}
