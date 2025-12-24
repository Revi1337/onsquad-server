package revi1337.onsquad.crew_request.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.member.domain.result.SimpleMemberResult;

public record CrewRequestWithMemberResult(
        SimpleMemberResult memberInfo,
        CrewRequestResult request
) {

    @QueryProjection
    public CrewRequestWithMemberResult(SimpleMemberResult memberInfo, CrewRequestResult request) {
        this.memberInfo = memberInfo;
        this.request = request;
    }
}
