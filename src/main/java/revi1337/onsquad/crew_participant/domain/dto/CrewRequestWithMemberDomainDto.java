package revi1337.onsquad.crew_participant.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.member.domain.dto.SimpleMemberDomainDto;

public record CrewRequestWithMemberDomainDto(
        SimpleMemberDomainDto memberInfo,
        CrewRequestDomainDto request
) {
    @QueryProjection
    public CrewRequestWithMemberDomainDto(SimpleMemberDomainDto memberInfo, CrewRequestDomainDto request) {
        this.memberInfo = memberInfo;
        this.request = request;
    }
}
