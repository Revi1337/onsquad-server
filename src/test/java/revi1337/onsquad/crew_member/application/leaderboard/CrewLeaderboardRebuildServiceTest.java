package revi1337.onsquad.crew_member.application.leaderboard;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import revi1337.onsquad.common.ApplicationLayerWithTestContainerSupport;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;
import revi1337.onsquad.crew_member.domain.model.CrewRankerDetail;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

class CrewLeaderboardRebuildServiceTest extends ApplicationLayerWithTestContainerSupport {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MemberJpaRepository memberRepository;

    @SpyBean
    private CrewRankerRepository crewRankerRepository;

    @Autowired
    private CrewLeaderboardManager leaderboardManager;

    @SpyBean
    private CrewLeaderboardBackupManager crewLeaderboardBackupManager;

    @Autowired
    private CrewLeaderboardRebuildService leaderboardRebuildService;

    @BeforeEach
    void setUp() {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    @Test
    @DisplayName("새로운 랭킹 데이터를 전달받으면 기존 데이터를 삭제하고 성공적으로 RDB를 갱신한다.")
    void renewTopRankers1() {
        //  given
        Member revi = createRevi();
        Member andong = createAndong();
        memberRepository.saveAll(List.of(revi, andong));

        Long legacyCrewId = 1L;
        LocalDateTime activityTime = LocalDateTime.of(2026, 1, 4, 0, 0, 0);
        crewRankerRepository.insertBatch(List.of(
                createCrewRanker(legacyCrewId, 1, 2, revi, activityTime),
                createCrewRanker(legacyCrewId, 2, 3, andong, activityTime)
        ));

        Long renewCrewId = 5L;
        Instant renewActivityTime = CompositeScore.BASE_DATE.toInstant();
        leaderboardManager.applyActivity(renewCrewId, revi.getId(), renewActivityTime, CrewActivity.CREW_PARTICIPANT);
        leaderboardManager.applyActivity(renewCrewId, andong.getId(), renewActivityTime, CrewActivity.SQUAD_CREATE);
        List<CrewRankerDetail> rankedMember = leaderboardManager.getLeaderboard(renewCrewId, 2);

        // when
        leaderboardRebuildService.renewTopRankers(rankedMember);

        // then
        assertSoftly(softly -> {
            List<CrewRanker> renewRankers = crewRankerRepository.findAllByCrewId(renewCrewId);
            softly.assertThat(renewRankers).hasSize(2);
            softly.assertThat(renewRankers.get(0).getMemberId()).isEqualTo(andong.getId());
            softly.assertThat(renewRankers.get(1).getMemberId()).isEqualTo(revi.getId());
        });
    }

    @Test
    @DisplayName("탈퇴 유저가 포함된 랭킹 후보군을 전달받으면, 해당 유저를 제외하고 순위를 재구성하여 갱신한다.")
    void renewTopRankers2() {
        //  given
        Member revi = createRevi();
        Member andong = createAndong();
        Member member1 = createMember(1);
        Member member2 = createMember(2);
        Member member3 = createMember(3);
        Member member4 = createMember(4);
        Member member5 = createMember(5);
        memberRepository.saveAll(List.of(revi, andong, member1, member2, member3, member4, member5));

        Long crewId = 1L;
        LocalDateTime activityTime = LocalDate.of(2025, 12, 20).atStartOfDay();
        crewRankerRepository.insertBatch(List.of(
                createCrewRanker(crewId, 1, 10, revi, activityTime),
                createCrewRanker(crewId, 2, 9, andong, activityTime)
        ));

        Instant renewActivityTime = CompositeScore.BASE_DATE.toInstant();
        leaderboardManager.applyActivity(crewId, revi.getId(), renewActivityTime, CrewActivity.SQUAD_CREATE);
        leaderboardManager.applyActivity(crewId, andong.getId(), renewActivityTime, CrewActivity.CREW_PARTICIPANT);
        leaderboardManager.applyActivity(crewId, member1.getId(), renewActivityTime, CrewActivity.SQUAD_PARTICIPANT);
        leaderboardManager.applyActivity(crewId, member2.getId(), renewActivityTime.plusSeconds(10), CrewActivity.SQUAD_COMMENT);
        leaderboardManager.applyActivity(crewId, member3.getId(), renewActivityTime.plusSeconds(5), CrewActivity.SQUAD_COMMENT_REPLY);
        leaderboardManager.applyActivity(crewId, member4.getId(), renewActivityTime.plusSeconds(3), CrewActivity.SQUAD_COMMENT_REPLY);
        leaderboardManager.applyActivity(crewId, member5.getId(), renewActivityTime.plusSeconds(1), CrewActivity.SQUAD_COMMENT_REPLY);
        List<CrewRankerDetail> rankedMember = leaderboardManager.getLeaderboard(crewId, 50);
        memberRepository.deleteById(revi.getId());
        memberRepository.deleteById(andong.getId());
        clearPersistenceContext();

        // when
        leaderboardRebuildService.renewTopRankers(rankedMember);

        // then
        assertSoftly(softly -> {
            clearPersistenceContext();
            List<CrewRanker> renewRankers = crewRankerRepository.findAllByCrewId(crewId);
            softly.assertThat(renewRankers).hasSize(5);
            softly.assertThat(renewRankers).extracting(CrewRanker::getRank)
                    .containsExactlyInAnyOrder(1, 2, 3, 4, 5);
            softly.assertThat(renewRankers).extracting(CrewRanker::getNickname)
                    .containsExactlyInAnyOrder("nick1", "nick2", "nick3", "nick4", "nick5");
        });
    }

    @Test
    @DisplayName("DB 갱신 중 예외가 발생하더라도, Redis 백업본을 통해 기존 데이터를 안전하게 복구한다.")
    void renewTopRankers3() {
        // given
        Member revi = memberRepository.save(createRevi());
        crewRankerRepository.insertBatch(List.of(createCrewRanker(1L, 1, 10, revi, LocalDateTime.now())));
        crewLeaderboardBackupManager.backupCurrentTopRankers();
        doThrow(new RuntimeException())
                .doCallRealMethod()
                .when(crewRankerRepository).insertBatch(anyList());
        List<CrewRankerDetail> newRankedResults = List.of(createCrewRankerDetail(1L, 2, 100, revi, LocalDateTime.now()));

        // when
        assertThatThrownBy(() -> leaderboardRebuildService.renewTopRankers(newRankedResults))
                .isInstanceOf(RuntimeException.class);

        // then
        assertSoftly(softly -> {
            List<CrewRanker> finalMembers = crewRankerRepository.findAll();
            softly.assertThat(finalMembers).hasSize(1);
            softly.assertThat(finalMembers.get(0).getScore()).isEqualTo(10);
            verify(crewRankerRepository, times(3)).insertBatch(anyList());
            verify(crewLeaderboardBackupManager, times(1)).getBackup();
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

    public CrewRankerDetail createCrewRankerDetail(Long crewId, int rank, long score, Member member, LocalDateTime lastActivityTime) {
        return new CrewRankerDetail(
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
