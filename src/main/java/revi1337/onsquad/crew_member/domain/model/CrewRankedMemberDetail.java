package revi1337.onsquad.crew_member.domain.model;

import java.time.LocalDateTime;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;
import revi1337.onsquad.member.domain.entity.Member;

public record CrewRankedMemberDetail(
        Long crewId,
        int rank,
        long score,
        Long memberId,
        String nickname,
        String mbti,
        LocalDateTime lastActivityTime
) {

    public static CrewRankedMemberDetail from(CrewRankedMember crewRankedMember) {
        return new CrewRankedMemberDetail(
                crewRankedMember.getCrewId(),
                crewRankedMember.getRank(),
                crewRankedMember.getScore(),
                crewRankedMember.getMemberId(),
                crewRankedMember.getNickname(),
                crewRankedMember.getMbti(),
                crewRankedMember.getLastActivityTime()
        );
    }

    public static CrewRankedMemberDetail from(Long crewId, Long memberId, int rank, long score, LocalDateTime lastActivityTime) {
        return new CrewRankedMemberDetail(crewId, rank, score, memberId, null, null, lastActivityTime);
    }

    public CrewRankedMember toEntity() {
        return new CrewRankedMember(crewId, rank, score, memberId, nickname, mbti, lastActivityTime);
    }

    public CrewRankedMember toEntityWithMember(Member member) {
        return new CrewRankedMember(
                crewId,
                rank,
                score,
                memberId,
                member.getNickname().getValue(),
                member.getMbti() == null ? "" : member.getMbti().name(),
                lastActivityTime
        );
    }
}
