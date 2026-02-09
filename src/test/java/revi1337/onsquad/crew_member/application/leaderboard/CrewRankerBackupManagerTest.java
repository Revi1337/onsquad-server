package revi1337.onsquad.crew_member.application.leaderboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import revi1337.onsquad.common.ApplicationLayerWithTestContainerSupport;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;
import revi1337.onsquad.crew_member.domain.model.CrewRankedMemberDetail;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankedMemberRepository;
import revi1337.onsquad.member.domain.entity.Member;

class CrewRankerBackupManagerTest extends ApplicationLayerWithTestContainerSupport {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CrewRankedMemberRepository crewRankedMemberRepository;

    @Autowired
    private CrewRankerBackupManager crewRankerBackupManager;

    @BeforeEach
    void setUp() {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    @Test
    @DisplayName("RDB의 현재 순위 데이터를 Redis로 백업한다.")
    void backupCurrentTopRankers() {
        LocalDateTime activityTime = LocalDate.of(2026, 1, 4).atStartOfDay();
        CrewRankedMember rankedMember = createCrewRankedMember(1L, 1, 2, createAndong(2L), activityTime);
        crewRankedMemberRepository.insertBatch(List.of(rankedMember));
        String key = CrewLeaderboardKeyMapper.toPreviousLeaderboardKey(1L);
        assertThat(stringRedisTemplate.opsForValue().get(key)).isNull();

        crewRankerBackupManager.backupCurrentTopRankers();

        assertThat(stringRedisTemplate.opsForValue().get(key)).isNotNull();
    }

    @Test
    @DisplayName("Redis 에 백업된 데이터들을 삭제한다.")
    void removeBackups() {
        LocalDateTime activityTime = LocalDate.of(2026, 1, 4).atStartOfDay();
        CrewRankedMember rankedMember = createCrewRankedMember(1L, 1, 2, createAndong(2L), activityTime);
        crewRankedMemberRepository.insertBatch(List.of(rankedMember));
        String key = CrewLeaderboardKeyMapper.toPreviousLeaderboardKey(1L);
        crewRankerBackupManager.backupCurrentTopRankers();
        assertThat(stringRedisTemplate.hasKey(key)).isTrue();

        crewRankerBackupManager.removeBackups(List.of(1L));

        assertThat(stringRedisTemplate.hasKey(key)).isFalse();
    }

    @Test
    @DisplayName("Redis에 저장된 백업 데이터를 전체 조회하여 DTO 리스트로 복구한다.")
    void getBackup() {
        LocalDateTime activityTime = LocalDate.of(2026, 1, 4).atStartOfDay();
        CrewRankedMember rankedMember1 = createCrewRankedMember(1L, 1, 2, createRevi(1L), activityTime);
        CrewRankedMember rankedMember2 = createCrewRankedMember(1L, 2, 3, createAndong(2L), activityTime);
        crewRankedMemberRepository.insertBatch(List.of(rankedMember1, rankedMember2));
        crewRankerBackupManager.backupCurrentTopRankers();

        List<CrewRankedMemberDetail> backup = crewRankerBackupManager.getBackup();

        assertSoftly(softly -> {
            softly.assertThat(backup).hasSize(2);
            softly.assertThat(backup.get(0).crewId()).isEqualTo(rankedMember1.getCrewId());
            softly.assertThat(backup.get(0).rank()).isEqualTo(rankedMember1.getRank());
            softly.assertThat(backup.get(0).score()).isEqualTo(rankedMember1.getScore());
            softly.assertThat(backup.get(0).memberId()).isEqualTo(rankedMember1.getMemberId());
            softly.assertThat(backup.get(0).nickname()).isEqualTo(rankedMember1.getNickname());
            softly.assertThat(backup.get(0).mbti()).isEqualTo(rankedMember1.getMbti());
            softly.assertThat(backup.get(1).crewId()).isEqualTo(rankedMember2.getCrewId());
            softly.assertThat(backup.get(1).rank()).isEqualTo(rankedMember2.getRank());
            softly.assertThat(backup.get(1).score()).isEqualTo(rankedMember2.getScore());
            softly.assertThat(backup.get(1).memberId()).isEqualTo(rankedMember2.getMemberId());
            softly.assertThat(backup.get(1).nickname()).isEqualTo(rankedMember2.getNickname());
            softly.assertThat(backup.get(1).mbti()).isEqualTo(rankedMember2.getMbti());
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
}
