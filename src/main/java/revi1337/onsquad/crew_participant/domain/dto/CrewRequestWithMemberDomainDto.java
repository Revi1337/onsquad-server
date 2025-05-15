package revi1337.onsquad.crew_participant.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;

public record CrewRequestWithMemberDomainDto(
        SimpleMemberInfoDomainDto memberInfo,
        CrewRequestDomainDto request
) {
    @QueryProjection
    public CrewRequestWithMemberDomainDto(SimpleMemberInfoDomainDto memberInfo, CrewRequestDomainDto request) {
        this.memberInfo = memberInfo;
        this.request = request;
    }
}
