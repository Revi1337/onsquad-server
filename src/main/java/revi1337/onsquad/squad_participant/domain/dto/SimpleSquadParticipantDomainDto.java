package revi1337.onsquad.squad_participant.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;

public record SimpleSquadParticipantDomainDto(
        SimpleMemberInfoDomainDto memberInfo,
        LocalDateTime requestAt
) {
    @QueryProjection
    public SimpleSquadParticipantDomainDto(SimpleMemberInfoDomainDto memberInfo, LocalDateTime requestAt) {
        this.memberInfo = memberInfo;
        this.requestAt = requestAt;
    }
}
