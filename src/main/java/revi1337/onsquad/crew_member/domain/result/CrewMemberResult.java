package revi1337.onsquad.crew_member.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import revi1337.onsquad.member.domain.result.SimpleMemberResult;

public record CrewMemberResult(
        SimpleMemberResult memberInfo,
        LocalDateTime participantAt
) {

    @QueryProjection
    public CrewMemberResult(SimpleMemberResult memberInfo, LocalDateTime participantAt) {
        this.memberInfo = memberInfo;
        this.participantAt = participantAt;
    }
}
