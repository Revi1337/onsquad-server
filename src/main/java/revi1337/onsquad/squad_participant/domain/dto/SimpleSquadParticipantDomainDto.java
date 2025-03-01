package revi1337.onsquad.squad_participant.domain.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import revi1337.onsquad.member.domain.dto.SimpleMemberInfoDomainDto;

public record SimpleSquadParticipantDomainDto(
        Long id,
        LocalDateTime requestAt,
        SimpleMemberInfoDomainDto memberInfo
) {
    @QueryProjection
    public SimpleSquadParticipantDomainDto(Long id, LocalDateTime requestAt, SimpleMemberInfoDomainDto memberInfo) {
        this.id = id;
        this.memberInfo = memberInfo;
        this.requestAt = requestAt;
    }
}
