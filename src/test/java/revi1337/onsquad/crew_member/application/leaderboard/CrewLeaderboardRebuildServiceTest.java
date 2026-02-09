package revi1337.onsquad.crew_member.application.leaderboard;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.Instant;
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
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;
import revi1337.onsquad.crew_member.domain.model.CrewRankedMemberDetail;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankedMemberRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

class CrewLeaderboardRebuildServiceTest extends ApplicationLayerWithTestContainerSupport {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MemberJpaRepository memberRepository;

    @SpyBean
    private CrewRankedMemberRepository crewRankedMemberRepository;

    @Autowired
    private CrewLeaderboardManager leaderboardManager;

    @SpyBean
    private CrewRankerBackupManager crewRankerBackupManager;

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
        crewRankedMemberRepository.insertBatch(List.of(
                createCrewRankedMember(legacyCrewId, 1, 2, revi, activityTime),
                createCrewRankedMember(legacyCrewId, 2, 3, andong, activityTime)
        ));

        Long renewCrewId = 5L;
        Instant renewActivityTime = CompositeScore.BASE_DATE.toInstant();
        leaderboardManager.applyActivity(renewCrewId, revi.getId(), renewActivityTime, CrewActivity.CREW_PARTICIPANT);
        leaderboardManager.applyActivity(renewCrewId, andong.getId(), renewActivityTime, CrewActivity.SQUAD_CREATE);
        List<CrewRankedMemberDetail> rankedMember = leaderboardManager.getLeaderboard(renewCrewId, 2);

        // when
        leaderboardRebuildService.renewTopRankers(rankedMember);

        // then
        assertSoftly(softly -> {
            List<CrewRankedMember> renewRankers = crewRankedMemberRepository.findAllByCrewId(renewCrewId);
            softly.assertThat(renewRankers).hasSize(2);
            softly.assertThat(renewRankers.get(0).getMemberId()).isEqualTo(andong.getId());
            softly.assertThat(renewRankers.get(1).getMemberId()).isEqualTo(revi.getId());
        });
    }

    @Test
    @DisplayName("DB 갱신 중 예외가 발생하더라도, Redis 백업본을 통해 기존 데이터를 안전하게 복구한다.")
    void renewTopRankers2() {
        // given
        Member revi = memberRepository.save(createRevi());
        crewRankedMemberRepository.insertBatch(List.of(createCrewRankedMember(1L, 1, 10, revi, LocalDateTime.now())));
        crewRankerBackupManager.backupCurrentTopRankers();
        doThrow(new RuntimeException())
                .doCallRealMethod()
                .when(crewRankedMemberRepository).insertBatch(anyList());
        List<CrewRankedMemberDetail> newRankedResults = List.of(createCrewRankedMemberResult(1L, 2, 100, revi, LocalDateTime.now()));

        // when
        assertThatThrownBy(() -> leaderboardRebuildService.renewTopRankers(newRankedResults))
                .isInstanceOf(RuntimeException.class);

        // then
        assertSoftly(softly -> {
            List<CrewRankedMember> finalMembers = crewRankedMemberRepository.findAll();
            softly.assertThat(finalMembers).hasSize(1);
            softly.assertThat(finalMembers.get(0).getScore()).isEqualTo(10);
            verify(crewRankedMemberRepository, times(3)).insertBatch(anyList());
            verify(crewRankerBackupManager, times(1)).getBackup();
        });
    }

    public CrewRankedMember createCrewRankedMember(Long crewId, int rank, long score, Member member, LocalDateTime lastActivityTime) {
        return new CrewRankedMember(
                crewId,
                rank,
                score,
                member.getId(),
                member.getNickname().getValue(),
                member.getMbti().name(),
                lastActivityTime
        );
    }

    public CrewRankedMemberDetail createCrewRankedMemberResult(Long crewId, int rank, long score, Member member, LocalDateTime lastActivityTime) {
        return new CrewRankedMemberDetail(
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
