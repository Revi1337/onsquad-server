package revi1337.onsquad.crew_member.application.leaderboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CrewLeaderboard Key 매핑 테스트")
class CrewLeaderboardKeyMapperTest {

    @Test
    @DisplayName("크루 식별자를 사용하여 현재 실시간 리더보드 Redis 키를 생성한다")
    void toLeaderboardKey() {
        Long crewId = 1L;

        String leaderboardKey = CrewLeaderboardKeyMapper.toLeaderboardKey(crewId);

        assertThat(leaderboardKey).isEqualTo("onsquad:crew:1:leaderboard");
    }

    @Test
    @DisplayName("다수의 크루 식별자 리스트를 실시간 리더보드 키 리스트로 변환한다")
    void toLeaderboardKeys() {
        List<Long> crewIds = List.of(1L, 2L, 3L);

        List<String> leaderboardKeys = CrewLeaderboardKeyMapper.toLeaderboardKeys(crewIds);

        assertThat(leaderboardKeys).containsExactly(
                "onsquad:crew:1:leaderboard",
                "onsquad:crew:2:leaderboard",
                "onsquad:crew:3:leaderboard"
        );
    }

    @Test
    @DisplayName("크루 식별자를 사용하여 스케줄러 이관용 스냅샷 Redis 키를 생성한다")
    void toLeaderboardSnapshotKey() {
        Long crewId = 1L;

        String snapshotKey = CrewLeaderboardKeyMapper.toLeaderboardSnapshotKey(crewId);

        assertThat(snapshotKey).isEqualTo("onsquad:crew:1:leaderboard:snapshot");
    }

    @Test
    @DisplayName("다수의 크루 식별자 리스트를 리더보드 스냅샷 키 리스트로 변환한다")
    void toLeaderboardSnapshotKeys() {
        List<Long> crewIds = List.of(1L, 2L, 3L);

        List<String> snapshotKeys = CrewLeaderboardKeyMapper.toLeaderboardSnapshotKeys(crewIds);

        assertThat(snapshotKeys).containsExactly(
                "onsquad:crew:1:leaderboard:snapshot",
                "onsquad:crew:2:leaderboard:snapshot",
                "onsquad:crew:3:leaderboard:snapshot"
        );
    }

    @Test
    @DisplayName("Redis SCAN을 위한 리더보드 키 전체 검색 패턴을 반환한다")
    void getLeaderboardPattern() {
        String leaderboardPattern = CrewLeaderboardKeyMapper.getLeaderboardPattern();
        String snapshotPattern = CrewLeaderboardKeyMapper.getLeaderboardSnapshotPattern();

        assertSoftly(softly -> {
            softly.assertThat(leaderboardPattern).isEqualTo("onsquad:crew:*:leaderboard");
            softly.assertThat(snapshotPattern).isEqualTo("onsquad:crew:*:leaderboard:snapshot");
        });
    }

    @Test
    @DisplayName("생성된 리더보드 및 스냅샷 Redis 키에서 크루 식별자(ID)를 정확히 추출한다")
    void parseCrewIdFromKey() {
        String computedLeaderboardKey = "onsquad:crew:1:leaderboard";
        String computedSnapshotKey = "onsquad:crew:1:leaderboard:snapshot";

        Long crewId1 = CrewLeaderboardKeyMapper.parseCrewIdFromKey(computedLeaderboardKey);
        Long crewId2 = CrewLeaderboardKeyMapper.parseCrewIdFromKey(computedSnapshotKey);

        assertSoftly(softly -> {
            softly.assertThat(crewId1).isEqualTo(1L);
            softly.assertThat(crewId2).isEqualTo(1L);
        });
    }

    @Test
    @DisplayName("다수의 리더보드 및 스냅샷 키 리스트에서 크루 식별자(ID)들을 순서대로 정확히 추출한다")
    void parseCrewIdFromKeys() {
        List<String> computedKeys = List.of(
                "onsquad:crew:1:leaderboard",
                "onsquad:crew:2:leaderboard:snapshot",
                "onsquad:crew:3:leaderboard",
                "onsquad:crew:4:leaderboard:snapshot"
        );

        List<Long> crewIds = CrewLeaderboardKeyMapper.parseCrewIdFromKeys(computedKeys);

        assertThat(crewIds).hasSize(4).containsExactly(1L, 2L, 3L, 4L);
    }

    @Test
    @DisplayName("멤버 식별자를 Redis SortedSet 내부의 멤버 키 포맷으로 변환한다")
    void toMemberKey() {
        Long memberId = 1L;

        String memberKey = CrewLeaderboardKeyMapper.toMemberKey(memberId);

        assertThat(memberKey).isEqualTo("member:1");
    }

    @Test
    @DisplayName("Redis 멤버 키 문자열에서 멤버 식별자(ID)를 정확히 추출한다")
    void parseMemberIdFromKey() {
        String memberKey = "member:1";

        Long memberId = CrewLeaderboardKeyMapper.parseMemberIdFromKey(memberKey);

        assertThat(memberId).isEqualTo(1L);
    }
}
