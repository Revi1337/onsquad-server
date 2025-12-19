package revi1337.onsquad.crew_request.domain.result;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;

public record CrewRequestWithMemberResult(
        SimpleMemberDomainDto memberInfo,
        CrewRequestResult request
) {

    @QueryProjection
    public CrewRequestWithMemberResult(SimpleMemberDomainDto memberInfo, CrewRequestResult request) {
        this.memberInfo = memberInfo;
        this.request = request;
    }
}
