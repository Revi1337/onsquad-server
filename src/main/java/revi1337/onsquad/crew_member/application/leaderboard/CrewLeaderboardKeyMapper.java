package revi1337.onsquad.crew_member.application.leaderboard;

import java.util.Collection;
import java.util.List;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.common.constant.Sign;

public abstract class CrewLeaderboardKeyMapper {

    private CrewLeaderboardKeyMapper() {
    }

    private static final String LEADERBOARD_KEY_FORMAT = "crew:%s:leaderboard";
    private static final String LEADERBOARD_PATTERN = "crew:*:leaderboard";
    private static final String LEADERBOARD_SNAPSHOT_KEY_FORMAT = "crew:%s:leaderboard:snapshot";
    private static final String LEADERBOARD_SNAPSHOT_PATTERN = "crew:*:leaderboard:snapshot";
    private static final String MEMBER_KEY_FORMAT = "member:%s";
    public static final String LEADERBOARD_SNAPSHOT_POSTFIX = ":snapshot";

    public static String toLeaderboardKey(Long crewId) {
        String leaderboardKey = String.format(LEADERBOARD_KEY_FORMAT, crewId);
        return String.format(CacheFormat.SIMPLE, leaderboardKey);
    }

    public static List<String> toLeaderboardKeys(List<Long> crewIds) {
        return crewIds.stream()
                .map(CrewLeaderboardKeyMapper::toLeaderboardKey)
                .toList();
    }

    public static String toLeaderboardSnapshotKey(Long crewId) {
        String leaderboardKey = String.format(LEADERBOARD_SNAPSHOT_KEY_FORMAT, crewId);
        return String.format(CacheFormat.SIMPLE, leaderboardKey);
    }

    public static List<String> toLeaderboardSnapshotKeys(Collection<Long> crewIds) {
        return crewIds.stream()
                .map(CrewLeaderboardKeyMapper::toLeaderboardSnapshotKey)
                .toList();
    }

    public static String getLeaderboardPattern() {
        return String.format(CacheFormat.SIMPLE, LEADERBOARD_PATTERN);
    }

    public static String getLeaderboardSnapshotPattern() {
        return String.format(CacheFormat.SIMPLE, LEADERBOARD_SNAPSHOT_PATTERN);
    }

    public static Long parseCrewIdFromKey(String computedLeaderboardKey) {
        return Long.parseLong(computedLeaderboardKey.split(Sign.COLON)[2]);
    }

    public static List<Long> parseCrewIdFromKeys(List<String> computedLeaderboardKeys) {
        return computedLeaderboardKeys.stream()
                .map(CrewLeaderboardKeyMapper::parseCrewIdFromKey)
                .toList();
    }

    public static String toMemberKey(Long memberId) {
        return String.format(MEMBER_KEY_FORMAT, memberId);
    }

    public static Long parseMemberIdFromKey(String memberKey) {
        return Long.parseLong(memberKey.split(Sign.COLON)[1]);
    }
}
