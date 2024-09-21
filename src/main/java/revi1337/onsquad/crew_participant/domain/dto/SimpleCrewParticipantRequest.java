package revi1337.onsquad.crew_participant.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;

public record SimpleCrewParticipantRequest(
        SimpleMemberInfoDomainDto memberInfo,
        CrewParticipantDomainDto request
) {
    @QueryProjection
    public SimpleCrewParticipantRequest(SimpleMemberInfoDomainDto memberInfo, CrewParticipantDomainDto request) {
        this.memberInfo = memberInfo;
        this.request = request;
    }
}
