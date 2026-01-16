package revi1337.onsquad.crew_member.application.leaderboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CrewLeaderboard Key 매핑 테스트")
class CrewLeaderboardKeyMapperTest {

    @Test
    void toLeaderboardKey() {
        Long crewId = 1L;

        String leaderboardKey = CrewLeaderboardKeyMapper.toLeaderboardKey(crewId);

        assertThat(leaderboardKey).isEqualTo("onsquad:crew:1:leaderboard:current");
    }

    @Test
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
    void toPreviousLeaderboardKey() {
        Long crewId = 1L;

        String previousLeaderboardKey = CrewLeaderboardKeyMapper.toPreviousLeaderboardKey(crewId);

        assertThat(previousLeaderboardKey).isEqualTo("onsquad:crew:1:leaderboard:last-week");
    }

    @Test
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
    void getLeaderboardPattern() {
        String leaderboardPattern = CrewLeaderboardKeyMapper.getLeaderboardPattern();

        assertThat(leaderboardPattern).isEqualTo("onsquad:crew:*:leaderboard:current");
    }

    @Test
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
    void toMemberKey() {
        Long memberId = 1L;

        String memberKey = CrewLeaderboardKeyMapper.toMemberKey(memberId);

        assertThat(memberKey).isEqualTo("member:1");
    }

    @Test
    void parseMemberIdFromKey() {
        String memberKey = "member:1";

        Long memberId = CrewLeaderboardKeyMapper.parseMemberIdFromKey(memberKey);

        assertThat(memberId).isEqualTo(1);
    }
}
