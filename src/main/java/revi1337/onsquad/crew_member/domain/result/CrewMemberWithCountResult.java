package revi1337.onsquad.crew_member.domain.result;

import com.querydsl.core.annotations.QueryProjection;

public record CrewMemberWithCountResult(
        long memberCount,
        CrewMemberResult member
) {

    @QueryProjection
    public CrewMemberWithCountResult(long memberCount, CrewMemberResult member) {
        this.memberCount = memberCount;
        this.member = member;
    }
}
