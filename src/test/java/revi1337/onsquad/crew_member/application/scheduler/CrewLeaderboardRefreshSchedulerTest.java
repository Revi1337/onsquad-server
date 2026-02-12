package revi1337.onsquad.crew_member.application.scheduler;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.ApplicationLayerWithTestContainerSupport;
import revi1337.onsquad.crew_member.application.leaderboard.CompositeScore;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardBackupManager;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardManager;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;
import revi1337.onsquad.crew_member.domain.model.CrewRankerDetail;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

class CrewLeaderboardRefreshSchedulerTest extends ApplicationLayerWithTestContainerSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewRankerRepository crewRankerRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CrewLeaderboardManager crewLeaderboardManager;

    @Autowired
    private CrewLeaderboardBackupManager crewLeaderboardBackupManager;

    @Autowired
    private CrewLeaderboardRefreshScheduler crewLeaderboardRefreshScheduler;

    @BeforeEach
    void setUp() {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    @Test
    @DisplayName("스케줄러 실행 시 기존 랭킹은 백업되고, 최신 활동 기반의 새로운 랭킹이 DB에 반영된다")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void refreshLeaderboard() {
        // given
        Member revi = createRevi();
        Member andong = createAndong();
        Member kwangwon = createKwangwon();
        memberRepository.saveAll(List.of(revi, andong, kwangwon));
        CrewRanker previousRankedMember1 = createCrewRanker(1L, 2, 1, revi, LocalDateTime.now());
        CrewRanker previousRankedMember2 = createCrewRanker(1L, 2, 1, andong, LocalDateTime.now());
        crewRankerRepository.insertBatch(List.of(previousRankedMember1, previousRankedMember2));

        Instant activityTime = CompositeScore.BASE_DATE.toInstant();
        crewLeaderboardManager.applyActivity(1L, revi.getId(), activityTime.plusSeconds(600), CrewActivity.SQUAD_CREATE);
        crewLeaderboardManager.applyActivity(1L, andong.getId(), activityTime.plusSeconds(660), CrewActivity.SQUAD_CREATE);
        crewLeaderboardManager.applyActivity(1L, kwangwon.getId(), activityTime.plusSeconds(720), CrewActivity.SQUAD_COMMENT);

        // when
        crewLeaderboardRefreshScheduler.refreshLeaderboard();

        // then
        assertSoftly(softly -> {
            List<CrewRankerDetail> backupRankedMembers = crewLeaderboardBackupManager.getBackup();
            List<CrewRanker> currentRankedMembers = crewRankerRepository.findAllByCrewId(1L);
            List<CrewRankerDetail> allRankedMembers = crewLeaderboardManager.getAllLeaderboards(-1);

            softly.assertThat(backupRankedMembers).hasSize(2);
            softly.assertThat(backupRankedMembers.get(0).memberId()).isEqualTo(revi.getId());
            softly.assertThat(backupRankedMembers.get(1).memberId()).isEqualTo(andong.getId());

            softly.assertThat(currentRankedMembers).hasSize(3);
            softly.assertThat(currentRankedMembers.get(0).getMemberId()).isEqualTo(andong.getId());
            softly.assertThat(currentRankedMembers.get(1).getMemberId()).isEqualTo(revi.getId());
            softly.assertThat(currentRankedMembers.get(2).getMemberId()).isEqualTo(kwangwon.getId());

            softly.assertThat(allRankedMembers).isEmpty();
        });
    }

    public CrewRanker createCrewRanker(Long crewId, int rank, long score, Member member, LocalDateTime lastActivityTime) {
        return new CrewRanker(
                crewId,
                rank,
                score,
                member.getId(),
                member.getNickname().getValue(),
                member.getMbti().name(),
                lastActivityTime
        );
    }
}
