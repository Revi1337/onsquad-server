package revi1337.onsquad.crew_member.application.leaderboard;

import java.util.Collection;
import java.util.List;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.common.constant.Sign;

public abstract class CrewLeaderboardKeyMapper {

    private CrewLeaderboardKeyMapper() {
    }

    private static final String CURRENT_LEADERBOARD_KEY_FORMAT = "crew:%s:leaderboard:current";
    private static final String CURRENT_LEADERBOARD_PATTERN = "crew:*:leaderboard:current";
    private static final String PREVIOUS_LEADERBOARD_KEY_FORMAT = "crew:%s:leaderboard:last-week";
    private static final String PREVIOUS_LEADERBOARD_PATTERN = "crew:*:leaderboard:last-week";
    private static final String MEMBER_KEY_FORMAT = "member:%s";

    public static String toLeaderboardKey(Long crewId) {
        String leaderboardKey = String.format(CURRENT_LEADERBOARD_KEY_FORMAT, crewId);
        return String.format(CacheFormat.SIMPLE, leaderboardKey);
    }

    public static List<String> toLeaderboardKeys(List<Long> crewIds) {
        return crewIds.stream()
                .map(CrewLeaderboardKeyMapper::toLeaderboardKey)
                .toList();
    }

    public static String toPreviousLeaderboardKey(Long crewId) {
        String leaderboardKey = String.format(PREVIOUS_LEADERBOARD_KEY_FORMAT, crewId);
        return String.format(CacheFormat.SIMPLE, leaderboardKey);
    }

    public static List<String> toPreviousLeaderboardKeys(Collection<Long> crewIds) {
        return crewIds.stream()
                .map(CrewLeaderboardKeyMapper::toPreviousLeaderboardKey)
                .toList();
    }

    public static String getLeaderboardPattern() {
        return String.format(CacheFormat.SIMPLE, CURRENT_LEADERBOARD_PATTERN);
    }

    public static String getPreviousLeaderboardPattern() {
        return String.format(CacheFormat.SIMPLE, PREVIOUS_LEADERBOARD_PATTERN);
    }

    public static Long parseCrewIdFromKey(String computedLeaderboardKey) {
        return Long.parseLong(computedLeaderboardKey.split(Sign.COLON)[2]);
    }

    public static String toMemberKey(Long memberId) {
        return String.format(MEMBER_KEY_FORMAT, memberId);
    }

    public static Long parseMemberIdFromKey(String memberKey) {
        return Long.parseLong(memberKey.split(Sign.COLON)[1]);
    }
}
