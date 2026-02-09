package revi1337.onsquad.crew_member.application.leaderboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CrewLeaderboard Key 매핑 테스트")
class CrewLeaderboardKeyMapperTest {

    @Test
    @DisplayName("크루 식별자를 사용하여 현재 시즌의 리더보드 Redis 키를 생성한다")
    void toLeaderboardKey() {
        Long crewId = 1L;

        String leaderboardKey = CrewLeaderboardKeyMapper.toLeaderboardKey(crewId);

        assertThat(leaderboardKey).isEqualTo("onsquad:crew:1:leaderboard:current");
    }

    @Test
    @DisplayName("다수의 크루 식별자 리스트를 현재 시즌의 리더보드 키 리스트로 변환한다")
    void toLeaderboardKeys() {
        List<Long> crewIds = List.of(1L, 2L, 3L);

        List<String> leaderboardKeys = CrewLeaderboardKeyMapper.toLeaderboardKeys(crewIds);

        assertThat(leaderboardKeys).containsExactly(
                "onsquad:crew:1:leaderboard:current",
                "onsquad:crew:2:leaderboard:current",
                "onsquad:crew:3:leaderboard:current"
        );
    }

    @Test
    @DisplayName("크루 식별자를 사용하여 지난 시즌(지난주)의 리더보드 Redis 키를 생성한다")
    void toPreviousLeaderboardKey() {
        Long crewId = 1L;

        String previousLeaderboardKey = CrewLeaderboardKeyMapper.toPreviousLeaderboardKey(crewId);

        assertThat(previousLeaderboardKey).isEqualTo("onsquad:crew:1:leaderboard:last-week");
    }

    @Test
    @DisplayName("다수의 크루 식별자 리스트를 지난 시즌의 리더보드 키 리스트로 변환한다")
    void toPreviousLeaderboardKeys() {
        List<Long> crewIds = List.of(1L, 2L, 3L);

        List<String> previousLeaderboardKeys = CrewLeaderboardKeyMapper.toPreviousLeaderboardKeys(crewIds);

        assertThat(previousLeaderboardKeys).containsExactly(
                "onsquad:crew:1:leaderboard:last-week",
                "onsquad:crew:2:leaderboard:last-week",
                "onsquad:crew:3:leaderboard:last-week"
        );
    }

    @Test
    @DisplayName("Redis SCAN을 위한 현재 리더보드 키 전체 검색 패턴을 반환한다")
    void getLeaderboardPattern() {
        String leaderboardPattern = CrewLeaderboardKeyMapper.getLeaderboardPattern();

        assertThat(leaderboardPattern).isEqualTo("onsquad:crew:*:leaderboard:current");
    }

    @Test
    @DisplayName("생성된 리더보드 Redis 키 문자열에서 크루 식별자(ID)만 추출한다")
    void parseCrewIdFromKey() {
        String computedLeaderboardKey = "onsquad:crew:1:leaderboard:current";
        String computedPreviousLeaderboardKey = "onsquad:crew:1:last-week";

        Long crewId1 = CrewLeaderboardKeyMapper.parseCrewIdFromKey(computedLeaderboardKey);
        Long crewId2 = CrewLeaderboardKeyMapper.parseCrewIdFromKey(computedPreviousLeaderboardKey);

        assertSoftly(softly -> {
            softly.assertThat(crewId1).isEqualTo(1);
            softly.assertThat(crewId2).isEqualTo(1);
        });
    }

    @Test
    @DisplayName("멤버 식별자를 Redis SortedSet 내부의 멤버 키 포맷으로 변환한다")
    void toMemberKey() {
        Long memberId = 1L;

        String memberKey = CrewLeaderboardKeyMapper.toMemberKey(memberId);

        assertThat(memberKey).isEqualTo("member:1");
    }

    @Test
    @DisplayName("Redis에서 조회한 멤버 키 문자열에서 멤버 식별자(ID)만 추출한다")
    void parseMemberIdFromKey() {
        String memberKey = "member:1";

        Long memberId = CrewLeaderboardKeyMapper.parseMemberIdFromKey(memberKey);

        assertThat(memberId).isEqualTo(1);
    }
}
