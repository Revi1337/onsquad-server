package revi1337.onsquad.crew.util;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import lombok.NoArgsConstructor;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.common.constant.Sign;

@NoArgsConstructor(access = PRIVATE)
public final class CrewRankKeyMapper {

    /**
     * @param crewId
     * @return {@code String} onsquad:crew:{crewId}:rank-members:current
     */
    public static String toCrewRankKey(Long crewId) {
        String key = String.join(Sign.COLON, "crew", crewId.toString(), CacheConst.RANK_MEMBERS, "current");
        return String.format(CacheFormat.SIMPLE, key);
    }

    /**
     * @param crewIds
     * @return {@code List<String>} onsquad:crew:{crewId}:rank-members:current
     */
    public static List<String> toCrewRankKeys(List<Long> crewIds) {
        return crewIds.stream()
                .map(CrewRankKeyMapper::toCrewRankKey)
                .toList();
    }

    /**
     * @param crewKey onsquad:crew:{crewId}:rank-members:current
     * @return {@code Long} crewId
     */
    public static Long parseCrewId(String crewKey) {
        return Long.parseLong(crewKey.split(Sign.COLON)[2]);
    }

    /**
     * @return {@code String} onsquad:crew:*:rank-members:current
     */
    public static String getCrewRankPattern() {
        String key = String.join(Sign.COLON, "crew", Sign.ASTERISK, CacheConst.RANK_MEMBERS, "current");
        return String.format(CacheFormat.SIMPLE, key);
    }

    /**
     * @param memberId
     * @return {@code String} member:{memberId}
     */
    public static String toMemberKey(Long memberId) {
        return String.join(Sign.COLON, "member", memberId.toString());
    }

    /**
     * @param memberKey member:{memberId}
     * @return {@code Long} memberId
     */
    public static Long parseMemberId(String memberKey) {
        return Long.parseLong(memberKey.split(Sign.COLON)[1]);
    }
}
